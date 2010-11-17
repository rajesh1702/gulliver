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
 * When user clicks on the search button then this event is fired and subsequently handled.
 * 
 *
 */
public class ShowHomePageEvent extends BaseEvent<ShowHomePageEventHandler> {

  protected static Type<ShowHomePageEventHandler> type;
  
  public ShowHomePageEvent() {
    this(false);
  }
  
  public ShowHomePageEvent(boolean isHistoryEvent) {
    super(isHistoryEvent);
  }

  public static Type<ShowHomePageEventHandler> getType() {
    return type != null ? type : (type = new Type<ShowHomePageEventHandler>());
  }

  @Override
  protected void dispatch(ShowHomePageEventHandler handler) {
    handler.showHomePage(this);
  }
  
  @Override
  public Type<ShowHomePageEventHandler> getAssociatedType() {
    return getType();
  }

}
