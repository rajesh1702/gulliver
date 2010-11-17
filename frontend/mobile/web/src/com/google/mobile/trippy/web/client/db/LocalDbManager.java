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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.mobile.trippy.web.client.base.Utils;
import com.google.mobile.trippy.web.client.event.CommentAddedEvent;
import com.google.mobile.trippy.web.client.event.CommentDeletedEvent;
import com.google.mobile.trippy.web.client.event.TripAddedEvent;
import com.google.mobile.trippy.web.client.event.TripDeletedEvent;
import com.google.mobile.trippy.web.client.event.TripItemAddedEvent;
import com.google.mobile.trippy.web.client.event.TripItemDeletedEvent;
import com.google.mobile.trippy.web.client.event.TripItemUpdatedEvent;
import com.google.mobile.trippy.web.client.event.TripUpdatedEvent;
import com.google.mobile.trippy.web.shared.models.Comment;
import com.google.mobile.trippy.web.shared.models.Status;
import com.google.mobile.trippy.web.shared.models.Trip;
import com.google.mobile.trippy.web.shared.models.TripItem;

import java.util.ArrayList;

/**
 * Local manager to deal with local db.
 * 
 */
public class LocalDbManager {

  /**
   * Key for trip list in local db.
   */
  private static final String KEY_LOCAL_STORE_TRIPS = "trippy_trips";
  /**
   * Key for list of trips pending to sync to remote db.
   */
  private static final String KEY_LOCAL_STORE_TRIPS_PENDING = "trippy_trips_pending";
  /**
   * Key prefix for each trip. (key = PREFIX + tripId)
   */
  private static final String KEY_LOCAL_STORE_TRIP_PREFIX = "trippy_trip_";
  /**
   * Key for last sync time in local db.
   */
  private static final String KEY_TRIP_LAST_SYNC = "sync_trip";

  /**
   * Key prefix for list trip items in a trip in local db. (key = PREFIX +
   * tripId)
   */
  private static final String KEY_LOCAL_STORE_TIS_PREFIX = "trippy_tis_";
  /**
   * Key for list of trip items pending for local --> remote sync.
   */
  private static final String KEY_LOCAL_STORE_TIS_PENDING = "trippy_tis_pending";
  /**
   * Key prefix for trip item in local db. (key = PREFIX + tripItemId)
   */
  private static final String KEY_LOCAL_STORE_TI_PREFIX = "trippy_ti_";

  /**
   * Key prefix for comments list in local db. (key = PREFIX + tripId + "_" +
   * tripItemId).
   */
  private static final String KEY_LOCAL_STORE_COMMENTS_PREFIX = "trippy_comments_";
  /**
   * Key for list of comments pending for local to remote sync.
   */
  private static final String KEY_LOCAL_STORE_COMMENTS_PENDING = "trippy_comments_pending";
  /**
   * Key prefix for comment in local db. (key = PREFIX + commentId)
   */
  private static final String KEY_LOCAL_STORE_COMMENT_PREFIX = "trippy_comment_";

  private HandlerManager eventBus;
  private Utils utils;

  public LocalDbManager(HandlerManager eventBus, Utils utils) {
    super();
    this.eventBus = eventBus;
    this.utils = utils;
  }

  /**
   * Get trips from main index.
   */
  public ArrayList<Trip> getTrips() {
    JsArrayString tripKeys = getIndexKeys(KEY_LOCAL_STORE_TRIPS);
    ArrayList<Trip> trips = new ArrayList<Trip>();
    for (int i = 0; i < tripKeys.length(); ++i) {
      trips.add(getTripForKey(tripKeys.get(i)));
    }
    return trips;
  }

  /**
   * Get trip by id from local db.
   */
  public Trip getTrip(String tripId) {
    return getTripForKey(KEY_LOCAL_STORE_TRIP_PREFIX + tripId);
  }

