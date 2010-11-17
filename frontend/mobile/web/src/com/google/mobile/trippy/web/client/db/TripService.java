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

import java.util.ArrayList;


/**
 * Add, edit, delete and sync trips.
 * 
 */
public interface TripService {

  public void addTrip(final Trip trip, final AsyncCallback<Trip> callback);

  /**
   * Update trip at remote and local db.
   * 
   * @throws AuthorizationException
   */
  public void updateTrip(final Trip trip, final AsyncCallback<Trip> callback)
      throws AuthorizationException;
  
  /**
   * Update trip's trip items tuple at remote and local db.
   * 
   * @throws AuthorizationException
   */
  public void updateTripItemsTuple(final Trip trip, final AsyncCallback<Trip> callback) 
      throws AuthorizationException ;

  /**
   * Delete trip from remote and local db.
   * 
   * @throws AuthorizationException
   */
  public void deleteTrip(final Trip trip, final AsyncCallback<Void> callback)
      throws AuthorizationException;

  /**
   * Sync trips from remote --> local db.
   */
  public void syncFromRemoteDb(final AsyncCallback<Void> callback);

  /**
   * Get trips from main index.
   */
  public ArrayList<Trip> getTrips();

  /**
   * Get trip by id from local db.
   */
  public Trip getTrip(String tripId);

  /**
   * Set trip to update or not.
   */
  public void setTripUpdated(final String tripId, final boolean updated);

  public void addTripToLocalDb(Trip trip);
}
