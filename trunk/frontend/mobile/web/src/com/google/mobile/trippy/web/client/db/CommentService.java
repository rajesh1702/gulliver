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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.mobile.trippy.web.shared.exception.AuthorizationException;
import com.google.mobile.trippy.web.shared.models.Comment;

import java.util.ArrayList;

/**
 * Add, delete and sync trip item comments.
 * 
 */
public interface CommentService {

  /**
   * Add comment to the remote and local db.
   * 
   * If writes to remote db are successful, then the comment is pushed to main
   * index of local db. Else the comment is push to pending index for later
   * retries.
   */
  public void addComment(final Comment comment, final AsyncCallback<Comment> callback)
      throws AuthorizationException;

  /**
   * Delete comment from remote and local db.
   */
  public void deleteComment(final Comment comment, final AsyncCallback<Void> callback)
      throws AuthorizationException;

  /**
   * Get comments by trip and trip item id.
   */
  public ArrayList<Comment> getComments(final String tripId, final String tripItemId);
  
  public Comment getComment(String id);
  
}
