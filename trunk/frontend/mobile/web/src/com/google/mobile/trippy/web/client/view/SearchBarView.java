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
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.mobile.trippy.web.client.base.Constants;
import com.google.mobile.trippy.web.client.presenter.SearchBarPresenter;

/**
 * View for SearchPanelPresenter.
 * 
 * 
 */
@SuppressWarnings("deprecation")
public class SearchBarView extends Composite implements SearchBarPresenter.Display {

  /**
   * Interface for css style.
   * 
   *
   */
  public static interface Style extends CssResource {
    String basePopup();
  }
  
  /** UI Binder. */
  @UiTemplate("SearchBarView.ui.xml")
  interface Binder extends UiBinder<Widget, SearchBarView> {
  }

  @UiField Style style;
  @UiField(provided = true)
  SuggestBox searchBox;
  @UiField Image searchButton;
  @UiField Label lpSight;
  @UiField Label lpShopping;
  @UiField Label lpHotel;
  @UiField Label lpEntertainment;
  @UiField Label lpRestaurant;
  @UiField Label lpActivities;
  @UiField Label lpGeneral;
  @UiField Image cancelImg;
  @UiField FocusPanel cancelBar;
  private final DecoratedPopupPanel searchPopUp = new DecoratedPopupPanel();
  
  /** UIBinder instance. */
  private static Binder uiBinder = GWT.create(Binder.class);
  private final MultiWordSuggestOracle mySuggestions = new MultiWordSuggestOracle();

  public SearchBarView() {
    searchBox = new SuggestBox(mySuggestions);
    initWidget(uiBinder.createAndBindUi(this));
    searchBox.addSelectionHandler(new SelectionHandler<Suggestion>() {

      @Override
      public void onSelection(SelectionEvent<Suggestion> event) {
        NativeEvent evt = Document.get().createClickEvent(1, 0, 0, 0, 0, false,
                  false, false, false);
              searchButton.getElement().dispatchEvent(evt);        
      }
    });

    searchPopUp.setWidget(this);
    searchPopUp.setGlassEnabled(true);
    searchPopUp.setStyleName(style.basePopup());
    final KeyboardListener onEnter = new KeyboardListener() {

      @Override
      public void onKeyUp(Widget sender, char keyCode, int modifiers) {
        //No-ops.
      }

      @Override
      public void onKeyPress(Widget sender, char keyCode, int modifiers) {
        if (keyCode == KeyboardListener.KEY_ENTER) {
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
    defaultSearchText();
    loadSearchCategory();
  }

  @Override
  public Widget asWidget() {
    return this;
  }
  
  @Override
  public HasClickHandlers[] getLPLinks() {
    HasClickHandlers[] lpLinks = new
        HasClickHandlers[SearchBarPresenter.LP_POI_TYPES.length];
    lpLinks[0] = lpSight;
    lpLinks[1] = lpShopping;
    lpLinks[2] = lpHotel;
    lpLinks[3] = lpEntertainment;
    lpLinks[4] = lpRestaurant;
    lpLinks[5] = lpActivities;
    lpLinks[6] = lpGeneral;
    return lpLinks;
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
  public void defaultSearchText() {
    searchBox.setText("Search Google Maps");
  }
  
  @Override
  public void setPopupVisible(boolean visible) {
    if (visible) {
      searchPopUp.center();
    } else {
      searchPopUp.hide();
    }
  }

  @Override
  public HasClickHandlers getTextArea() {
    return searchBox.getTextBox();
  }

  @Override
  public void clearSearchText() {
    searchBox.setText("");
  }

  private void loadSearchCategory() {
    for (String option: Constants.SEARCH_CATEGORY) {
      mySuggestions.add(option);
    }
  }
}
