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

package com.google.mobile.trippy.web.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.mobile.trippy.web.client.presenter.TripItemPresenter.Display;

/**
 * To show trip item detail on a map screen
 * 
 */
public class TripItemMapView extends Composite implements Display {

  /** UI Binder. */
  @UiTemplate("TripItemMapView.ui.xml")
  interface Binder extends UiBinder<Widget, TripItemMapView> {
  }
  
  // UiFields
  @UiField FocusPanel clickPanel;
  @UiField Label name;
  @UiField Label address;

  private static Binder uiBinder = GWT.create(Binder.class);
  private final HasClickHandlers dummyHandler = new HasClickHandlers() {
    @Override
    public void fireEvent(GwtEvent<?> event) {
    }
    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
      return null;
    }
  };
  
  public TripItemMapView() {
    initWidget(uiBinder.createAndBindUi(this));
    //TODO :Its just remove the tabindex from DOM. But need to find
    //the alternative approach.
    clickPanel.getElement().getChild(clickPanel.getTabIndex()).removeFromParent();
  }

  @Override
  public HasClickHandlers getShowDetails() {
    return clickPanel;
  }
  
  @Override
  public HasClickHandlers getCommentButton() {
    return dummyHandler;
  }

  @Override
  public HasClickHandlers getDislike() {
    return dummyHandler;
  }

  @Override
  public HasClickHandlers getLike() {
    return dummyHandler;
  }

  @Override
  public void setCommentCount(final int count) {
    //No-ops.
  }

  @Override
  public void setLocation(final String location, final double lat, final double lng) {
    address.setText("");
  }

  @Override
  public void showOnGoogleMapUrl(final String url) {
    //No-ops.
  }
  
  @Override
  public void setMoreInfoLink(final String href) {
    //No-ops.
  }

  @Override
  public void setName(final String title) {
    name.setText(title);
  }

  @Override
  public void setThumbDownCount(final int count) {
    //No-ops.
  }

  @Override
  public void setThumbUpCount(final int count) {
    //No-ops.
  }

  @Override
  public Widget asWidget() {
    return this;
  }

  @Override
  public void addPhoneNumber(final String phoneNumbers) {
    //No-ops.
  }
  
  @Override
  public void clear() {
    //No-ops.    
  }

  @Override
  public void setDay(final String dateStr) {
    //No-ops.    
  }

  @Override
  public void setSchedule(final String scheduleStr) {
    //No-ops.
  }
  
  @Override
  public void setReview(final String html) {
    //No-ops.
  }

  @Override
  public HasClickHandlers getRescheduleButton() {
    return dummyHandler;
  }

  @Override
  public HasClickHandlers getShowOnMapButton() {
    return dummyHandler;
  }

  @Override
  public HasClickHandlers getCommentIcon() {
    return dummyHandler;
  }

  @Override
  public HasClickHandlers getRescheduleIcon() {
    return dummyHandler;
  }

  @Override
  public HasClickHandlers getShowOnMapIcon() {
    return dummyHandler;
  }

  @Override
  public HasClickHandlers getLikeIcon() {
    return dummyHandler;
  }

  @Override
  public HasClickHandlers getOptions() {
    return dummyHandler;
  }
}
