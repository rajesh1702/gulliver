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

import com.google.common.annotations.VisibleForTesting;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.mobile.trippy.web.client.base.Constants;
import com.google.mobile.trippy.web.client.base.SingletonComponents;
import com.google.mobile.trippy.web.client.base.Utils;
import com.google.mobile.trippy.web.client.view.View;
import com.google.mobile.trippy.web.client.widget.Toast;
import com.google.mobile.trippy.web.shared.exception.AuthorizationException;
import com.google.mobile.trippy.web.shared.models.Comment;
import com.google.mobile.trippy.web.shared.models.Trip;
import com.google.mobile.trippy.web.shared.models.TripItem;

import java.util.ArrayList;
import java.util.List;

/**
 * This presenter is responsible for creating a comment. It will send add
 * comment request to Comment Service for adding a new comment..
 * 
 * 
 */
public class CommentAddPresenter implements EventHandlerPresenter<CommentAddPresenter.Display> {

  /**
   * View interface for the comment screen.
   */
  public interface Display extends View {
    HasClickHandlers getAddButton();
    void setTextAreaVisible(final boolean visible);
    void setAddButtonVisible(final boolean visible);
    void resetCommentText();
    String getCommentText();
  }

  /** Object to contain the reference of associated view. */
  private final Display display;

  /** Common container for all the singleton components of this application. */
  private final SingletonComponents singletonComponents;

  private final List<HandlerRegistration> handlers;

  /** Boolean to check whether current viewer is contributor or not. */
  private boolean isViewer;

  /** Boolean to check whether current viewer is online or not. */
  private boolean isOnline;

  private TripItem tripItem;

  public CommentAddPresenter(final Display display, final SingletonComponents provider) {
    this.display = display;
    this.singletonComponents = provider;
    this.handlers = new ArrayList<HandlerRegistration>();
  }

  /**
   * Method will extract viewer info from trip item and will call populateView()
   * with this info.
   */
  public void setTripItem(final TripItem item) {
    this.tripItem = item;
    final Trip trip = singletonComponents.getTripService().getTrip(tripItem.getTripId());
    isViewer = singletonComponents.getUserUtils().isViewer(
        trip, singletonComponents.getUtils().getUserEmail());
    populateView();
  }

  /**
   * Method takes the responsibility for handling all the events and firing the
   * events.
   * 
   * It also checks for authorization i.e. whether current user is viewer or not
   * while adding click handlers.
   * 
   * Click Handlers: 1) It will have a click handler on Click event of Add
   * button, which will call a method addComment(String commentText).
   */
  @Override
  public void bind() {
    if (isViewer) { // authorization check
      handlers.add(display.getAddButton().addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          if (isOnline) {
            String commentText = display.getCommentText();
            addComment(commentText);
          }
        }
      }));
    }
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
   * Method is used to populate the Comment Screen View.
   * 
   * It also checks whether current user is viewer or not. if current user is
   * viewer then it enable/disable "Add" button as per online state, else if
   * current user is not viewer then hide the text area and add button.
   */
  @VisibleForTesting
  void populateView() {
    display.setTextAreaVisible(isViewer);
    display.setAddButtonVisible(isViewer);
    display.resetCommentText();
    final Utils utils = singletonComponents.getUtils();
    setOnline(utils.isOnline());
    utils.addOnlineHandler(new ValueChangeHandler<Boolean>() {
      @Override
      public void onValueChange(ValueChangeEvent<Boolean> event) {
        setOnline(utils.isOnline());
      }
    });
  }

  /**
   * Method will set online status for the user.
   */
  @VisibleForTesting
  void setOnline(boolean online) {
    isOnline = online;
  }

  /**
   * Method will set the boolean isViewer.
   * 
   * This method is used only for Test Case Scenarios.
   */
  @VisibleForTesting
  void setViewer(boolean viewer) {
    this.isViewer = viewer;
  }

  /**
   * Method will send add request for the comment to comment service.
   * 
   * It will be called on Click Event of Add Button. This method will create a
   * {@link Comment} object and send it to the comment Service for addition.
   */
  @VisibleForTesting
  void addComment(final String commentText) {
    if (commentText.trim().length() != 0) {
      final Comment comment = new Comment();
      comment.setTripId(tripItem.getTripId());
      comment.setTripItemId(tripItem.getKey());
      comment.setComment(commentText);
      comment.setUserId(singletonComponents.getUtils().getUserEmail());
      final Toast toast = singletonComponents.getToast();
      try {
        toast.showLoading(singletonComponents.getMessage().adding(Constants.COMMENT_LOWER_CASE_STR));
        singletonComponents.getCommentService().addComment(comment, new AsyncCallback<Comment>() {
          @Override
          public void onSuccess(final Comment result) {
            toast.hideLoading();
            singletonComponents.getToast().showToast(
                singletonComponents.getMessage().commentAdded());
            display.resetCommentText();
          }

          @Override
          public void onFailure(Throwable caught) {
            toast.hideLoading();
            if (caught instanceof AuthorizationException) {
              toast.showToast(singletonComponents.getMessage().unauthorizedAdd(
                  Constants.COMMENT_LOWER_CASE_STR));
            } else {
              toast.showToast(singletonComponents.getMessage().commentAddFailed());
            }
          }
        });
      } catch (AuthorizationException e) {
        toast.hideLoading();
        singletonComponents.getToast().showToast(e.getMessage());
      }
    }
  }
}
