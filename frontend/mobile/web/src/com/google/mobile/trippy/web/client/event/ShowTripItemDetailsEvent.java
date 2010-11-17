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
 * Show details of trip item
 * 
 */
public class ShowTripItemDetailsEvent  extends TripItemEvent<ShowTripItemDetailsEventHandler> {

  protected static Type<ShowTripItemDetailsEventHandler> type;
  
  public static Type<ShowTripItemDetailsEventHandler> getType() {
    return type != null ? type : (type = new Type<ShowTripItemDetailsEventHandler>());
  }

  public ShowTripItemDetailsEvent(final TripItem tripItem) {
    this(tripItem, false);
  }

  public ShowTripItemDetailsEvent(final TripItem tripItem, boolean isHistoryEvent) {
    super(tripItem, isHistoryEvent);
  }
  
  @Override
  public Type<ShowTripItemDetailsEventHandler> getAssociatedType() {
    return getType();
  }

  @Override
  protected void dispatch(ShowTripItemDetailsEventHandler handler) {
    handler.onShowTripItemDetails(this);
  }
}
