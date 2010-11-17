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

package com.google.mobile.trippy.web.client.db;

import com.google.common.base.Preconditions;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.maps.client.base.HasLatLngBounds;
import com.google.gwt.maps.client.base.LatLng;
import com.google.gwt.maps.client.base.LatLngBounds;
import com.google.gwt.search.client.AddressLookupMode;
import com.google.gwt.search.client.LocalResult;
import com.google.gwt.search.client.LocalSearch;
import com.google.gwt.search.client.ResultSetSize;
import com.google.gwt.search.client.Search.Cursor;
import com.google.gwt.search.client.SearchResultsHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.mobile.trippy.web.client.Page;
import com.google.mobile.trippy.web.client.service.LPSearchService;
import com.google.mobile.trippy.web.client.service.LPSearchServiceAsync;
import com.google.mobile.trippy.web.shared.models.POI;
import com.google.mobile.trippy.web.shared.models.POIDetail;
import com.google.mobile.trippy.web.shared.models.SearchItem;
import com.google.mobile.trippy.web.shared.models.SearchItem.SearchType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class for search network calls.
 * 
 */
public class DefaultSearchService implements SearchService {

  private final Map<String, List<SearchItem>> searchResultsCache =
      new HashMap<String, List<SearchItem>>();
  private final Map<String, SearchItem> searchItemCache = new HashMap<String, SearchItem>();
  private final LPSearchServiceAsync lPSearchService = GWT.create(LPSearchService.class);
  private final LocalSearch googleSearchService = new LocalSearch();

  @Override
  public void search(final SearchType type, final String queryString,
      final HasLatLngBounds searchBounds, final String tripLocation, final SearchResultsListener resultsListener) {
    // Verify that everything that is needed is available
    Preconditions.checkNotNull(queryString);
    Preconditions.checkNotNull(searchBounds);
    Preconditions.checkNotNull(resultsListener);
    Preconditions.checkNotNull(tripLocation);

    // Check in results cache
    String key = getResultsCacheKey(type, queryString, searchBounds);
    final List<SearchItem> cachedResults = searchResultsCache.get(key);
    if (cachedResults != null) {
      resultsListener.onSuccess(cachedResults);
      return;
    }

    // if not found in cache then do actual search
    if (type == SearchType.GOOGLE) {
      doGoogleSearch(queryString, searchBounds, tripLocation, resultsListener);
    } else if (type == SearchType.LP) {
      doLpSearch(queryString, searchBounds, resultsListener);
    }
  }

  @Override
  public void getSearchItem(final String itemKey, final String tripLocation,
      final SearchItemListener listener) {
    // Verify that everything that is needed is available
    Preconditions.checkNotNull(itemKey);
    Preconditions.checkNotNull(listener);

    // Check if the item is in item cache
    final SearchItem cachedItem = searchItemCache.get(itemKey);
    if (cachedItem != null) {
      listener.onSuccess(cachedItem);
      return;
    }

    // Item not found in cache, parse itemKey to retrieve the parameters
    // required to search the item
    long resultIndex = 0;
    String searchType = SearchType.LP.toString();
    String query = "";
    HasLatLngBounds searchBounds = null;

    String[] keyValuePairs = itemKey.split(":");

    for (String keyValueStr : keyValuePairs) {
      String[] keyValue = keyValueStr.split("=", 2);
      String key = keyValue[0];
      String value = keyValue[1];
      if (key.equals(Page.QUERY_TYPE)) {
        searchType = value;
      } else if (key.equals(Page.SEARCH_QUERY)) {
        query = value;
      } else if (key.equals(Page.SEARCH_BOUNDS)) {
        searchBounds = stringToBounds(value);
      } else if (key.equals(Page.SEARCH_ITEM_INDEX)) {
        resultIndex = Long.parseLong(value);
      }
    }

    if (searchType.equals(SearchType.GOOGLE.toString())) {
      getGoogleItemByIndex((int) resultIndex, query, searchBounds, tripLocation, listener);
    } else if (searchType.equals(SearchType.LP.toString())) {
      getLPItemByPoiId(resultIndex, query, searchBounds, listener);
    }
  }



