/*
 * Copyright 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.mobile.trippy.web.client.presenter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.mobile.trippy.web.client.base.SingletonComponents;
import com.google.mobile.trippy.web.client.event.ShowTripScheduleEvent;
import com.google.mobile.trippy.web.client.service.RemoteTripServiceAsync;
import com.google.mobile.trippy.web.client.view.View;
import com.google.mobile.trippy.web.client.widget.Toast;
import com.google.mobile.trippy.web.shared.models.Trip;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class is responsible to create a panel for trip share. 
 * 
 *
 */
public class TripSharePresenter implements EventHandlerPresenter<TripSharePresenter.Display> {

  /**
   *  Interface to view the trip list panel.
   */
  public static interface Display extends View {
    HasClickHandlers getSave();
    HasClickHandlers getCancel();
    HasClickHandlers getEmailTxtBox();
    void clear();
    void setEmailTextArea(final String text);
    void setEmailIdErrorMsgLabel(final String errorMsg);
    void addContributorUserId(final String s);
    void addViewerUserId(final String s);
    void showSharedUserIds();
    boolean isChecked();
    String getEmailIds();
  }

  private static final String EMAIL_REGEX = "^[a-zA-Z][\\w\\.-]*[a-zA-Z0-9]@"
      + "[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
  private static final int TOAST_SHOW_TIME_MILLIS = 3000;

  private Trip trip;
  private final Display display;

  private final SingletonComponents singletonComponents;
  private final RemoteTripServiceAsync service;
  private List<HandlerRegistration> handlers;
  private ArrayList<String> emails;

  public TripSharePresenter(final Display display, final SingletonComponents singletonComponents,
      final RemoteTripServiceAsync service) {
    this.display = display;
    this.singletonComponents = singletonComponents;
    this.service = service;
    this.handlers = new ArrayList<HandlerRegistration>();
  }

  /**
   * Bind the presenter and the view.
   * 
   * Listen for user events on display and take action. Also listen for
   * appropriate application events and update display accordingly
   * 
   * Events fired :
   * 1). ShowTripScheduleEvent : Shows the trip schedule page after 
   * successfully sent the invitation or when user pressed the skip button.
   * 
   * Events listened : 
   * None.
   * 
   */
  @Override
  public void bind() {

    handlers.add(display.getCancel().addClickHandler(new ClickHandler() {
      
      @Override
      public void onClick(ClickEvent event) {
        singletonComponents.getEventBus().fireEvent(new ShowTripScheduleEvent(trip));
      }
    }));

    handlers.add(display.getEmailTxtBox().addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        if (display.getEmailIds().equals(
            singletonComponents.getMessage().initialEmailTextAreaContent())) {
          display.setEmailTextArea("");
        }
      }
    }));

    handlers.add(display.getSave().addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        final Toast toast = singletonComponents.getToast();
        if (display.isChecked()) {
          if (validateEmailId(getEmails()) && trip != null) {
            emails = new ArrayList<String>(getEmails());
            service.sendInvite(trip.getKey(), emails, true, new AsyncCallback<String>() {

              @Override
              public void onSuccess(String result) {
                singletonComponents.getToast().showToast(
                    singletonComponents.getMessage().invitationSent(), TOAST_SHOW_TIME_MILLIS);
                singletonComponents.getEventBus().fireEvent(new ShowTripScheduleEvent(trip));
                toast.showToast(singletonComponents.getMessage().invitationSent());
              }

              @Override
              public void onFailure(Throwable caught) {
                singletonComponents.getToast().showToast(caught.getMessage(),
                    TOAST_SHOW_TIME_MILLIS);
              }
            });
          }
        } else {
          if (validateEmailId(getEmails()) && trip != null) {
            emails = new ArrayList<String>(getEmails());
            toast.showLoading(singletonComponents.getMessage().loading());
            service.sendInvite(trip.getKey(), emails, false, new AsyncCallback<String>() {

              @Override
              public void onSuccess(String result) {
                toast.hideToast();
                singletonComponents.getEventBus().fireEvent(new ShowTripScheduleEvent(trip));
                toast.showToast(singletonComponents.getMessage().invitationSent());
              }

              @Override
              public void onFailure(Throwable caught) {
                toast.hideToast();
                toast.showToast(singletonComponents.getMessage().errorInvitation());
              }
            });
          }
        }
      }
    }));
  }

  /**
   * Set trip.
   */
  public void setTrip(final Trip trip) {
    this.trip = trip;
    display.clear();
    display.setEmailTextArea(singletonComponents.getMessage().initialEmailTextAreaContent());
    
    for (final String contributor : trip.getContributorIds()) {
      if (!contributor.equals(singletonComponents.getUtils().getUserEmail())) {
        display.addContributorUserId(contributor);
      }
    }

    for (final String viewer : trip.getViewerIds()) {
      if (!viewer.equals(singletonComponents.getUtils().getUserEmail())
          && !trip.getContributorIds().contains(viewer)) {
        display.addViewerUserId(viewer);
      }
    }
    display.showSharedUserIds();
  }

  @Override
  public HandlerManager getEventBus() {
    return singletonComponents.getEventBus();
  }

  @Override
  public void release() {
    for (HandlerRegistration handler : handlers) {
      if (handler != null) {
        handler.removeHandler();
      }
    }
    handlers.clear();
  }

  @Override
  public Display getDisplay() {
    return display;
  }

  /**
   * Takes the Email ids as user inputs. 
   * 
   */
  private List<String> getEmails() {
    final String emailStr = display.getEmailIds();
    if (emailStr.isEmpty()) {
      return null;
    }
    return Arrays.asList(emailStr.split("[\\s]*,[\\s]*"));
  }

  /**
   * Validate Email Ids for correctness and generate errors if email id is
   * not correct. 
   * 
   */
  private boolean validateEmailId(final List<String> testEmails) {
    if (testEmails == null || testEmails.isEmpty()) {
      display.setEmailIdErrorMsgLabel(singletonComponents.getMessage().errorMsgEmailBoxEmpty());
      return false;
    }

    for (final String email : testEmails) {
      if (!email.matches(EMAIL_REGEX)) {
        display.setEmailIdErrorMsgLabel(singletonComponents
            .getMessage().errorMsgEmailIdNotCorrect());
        return false;
      }
    }
    return true;
  }
}
