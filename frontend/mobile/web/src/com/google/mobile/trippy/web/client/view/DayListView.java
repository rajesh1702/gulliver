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
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.mobile.trippy.web.client.TrippyBundle;
import com.google.mobile.trippy.web.client.presenter.DayListItemPresenter;
import com.google.mobile.trippy.web.client.presenter.DayListPresenter;
import com.google.mobile.trippy.web.client.presenter.TripItemPresenter;
import com.google.mobile.trippy.web.shared.models.IdDayDateTuple;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.VerticalPanelDropController;

import java.util.ArrayList;
import java.util.Date;

/**
 * View for DayListPresenter.
 * 
 */
public class DayListView extends Composite implements DayListPresenter.Display {

  /**
   * style class for DayListView.
   */
  public static interface Style extends CssResource {
    String heading();
  }
  
  /**
   * UI Binder to bind java and xml codes
   */
  @UiTemplate("DayListView.ui.xml")
  interface Binder extends UiBinder<Widget, DayListView> {
  }

  @UiField
  VerticalPanel daysList;
  @UiField
  Button saveButton;
  @UiField
  Style style;

  
  private PickupDragController dragController;
  private VerticalPanelDropController widgetDropController;
  public final String imageUrl = TrippyBundle.INSTANCE.greenDot().getURL();
  private static Binder uiBinder = GWT.create(Binder.class);
  private final String noPos = "No Position";

  public DayListView() {
    super();
    initWidget(uiBinder.createAndBindUi(this));
    dragController = new PickupDragController(RootPanel.get(), false);
    dragController.setBehaviorConstrainedToBoundaryPanel(false);
    widgetDropController = new VerticalPanelDropController(
      daysList);
    dragController.registerDropController(widgetDropController);
    saveButton.setVisible(false);
  }

  @Override
  public Widget asWidget() {
    return this;
  }

  @Override
  public void addDay(DayListItemPresenter.Display dayDisplay, int day) {
    Label itemPos = new Label(noPos);
    itemPos.setVisible(false);
    Label dayLabel = new Label("" + day);
    dayLabel.setVisible(false);
    FlowPanel panel = new FlowPanel();
    panel.add(itemPos);
    panel.add(dayLabel);
    panel.add(dayDisplay.asWidget());
    
    daysList.add(panel);
  }
  
  @Override
  public void addTripItem(TripItemPresenter.Display tripItemDisplay, String key) {
    Image heading = new Image(imageUrl);
    heading.setVisible(false);
    heading.setStyleName(style.heading());
    Label itemPos = new Label(key);
    itemPos.setVisible(false);
    
    FlowPanel panel = new FlowPanel();
    panel.add(itemPos);
    panel.add(heading);
    panel.add(tripItemDisplay.asWidget());

    daysList.add(panel);
    dragController.makeDraggable(panel, heading);
  }

  @Override
  public void clear() {
    daysList.clear();
  }
  
  @Override
  public HasClickHandlers getSaveButton() {
    return saveButton;
  }
  
  @Override
  public ArrayList<IdDayDateTuple> getReorderedItems() {
    final Date currTime = new Date();
    
    int day = 0;
    ArrayList<IdDayDateTuple> itemTuples = new ArrayList<IdDayDateTuple>(); 
    for (int index = 0; index < daysList.getWidgetCount(); index++) {
      FlowPanel panel = (FlowPanel) daysList.getWidget(index);
      Label itemPos = (Label) panel.getWidget(0);
      String itemPosText = itemPos.getText();
      if (!itemPosText.equals(noPos)) {
        itemTuples.add(new IdDayDateTuple(itemPosText, day, currTime));
      } else {
        Label dayLabel = (Label) panel.getWidget(1);
        day = Integer.parseInt(dayLabel.getText());
      }
    }
    return itemTuples;
  }
}
