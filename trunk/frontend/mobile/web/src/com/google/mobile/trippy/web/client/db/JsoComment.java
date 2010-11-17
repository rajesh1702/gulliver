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
import com.google.mobile.trippy.web.shared.models.Comment;
import com.google.mobile.trippy.web.shared.models.Status;

import java.util.Date;

/**
 * Comment JSO, used to serialize/deserialize trip/trip item comment for client HTML5 storage.. 
 * 
 */
public class JsoComment extends JavaScriptObject {

  protected JsoComment() {}
  
  private final native String getKey() /*-{
    return this.key;
  }-*/;

  private final native void setKey(String key) /*-{
    this.key = key;
  }-*/;
  
  private final native String getTripId() /*-{
    return this.tripId;
  }-*/;

  private final native void setTripId(String tripId) /*-{
    this.tripId = tripId;
  }-*/;

  private final native String getTripItemId() /*-{
    return this.tripItemId;
  }-*/;

  private final native void setTripItemId(String tripItemId) /*-{
    this.tripItemId = tripItemId;
  }-*/;

  private final native String getComment_() /*-{
    return this.comment;
  }-*/;

  private final native void setComment_(String comment) /*-{
    this.comment = comment;
  }-*/;

  private final native String getUserId() /*-{
    return this.userId;
  }-*/;

  private final native void setUserId(String userId) /*-{
    this.userId = userId;
  }-*/;
  
  private final native String getStatus_() /*-{
    return this.status;
  }-*/;
  
  public final Status getStatus() {
    return Status.valueOf(getStatus_());
  }
  
  private final native void setStatus_(String status) /*-{
    this.status = status;
  }-*/;
  
  public final void setStatus(Status status) {
    setStatus_(status.toString());
  }

  private final native double getAddedOn_() /*-{
    return this.addedOn;
  }-*/;

  private final native void setAddedOn_(double addedOn) /*-{
    this.addedOn = addedOn;
  }-*/;
  
  private final Date getAddedOn() {
    return new Date((long) getAddedOn_());
  }
  
  private final void setAddedOn(Date addedOn) {
    setAddedOn_(addedOn.getTime());
  }

  private final native double getLastModified_() /*-{
    return this.lastModified;
  }-*/;

  private final native void setLastModified_(double lastModified) /*-{
    this.lastModified = lastModified;
  }-*/;
  
  private final Date getLastModified() {
    return new Date((long) getLastModified_());
  }
  
  private final void setLastModified(Date lastModified) {
    setLastModified_(lastModified.getTime());
  }
  
  private final native String getLastModifiedBy() /*-{
    return this.lastModifiedBy;
  }-*/;
  
  private final native void setLastModifiedBy(String lastModifiedBy) /*-{
    this.lastModifiedBy = lastModifiedBy;
  }-*/;
  
  private final native String getOwnerId() /*-{
    return this.ownerId;
  }-*/;
  
  private final native void setOwnerId(String ownerId) /*-{
    this.ownerId = ownerId;
  }-*/;
  
  public final void setComment(Comment comment) {
    setKey(comment.getKey());
    setTripId(comment.getTripId());
    setTripItemId(comment.getTripItemId());
    setComment_(comment.getComment());
    setUserId(comment.getUserId());
    setOwnerId(comment.getOwnerId());
    setStatus(comment.getStatus());
    setAddedOn(comment.getAddedOn());
    setLastModified(comment.getLastModified());
    setLastModifiedBy(comment.getLastModifiedBy());
  }
  
  public final Comment getComment() {
    Comment comment = new Comment();
    comment.setKey(getKey());
    comment.setTripId(getTripId());
    comment.setTripItemId(getTripItemId());
    comment.setComment(getComment_());
    comment.setUserId(getUserId());
    comment.setStatus(getStatus());
    comment.setOwnerId(getOwnerId());
    comment.setAddedOn(getAddedOn());
    comment.setLastModified(getLastModified());
    comment.setLastModifiedBy(getLastModifiedBy());
    return comment;
  }
}
