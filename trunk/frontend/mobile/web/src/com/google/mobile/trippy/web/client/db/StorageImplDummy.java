/*
 * Copyright 2009 Bart Guijt and others.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.mobile.trippy.web.client.db;

import com.google.code.gwt.storage.client.Storage;
import com.google.code.gwt.storage.client.StorageEventHandler;
import com.google.code.gwt.storage.client.impl.StorageImpl;


/**
 * Implements the HTML5 Storage interface.
 * 
 * Dummy for user.agent which are not HTML 5 complaint.
 * 
 */
public class StorageImplDummy extends StorageImpl{
  
  protected StorageImplDummy() {
  }

  /**
   * Returns a Local Storage.
   */
  @Override
  public native Storage getLocalStorage() /*-{
    return $wnd.TLS;
  }-*/;

  /**
   * Returns a Session Storage.
   */
  @Override
  public Storage getSessionStorage() {
    return getLocalStorage();
  }

  /**
   * Registers an event handler for StorageEvents.
   */
  @Override
  public void addStorageEventHandler(StorageEventHandler handler) {
    return;
  }

  /**
   * De-registers an event handler for StorageEvents.
   */
  @Override
  public void removeStorageEventHandler(StorageEventHandler handler) {
    return;
  }

  @Override
  public native int getLength(Storage storage) /*-{
    return storage.getLength();
  }-*/;

  @Override
  public native String key(Storage storage, int index) /*-{
    return storage.key(index);
  }-*/;

  @Override
  public native String getItem(Storage storage, String key) /*-{
    return storage.getItem(key);
  }-*/;

  @Override
  public native void setItem(Storage storage, String key, String data) /*-{
    storage.setItem(key, data);
  }-*/;

  @Override
  public native void removeItem(Storage storage, String key) /*-{
    storage.removeItem(key);
  }-*/;

  @Override
  public native void clear(Storage storage) /*-{
    storage.clear();
  }-*/;
}
