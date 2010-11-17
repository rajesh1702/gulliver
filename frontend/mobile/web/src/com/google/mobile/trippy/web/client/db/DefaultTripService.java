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

package com.google.mobile.trippy.web.client.db;

import com.google.common.base.Preconditions;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.mobile.trippy.web.client.base.DefaultUtils;
import com.google.mobile.trippy.web.client.i18n.Message;
import com.google.mobile.trippy.web.client.service.RemoteTripService;
import com.google.mobile.trippy.web.client.service.RemoteTripServiceAsync;
import com.google.mobile.trippy.web.shared.DefaultUserUtils;
import com.google.mobile.trippy.web.shared.exception.AuthenticationException;
import com.google.mobile.trippy.web.shared.exception.AuthorizationException;
import com.google.mobile.trippy.web.shared.exception.TripNotFoundException;
import com.google.mobile.trippy.web.shared.exception.TripVersionException;
import com.google.mobile.trippy.web.shared.models.IdDayDateTuple;
import com.google.mobile.trippy.web.shared.models.IdDayDateTupleList;
import com.google.mobile.trippy.web.shared.models.Status;
import com.google.mobile.trippy.web.shared.models.Trip;
import com.google.mobile.trippy.web.shared.models.TripFetchResult;
import com.google.mobile.trippy.web.shared.models.TripItem;

import java.util.ArrayList;
import java.util.Date;


/**
 * Add, edit, delete and sync trips.
 * 
 */
public class DefaultTripService implements TripService {
  /**
   * Key for last sync time in local db.
   */
  private static final String KEY_TRIP_LAST_SYNC = "sync_trip";

  private final RemoteTripServiceAsync remoteTripService =
      (RemoteTripServiceAsync) GWT.create(RemoteTripService.class);
  private final Message message = (Message) GWT.create(Message.class);
  private final SyncManager syncManager;
  private final LocalDbManager localDbManager;

  public DefaultTripService(HandlerManager eventBus) {
    super();
    this.localDbManager = new LocalDbManager(eventBus, DefaultUtils.getInstance());
    syncManager = new SyncManager(localDbManager);
  }
  
  /**
   * Add trip to remote and local db.
   */
  @Override
  public void addTrip(final Trip trip, final AsyncCallback<Trip> callback) {
    Preconditions.checkArgument(trip.getKey() == null || trip.getKey().isEmpty());

    remoteTripService.addTrip(trip, new AsyncCallback<Trip>() {

      @Override
      public void onFailure(final Throwable caught) {
        if (caught instanceof AuthenticationException) {
          DefaultUtils.getInstance().redirect(
              DefaultUtils.getInstance().getLoginUrl(Location.getHref()));
        }
        if (callback != null) {
          callback.onFailure(caught);
        }
      }
      
      @Override
      public void onSuccess(final Trip resultTrip) {
        localDbManager.addTrip(resultTrip);
        if (callback != null) {
          callback.onSuccess(resultTrip);
        }
      }
    });
  }
  
  /**
   * Add trip to local db.
   */
  @Override
  public void addTripToLocalDb(final Trip trip) {
    localDbManager.addTrip(trip);
  }

  /**
   * Update trip at remote and local db.
   * 
   * @throws AuthorizationException
   */
  @Override
  public void updateTrip(final Trip trip, final AsyncCallback<Trip> callback)
      throws AuthorizationException {
    Preconditions.checkArgument(!trip.getKey().isEmpty());
    if (!DefaultUserUtils.getInstance().isContributor(trip,
        DefaultUtils.getInstance().getUserEmail())) {
      throw new AuthorizationException(message.unauthorizedEdit("trip"));
    }
    remoteTripService.updateTrip(trip, new AsyncCallback<Trip>() {

      @Override
      public void onFailure(final Throwable caught) {
        if (caught instanceof AuthenticationException) {
          DefaultUtils.getInstance().redirect(
              DefaultUtils.getInstance().getLoginUrl(Location.getHref()));
        }
        if (callback != null) {
          callback.onFailure(caught);
        }
      }
      
      @Override
      public void onSuccess(final Trip resultTrip) {
        localDbManager.updateTrip(resultTrip);
        if (callback != null) {
          callback.onSuccess(resultTrip);
        }
        syncManager.sync(resultTrip, null);
      }
    });
  }


