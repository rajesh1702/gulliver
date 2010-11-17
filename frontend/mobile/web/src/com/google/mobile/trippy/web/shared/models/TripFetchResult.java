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

package com.google.mobile.trippy.web.shared.models;

import com.google.mobile.trippy.web.client.service.RemoteTripService;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * POJO to be returned for {@link RemoteTripService.fetchUpdatedTrips}.
 * 
 */
@SuppressWarnings("serial")
public class TripFetchResult implements Serializable {

  private ArrayList<Trip> trips;
  
  private String cursor;

  public ArrayList<Trip> getTrips() {
    return trips;
  }

  public String getCursor() {
    return cursor;
  }

  public void setTrips(ArrayList<Trip> trips) {
    this.trips = trips;
  }

  public void setCursor(String cursor) {
    this.cursor = cursor;
  }
  
}
