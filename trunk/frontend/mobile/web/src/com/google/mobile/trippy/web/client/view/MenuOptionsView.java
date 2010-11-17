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
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.mobile.trippy.web.client.TrippyBundle;
import com.google.mobile.trippy.web.client.presenter.MenuPresenter;
import com.google.mobile.trippy.web.client.presenter.header.MenuOptionsPresenter.Display;

/**
 * View for menu header options.
 * 
 */
public class MenuOptionsView extends Composite implements Display {

  /** UI Binder. */
  @UiTemplate("MenuOptionsView.ui.xml")
  interface Binder extends UiBinder<Widget, MenuOptionsView> {
  }
  
  @UiField Image leftItemIcon;
  @UiField FlowPanel menuPanel;
  
  private static Binder uiBinder = GWT.create(Binder.class);

  public MenuOptionsView() {
    initWidget(uiBinder.createAndBindUi(this));
    leftItemIcon.setResource(TrippyBundle.INSTANCE.iconSearch());
  }

  @Override
  public Widget asWidget() {
    return this;
  }

  @Override
  public void setMenu(MenuPresenter.Display menuDisplay) {
    menuPanel.clear();
    menuPanel.add(menuDisplay.asWidget());
  }
  
  @Override
  public HasClickHandlers getSearchOption() {
    return leftItemIcon;
  }
}
