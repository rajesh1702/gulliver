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

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.mobile.trippy.web.shared.models.Trip;
import com.google.mobile.trippy.web.shared.models.TripItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class containing the utility methods required by the app
 * 
 */

public interface Utils {

  public static final String MONTHS[] =
      {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

  public static final String SHORTWEEKDAYS[] = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

  public static final String FULLWEEKDAYS[] =
      {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

  /**
   * find the month name from given index.
   */

  // page related methods
  /**
   * This method will cause the call to onModuleLoad
   */
  public void reload();

  public void redirect(String url);

  // public String createTokenHash(final Map<String, String[]> listParamMap);

  // App state related methods
  public boolean isOnline();

  public void startCheckOnlineTimer();

//  public void startLocationUpdateTimer();

  public void addOnlineHandler(ValueChangeHandler<Boolean> handler);

//  public Location getCurrentLocation() throws LocationNotFoundException;


  // Current User related methods
  public String getUserEmail();

  public String getUserNickname();

  public String getLoginUrl();

  public String getLoginUrl(String destination);

  public String nativeGetLoginUrl();

  public String getLogoutUrl();

  public String getLogoutUrl(String destination);

  public String nativeGetLogoutUrl();

  // date time related utility methods
  /**
   * @param date
   * @return "MONTH DATE". Eg. "April 22".
   */
  public String getDisplayDate(final Date date);

  public Date getDateFromDisplayString(final String ds);

  public String getDisplayTime(Date date);

  public String getDateTimeString(Date date);

  public String convertNumToMonthName(int i);

  public int convertMonthNameToNum(String month);

  public String convertNumToDayName(int i);

  public String convertNumToDayFullName(int i);

  public Date addDaysToDate(Date date, int days);

  // trip related methods
  public ArrayList<Trip> filteredTrips(String query, ArrayList<Trip> trips);

  public List<TripItem> sortTripItems(List<TripItem> tripItems);

  public List<Trip> sortTrips(List<Trip> trips);

  public boolean isNearCurrentLocation(final double latitude, final double longitude);

  public String[] getTopDestination();

  public String getItineraryDate(Date date, int day);

  public String getTripItemDate(Date date);
  // Native app integration.
  /**
   * Called on loading a trip, adding/editing trip item, responding to getTripItems
   * @param date "yyyy-MM-dd HH.mm.ss" This should be the same in any timezone.
   * @param tripItem
   * @param tripItemId
   * @param name
   * @param location
   */
  public void addTripItem(String date, String tripId, String tripItemId,
      String name, String location, String latlng, int position);
  
  /**
   * Date in a format required for Android app.
   * 
   * @param date
   * @return "YYYY-MM-DD <whatever printed for web app>"
   */
  public String getDateForAndroid(Date date);
  
  /**
   * Trip item's position in trip item list.
   * @param day 
   */
  public int getTripItemPosition(String tripId, String tripItemId, int day);
  
  /**
   * Called on delete trip item.
   * @param tripItemId
   */
  public void remTripItem(String tripItemId);
  
  /**
   * Clear trip Items
   */
  public void clear();

  public void scrollToTop();
}
