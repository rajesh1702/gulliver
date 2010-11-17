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
import com.google.mobile.trippy.web.shared.models.Status;
import com.google.mobile.trippy.web.shared.models.TripItem;

import java.util.ArrayList;
import java.util.Date;

/**
 * TripItem JSO, used to serialize/deserialize trip item for client HTML5 storage.. 
 * 
 */
public class JsoTripItem extends JavaScriptObject {
  
  protected JsoTripItem() {}
  
  public final native String getKey() /*-{
    return this.key;
  }-*/;

  public final native void setKey(String key) /*-{
    this.key = key;
  }-*/;
  
  public final native String getTripId() /*-{
    return this.tripId;
  }-*/;

  public final native void setTripId(String tripId) /*-{
    this.tripId = tripId;
  }-*/;

  public final native String getSearchResultUrl() /*-{
    return this.searchResultUrl;
  }-*/;

  public final native void setSearchResultUrl(String searchResultUrl) /*-{
    this.searchResultUrl = searchResultUrl;
  }-*/;

  public final native String getName() /*-{
    return this.name;
  }-*/;

  public final native void setName(String name) /*-{
    this.name = name;
  }-*/;

  public final native String getDescription() /*-{
    return this.description;
  }-*/;

  public final native void setDescription(String description) /*-{
    this.description = description;
  }-*/;

  public final native String getAddress() /*-{
    return this.address;
  }-*/;

  public final native void setAddress(String address) /*-{
    this.address = address;
  }-*/;

  public final native void setImageUrl(String imageUrl) /*-{
    this.imageUrl = imageUrl;
  }-*/;

  public final native String getImageUrl() /*-{
    return this.imageUrl;
  }-*/;
  
  public final native double getLatitude() /*-{
    return this.latitude;
  }-*/;

  public final native void setLatitude(double latitude) /*-{
    this.latitude = latitude;
  }-*/;

  public final native double getLongitude() /*-{
    return this.longitude;
  }-*/;

  public final native void setLongitude(double longitude) /*-{
    this.longitude = longitude;
  }-*/;

  public final native JsArrayString getPhoneNumbers_() /*-{
    return this.phoneNumbers;
  }-*/;
  
  public final ArrayList<String> getPhoneNumbers() {
    ArrayList<String> parr = new ArrayList<String> ();
    JsArrayString jarr = getPhoneNumbers_();
    for (int i = 0; i < jarr.length(); ++i) {
      parr.add(jarr.get(i));
    }
    return parr;
  }

  public final native void setPhoneNumbers_(JsArrayString phoneNumbers) /*-{
    this.phoneNumbers = phoneNumbers;
  }-*/;
  
  public final void setPhoneNumbers(ArrayList<String> phoneNumbers) {
    JsArrayString jarr = (JsArrayString) JavaScriptObject.createArray();
    for (String ph: phoneNumbers) {
      jarr.push(ph);
    }
    setPhoneNumbers_(jarr);
  }

  public final native boolean isVisited() /*-{
    return this.visited;
  }-*/;

  public final native void setVisited(boolean visited) /*-{
    this.visited = visited;
  }-*/;

  private final native int getStartDay() /*-{
    return this.startDay;
  }-*/;

  private final native void setStartDay(int startDay) /*-{
    this.startDay = startDay;
  }-*/;

  private final native double getStartTime_() /*-{
    return this.startTime;
  }-*/;

  private final native void setStartTime_(double startTime) /*-{
    this.startTime = startTime;
  }-*/;
  
  public final Date getStartTime() {
    return new Date((long) getStartTime_());
  }
  
  public final void setStartTime(Date startTime) {
    setStartTime_(startTime.getTime());
  }

  public final native int getDuration() /*-{
    return this.duration;
  }-*/;

  public final native void setDuration(int duration) /*-{
    this.duration = duration;
  }-*/;

  public final native int getThumbsUp() /*-{
    return this.thumbsUp;
  }-*/;

  public final native void setThumbsUp(int thumbsUp) /*-{
    this.thumbsUp = thumbsUp;
  }-*/;

  public final native int getThumbsDown() /*-{
    return this.thumbsDown;
  }-*/;

  public final native void setThumbsDown(int thumbsDown) /*-{
    this.thumbsDown = thumbsDown;
  }-*/;

  public final native String getOwnerId() /*-{
    return this.ownerId;
  }-*/;

  public final native void setOwnerId(String ownerId) /*-{
    this.ownerId = ownerId;
  }-*/;

  public final native String getOwnerName() /*-{
    return this.ownerName;
  }-*/;
  
  public final native void setOwnerName(String ownerName) /*-{
    this.ownerName = ownerName;
  }-*/;
  
  public final native String getCategory() /*-{
    return this.category;
  }-*/;
  
  public final native void setCategory(String category) /*-{
    this.category = category;
  }-*/;

  public final native String getDataSource() /*-{
    return this.dataSource;
  }-*/;
  
  public final native void setDataSource(String dataSource) /*-{
    this.dataSource = dataSource;
  }-*/;

  private final native double getAddedOn_() /*-{
    return this.addedOn;
  }-*/;
  
  public final Date getAddedOn() {
    return new Date((long) getAddedOn_());
  }

  private final native void setAddedOn_(double addedOn) /*-{
    this.addedOn = addedOn;
  }-*/;
  
  public final void setAddedOn(Date addedOn) {
    setAddedOn_(addedOn.getTime());
  }

