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

package com.google.mobile.trippy.web.server.service;

import com.google.appengine.api.users.User;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.mobile.trippy.web.client.service.TripCopyService;
import com.google.mobile.trippy.web.shared.SharedConstants;
import com.google.mobile.trippy.web.shared.models.Trip;

import java.util.HashMap;

/**
 * Service to copy trip service and its all item.
 */
public class TripCopyServiceImpl extends RemoteServiceServlet implements
    TripCopyService {
  
  private static final HashMap<String, String> lpDestinationVsIdMap = new HashMap<String, String>();  
  
  static {
    for (int i = 0; i < SharedConstants.TOP_DESTINATIONS.length; i++) {
      lpDestinationVsIdMap.put(SharedConstants.TOP_DESTINATIONS[i],
          SharedConstants.TOP_DESTINATIONS_IDS[i]);
    }
  }
  
  @SuppressWarnings("deprecation")
  private final TripUtils tripUtils = new TripUtils();
  
  @Override
  public Trip createLpTrip(String lpDestination) {
    final String tripId = lpDestinationVsIdMap.get(lpDestination);
    final Trip trip = createTrip(tripId);
    return trip;
  }
  
  public Trip createTrip(String tripId) {
    final User user = Utils.getCurrentUser();
    if (user == null) {
      return null;
    } else {
      return tripUtils.copyTrip(tripId, user.getEmail(), user.getNickname());
    }
  }
}
