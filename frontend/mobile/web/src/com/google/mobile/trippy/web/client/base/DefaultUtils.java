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

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.mobile.trippy.web.client.db.DefaultTripService;
import com.google.mobile.trippy.web.client.db.TripService;
import com.google.mobile.trippy.web.shared.SharedConstants;
import com.google.mobile.trippy.web.shared.UserUtils;
import com.google.mobile.trippy.web.shared.models.IdDayDateTuple;
import com.google.mobile.trippy.web.shared.models.Trip;
import com.google.mobile.trippy.web.shared.models.TripItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Class containing the utility methods required by the app
 * 
 */

public class DefaultUtils implements Utils {

  private ArrayList<ValueChangeHandler<Boolean>> handlers;
  private Boolean online = null;
//  private Location location = null;
  private Timer onlineCheckTimer;
//  private Timer locUpdateTimer;
  /**
   * Should be used only in read-only mode.
   */

  private static Utils instance = new DefaultUtils();

  private DefaultUtils() {

  }

  public static Utils getInstance() {
    return instance;
  }

  @Override
  public ArrayList<Trip> filteredTrips(String query, ArrayList<Trip> trips) {
    final ArrayList<Trip> filteredTripList = new ArrayList<Trip>();
    for (Trip trip : trips) {
      if (trip.getName().toLowerCase().contains(query.toLowerCase())) {
        filteredTripList.add(trip);
      }
    }
    return filteredTripList;
  }

  @Override
  public List<TripItem> sortTripItems(List<TripItem> tripItems) {
    Collections.sort(tripItems, new Comparator<TripItem>() {
      @Override
      public int compare(TripItem item0, TripItem item1) {
        final int startDay0 = item0.getStartDay();
        final int startDay1 = item1.getStartDay();
        int compare = startDay0 > startDay1 ? 1 : startDay0 == startDay1 ? 0 : -1;
        if (compare == 0) {
          compare = item0.getStartTime().compareTo(item1.getStartTime());
        }
        return compare;
      }
    });
    return tripItems;
  }

  @Override
  public List<Trip> sortTrips(List<Trip> trips) {
    Collections.sort(trips, new Comparator<Trip>() {
      @Override
      public int compare(Trip trip0, Trip trip1) {
        return trip0.getStartDate().compareTo(trip1.getStartDate());
      }
    });
    return trips;
  }

  @Override
  public native boolean isOnline() /*-{
    return $wnd.navigator.onLine;
  }-*/;

  @Override
  public void startCheckOnlineTimer() {
    if (onlineCheckTimer == null) {
      onlineCheckTimer = new Timer() {
        @Override
        public void run() {
          // Check online state

          if (online == null || online != isOnline()) {
            online = isOnline();
            ValueChangeEvent<Boolean> e = new ValueChangeEvent<Boolean>(online) {};
            for (ValueChangeHandler<Boolean> handler : handlers) {
              handler.onValueChange(e);
            }
          }
        }
      };
    }
    onlineCheckTimer.run();
    // Schedule the timer to run every 5 seconds.
    onlineCheckTimer.scheduleRepeating(5000);
  }

//  @Override
//  public void startLocationUpdateTimer() {
//    /*if (locUpdateTimer == null) {
//      locUpdateTimer = new Timer() {
//        @Override
//        public void run() {
//          // Check online state
//          final String providerName = Geolocation.getProviderName();
//          final String permName = GWT.getPermutationStrongName();
//          if (!Geolocation.isSupported()) {
//            return;
//          }
//          final Geolocation geo = Geolocation.getGeolocation();
//          if (geo == null) {
//            return;
//          }
//          geo.getCurrentPosition(new PositionCallback() {
//            public void onFailure(PositionError error) {
//            }
//
//            public void onSuccess(Position position) {
//              final Coordinates c = position.getCoords();
//              // Update current location
//              location = new Location();
//              location.setCoordinates(new LatLng(c.getLatitude(), c.getLongitude()));
//              location.setName("Your current location");
//            }
//          }, PositionOptions.getPositionOptions(false, 15000, 30000));
//        }
//
//      };
//    }
//    locUpdateTimer.run();
//    // Schedule the timer to run every 1 minute.
//    locUpd*/ateTimer.scheduleRepeating(60000);
//  }

  @Override
  public void addOnlineHandler(ValueChangeHandler<Boolean> handler) {
    if (handlers == null) {
      handlers = new ArrayList<ValueChangeHandler<Boolean>>();
    }
    handlers.add(handler);
  }

  /**
   * This method will cause the call to onModuleLoad
   */

  @Override
  public void reload() {
    Window.Location.reload();
  }

//  @Override
//  public Location getCurrentLocation() throws LocationNotFoundException {
//    if (location == null) {
//      throw new LocationNotFoundException();
//    }
//    return location;
//  }

  @Override
  public native String getUserEmail() /*-{
    var userEmail = $wnd.userEmail_;
    if (typeof(userEmail) === "undefined") {
    return null;
    }
    return userEmail;
  }-*/;

  @Override
  public native String getUserNickname() /*-{
    var userNickname = $wnd.userNickname_;
    if (typeof(userNickname) === "undefined") {
    return null;
    }
    return userNickname;
  }-*/;

  @Override
  public String getLoginUrl() {
    return getLoginUrl("/");
  }

  @Override
  public String getLoginUrl(String destination) {
    final String loginUrl = nativeGetLoginUrl();
    if (loginUrl == null) {
      return null;
    }
    return loginUrl.replace(UserUtils.AUTH_DESTINATION_URL_DUMMY, URL.encode(destination));
  }

  @Override
  public native String nativeGetLoginUrl() /*-{
    var loginUrl = $wnd.loginUrl_;
    if (typeof(loginUrl) === "undefined") {
    return null;
    }
    return loginUrl;
  }-*/;