  private final native double getLastModified_() /*-{
    return this.lastModified;
  }-*/;
  
  public final Date getLastModified() {
    return new Date((long) getLastModified_());
  }

  private final native void setLastModified_(double lastModified) /*-{
    this.lastModified = lastModified;
  }-*/;
  
  public final void setLastModified(Date lastModified) {
    setLastModified_(lastModified.getTime());
  }

  public final native String getLastModifiedBy() /*-{
    return this.lastModifiedBy;
  }-*/;

  public final native void setLastModifiedBy(String lastModifiedBy) /*-{
    this.lastModifiedBy = lastModifiedBy;
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

  public final native void setUpdated(boolean updated) /*-{
    this.updated = updated;
  }-*/;

  public final native boolean isUpdated() /*-{
    return this.updated;
  }-*/;

  public final native void setCommentsUpdated(boolean commentsUpdated) /*-{
    this.commentsUpdated = commentsUpdated;
  }-*/;

  public final native boolean isCommentsUpdated() /*-{
    return this.commentsUpdated;
  }-*/;
  
  public final void setTripItem(TripItem tripItem) {
    setKey(nullToEmpty(tripItem.getKey()));
    setTripId(nullToEmpty(tripItem.getTripId()));
    setSearchResultUrl(nullToEmpty(tripItem.getSearchResultUrl()));
    setName(nullToEmpty(tripItem.getName()));
    setDescription(nullToEmpty(tripItem.getDescription()));
    setAddress(nullToEmpty(tripItem.getAddress()));
    setImageUrl(nullToEmpty(tripItem.getImageUrl()));
    setLatitude(nullToEmpty(tripItem.getLatitude()));
    setLongitude(nullToEmpty(tripItem.getLongitude()));
    setPhoneNumbers(nullToEmpty(tripItem.getPhoneNumbers()));
    setVisited(nullToEmpty(tripItem.isVisited()));
    setStartDay(nullToEmpty(tripItem.getStartDay()));
    setStartTime(nullToEmpty(tripItem.getStartTime()));
    setDuration(nullToEmpty(tripItem.getDuration()));
    setThumbsUp(nullToEmpty(tripItem.getThumbsUp()));
    setThumbsDown(nullToEmpty(tripItem.getThumbsDown()));
    setUpdated(nullToEmpty(tripItem.isUpdated()));
    setCommentsUpdated(nullToEmpty(tripItem.isCommentsUpdated()));
    setOwnerId(nullToEmpty(tripItem.getOwnerId()));
    setOwnerName(nullToEmpty(tripItem.getOwnerName()));
    setCategory(nullToEmpty(tripItem.getCategory()));
    setDataSource(nullToEmpty(tripItem.getDataSource()));
    setAddedOn(nullToEmpty(tripItem.getAddedOn()));
    setLastModified(nullToEmpty(tripItem.getLastModified()));
    setLastModifiedBy(nullToEmpty(tripItem.getLastModifiedBy()));
    setStatus(nullToEmpty(tripItem.getStatus()));
    setUpdated(nullToEmpty(tripItem.isUpdated()));
    setCommentsUpdated(nullToEmpty(tripItem.isCommentsUpdated()));
  }
  
  public final TripItem getTripItem() {
    TripItem tripItem = new TripItem();
    tripItem.setKey(getKey());
    tripItem.setTripId( getTripId());
    tripItem.setSearchResultUrl(getSearchResultUrl());
    tripItem.setName(getName());
    tripItem.setDescription(getDescription());
    tripItem.setAddress(getAddress());
    tripItem.setImageUrl(getImageUrl());
    tripItem.setLatitude(getLatitude());
    tripItem.setLongitude(getLongitude());
    tripItem.setPhoneNumbers(getPhoneNumbers());
    tripItem.setVisited(isVisited());
    tripItem.setStartDay(getStartDay());
    tripItem.setStartTime(getStartTime());
    tripItem.setDuration(getDuration());
    tripItem.setThumbsUp(getThumbsUp());
    tripItem.setThumbsDown(getThumbsDown());
    tripItem.setOwnerId(getOwnerId());
    tripItem.setOwnerName(getOwnerName());
    tripItem.setCategory(getCategory());
    tripItem.setDataSource(getDataSource());
    tripItem.setAddedOn(getAddedOn());
    tripItem.setLastModified(getLastModified());
    tripItem.setLastModifiedBy(getLastModifiedBy());
    tripItem.setStatus(getStatus());
    tripItem.setUpdated(isUpdated());
    tripItem.setCommentsUpdated(isCommentsUpdated());
    return tripItem;
  }
  
  private final String nullToEmpty(String string) {
    if (string == null) {
      return "";
    }
    return string;
  }
  
  private final double nullToEmpty(double f) {
    return f;
  }
  
  private final ArrayList<String> nullToEmpty(ArrayList<String> stringList) {
    if (stringList == null) {
      return new ArrayList<String>();
    }
    return stringList;
  }
  
  private final boolean nullToEmpty(boolean b) {
    return b;
  }
  
  private final int nullToEmpty(int i) {
    return i;
  }
  
  private final Date nullToEmpty(Date date) {
    if (date == null) {
      return new Date();
    }
    return date;
  }
  
  private Status nullToEmpty(Status status) {
    if (status == null) {
      return Status.ACTIVE;
    }
    return status;
  }
}
