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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.mobile.trippy.web.client.service.RemoteTripService;
import com.google.mobile.trippy.web.client.service.RemoteTripServiceAsync;
import com.google.mobile.trippy.web.shared.models.Comment;
import com.google.mobile.trippy.web.shared.models.IdDateTuple;
import com.google.mobile.trippy.web.shared.models.IdDayDateTuple;
import com.google.mobile.trippy.web.shared.models.Trip;
import com.google.mobile.trippy.web.shared.models.TripItem;

import java.util.ArrayList;

/**
 * Keep remote and client db on same page.
 * 
 */
public class SyncManager {
  
  private final LocalDbManager localDbManager;
  private final RemoteTripServiceAsync remoteTripService = GWT.create(RemoteTripService.class);
  
  public SyncManager(LocalDbManager localDbManager) {
    super();
    this.localDbManager = localDbManager;
  }
  
  public void sync(final Trip trip, final AsyncCallback<Void> callback) {
    final ArrayList<String> extraItems = new ArrayList<String>();
    final ArrayList<String> itemsToFetch = new ArrayList<String>();
    final ArrayList<String> extraComments = new ArrayList<String>();
    final ArrayList<String> commentsToFetch = new ArrayList<String>();
    fillDiffItems(trip, extraItems, itemsToFetch);
    fillDiffComments(trip, extraComments, commentsToFetch);
    
    final AsyncCallback<Void> callbackWrap = new AsyncCallback<Void>() {

      private boolean calledOnce = false;
      
      @Override
      public void onFailure(Throwable caught) {
        if (callback != null) {
          callback.onFailure(caught);
        }
      }

      @Override
      public void onSuccess(Void result) {
        if (calledOnce && callback != null) {
          callback.onSuccess(result);
        }
        calledOnce = true;
      }
    };
    
    deleteTripItems(extraItems);
    fetchAndSaveTripItems(itemsToFetch, callbackWrap);
    deleteComments(extraComments);
    fetchAndSaveComments(commentsToFetch, callbackWrap);
  }
  
  public void sync(final ArrayList<Trip> trips, final AsyncCallback<Void> callback) {
    final ArrayList<String> extraItems = new ArrayList<String>();
    final ArrayList<String> itemsToFetch = new ArrayList<String>();
    final ArrayList<String> extraComments = new ArrayList<String>();
    final ArrayList<String> commentsToFetch = new ArrayList<String>();
    for (Trip trip : trips) {
      fillDiffItems(trip, extraItems, itemsToFetch);
      fillDiffComments(trip, extraComments, commentsToFetch);
    }
    
    final AsyncCallback<Void> callbackWrap = new AsyncCallback<Void>() {

      private boolean calledOnce = false;
      
      @Override
      public void onFailure(Throwable caught) {
        if (callback != null) {
          callback.onFailure(caught);
        }
      }

      @Override
      public void onSuccess(Void result) {
        if (calledOnce && callback != null) {
          callback.onSuccess(result);
        }
        calledOnce = true;
      }
    };
    
    deleteTripItems(extraItems);
    fetchAndSaveTripItems(itemsToFetch, callbackWrap);
    deleteComments(extraComments);
    fetchAndSaveComments(commentsToFetch, callbackWrap);
  }
  
  void fillDiffItems(final Trip trip, final ArrayList<String> extraItems, 
      final ArrayList<String> itemsToFetch) {
    final ArrayList<IdDayDateTuple> remoteItems = trip.getTripItemIds().getTuples();
    final ArrayList<String> remoteItemIds = new ArrayList<String>();
    for (IdDayDateTuple tup : remoteItems) {
      remoteItemIds.add(tup.getId());
    }
    extraItems.addAll(getExtraItems(trip.getKey(), remoteItemIds));
    itemsToFetch.addAll(getDiffItems(trip.getKey(), remoteItems));
  }
  
  void fillDiffComments(final Trip trip, final ArrayList<String> extraComments, 
      final ArrayList<String> commentsToFetch) {
    final ArrayList<IdDateTuple> remoteComments = trip.getCommentIds().getTuples();
    final ArrayList<String> remoteCommentIds = new ArrayList<String>();
    for (IdDateTuple tup : remoteComments) {
      remoteCommentIds.add(tup.getId());
    }
    extraComments.addAll(getExtraComments(trip.getKey(), remoteCommentIds));
    commentsToFetch.addAll(getDiffComments(trip.getKey(), remoteComments));
  }

  /**
   * Extra trip items in the local db. Trip items to be deleted.
   * 
   * @param tripId
   * @param remoteItemIds
   * @return
   */
  ArrayList<String> getExtraItems(final String tripId, final ArrayList<String> remoteItemIds) {
    final ArrayList<String> localItemIds = getLocalItemIds(tripId);
    localItemIds.removeAll(remoteItemIds);
    return localItemIds;
  }
  
