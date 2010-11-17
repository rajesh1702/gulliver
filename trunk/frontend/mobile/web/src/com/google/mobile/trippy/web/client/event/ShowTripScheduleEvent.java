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
package com.google.mobile.trippy.web.client.event;

import com.google.mobile.trippy.web.shared.models.Trip;

/**
 * Event to open the trip schedule page
 * 
 */
public class ShowTripScheduleEvent extends TripEvent<ShowTripScheduleEventHandler> {

  protected static Type<ShowTripScheduleEventHandler> type;
  private boolean isNewTrip;

  public ShowTripScheduleEvent(Trip trip) {
    super(trip);
  }

  public ShowTripScheduleEvent(Trip trip, boolean isNewTrip) {
    this(trip);
    this.isNewTrip = isNewTrip;
  }

  public static Type<ShowTripScheduleEventHandler> getType() {
    return type != null ? type : (type = new Type<ShowTripScheduleEventHandler>());
  }

  @Override
  public Type<ShowTripScheduleEventHandler> getAssociatedType() {
    return getType();
  }

  public boolean isNewTrip() {
    return this.isNewTrip;
  }

  @Override
  protected void dispatch(ShowTripScheduleEventHandler handler) {
    handler.onShowTripSchedule(this);
  }
}
