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
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.mobile.trippy.web.client.i18n.Message;
import com.google.mobile.trippy.web.client.presenter.DayPopupPresenter;

import java.util.ArrayList;

/**
 * This Class is responsible to create and show the popup to select the day.
 *  
 *
 */

public class DayPopUpView extends Composite implements DayPopupPresenter.Display{
  
  /** UI Binder. */
  @UiTemplate("DayPopUpView.ui.xml")
  interface Binder extends UiBinder<Widget, DayPopUpView> {
  }

  @UiField DecoratedPopupPanel dayPopUp;
  @UiField FocusPanel cancel;
  @UiField FlowPanel content;
  @UiField Label selectDaysLabel;

  private static Binder uiBinder = GWT.create(Binder.class);
  private final DayPopUpStyle dayStyle = new DayPopUpStyle();
  private ArrayList<HasClickHandlers> dayHandlers;
  
  public DayPopUpView() {
    initWidget(uiBinder.createAndBindUi(this));
    dayPopUp.setStyleName(dayStyle.style.base());
    dayPopUp.setGlassEnabled(true);
    dayPopUp.setModal(true);
  }

  /**
   * Sets the content of Pop up Panel.
   * 
   */
  @Override
  public void createPopUp(final int daysCount) {
    if (dayHandlers == null) {
      dayHandlers = new ArrayList<HasClickHandlers>();
    } else {
      dayHandlers.clear();
    }
    content.clear();
    final FlowPanel daysMenu = new FlowPanel();
    selectDaysLabel.addStyleName(dayStyle.style.titleBaseOriginal());
    for (int i = 0; i <= daysCount; i++) {
      Label dayLabel = new Label("Day " + i);
      dayLabel.setStyleName(dayStyle.style.dayLabel());
      if (i == 0) {
        final Message messages = GWT.create(Message.class);
        dayLabel.setText(messages.unscheduled());
      }
      daysMenu.add(dayLabel);
      dayHandlers.add(dayLabel);
    }
    content.add(daysMenu);
    dayPopUp.hide();
  }
    
  @Override
  public ArrayList<HasClickHandlers> getDayHandlers() {
    if (dayHandlers == null) {
      dayHandlers = new ArrayList<HasClickHandlers>();
    }
    return dayHandlers;
  }
  
  @Override
  public HasClickHandlers getCancel() {
    return cancel;
  }

  @Override
  public void setPopupVisible(boolean visible) {
    if (visible) {
      dayPopUp.center();
      dayPopUp.show();
    } else {
      dayPopUp.hide();
    }
  }

  @Override
  public Widget asWidget() {
    return this;
  }
}
