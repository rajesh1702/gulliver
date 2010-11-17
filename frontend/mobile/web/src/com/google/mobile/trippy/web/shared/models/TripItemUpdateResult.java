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

import java.io.Serializable;

/**
 * 
 * 
 */
@SuppressWarnings("serial")
public class TripItemUpdateResult implements Serializable {

  private Trip trip;
  
  private TripItem tripItem;
  
  public TripItemUpdateResult() {
    super();
  }
  
  public TripItemUpdateResult(final Trip trip, final TripItem tripItem) {
    this.trip = trip;
    this.tripItem = tripItem;
  }

  public Trip getTrip() {
    return trip;
  }

  public TripItem getTripItem() {
    return tripItem;
  }

  public void setTrip(Trip trip) {
    this.trip = trip;
  }

  public void setTripItem(TripItem tripItem) {
    this.tripItem = tripItem;
  }
  
}
