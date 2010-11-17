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

import com.google.gwt.event.shared.HandlerManager;
import com.google.mobile.trippy.web.client.base.Provider;
import com.google.mobile.trippy.web.client.base.SingletonComponents;
import com.google.mobile.trippy.web.client.event.SearchClickEvent;
import com.google.mobile.trippy.web.client.event.SearchClickEventHandler;
import com.google.mobile.trippy.web.client.presenter.TripListFilterPresenter;

/**
 * Presenter for header for screens showing logOut button and trip search icon.
 * 
 */
public class LogOutOptionsPresenter extends BaseHeaderOptionsPresenter {

  /**
   * Interface for view of this presenter.
   */
  public interface Display extends BaseHeaderOptionsPresenter.Display {
  }

  private final Display display;
  private final SingletonComponents singletonComponents;
  private final TripListFilterPresenter filterPresenter;

  public LogOutOptionsPresenter(final Display display,
      final SingletonComponents singletonComponents,
      final Provider<TripListFilterPresenter> filterProvider) {
    super(display);
    this.display = display;
    this.singletonComponents = singletonComponents;
    this.filterPresenter = filterProvider.get();
  }

  /**
   * Handle user events on header options.
   * 
   * Events Fired: None 
   * 
   * Events listened: SearchClickEvent, it is fired by clicking on native 
   * search button.
   */
  @Override
  public void bind() {
    filterPresenter.bind();
    super.setSearchAction(new Runnable() {
      @Override
      public void run() {
        filterPresenter.clearSearchBox();
        filterPresenter.showPopup();
      }
    });
    
    // For hardware search action.
    HANDLERS.add(singletonComponents.getEventBus().addHandler(
        SearchClickEvent.getType(), new SearchClickEventHandler() {
      @Override
      public void onSearch(SearchClickEvent event) {
        filterPresenter.clearSearchBox();
        filterPresenter.showPopup();
      }
    }));
  }

  @Override
  public void release() {
    filterPresenter.release();
    super.release();
  }

  @Override
  public HandlerManager getEventBus() {
    return singletonComponents.getEventBus();
  }

  @Override
  public Display getDisplay() {
    return display;
  }
}
