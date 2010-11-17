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
import com.google.common.base.Preconditions;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.maps.client.base.HasLatLng;
import com.google.gwt.maps.client.base.HasLatLngBounds;
import com.google.gwt.maps.client.event.EventCallback;
import com.google.gwt.maps.client.overlay.HasMarker;
import com.google.mobile.trippy.web.client.Page;
import com.google.mobile.trippy.web.client.TrippyBundle;
import com.google.mobile.trippy.web.client.base.Constants;
import com.google.mobile.trippy.web.client.base.Provider;
import com.google.mobile.trippy.web.client.base.SingletonComponents;
import com.google.mobile.trippy.web.client.db.DefaultSearchService;
import com.google.mobile.trippy.web.client.db.SearchService;
import com.google.mobile.trippy.web.client.event.SearchQueryEvent;
import com.google.mobile.trippy.web.client.event.ShowSearchItemDetailsEvent;
import com.google.mobile.trippy.web.client.event.ShowSearchResultsInListEvent;
import com.google.mobile.trippy.web.client.event.ShowTripItemDetailsEvent;
import com.google.mobile.trippy.web.client.event.ShowTripScheduleEvent;
import com.google.mobile.trippy.web.client.presenter.MapPresenter.MarkerClickListener;
import com.google.mobile.trippy.web.client.presenter.header.SearchResultsOptionsPresenter;
import com.google.mobile.trippy.web.shared.models.POIType;
import com.google.mobile.trippy.web.shared.models.SearchItem;
import com.google.mobile.trippy.web.shared.models.SearchItem.SearchType;
import com.google.mobile.trippy.web.shared.models.Trip;
import com.google.mobile.trippy.web.shared.models.TripItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Presenter for search results on map.
 * 
 * 
 */
public class SearchResultsMapPresenter extends BaseMapPresenter {

  /**
   * View interface for this presenter
   */
  public static interface Display extends BaseMapPresenter.Display {
    public static final int RESULTS_PER_PAGE = 50;
    SearchResultItemPresenter.Display getInfoDisplay();
    void setNoItemText(boolean isLp);
    HasClickHandlers getGoogleSearch();
  }

  private final SearchResultItemPresenter searchResultItemPresenter;

  private List<SearchItem> searchResults;
  private List<SearchItem> shownResults;
  private String lastkey;
  private String key;

  /**
   * Parameterized constructor
   */
  public SearchResultsMapPresenter(final Display display,
      final SingletonComponents singletonComponents,
      final Provider<BaseHeaderPresenter> headerProvider, final Provider<MapPresenter> mapProvider,
      final Provider<SearchResultItemPresenter> searchResultItemProvider,
      final Provider<TripEditPopupPresenter> tripEditProvider,
      final Provider<SearchResultsOptionsPresenter> headerOptionsProvider) {
    super(display, singletonComponents, headerProvider, mapProvider, tripEditProvider,
        headerOptionsProvider, false);
    searchResultItemPresenter = searchResultItemProvider.get();
  }

  /**
   * Tasks done: 1) Set the data for sub-components "item details window" and
   * "search box". 2) Show markers on map for each corresponding search result.
   * 3) Show details of first search results in the details window
   */
  public void setResults(final Trip trip, final int day, final List<SearchItem> results,
      final String key) {
    Preconditions.checkNotNull(results);
    Preconditions.checkNotNull(key);

    this.searchResults = results;
    this.key = key;

    // Select a marker image
    if (!searchResults.isEmpty()) {
      SearchItem item = searchResults.get(0);
      if (item.getType().equals(SearchType.LP)) {
        if (item.getId().contains(POIType.DO.toString())) {
          resultPinUrl = TrippyBundle.INSTANCE.pinLPdo().getURL();
        } else if (item.getId().contains(POIType.EAT.toString())) {
          resultPinUrl = TrippyBundle.INSTANCE.pinLPrestaurants().getURL();
        } else if (item.getId().contains(POIType.GENERAL.toString())) {
          resultPinUrl = TrippyBundle.INSTANCE.pinLPgeneral().getURL();
        } else if (item.getId().contains(POIType.NIGHT.toString())) {
          resultPinUrl = TrippyBundle.INSTANCE.pinLPentertainment().getURL();
        } else if (item.getId().contains(POIType.SEE.toString())) {
          resultPinUrl = TrippyBundle.INSTANCE.pinLPsights().getURL();
        } else if (item.getId().contains(POIType.SHOP.toString())) {
          resultPinUrl = TrippyBundle.INSTANCE.pinLPshopping().getURL();
        } else if (item.getId().contains(POIType.SLEEP.toString())) {
          resultPinUrl = TrippyBundle.INSTANCE.pinLPhotels().getURL();
        }
      } else {
        resultPinUrl = TrippyBundle.INSTANCE.mapMarkerNormal().getURL();
      }
    }
    
    super.setMap(trip, day, new Runnable() {
      @Override
      public void run() {
        singletonComponents.getEventBus().fireEvent(new ShowTripScheduleEvent(trip));
      }
    });

    ((SearchResultsOptionsPresenter) headerOptions).setSearchResults(trip, day, key);
    headerPresenter.setSubNavigation(Constants.List_STR, new Runnable() {

      @Override
      public void run() {
        getEventBus().fireEvent(new ShowSearchResultsInListEvent(trip, day, key));
      }
    });

    int completePages = searchResults.size() / Display.RESULTS_PER_PAGE;
    display.populatePageList(
        searchResults.size() % Display.RESULTS_PER_PAGE == 0 ? completePages : completePages + 1);
    display.setSelectedPage(0);
    
    if (shownItems == null) {
      shownItems = new ArrayList<Item>();
      shownResults = new ArrayList<SearchItem>();
    }
    showPageItems(0);
  }

