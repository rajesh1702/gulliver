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

package com.google.mobile.trippy.web.server.service.task;

import com.google.inject.Inject;
import com.google.mobile.trippy.web.server.service.PMF;
import com.google.mobile.trippy.web.server.service.POIDbCacheManager;
import com.google.mobile.trippy.web.shared.models.POI;
import com.google.mobile.trippy.web.shared.models.POIStringMap;

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
public class POIDbCacheManagerServlet extends HttpServlet {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private final POIDbCacheManager poiDbCacheManager;
  @Inject
  public POIDbCacheManagerServlet(POIDbCacheManager poiDbCacheManager){
    this.poiDbCacheManager = poiDbCacheManager;
  }
  @SuppressWarnings("unchecked")
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
  throws IOException {
    final Logger logger = Logger.getLogger(
        POIDbCacheManagerServlet.class.getName());
    ArrayList<POI> places = null;
    //TODO: Do we need this additional object?
    ArrayList<POI> placesFromDb = new ArrayList<POI>();
    //String placeStr = req.getParameter("placeStr");
    //TODO: Replace constant with code to get the place name from URL.
    String keyStr = "1#test";
      String keyStrArray[] = keyStr.split("#");
    places = poiDbCacheManager.getPOIList(Long.parseLong(keyStrArray[0]), keyStrArray[0]);
    if (places == null) {
      InputStream is = req.getInputStream();
      ObjectInputStream inStream = new ObjectInputStream(is);
      try {
        places = (ArrayList<POI>) inStream.readObject();
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
    }
    //TODO: Improve or change location of the code
    PersistenceManager pm = PMF.get().getPersistenceManager();
    try {
      placesFromDb.addAll(pm.makePersistentAll(places));
      POIStringMap poiStringMap = new POIStringMap(keyStr,
          placesFromDb);
      pm.makePersistent(poiStringMap);
    } catch (Exception e) {
      logger.warning(e.getMessage());
      e.printStackTrace();
    }
    resp.setStatus(200);
  }
}
