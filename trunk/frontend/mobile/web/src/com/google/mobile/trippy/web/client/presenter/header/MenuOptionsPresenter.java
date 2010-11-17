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

package com.google.mobile.trippy.web.client.presenter.header;

import com.google.common.base.Preconditions;
import com.google.gwt.event.shared.HandlerManager;
import com.google.mobile.trippy.web.client.TrippyBundle;
import com.google.mobile.trippy.web.client.base.Constants;
import com.google.mobile.trippy.web.client.base.SingletonComponents;
import com.google.mobile.trippy.web.client.db.LocalDbService;
import com.google.mobile.trippy.web.client.event.ShowHomePageEvent;
import com.google.mobile.trippy.web.client.presenter.MenuPresenter;
import com.google.mobile.trippy.web.client.presenter.SearchBarPresenter;

/**
 * Presenter for the Menu Header Options.
 * 
 */
public abstract class MenuOptionsPresenter extends BaseHeaderOptionsPresenter {
  
  /**
   * Listener for user classes to get search click notification, so that they
   * can customize search by setting search bounds or trip day etc.
   */
  public interface SearchClickListener {
    void onSearchClick(SearchBarPresenter searchBar);
  }
  
  /**
   * Interface for view of this presenter
   */
  public interface Display extends BaseHeaderOptionsPresenter.Display {
    void setMenu(MenuPresenter.Display menuDisplay);
  }

  protected SearchClickListener searchClickListener;
  
  protected SingletonComponents singletonComponents;
  protected MenuPresenter menuPresenter;
  private boolean isMapScreen;
  private final Display display;

  public MenuOptionsPresenter(final Display display) {
    super(display);
    this.display = display;
  }

  public void setMapScreen(final boolean mapScreen) {
    this.isMapScreen = mapScreen;
  }

  /**
   * Handle user events on header.
   * 
   * Events Fired: None Events listened: None
   */
  @Override
  public void bind() {
    menuPresenter.bind();
  }

  @Override
  public HandlerManager getEventBus() {
    return singletonComponents.getEventBus();
  }

  @Override
  public void release() {
    if (menuPresenter != null) {
      menuPresenter.release();
    }
    super.release();
  }

  public void addMenu() {
    Preconditions.checkNotNull(menuPresenter);
    addCommonMenuItems();
    display.setMenu(menuPresenter.getDisplay());
  }

  @Override
  public Display getDisplay() {
    return display;
  }

  protected boolean isMapScreen() {
    return isMapScreen;
  }

  private void addCommonMenuItems() {
    menuPresenter.addMenuItem(Constants.HOME_STR, TrippyBundle.INSTANCE.homeIcon(), new Runnable() {
      @Override
      public void run() {
        singletonComponents.getEventBus().fireEvent(new ShowHomePageEvent());
      }
    });

    menuPresenter.addMenuItem(Constants.LOGOUT_STR, TrippyBundle.INSTANCE.signoutIcon(),
        new Runnable() {
          @Override
          public void run() {
            LocalDbService.clearDb();
            singletonComponents.getUtils().redirect(singletonComponents.getUtils().getLogoutUrl());
          }
        });
  }
  
  /**
   * Set "search" click listener, User class can use it, if want to customize 
   * search action.
   */
  public void setSearchClickListener(SearchClickListener listener) {
    searchClickListener = listener;
  }

  public MenuPresenter getMenuPresenter() {
    return menuPresenter;
  }
}
