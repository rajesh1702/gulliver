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

/**
 * Interface for the utility class to authorize users against a trip.
 * 
 */
public interface UserUtils {
  public static  final String VIEWER_ALL = "all";
  public static final String AUTH_DESTINATION_URL_DUMMY = "DESTINATION";
  
  public boolean isContributor(Trip trip, String userEmail);
  public boolean isViewer(Trip trip, String userEmail);
  public boolean isOwner(Trip trip, String userEmail);
  public boolean isOwner(Trip trip, TripItem tripItem, String userEmail);
  public boolean isOwner(Trip trip, Comment comment, String userEmail);
}
