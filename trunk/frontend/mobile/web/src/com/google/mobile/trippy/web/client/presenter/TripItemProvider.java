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

import com.google.mobile.trippy.web.client.base.Provider;
import com.google.mobile.trippy.web.client.base.SingletonComponents;
import com.google.mobile.trippy.web.client.presenter.header.TripItemOptionsPresenter;
import com.google.mobile.trippy.web.client.view.TripItemDetailsView;
import com.google.mobile.trippy.web.client.view.TripItemListView;
import com.google.mobile.trippy.web.client.view.TripItemMapView;

/**
 * Provider class for {@link TripItemPresenter}.
 * 
 */
public class TripItemProvider implements Provider<TripItemPresenter> {
  /**
   * Enum for view type.
   */
  public static enum ViewType {
    DETAILS, LIST, MAP
  }

  /** Common container for all the singleton components of this application. */
  private ViewType viewType;
  private TripItemPresenter.Display display;
  private final SingletonComponents singletonComponents;
  private final Provider<TripItemOptionsPresenter> tripItemOptionsPresenterProvider;
  
  /**
   * Create a provider which uses the specified view to create the presenters
   */
  public TripItemProvider(final ViewType viewType,
      final SingletonComponents singletonComponent,
      final Provider<TripItemOptionsPresenter> tripItemOptionsPresenterProvider) {
    this.viewType = viewType;
    this.singletonComponents = singletonComponent;
    this.tripItemOptionsPresenterProvider = tripItemOptionsPresenterProvider;
  }

  /**
   * Create a provider which creates a new view for each new presenter.
   */
  public TripItemProvider(final TripItemPresenter.Display display,
      final SingletonComponents singletonComponent,
      final Provider<TripItemOptionsPresenter> tripItemOptionsPresenterProvider) {
    this.display = display;
    this.singletonComponents = singletonComponent;
    this.tripItemOptionsPresenterProvider = tripItemOptionsPresenterProvider;
  }

  /**
   * If a view has been provided use that else create a new view for each
   * presenter
   */
  @Override
  public TripItemPresenter get() {
    if (display != null) {
      return new TripItemPresenter(display, singletonComponents, tripItemOptionsPresenterProvider);
    }
    switch (viewType) {
      case DETAILS:
        return new TripItemPresenter(new TripItemDetailsView(), singletonComponents,
            tripItemOptionsPresenterProvider);
      case LIST:
        return new TripItemPresenter(new TripItemListView(), singletonComponents,
            tripItemOptionsPresenterProvider);
      case MAP:
        return new TripItemPresenter(new TripItemMapView(), singletonComponents,
            tripItemOptionsPresenterProvider);
    }
    return null;
  }
}
