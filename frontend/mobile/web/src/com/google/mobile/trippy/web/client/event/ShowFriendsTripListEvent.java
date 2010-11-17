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

/**
 * Event to show "friends" trip list
 * 
 *
 */
public class ShowFriendsTripListEvent extends BaseEvent<ShowFriendsTripListEventHanlder> {

  protected static Type<ShowFriendsTripListEventHanlder> type;

  public ShowFriendsTripListEvent() {
    this(false);
  }
  
  public ShowFriendsTripListEvent(boolean isHistoryEvent) {
    super(isHistoryEvent);
  }

  public static Type<ShowFriendsTripListEventHanlder> getType() {
    return type != null ? type : (type = new Type<ShowFriendsTripListEventHanlder>());
  }

  @Override
  public Type<ShowFriendsTripListEventHanlder> getAssociatedType() {
    return getType();
  }

  @Override
  protected void dispatch(ShowFriendsTripListEventHanlder handler) {
    handler.onShowFriendsTripList(this);
  }

}
