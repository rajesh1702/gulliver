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
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.mobile.trippy.web.client.presenter.MenuItemPresenter;

/**
 * View for menu item.
 * 
 * 
 */
public class MenuItemView extends Composite implements MenuItemPresenter.Display {

  /** UI Binder. */
  @UiTemplate("MenuItemView.ui.xml")
  interface Binder extends UiBinder<Widget, MenuItemView> {
  }

  @UiField
  FocusPanel clickPanel;
  @UiField
  Image icon;
  @UiField
  Label label;
  @UiField
  Label greyedlabel;
  
  private static Binder uiBinder = GWT.create(Binder.class);

  public MenuItemView() {
    initWidget(uiBinder.createAndBindUi(this));
    //TODO :Its just remove the tabindex from DOM. But need to find
    //the alternative approach.
    clickPanel.getElement().getChild(clickPanel.getTabIndex()).removeFromParent();
  }

  @Override
  public HasClickHandlers getMenuItem() {
    return clickPanel;
  }

  @Override
  public void setIcon(ImageResource resource) {
    icon.setResource(resource);
  }

  @Override
  public void setLabel(String labelStr) {
    label.setText(labelStr);
  }

  @Override
  public Widget asWidget() {
    return this;
  }

  @Override
  public void setIconVisible(boolean visible) {
    icon.setVisible(visible);
  }

  @Override
  public void setLabelVisible(boolean visible) {
    label.setVisible(visible);
  }

  @Override
  public void setDisabled(boolean isDisabled) {
    if (isDisabled) {
      label.setStyleName(greyedlabel.getStyleName());
    }    
  }
}
