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
package com.google.mobile.trippy.web.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.mobile.trippy.web.shared.models.Comment;
import com.google.mobile.trippy.web.shared.models.CommentUpdateResult;
import com.google.mobile.trippy.web.shared.models.IdDayDateTupleList;
import com.google.mobile.trippy.web.shared.models.Trip;
import com.google.mobile.trippy.web.shared.models.TripFetchResult;
import com.google.mobile.trippy.web.shared.models.TripItem;
import com.google.mobile.trippy.web.shared.models.TripItemUpdateResult;

import java.util.ArrayList;
import java.util.Date;

public interface RemoteTripServiceAsync {

  void addTrip(Trip trip, AsyncCallback<Trip> callback);

  void addTripItem(TripItem trip, AsyncCallback<TripItemUpdateResult> callback);

  void addComment(Comment comment, AsyncCallback<CommentUpdateResult> callback);

  void updateTrip(Trip updatedTrip, AsyncCallback<Trip> callback);
  
  void updateTripItemsTuple(String tripKey, Long clientTripVersion, IdDayDateTupleList updatedItems, 
      AsyncCallback<Trip> callback);

  void updateTripItem(TripItem trip, AsyncCallback<TripItemUpdateResult> callback);

  void fetchUpdatedTrips(Date from, String cursor, int limit,
      AsyncCallback<TripFetchResult> callback);

  void fetchTripItems(ArrayList<String> ids,
      AsyncCallback<ArrayList<TripItem>> callback);

  void fetchComments(ArrayList<String> ids,
      AsyncCallback<ArrayList<Comment>> callback);

  void deleteTrip(Trip trip, AsyncCallback<Void> callback);

  void deleteTripItem(TripItem item, AsyncCallback<TripItemUpdateResult> callback);

  void deleteComment(Comment comment, AsyncCallback<CommentUpdateResult> callback);

  void sendInvite(String tripId, ArrayList<String> emails, boolean collaborator,
      AsyncCallback<String> callback);
  
}
