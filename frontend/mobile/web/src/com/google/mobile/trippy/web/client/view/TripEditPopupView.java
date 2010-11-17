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
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.mobile.trippy.web.client.TrippyBundle;
import com.google.mobile.trippy.web.client.presenter.TripEditPopupPresenter.Display;

/**
 * This class is responsible to show the trip edit popup.
 * 
 * 
 */
@SuppressWarnings("deprecation")
public class TripEditPopupView extends Composite implements Display {

  static {
    // Create an inject decorator-specific resources.
    TrippyBundle resources = GWT.create(TrippyBundle.class);
    StyleInjector.inject(resources.commonStyle().getText());
  }

  /** Ui Binder. */
  @UiTemplate("TripEditPopupView.ui.xml")
  interface Binder extends UiBinder<Widget, TripEditPopupView> {

  }

  @UiField Label lblLocationEmpty;
  @UiField DecoratedPopupPanel tripEditPopup;
  @UiField Label btnSave;
  @UiField Image cancelImg;
  @UiField TextBox displayTripName;
  @UiField RadioButton btnTripUnschedule;
  @UiField RadioButton btnTripSchedule;
  @UiField ListBox dayList;
  @UiField ListBox monthList;
  @UiField ListBox yearList;
  @UiField TextBox durationField;

  private static Binder uiBinder = GWT.create(Binder.class);


  public TripEditPopupView() {
    initWidget(uiBinder.createAndBindUi(this));
    tripEditPopup.setGlassEnabled(true);
    Window.addResizeHandler(new ResizeHandler() {

      @Override
      public void onResize(ResizeEvent event) {
        if (tripEditPopup.isShowing()) {
          tripEditPopup.center();
        }
      }
    });
    
    KeyPressHandler keyPressHandler = new KeyPressHandler() {
      
      @Override
      public void onKeyPress(KeyPressEvent event) {
        if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
          NativeEvent clickEvent = Document.get().createClickEvent(1, 0, 0, 0, 0, false,
            false, false, false);
          btnSave.getElement().dispatchEvent(clickEvent);
        }
      }
    };
    
    displayTripName.addKeyPressHandler(keyPressHandler);
    durationField.addKeyPressHandler(keyPressHandler);
  }

  @Override
  public Widget asWidget() {
    return this;
  }

  @Override
  public void setPopupVisible(final boolean visible) {
    if (visible) {
      tripEditPopup.center();
      tripEditPopup.show();
    } else {
      tripEditPopup.hide();
      //TODO : Remove extra call to hide popup(Fixed issue by latest gwt).
      tripEditPopup.removeFromParent();
    }
  }
  
  @Override
  public HasClickHandlers getSaveDate() {
    return btnSave;
  }
  
  @Override
  public void setDate(final int date) {
    dayList.setSelectedIndex((date - 1));
  }

  @Override
  public void setMonth(final int month) {
    monthList.setSelectedIndex(month);
  }

  @Override
  public void setYear(final int year) {
    yearList.setSelectedIndex((year - START_YEAR));
  }

  @Override
  public int getDate() {
    return (dayList.getSelectedIndex() + 1);
  }

  @Override
  public int getMonth() {
    return monthList.getSelectedIndex();
  }

  @Override
  public int getYear() {
    return Integer.parseInt(yearList.getValue(yearList.getSelectedIndex()));
  }

  @Override
  public String getDuration() {
    return (durationField.getText());
  }

  @Override
  public void setDuration(final String duration) {
    durationField.setText(duration);
  }

  @Override
  public String getTripName() {
    return displayTripName.getText();
  }
  
  @Override
  public void setTripName(final String name) {
    displayTripName.setText(name);
  }

  @Override
  public void showErrorMsg(final String msg) {
    lblLocationEmpty.setText(msg);
  }

  @Override
  public HasClickHandlers getCancel() {
    return cancelImg;
  }

  @Override
  public void clearErrorMsg() {
    lblLocationEmpty.setText("");
  }

  @Override
  public void setEnabledDate(final boolean enabled) {
    dayList.setEnabled(enabled);
    monthList.setEnabled(enabled);
    yearList.setEnabled(enabled);
  }

  @Override
  public HasClickHandlers getTripSchedule() {
    return btnTripSchedule;
  }

  @Override
  public HasClickHandlers getTripUnschedule() {
    return btnTripUnschedule;
  }

  @Override
  public void setTripSchedule(final boolean checked) {
    btnTripSchedule.setChecked(checked);
  }

  @Override
  public void setTripUnschedule(final boolean checked) {
    btnTripUnschedule.setChecked(checked);
  }

  @Override
  public boolean isTripUnscheduleChecked() {
    return btnTripUnschedule.isChecked();
  }

  @Override
  public boolean isTripScheduleChecked() {
    return btnTripSchedule.isChecked();
  }

  @Override
  public void setDefaultValues() {
    // Creating list view for dates.
    for (int i = START_DAY; i <= LAST_DAY; i++) {
      dayList.addItem("" + i);
    }
    // Creating list view for months.
    for (int i = 0; i <= LAST_MONTH; i++) {
      monthList.addItem(MONTHS[i]);
    }
    // Creating list view for years.
    for (int i = START_YEAR; i <= MAX_YEAR; i++) {
      yearList.addItem("" + i);
    }
  }

  @Override
  public void clearDate() {
    dayList.clear();
    monthList.clear();
    yearList.clear();
  }
}
