/*
 * Copyright 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.mobile.trippy.web.server.service;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.common.base.Preconditions;
import com.google.mobile.trippy.web.shared.DefaultUserUtils;
import com.google.mobile.trippy.web.shared.SharedConstants;
import com.google.mobile.trippy.web.shared.UserUtils;
import com.google.mobile.trippy.web.shared.Utils;
import com.google.mobile.trippy.web.shared.exception.AuthorizationException;
import com.google.mobile.trippy.web.shared.exception.TransactionFailedException;
import com.google.mobile.trippy.web.shared.exception.TripNotFoundException;
import com.google.mobile.trippy.web.shared.exception.TripVersionException;
import com.google.mobile.trippy.web.shared.models.IdDateTupleList;
import com.google.mobile.trippy.web.shared.models.IdDayDateTuple;
import com.google.mobile.trippy.web.shared.models.IdDayDateTupleList;
import com.google.mobile.trippy.web.shared.models.Status;
import com.google.mobile.trippy.web.shared.models.Trip;
import com.google.mobile.trippy.web.shared.models.TripFetchResult;
import com.google.mobile.trippy.web.shared.models.TripItem;

import org.datanucleus.store.appengine.query.JDOCursorHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

/**
 * Trip utilities.
 *
 */
public class TripUtils {

  private final UserUtils userUtils = DefaultUserUtils.getInstance();
  private final TripItemUtils tripItemUtils = new TripItemUtils();
  
  @SuppressWarnings("deprecation")
  public Trip addTrip(Trip trip, String userEmail) {
    final Date startDate = trip.getStartDate();
    if (startDate.getTimezoneOffset() != 0) {
      trip.setStartDate(new Date(Date.UTC(startDate.getYear(),
          startDate.getMonth(),
          startDate.getDate(),
          startDate.getHours(),
          startDate.getMinutes(),
          startDate.getSeconds())));
    }
    final Date currTime = new Date();
    trip.setOwnerId(userEmail);
    trip.addContributorId(userEmail);
    trip.addViewerId(userEmail);
    trip.setAddedOn(currTime);
    trip.setLastModified(currTime);
    trip.setLastModifiedBy(userEmail);
    // trip.setStatus(Status.ACTIVE);
    PersistenceManager pm = PMF.get().getPersistenceManager();
    trip = pm.makePersistent(trip);
    trip = pm.detachCopy(trip);
    return trip;
  }

  @SuppressWarnings("deprecation")
  public Trip updateTrip(Trip updatedTrip, String userEmail) throws AuthorizationException {
    Preconditions.checkArgument(!updatedTrip.getKey().isEmpty(), "Trip id must be set.");
    final Trip trip = getTrip(updatedTrip.getKey());
    Preconditions.checkArgument(trip != null, "Invalid trip id.");
    if (!userUtils.isContributor(trip, userEmail)) {
      throw new AuthorizationException(userEmail);
    }
    Date startDate = updatedTrip.getStartDate();
    if (startDate.getTimezoneOffset() != 0) {
      trip.setStartDate(new Date(Date.UTC(startDate.getYear(),
          startDate.getMonth(),
          startDate.getDate(),
          startDate.getHours(),
          startDate.getMinutes(),
          startDate.getSeconds())));
    } else {
      trip.setStartDate(startDate);
    }

    trip.setName(updatedTrip.getName());
    trip.setDuration(updatedTrip.getDuration());
    trip.setThumbsUp(updatedTrip.getThumbsUp());
    trip.setThumbsDown(updatedTrip.getThumbsDown());
    trip.setDescription(updatedTrip.getDescription());
    // trip.setTripItemIds(updatedTrip.getTripItemIds());
    trip.setLastModified(new Date());
    trip.setLastModifiedBy(userEmail);

    final PersistenceManager pm = PMF.get().getPersistenceManager();
    final Trip pTrip = pm.detachCopy(pm.makePersistent(trip));
    return pTrip;
  }

  @SuppressWarnings("unchecked")
  public TripFetchResult fetchUpdatedTrips(
      final Date from, final String userEmail, final String cursorS/* Nullable */,
      final int limit) {
    final PersistenceManager pm = PMF.get().getPersistenceManager();
    final Query query = pm.newQuery(Trip.class);
    query.setFilter("lastModified >= :lastMod && viewerIds == :userEmail");
    query.setOrdering("lastModified ASC");
    query.setRange(0, limit);

    if (cursorS != null && !cursorS.isEmpty()) {
      final Cursor cursor = Cursor.fromWebSafeString(cursorS);
      final Map<String, Object> extensionMap = new HashMap<String, Object>();
      extensionMap.put(JDOCursorHelper.CURSOR_EXTENSION, cursor);
      query.setExtensions(extensionMap);
    }

    final List<Trip> tripL = (List<Trip>) query.execute(from, Utils.getEncodedBase64(userEmail));
    final Cursor cursor = JDOCursorHelper.getCursor(tripL);
    final ArrayList<Trip> trips = (ArrayList<Trip>) pm.detachCopyAll(tripL);
    final TripFetchResult result = new TripFetchResult();
    result.setTrips(trips);
    result.setCursor(cursor.toWebSafeString());
    return result;
  }

