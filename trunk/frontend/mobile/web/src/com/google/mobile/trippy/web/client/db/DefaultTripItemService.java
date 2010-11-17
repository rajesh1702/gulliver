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
import com.google.mobile.trippy.web.shared.models.Trip;
import com.google.mobile.trippy.web.shared.models.TripItem;
import com.google.mobile.trippy.web.shared.models.TripItemUpdateResult;

import java.util.ArrayList;

/**
 * Add, edit, delete and sync trip items.
 * 
 */
public class DefaultTripItemService implements TripItemService {

  private final RemoteTripServiceAsync remoteTripService =
      (RemoteTripServiceAsync) GWT.create(RemoteTripService.class);
  private final Message messages = (Message) GWT.create(Message.class);
  private final LocalDbManager localDbManager;
  private final SyncManager syncManager;

  public DefaultTripItemService(HandlerManager eventBus) {
    super();
    this.localDbManager = new LocalDbManager(eventBus, DefaultUtils.getInstance());
    syncManager = new SyncManager(localDbManager);
  }

  /**
   * Add trip item to remote and local db.
   * 
   * @throws AuthorizationException
   */
  @Override
  public void addTripItem(final TripItem tripItem, final AsyncCallback<TripItem> callback)
      throws AuthorizationException {
    Preconditions.checkArgument(tripItem.getKey() == null || tripItem.getKey().isEmpty());

    final Trip trip = localDbManager.getTrip(tripItem.getTripId());
    if (!DefaultUserUtils.getInstance().isContributor(
        trip, DefaultUtils.getInstance().getUserEmail())) {
      throw new AuthorizationException(messages.unauthorizedAdd("trip item"));
    }

    remoteTripService.addTripItem(tripItem, new AsyncCallback<TripItemUpdateResult>() {

      @Override
      public void onSuccess(final TripItemUpdateResult resultTripItem) {
        localDbManager.updateTrip(resultTripItem.getTrip());
        localDbManager.addTripItem(resultTripItem.getTripItem());

        if (callback != null) {
          callback.onSuccess(resultTripItem.getTripItem());
        }
        syncManager.sync(resultTripItem.getTrip(), null);
      }

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
    });
  }

  /**
   * Add trip item to local db.
   */ 
  @Override
  public void addTripItemTolocalDb(final TripItem tripItem, final Trip trip) {
    localDbManager.updateTrip(trip);
    localDbManager.addTripItem(tripItem);
    syncManager.sync(trip, null);
  }
  /*
   * Fetch trip items and comments from remote db and save it in local db.
   */
  @Override
  public void sync(final Trip trip) {
    syncManager.sync(trip, null);
  }
  /**
   * Update trip item to remote and local db.
   * 
   * @throws AuthorizationException
   */
  @Override
  public void updateTripItem(final TripItem tripItem, final AsyncCallback<TripItem> callback)
      throws AuthorizationException {
    Preconditions.checkArgument(!tripItem.getKey().isEmpty());

    if (!DefaultUserUtils.getInstance().isContributor(
        localDbManager.getTrip(tripItem.getTripId()),
        DefaultUtils.getInstance().getUserEmail())) {
      throw new AuthorizationException(messages.unauthorizedEdit("trip item"));
    }

    remoteTripService.updateTripItem(tripItem, new AsyncCallback<TripItemUpdateResult>() {

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
     public void onSuccess(final TripItemUpdateResult resultTripItem) {
       localDbManager.updateTrip(resultTripItem.getTrip());
       localDbManager.updateTripItem(resultTripItem.getTripItem());

       if (callback != null) {
         callback.onSuccess(resultTripItem.getTripItem());
       }
       syncManager.sync(resultTripItem.getTrip(), null);
     }
     
    });
  }


  /**
   * Delete trip item to remote and local db.
   * 
   * @throws AuthorizationException
   */
  @Override
  public void deleteTripItem(final TripItem tripItem, final AsyncCallback<Void> callback)
      throws AuthorizationException {
    Preconditions.checkArgument(!tripItem.getKey().isEmpty());

    if (!DefaultUserUtils.getInstance().isContributor(
        localDbManager.getTrip(tripItem.getTripId()),
        DefaultUtils.getInstance().getUserEmail())) {
      throw new AuthorizationException(messages.unauthorizedDelete("trip item"));
    }

    remoteTripService.deleteTripItem(tripItem, new AsyncCallback<TripItemUpdateResult>() {

      /**
       * remove trip from local db and main index.
       */
      @Override
      public void onSuccess(final TripItemUpdateResult resultTripItem) {
        localDbManager.updateTrip(resultTripItem.getTrip());
        localDbManager.deleteTripItem(resultTripItem.getTrip().getKey(),
            resultTripItem.getTripItem().getKey());
        if (callback != null) {
          callback.onSuccess(null);
        }
        syncManager.sync(resultTripItem.getTrip(), null);
      }

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
    });
  }

  @Override
  public void setTripItemUpdated(final String tripItemId, final boolean updated) {
    final TripItem tripItem = getTripItem(tripItemId);
    if (tripItem.isUpdated() == updated) {
      return;
    }
    tripItem.setUpdated(updated);
    localDbManager.updateTripItem(tripItem);
  }

  /**
   * Get trip items from main index.
   */
  @Override
  public ArrayList<TripItem> getTripItems(final String tripId) {
    return localDbManager.getTripItems(tripId);
  }

  /**
   * Get trip item by id.
   */
  @Override
  public TripItem getTripItem(final String tripItemId) {
    return localDbManager.getTripItem(tripItemId);
  }
  
}
