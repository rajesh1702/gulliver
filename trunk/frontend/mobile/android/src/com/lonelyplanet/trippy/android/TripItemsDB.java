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
package com.lonelyplanet.trippy.android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * DB of TripItems
 */
public class TripItemsDB {
  /**
   * ID for unassociated trip or tripid.
   */
  public static final String NULL_TRIP_ID = "";

  private static final String KEY_DATE = "date";
  private static final String KEY_NAME = "name";
  private static final String KEY_TRIP_ID = "tripId";
  private static final String KEY_TRIP_ITEM_ID = "_id";
  private static final String KEY_LOCATION = "location";
  private static final String KEY_LAT_LONG = "latLong";
  private static final String KEY_POSITION = "position";
  private static final String DATABASE_TABLE = "tripitems";
  private static final String[] VIEW_TRIP_ITEMS = new String[] {
    KEY_DATE, KEY_NAME, KEY_TRIP_ITEM_ID, KEY_LOCATION, KEY_LAT_LONG};
  public static final int VIEW_TRIP_ITEMS_INDEX_DATE = 0;
  public static final int VIEW_TRIP_ITEMS_INDEX_NAME = 1;
  public static final int VIEW_TRIP_ITEMS_INDEX_TRIP_ITEM_ID = 2;
  public static final int VIEW_TRIP_ITEMS_INDEX_LOCATION = 3;
  public static final int VIEW_TRIP_ITEMS_INDEX_LAT_LONG = 4;

  /**
   * Dates from Trippy web app are represented by "Day <integer>"
   */
  private static final int DATE_PREFIX_LENGTH = 4;

  private static String formatDate(String s) {
    int day = 0;
    if (s.length() > DATE_PREFIX_LENGTH) {
      try {
        day = Integer.parseInt(s.substring(DATE_PREFIX_LENGTH));
        if (day < 0) {
           day = 0;
        }
      } catch (NumberFormatException e) {
        // pass
      }
    }
    return String.format("%05d", day);
  }
  
  ////////////////////
  // Methods
  ///////////////////
  /**
   * Construct TripItems
   * @param context
   */
  public TripItemsDB(Context context) {
    this.context = context;

    listAdapter = new SimpleCursorAdapter(context,
        R.layout.trip_items,
        null,
        new String[] {KEY_NAME, KEY_DATE},
        new int[] {R.id.description, R.id.date_header });
    listAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
      public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
        if (VIEW_TRIP_ITEMS_INDEX_DATE == columnIndex) {
          int day = Integer.parseInt(cursor.getString(columnIndex));
          if (day != prevDay) {
            view.setVisibility(View.VISIBLE);
            prevDay = day;
            if (0 == day) {
              ((TextView) view).setText("Unscheduled");
            } else {
              ((TextView) view).setText("Day " + day);
            }
          } else {
            ((TextView) view).setText("");
            view.setVisibility(View.GONE);
          }
          return true;
        }
        return false;
      }
      private int prevDay = -1;
    });
  }


  /**
   * Open the Trippy tripItems database. If it cannot be opened, try to create a new
   * instance of the database. If it cannot be created, throw an exception to
   * signal the failure
   *
   * @return this (self reference, allowing this to be chained in an
   *         initialization call)
   * @throws SQLException if the database could be neither opened or created
   */
  public TripItemsDB open() throws SQLException {
    dbHelper = new TrippyDbHelper(context);
    db = dbHelper.getWritableDatabase();
    return this;
  }

  public void close() {
    dbHelper.close();
  }


  /**
   * Called on loading a trip, adding/editing trip item, responding to getTripItems
   * @param date
   * @param tripId
   * @param tripItemId
   * @param name
   * @param location
   * @param position position in the day for the trip item.
   */
  public synchronized void addTripItem(String date, String tripId, String tripItemId,
                                       String name, String location, String latLong, int position) {
    if (null == date ||
        null == tripId ||
        null == tripItemId ||
        null == name ||
        null == location ||
        null == latLong) {
      throw new IllegalArgumentException();
    }
    Log.d("Trippy", "Add " + date + " " + tripId + " " + tripItemId + " " + name +
          " " + location + " " + latLong + " " + position);
    ContentValues args = new ContentValues(4);
    args.put(KEY_DATE, formatDate(date));
    args.put(KEY_TRIP_ID, tripId);
    args.put(KEY_TRIP_ITEM_ID, tripItemId);
    args.put(KEY_NAME, name);
    args.put(KEY_LOCATION, location);
    // Just in case.
    if (latLong.equals("0,0")) {
      args.put(KEY_LAT_LONG, "");
    } else {
      args.put(KEY_LAT_LONG, latLong);
    }
    args.put(KEY_POSITION, position);
    if (0 == db.update(DATABASE_TABLE, args, KEY_TRIP_ITEM_ID + " = ?", new String[]{tripItemId})) {
      db.insert(DATABASE_TABLE, null, args);
    }

    isUpdated = true;
  }

  /**
   * Called on delete trip item.
   * @param tripItemId
   */
  public synchronized void remTripItem(String tripItemId) {
    if (null == tripItemId) {
      throw new IllegalArgumentException();
    }
    db.delete(DATABASE_TABLE, KEY_TRIP_ITEM_ID + " = ?", new String[]{tripItemId});
    isUpdated = true;
  }

  /**
   * Clear trip items
   */
  public synchronized void clear() {
    db.delete(DATABASE_TABLE, null, null);
    isUpdated = true;
  }

  /**
   * Get associated ListAdapter for trip items
   * @return ListAdapter for trip items
   */
  public ListAdapter getListAdapter() {
    return listAdapter;
  }

  /**
   * Update associated ListAdapter with the trip items for the given tripId
   * @param tripId
   */
  public synchronized void updateAdapter(String tripId) {
    if (tripId != null && (null == lastTripId || !lastTripId.equals(tripId))) {
      Cursor cursor = db.query(DATABASE_TABLE, VIEW_TRIP_ITEMS, KEY_TRIP_ID + " = ?",
          new String[]{tripId}, null, null, KEY_DATE + ", " + KEY_POSITION);
      if (null != cursor) {
        listAdapter.changeCursor(cursor);
      }
      lastTripId = tripId;
    } else if (isUpdated) {
      Cursor cursor = listAdapter.getCursor();
      cursor.requery();
    }
    isUpdated = false;
  }

  /////////////
  // Fields
  /////////////
  private String lastTripId = null;
  private boolean isUpdated = false;

  /**
   * Representation of a selection of TripItems for ListView
   */
  private final SimpleCursorAdapter listAdapter;
  private TrippyDbHelper dbHelper;
  private SQLiteDatabase db;
  private final Context context;
}