  public void addTrip(final Trip trip) {
    JsoTrip jsoTrip = (JsoTrip) JavaScriptObject.createObject();
    jsoTrip.setTrip(trip);
    final String tripKey = KEY_LOCAL_STORE_TRIP_PREFIX + trip.getKey();
    LocalDbService.makePersistent(tripKey, new JSONObject(jsoTrip).toString());
    final JsArrayString tripKeys = getIndexKeys(KEY_LOCAL_STORE_TRIPS);
    for (int i = 0; i < tripKeys.length(); ++i) {
      if (tripKeys.get(i).equals(tripKey)) {
        return;
      }
    }
    tripKeys.push(tripKey);
    setIndexKeys(KEY_LOCAL_STORE_TRIPS, tripKeys);
    eventBus.fireEvent(new TripAddedEvent(trip));
  }

  public void updateTrip(final Trip trip) {
    JsoTrip jsoTrip = (JsoTrip) JavaScriptObject.createObject();
    jsoTrip.setTrip(trip);
    final String tripKey = KEY_LOCAL_STORE_TRIP_PREFIX + trip.getKey();
    LocalDbService.makePersistent(tripKey, new JSONObject(jsoTrip).toString());
    eventBus.fireEvent(new TripUpdatedEvent(trip));
  }

  public void deleteTrip(final Trip trip) {
    final String tripKey = KEY_LOCAL_STORE_TRIP_PREFIX + trip.getKey();
    LocalDbService.deletePersistent(tripKey);
    final JsArrayString tripKeys = getIndexKeys(KEY_LOCAL_STORE_TRIPS);
    final JsArrayString newTripKeys = (JsArrayString) JavaScriptObject.createArray();
    for (int i = 0; i < tripKeys.length(); ++i) {
      if (!tripKeys.get(i).equals(tripKey)) {
        newTripKeys.push(tripKeys.get(i));
      }
    }
    setIndexKeys(KEY_LOCAL_STORE_TRIPS, newTripKeys);
    eventBus.fireEvent(new TripDeletedEvent(trip));
  }

  public void addTripItem(final TripItem tripItem) {
    JsoTripItem jsoTripItem = (JsoTripItem) JavaScriptObject.createObject();
    jsoTripItem.setTripItem(tripItem);
    final String tripItemKey = KEY_LOCAL_STORE_TI_PREFIX + tripItem.getKey();
    LocalDbService.makePersistent(tripItemKey, new JSONObject(jsoTripItem).toString());
    final JsArrayString tripItemKeys =
        getIndexKeys(KEY_LOCAL_STORE_TIS_PREFIX + tripItem.getTripId());
    for (int i = 0; i < tripItemKeys.length(); ++i) {
      if (tripItemKeys.get(i).equals(tripItemKey)) {
        return;
      }
    }
    tripItemKeys.push(tripItemKey);
    setIndexKeys(KEY_LOCAL_STORE_TIS_PREFIX + tripItem.getTripId(), tripItemKeys);
    eventBus.fireEvent(new TripItemAddedEvent(tripItem));
    final Trip trip = getTrip(tripItem.getTripId());
    final String tripItemStartDate = "Day " + tripItem.getStartDay();
    final String latlng = tripItem.getLatitude() + "," + tripItem.getLongitude();
    utils.addTripItem(tripItemStartDate, tripItem.getTripId(), tripItem.getKey(), tripItem
        .getName(), tripItem.getAddress() + ", " + trip.getLocation(), latlng,
        utils.getTripItemPosition(tripItem.getTripId(),
        tripItem.getKey(), tripItem.getStartDay()));
  }

  public void updateTripItem(final TripItem tripItem) {
    JsoTripItem jsoTripItem = (JsoTripItem) JavaScriptObject.createObject();
    jsoTripItem.setTripItem(tripItem);
    final String tripItemKey = KEY_LOCAL_STORE_TI_PREFIX + tripItem.getKey();
    LocalDbService.makePersistent(tripItemKey, new JSONObject(jsoTripItem).toString());
    eventBus.fireEvent(new TripItemUpdatedEvent(tripItem));
    final Trip trip = getTrip(tripItem.getTripId());
    final String tripItemStartDate = "Day " + tripItem.getStartDay();
    final String latlng = tripItem.getLatitude() + "," + tripItem.getLongitude();
    utils.addTripItem(tripItemStartDate, tripItem.getTripId(), tripItem.getKey(), tripItem
        .getName(), tripItem.getAddress() + ", " + trip.getLocation(), latlng,
        utils.getTripItemPosition(tripItem.getTripId(),
        tripItem.getKey(), tripItem.getStartDay()));
  }

