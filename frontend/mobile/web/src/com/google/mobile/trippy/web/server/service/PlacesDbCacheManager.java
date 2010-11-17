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

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.url;

import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.mobile.trippy.web.shared.models.Place;
import com.google.mobile.trippy.web.shared.models.PlaceStringMap;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

/**
 */

public class PlacesDbCacheManager {
  private Cache places;
  public PlacesDbCacheManager() {
    try {
      CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
      places = cacheFactory.createCache(Collections.emptyMap());
    } catch (CacheException e) {
      // ...
    }
  }
  public void putPlaces(String placeStr, ArrayList<Place> places) {
    this.places.put(placeStr, places);
    //TODO: Improve or change location of the code 
    // Serialize to a byte array
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream out;
    try {
      out = new ObjectOutputStream(bos);
      out.writeObject(places);
      out.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    // Get the bytes of the serialized object
    byte[] buf = bos.toByteArray();
    Queue queue = QueueFactory.getQueue("DbCacheManager");
    queue.add(url("/trippy/DbCacheManager/places/" +
        placeStr).payload(buf, "UTF-8"));
  }

  @SuppressWarnings("unchecked")
  public ArrayList<Place> getPlaces(String placeStr){
    ArrayList<Place> placesList = null;
    placesList = (ArrayList<Place>) this.places.get(placeStr);
    if (placesList == null) {
      //TODO: Improve or change location of the code.
      PersistenceManager pm = PMF.get().getPersistenceManager();
      Query query = pm.newQuery(PlaceStringMap.class);
      query.setFilter("placeStr == placeStrParam");
      query.declareParameters("String placeStrParam");
      try {
        List<PlaceStringMap> results = (List<PlaceStringMap>)
            query.execute(placeStr);
        if (results.iterator().hasNext()) {
          placesList = (ArrayList<Place>) results.get(0).getPlaces();
        }
      } finally {
        query.closeAll();
      }
    }
    return placesList;
  }
}
