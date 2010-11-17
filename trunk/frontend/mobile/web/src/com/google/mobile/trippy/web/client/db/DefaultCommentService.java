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
import com.google.mobile.trippy.web.shared.models.Comment;
import com.google.mobile.trippy.web.shared.models.CommentUpdateResult;
import com.google.mobile.trippy.web.shared.models.Trip;

import java.util.ArrayList;

/**
 * Add, delete and sync trip item comments.
 * 
 */
public class DefaultCommentService implements CommentService {

  private final RemoteTripServiceAsync remoteTripService =
      (RemoteTripServiceAsync) GWT.create(RemoteTripService.class);
  private final Message message = (Message) GWT.create(Message.class);
  private final LocalDbManager localDbManager;
  private final SyncManager syncManager;

  public DefaultCommentService(HandlerManager eventBus) {
    super();
    this.localDbManager = new LocalDbManager(eventBus, DefaultUtils.getInstance());
    syncManager = new SyncManager(localDbManager);
  }

  /**
   * Add comment to the remote and local db.
   * 
   * If writes to remote db are successful, then the comment is pushed to main
   * index of local db. Else the comment is push to pending index for later
   * retries.
   */
  @Override
  public void addComment(final Comment comment, final AsyncCallback<Comment> callback)
      throws AuthorizationException {
    Preconditions.checkArgument(comment.getKey() == null || comment.getKey().isEmpty());
    
    final Trip trip = localDbManager.getTrip(comment.getTripId());
    // Check user authorization
    if (!DefaultUserUtils.getInstance().isViewer(
        trip, DefaultUtils.getInstance().getUserEmail())) {
      throw new AuthorizationException(message.unauthorizedAdd("comment"));
    }

    // Save at server.
    remoteTripService.addComment(comment, new AsyncCallback<CommentUpdateResult>() {


      @Override
      public void onSuccess(final CommentUpdateResult resultComment) {
        localDbManager.updateTrip(resultComment.getTrip());
        localDbManager.addComment(resultComment.getComment());

        if(callback != null) {
          callback.onSuccess(resultComment.getComment());
        }
        syncManager.sync(resultComment.getTrip(), null);
      }

      @Override
      public void onFailure(final Throwable caught) {
        // if Current user was not authorized to add comment
        if (caught instanceof AuthenticationException) {
          DefaultUtils.getInstance().redirect(
              DefaultUtils.getInstance().getLoginUrl(Location.getHref()));
        }
        if(callback != null) {
          callback.onFailure(caught);
        }
      }
    });
  }

  /**
   * Delete comment from remote and local db.
   */
  @Override
  public void deleteComment(final Comment comment, final AsyncCallback<Void> callback)
      throws AuthorizationException {

    Preconditions.checkArgument(!comment.getKey().isEmpty());
    
    final Trip trip = localDbManager.getTrip(comment.getTripId());
    // Check if current user is authorized to delete the comment or not
    if (!DefaultUserUtils.getInstance().isOwner(
        trip, comment, DefaultUtils.getInstance().getUserEmail())) {
      throw new AuthorizationException(message.unauthorizedDelete("comment"));
    }

    remoteTripService.deleteComment(comment, new AsyncCallback<CommentUpdateResult>() {

      /**
       * Remove trip from local db and main index.
       */
      @Override
      public void onSuccess(final CommentUpdateResult resultComment) {
        localDbManager.updateTrip(resultComment.getTrip());
        localDbManager.deleteComment(comment.getTripId(), comment.getTripItemId(), comment.getKey());
        if(callback != null) {
          callback.onSuccess(null);
        }
        syncManager.sync(resultComment.getTrip(), null);
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
        if(callback != null) {
          callback.onFailure(caught);
        }
      }
    });
  }

  @Override
  public Comment getComment(String id) {
    return localDbManager.getComment(id);
  }

  @Override
  public ArrayList<Comment> getComments(String tripId, String tripItemId) {
    return localDbManager.getComments(tripId, tripItemId);
  }
}
