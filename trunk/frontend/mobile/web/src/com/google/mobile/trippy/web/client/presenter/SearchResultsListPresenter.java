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

import com.google.common.annotations.VisibleForTesting;
import com.google.mobile.trippy.web.client.base.Provider;
import com.google.mobile.trippy.web.client.base.SingletonComponents;
import com.google.mobile.trippy.web.client.view.View;
import com.google.mobile.trippy.web.client.widget.Toast;
import com.google.mobile.trippy.web.shared.models.SearchItem;
import com.google.mobile.trippy.web.shared.models.Trip;

import java.util.List;

/**
 * Presenter to show search results in list
 * 
 */
public class SearchResultsListPresenter implements
    BasePresenter<SearchResultsListPresenter.Display> {

  /**
   * Interface for the view of this presenter
   */
  public static interface Display extends View {

    void addSearchItem(SearchResultItemPresenter.Display view);

    void clear();

    void setNoResults(boolean visible);
  }

  /** Object to contain the reference of associated view. */
  private final Display display;

  /** provider to get object of Search Result item presenter. */
  private final Provider<SearchResultItemPresenter> itemProvider;
  private final Toast toast;
  
  public SearchResultsListPresenter(final Display display,
      final SingletonComponents singeltonComponent,
      final Provider<SearchResultItemPresenter> itemProvider) {
    this.display = display;
    this.toast = singeltonComponent.getToast();
    this.itemProvider = itemProvider;

  }

  public Display getDisplay() {
    return display;
  }
  /**
   * This will add search results item view to the display list.
   * 
   * This will get a {@link SearchResultItemPresenter} from the provider of type
   * {@link SearchResultItemPresenter} and will call bind over it. The trip
   * object needs to be set before calling the bind(). Then populateView will be
   * called with {@link SearchItem} and an argument false, that request for a
   * non detailed view of the search result.
   * 
   * The above process will iterate over all elements of search result list.
   */
  @VisibleForTesting
  public void setResults(final Trip trip, final int day, final List<SearchItem> results) {
    display.clear();
    toast.showLoading("Creating list...");
    display.setNoResults(results.isEmpty() || results == null);
    for (SearchItem result : results) {
      SearchResultItemPresenter searchResultItemPresenter = itemProvider.get();
      searchResultItemPresenter.setTrip(result, trip, day);
      searchResultItemPresenter.bind();
      display.addSearchItem(searchResultItemPresenter.getDisplay());
    }
    toast.hideLoading();
  }
}
