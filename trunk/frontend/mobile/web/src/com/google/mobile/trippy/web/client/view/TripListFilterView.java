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
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.mobile.trippy.web.client.presenter.TripListFilterPresenter.Display;

/**
 * View for TripListFilterPresenter
 * 
 * 
 */
@SuppressWarnings("deprecation")
public class TripListFilterView extends Composite implements Display {

    
  /** UI Binder. */
  @UiTemplate("TripListFilterView.ui.xml")
  interface Binder extends UiBinder<Widget, TripListFilterView> {
  }

  @UiField Image cancelImg;
  @UiField FocusPanel cancelBar;
  @UiField FlowPanel content;  
  @UiField PopupPanel searchPopUp;
  @UiField TextBox searchBox;
  @UiField Image searchButton;
  
  /** UIBinder instance. */
  private static Binder uiBinder = GWT.create(Binder.class);

  public TripListFilterView() {

    initWidget(uiBinder.createAndBindUi(this));
    searchPopUp.setGlassEnabled(true);
    final KeyboardListener onEnter = new KeyboardListener() {

      @Override
      public void onKeyUp(Widget sender, char keyCode, int modifiers) {
        //No-ops.
      }

      @Override
      public void onKeyPress(Widget sender, char keyCode, int modifiers) {
        if(keyCode == KeyboardListener.KEY_ENTER) {
          NativeEvent evt = Document.get().createClickEvent(1, 0, 0, 0, 0, false,
              false, false, false);
          searchButton.getElement().dispatchEvent(evt);
        }
      }

      @Override
      public void onKeyDown(Widget sender, char keyCode, int modifiers) {
        //No-ops.
      }
    };
    searchBox.addKeyboardListener(onEnter);
    Window.addResizeHandler(new ResizeHandler() {

      @Override
      public void onResize(ResizeEvent event) {
        if (searchPopUp.isShowing()) {
          searchPopUp.center();
        }
      }
    });
  }

  @Override
  public Widget asWidget() {
    return this;
  }

  @Override
  public HasClickHandlers getSearchButton() {
    return searchButton;
  }

  @Override
  public HasClickHandlers getCancelButton() {
    return cancelBar;
  }
  
  @Override
  public String getSearchText() {
    return searchBox.getText();
  }

  @Override
  public void clearSearchBox() {
    searchBox.setText("");
  }

  @Override
  public void setPopupVisible(boolean visible) {
    if (visible) {
      searchPopUp.center();
    } else {
      searchPopUp.hide();
    }
  }
}