  @Override
  public void updateTripItemsTuple(final Trip trip, final AsyncCallback<Trip> callback)
      throws AuthorizationException {
    Preconditions.checkArgument(!trip.getKey().isEmpty());
    if (!DefaultUserUtils.getInstance().isContributor(trip,
        DefaultUtils.getInstance().getUserEmail())) {
      throw new AuthorizationException(message.unauthorizedEdit("trip"));
    }
    remoteTripService.updateTripItemsTuple(trip.getKey(), trip.getVersion(), 
        trip.getTripItemIds(), new AsyncCallback<Trip>() {

      @Override
      public void onFailure(final Throwable caught) {
        if (caught instanceof AuthorizationException) {
          callback.onFailure(caught);
        }
        if (caught instanceof AuthenticationException) {
          DefaultUtils.getInstance().redirect(
              DefaultUtils.getInstance().getLoginUrl(Location.getHref()));
        }
        if (caught instanceof TripNotFoundException) {
          localDbManager.deleteTrip(trip);
          callback.onFailure(caught);
        }
        if (caught instanceof TripVersionException) {
          callback.onFailure(caught);
        }
      }
      
      @Override
      public void onSuccess(final Trip resultTrip) {
        localDbManager.updateTrip(resultTrip);
        if (callback != null) {
          callback.onSuccess(resultTrip);
        }
        //TODO: remove this after implementing single item id for same
        // items. since Also remove the method defination, it is not used  
        // anywhere else.
        updateTripItems(trip.getTripItemIds());
        syncManager.sync(resultTrip, null);
      }
    });
  }
  
  /**
   * Delete trip from remote and local db.
   * 
   * @throws AuthorizationException
   */
  @Override
  public void deleteTrip(final Trip trip, final AsyncCallback<Void> callback)
      throws AuthorizationException {
    Preconditions.checkArgument(!trip.getKey().isEmpty());
    if (!DefaultUserUtils.getInstance().isOwner(trip, DefaultUtils.getInstance().getUserEmail())) {
      throw new AuthorizationException(message.unauthorizedDelete("trip"));
    }
    remoteTripService.deleteTrip(trip, new AsyncCallback<Void>() {
      /**
       * Add trip to pending index for later retries.
       */
      @Override
      public void onFailure(final Throwable caught) {
        if (caught instanceof AuthenticationException) {
          DefaultUtils.getInstance().redirect(
              DefaultUtils.getInstance().getLoginUrl(Location.getHref()));
        }
        if (callback != null) {
          callback.onFailure(caught);
        }
      }
      
      /**
       * remove trip from local db and main index.
       */
      @Override
      public void onSuccess(final Void result) {
        localDbManager.deleteTrip(trip);
        if (callback != null) {
          callback.onSuccess(result);
        }
      }
    });
  }

  /**
   * Sync trips from remote --> local db.
   */
  @Override
  public void syncFromRemoteDb(final AsyncCallback<Void> callback) {
    Date lastSyncTime = getLastSyncTime();
    syncFromRemoteDb(lastSyncTime, null, 20, callback);
  }
  
  void syncFromRemoteDb(final Date from, final String cursor, final int limit,
      final AsyncCallback<Void> callback) {
    remoteTripService.fetchUpdatedTrips(from, cursor, limit, new AsyncCallback<TripFetchResult>() {

      @Override
      public void onFailure(Throwable caught) {
        if (callback != null) {
          callback.onFailure(caught);
        }
      }

      @Override
      public void onSuccess(TripFetchResult result) {
        for (Trip trip : result.getTrips()) {
          if (trip.getStatus().equals(Status.ACTIVE)) {
            localDbManager.addTrip(trip);
          } else {
            localDbManager.deleteTrip(trip);
          }
          if (trip.getLastModified().after(getLastSyncTime())) {
            setLastSyncTime(trip.getLastModified());
          }
        }
        if (result.getTrips().size() == limit) {
          syncFromRemoteDb(from, result.getCursor(), limit, callback);
        } else {
          syncManager.sync(getTrips(), callback);
        }
      }
    });
  }

  private Date getLastSyncTime() {
    String lastSyncTimeStr = LocalDbService.getPersistent(KEY_TRIP_LAST_SYNC);
    if (lastSyncTimeStr == null) {
      return new Date(0);
    }
    return new Date(Long.parseLong(lastSyncTimeStr));
  }

  private void setLastSyncTime(Date lastSyncTime) {
    Preconditions.checkNotNull(lastSyncTime);
    LocalDbService.makePersistent(KEY_TRIP_LAST_SYNC, "" + lastSyncTime.getTime());
  }

  @Override
  public Trip getTrip(String tripId) {
    return localDbManager.getTrip(tripId);
  }

  @Override
  public ArrayList<Trip> getTrips() {
    return localDbManager.getTrips();
  }

  @Override
  public void setTripUpdated(String tripId, boolean updated) {
    final Trip trip = localDbManager.getTrip(tripId);
    trip.setUpdated(updated);
    localDbManager.updateTrip(trip);
  }
  
  private void updateTripItems(IdDayDateTupleList tripItemIds) {
    Date currDate = new Date();
    for (IdDayDateTuple tuple : tripItemIds.getTuples()){
      String tripItemId = tuple.getId();
      TripItem tripItem = localDbManager.getTripItem(tripItemId);
      
      if(tripItem.getStartDay() != tuple.getDay()) {
        tripItem.setStartDay(tuple.getDay());
        tripItem.setLastModified(currDate);
        tripItem.setLastModifiedBy(DefaultUtils.getInstance().getUserEmail());
      }
      
      // To maintain ordering too.
      localDbManager.deleteTripItem(tripItem.getTripId(), tripItemId);
      localDbManager.addTripItem(tripItem);
    }
  }
}
