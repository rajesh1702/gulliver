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
import android.net.Uri;
import android.os.Parcelable;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;


/**
 * 
 */
public class PhotosDB {
  public static final String KEY_TRIP_ID = "tripId";
  public static final String KEY_TRIP_ITEM_ID = "tripItemId";
  public static final String KEY_URI = "uri";
  public static final String KEY_FILE = "path";
  
  private static final String DATABASE_TABLE = "photos";
  
  public PhotosDB(Context context) {
    this.context = context;
  }
  
  /**
   * Open the Trippy photos database. If it cannot be opened, try to create a new
   * instance of the database. If it cannot be created, throw an exception to
   * signal the failure
   * 
   * @return this (self reference, allowing this to be chained in an
   *         initialization call)
   * @throws SQLException if the database could be neither opened or created
   */
  public PhotosDB open() throws SQLException {
    dbHelper = new TrippyDbHelper(context);
    db = dbHelper.getWritableDatabase();
    return this;
  }
  
  public void close() {
    dbHelper.close();
  }
  
  public void addPhotos(String tripId, String tripItemId, Uri uri, File file) {
    ContentValues args = new ContentValues(4);
    args.put(KEY_TRIP_ID, tripId);
    args.put(KEY_TRIP_ITEM_ID, tripItemId);
    args.put(KEY_URI, uri.toString());
    args.put(KEY_FILE, file.getAbsolutePath());
    
    db.insert(DATABASE_TABLE, null, args);
  }
  
  public ArrayList<Parcelable> getPhotoUris(String tripId) {
    ArrayList<Parcelable> list = new ArrayList<Parcelable>();
    Cursor cursor = db.query(DATABASE_TABLE, new String[] {KEY_URI, KEY_FILE}, 
        KEY_TRIP_ID + "= ?", new String[]{tripId}, null, null, null, null);
    if (null != cursor) {
      for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
        if (new File(cursor.getString(1)).canRead()) {
          Uri uri = Uri.parse(cursor.getString(0));
          Log.d("PHOTOS", uri.toString());
          list.add(uri);
        }
      }
      cursor.close();
    }
    return list;
  }

  public boolean hasPhotos(String tripId) {
    Cursor cursor = db.query(DATABASE_TABLE, new String[] {KEY_URI}, 
        KEY_TRIP_ID + "= ?", new String[]{tripId}, null, null, null);
    boolean ret = false;
    if (null != cursor) {
      ret = !cursor.isAfterLast();
      cursor.close();
    }
    
    return ret; 
  }
  
  private TrippyDbHelper dbHelper;
  private SQLiteDatabase db;
  private final Context context;
}
