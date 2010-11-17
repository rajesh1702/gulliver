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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Database Formatter for Trippy
 */
public class TrippyDbHelper extends SQLiteOpenHelper {
  private static final String DATABASE_NAME = "data";
  private static final int DATABASE_VERSION = 4;
  TrippyDbHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
      db.execSQL("create table photos (tripId text, "
          + "tripItemId text, uri text not null, path text not null);");
      db.execSQL("create table tripitems (date text, name text," +
      "tripId text, _id text, location text, position integer, latLong text default \"\");");
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    if (1 == oldVersion) {
      db.execSQL("create table tripitems (date text, name text," +
      "tripId text, _id text, location text, position integer);");
    }
    if (3 == oldVersion) {
      db.execSQL("alter table tripitems add column latLong text default \"\";");
    }
  }
}
