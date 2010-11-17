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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.common.base.Preconditions;
import com.google.mobile.trippy.web.shared.DefaultUserUtils;
import com.google.mobile.trippy.web.shared.UserUtils;
import com.google.mobile.trippy.web.shared.exception.AuthorizationException;
import com.google.mobile.trippy.web.shared.exception.TransactionFailedException;
import com.google.mobile.trippy.web.shared.models.IdDayDateTuple;
import com.google.mobile.trippy.web.shared.models.IdDayDateTupleList;
import com.google.mobile.trippy.web.shared.models.Status;
import com.google.mobile.trippy.web.shared.models.Trip;
import com.google.mobile.trippy.web.shared.models.TripItem;
import com.google.mobile.trippy.web.shared.models.TripItemUpdateResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

/**
 * 
 * 
 */
public class TripItemUtils {

  private UserUtils userUtils = DefaultUserUtils.getInstance();

  public TripItemUpdateResult addTripItem(final TripItem tripItem, final String userEmail)
      throws AuthorizationException, TransactionFailedException {
    Preconditions.checkArgument(!tripItem.getTripId().isEmpty(), "Trip id must be set.");
    final Date currTime = new Date();
    final PersistenceManager pm = PMF.get().getPersistenceManager();
    // Fetch parent trip.
    final Trip trip = pm.detachCopy(pm.getObjectById(Trip.class, tripItem.getTripId()));
    // Authorize user against trip.
    if (!userUtils.isContributor(trip, userEmail)) {
      throw new AuthorizationException(userEmail);
    }

    if (tripItem.getKey() == null) {
      // Set key with parent as trip.
      final Key tripItemKey = generateTripItemKey(trip.getKey());
      tripItem.setKey(KeyFactory.keyToString(tripItemKey));
    }
    // Set default values.
    tripItem.setOwnerId(userEmail);
    tripItem.setAddedOn(currTime);
    tripItem.setLastModified(currTime);
    tripItem.setLastModifiedBy(userEmail);
//    tripItem.setStatus(Status.ACTIVE);

    // Add trip item to ordered list in trip.
    final IdDayDateTuple idDayDateTuple = new IdDayDateTuple(tripItem.getKey(), 
        tripItem.getStartDay(), currTime);
    final IdDayDateTupleList idDayDateTupleList = trip.getTripItemIds();
    idDayDateTupleList.getTuples().add(idDayDateTuple);
    trip.setTripItemIds(idDayDateTupleList);
    // Update trip meta-data.
    trip.setVersion(trip.getVersion() + 1);
    trip.setLastModified(currTime);
    trip.setLastModifiedBy(userEmail);

    // Save trip and trip item in a transaction.
    Trip newTrip = null;
    TripItem newTripItem = null;
    final Transaction tx = pm.currentTransaction();
    try {
      tx.begin();
      newTrip = pm.makePersistent(trip);
      newTripItem = pm.makePersistent(tripItem);
      tx.commit();
    } finally {
      if (tx.isActive()) {
        tx.rollback();
        throw new TransactionFailedException();
      }
    }
    final TripItemUpdateResult tripItemUpdateResult =
        new TripItemUpdateResult(pm.detachCopy(newTrip), pm.detachCopy(newTripItem));
    return tripItemUpdateResult;
  }

  public TripItemUpdateResult updateTripItem(final TripItem tripItem, final String userEmail)
      throws AuthorizationException, TransactionFailedException {
    Preconditions.checkArgument(!tripItem.getTripId().isEmpty(), "Trip id must be set.");
    final Date currTime = new Date();
    final PersistenceManager pm = PMF.get().getPersistenceManager();
    // Fetch parent trip.
    final Trip trip = pm.detachCopy(pm.getObjectById(Trip.class, tripItem.getTripId()));
    // Authorize user against trip.
    if (!userUtils.isContributor(trip, userEmail)) {
      throw new AuthorizationException(userEmail);
    }

    // Update trip item meta-data.
    tripItem.setLastModified(currTime);
    tripItem.setLastModifiedBy(userEmail);

    // Update trip item in ordered list in trip.
    final IdDayDateTuple idDayDateTuple = new IdDayDateTuple(tripItem.getKey(), 
        tripItem.getStartDay(), currTime);
    final IdDayDateTupleList idDayDateTupleList = trip.getTripItemIds();
    final int index = idDayDateTupleList.getTuples().indexOf(idDayDateTuple);
    if (index >= 0) {
      idDayDateTupleList.getTuples().set(index, idDayDateTuple);
    } else {
      idDayDateTupleList.getTuples().add(idDayDateTuple);
    }
    trip.setTripItemIds(idDayDateTupleList);
    // Update trip meta-data.
    trip.setVersion(trip.getVersion() + 1);
    trip.setLastModified(currTime);
    trip.setLastModifiedBy(userEmail);

    // Save trip and trip item in transaction.
    final Transaction tx = pm.currentTransaction();
    Trip newTrip = null;
    TripItem newTripItem = null;
    try {
      tx.begin();
      newTrip = pm.makePersistent(trip);
      newTripItem = pm.makePersistent(tripItem);
      tx.commit();
    } finally {
      if (tx.isActive()) {
        tx.rollback();
        throw new TransactionFailedException();
      }
    }
    final TripItemUpdateResult tripItemUpdateResult =
        new TripItemUpdateResult(pm.detachCopy(newTrip), pm.detachCopy(newTripItem));
    return tripItemUpdateResult;
  }