  public void deleteTrip(final Trip trip, final String userEmail) throws AuthorizationException {
    if (!userUtils.isOwner(trip, userEmail)) {
      throw new AuthorizationException(userEmail);
    }
    trip.setStatus(Status.DELETED);
    trip.setLastModified(new Date());
    trip.setLastModifiedBy(userEmail);
    PersistenceManager pm = PMF.get().getPersistenceManager();
    pm.makePersistent(trip);
    // TODO delete trip items using tast queue.
  }

  public Trip getTrip(String id) {
    final PersistenceManager pm = PMF.get().getPersistenceManager();
    final Key key = KeyFactory.stringToKey(id);
    final Trip trip = pm.getObjectById(Trip.class, key);
    return pm.detachCopy(trip);
  }

  public Trip updateTripItemsTuple(final String tripKey, final Long clientTripVersion,
      final IdDayDateTupleList itemsByDay, final String userEmail)
      throws AuthorizationException, TripNotFoundException, TripVersionException {
    try {
      Preconditions.checkArgument(tripKey != null && !tripKey.isEmpty(), "Invalid trip key.");
    } catch (IllegalArgumentException iae) {
      throw new TripNotFoundException(tripKey);
    }

    final Trip serverTrip = getTrip(tripKey);
    if (serverTrip == null) {
      throw new TripNotFoundException(tripKey);
    }
    if (!userUtils.isContributor(serverTrip, userEmail)) {
      throw new AuthorizationException(userEmail);
    }

    if (serverTrip.getVersion() > clientTripVersion) {
      throw new TripVersionException();
    }

    serverTrip.setTripItemIds(itemsByDay);
    serverTrip.setLastModified(new Date());
    serverTrip.setVersion(serverTrip.getVersion() + 1);
    serverTrip.setLastModifiedBy(userEmail);

    final PersistenceManager pm = PMF.get().getPersistenceManager();
    return pm.detachCopy(pm.makePersistent(serverTrip));
  }

  public Trip copyTrip(String tripId, String userEmail, String username) {
    Trip tripToCopy = getTrip(tripId);
    Trip trip = tripToCopy.copy();
    try {
      trip.setStartDate(SharedConstants.UNSCHEDULED_DATE);

      trip.setOwnerId(userEmail);
      trip.setOwnerName(username);
      trip.setAddedOn(new Date());
      trip.setLastModified(new Date());
      trip.setLastModifiedBy(userEmail);
      trip.setStatus(Status.ACTIVE);

      trip.setContributorIds(new HashSet<String>());
      trip.setViewerIds(new HashSet<String>());

      trip.setCommentIds(new IdDateTupleList());
      trip.setCommentsUpdated(false);

      trip.setTripItemIds(new IdDayDateTupleList());
      trip.setUpdated(false);

      trip = addTrip(trip, userEmail);
      trip = addTripItemsToTrip(getTripItems(tripToCopy), trip.getKey(), userEmail);
    } catch (AuthorizationException e) {
      e.printStackTrace();
      trip = null;
    } catch (TransactionFailedException e) {
      e.printStackTrace();
      trip = null;
    }
    return trip;
  }

  private ArrayList<TripItem> getTripItems(Trip trip) {
    final ArrayList<String> itemIds = new ArrayList<String>();
    for (IdDayDateTuple tup : trip.getTripItemIds().getTuples()) {
      itemIds.add(tup.getId());
    }
    return tripItemUtils.fetchTripItems(itemIds);
  }
  
  private Trip addTripItemsToTrip(ArrayList<TripItem> items, String tripId, String userEmail)
      throws AuthorizationException, TransactionFailedException {
    ArrayList<TripItem> newItems = new ArrayList<TripItem>();

    for (TripItem item : items) {
      TripItem newItem = item.copy();
      newItem.setTripId(tripId);
//      newItem.setStartDay(SharedConstants.UNSCHEDULED_DAY);

      newItem.setOwnerId(userEmail);
      newItem.setAddedOn(new Date());
      newItem.setLastModified(new Date());
      newItem.setLastModifiedBy(userEmail);
      newItem.setStatus(Status.ACTIVE);

      newItem.setThumbsDown(0);
      newItem.setThumbsUp(0);

      newItem.setCommentsUpdated(false);
      newItem.setUpdated(false);
      newItem.setVisited(false);
      newItems.add(newItem);
    }
    tripItemUtils.addTripItems(newItems, userEmail, tripId);
    return getTrip(tripId);
  }
}
