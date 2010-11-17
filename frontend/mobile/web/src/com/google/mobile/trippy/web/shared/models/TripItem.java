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

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.NullValue;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * JDO: Venues for a Trip.
 * 
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class TripItem implements Serializable, IsSerializable {

  private static final long serialVersionUID = 5697367960089660141L;
  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  @Extension(vendorName = "datanucleus", key = "gae.encoded-pk", value = "true")
  private String key;
  @Persistent(nullValue = NullValue.EXCEPTION)
  private String tripId;
  // To be treated as unique id for local search result item.
  // Will help in identifying already existing item among the search results
  @Persistent(nullValue = NullValue.EXCEPTION)
  private String searchResultUrl;
  @Persistent(nullValue = NullValue.EXCEPTION)
  private String name;
  @Persistent
  private String description;
  @Persistent(nullValue = NullValue.EXCEPTION)
  private String address;
  @Persistent
  private String imageUrl;
  @Persistent(nullValue = NullValue.EXCEPTION)
  private Double latitude;
  @Persistent(nullValue = NullValue.EXCEPTION)
  private Double longitude;
  @Persistent(defaultFetchGroup = "true")
  private ArrayList<String> phoneNumbers = new ArrayList<String>();
  @Persistent
  private boolean visited;
  @Persistent
  private int startDay;
  @Persistent
  private Date startTime = new Date(0);
  @Persistent
  private int duration;
  @Persistent
  private int thumbsUp;
  @Persistent
  private int thumbsDown;
  @Persistent(nullValue = NullValue.EXCEPTION)
  private String ownerId;
  @Persistent(nullValue = NullValue.EXCEPTION)
  private String ownerName = "";
  @Persistent(nullValue = NullValue.EXCEPTION)
  private String category = POIType.GENERAL.toString();
  @Persistent(nullValue = NullValue.EXCEPTION)
  private String dataSource = "LP";
  @Persistent(nullValue = NullValue.EXCEPTION)
  private Date addedOn;
  @Persistent(nullValue = NullValue.EXCEPTION)
  private Date lastModified;
  // TODO: @Persistent(nullValue = NullValue.EXCEPTION)
  @Persistent(nullValue = NullValue.EXCEPTION)
  private String lastModifiedBy = null;
  @Persistent()
  private Status status = Status.ACTIVE;

  private boolean updated = false;
  private boolean commentsUpdated = false;

  public TripItem() {
    super();
    setPhoneNumbers(new ArrayList<String>());
    setStatus(Status.ACTIVE);
  }
  
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

  public final String getSearchResultUrl() {
    return searchResultUrl;
  }

  public final void setSearchResultUrl(String searchResultUrl) {
    this.searchResultUrl = searchResultUrl;
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

  public final String getAddress() {
    return address;
  }

  public final void setAddress(String address) {
    this.address = address;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public String getImageUrl() {
    return imageUrl;
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

  public final ArrayList<String> getPhoneNumbers() {
    return phoneNumbers;
  }

  public final void setPhoneNumbers(ArrayList<String> phoneNumbers) {
    this.phoneNumbers = phoneNumbers;
  }

  public final void addPhoneNumber(String phoneNumber) {
    if (phoneNumbers == null) {
      phoneNumbers = new ArrayList<String>();
    }
    phoneNumbers.add(phoneNumber);
  }

  public final boolean isVisited() {
    return visited;
  }

  public final void setVisited(boolean visited) {
    this.visited = visited;
  }

  public final int getStartDay() {
    return startDay;
  }

  public final void setStartDay(int startDay) {
    this.startDay = startDay;
  }

  public final void setStartDay(Long startDay) {
    if (startDay != null) {
      this.startDay = startDay.intValue();
    }
  }

  public final Date getStartTime() {
    return startTime;
  }

  public final void setStartTime(Date startTime) {
    this.startTime = startTime;
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
    return ownerName;
  }

  public final void setOwnerName(String ownerName) {
    this.ownerName = ownerName;
  }
  
  public final String getCategory() {
    return category;
  }

  public final void setCategory(String category) {
    this.category = category;
  }
  
  public final String getDataSource() {
    return dataSource;
  }

  public final void setDataSource(String dataSource) {
    this.dataSource = dataSource;
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

  public final String getLastModifiedBy() {
   return Utils.getDecodedBase64(lastModifiedBy);
  }

  public final void setLastModifiedBy(String lastModifiedBy) {
    this.lastModifiedBy = Utils.getEncodedBase64(lastModifiedBy);
  }

  public final boolean isUpdated() {
    return updated;
  }

  public final void setUpdated(boolean updated) {
    this.updated = updated;
  }

  public final boolean isCommentsUpdated() {
    return commentsUpdated;
  }

  public final void setCommentsUpdated(boolean commentsUpdated) {
    this.commentsUpdated = commentsUpdated;
  }

  public final Status getStatus() {
    return status;
  }

  public final void setStatus(Status status) {
    this.status = status;
  }

  public void setLatitude(Double latitude) {
    this.latitude = latitude;
  }

  public void setLongitude(Double longitude) {
    this.longitude = longitude;
  }

  @Override
  public boolean equals(Object object) {
    if (object == null || !(object instanceof TripItem)) {
      return false;
    }
    return getKey().equals(((TripItem) object).getKey());
  }

  @Override
  public int hashCode() {
    return getKey().hashCode();
  }

  @Override
  public String toString() {
    StringBuffer buffer = new StringBuffer();
    buffer.append("id=" + key);
    buffer.append(" , tripId=" + tripId);
    buffer.append(" , searchResultUrl=" + searchResultUrl);
    buffer.append(" , name=" + name);
    buffer.append(" , description=" + description);
    buffer.append(" , address=" + address);
    buffer.append(" , imageUrl=" + imageUrl);
    buffer.append(" , latitude=" + latitude);
    buffer.append(" , longitude=" + longitude);
    buffer.append(" , phoneNumbers=" + phoneNumbers);
    buffer.append(" , visited=" + visited);
    buffer.append(" , startDay=" + startDay);
    buffer.append(" , startTime=" + startTime);
    buffer.append(" , duration=" + duration);
    buffer.append(" , thumbsUp=" + thumbsUp);
    buffer.append(" , thumbsDown=" + thumbsDown);
    buffer.append(" , ownerId=" + ownerId);
    buffer.append(" , ownerName=" + ownerName);
    buffer.append(" , category=" + category);
    buffer.append(" , dataSource=" + dataSource);
    buffer.append(" , addedOn=" + addedOn);
    buffer.append(" , lastModified=" + lastModified);
    buffer.append(" , lastModifiedBy=" + lastModifiedBy);
    buffer.append(" , status=" + status.toString());
    buffer.append(" , updated=" + updated);
    buffer.append(" , commentsUpdated=" + commentsUpdated);
    return buffer.toString();
  }
  
  public TripItem copy() {
    final TripItem item = new TripItem();
    item.setKey(null);
    
    item.setAddedOn(this.getAddedOn());
    item.setAddress(this.getAddress());
    item.setCommentsUpdated(this.isCommentsUpdated());
    item.setDescription(this.getDescription());
    item.setDuration(this.getDuration());
    item.setImageUrl(this.getImageUrl());
    item.setLastModified(this.getLastModified());
    item.setLastModifiedBy(this.getLastModifiedBy());
    item.setLatitude(this.getLatitude());
    item.setLongitude(this.getLongitude());
    item.setName(this.getName());
    item.setOwnerId(this.getOwnerId());
    item.setOwnerName(this.getOwnerName());
    item.setCategory(this.getCategory());
    item.setDataSource(this.getDataSource());
    item.setPhoneNumbers(this.getPhoneNumbers());
    item.setSearchResultUrl(this.getSearchResultUrl());
    item.setStartDay(this.getStartDay());
    item.setStartTime(this.getStartTime());
    item.setStatus(this.getStatus());
    item.setThumbsDown(this.getThumbsDown());
    item.setThumbsUp(this.getThumbsUp());
    item.setTripId(this.getTripId());
    item.setUpdated(this.isUpdated());
    item.setVisited(this.isVisited());    
    return item;
  }
}
