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
import com.google.mobile.trippy.web.client.presenter.TripListFilterPresenter;
import com.google.mobile.trippy.web.client.view.MenuOptionsView;

/**
 * Provider for TripHeaderPresenter.
 * 
 * 
 */
public class TripListOptionsProvider implements Provider<TripListOptionsPresenter> {

  private final SingletonComponents singletonComponents;
  private final Provider<TripListFilterPresenter> filterProvider;
  private final Provider<MenuPresenter> menuProvider;

  public TripListOptionsProvider(final SingletonComponents singletonComponents,
      final Provider<TripListFilterPresenter> filterProvider,
      final Provider<MenuPresenter> menuProvider) {
    this.singletonComponents = singletonComponents;
    this.menuProvider = menuProvider;
    this.filterProvider = filterProvider;
  }

  @Override
  public TripListOptionsPresenter get() {
      return new TripListOptionsPresenter(new MenuOptionsView(), singletonComponents, 
          filterProvider, menuProvider);
    
  }
}