  public void deleteTripItem(final String tripId, final String tripItemId) {
    final TripItem tripItem = getTripItem(tripItemId);
    final String tripItemKey = KEY_LOCAL_STORE_TI_PREFIX + tripItemId;
    LocalDbService.deletePersistent(tripItemKey);
    final JsArrayString tripItemKeys = getIndexKeys(KEY_LOCAL_STORE_TIS_PREFIX + tripId);
    final JsArrayString newTripItemKeys = (JsArrayString) JavaScriptObject.createArray();
    for (int i = 0; i < tripItemKeys.length(); ++i) {
      if (!tripItemKeys.get(i).equals(tripItemKey)) {
        newTripItemKeys.push(tripItemKeys.get(i));
      }
    }
    setIndexKeys(KEY_LOCAL_STORE_TIS_PREFIX + tripId, newTripItemKeys);
    eventBus.fireEvent(new TripItemDeletedEvent(tripItem));
    utils.remTripItem(tripItemId);
  }

  /**
   * Get trip items from main index.
   */
  public ArrayList<TripItem> getTripItems(final String tripId) {
    final JsArrayString tripItemKeys = getIndexKeys(KEY_LOCAL_STORE_TIS_PREFIX + tripId);
    final ArrayList<TripItem> tripItems = new ArrayList<TripItem>();
    for (int i = 0; i < tripItemKeys.length(); ++i) {
      final TripItem tripItem = getTripItemForKey(tripItemKeys.get(i));
      if (tripItem != null && tripItem.getStatus().equals(Status.ACTIVE)) {
        tripItems.add(tripItem);
      }
    }
    return tripItems;
  }

  /**
   * Get trip item by id.
   */
  public TripItem getTripItem(final String tripItemId) {
    return getTripItemForKey(KEY_LOCAL_STORE_TI_PREFIX + tripItemId);
  }

  public ArrayList<String> getTripItemIds() {
    final JsArrayString keys = getIndexKeys(KEY_LOCAL_STORE_TIS_PREFIX);
    final ArrayList<String> ids = new ArrayList<String>();
    for (int i = 0; i < keys.length(); ++i) {
      final String key = keys.get(i);
      final String id = key.replace(KEY_LOCAL_STORE_TI_PREFIX, "");
      ids.add(id);
    }
    return ids;
  }

  public void addComment(final Comment comment) {
    final JsoComment jsoComment = (JsoComment) JavaScriptObject.createObject();
    jsoComment.setComment(comment);
    final String commentKey = KEY_LOCAL_STORE_COMMENT_PREFIX + comment.getKey();
    LocalDbService.makePersistent(commentKey, new JSONObject(jsoComment).toString());
    final JsArrayString commentKeys =
        getIndexKeys(KEY_LOCAL_STORE_COMMENTS_PREFIX + comment.getTripId() + "_"
            + comment.getTripItemId());
    for (int i = 0; i < commentKeys.length(); ++i) {
      if (commentKeys.get(i).equals(commentKey)) {
        return;
      }
    }
    commentKeys.push(commentKey);
    setIndexKeys(KEY_LOCAL_STORE_COMMENTS_PREFIX + comment.getTripId() + "_"
        + comment.getTripItemId(), commentKeys);
    eventBus.fireEvent(new CommentAddedEvent(comment));
  }

