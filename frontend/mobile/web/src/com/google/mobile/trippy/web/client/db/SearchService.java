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

import com.google.gwt.maps.client.base.HasLatLngBounds;
import com.google.mobile.trippy.web.shared.models.SearchItem;
import com.google.mobile.trippy.web.shared.models.SearchItem.SearchType;

import java.util.List;

/**
 * Class returns Search results for Google and LP search query string.
 * 
 */
public interface SearchService {
  public static final int MAX_GOOGLE_SEARCH_RESULTS_PAGE = 5;
  public static final int MAX_GOOGLE_SEARCH_RESULTS = 32;
  
  /**
   * Listener for query search
   */
  public interface SearchResultsListener {
    public void onSuccess(List<SearchItem> results);
    
    public void onFailure(Throwable caught);
  }
  
  /**
   * Listener for item search
   */
  public interface SearchItemListener {
    public void onSuccess(SearchItem searchItem);
    
    public void onFailure(Throwable caught);
  }
  
  /**
   * Search using Google or LP
   */
  void search(SearchType type, String queryString, HasLatLngBounds searchBounds,
      String tripName, SearchResultsListener resultsListener);

  /**
   * Search a specific item
   */
  void getSearchItem(String itemIdStr, String tripName, SearchItemListener itemlistener);
  
  /**
   * Convert bounds to string
   */
  String boundsToString(HasLatLngBounds bounds);
  
  /**
   * Convert String generated through boundsToString() back to HasLatLngBounds
   */
  HasLatLngBounds stringToBounds(String strBounds);
  
  /**
   * 
   */
  String extractKeyProperty(String key, String property);

  String getResultsCacheKey(SearchType searchType, String searchQuery, HasLatLngBounds searchBounds);

  String getItemCacheKey(String type, String query, HasLatLngBounds bounds, long resultId);
}
