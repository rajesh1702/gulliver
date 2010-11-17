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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.mobile.trippy.web.client.base.Provider;
import com.google.mobile.trippy.web.client.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Presenter for screen menu
 * 
 *
 */
public class MenuPresenter implements EventHandlerPresenter<MenuPresenter.Display> {

  /** View for this presenter*/
  public interface Display extends View {
    HasClickHandlers getLabel();

    boolean isPopupVisible();

    void setMenuPopupTitle(String popupTitle);
    
    void setPopupVisible(boolean visible);

    void add(MenuItemPresenter.Display menuItem);

    void insert(MenuItemPresenter.Display menuItem, int index);

    void remove(MenuItemPresenter.Display menuItem);

    void clearMenuItems();
  }

  private final Display display;
  private final List<HandlerRegistration> handlers;
  private final Provider<MenuItemPresenter> menuItemProvider;
  final List<MenuItemPresenter> menuItems;
  
  public MenuPresenter(final Display display, final Provider<MenuItemPresenter> menuItemProvider) {
    this.display = display;
    this.menuItemProvider = menuItemProvider;
    this.handlers = new ArrayList<HandlerRegistration>();
    this.menuItems = new ArrayList<MenuItemPresenter>();
  }

  public void clear() {
    display.clearMenuItems();
    menuItems.clear();
  }
  
  public void addMenuItem(final String label, final ImageResource icon, boolean disabled, final Runnable operation) {
    MenuItemPresenter item = menuItemProvider.get();
    item.setContent(label, icon, new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        display.setPopupVisible(false);
        if (operation != null) {
          operation.run();
        }
      }
    });
    item.setDisabled(disabled);
    menuItems.add(item);
    display.add(item.getDisplay());
  }

  public void addMenuItem(final String label, final ImageResource icon, final Runnable operation) {
    addMenuItem(label, icon, false, operation);
  }
  
  @Override
  public void bind() {
    for (MenuItemPresenter menuItem : menuItems) {
      menuItem.bind();
    }
    handlers.add(display.getLabel().addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        display.setPopupVisible(!display.isPopupVisible());
      }
    }));
  }

  @Override
  public HandlerManager getEventBus() {
    return null;
  }

  @Override
  public void release() {
    for (MenuItemPresenter menuItem : menuItems) {
      menuItem.release();
    }
    
    for (HandlerRegistration handler : handlers) {
      handler.removeHandler();
    }
    handlers.clear();
  }

  @Override
  public Display getDisplay() {
    return display;
  }
  
  public void showMenu() {
    display.setPopupVisible(true);
  }
  
  public void hideMenu() {
    display.setPopupVisible(false);
  }
  
  public void setPopupTitle(String popupTitle) {
    display.setMenuPopupTitle(popupTitle);
  }
}
