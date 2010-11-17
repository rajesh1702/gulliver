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
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.mobile.trippy.web.client.TrippyBundle;
import com.google.mobile.trippy.web.client.presenter.HomePanelPresenter.Display;
import com.google.mobile.trippy.web.client.presenter.UpcomingTripPresenter;

/**
 * This class is responsible to show the view for home panel with up coming 
 * trip list.
 * 
 */
public class HomePanelView extends Composite implements Display {

  /** Ui Binder. */
  @UiTemplate("HomePanelView.ui.xml")
  interface Binder extends UiBinder<Widget, HomePanelView> {
    
  }

  static {
    // Create an inject decorator-specific resources.
    TrippyBundle resources = GWT.create(TrippyBundle.class);
    StyleInjector.inject(resources.commonStyle().getText());
  }

  @UiField Label btnCreateTrip;
  @UiField Label btnViewTrips;
  @UiField Label upcomingTripTitle;
  @UiField Label unscheduleTripTitle;
  @UiField FlowPanel flwTripList;
  @UiField FlowPanel flwUnscheduleTripList;

  private static Binder uiBinder = GWT.create(Binder.class);

  public HomePanelView() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  @Override
  public Widget asWidget() {
    return this;
  }

  @Override
  public HasClickHandlers getCreateNewTrip() {
    return btnCreateTrip;
  }

  @Override
  public HasClickHandlers getViewMyTrips() {
    return btnViewTrips;
  }

  @Override
  public void clearTripList() {
    flwTripList.clear();
    flwUnscheduleTripList.clear();
  }

  @Override
  public void setUpcomingTripsTitle(final boolean visible) {
    upcomingTripTitle.setVisible(visible);
  }

  @Override
  public void addUpcomingTrip(final UpcomingTripPresenter.Display display) {
    flwTripList.add(display.asWidget());
  }

  @Override
  public void setUnscheduledTripsTitle(final boolean visible) {
    unscheduleTripTitle.setVisible(visible);
  }

  @Override
  public void addUnscheduledTrip(final UpcomingTripPresenter.Display display) {
    flwUnscheduleTripList.add(display.asWidget());
  }
}
