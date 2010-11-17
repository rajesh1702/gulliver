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
 * Event to notify that a trip has been deleted
 * 
 *
 */
public class TripDeletedEvent  extends TripEvent<TripDeletedEventHandler> {

  protected static Type<TripDeletedEventHandler> type;
  
  public static Type<TripDeletedEventHandler> getType() {
    return type != null ? type : (type = new Type<TripDeletedEventHandler>());
  }

  public TripDeletedEvent(final Trip trip) {
    super(trip);
  }

  @Override
  public Type<TripDeletedEventHandler> getAssociatedType() {
    return getType();
  }

  @Override
  protected void dispatch(TripDeletedEventHandler handler) {
    handler.onTripDeleted(this);
  }
}
