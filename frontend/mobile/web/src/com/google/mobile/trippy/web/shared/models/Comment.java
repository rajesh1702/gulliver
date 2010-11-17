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

package com.google.mobile.trippy.web.shared.models;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.mobile.trippy.web.shared.Utils;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * JDO: Comment for a Trip or its Items.
 * 
 */
@SuppressWarnings("serial")
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Comment implements Serializable, IsSerializable {
  
  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  @Extension(vendorName="datanucleus", key="gae.encoded-pk", value="true")
  private String key;
  @Persistent
  private String tripId;
  @Persistent
  private String tripItemId;
  @Persistent
  private String comment;
  @Persistent
  private String userId;
  @Persistent
  private Status status;
  @Persistent
  private String ownerId;
  @Persistent
  private Date lastModified;
  @Persistent
  private String lastModifiedBy;
  @Persistent
  private Date addedOn;

  public final String getKey() {
    return key;
  }

  public final void setKey(String key) {
    this.key = key;
  }

  public final String getTripId() {
    return tripId;
  }

  public final void setTripId(String tripId) {
    this.tripId = tripId;
  }

  public final String getTripItemId() {
    return tripItemId;
  }

  public final void setTripItemId(String tripItemId) {
    this.tripItemId = tripItemId;
  }

  public final String getComment() {
    return comment;
  }

  public final void setComment(String comment) {
    this.comment = comment;
  }

  public final String getUserId() {
    return Utils.getDecodedBase64(userId);
  }

  public final void setUserId(String userId) {
    this.userId = Utils.getEncodedBase64(userId);
  }

  public final Status getStatus() {
    return status;
  }

  public final void setStatus(Status status) {
    this.status = status;
  }

  public final String getOwnerId() {
    return Utils.getDecodedBase64(ownerId);
  }

  public final void setOwnerId(String ownerId) {
    this.ownerId = Utils.getEncodedBase64(ownerId);
  }

  public final Date getLastModified() {
    return lastModified;
  }

  public final void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }

  public final String getLastModifiedBy() {
    return Utils.getDecodedBase64(lastModifiedBy);
  }

  public final void setLastModifiedBy(String lastModifiedBy) {
    this.lastModifiedBy = Utils.getEncodedBase64(lastModifiedBy);
  }

  public final Date getAddedOn() {
    return addedOn;
  }

  public final void setAddedOn(Date addedOn) {
    this.addedOn = addedOn;
  }

  @Override
  public boolean equals(Object object) {
    
    if (object == null || !(object instanceof Comment)) {
      return false;
    }
    return getKey().equals(((Comment) object).getKey());
  }

  @Override
  public int hashCode() {
    return getKey().hashCode();
  }

  @Override
  public String toString() {
    StringBuffer buffer = new StringBuffer();
    buffer.append("id=" + getKey());
    buffer.append(" , tripId=" + tripId);
    buffer.append(" , tripItemId=" + tripItemId);
    buffer.append(" , comment=" + comment);
    buffer.append(" , userId=" + userId);
    buffer.append(" , status=" + status);
    buffer.append(" , ownerId=" + ownerId);
    buffer.append(" , lastModified=" + lastModified);
    buffer.append(" , lastModifiedBy=" + lastModifiedBy);
    buffer.append(" , addedOn=" + addedOn);
    
    return buffer.toString();
  }
}
