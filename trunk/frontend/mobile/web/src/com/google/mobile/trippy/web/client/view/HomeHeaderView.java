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
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.mobile.trippy.web.client.TrippyBundle;
import com.google.mobile.trippy.web.client.base.Constants;
import com.google.mobile.trippy.web.client.presenter.HomeHeaderPresenter.Display;
import com.google.mobile.trippy.web.client.presenter.header.LogOutOptionsPresenter;

/**
 * Header Panel View.
 * 
 *
 */
@SuppressWarnings("deprecation")
public class HomeHeaderView extends Composite implements Display {

  static {
    // Create an inject decorator-specific resources.
    TrippyBundle resources = GWT.create(TrippyBundle.class);
    StyleInjector.inject(resources.commonStyle().getText());
  }

  /** UI Binder. */
  @UiTemplate("HomeHeaderView.ui.xml")
  interface Binder extends UiBinder<Widget, HomeHeaderView> {}
  
  @UiField Label txtTitle;
  @UiField Image iconLonelyPlanet;
  @UiField FlowPanel optionsPanel; 
 
  /** UIBinder instance. */
  private static Binder uiBinder = GWT.create(Binder.class);

  public HomeHeaderView() {
    initWidget(uiBinder.createAndBindUi(this));
    txtTitle.setText(Constants.HOME_STR);
  }

  @Override
  public Widget asWidget() {
    return this;
  }

  @Override
  public void setOptions(final LogOutOptionsPresenter options, final boolean isVisible) {
    optionsPanel.setVisible(isVisible);
    optionsPanel.add(options.getDisplay().asWidget());
  }
}
