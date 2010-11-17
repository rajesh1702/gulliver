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
import com.google.mobile.trippy.web.shared.models.Comment;
import com.google.mobile.trippy.web.shared.models.CommentUpdateResult;
import com.google.mobile.trippy.web.shared.models.IdDateTuple;
import com.google.mobile.trippy.web.shared.models.IdDateTupleList;
import com.google.mobile.trippy.web.shared.models.Status;
import com.google.mobile.trippy.web.shared.models.Trip;

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
public class CommentUtils {

  private final UserUtils userUtils = DefaultUserUtils.getInstance();
  
  CommentUpdateResult addComment(final Comment comment, final String userEmail)
      throws AuthorizationException, TransactionFailedException {
    Preconditions.checkArgument(!comment.getTripId().isEmpty() && !comment.getTripId().isEmpty());
    final Date currTime = new Date();
    final PersistenceManager pm = PMF.get().getPersistenceManager();
    // Fetch parent trip.
    final Trip trip = pm.detachCopy(pm.getObjectById(Trip.class, comment.getTripId()));
    // Authorize user against trip.
    if (!userUtils.isContributor(trip, userEmail)) {
      throw new AuthorizationException(userEmail);
    }
    // Set key with parent as trip.
    final Key commentKey = generateCommentKey(trip.getKey());
    comment.setKey(KeyFactory.keyToString(commentKey));
    // Set default values.
    comment.setOwnerId(userEmail);
    comment.setAddedOn(currTime);
    comment.setLastModified(currTime);
    comment.setLastModifiedBy(userEmail);
    comment.setStatus(Status.ACTIVE);
    
    // Add trip item to ordered list in trip.
    final IdDateTuple idDateTuple
        = new IdDateTuple(comment.getKey(), currTime);
    final IdDateTupleList idDateTupleList = trip.getCommentIds();
    idDateTupleList.getTuples().add(idDateTuple);
    trip.setCommentIds(idDateTupleList);
    // Update trip meta-data.
    trip.setLastModified(currTime);
    trip.setLastModifiedBy(userEmail);

    // Save trip and trip item in a transaction.
    Trip newTrip = null;
    Comment newComment = null;
    final Transaction tx = pm.currentTransaction();
    try {
      tx.begin();
      newTrip = pm.makePersistent(trip);
      newComment = pm.makePersistent(comment);
      tx.commit();
    } finally {
      if (tx.isActive()) {
        tx.rollback();
        throw new TransactionFailedException();
      }
    }
    final CommentUpdateResult commentUpdateResult
        = new CommentUpdateResult(pm.detachCopy(newTrip), pm.detachCopy(newComment));
    return commentUpdateResult;
  }
  
  CommentUpdateResult deleteComment(final Comment comment, final String userEmail)
      throws AuthorizationException, TransactionFailedException {
    final Date currTime = new Date();
    final PersistenceManager pm = PMF.get().getPersistenceManager();
    final Trip trip = pm.detachCopy(pm.getObjectById(Trip.class, comment.getTripId()));
    if (!userUtils.isOwner(trip, comment, userEmail)) {
      throw new AuthorizationException(userEmail);
    }
    
    comment.setStatus(Status.DELETED);
    comment.setLastModified(currTime);
    comment.setLastModifiedBy(userEmail);
    
    final IdDateTuple idDateTuple
        = new IdDateTuple(comment.getKey(), currTime);
    final IdDateTupleList idDateTupleList = trip.getCommentIds();
    idDateTupleList.getTuples().remove(idDateTuple);
    trip.setCommentIds(idDateTupleList);
    trip.setLastModified(currTime);
    trip.setLastModifiedBy(userEmail);
    
    // Save trip and trip item in a transaction.
    Trip newTrip = null;
    final Transaction tx = pm.currentTransaction();
    try {
      tx.begin();
      newTrip = pm.makePersistent(trip);
      final Comment toDelete = pm.getObjectById(Comment.class,
          KeyFactory.stringToKey(comment.getKey()));
      pm.deletePersistent(toDelete);
      tx.commit();
    } catch (Throwable e) {
      if (tx.isActive()) {
        tx.rollback();
        e.printStackTrace();
        throw new TransactionFailedException(e);
      }
    }
    final CommentUpdateResult commentUpdateResult
        = new CommentUpdateResult(pm.detachCopy(newTrip), comment);
    return commentUpdateResult;
  }

  @SuppressWarnings("unchecked")
  ArrayList<Comment> fetchComments(final ArrayList<String> ids) {
    if (ids == null || ids.isEmpty()) {
      return new ArrayList<Comment>();
    }
    final PersistenceManager pm = PMF.get().getPersistenceManager();
    final ArrayList<Key> keys = new ArrayList<Key>();
    for (String id : ids) {
      final Key k = KeyFactory.stringToKey(id);
      keys.add(k);
    }
    Query q = pm.newQuery("select from " + Comment.class.getName() + " where key == :keys");
    ArrayList<Comment> items = (ArrayList<Comment>) pm.detachCopyAll(
        (List<Comment>) q.execute(keys));
    return items;
  }
  
  Key generateCommentKey(final String parentKeyS) {
    final Key parentKey = KeyFactory.stringToKey(parentKeyS);
    final DatastoreService service = DatastoreServiceFactory.getDatastoreService();
    return service.allocateIds(parentKey, Comment.class.getSimpleName(), 1).getStart();
  }
}
