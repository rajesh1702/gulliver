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

import com.google.appengine.repackaged.com.google.common.base.Preconditions;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.mobile.trippy.web.client.service.LPSearchService;
import com.google.mobile.trippy.web.shared.exception.MissingAttributeException;
import com.google.mobile.trippy.web.shared.models.POI;
import com.google.mobile.trippy.web.shared.models.POIDetail;
import com.google.mobile.trippy.web.shared.models.Place;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;

/**
 */
@SuppressWarnings("serial")
public class LPCachedSearchServiceImpl extends RemoteServiceServlet implements
    LPSearchService {

  private static final Logger logger = Logger.getLogger(
      LPCachedSearchServiceImpl.class.getName());
  private final PlacesDbCacheManager placesDbCacheManager;
  private final POIDbCacheManager poiDbCacheManager;
  private final LPSearchServiceImpl searchService;
  private final DocumentBuilder bdfInst;
  @Inject
  public LPCachedSearchServiceImpl(DocumentBuilder bdfInst,
      PlacesDbCacheManager placesDbCacheManager, LPSearchServiceImpl searchService,
      POIDbCacheManager poiDbCacheManager) {
    this.bdfInst = bdfInst;
    this.placesDbCacheManager = placesDbCacheManager;
    this.searchService = searchService;
    this.poiDbCacheManager = poiDbCacheManager;
  }
  @Override
  public ArrayList<Place> getPlaces(String placeStr)
      throws MissingAttributeException, IOException {
    Preconditions.checkNotNull(placeStr, "Place must be provided.");
    Preconditions.checkArgument(!placeStr.isEmpty(),
        "Place must not be empty.");
    ArrayList<Place> places = new ArrayList<Place>();
    places = placesDbCacheManager.getPlaces(placeStr);
    if (places == null) {
      try {
        places = searchService.getPlaces(placeStr);
        placesDbCacheManager.putPlaces(placeStr, places);
      } catch (Exception e) {
        logger.warning(e.getMessage());
      }
    }
    return places;
  }

  @Override
  public ArrayList<POI> searchPOIByBoundingBox(double north, double south,
      double east, double west, String poiType)
      throws MissingAttributeException, IOException {
    Preconditions.checkNotNull(north, "North-latitude must be provided.");
    Preconditions.checkNotNull(south, "South-latitude must be provided.");
    Preconditions.checkNotNull(east, "East-longitude must be provided.");
    Preconditions.checkNotNull(west, "West-longitude must be provided.");
    Preconditions.checkNotNull(poiType, "Place of intrest type must be provided.");
    Preconditions.checkArgument(!poiType.isEmpty(), "Place of intrest type must not be empty.");
    ArrayList<POI> pois = new ArrayList<POI>();
    pois = poiDbCacheManager.getPOIByBoundingBox(north, south, east, west, poiType);
    if (pois == null) {
      try {
        pois = searchService.searchPOIByBoundingBox(north, south, east, west, poiType);
        poiDbCacheManager.putPOIByBoundingBox(north, south, east, west, poiType, pois);
      } catch (Exception e) {
        logger.warning(e.getMessage());
      }
    }
    return pois;
  }
  @Override
  public POIDetail getPOI(final long poiId) throws MissingAttributeException,
      IOException {
    Preconditions.checkNotNull(poiId, "Place of intrest id must be provided.");
    POIDetail poiDetail = poiDbCacheManager.getPOI(poiId);
    if (poiDetail == null) {
      try {
        poiDetail = searchService.getPOI(poiId);
        poiDbCacheManager.putPOI(poiId, poiDetail);
      } catch (Exception e) {
        logger.warning(e.getMessage());
      }
    }
    return poiDetail;
  }
  @Override
  public ArrayList<POI> searchPOI(long placeId, String poiType)
      throws MissingAttributeException, IOException {
    Preconditions.checkNotNull(placeId, "Place id must be provided.");
    Preconditions.checkNotNull(poiType, "Place of intrest type must be provided.");
    Preconditions.checkArgument(!poiType.isEmpty(), "Place of intrest type must not be empty.");
    ArrayList<POI> pois = new ArrayList<POI>();
    pois = poiDbCacheManager.getPOIList(placeId, poiType);
    if (pois == null) {
      try {
        pois = searchService.searchPOI(placeId, poiType);
        poiDbCacheManager.putPOIList(placeId, poiType, pois);
      } catch (Exception e) {
        logger.warning(e.getMessage());
      }
    }
    return pois;
  }
}

