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

import android.os.Handler;

/**
 * Class that is injected into the JavaScript namespace and allows advanced features.
 * Runs in separate thread from main TrippyActivity thread.
 */
public class AndroidProxy {
  public AndroidProxy(TrippyActivity trippyActivity, Handler handler, TripItemsDB tripItems,
      boolean canNavigate, boolean canTakePicture) {
    this.trippyActivity = trippyActivity;
    this.handler = handler;
    this.tripItems = tripItems;
    this.canNavigate = canNavigate;
    this.canTakePicture = canTakePicture;
  }

  ///////////////////////////////////////////////
  // Android -> Webapp
  ///////////////////////////////////////////////

  /**
   * Returns whether this device has navigation capability.
   * Always true/false for a session
   * @return true if this device has navigation capability.
   */
  public boolean canNavigate() {
    return canNavigate;
  }

  /**
   * Returns whether this device has camera capability.
   * Always true/false for a session
   * @return true if this device has camera capability.
   */
  public boolean canTakePicture() {
    return canTakePicture;
  }

  /**
   * Returns whether photos are associated with given trip item.
   * @param tripItemId Id of trip item.
   * @return true if photos are associated with given trip item.
   */
  public boolean hasPictures(String tripItemId) {
    // TODO: Implement for Trippy 2.0
    return false;
  }

  ///////////////////////////////////////////////
  // Webapp -> Android
  ///////////////////////////////////////////////
  /**
   * Launch GPS Navigation app.
   * Triggered from Navigate icon in Trippy web application.
   * @param name Name of destination.
   * @param address Address to navigate to.
   * @param latLong Latitude and longitude to navigate to.
   */
  public void doNavigate(final String name, final String address, final String latLong) {
    handler.post(new Runnable() {
      public void run() {
        trippyActivity.navigate(name, address, latLong);
      }
    });
  }

  /**
   * Take a picture and associate with a Trip Item.
   * Triggered from Camera icon in Trippy web application.
   * @param tripItemId Id of trip item to associate photos to.
   */
  public void doTakePicture(String tripItemId) {
    // TODO: Implement for Trippy 2.0
  }
  /**
   * View pictures, starting with giben Trip Item.
   * Triggered from Photo icon in Trippy web application.
   * @param tripItemId
   */
  public void doViewGallery(String tripItemId) {
    // TODO: Implement for Trippy 2.0
  }

  ///////////////////////////////////////////////
  // Android access to trip items for Gallery.
  ///////////////////////////////////////////////
  /**
   * Called on loading a trip, adding/editing trip item, responding to getTripItems.
   * Items for a trip are ordered by date and position.
   * @param date e.g. "Day <integer>".  0 for unscheduled day.
   * @param tripId
   * @param tripItemId
   * @param name
   * @param location
   * @param position position in the day for the trip item.
   */
  @Deprecated
  public void addTripItem(String date, String tripId, String tripItemId, String name,
                          String location, int position) {
    tripItems.addTripItem(date, tripId, tripItemId, name, location, "", position);
  }

  /**
   * Called on loading a trip, adding/editing trip item, responding to getTripItems.
   * Items for a trip are ordered by date and position.
   * @param date e.g. "Day <integer>".  0 for unscheduled day.
   * @param tripId
   * @param tripItemId
   * @param name
   * @param location e.g. "1020 North Rengstorff Avenue"
   * @param latLong e.g. "37.420072,-122.095973"
   * @param position position in the day for the trip item.
   */
  public void addTripItem(String date, String tripId, String tripItemId, String name,
                          String location, String latLong, int position) {
    tripItems.addTripItem(date, tripId, tripItemId, name, location, latLong, position);
  }

  /**
   * Called on finish loading trip items, responding to getTripItems
   */
  public void setFinishLoad() {
  }

  /**
   * Called on delete trip item.
   * @param tripItemId
   */
  public void remTripItem(String tripItemId) {
    tripItems.remTripItem(tripItemId);
  }

  /**
   * Clear trip items
   */
  public void clear() {
    tripItems.clear();
  }

  /////////////
  // Fields
  /////////////
  private boolean canNavigate;
  private boolean canTakePicture;

  // All operations on trippyActivity must be wrapped in handler
  // for thread safety.
  private Handler handler;
  private TrippyActivity trippyActivity;
  private TripItemsDB tripItems;
}
