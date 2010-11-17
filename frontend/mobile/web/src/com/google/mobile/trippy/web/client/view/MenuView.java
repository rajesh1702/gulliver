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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.mobile.trippy.web.client.presenter.MenuItemPresenter;
import com.google.mobile.trippy.web.client.presenter.MenuPresenter;

/**
 * View for menu.
 * 
 * 
 */
public class MenuView extends Composite implements MenuPresenter.Display {

  /** UI Binder. */
  @UiTemplate("MenuView.ui.xml")
  interface Binder extends UiBinder<Widget, MenuView> {
  }

  /**
   * Style for PopUp.
   * 
   * 
   */
  interface MenuCss extends CssResource {
    String tableTd();
  }

  @UiField
  Label label;
  @UiField
  Label menuPopUpTitle;
  @UiField
  PopupPanel menuPopUp;
  @UiField
  FlexTable flexTable;
  @UiField
  FocusPanel imgClose;
  @UiField
  MenuCss style;

  private int row = 0;
  private int col = 0;
  private static final int MAX_MENU_COL = 2;

  private static Binder uiBinder = GWT.create(Binder.class);

  public MenuView() {
    initWidget(uiBinder.createAndBindUi(this));
    menuPopUp.setModal(true);
    menuPopUp.setGlassEnabled(true);
    menuPopUp.removeFromParent();
    Window.addResizeHandler(new ResizeHandler() {

      @Override
      public void onResize(ResizeEvent event) {
        if (menuPopUp.isShowing()) {
          menuPopUp.center();
        }
      }
    });
  }

  @Override
  public void add(MenuItemPresenter.Display menuItem) {
    if (col >= MAX_MENU_COL) {
      row++;
      col = 0;
    }
    flexTable.setWidget(row, col, menuItem.asWidget());
    // flexTable.getFlexCellFormatter().setStyleName(row, col, style.tableTd());
    ++col;
  }

  @Override
  public void clearMenuItems() {
    row = 0;
    col = 0;
    flexTable.clear();
  }

  @Override
  public HasClickHandlers getLabel() {
    return label;
  }

  @Override
  public void insert(MenuItemPresenter.Display menuItem, int index) {
    // No-ops.
  }

  @Override
  public boolean isPopupVisible() {
    return menuPopUp.isShowing();
  }

  @Override
  public void remove(MenuItemPresenter.Display menuItem) {
    // No-ops.
  }

  @Override
  public void setPopupVisible(boolean visible) {
    if (visible) {
      menuPopUp.center();
    } else {
      menuPopUp.hide();
    }
  }

  @Override
  public Widget asWidget() {
    return this;
  }

  @UiHandler("imgClose")
  public void onClickClose(ClickEvent event) {
    menuPopUp.removeFromParent();
  }

  @Override
  public void setMenuPopupTitle(String popupTitle) {
    menuPopUpTitle.setText(popupTitle);
  }
}
