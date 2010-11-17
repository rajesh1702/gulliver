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
 * Event to show search actions.
 * 
 *
 */
public class SearchClickEvent extends BaseEvent<SearchClickEventHandler> {

  protected static Type<SearchClickEventHandler> type;

  public SearchClickEvent() {
    this(false);
  }
  
  public SearchClickEvent(boolean isHistoryEvent) {
    super(isHistoryEvent);
  }

  public static Type<SearchClickEventHandler> getType() {
    return type != null ? type : (type = new Type<SearchClickEventHandler>());
  }

  @Override
  protected void dispatch(SearchClickEventHandler handler) {
    handler.onSearch(this);
  }
  
  @Override
  public Type<SearchClickEventHandler> getAssociatedType() {
    return getType();
  }

}
