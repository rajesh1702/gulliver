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

import com.google.common.base.Preconditions;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.mobile.trippy.web.client.i18n.Message;
import com.google.mobile.trippy.web.client.presenter.BaseHeaderPresenter;
import com.google.mobile.trippy.web.client.presenter.MapPresenter;
import com.google.mobile.trippy.web.client.presenter.SearchResultItemPresenter;
import com.google.mobile.trippy.web.client.presenter.SearchResultsMapPresenter;

/**
 * View implementation for search results map view
 * 
 */
public class SearchResultsMapView extends Composite implements SearchResultsMapPresenter.Display {

  /** UI Binder. */
  @UiTemplate("SearchResultsMapView.ui.xml")
  interface Binder extends UiBinder<Widget, SearchResultsMapView> {
  }

  @UiField
  FocusPanel prevItem;
  @UiField
  FocusPanel nextItem;
  @UiField
  BaseHeaderPresenter.Display headerDisplay;
  @UiField
  HTMLPanel panelNoItems;
  @UiField
  SearchResultItemPresenter.Display itemInfoWindow;
  @UiField
  ListBox pageDropDown;
  @UiField
  MapView mapView;
  MenuOptionsView headerOptionsView;
  @UiField
  Label textNoItems;
  @UiField
  Anchor showGoogleSearch;

  /** UIBinder instance. */
  private static Binder uiBinder = GWT.create(Binder.class);

  public SearchResultsMapView() {
    super();
    initWidget(uiBinder.createAndBindUi(this));
    //TODO :Its just remove the tabindex from DOM. But need to find
    //the alternative approach.
    nextItem.getElement().getChild(nextItem.getTabIndex()).removeFromParent();
    prevItem.getElement().getChild(prevItem.getTabIndex()).removeFromParent();
    Window.addResizeHandler(new ResizeHandler() {

      @Override
      public void onResize(ResizeEvent event) {
        mapView.setHeight((Window.getClientHeight() - 74) + "px");
      }
    });
    mapView.setHeight((Window.getClientHeight() - 74) + "px");
  }

  @Override
  public Widget asWidget() {
    return this;
  }

  @Override
  public BaseHeaderPresenter.Display getHeaderDisplay() {
    return headerDisplay;
  }

  @Override
  public SearchResultItemPresenter.Display getInfoDisplay() {
    return itemInfoWindow;
  }

  @Override
  public MapPresenter.Display getMapDisplay() {
    return mapView;
  }

  @Override
  public HasClickHandlers getNextButton() {
    return nextItem;
  }

  @Override
  public HasClickHandlers getPrevButton() {
    return prevItem;
  }

  @Override
  public MenuOptionsView getHeaderOptionsDisplay() {
    return headerOptionsView;
  }

  @Override
  public void setNextButtonEnabled(final boolean enabled) {
    nextItem.setVisible(enabled);
  }

  @Override
  public void setPrevButtonEnabled(final boolean enabled) {
    prevItem.setVisible(enabled);
  }

  @Override
  public void populatePageList(int totalPages) {
    Preconditions.checkArgument(totalPages >= 0);
    pageDropDown.clear();
    pageDropDown.setVisible(!(totalPages == 0));
    final Message messages = GWT.create(Message.class);
    for (int i = 1; i <= totalPages; i++) {
      pageDropDown.addItem("Page " + i);
    }
  }

  @Override
  public HasChangeHandlers getPageList() {
    return pageDropDown;
  }

  @Override
  public int getSelectedPage() {
    return pageDropDown.getSelectedIndex();
  }

  @Override
  public void setItemVisible(final boolean visible) {
    itemInfoWindow.asWidget().setVisible(visible);
    panelNoItems.setVisible(!visible);
  }

  @Override
  public void setSelectedPage(int index) {
    pageDropDown.setSelectedIndex(index);
  }

  @Override
  public void setNoItemText(boolean isLp) {
    //TODO: move constant strings to constant file.
    if (isLp) {
      textNoItems.setText("No Lonely Planet results found.");
    } else {
      textNoItems.setText("No Google Results");
    }
    showGoogleSearch.setVisible(isLp);
  }
  
  @Override
  public HasClickHandlers getGoogleSearch() {
    return showGoogleSearch;
  }
}
