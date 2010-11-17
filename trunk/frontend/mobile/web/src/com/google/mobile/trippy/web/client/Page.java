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

package com.google.mobile.trippy.web.client;

/**
 * Page Constants.
 * 
 */
public class Page {
  
  private Page() {
    throw new UnsupportedOperationException();
  }

  public static final String PAGE = "page";
  public static final String SUBPAGE = "sub";
  public static final String SEARCH_QUERY = "q";
  public static final String TRIP_ID = "t";
  public static final String TRIP_ITEM_ID = "ti";
  public static final String SEARCH_ITEM_ID = "si";  
  public static final String TRIP_DAY = "td";
  public static final String ROUTE_START_ITEM_ID = "rs";
  public static final String ROUTE_END_ITEM_ID = "re";
  public static final String QUERY_TYPE = "st";
  public static final String SEARCH_ITEM_INDEX = "sii";
  public static final String SEARCH_BOUNDS = "bounds";
  public static final String SEARCH_RESULTS_KEY = "resultsKey";
  
  public static final int PAGE_HOME = 1;
  public static final int PAGE_CREATE_TRIP = PAGE_HOME + 1;
  public static final int PAGE_TRIP_LIST = PAGE_CREATE_TRIP + 1;
  public static final int PAGE_TRIP_DETAILS = PAGE_TRIP_LIST + 1;
  public static final int PAGE_TRIP_MAP = PAGE_TRIP_DETAILS + 1;
  public static final int PAGE_TRIP_ITEM_DETAILS = PAGE_TRIP_MAP + 1;
  public static final int PAGE_SEARCH_RESULT_MAP = PAGE_TRIP_ITEM_DETAILS + 1;
  public static final int PAGE_SEARCH_RESULT_LIST = PAGE_SEARCH_RESULT_MAP + 1;
  public static final int PAGE_SEARCH_ITEM_DETAILS = PAGE_SEARCH_RESULT_LIST + 1;
  public static final int PAGE_TRIP_ITEM_COMMENTS = PAGE_SEARCH_ITEM_DETAILS + 1;
  public static final int PAGE_TRIP_SHARE = PAGE_TRIP_ITEM_COMMENTS + 1;
  public static final int PAGE_FILTERED_TRIP_LIST = PAGE_TRIP_SHARE + 1;
  public static final int TOTAL_PAGES = PAGE_FILTERED_TRIP_LIST;
}
