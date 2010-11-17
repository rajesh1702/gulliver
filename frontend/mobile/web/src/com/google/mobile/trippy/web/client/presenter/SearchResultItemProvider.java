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

import com.google.gwt.core.client.GWT;
import com.google.mobile.trippy.web.client.base.Provider;
import com.google.mobile.trippy.web.client.base.SingletonComponents;
import com.google.mobile.trippy.web.client.service.LPSearchService;
import com.google.mobile.trippy.web.client.service.LPSearchServiceAsync;
import com.google.mobile.trippy.web.client.view.SearchResultItemDetailsView;
import com.google.mobile.trippy.web.client.view.SearchResultItemListView;
import com.google.mobile.trippy.web.client.view.SearchResultItemMapView;

/**
 * Provider class for {@link SearchResultItemPresenter}.
 * 
 */
public class SearchResultItemProvider implements Provider<SearchResultItemPresenter> {
  /**
   * Enum for view type.
   */
  public static enum ViewType {
    DETAILS, LIST, MAP
  }

  private ViewType viewType;
  private SearchResultItemPresenter.Display display;
  private final AddItemPopupProvider addItemPopupProvider;
  private final SingletonComponents singletonComponents;

  /**
   * Create a provider which uses the specified view to create the presenters
   */
  public SearchResultItemProvider(final ViewType viewType,
      final SingletonComponents singletonComponent,
      final AddItemPopupProvider addItemPopupProvider) {
    this.viewType = viewType;
    this.singletonComponents = singletonComponent;
    this.addItemPopupProvider = addItemPopupProvider;
  }

  /**
   * Create a provider which creates a new view for each new presenter.
   */
  public SearchResultItemProvider(final SearchResultItemPresenter.Display display,
      final SingletonComponents singletonComponent,
      final AddItemPopupProvider addItemPopupProvider) {
    this.display = display;
    this.singletonComponents = singletonComponent;
    this.addItemPopupProvider = addItemPopupProvider;
  }

  /**
   * If a view has been provided use that else create a new view for each
   * presenter
   */
  @Override
  public SearchResultItemPresenter get() {
    final LPSearchServiceAsync lpSearchServiceAsync = GWT.create(LPSearchService.class);
    if (display != null) {
      return new SearchResultItemPresenter(display, singletonComponents, addItemPopupProvider,
          lpSearchServiceAsync);
    }
    switch (viewType) {
      case DETAILS:
        return new SearchResultItemPresenter(new SearchResultItemDetailsView(),
            singletonComponents, addItemPopupProvider, lpSearchServiceAsync);
      case LIST:
        return new SearchResultItemPresenter(new SearchResultItemListView(), singletonComponents,
            addItemPopupProvider, lpSearchServiceAsync);
      case MAP:
        return new SearchResultItemPresenter(new SearchResultItemMapView(), singletonComponents,
            addItemPopupProvider, lpSearchServiceAsync);
    }
    return null;
  }
}
