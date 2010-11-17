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

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.mobile.trippy.web.shared.exception.AuthenticationException;
import com.google.mobile.trippy.web.shared.exception.AuthorizationException;
import com.google.mobile.trippy.web.shared.exception.TransactionFailedException;
import com.google.mobile.trippy.web.shared.exception.TripNotFoundException;
import com.google.mobile.trippy.web.shared.exception.TripVersionException;
import com.google.mobile.trippy.web.shared.models.Comment;
import com.google.mobile.trippy.web.shared.models.CommentUpdateResult;
import com.google.mobile.trippy.web.shared.models.IdDayDateTupleList;
import com.google.mobile.trippy.web.shared.models.Trip;
import com.google.mobile.trippy.web.shared.models.TripFetchResult;
import com.google.mobile.trippy.web.shared.models.TripItem;
import com.google.mobile.trippy.web.shared.models.TripItemUpdateResult;

import java.util.ArrayList;
import java.util.Date;

/**
 * 
 * 
 */
@RemoteServiceRelativePath("tripservice")
public interface RemoteTripService extends RemoteService {

  /**
   * Save trip on server and return new trip with remote trip and item Id.
   * 
   * @throws AuthenticationException
   */
  public Trip addTrip(Trip trip) throws AuthenticationException;

  /**
   * Save trip on server and return new trip with remote trip and item Id.
   * 
   * @throws AuthenticationException
   */
  public Trip updateTrip(Trip updatedTrip) throws AuthorizationException, AuthenticationException, 
      TripNotFoundException, TripVersionException;

  /**
   * Save trip on server and return new trip with remote trip and item Id.
   * 
   * @throws AuthenticationException
   */
  public Trip updateTripItemsTuple(String tripKey, Long clientTripVersion, 
      IdDayDateTupleList itemsByDay) throws AuthorizationException, AuthenticationException, 
      TripNotFoundException, TripVersionException;
  
  /**
   * Save trip on server and return new trip with remote trip and item Id.
   * 
   * @throws AuthenticationException
   * @throws TransactionFailedException
   */
  public TripItemUpdateResult addTripItem(TripItem trip) throws AuthorizationException,
      AuthenticationException, TransactionFailedException;

  /**
   * Save trip on server and return new trip with remote trip and item Id.
   * 
   * @throws AuthenticationException
   * @throws TransactionFailedException
   */
  public TripItemUpdateResult updateTripItem(TripItem trip) throws AuthorizationException,
      AuthenticationException, TransactionFailedException;

  /**
   * Add comment on the server and return its remote id.
   * 
   * @throws AuthorizationException
   * @throws AuthenticationException
   * @throws TransactionFailedException
   */
  public CommentUpdateResult addComment(Comment comment) throws AuthorizationException,
      AuthenticationException, TransactionFailedException;

  /**
   * Return list of trips which are modified after 'from' datetime.
   * 
   * @throws AuthenticationException
   */
  public TripFetchResult fetchUpdatedTrips(Date from, String cursor, int limit)
      throws AuthenticationException;

  /**
   * Return list of trips which are modified after 'from' datetime.
   * 
   * @throws AuthenticationException
   */
  public ArrayList<TripItem> fetchTripItems(ArrayList<String> ids) throws AuthenticationException;

  /**
   * Return list of comments which are modified after 'from' datetime.
   * 
   * @throws AuthenticationException
   */
  public ArrayList<Comment> fetchComments(ArrayList<String> ids) throws AuthenticationException;

  /**
   * Deletion of Trip.
   * 
   * @throws AuthenticationException
   */
  public void deleteTrip(Trip trip) throws AuthorizationException, AuthenticationException;

  /**
   * Deletion of Trip item.
   * 
   * @throws AuthenticationException
   * @throws TransactionFailedException
   */
  public TripItemUpdateResult deleteTripItem(TripItem item) throws AuthorizationException,
      AuthenticationException, TransactionFailedException;

  public CommentUpdateResult deleteComment(Comment comment) throws AuthorizationException,
      AuthenticationException, TransactionFailedException;

  public String sendInvite(String tripId, ArrayList<String> emails, boolean collaborator)
      throws IllegalArgumentException;

}
