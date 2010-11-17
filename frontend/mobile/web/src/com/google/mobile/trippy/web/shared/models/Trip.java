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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.NullValue;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * JDO: Trip datastore class.
 * 
 */
@SuppressWarnings("serial")
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Trip implements Serializable, IsSerializable {

  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  @Extension(vendorName = "datanucleus", key = "gae.encoded-pk", value = "true")
  private String key;
  @Persistent(nullValue = NullValue.EXCEPTION)
  private String name;
  @Persistent
  private String description;
  @Persistent(nullValue = NullValue.EXCEPTION)
  private String location;
  @Persistent(nullValue = NullValue.EXCEPTION)
  private Double latitude;
  @Persistent(nullValue = NullValue.EXCEPTION)
  private Double longitude;

  // LP data.
  @Persistent(nullValue = NullValue.EXCEPTION)
  private Long placeId;
  @Persistent(nullValue = NullValue.EXCEPTION)
  private Double northLatitude = null;
  @Persistent(nullValue = NullValue.EXCEPTION)
  private Double southLatitude = null;
  @Persistent(nullValue = NullValue.EXCEPTION)
  private Double eastLongitude = null;
  @Persistent(nullValue = NullValue.EXCEPTION)
  private Double westLongitude = null;

  @Persistent
  private Date startDate;
  @Persistent
  private int duration;
  @Persistent
  private Long version = 0L;
  @Persistent
  private int thumbsUp;
  @Persistent
  private int thumbsDown;
  // TODO: @Persistent(nullValue = NullValue.EXCEPTION)
  @Persistent
  private String ownerId;
  @Persistent
  private String ownerName = "";
  // TODO: @Persistent(nullValue = NullValue.EXCEPTION)
  @Persistent(defaultFetchGroup = "true")
  private HashSet<String> contributorIds = new HashSet<String>();
  @Persistent(defaultFetchGroup = "true")
  private HashSet<String> viewerIds = new HashSet<String>();
  @Persistent(nullValue = NullValue.EXCEPTION)
  private Date addedOn;
  @Persistent(nullValue = NullValue.EXCEPTION)
  private Date lastModified;
  @Persistent(nullValue = NullValue.EXCEPTION)
  private String lastModifiedBy;
  @Persistent
  private Status status;

  @Persistent(serialized = "true", defaultFetchGroup = "true")
  private IdDayDateTupleList tripItemIds = new IdDayDateTupleList();
  @Persistent(serialized = "true", defaultFetchGroup = "true")
  private IdDateTupleList commentIds = new IdDateTupleList();

  @NotPersistent
  private boolean updated;
  @NotPersistent
  private boolean commentsUpdated;

  public Trip() {
    contributorIds = new HashSet<String>();
    setStatus(Status.ACTIVE);
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public final String getName() {
    return name;
  }

  public final void setName(String name) {
    this.name = name;
  }

  public final String getDescription() {
    return description;
  }

  public final void setDescription(String description) {
    this.description = description;
  }

  public final String getLocation() {
    return location;
  }

  public final void setLocation(String location) {
    this.location = location;
  }

  public final double getLatitude() {
    return latitude;
  }

  public final void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public final double getLongitude() {
    return longitude;
  }

  public final void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public final Long getPlaceId() {
    return placeId;
  }

  public final void setPlaceId(Long placeId) {
    this.placeId = placeId;
  }

  public final Double getNorthLatitude() {
    return northLatitude;
  }

  public final void setNorthLatitude(Double northLatitude) {
    this.northLatitude = northLatitude;
  }

  public final Double getSouthLatitude() {
    return southLatitude;
  }

  public final void setSouthLatitude(Double southLatitude) {
    this.southLatitude = southLatitude;
  }

  public final Double getEastLongitude() {
    return eastLongitude;
  }

  public final void setEastLongitude(Double eastLongitude) {
    this.eastLongitude = eastLongitude;
  }

  public final Double getWestLongitude() {
    return westLongitude;
  }

  public final void setWestLongitude(Double westLongitude) {
    this.westLongitude = westLongitude;
  }

  public final Date getStartDate() {
    return startDate;
  }

  public final void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public final int getDuration() {
    return duration;
  }

  public final void setDuration(int duration) {
    this.duration = duration;
  }

  public final void setDuration(Long duration) {
    if (duration != null) {
      this.duration = duration.intValue();
    }
  }

  public final Long getVersion() {
    return version;
  }

  public final void setVersion(Long version) {
    this.version = version;
  }
  
  public final int getThumbsUp() {
    return thumbsUp;
  }

  public final void setThumbsUp(int thumbsUp) {
    this.thumbsUp = thumbsUp;
  }

  public final void setThumbsUp(Long thumbsUp) {
    if (thumbsUp != null) {
      this.thumbsUp = thumbsUp.intValue();
    }
  }

  public final int getThumbsDown() {
    return thumbsDown;
  }

  public final void setThumbsDown(int thumbsDown) {
    this.thumbsDown = thumbsDown;
  }

  public final void setThumbsDown(Long thumbsDown) {
    if (thumbsDown != null) {
      this.thumbsDown = thumbsDown.intValue();
    }
  }

  public final String getOwnerId() {
    return Utils.getDecodedBase64(ownerId);
  }

  public final void setOwnerId(String ownerId) {
    this.ownerId = Utils.getEncodedBase64(ownerId);
  }

  public final String getOwnerName() {
    return ownerId;
  }

  public final void setOwnerName(String ownerName) {
    this.ownerName = ownerName;
  }

  public final HashSet<String> getContributorIds() {
    if (contributorIds == null) {
      contributorIds = new HashSet<String>();
    }
    
    HashSet<String> decodedContributorIds = new HashSet<String>();
    for (String contributorId : contributorIds) {
      decodedContributorIds.add(Utils.getDecodedBase64(contributorId));
    }
    
    return decodedContributorIds;
  }

  public final void setContributorIds(HashSet<String> contributorIds) {
    HashSet<String> encodedContributorIds = new HashSet<String>();
    for (String contributorId : contributorIds) {
      encodedContributorIds.add(Utils.getEncodedBase64(contributorId));
    }
    this.contributorIds = encodedContributorIds;
  }

  public final void setContributorIds(ArrayList<String> contributorIds) {
    setContributorIds(new HashSet<String>(contributorIds));
  }

  public final void addContributorId(String contributor) {
    if (contributorIds == null) {
      contributorIds = new HashSet<String>();
    }
    // To decode ids before adding new decoded ids.
    contributorIds = this.getContributorIds();
    contributorIds.add(contributor);
    setContributorIds(contributorIds);
  }

  public final HashSet<String> getViewerIds() {
    if (viewerIds == null) {
      viewerIds = new HashSet<String>();
    }
    
    HashSet<String> decodedViewerIds = new HashSet<String>();
    for (String viewerId : viewerIds) {
      decodedViewerIds.add(Utils.getDecodedBase64(viewerId));
    }

    return decodedViewerIds;
  }

  public final void setViewerIds(HashSet<String> viewerIds) {
    HashSet<String> encodedViewerIds = new HashSet<String>();
    for (String viewerId : viewerIds) {
      encodedViewerIds.add(Utils.getEncodedBase64(viewerId));
    }
    this.viewerIds = encodedViewerIds;
  }

  public final void setViewerIds(ArrayList<String> viewerIds) {
    setViewerIds(new HashSet<String>(viewerIds));
  }

  public final void addViewerId(String viewer) {
    if (viewerIds == null) {
      viewerIds = new HashSet<String>();
    }
    // To decode ids before adding new decoded ids.
    viewerIds = this.getViewerIds();
    viewerIds.add(viewer);
    setViewerIds(viewerIds);
  }

  public final Date getAddedOn() {
    return addedOn;
  }

  public final void setAddedOn(Date addedOn) {
    this.addedOn = addedOn;
  }

  public final Date getLastModified() {
    return lastModified;
  }

  public final void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }

  public void setUpdated(boolean updated) {
    this.updated = updated;
  }

  public boolean isUpdated() {
    return updated;
  }

  public void setCommentsUpdated(boolean commentsUpdated) {
    this.commentsUpdated = commentsUpdated;
  }

  public boolean isCommentsUpdated() {
    return this.commentsUpdated;
  }

  public final String getLastModifiedBy() {
    return Utils.getDecodedBase64(lastModifiedBy);
  }

  public final void setLastModifiedBy(String lastModifiedBy) {
    this.lastModifiedBy = Utils.getEncodedBase64(lastModifiedBy);
  }

  public final Status getStatus() {
    return status;
  }

  public final void setStatus(Status status) {
    this.status = status;
  }

  public IdDayDateTupleList getTripItemIds() {
    return tripItemIds;
  }

  public void setLatitude(Double latitude) {
    this.latitude = latitude;
  }

  public void setLongitude(Double longitude) {
    this.longitude = longitude;
  }

  public void setTripItemIds(IdDayDateTupleList tripItemIds) {
    this.tripItemIds = tripItemIds;
  }

  public IdDateTupleList getCommentIds() {
    return commentIds;
  }

  public void setCommentIds(IdDateTupleList commentIds) {
    this.commentIds = commentIds;
  }

  @Override
  public boolean equals(Object object) {

    if (object == null || !(object instanceof Trip)) {
      return false;
    }
    return getKey().equals(((Trip) object).getKey());
  }

  @Override
  public int hashCode() {
    return getKey().hashCode();
  }

  @Override
  public String toString() {
    StringBuffer buffer = new StringBuffer();
    buffer.append("id=" + key);
    buffer.append(" , name=" + name);
    buffer.append(" , description=" + description);
    buffer.append(" , location=" + location);
    buffer.append(" , latitude=" + latitude);
    buffer.append(" , longitude=" + longitude);
    buffer.append(" , placeId=" + placeId);
    buffer.append(" , northLatitude=" + northLatitude);
    buffer.append(" , southLatitude=" + southLatitude);
    buffer.append(" , eastLongitude=" + eastLongitude);
    buffer.append(" , westLongitude=" + westLongitude);
    buffer.append(" , startDate=" + startDate);
    buffer.append(" , duration=" + duration);
    buffer.append(" , version=" + version);
    buffer.append(" , thumbsUp=" + thumbsUp);
    buffer.append(" , thumbsDown=" + thumbsDown);
    buffer.append(" , ownerId=" + ownerId);
    buffer.append(" , ownerName=" + ownerName);
    buffer.append(" , contributorIds=" + contributorIds);
    buffer.append(" , viewerIds=" + viewerIds);
    buffer.append(" , addedOn=" + addedOn);
    buffer.append(" , lastModified=" + lastModified);
    buffer.append(" , lastModifiedBy=" + lastModifiedBy);
    buffer.append(" , status=" + status.toString());
    buffer.append(" , updated=" + updated);
    buffer.append(" , commentsUpdated=" + commentsUpdated);

    return buffer.toString();
  }

  public Trip copy() {
    final Trip trip = new Trip();
    trip.setKey(null);

    trip.setLocation(this.getLocation());
    trip.setDuration(this.getDuration());
    trip.setVersion(this.getVersion());
    trip.setLatitude(this.getLatitude());
    trip.setLongitude(this.getLongitude());
    trip.setPlaceId(this.getPlaceId());
    trip.setNorthLatitude(this.getNorthLatitude());
    trip.setSouthLatitude(this.getSouthLatitude());
    trip.setEastLongitude(this.getEastLongitude());
    trip.setWestLongitude(this.getWestLongitude());
    trip.setStartDate(this.getStartDate());
    trip.setLastModified(this.getLastModified());
    trip.setAddedOn(this.getAddedOn());
    trip.setName(this.getName());
    trip.setDescription(this.getDescription());
    trip.setOwnerId(this.getOwnerId());
    trip.setOwnerName(this.getOwnerName());
    trip.setThumbsDown(this.getThumbsDown());
    trip.setThumbsUp(this.getThumbsUp());
    trip.setContributorIds(this.getContributorIds());
    trip.setViewerIds(this.getViewerIds());
    trip.setCommentIds(this.getCommentIds());
    trip.setCommentsUpdated(this.isCommentsUpdated());
    trip.setLastModifiedBy(this.getLastModifiedBy());
    trip.setStatus(this.getStatus());
    trip.setTripItemIds(this.getTripItemIds());
    trip.setUpdated(this.isUpdated());
    return trip;
  }
}
