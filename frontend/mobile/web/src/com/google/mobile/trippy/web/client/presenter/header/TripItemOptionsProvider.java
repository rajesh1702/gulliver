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

import com.google.mobile.trippy.web.client.base.Provider;
import com.google.mobile.trippy.web.client.base.SingletonComponents;
import com.google.mobile.trippy.web.client.presenter.DayPopupPresenter;
import com.google.mobile.trippy.web.client.presenter.MenuItemPresenter;
import com.google.mobile.trippy.web.client.presenter.MenuPresenter;
import com.google.mobile.trippy.web.client.presenter.SearchBarPresenter;
import com.google.mobile.trippy.web.client.view.MenuOptionsView;

/**
 * Provider for TripItemOptionsPresenter.
 * 
 */
public class TripItemOptionsProvider implements Provider<TripItemOptionsPresenter> {

  private final MenuOptionsPresenter.Display display;
  private final SingletonComponents singletonComponents;
  private final Provider<SearchBarPresenter> searchProvider;
  private final Provider<MenuPresenter> menuProvider;
  private final Provider<DayPopupPresenter> dayPopupProvider;

  public TripItemOptionsProvider(final MenuOptionsPresenter.Display display,
      final SingletonComponents singletonComponents,
      final Provider<SearchBarPresenter> searchProvider,
      final Provider<MenuPresenter> menuProvider,
      final Provider<MenuItemPresenter> menuItemProvider,
      final Provider<DayPopupPresenter> dayPopupProvider) {
    this.display = display;
    this.singletonComponents = singletonComponents;
    this.searchProvider = searchProvider;
    this.menuProvider = menuProvider;
    this.dayPopupProvider = dayPopupProvider;
  }

  @Override
  public TripItemOptionsPresenter get() {
    if (display != null) {
      return new TripItemOptionsPresenter(display, singletonComponents, searchProvider,
          menuProvider, dayPopupProvider);
    } else {
      return new TripItemOptionsPresenter(new MenuOptionsView(), singletonComponents,
          searchProvider, menuProvider, dayPopupProvider);
    }
  }
}
