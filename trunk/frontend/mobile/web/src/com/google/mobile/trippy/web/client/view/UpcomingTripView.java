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
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowResizeListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import com.google.mobile.trippy.web.client.presenter.UpcomingTripPresenter.Display;

/**
 * This class is responsible to create view for up coming trip panel.
 * 
 *
 */
@SuppressWarnings("deprecation")
public class UpcomingTripView extends Composite implements Display {

  /** Ui Binder. */
  @UiTemplate("UpcomingTripView.ui.xml")
  interface Binder extends UiBinder<Widget, UpcomingTripView> {
  }

  @UiField Label lblName;
  @UiField Label lblDuration;
  @UiField FocusPanel container;

  private static Binder uiBinder = GWT.create(Binder.class);
  private static final int TRIP_NAME_PADDING = 200;

  public UpcomingTripView() {
    initWidget(uiBinder.createAndBindUi(this));
    //TODO: Use good approach if available.
    Window.addWindowResizeListener(new WindowResizeListener() {
      @Override
      public void onWindowResized(int width, int height) {
        int txtTitleWidth = width - TRIP_NAME_PADDING;
        lblName.setWidth(txtTitleWidth + "px");
      }
    });
    //TODO :Its just remove the tabindex from DOM. But need to find
    //the alternative approach.
    container.getElement().getChild(container.getTabIndex()).removeFromParent();
  }

  @Override
  protected void onAttach() {
    super.onAttach();
    int headerWidth = container.getOffsetWidth();
    int txtTitleWidth = headerWidth - TRIP_NAME_PADDING;
    lblName.setWidth(txtTitleWidth + "px");
  }

  @Override
  public void setDuration(final String tripDuration) {
    lblDuration.setText(tripDuration);
  }

  @Override
  public void setName(final String tripName) {
    lblName.setText(tripName);
  }

  @Override
  public Widget asWidget() {
    return this;
  }

  @Override
  public HasClickHandlers getTripHandler() {
    return container;
  }
}