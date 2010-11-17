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

package com.google.mobile.trippy.web.client.base;

import java.util.Date;

/**
 * Constant values
 * 
 *
 */
public class Constants {
  public static final long MILLIS_IN_DAY = 1000 * 60 * 60 * 24;

  public static final int NO_SELECTED_DAY = -1;
  public static final int UNSCHEDULED_DAY = 0;
  public static final int BASE_YEAR = 1900;
  public static final int DEFAULT_TRIP_DURATION = 7;
  public static final int NAVIGATION_WIDTH = 100;
  public static final int OPTIONS_PANEL_WIDTH = 140;

  public static final Date UNSCHEDULED_DATE = new Date(Date.UTC(0, 0, 1, 0, 0, 0));

  public static final String INVITE = "Invite Others";
  public static final String UNSCHEDULED_STR = "Unscheduled";
  
  public static final String HOME_STR = "Home";
  public static final String List_STR = "List";
  public static final String LOGOUT_STR = "Log Out";
  public static final String TRIP_STR = "trip";
  public static final String TRIP_ITEM_STR = "trip item";
  public static final String SHARED_TRIP_STR = "shared trip";
  public static final String SHARED_TRIP_ITEM_STR = "shared trip item";
  public static final String RESCHEDULE_STR = "Reschedule";
  public static final String EDIT_STR = "Edit";
  public static final String DELETE_STR = "Delete";
  public static final String SHOW_ON_MAP_STR = "Show On Map";
  public static final String SHOW_DETAILS_STR = "Show Details";
  public static final String TRIP_LIST_MENU_STR = "Trip List Menu";
  public static final String TRIP_MENU_STR = "Trip Menu";
  public static final String TRIP_ITEM_MENU_STR = "Trip Item Menu";
  public static final String SHOW_IN_LIST_STR = "Show In List";
  public static final String ITEM_STR = "1 item ";
  public static final String ITEMS_STR = "items";
  public static final String ALL_TRIPS_STR = "All Trips";
  public static final String CURRENT_TRIPS_STR = "Current Trips";
  public static final String UPCOMING_TRIPS_STR = "Upcoming Trips";
  public static final String PAST_TRIPS_STR = "Past Trips";
  public static final String DAY_LABEL_STR = "Day";

  public static final String ITINERARY_STR = "Itinerary";
  public static final String MY_TRIPS_STR = "My Trips";
  public static final String TRIPS_STR = "Trips";
  public static final String TRIP_HEADER_STR = "Trip";
  public static final String MAP_STR = "Map";
  public static final String DETAILS_STR = "Details";
  public static final String NEW_TRIP_STR = "New Trip";
  public static final String SHARE_STR = "Share ";
  public static final String LIKE_STR = "Like";
  public static final String DISLIKE_STR = "Dislike";
  public static final String COMMENT_STR = "Comment";
  public static final String COMMENTS_STR = "Comments";
  public static final String COMMENT_LOWER_CASE_STR = "comment";
  public static final String ADD_COMMENT_STR = "Add a Comment";
  public static final String REVIEW_STR = "Review";
  public static final String MAP_LINK_GOOGLE = "http://maps.google.com/maps";
  public static final String MAP_LINK_LP = "http://m.lonelyplanet.com/";
  public static final String[] SEARCH_CATEGORY = new String[] { "Museums", "Zoos",
      "Parks", "Shopping", "Malls", "Grocery", "Drug stores", "Hotels", "5-star hotels",
      "4-star hotels", "3-star hotels", "Bed and breakfast", "Entertainment", "Performing arts", 
      "Bars", "Restaurants", "Chinese restaurants", "Italian restaurants", "Mexican restaurants",
      "Brunch", "Cafe", "Fast Food", "Activities", "Spas", "Gyms", "General", "banks", "medical",
      "church", "Banks", "Medical", "Church"};
  public static final String ACTIVITIES_SEARCH_REPLACEMENT = "fun | tours | golf  | cruise | gym";
  public static final String GENERAL_SEARCH_REPLACEMENT = "banks | medical | church | center";
  private Constants() {
    throw new UnsupportedOperationException();
  }
}
