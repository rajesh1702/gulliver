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

package com.google.mobile.trippy.web.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.mobile.trippy.web.shared.models.Trip;

/**
 * service for creating trips similar to one already existing 
 * for example: LP recommended trips with some pre-determined data.
 */
@RemoteServiceRelativePath("tripcopyservice")
public interface TripCopyService extends RemoteService {
  /**
   * copy trip from server and return new trip with same trip items as the 
   * copied one.
   */
  public Trip createLpTrip(String tripId);
}
