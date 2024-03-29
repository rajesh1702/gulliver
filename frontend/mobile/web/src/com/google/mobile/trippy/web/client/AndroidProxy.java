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

package com.google.mobile.trippy.web.client;

import com.google.gwt.event.shared.HandlerManager;
import com.google.mobile.trippy.web.client.event.SearchClickEvent;

/**
 * Methods exposed to Android shell app.
 * 
 */
public class AndroidProxy {
  
  private final HandlerManager eventBus;
  
  public AndroidProxy(final HandlerManager eventBus) {
    this.eventBus = eventBus;
  }
  
  public native void setDoSearch(AndroidProxy proxy) /*-{
    $wnd.doSearch = function() {
      proxy.@com.google.mobile.trippy.web.client.AndroidProxy::doSearch()();
    }
  }-*/;

  private void doSearch() {
    eventBus.fireEvent(new SearchClickEvent());
  }
}
