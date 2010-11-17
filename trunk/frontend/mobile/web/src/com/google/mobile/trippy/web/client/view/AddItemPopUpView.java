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
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.mobile.trippy.web.client.i18n.Message;
import com.google.mobile.trippy.web.client.presenter.AddItemPopupPresenter;

import java.util.ArrayList;

/**
 * This Class is responsible to create and show the popup to select the multiple
 * days to add items to the trip.
 * 
 * 
 */

public class AddItemPopUpView extends Composite implements AddItemPopupPresenter.Display {
  
  /** UI Binder. */
  @UiTemplate("DayPopUpView.ui.xml")
  interface Binder extends UiBinder<Widget, AddItemPopUpView> {
  }

  @UiField DecoratedPopupPanel dayPopUp;
  @UiField FocusPanel cancel;
  @UiField FlowPanel content;
  @UiField FlowPanel buttonPanel;
  @UiField Label addItemsBtn;
  @UiField Label selectDaysLabel;

  private static Binder uiBinder = GWT.create(Binder.class);
  private final DayPopUpStyle dayStyle = new DayPopUpStyle();
  private ArrayList<CheckBox> daysPanel;
  private ArrayList<Integer> selectedDay;
  final FlowPanel daysMenu = new FlowPanel();
  
  public AddItemPopUpView() {
    initWidget(uiBinder.createAndBindUi(this));
    dayPopUp.setStyleName(dayStyle.style.base());
    dayPopUp.setGlassEnabled(true);
    dayPopUp.setModal(true);
    buttonPanel.setVisible(true);
    addItemsBtn.setVisible(true);
  }

  /**
   * Sets the content of Pop up Panel.
   * 
   */
  @Override
  public void createPopUp(final int daysCount) {
    if (daysPanel == null) {
      daysPanel = new ArrayList<CheckBox>();
    } else {
      daysPanel.clear();
    }
    content.clear();
    
    selectDaysLabel.setText("Select Day(s)");
    selectDaysLabel.addStyleName(dayStyle.style.titleBaseModified());    
    
    daysMenu.clear();
    daysMenu.setStyleName(dayStyle.style.daysMenu());
    addSelectOptions(daysMenu);

    for (int i = 0; i <= daysCount; i++) {
      final CheckBox dayPanel = new CheckBox(" Day " + i);
      dayPanel.setStyleName(dayStyle.style.dayPanel());
      if (i == 0) {
        final Message messages = GWT.create(Message.class);
        dayPanel.setText(" " + messages.unscheduled());
        dayPanel.setValue(true);
      } else {
        // if any day is selected, make sure we uncheck "unscheduled"
        dayPanel.addClickHandler(new ClickHandler() {
          public void onClick(ClickEvent event) {
            boolean checked = ((CheckBox) event.getSource()).getValue();
            if (checked) {
              ((CheckBox) (daysMenu.getWidget(0))).setValue(false);
            } else {
              ((CheckBox) (daysMenu.getWidget(0))).setValue(true);
              for (int i = 1; i < daysMenu.getWidgetCount(); i++) {
                CheckBox cb = (CheckBox) daysMenu.getWidget(i);
                if (cb.getValue()) {
                  ((CheckBox) (daysMenu.getWidget(0))).setValue(false);
                }                
              }
            }
          }
        });
      }
      daysMenu.add(dayPanel);
      daysPanel.add(dayPanel);
    }
    content.add(daysMenu);
    dayPopUp.hide();
  }
    
  private void addSelectOptions(final FlowPanel daysMenu) {
    Anchor all = new Anchor("Select all");
    all.addClickHandler(new ClickHandler() {
          public void onClick(ClickEvent event) {
            resetCheckMarks(true);
          }
    });

    Anchor none = new Anchor("Select none");
    none.addClickHandler(new ClickHandler() {
          public void onClick(ClickEvent event) {
            resetCheckMarks(false);
          }
    });
    
    all.setStyleName(dayStyle.style.selectAnchor());
    none.setStyleName(dayStyle.style.selectAnchor());
    
    content.add(all);
    content.add(none);    
  }
  @SuppressWarnings("deprecation")
  @Override
  public ArrayList<Integer> getSelectedDay() {
    if (selectedDay == null) {
      selectedDay = new ArrayList<Integer>();
    } else {
      selectedDay.clear();
    }
    for (final CheckBox dayPanel : daysPanel) {
      if (dayPanel.isChecked()) {
        selectedDay.add(daysPanel.indexOf(dayPanel));
      }
    }
    return selectedDay;
  }
  
  @Override
  public HasClickHandlers getCancel() {
    return cancel;
  }

  @Override
  public void setPopupVisible(final boolean visible) {
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

  @Override
  public HasClickHandlers getAddItems() {
    return addItemsBtn;
  }

  public void resetCheckMarks(boolean checkAll) {
    for (int i = 1; i < daysMenu.getWidgetCount(); i++) {
      CheckBox cb = (CheckBox) daysMenu.getWidget(i);
      cb.setValue(checkAll);               
    }
    ((CheckBox) daysMenu.getWidget(0)).setValue(!checkAll);
  }
  
  @SuppressWarnings("deprecation")
  @Override
  public void setChecked(final boolean checked) {
    for (final CheckBox dayBox : daysPanel) {
      dayBox.setChecked(checked);
    }
  }
}
 