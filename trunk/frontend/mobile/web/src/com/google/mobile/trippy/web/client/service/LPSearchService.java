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
import com.google.mobile.trippy.web.shared.exception.MissingAttributeException;
import com.google.mobile.trippy.web.shared.models.POI;
import com.google.mobile.trippy.web.shared.models.POIDetail;
import com.google.mobile.trippy.web.shared.models.Place;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Search service client for Lonely Planet.
 */
@RemoteServiceRelativePath("lpsearch")
public interface LPSearchService extends RemoteService {
  ArrayList<POI> searchPOI(long placeId, String poiType) 
      throws MissingAttributeException, IOException;
  ArrayList<Place> getPlaces(String place) throws MissingAttributeException, IOException;
  POIDetail getPOI(long id) throws MissingAttributeException, IOException;
  ArrayList<POI> searchPOIByBoundingBox(double north, double south,
      double east, double west, String poiType)
      throws MissingAttributeException, IOException;
}