  @Override
  public String getLogoutUrl() {
    return getLogoutUrl("/");
  }

  @Override
  public String getLogoutUrl(String destination) {
    final String logoutUrl = nativeGetLogoutUrl();
    if (logoutUrl == null) {
      return null;
    }
    return logoutUrl.replace(UserUtils.AUTH_DESTINATION_URL_DUMMY, URL.encode(destination));
  }

  @Override
  public native String nativeGetLogoutUrl() /*-{
    var logoutUrl = $wnd.logoutUrl_;
    if (typeof(logoutUrl) === "undefined") {
    return null;
    }
    return logoutUrl;
  }-*/;

  @Override
  public void redirect(String url) {
    Window.Location.replace(url);
  }
  
  @Override
  @SuppressWarnings("deprecation")
  public String getDisplayDate(final Date date) {
    final String month = convertNumToMonthName(date.getMonth());
    final String day = Integer.toString(date.getDate());
    return month + " " + day;
  }

  @Override
  @SuppressWarnings("deprecation")
  public Date getDateFromDisplayString(final String ds) {
    return new Date(Date.parse(ds));
  }

  @Override
  @SuppressWarnings("deprecation")
  public String getDisplayTime(Date date) {
    final String hours = date.getHours() == 12 ? "12" : "" + (date.getHours() % 12);
    final String minutes = (date.getMinutes() < 10 ? "0" : "") + date.getMinutes();
    final String ampm = (date.getHours() / 12) == 0 ? "AM" : "PM";
    return hours + ":" + minutes + " " + ampm;
  }

  @Override
  @SuppressWarnings("deprecation")
  public String getDateTimeString(Date date) {
    Date todayDate = new Date();
    if (todayDate.getDate() == date.getDate() && todayDate.getMonth() == date.getMonth()
        && todayDate.getYear() == date.getYear()) {
      return getDisplayTime(date);
    } else {
      return getDisplayDate(date);
    }
  }

  /**
   * find the month name from given index.
   */
  @Override
  public String convertNumToMonthName(int index) {
    return MONTHS[index];
  }

  @Override
  public Date addDaysToDate(Date date, int days) {
    if (date == null) {
      return null;
    }
    return new Date(date.getTime() + days * Constants.MILLIS_IN_DAY);
  }

  @Override
  public boolean isNearCurrentLocation(final double latitude, final double longitude) {
    //TODO: Implement Utils isNearCurrentLocation Method.
    return true;
  }

  @Override
  public String[] getTopDestination() {
    return SharedConstants.TOP_DESTINATIONS;
  }

  @Override
  public String convertNumToDayName(int index) {
    return SHORTWEEKDAYS[index];
  }

  @Override
  public String convertNumToDayFullName(int index) {
    return FULLWEEKDAYS[index];
  }
  
  @SuppressWarnings("deprecation")
  @Override
  public String getItineraryDate(Date date, int day) {
    String dayStr = "";
    
    if (day == Constants.UNSCHEDULED_DAY) {
      dayStr = Constants.UNSCHEDULED_STR;
    } else {
      Date startDate = new Date(date.getTime() - (day - 1) * Constants.MILLIS_IN_DAY);
      if (!startDate.equals(Constants.UNSCHEDULED_DATE)) {
        int year = Constants.BASE_YEAR + date.getYear();
        dayStr = "Day " + day + ": " + convertNumToDayName(date.getDay()) + ", "
        + convertNumToMonthName(date.getMonth()) + " " + date.getDate() + " " + year;
      } else {
        dayStr = "Day " + day;
      }
    }
    return dayStr;
  }

  @Override
  public native void addTripItem(String date, String tripId, String tripItemId,
      String name, String location, String latlng, int position) /*-{
    try {
      $wnd.ANDROID.addTripItem(date, tripId, tripItemId, name, location, latlng, position);
    } catch(e) {
      // No-op.
    }
  }-*/;

  @SuppressWarnings("deprecation")
  @Override
  public String getDateForAndroid(Date date) {
    final String year = "" + (1900 + date.getYear());
    final int monthI = date.getMonth() + 1;
    final String month = (monthI < 10 ? "0" : "") + monthI;
    final int dayI = date.getDate();
    final String day = (dayI < 10 ? "0" : "") + dayI;
    return "" + year + "-" + month + "-" + day + " " + getDisplayDate(date);
  }

  @Override
  public int getTripItemPosition(String tripId, String tripItemId, int day) {
    final TripService tripService = new DefaultTripService(null);
    final Trip trip = tripService.getTrip(tripId);
    return trip.getTripItemIds().getTuples().indexOf(new IdDayDateTuple(tripItemId, day, null));
  }

  @Override
  public native void clear() /*-{
    try {
      $wnd.ANDROID.clear();
    } catch(e) {
      // No-op.
    }
  }-*/;

  @Override
  public native void remTripItem(String tripItemId) /*-{
    try {
      $wnd.ANDROID.remTripItem(tripItemId);
    } catch(e) {
      // No-op.
    }
  }-*/;

  @Override
  public int convertMonthNameToNum(String findMonth) {
    for (int i = 0; i < MONTHS.length; ++i) {
      if (MONTHS[i].contains(findMonth)) {
         return i;
      }
    }
    return -1;
  }

  @SuppressWarnings("deprecation")
  @Override
  public String getTripItemDate(Date date) {
    return convertNumToDayName(date.getDay()) + ", " + date.getDate() + " "
        + convertNumToMonthName(date.getMonth()) + " " + (1900 + date.getYear());
  }

  @Override
  public native void scrollToTop() /*-{
    $wnd.scroll(0, 0);
  }-*/;
}
