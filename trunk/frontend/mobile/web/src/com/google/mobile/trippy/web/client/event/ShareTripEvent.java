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
 * Event to share a trip
 * 
 */
public class ShareTripEvent extends TripEvent<ShareTripEventHandler> {

  protected static Type<ShareTripEventHandler> type;

  public ShareTripEvent(final Trip trip) {
    this(trip, false);
  }
  
  public ShareTripEvent(final Trip trip, final boolean isHistoryEvent) {
    super(trip, isHistoryEvent);
  }
  
  public static Type<ShareTripEventHandler> getType() {
    return type != null ? type : (type = new Type<ShareTripEventHandler>());
  }

  @Override
  public Type<ShareTripEventHandler> getAssociatedType() {
    return getType();
  }

  @Override
  protected void dispatch(ShareTripEventHandler handler) {
    handler.onShareTrip(this);
  }
}
