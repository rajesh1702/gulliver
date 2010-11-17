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

package com.google.mobile.trippy.web.shared;

import java.util.Date;

/**
 */
public class SharedConstants {
  //TODO: place name of trips to copy.
  public static final String TOP_DESTINATIONS[] = {};
  //TODO: place datastore keys of trips to copy.
  public static final String TOP_DESTINATIONS_IDS[] = {};
  
  public static final int UNSCHEDULED_DAY = 0;
  
  @SuppressWarnings("deprecation")
  public static final Date UNSCHEDULED_DATE = new Date(Date.UTC(0, 0, 1, 0, 0, 0));
  
 /**
  * Private constructor to prevent instantiation.
  */
  private SharedConstants() {
  }
}
