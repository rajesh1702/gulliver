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
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.mobile.trippy.web.client.presenter.TripListItemPresenter;
import com.google.mobile.trippy.web.client.presenter.TripListPresenter;

/**
 * List item on trip list
 * 
 */
public class TripListView extends Composite implements TripListPresenter.Display{

  /**
   * Trip List Css style.
   */
  public static interface TripListViewCss extends CssResource {
    String activeList();

    String pastList();
  }
  
  /**
   * UI Binder to bind java and xml codes
   */
  @UiTemplate("TripListView.ui.xml")
  interface Binder extends UiBinder<Widget, TripListView> {
  }

  @UiField
  Label noTrips;
  @UiField
  Label btnCreateTrip;
  @UiField
  Label btnRefresh;
  @UiField
  FlowPanel activeTripsList;
  @UiField
  FlowPanel pastTripsList;
  @UiField
  TripListViewCss style;

  private static Binder uiBinder = GWT.create(Binder.class);
  
  public TripListView() {
    super();
    initWidget(uiBinder.createAndBindUi(this));
  }

  @Override
  public void addActiveTrip(final TripListItemPresenter.Display activeTrip) {
    activeTripsList.add(activeTrip.asWidget());
  }

  @Override
  public void addPastTrip(final TripListItemPresenter.Display pastTrip) {
    pastTripsList.add(pastTrip.asWidget());
  }

  @Override
  public void clear() {
    activeTripsList.clear();
    pastTripsList.clear();
  }

  @Override
  public Widget asWidget() {
    return this;
  }

  @Override
  public void setNoTripsMsgVisible(boolean visible) {
    noTrips.setVisible(visible);
    btnCreateTrip.setVisible(visible);
    btnRefresh.setVisible(visible);
  }
  
  @Override
  public HasClickHandlers getCreateTripButton() {
    return btnCreateTrip;
  }

  @Override
  public HasClickHandlers getRefreshButton() {
    return btnRefresh;
  }
}
