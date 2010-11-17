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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.mobile.trippy.web.client.presenter.DayListItemPresenter;

/**
 * View for Day list item presenter
 * 
 */
public class DayListItemView extends Composite implements DayListItemPresenter.Display {

  /**
   * UI Binder.
   */
  @UiTemplate("DayListItemView.ui.xml")
  interface Binder extends UiBinder<Widget, DayListItemView> {
  }

  @UiField
  FlowPanel dayFocus;
  @UiField
  Label lblDay;
  @UiField
  Label lblItemCount;
  @UiField
  FocusPanel htmlPanel;
  @UiField 
  Image imgSearchItem;

  private static Binder uiBinder = GWT.create(Binder.class);

  /**
   * Creates Trip Panel.
   */
  public DayListItemView() {
    super();
    initWidget(uiBinder.createAndBindUi(this));
    //TODO :Its just remove the tabindex from DOM. But need to find
    //the alternative approach.
    htmlPanel.getElement().getChild(htmlPanel.getTabIndex()).removeFromParent();
    //TODO : Remove search functionality from day if not required. 
    imgSearchItem.setVisible(false);
  }

  @Override
  public HasClickHandlers getDayFocus() {
    return htmlPanel;
  }
  
  @Override
  public void setDay(final String day) {
    lblDay.setText(day);
  }

  @Override
  public void setItemCount(final String count) {
    lblItemCount.setText(count);
  }

  @Override
  public Widget asWidget() {
    return this;
  }
  
  @Override
  public HasClickHandlers getSearch() {
    return imgSearchItem;
  }
}
