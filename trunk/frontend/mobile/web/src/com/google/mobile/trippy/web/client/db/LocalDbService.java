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

package com.google.mobile.trippy.web.client.db;

import com.google.code.gwt.storage.client.Storage;
import com.google.mobile.trippy.web.client.base.DefaultUtils;
import com.google.mobile.trippy.web.client.base.Utils;

/**
 * Service to persist key-value pair to local db.
 * 
 */
public class LocalDbService {
  
  private final static Utils utils = DefaultUtils.getInstance();

  private LocalDbService() {
    throw new UnsupportedOperationException();
  }
  
  /**
   * Get value for the key from local db.
   */
  public static String getPersistent(String key) {
    Storage local = Storage.getLocalStorage();
    return local.getItem(key);
  }

  /**
   * Save (key, value) pair to the local db.
   */
  public static void makePersistent(String key, String value) {
    Storage local = Storage.getLocalStorage();
    local.setItem(key , value);
  }

  /**
   * Get value for the key from local db.
   */
  public static void deletePersistent(String key) {
    Storage local = Storage.getLocalStorage();
    local.removeItem(key);
  }
  
  public static void clearDb() {
    Storage local = Storage.getLocalStorage();
    local.clear();
    utils.clear();
  }

}
