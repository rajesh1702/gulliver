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

import com.google.mobile.trippy.web.shared.models.Comment;
import com.google.mobile.trippy.web.shared.models.Trip;
import com.google.mobile.trippy.web.shared.models.TripItem;

import java.util.Set;

/**
 * Utility class to authorize users against a trip.
 * 
 */
public class DefaultUserUtils implements UserUtils{

  private static UserUtils instance = new DefaultUserUtils();
  private DefaultUserUtils() {
    
  }
  public static UserUtils getInstance() {
    return instance;
  }

  public boolean isContributor(Trip trip, String userEmail) {
    final Set<String> contributors = trip.getContributorIds();
    if (contributors != null && !contributors.isEmpty()) {
      return contributors.contains(userEmail);
    }
    return false;
  }

  public boolean isViewer(Trip trip, String userEmail) {
    final Set<String> viewers = trip.getViewerIds();
    if (viewers != null && !viewers.isEmpty()) {
      return viewers.contains(userEmail) || viewers.contains(VIEWER_ALL);
    }
    return false;
  }

  public boolean isOwner(Trip trip, String userEmail) {
    final String owner = trip.getOwnerId();
    return owner != null && owner.equals(userEmail);
  }

  public boolean isOwner(Trip trip, TripItem tripItem, String userEmail) {
    return isOwner(trip, userEmail) || tripItem.getOwnerId().equals(userEmail);
  }

  public boolean isOwner(Trip trip, Comment comment, String userEmail) {
    return isOwner(trip, userEmail) || comment.getOwnerId().equals(userEmail);
  }
}
