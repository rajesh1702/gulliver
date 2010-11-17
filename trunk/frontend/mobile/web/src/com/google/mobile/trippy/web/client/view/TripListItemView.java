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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowResizeListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.mobile.trippy.web.client.presenter.TripListItemPresenter;
/**
 * View for trip list item presenter
 * 
 */
public class TripListItemView extends Composite implements TripListItemPresenter.Display{

  /**
   * UI Binder.
   */
  @UiTemplate("TripListItemView.ui.xml")
  interface Binder extends UiBinder<Widget, TripListItemView> {
  }

  @UiField
  FocusPanel container;
  @UiField
  Label lblName;
  @UiField
  Label lblAddress;
  @UiField
  Label lblDuration;
  @UiField
  Label lblCommentCount;
  @UiField
  HTML htmUpdated;
  @UiField
  TripListItemCss style;
  @UiField
  Image imgOptions;

  private static Binder uiBinder = GWT.create(Binder.class);
  /**
   * Trip Panel Css style
   */
  public static interface TripListItemCss extends CssResource {
    String updateTripComment();
  }

  /**
   * Creates Trip Panel.
   */
  @SuppressWarnings("deprecation")
  public TripListItemView() {
    super();
    initWidget(uiBinder.createAndBindUi(this));
    //TODO :Its just remove the tabindex from DOM. But need to find
    //the alternative approach.
    container.getElement().getChild(container.getTabIndex()).removeFromParent();
    Window.addWindowResizeListener(new WindowResizeListener() {
      @Override
      public void onWindowResized(int width, int height) {
        int txtTitleWidth = width - 40;
        lblName.setWidth(txtTitleWidth + "px");
      }
    });
  }

  @Override
  public HasClickHandlers getName() {
    return container;
  }
  
  @Override
  public HasClickHandlers getOptions() {
    return imgOptions;
  }

  @Override
  public void setAddress(final String address) {
    lblAddress.setText(address);
  }

  @Override
  public void setCommentCount(final String count) {
    lblCommentCount.setVisible(true);
    lblCommentCount.setText(count);
  }

  @Override
  public void setDuration(final String duration) {
    lblDuration.setText(duration);
  }


  @Override
  public void setName(final String name) {
    lblName.setText(name);
  }


  @Override
  public void setUpdate(final boolean isUpdated, final boolean commentsUpdated) {
    htmUpdated.setHTML("&nbsp;");
    if (isUpdated) {
      htmUpdated.removeStyleName(style.updateTripComment());
      htmUpdated.setVisible(true);
    } else if (commentsUpdated) {
      htmUpdated.addStyleName(style.updateTripComment());
      htmUpdated.setVisible(true);
    } else {
      htmUpdated.setVisible(false);
    }
  }


  @Override
  public Widget asWidget() {
    return this;
  }
  
  @Override
  protected void onAttach() {
    super.onAttach();
    int headerWidth = this.getOffsetWidth();
    int txtTitleWidth = headerWidth - 40;
    lblName.setWidth(txtTitleWidth + "px");
  }
}
