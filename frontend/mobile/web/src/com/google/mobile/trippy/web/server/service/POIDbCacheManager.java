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
import com.google.mobile.trippy.web.shared.models.POI;
import com.google.mobile.trippy.web.shared.models.POIDetail;
import com.google.mobile.trippy.web.shared.models.POIStringMap;

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
public class POIDbCacheManager {
  private Cache pois;
  public POIDbCacheManager() {
    try {
      CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
      pois = cacheFactory.createCache(Collections.emptyMap());
    } catch (CacheException e) {
      // ...
    }
  }
  public void putPOIByBoundingBox(double north, double south, double east,
      double west, String poiType, ArrayList<POI> pois) {
    String keyStr = north + "#" + south + "#" + east + "#" + west + "#" + poiType;
    this.pois.put(keyStr, pois);
    //TODO: Improve or change location of the code 
    // Serialize to a byte array
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream out;
    try {
      out = new ObjectOutputStream(bos);
      out.writeObject(pois);
      out.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    // Get the bytes of the serialized object
    byte[] buf = bos.toByteArray();
    Queue queue = QueueFactory.getQueue("DbCacheManager");
    queue.add(url("/trippy/DbCacheManager/pois/" +
        keyStr).payload(buf, "UTF-8"));
    
  }
  @SuppressWarnings("unchecked")
  public ArrayList<POI> getPOIByBoundingBox(double north, double south,
      double east, double west, String poiType) {
    ArrayList<POI>  poisList = null;
    String keyStr = north + "#" + south + "#" + east + "#" + west + "#" + poiType;
    poisList = (ArrayList<POI>) this.pois.get(keyStr);
    if (poisList == null) {
      //TODO: Improve or change location of the code 
      PersistenceManager pm = PMF.get().getPersistenceManager();
      Query query = pm.newQuery(POIStringMap.class);
      query.setFilter("poiKeyString == poiKeyStringParam");
      query.declareParameters("String poiKeyStringParam");
      try {
        List<POIStringMap> results = (List<POIStringMap>)
            query.execute(keyStr);
        if (results.iterator().hasNext()) {
          poisList = (ArrayList<POI>) results.get(0).getPOIs();
        }
      } finally {
        query.closeAll();
      }
    }
    return poisList;
  }
  @SuppressWarnings("unchecked")
  public ArrayList<POI> getPOIList(long placeId, String poiType) {
    ArrayList<POI>  poisList = null;
    String keyStr = placeId + "#" + poiType;
    poisList = (ArrayList<POI>) this.pois.get(keyStr);
    if (poisList == null) {
      //TODO: Improve or change location of the code 
      PersistenceManager pm = PMF.get().getPersistenceManager();
      Query query = pm.newQuery(POIStringMap.class);
      query.setFilter("placeStr == placeStrParam");
      query.declareParameters("String placeStrParam");
      try {
        List<POIStringMap> results = (List<POIStringMap>)
            query.execute(keyStr);
        if (results.iterator().hasNext()) {
          poisList = (ArrayList<POI>) results.get(0).getPOIs();
        }
      } finally {
        query.closeAll();
      }
    }
    return poisList;
  }
  public void putPOIList(long placeId, String poiType, ArrayList<POI> pois) {
    String keyStr = placeId + "#" + poiType;
    this.pois.put(keyStr, pois);
    //TODO: Improve or change location of the code 
    // Serialize to a byte array
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream out;
    try {
      out = new ObjectOutputStream(bos);
      out.writeObject(pois);
      out.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    // Get the bytes of the serialized object
    byte[] buf = bos.toByteArray();
    Queue queue = QueueFactory.getQueue("DbCacheManager");
    queue.add(url("/trippy/DbCacheManager/pois/" +
        keyStr).payload(buf, "UTF-8"));
  }
  @SuppressWarnings("unchecked")
  public POIDetail getPOI(long poiId) {
    POIDetail  poiDetail = null;
    poiDetail = (POIDetail) this.pois.get(poiId);
    if (poiDetail == null) {
      //TODO: Improve or change location of the code 
      PersistenceManager pm = PMF.get().getPersistenceManager();
      Query query = pm.newQuery(POIDetail.class);
      query.setFilter("id == idParam");
      query.declareParameters("long placeStrParam");
      try {
        List<POIDetail> results = (List<POIDetail>)
            query.execute(poiId);
        if (results.iterator().hasNext()) {
          poiDetail = (POIDetail) results.get(0);
        }
      } finally {
        query.closeAll();
      }
    }
    return poiDetail;
  }
  public void putPOI(long poiId, POIDetail poiDetail) {
    this.pois.put(poiId, poiDetail);
    //TODO: Improve or change location of the code 
    // Serialize to a byte array
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream out;
    try {
      out = new ObjectOutputStream(bos);
      out.writeObject(pois);
      out.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    // Get the bytes of the serialized object
    byte[] buf = bos.toByteArray();
    Queue queue = QueueFactory.getQueue("DbCacheManager");
    queue.add(url("/trippy/DbCacheManager/pois?poiId=" +
        poiId).payload(buf, "UTF-8"));
    
  }

}