  public void deleteComment(final String tripId, final String tripItemId, final String commentId) {
    final Comment comment = getComment(commentId);
    final String commentKey = KEY_LOCAL_STORE_COMMENT_PREFIX + commentId;
    LocalDbService.deletePersistent(commentKey);
    final JsArrayString commentKeys =
        getIndexKeys(KEY_LOCAL_STORE_COMMENTS_PREFIX + tripId + "_" + tripItemId);
    final JsArrayString newCommentKeys = (JsArrayString) JavaScriptObject.createArray();
    for (int i = 0; i < commentKeys.length(); ++i) {
      if (!commentKeys.get(i).equals(commentKey)) {
        newCommentKeys.push(commentKeys.get(i));
      }
    }
    setIndexKeys(KEY_LOCAL_STORE_COMMENTS_PREFIX + tripId + "_" + tripItemId, newCommentKeys);
    eventBus.fireEvent(new CommentDeletedEvent(comment));
  }

  /**
   * Get comments by trip and trip item id.
   */
  public ArrayList<Comment> getComments(final String tripId, final String tripItemId) {
    final JsArrayString tripItemKeys =
        getIndexKeys(KEY_LOCAL_STORE_COMMENTS_PREFIX + tripId + "_" + tripItemId);
    final ArrayList<Comment> comments = new ArrayList<Comment>();
    for (int i = 0; i < tripItemKeys.length(); ++i) {
      final Comment comment = getCommentFromKey(tripItemKeys.get(i));
      if (comment != null && comment.getStatus().equals(Status.ACTIVE)) {
        comments.add(comment);
      }
    }
    return comments;
  }

  public ArrayList<String> getCommentIds(final String tripId, final String tripItemId) {
    final JsArrayString keys =
        getIndexKeys(KEY_LOCAL_STORE_COMMENTS_PREFIX + tripId + "_" + tripItemId);
    final ArrayList<String> ids = new ArrayList<String>();
    for (int i = 0; i < keys.length(); ++i) {
      final String key = keys.get(i);
      final String id = key.replace(KEY_LOCAL_STORE_COMMENT_PREFIX, "");
      ids.add(id);
    }
    return ids;
  }

  public Comment getComment(final String id) {
    final String key = KEY_LOCAL_STORE_COMMENT_PREFIX + id;
    return getCommentFromKey(key);
  }

  /**
   * Set trip key array to the given index.
   */
  void setIndexKeys(final String indexName, final JsArrayString tripKeys) {
    LocalDbService.makePersistent(indexName, new JSONArray(tripKeys).toString());
  }

  /**
   * Get trip key array from the given index.
   */
  JsArrayString getIndexKeys(final String indexName) {
    String jsonString = LocalDbService.getPersistent(indexName);
    if (jsonString == null) {
      return (JsArrayString) JavaScriptObject.createArray();
    }
    return (JsArrayString) JSONParser.parse(jsonString).isArray().getJavaScriptObject();
  }

  /**
   * Get trip by key from local db.
   */
  private Trip getTripForKey(String tripKey) {
    final String tripSerial = LocalDbService.getPersistent(tripKey);
    if (tripSerial == null) {
      return null;
    }
    final JsoTrip jsoTrip = (JsoTrip) JSONParser.parse(tripSerial).isObject().getJavaScriptObject();

    return jsoTrip.getTrip();
  }

  /**
   * Get trip item by key.
   */
  private TripItem getTripItemForKey(final String tripItemKey) {
    final String tripItemSerial = LocalDbService.getPersistent(tripItemKey);
    if (tripItemSerial == null) {
      return null;
    }
    final JsoTripItem jsoTripItem =
        (JsoTripItem) JSONParser.parse(tripItemSerial).isObject().getJavaScriptObject();

    return jsoTripItem.getTripItem();
  }

  /**
   * Get comment by key.
   */
  private Comment getCommentFromKey(final String commentKey) {
    final String commentSerial = LocalDbService.getPersistent(commentKey);
    if (commentSerial == null) {
      return null;
    }
    final JsoComment jsoComment =
        (JsoComment) JSONParser.parse(commentSerial).isObject().getJavaScriptObject();

    return jsoComment.getComment();
  }
}