  @SuppressWarnings("unchecked")
  public ArrayList<TripItem> fetchTripItems(final ArrayList<String> ids) {
    if (ids == null || ids.isEmpty()) {
      return new ArrayList<TripItem>();
    }
    final PersistenceManager pm = PMF.get().getPersistenceManager();
    final ArrayList<Key> keys = new ArrayList<Key>();
    for (String id : ids) {
      final Key k = KeyFactory.stringToKey(id);
      keys.add(k);
    }
    Query q = pm.newQuery("select from " + TripItem.class.getName() + " where key == :keys");
    ArrayList<TripItem> items =
        (ArrayList<TripItem>) pm.detachCopyAll((List<TripItem>) q.execute(keys));
    return items;
  }

  public TripItemUpdateResult deleteTripItem(final TripItem tripItem, final String userEmail)
      throws AuthorizationException, TransactionFailedException {
    final Date currTime = new Date();
    final PersistenceManager pm = PMF.get().getPersistenceManager();
    final Trip trip = pm.detachCopy(pm.getObjectById(Trip.class, tripItem.getTripId()));
    if (!userUtils.isContributor(trip, userEmail)) {
      throw new AuthorizationException(userEmail);
    }

    tripItem.setStatus(Status.DELETED);
    tripItem.setLastModified(currTime);
    tripItem.setLastModifiedBy(userEmail);

    final IdDayDateTuple idDayDateTuple = new IdDayDateTuple(tripItem.getKey(), 
        tripItem.getStartDay(), currTime);
    final IdDayDateTupleList idDayDateTupleList = trip.getTripItemIds();
    idDayDateTupleList.getTuples().remove(idDayDateTuple);
    trip.setTripItemIds(idDayDateTupleList);
    
    trip.setVersion(trip.getVersion());
    trip.setLastModified(currTime);
    trip.setLastModifiedBy(userEmail);

    // Save trip and trip item in transaction.
    final Transaction tx = pm.currentTransaction();
    final TripItem toDelete =
        pm.getObjectById(TripItem.class, KeyFactory.stringToKey(tripItem.getKey()));
    Trip newTrip = null;
    try {
      tx.begin();
      newTrip = pm.makePersistent(trip);
      pm.deletePersistent(toDelete);
      tx.commit();
    } catch (Throwable e) {
      if (tx.isActive()) {
        tx.rollback();
        e.printStackTrace();
        throw new TransactionFailedException(e);
      }
    }
    final TripItemUpdateResult tripItemUpdateResult =
        new TripItemUpdateResult(pm.detachCopy(newTrip), tripItem);
    return tripItemUpdateResult;
  }

  public Key generateTripItemKey(final String parentKeyS) {
    final Key parentKey = KeyFactory.stringToKey(parentKeyS);
    final DatastoreService service = DatastoreServiceFactory.getDatastoreService();
    return service.allocateIds(parentKey, TripItem.class.getSimpleName(), 1).getStart();
  }
  
  /*
   * Add all Trip Items to trip with given trip Id. 
   */
  public void addTripItems(final ArrayList<TripItem> tripItems, final String userEmail, String tripId)
      throws AuthorizationException, TransactionFailedException {
    final Date currTime = new Date();
    final PersistenceManager pm = PMF.get().getPersistenceManager();
    // Fetch parent trip.
    final Trip trip = pm.detachCopy(pm.getObjectById(Trip.class, tripId));
    // Authorize user against trip.
    if (!userUtils.isContributor(trip, userEmail)) {
      throw new AuthorizationException(userEmail);
    }
    final IdDayDateTupleList idDateTupleList = trip.getTripItemIds();
    
    for (TripItem tripItem : tripItems) {
      Preconditions.checkArgument(!tripItem.getTripId().isEmpty(), "Trip id must be set.");
      //TODO: move strings to message file.
      Preconditions.checkArgument(tripItem.getTripId().equals(tripId), 
          "Trip Item have different trip Id.");
      
      // Set key with parent as trip.
      final Key tripItemKey = generateTripItemKey(trip.getKey());
      tripItem.setKey(KeyFactory.keyToString(tripItemKey));
      // Set default values.
      tripItem.setOwnerId(userEmail);
      tripItem.setAddedOn(currTime);
      tripItem.setLastModified(currTime);
      tripItem.setLastModifiedBy(userEmail);
      tripItem.setStatus(Status.ACTIVE);
      
      // Add trip item to ordered list in trip.
      final IdDayDateTuple idDateTuple = new IdDayDateTuple(tripItem.getKey(), 
          tripItem.getStartDay(), currTime);
      idDateTupleList.getTuples().add(idDateTuple);
    }
    
    trip.setTripItemIds(idDateTupleList);
    // Update trip meta-data.
    trip.setLastModified(currTime);
    trip.setLastModifiedBy(userEmail);
    
    // Save trip and trip item in a transaction.
    Trip newTrip = null;
    ArrayList<TripItem> newTripItems = null;
    final Transaction tx = pm.currentTransaction();
    try {
      tx.begin();
      newTrip = pm.makePersistent(trip);
      newTripItems = (ArrayList<TripItem>) pm.makePersistentAll(tripItems);
      tx.commit();
    } finally {
      if (tx.isActive()) {
        tx.rollback();
        throw new TransactionFailedException();
      }
    }
    pm.detachCopy(newTrip);
    pm.detachCopyAll(newTripItems);
  }
}