  /**
   * Google search related methods
   */
  private void doGoogleSearch(final String queryString, final HasLatLngBounds searchBounds,
      final String tripLocation, final SearchResultsListener listener) {

    googleSearchService.clearResults();
    googleSearchService.setNoHtmlGeneration();
    googleSearchService.setResultSetSize(ResultSetSize.LARGE);
//    googleSearchService.setCenterPoint(searchBounds.getCenter().getJso());
    googleSearchService.setAddressLookupMode(AddressLookupMode.ENABLED);
    googleSearchService.setCenterPoint(tripLocation);

    final List<SearchItem> searchItemList = new ArrayList<SearchItem>();
    googleSearchService.addSearchResultsHandler(new SearchResultsHandler() {

      @SuppressWarnings("unchecked")
      @Override
      public void onSearchResults(SearchResultsEvent event) {
        JsArray<LocalResult> results = event.getResults().cast();        
        if (results == null || results.length() == 0) {
          searchItemList.clear();
          listener.onSuccess(searchItemList);
          return;
        }        
        addGoogleResultsToList(queryString, searchBounds, searchItemList,
            results);
        
        // for more than one page.
        final Cursor resultCursor = googleSearchService.getCursor();
        if (resultCursor != null) {
          int currentPage = resultCursor.getCurrentPageIndex(); 
          int totalPages = resultCursor.getPages().length();
          if (currentPage < totalPages - 1 && currentPage < MAX_GOOGLE_SEARCH_RESULTS_PAGE) {
            googleSearchService.gotoPage(++currentPage);
          } else {
            // Trippy-enforced page limit reached
            searchResultsCache.put(getResultsCacheKey(SearchType.GOOGLE, queryString, searchBounds),
                searchItemList);
            listener.onSuccess(searchItemList);
          }
        } else {
          // No more results if cursor is null.
          searchResultsCache.put(getResultsCacheKey(SearchType.GOOGLE, queryString, searchBounds),
              searchItemList);
          listener.onSuccess(searchItemList);
        }
      }
    });
    
    googleSearchService.execute(queryString);
  }

  /**
   * Add a results of one page to total results
   */
  private void addGoogleResultsToList(final String queryString, final HasLatLngBounds bounds,
      List<SearchItem> searchItemList, final JsArray<LocalResult> results) {

    final long sizeOffset = searchItemList.size();
    if (results != null && results.length() > 0) {
      int maxResultToDisplay = Math.min(results.length(), MAX_GOOGLE_SEARCH_RESULTS);
      for (int i = 0; i < maxResultToDisplay; ++i) {
        final LocalResult result = results.get(i);
        final SearchItem item = new SearchItem();
        item.setId(getItemCacheKey(SearchType.GOOGLE.toString(), queryString, bounds, sizeOffset
            + i));
        item.setName(result.getTitleNoFormatting());
        item.setAddress(result.getStreetAddress() + " " + result.getCity());

        final JsArray<LocalResult.PhoneNumber> phn = result.getPhoneNumbers();
        final ArrayList<String> phoneNumbers = new ArrayList<String>();
        if (phn != null) {
          for (int j = 0; j < phn.length(); j++) {
            phoneNumbers.add(phn.get(j).getNumber().toString());
          }
          item.setPhoneNumbers(phoneNumbers);
        }

        item.setLatitude(result.getLat());
        item.setLongitude(result.getLng());
        item.setUrl(getMobileUrl(result.getUrl()));
        item.setType(SearchType.GOOGLE);

        searchItemList.add(item);
      }
    }
  }

  /**
   * Get a google search result item
   * 
   * @param resultIndex - index of requested item in search results
   */
  private void getGoogleItemByIndex(final int resultIndex, final String query,
      final HasLatLngBounds bounds, final String tripLocation, final SearchItemListener listener) {

    // try to fetch the results this item was part of, then fetch the item from
    // those results
    final String resultsCacheKey = getResultsCacheKey(SearchType.GOOGLE, query, bounds);
    final List<SearchItem> cachedResults = searchResultsCache.get(resultsCacheKey);
    if (cachedResults != null && resultIndex < cachedResults.size()) {
      final SearchItem item = cachedResults.get(resultIndex);
      searchItemCache.put(
          getItemCacheKey(SearchType.GOOGLE.toString(), query, bounds, resultIndex), item);
      listener.onSuccess(item);
      return;
    }

    // Exhausted all caches, item not found, have to make a network request
    doGoogleSearch(query, bounds, tripLocation, new SearchResultsListener() {

      @Override
      public void onSuccess(final List<SearchItem> results) {
        if (resultIndex < results.size()) {
          final SearchItem item = results.get(resultIndex);
          searchItemCache.put(getItemCacheKey(SearchType.GOOGLE.toString(), query, bounds,
              resultIndex), item);
          listener.onSuccess(item);
        } else {
          listener.onFailure(new ArrayIndexOutOfBoundsException(
              "Could not find search item"));
        }
      }

      @Override
      public void onFailure(Throwable caught) {
        listener.onFailure(caught);
      }
    });
  }

  /**
   * Lonely planet search related methods
   */