  /**
   * Trip items to be fetched and saved.
   * 
   * @param tripId
   * @param remoteItems
   * @return
   */
  ArrayList<String> getDiffItems(final String tripId, final ArrayList<IdDayDateTuple> remoteItems) {
    final ArrayList<String> rItemIds = new ArrayList<String>();
    for (IdDayDateTuple remoteTup : remoteItems) {
      final TripItem localItem = localDbManager.getTripItem(remoteTup.getId());
      if (localItem == null || remoteTup.getLastModified().after(localItem.getLastModified())) {
        rItemIds.add(remoteTup.getId());
      }
    }
    return rItemIds;
  }
  
  /**
   * Get Trip item ids from local db.
   * 
   * @param tripId
   * @return
   */
  ArrayList<String> getLocalItemIds(String tripId) {
    ArrayList<TripItem> localItems = localDbManager.getTripItems(tripId);
    ArrayList<String> localItemIds = new ArrayList<String>();
    for (TripItem item : localItems) {
      localItemIds.add(item.getKey());
    }
    return localItemIds;
  }
  
  /**
   * Fetch trip items from remote db and save it in local db.
   * 
   * @param itemIds
   */
  void fetchAndSaveTripItems(final ArrayList<String> itemIds, final AsyncCallback<Void> callback) {
    if (itemIds == null || itemIds.isEmpty()) {
      if (callback != null) {
        callback.onSuccess(null);
      }
      return;
    }
    final int toIndex = itemIds.size() > 20 ? 20 : itemIds.size();
    remoteTripService.fetchTripItems(new ArrayList<String>(itemIds.subList(0, toIndex)),
        new AsyncCallback<ArrayList<TripItem>>() {

      @Override
      public void onFailure(Throwable caught) {
        if (callback != null) {
          callback.onFailure(caught);
        }
      }

      @Override
      public void onSuccess(ArrayList<TripItem> result) {
        for (TripItem tripItem : result) {
          tripItem.setUpdated(true);
          localDbManager.addTripItem(tripItem);
        }
        itemIds.subList(0, toIndex).clear();
        fetchAndSaveTripItems(itemIds, callback);
      }
    });
  }
  
  /**
   * Delete trip items from local db.
   * 
   * @param itemIds
   */
  void deleteTripItems(final ArrayList<String> itemIds) {
    for (String itemId : itemIds) {
      TripItem item = localDbManager.getTripItem(itemId);
      localDbManager.deleteTripItem(item.getTripId(), itemId);
    }
  }
  
  /**
   * Extra comments in local db. Comments to be deleted.
   * 
   * @param tripId
   * @param remoteCommentIds
   * @return
   */
  ArrayList<String> getExtraComments(String tripId, ArrayList<String> remoteCommentIds) {
    final ArrayList<String> commentIds = getLocalCommentIds(tripId);
    commentIds.removeAll(remoteCommentIds);
    return commentIds;
  }
  
  /**
   * Comments to be fetched and saved.
   * 
   * @param tripId
   * @param remoteComments
   * @return
   */
  ArrayList<String> getDiffComments(final String tripId, 
      final ArrayList<IdDateTuple> remoteComments) {
    final ArrayList<String> rCommentIds = new ArrayList<String>();
    for (IdDateTuple remoteTup : remoteComments) {
      final Comment localComment = localDbManager.getComment(remoteTup.getId());
      if (localComment == null 
          || remoteTup.getLastModified().after(localComment.getLastModified())) {
        rCommentIds.add(remoteTup.getId());
      }
    }
    return rCommentIds;
  }
  
  /**
   * Comment ids in local db.
   * 
   * @param tripId
   * @return
   */
  ArrayList<String> getLocalCommentIds(String tripId) {
    ArrayList<String> itemIds = getLocalItemIds(tripId);
    ArrayList<String> commentIds = new ArrayList<String>();
    for (String itemId : itemIds) {
      commentIds.addAll(localDbManager.getCommentIds(tripId, itemId));
    }
    return commentIds;
  }
  
  /**
   * Fetch comments from remote db and save to local db.
   * 
   * @param commentIds
   */
  void fetchAndSaveComments(final ArrayList<String> ids, final AsyncCallback<Void> callback) {
    if (ids == null || ids.isEmpty()) {
      if (callback != null) {
        callback.onSuccess(null);
      }
      return;
    }
    final int toIndex = ids.size() > 20 ? 20 : ids.size();
    remoteTripService.fetchComments(new ArrayList<String>(ids.subList(0, toIndex)),
        new AsyncCallback<ArrayList<Comment>>() {

      @Override
      public void onFailure(Throwable caught) {
        if (callback != null) {
          callback.onFailure(caught);
        }
      }

      @Override
      public void onSuccess(ArrayList<Comment> result) {
        for (Comment comment : result) {
          localDbManager.addComment(comment);
        }
        ids.subList(0, toIndex).clear();
        fetchAndSaveComments(ids, callback);
      }
    });
  }
  
  /**
   * Delete comment from local db.
   * 
   * @param ids
   */
  void deleteComments(ArrayList<String> ids) {
    for (String id : ids) {
      final Comment comment = localDbManager.getComment(id);
      localDbManager.deleteComment(comment.getTripId(), comment.getTripItemId(), id);
    }
  }
}