  /**
   * Handle user-events happening on view and update view in response to app
   * events.
   * 
   * Events fired : None; Events listened : None
   */
  @Override
  public void bind() {
    super.bind();
    if (searchResults != null && !searchResults.isEmpty()) {
      searchResultItemPresenter.bind();
    } else {
      ((Display) display).getGoogleSearch().addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          startGoogleSearch();
        }
      });
    }
  }

  @Override
  public void release() {
    super.release();
    if (searchResults != null) {
      searchResultItemPresenter.release();
    }
  }

  @Override
  protected void highlightResultIndex(final int index) {
    final Item item = shownItems.get(index);
    searchResultItemPresenter.setTrip(shownResults.get(index), trip, tripDay);
    highlightResultIndex(index, item, new EventCallback() {
      @Override
      public void callback() {
        singletonComponents.getEventBus().fireEvent(
            new ShowSearchItemDetailsEvent(item.getId(), trip, tripDay));
      }
    });
  }

  @VisibleForTesting
  protected void populateView() {
    List<HasLatLng> points = populate();
    if (points != null && !points.isEmpty()) {
      showTripItemOnMap(points);
      highlightResultIndex(changeHighlightedItem());
    } else {
      //TODO: remove new, if neccesary.
      SearchService searchService = new DefaultSearchService();
      final SearchType type =
        (searchService.extractKeyProperty(key, Page.QUERY_TYPE)).equals(SearchType.GOOGLE
            .toString()) ? SearchType.GOOGLE : SearchType.LP;
      if (type.equals(SearchType.LP)) {
        singletonComponents.getToast().hideLoading();
        singletonComponents.getToast().showToast(singletonComponents.getMessage().noLpResults());
        startGoogleSearch();
      }
      ((Display) display).setNoItemText(type.equals(SearchType.LP));
    }
  }
  
  @Override
  protected void showPageItems(final int page) {
    shownItems.clear();
    shownResults.clear();
    try {
      for (int i = 0; i < Display.RESULTS_PER_PAGE; i++) {
        SearchItem searchItem = searchResults.get(page * Display.RESULTS_PER_PAGE + i);
        shownItems.add(new Item(searchItem));
        shownResults.add(searchItem);
      }
    } catch (IndexOutOfBoundsException aiob) {
      //If the page does not have RESULTS_PER_PAGE results
    }    
    currentItem = 0;
    populateView();
  }
  
  private int changeHighlightedItem() {
    //TODO: clean up this logic
    int highlightedIndex = getSearchItemFromResult(key);
    
    if (highlightedIndex >= 0) { // if from details page.
    } else if (!key.equals(lastkey)) { // if new search.
      lastkey = key;
      highlightedIndex = 0;
    } else { // else back button.
      highlightedIndex = currentItem;
    } 
    return highlightedIndex;
  }
  
  private void showTripItemOnMap(List<HasLatLng> points) {
    int numResults = shownItems.size();
    List<TripItem> tripItems = singletonComponents.getTripItemService().getTripItems(trip.getKey());
    for (int i = 0, numItems = tripItems.size(); i < numItems; i++) {
      final TripItem item = tripItems.get(i);
      final HasLatLng point = mapPresenter.getLatLng(item.getLatitude(), item.getLongitude());
      final HasMarker marker = mapPresenter.addMarker(item.getLatitude(), item.getLongitude());
      mapPresenter.setMarkerImage(i + numResults, TrippyBundle.INSTANCE.bluePushPin().getURL());

      mapPresenter.setMarkerClickListener(marker, new MarkerClickListener() {
        @Override
        public void onMarkerClick(HasMarker marker) {
          infoWindow.setContent(item.getName());
          infoWindow.setPosition(point);
          infoWindow.setClickHandler(new EventCallback() {
            @Override
            public void callback() {
              singletonComponents.getEventBus().fireEvent(new ShowTripItemDetailsEvent(item));
            }
          });
          infoWindow.open(null, null);
        }
      });
      points.add(point);
    }
    if (points != null && !points.isEmpty()) {
      mapPresenter.setBounds(points);
    }
  }

  private int getSearchItemFromResult(String key) {
    int index = -1;
    for (int i = 0; i < searchResults.size(); i++) {
      if (searchResults.get(i).getId().equals(key)) {
        index = i;
        break;
      }
    }
    return index;
  }
  
  private void startGoogleSearch() {
    //TODO: remove new, if necessary.
    final SearchService searchService = new DefaultSearchService();
    final String searchText = searchService.extractKeyProperty(key, Page.SEARCH_QUERY);
    final HasLatLngBounds searchBounds =
      searchService.stringToBounds(searchService.extractKeyProperty(key, Page.SEARCH_BOUNDS));

    singletonComponents.getEventBus().fireEvent(
        new SearchQueryEvent(trip, tripDay,
        POIType.getGoogleQueryString(searchText), searchBounds,
        SearchType.GOOGLE));
  }
}