  private void doLpSearch(final String poiType, final HasLatLngBounds searchBounds,
      final SearchResultsListener listener) {

    final List<SearchItem> searchItemList = new ArrayList<SearchItem>();

    lPSearchService.searchPOIByBoundingBox(searchBounds.getNorthEast().getLatitude(), searchBounds
        .getSouthWest().getLatitude(), searchBounds.getNorthEast().getLongitude(), searchBounds
        .getSouthWest().getLongitude(), poiType, new AsyncCallback<ArrayList<POI>>() {

      @Override
      public void onFailure(Throwable caught) {
        listener.onFailure(caught);
      }

      @Override
      public void onSuccess(final ArrayList<POI> results) {
        addLPResultsToList(poiType, searchBounds, searchItemList, results);
        searchResultsCache.put(getResultsCacheKey(SearchType.LP, poiType, searchBounds),
            searchItemList);
        listener.onSuccess(searchItemList);
      }
    });
  }

  /**
   * Convert the POIs to search items and populate the result list
   */
  private void addLPResultsToList(final String queryString, final HasLatLngBounds bounds,
      List<SearchItem> searchItemList, final List<POI> pois) {
    if (pois != null && pois.size() > 0) {
      for (final POI poi : pois) {
        final SearchItem item = new SearchItem();
        item.setId(getItemCacheKey(SearchType.LP.toString(), queryString, bounds, poi.getId()));
        item.setPoiId(poi.getId());
        item.setName(poi.getName());
        item.setLatitude(poi.getLatitude());
        item.setLongitude(poi.getLongitude());
        item.setType(SearchType.LP);
        searchItemList.add(item);
      }
    }
  }

  /**
   * Fetch details of a specific POI item
   * 
   * @param poiId - lonely planet id of item
   */
  private void getLPItemByPoiId(final long poiId, final String query, final HasLatLngBounds bounds,
      final SearchItemListener listener) {
    lPSearchService.getPOI(poiId, new AsyncCallback<POIDetail>() {
      @Override
      public void onSuccess(POIDetail result) {
        final SearchItem item = new SearchItem();
        final String key = getItemCacheKey(SearchType.LP.toString(), query, bounds, poiId);
        item.setId(key);
        item.setPoiId(result.getId());
        item.setName(result.getName());
        item.setAddress(result.getAddress());
        item.setPhoneNumbers(result.getPhones());
        item.setUrl(result.getSearchResultUrl());
        item.setLatitude(result.getLatitude());
        item.setLongitude(result.getLongitude());
        item.setType(SearchType.LP);
        item.setReview(result.getReview());
        searchItemCache.put(key, item);

        listener.onSuccess(item);
      }

      @Override
      public void onFailure(Throwable caught) {
        listener.onFailure(caught);
      }
    });
  }

  // Utility methods
  @Override
  public String getResultsCacheKey(final SearchType searchType, final String searchQuery,
      final HasLatLngBounds searchBounds) {
    return Page.QUERY_TYPE + "=" + searchType.toString() + ":" + Page.SEARCH_QUERY + "="
        + searchQuery + ":" + Page.SEARCH_BOUNDS + "=" + boundsToString(searchBounds);
  }

  @Override
  public String getItemCacheKey(final String type, final String query,
      final HasLatLngBounds bounds, final long resultId) {
    return getResultsCacheKey(type.equals(SearchType.GOOGLE.toString()) ? SearchType.GOOGLE
        : SearchType.LP, query, bounds)
        + ":" + Page.SEARCH_ITEM_INDEX + "=" + resultId;
  }

  @Override
  public HasLatLngBounds stringToBounds(final String boundsStr) {
    final String[] coordStr = boundsStr.split(",");
    return new LatLngBounds(new LatLng(Double.parseDouble(coordStr[0]), Double
        .parseDouble(coordStr[1])), new LatLng(Double.parseDouble(coordStr[2]), Double
        .parseDouble(coordStr[3])));
  }

  @Override
  public String boundsToString(final HasLatLngBounds bounds) {
    return bounds.getSouthWest().getLatitude() + "," + bounds.getSouthWest().getLongitude() + ","
        + bounds.getNorthEast().getLatitude() + "," + bounds.getNorthEast().getLongitude();
  }

  @Override
  public String extractKeyProperty(String key, String checkProperty) {
    String[] propertyValuePairs = key.split(":");

    for (String propertyValueStr : propertyValuePairs) {
      String[] propertyValue = propertyValueStr.split("=", 2);
      String property = propertyValue[0];
      String value = propertyValue[1];
      if (property.equals(checkProperty)) {
        return value;
      }
    }
    // checkProperty not found in key
    return null;
  }

  /**
   * To make more info url for mobile version.
   * 
   */
   private String getMobileUrl(String moreInfoUrl) {
     moreInfoUrl = moreInfoUrl.replaceFirst("maps", "m");
     return moreInfoUrl;
   }
}
