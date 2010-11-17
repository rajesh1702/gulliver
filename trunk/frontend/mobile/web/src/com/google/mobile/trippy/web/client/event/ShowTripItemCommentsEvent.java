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
 * When user clicks on the show comments for trip item button then event is
 * fired and subsequently handled.
 * 
 */
public class ShowTripItemCommentsEvent  extends TripItemEvent<ShowTripItemCommentsEventHandler> {

  protected static Type<ShowTripItemCommentsEventHandler> type;
  
  public static Type<ShowTripItemCommentsEventHandler> getType() {
    return type != null ? type : (type = new Type<ShowTripItemCommentsEventHandler>());
  }

  public ShowTripItemCommentsEvent(final TripItem tripItem) {
    this(tripItem, false);
  }

  public ShowTripItemCommentsEvent(final TripItem tripItem, boolean isHistoryEvent) {
    super(tripItem, isHistoryEvent);
  }

  @Override
  public Type<ShowTripItemCommentsEventHandler> getAssociatedType() {
    return getType();
  }

  @Override
  protected void dispatch(ShowTripItemCommentsEventHandler handler) {
    handler.onShowTripItemComments(this);
  }
}
