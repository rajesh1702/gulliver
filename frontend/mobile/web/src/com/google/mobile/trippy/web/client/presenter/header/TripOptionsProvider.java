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
import com.google.mobile.trippy.web.client.presenter.MenuPresenter;
import com.google.mobile.trippy.web.client.presenter.SearchBarPresenter;
import com.google.mobile.trippy.web.client.presenter.TripEditPopupPresenter;
import com.google.mobile.trippy.web.client.view.MenuOptionsView;

/**
 * Provider for TripHeaderPresenter.
 * 
 * 
 */
public class TripOptionsProvider implements Provider<TripOptionsPresenter> {

  private final MenuOptionsPresenter.Display display;
  private final SingletonComponents singletonComponents;
  private final Provider<SearchBarPresenter> searchProvider;
  private final Provider<TripEditPopupPresenter> tripEditProvider;
  private final Provider<MenuPresenter> menuProvider;

  public TripOptionsProvider(final MenuOptionsPresenter.Display display,
      final SingletonComponents singletonComponents,
      final Provider<SearchBarPresenter> searchProvider,
      final Provider<TripEditPopupPresenter> tripEditProvider,
      final Provider<MenuPresenter> menuProvider) {
    this.display = display;
    this.singletonComponents = singletonComponents;
    this.searchProvider = searchProvider;
    this.tripEditProvider = tripEditProvider;
    this.menuProvider = menuProvider;
  }

  @Override
  public TripOptionsPresenter get() {
    if (display != null) {
      return new TripOptionsPresenter(display, singletonComponents, searchProvider,
          tripEditProvider, menuProvider);
    } else {
      return new TripOptionsPresenter(new MenuOptionsView(), singletonComponents, searchProvider,
          tripEditProvider, menuProvider);
    }
  }
}
