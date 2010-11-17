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

import com.google.mobile.trippy.web.shared.models.TripItem;

/**
 * Event to signal that a trip item has been updated.
 * 
 */
public class TripItemUpdatedEvent extends TripItemEvent<TripItemUpdatedEventHandler> {

  protected static Type<TripItemUpdatedEventHandler> type;

  public TripItemUpdatedEvent(final TripItem tripItem) {
    super(tripItem);
  }

  public static Type<TripItemUpdatedEventHandler> getType() {
    return type != null ? type : (type = new Type<TripItemUpdatedEventHandler>());
  }

  @Override
  public Type<TripItemUpdatedEventHandler> getAssociatedType() {
    return getType();
  }

  @Override
  protected void dispatch(TripItemUpdatedEventHandler handler) {
    handler.onTripItemUpdated(this);
  }
}
