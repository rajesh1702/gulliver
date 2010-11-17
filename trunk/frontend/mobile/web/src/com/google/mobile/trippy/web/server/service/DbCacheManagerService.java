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

import com.google.inject.Inject;
import com.google.mobile.trippy.web.shared.models.Place;
import com.google.mobile.trippy.web.shared.models.PlaceStringMap;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 */
public class DbCacheManagerService extends HttpServlet {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private final PlacesDbCacheManager placesDbCacheManager;
  @Inject
  public DbCacheManagerService(PlacesDbCacheManager placesDbCacheManager){
    this.placesDbCacheManager = placesDbCacheManager;
  }
  @SuppressWarnings("unchecked")
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
  throws IOException {
    final Logger logger = Logger.getLogger(
        DbCacheManagerService.class.getName());
    ArrayList<Place> places = null;
    //TODO: Do we need this additional object?
    ArrayList<Place> placesFromDb = new ArrayList<Place>();
    //String placeStr = req.getParameter("placeStr");
    //TODO: Replace constant with code to get the place name from URL.
    String placeStr = "place";
    places = placesDbCacheManager.getPlaces(placeStr);
    if (places == null) {
      InputStream is = req.getInputStream();
      ObjectInputStream inStream = new ObjectInputStream(is);
      try {
        places = (ArrayList<Place>) inStream.readObject();
      } catch (ClassNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    //TODO: Improve or change location of the code
    PersistenceManager pm = PMF.get().getPersistenceManager();
    try {
      placesFromDb.addAll(pm.makePersistentAll(places));
      PlaceStringMap placeStringMap = new PlaceStringMap(placeStr,
          placesFromDb);
      pm.makePersistent(placeStringMap);
    } catch(Exception e) {
      logger.warning(e.getMessage());
      e.printStackTrace();
    }
    resp.setStatus(200);
  }
}
