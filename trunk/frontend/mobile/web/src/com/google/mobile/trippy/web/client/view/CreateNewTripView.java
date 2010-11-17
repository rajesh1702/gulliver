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
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.mobile.trippy.web.client.TrippyBundle;
import com.google.mobile.trippy.web.client.i18n.Message;
import com.google.mobile.trippy.web.client.presenter.CreateNewTripPresenter.Display;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible to create View for create a new trip screen.
 * 
 *
 */
public class CreateNewTripView extends Composite implements Display {

  static {
    // Create an inject decorator-specific resources.
    TrippyBundle resources = GWT.create(TrippyBundle.class);
    StyleInjector.inject(resources.commonStyle().getText());
  }

  /**
   * Interface for css style.
   * 
   *
   */
  public static interface Style extends CssResource {
    String cancelImage();
    String tripName();
    String topDestinationStyle();
    String topSuggestionBase();
    String arrowIcon();
  }

  /** UI Binder. */
  @UiTemplate("CreateNewTrip.ui.xml")
  interface Binder extends UiBinder<Widget, CreateNewTripView> {
  }

  @UiField Label lblErrorMsg;
  @UiField DecoratedPopupPanel popupPanel;
  @UiField FlowPanel tripNameList;
  @UiField Style style;
  @UiField Image imgCancel;
  @UiField TextBox txtBoxLocation;
  @UiField Label btnCreateTrip;
  @UiField FlowPanel flwTopDestinations;

  private static Binder uiBinder = GWT.create(Binder.class);
  private List<HasClickHandlers> suggestedTripListClicks;
  private List<HasClickHandlers> topDestinationHandler;
  private List<String> topDestinationText;

  public CreateNewTripView() {
    initWidget(uiBinder.createAndBindUi(this));
    imgCancel.addStyleName(style.cancelImage());
    popupPanel.removeFromParent();
    popupPanel.setGlassEnabled(true);
    
    txtBoxLocation.addKeyPressHandler(new KeyPressHandler() {
      
      @Override
      public void onKeyPress(KeyPressEvent event) {
        if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
          // Create a click event and dispatch it to create trip button.
          NativeEvent clickEvent = Document.get().createClickEvent(1, 0, 0, 0, 0, false,
            false, false, false);
          btnCreateTrip.getElement().dispatchEvent(clickEvent);
        }
      }
    });
  }

  @Override
  public HasClickHandlers getCreateNewTripButton() {
    return btnCreateTrip;
  }
  
  @Override
  public List<HasClickHandlers> getTopDestinations() {
    return topDestinationHandler;
  }

  @Override
  public List<String> getTopDestinationsText() {
    return topDestinationText;
  }

  @Override
  public void setTopDestinations(final String topDestinations[]) {
    // Top destinations are fetched and clickHandlers are created accordingly.
    if (topDestinationHandler == null) {
      topDestinationHandler = new ArrayList<HasClickHandlers>();
    } else {
      topDestinationHandler.clear();
    }

    if (topDestinationText == null) {
      topDestinationText = new ArrayList<String>();
    } else {
      topDestinationText.clear();
    }

    // Assuring topdestinationHandler,topDestinationText and view for 
    // top destination are clear.
    flwTopDestinations.clear();
    for (final String topDestination : topDestinations) {
      final Label destination = new Label(topDestination);
      topDestinationText.add(topDestination);
      destination.addStyleName(TrippyBundle.INSTANCE.commonStyle().titleFont());
      destination.addStyleName(TrippyBundle.INSTANCE.commonStyle().colorFont());
      destination.addStyleName(style.topDestinationStyle());
      destination.addStyleName(style.topSuggestionBase());
      final Image imgArrow = new Image();
      imgArrow.setUrl(TrippyBundle.INSTANCE.arrowIcon().getURL());
      imgArrow.addStyleName(style.arrowIcon());
      final FocusPanel topDestinationPanel = new FocusPanel();
      final FlowPanel panel = new FlowPanel();
      panel.add(imgArrow);
      panel.add(destination);
      topDestinationPanel.add(panel);
      topDestinationHandler.add(topDestinationPanel);
      flwTopDestinations.add(topDestinationPanel);
    }
  }

  @Override
  public Widget asWidget() {
    return this;
  }

  @Override
  public String getLocationText() {
    return txtBoxLocation.getText();
  }

  @Override
  public void setLocationBoxEmpty() {
    txtBoxLocation.setText("");
  }

  @Override
  public void setLocationEmptyErrorMsg() {
    final Message message = GWT.create(Message.class);
    lblErrorMsg.setText(message.msgErrLocationEmpty());
  }

  @Override
  public void addSuggestedTripList(final List<String> suggestedTripList) {
    tripNameList.clear();
    if (suggestedTripListClicks == null) {
      suggestedTripListClicks = new ArrayList<HasClickHandlers>();
    } else {
      suggestedTripListClicks.clear();
    }
    for (final String suggestedTrip : suggestedTripList) {
      final Label tripLabel = new Label();
      tripLabel.setText(suggestedTrip);
      tripLabel.addStyleName(style.tripName());
      tripLabel.addStyleName(TrippyBundle.INSTANCE.commonStyle().baseFont());
      tripLabel.addStyleName(TrippyBundle.INSTANCE.commonStyle().colorFont());
      tripNameList.add(tripLabel);
      suggestedTripListClicks.add(tripLabel);
    }
  }

  @Override
  public void showPopup() {
    popupPanel.center();
  }

  @Override
  public void hidePopup() {
    popupPanel.hide();
  }

  @Override
  public HasClickHandlers getClosePopUp() {
    return imgCancel;
  }

  @Override
  public List<HasClickHandlers> getSuggestedTripList() {
    return suggestedTripListClicks;
  }

  @Override
  public void clearErrorMsg() {
    lblErrorMsg.setText("");
  }
}
