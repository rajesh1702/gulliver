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

package com.google.mobile.trippy.web.client.presenter;

import com.google.common.base.Preconditions;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.mobile.trippy.web.client.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible to create a popup to select multiple days to add
 * items.
 * 
 */
public class AddItemPopupPresenter implements 
    EventHandlerPresenter<AddItemPopupPresenter.Display> {

  /**
   * Interface for the view of this presenter
   */
  public interface Display extends View {
    void createPopUp(final int daysCount);
    void resetCheckMarks(boolean checkAll);
    void setPopupVisible(final boolean visible);
    void setChecked(final boolean checked);
    List<Integer> getSelectedDay();
    HasClickHandlers getAddItems();
    HasClickHandlers getCancel();
  }

  private final Display display;
  private final List<HandlerRegistration> handlers;
  
  public AddItemPopupPresenter(final Display display) {
    this.display = display;
    this.handlers = new ArrayList<HandlerRegistration>();
  }

  public void showPopup() {
    display.resetCheckMarks(false);
    display.setPopupVisible(true);
  }
  
  public void hidePopup() {
    display.setPopupVisible(false);
  }

  /**
   * Set the trip detail header.
   * 
   */
  public void setDays(final int days) {
    Preconditions.checkArgument(days > 0);
    display.createPopUp(days);
  }

  @Override
  public Display getDisplay() {
    return display;
  }
  
  public List<Integer> getSelectedDay() {
    return display.getSelectedDay();
  }

  public HasClickHandlers getAddItems() {
    return display.getAddItems();
  }

  /**
   * Method binds the handlers with event.
   * 
   * This method takes the responsibility for handling all the events and
   * firing the events.
   * 
   * Event Listened: None.
   *
   * Events fired : None.
   */
  @Override
  public void bind() {
    handlers.add(display.getCancel().addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        display.setPopupVisible(false);
      }
    }));
  }

  @Override
  public HandlerManager getEventBus() {
    return null;
  }

  @Override
  public void release() {
    for (final HandlerRegistration handler : handlers) {
      if (handler != null) {
        handler.removeHandler();
      }
    }
    handlers.clear();
  }
}

