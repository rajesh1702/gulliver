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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.mobile.trippy.web.shared.exception.AuthorizationException;
import com.google.mobile.trippy.web.shared.models.Trip;
import com.google.mobile.trippy.web.shared.models.TripItem;

import java.util.ArrayList;

/**
 * Add, edit, delete and sync trip items.
 * 
 */
public interface TripItemService {

  /**
   * Add trip item to the remote and local db.
   * 
   * If writes to remote db are successful, then the trip item is pushed to main
   * index of local db. Else the trip item is push to pending index for later
   * retries.
   */
  public void addTripItem(
      final TripItem tripItem, final AsyncCallback<TripItem> callback) 
        throws AuthorizationException;

  /**
   * Update trip item in the remote and local db.
   * 
   */
  public void updateTripItem(
      final TripItem tripItem, final AsyncCallback<TripItem> callback) 
        throws AuthorizationException;

  /**
   * Delete trip item to remote and local db.
   * 
   * @throws AuthorizationException
   */
  public void deleteTripItem(final TripItem tripItem, final AsyncCallback<Void> callback)
      throws AuthorizationException;

  public void setTripItemUpdated(final String tripItemId, final boolean updated);

  /**
   * Get trip items from main index.
   */
  public ArrayList<TripItem> getTripItems(final String tripId);

  /**
   * Get trip item by id.
   */
  public TripItem getTripItem(final String tripItemId);

  public void addTripItemTolocalDb(TripItem tripItem, Trip trip);

  void sync(Trip trip);
}
