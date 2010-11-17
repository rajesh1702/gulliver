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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowResizeListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.mobile.trippy.web.client.TrippyBundle;
import com.google.mobile.trippy.web.client.base.Constants;
import com.google.mobile.trippy.web.client.presenter.BaseHeaderPresenter.Display;
import com.google.mobile.trippy.web.client.presenter.header.BaseHeaderOptionsPresenter;

/**
 * Header Panel View.
 * 
 */
@SuppressWarnings("deprecation")
public class BaseHeaderView extends Composite implements Display {

  static {
    // Create an inject decorator-specific resources.
    TrippyBundle resources = GWT.create(TrippyBundle.class);
    StyleInjector.inject(resources.commonStyle().getText());
  }

  /** UI Binder. */
  @UiTemplate("BaseHeaderView.ui.xml")
  interface Binder extends UiBinder<Widget, BaseHeaderView> {}
  
  @UiField Label btnNavigate;
  @UiField Label btnSubNavigate;
  @UiField Label btnTitle;
  @UiField FlowPanel optionsPanel; 
 
  /** UIBinder instance. */
  private static Binder uiBinder = GWT.create(Binder.class);

  public BaseHeaderView() {
    initWidget(uiBinder.createAndBindUi(this));
    btnNavigate.setText(Constants.ITINERARY_STR);
    Window.addWindowResizeListener(new WindowResizeListener() {
      @Override
      public void onWindowResized(int width, int height) {
        final int txtTitleWidth = width - Constants.NAVIGATION_WIDTH - 
            Constants.OPTIONS_PANEL_WIDTH;
        btnTitle.setWidth(Math.max(txtTitleWidth, 5) + "px");
      }
    });
  }

  @Override
  public Widget asWidget() {
    return this;
  }
  
  @Override
  public HasClickHandlers getTitleButton() {
    return btnTitle;
  }

  @Override
  public HasClickHandlers getNavigationButton() {
    return btnNavigate;
  }
  
  @Override
  public void setScreenTitleButtonTitle(final String title) {
    btnTitle.setText(title);
  }

  @Override
  public void setNavigationButtonTitle(String title) {
    btnNavigate.setText(title);
  }

  @Override
  public void setOptions(BaseHeaderOptionsPresenter options, boolean isVisible) {
    optionsPanel.setVisible(isVisible);
    optionsPanel.add(options.getDisplay().asWidget());
  }
  
  @Override
  protected void onAttach() {
    super.onAttach();
    final int headerWidth = this.getOffsetWidth();
    final int txtTitleWidth = headerWidth - Constants.NAVIGATION_WIDTH - 
        Constants.OPTIONS_PANEL_WIDTH;
    btnTitle.setWidth(Math.max(txtTitleWidth, 5) + "px");
  }

  @Override
  public void setSubNavigateButtonTitle(final String title) {
    btnSubNavigate.setText(title);
  }

  @Override
  public void setSubNavigateVisible(final boolean visible) {
    btnSubNavigate.setVisible(visible);
  }

  @Override
  public HasClickHandlers getSubNavigationButton() {
    return btnSubNavigate;
  }
}
