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
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.mobile.trippy.web.client.presenter.SearchResultItemPresenter;

import java.util.ArrayList;

/**
 * Class having panel for showing each search Result Item.
 * 
 */
public class SearchResultItemMapView extends Composite
    implements SearchResultItemPresenter.Display {

  /** UI Binder. */
  @UiTemplate("SearchResultItemMapView.ui.xml")
  interface Binder extends UiBinder<Widget, SearchResultItemMapView> {
  }

  @UiField
  Label name;
  @UiField
  Label address;
  @UiField
  FocusPanel addRemove;
  @UiField
  Image imgAddRemove;
  @UiField
  FocusPanel detailsPanel;

  private static Binder uiBinder = GWT.create(Binder.class);

  private HasClickHandlers dummyHandler = new HasClickHandlers() {

    @Override
    public void fireEvent(GwtEvent<?> event) {
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
      return null;
    }
  };

  public SearchResultItemMapView() {
    initWidget(uiBinder.createAndBindUi(this));
    //TODO :Its just remove the tabindex from DOM. But need to find
    //the alternative approach.
    addRemove.getElement().getChild(addRemove.getTabIndex()).removeFromParent();
    detailsPanel.getElement().getChild(detailsPanel.getTabIndex()).removeFromParent();
  }

  @Override
  public HasClickHandlers getShowDetailsButton() {
    return detailsPanel;
  }
  
  @Override
  public HasClickHandlers getAddButton() {
    return addRemove;
  }

  @Override
  public void setName(final String itemName) {
    name.setText(itemName);
  }

  @Override
  public void setAddress(final String itemAddress) {
    address.setText(""); //In Future, set proper address if required.
  }

  @Override
  public void setPhoneNumbers(final ArrayList<String> phoneNumbers) {
  }

  @Override
  public void setMoreInfoURL(final String url) {
  }

  @Override
  public Widget asWidget() {
    return this;
  }

  @Override
  public void setAddButtonVisible(boolean visible) {
    addRemove.setVisible(visible);
  }

  @Override
  public void setReview(String html) {
    // No-ops.
  }

  @Override
  public void showOnGoogleMapUrl(final String url) {
    //No-ops.
  }

  @Override
  public HasClickHandlers getAddIcon() {
    return dummyHandler;
  }

  @Override
  public HasClickHandlers getShowOnMap() {
    return dummyHandler;
  }

  @Override
  public HasClickHandlers getShowOnMapIcon() {
    return dummyHandler;
  }
}
