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
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.mobile.trippy.web.shared.models.IdDateTuple;
import com.google.mobile.trippy.web.shared.models.IdDateTupleList;
import com.google.mobile.trippy.web.shared.models.IdDayDateTuple;
import com.google.mobile.trippy.web.shared.models.IdDayDateTupleList;
import com.google.mobile.trippy.web.shared.models.Status;
import com.google.mobile.trippy.web.shared.models.Trip;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

/**
 * Trip JSO, used to serialize/deserialize trip for client HTML5 storage..
 * 
 */
public class JsoTrip extends JavaScriptObject {

  /**
   * Wrapper for java script objects
   */
  public static class JsoIdDateTuple extends JavaScriptObject {

    protected JsoIdDateTuple() {
    }

    public final native String getId() /*-{
      return this.id;
    }-*/;

    public final native void setId(String id) /*-{
      this.id = id;
    }-*/;

    public final native String getDate() /*-{
      return this.date;
    }-*/;

    public final native void setDate(String date) /*-{
      this.date = date;
    }-*/;
  }

  /**
   * Wrapper for java script objects
   */
  public static final class JsoIdDayDateTuple extends JavaScriptObject {

    protected JsoIdDayDateTuple() {
    }

    public final native String getId() /*-{
      return this.id;
    }-*/;

    public final native void setId(String id) /*-{
      this.id = id;
    }-*/;

    public final native String getDay() /*-{
      return this.day;
    }-*/;

    public final native void setDay(String day) /*-{
      this.day = day;
    }-*/;

    public final native String getDate() /*-{
      return this.date;
    }-*/;

    public final native void setDate(String date) /*-{
      this.date = date;
    }-*/;
  }

  /**
   * Constructor
   */
  protected JsoTrip() {
  }

  public final native String getKey() /*-{
    return this.key;
  }-*/;

  public final native void setKey(String key) /*-{
    this.key = key;
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

  public final native String getLocation() /*-{
    return this.location;
  }-*/;

  public final native void setLocation(String location) /*-{
    this.location = location;
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

  public final native double getPlaceId() /*-{
    return this.placeId;
  }-*/;

  public final native void setPlaceId(double placeId) /*-{
    this.placeId = placeId;
  }-*/;

  public final native double getNorthLatitude() /*-{
    if (!this.northLatitude) {
      return 0.0;
    }
    return this.northLatitude;
  }-*/;

  public final native void setNorthLatitude(double northLatitude) /*-{
    this.northLatitude = northLatitude;
  }-*/;

  public final native double getSouthLatitude() /*-{
    if (!this.southLatitude) {
      return 0.0;
    }
    return this.southLatitude;
  }-*/;

  public final native void setSouthLatitude(double southLatitude) /*-{
    this.southLatitude = southLatitude;
  }-*/;

  public final native double getEastLongitude() /*-{
    if (!this.eastLongitude) {
      return 0.0;
    }
    return this.eastLongitude;
  }-*/;

  public final native void setEastLongitude(double eastLongitude) /*-{
    this.eastLongitude = eastLongitude;
  }-*/;

  public final native double getWestLongitude() /*-{
    if (!this.westLongitude) {
      return 0.0;
    }
    return this.westLongitude;
  }-*/;

  public final native void setWestLongitude(double westLongitude) /*-{
    this.westLongitude = westLongitude;
  }-*/;

  private final native String getStartDate_() /*-{
    return this.startDate;
  }-*/;

  public final Date getStartDate() {
    return new Date(Long.parseLong(getStartDate_()));
  }

  private final native void setStartDate_(String startDate) /*-{
    this.startDate = startDate;
  }-*/;

  public final void setStartDate(Date startDate) {
    setStartDate_(startDate.getTime() + "");
  }

  public final native int getDuration() /*-{
    return this.duration;
  }-*/;

  public final native void setDuration(int duration) /*-{
    this.duration = duration;
  }-*/;

  public final native String getVersion_() /*-{
    return this.version;
  }-*/;

  public final Long getVersion() {
    return Long.valueOf(getVersion_());
  }
  
  public final native void setVersion_(String version) /*-{
    this.version = version;
  }-*/;
  
  public final void setVersion(Long version) {
    setVersion_(version.toString());
  }

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

  private final native JsArrayString getContributorIds_() /*-{
    return this.contributorIds;
  }-*/;

  public final HashSet<String> getContributorIds() {
    JsArrayString sArr = getContributorIds_();
    HashSet<String> arr = new HashSet<String>();
    for (int i = 0; i < sArr.length(); ++i) {
      arr.add(sArr.get(i));
    }
    return arr;
  }

  public final native void setContributorIds(JsArrayString contributorIds) /*-{
    this.contributorIds = contributorIds;
  }-*/;

  public final void setContributorIds(HashSet<String> contributorIds) {
    JsArrayString stringArr = (JsArrayString) JavaScriptObject.createArray();
    for (String s : contributorIds) {
      stringArr.push(s);
    }
    setContributorIds(stringArr);
  }

  private final native JsArrayString getViewerIds_() /*-{
    return this.viewerIds;
  }-*/;

  public final HashSet<String> getViewerIds() {
    JsArrayString sArr = getViewerIds_();
    HashSet<String> arr = new HashSet<String>();
    for (int i = 0; i < sArr.length(); ++i) {
      arr.add(sArr.get(i));
    }
    return arr;
  }

  public final native void setViewerIds(JsArrayString viewerIds) /*-{
    this.viewerIds = viewerIds;
  }-*/;

  public final void setViewerIds(HashSet<String> viewerIds) {
    JsArrayString stringArr = (JsArrayString) JavaScriptObject.createArray();
    for (String s : viewerIds) {
      stringArr.push(s);
    }
    setViewerIds(stringArr);
  }

  private final native String getAddedOn_() /*-{
    return this.addedOn;
  }-*/;

  public final Date getAddedOn() {
    return new Date(Long.parseLong(getAddedOn_()));
  }

  private final native void setAddedOn_(String addedOn) /*-{
    this.addedOn = addedOn;
  }-*/;

  public final void setAddedOn(Date addedOn) {
    setAddedOn_(addedOn.getTime() + "");
  }

  private final native String getLastModified_() /*-{
    return this.lastModified;
  }-*/;

  public final Date getLastModified() {
    return new Date(Long.parseLong(getLastModified_()));
  }

  private final native void setLastModified_(String lastModified) /*-{
    this.lastModified = lastModified;
  }-*/;

  public final void setLastModified(Date lastModified) {
    setLastModified_(lastModified.getTime() + "");
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

  public final native JsArray<JsoIdDayDateTuple> getTripItemIds_() /*-{
    return this.tripItemIds;
  }-*/;

  public final IdDayDateTupleList getTripItemIds() {
    final JsArray<JsoIdDayDateTuple> idArr = getTripItemIds_();
    final IdDayDateTupleList idDayDateTupleList = new IdDayDateTupleList();
    for (int i = 0; i < idArr.length(); ++i) {
      final String id = idArr.get(i).getId();
      final String day = idArr.get(i).getDay();
      final String dateS = idArr.get(i).getDate();
      final Date lastModified = new Date(Long.parseLong(dateS));
      final IdDayDateTuple idTup = new IdDayDateTuple(id, Integer.parseInt(day), lastModified);
      idDayDateTupleList.getTuples().add(idTup);
    }
    return idDayDateTupleList;
  }

  public final native void setTripItemIds_(JsArray<JsoIdDayDateTuple> tripItemIds) /*-{
    this.tripItemIds = tripItemIds;
  }-*/;

  @SuppressWarnings("unchecked")
  public final void setTripItemIds(final IdDayDateTupleList idDateTupleList) {
    JsArray<JsoIdDayDateTuple> idArr = (JsArray<JsoIdDayDateTuple>) JavaScriptObject.createArray();
    for (IdDayDateTuple s : idDateTupleList.getTuples()) {
      JsoIdDayDateTuple idTup = (JsoIdDayDateTuple) JavaScriptObject.createObject();
      idTup.setId(s.getId());
      idTup.setDay(s.getDay() + "");
      idTup.setDate(s.getLastModified().getTime() + "");
      idArr.push(idTup);
    }
    setTripItemIds_(idArr);
  }

  public final native JsArray<JsoIdDateTuple> getCommentIds_() /*-{
    return this.commentIds;
  }-*/;

  public final IdDateTupleList getCommentIds() {
    final JsArray<JsoIdDateTuple> idArr = getCommentIds_();
    final IdDateTupleList idDateTupleList = new IdDateTupleList();
    for (int i = 0; i < idArr.length(); ++i) {
      final String id = idArr.get(i).getId();
      final String dateS = idArr.get(i).getDate();
      final Date lastModified = new Date(Long.parseLong(dateS));
      final IdDateTuple idTup = new IdDateTuple(id, lastModified);
      idDateTupleList.getTuples().add(idTup);
    }
    return idDateTupleList;
  }

  public final native void setCommentIds_(JsArray<JsoIdDateTuple> commentIds) /*-{
    this.commentIds = commentIds;
  }-*/;

  @SuppressWarnings("unchecked")
  public final void setCommentIds(final IdDateTupleList idDateTupleList) {
    JsArray<JsoIdDateTuple> idArr = (JsArray<JsoIdDateTuple>) JavaScriptObject.createArray();
    for (IdDateTuple s : idDateTupleList.getTuples()) {
      JsoIdDateTuple idTup = (JsoIdDateTuple) JavaScriptObject.createObject();
      idTup.setId(s.getId());
      idTup.setDate(s.getLastModified().getTime() + "");
      idArr.push(idTup);
    }
    setCommentIds_(idArr);
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

  public final void setTrip(Trip t) {
    setKey(nullToEmpty(t.getKey()));
    setName(nullToEmpty(t.getName()));
    setDescription(nullToEmpty(t.getDescription()));
    setLocation(nullToEmpty(t.getLocation()));
    setLatitude(nullToEmpty(t.getLatitude()));
    setLongitude(nullToEmpty(t.getLongitude()));
    setPlaceId(nullToEmpty(t.getPlaceId()));
    setNorthLatitude(nullToEmpty(t.getNorthLatitude()));
    setSouthLatitude(nullToEmpty(t.getSouthLatitude()));
    setEastLongitude(nullToEmpty(t.getEastLongitude()));
    setWestLongitude(nullToEmpty(t.getWestLongitude()));
    setStartDate(nullToEmpty(t.getStartDate()));
    setDuration(nullToEmpty(t.getDuration()));
    setVersion(nullToEmpty(t.getVersion()));
    setThumbsUp(nullToEmpty(t.getThumbsUp()));
    setThumbsDown(nullToEmpty(t.getThumbsDown()));
    setOwnerId(nullToEmpty(t.getOwnerId()));
    setOwnerName(nullToEmpty(t.getOwnerName()));
    setContributorIds(nullToEmpty(t.getContributorIds()));
    setViewerIds(nullToEmpty(t.getViewerIds()));
    setAddedOn(nullToEmpty(t.getAddedOn()));
    setLastModified(nullToEmpty(t.getLastModified()));
    setLastModifiedBy(nullToEmpty(t.getLastModifiedBy()));
    setStatus(nullToEmpty(t.getStatus()));
    setTripItemIds(nullToEmpty(t.getTripItemIds()));
    setCommentIds(nullToEmpty(t.getCommentIds()));
    setUpdated(nullToEmpty(t.isUpdated()));
    setCommentsUpdated(nullToEmpty(t.isCommentsUpdated()));
  }

  public final Trip getTrip() {
    Trip t = new Trip();
    t.setKey(getKey());
    t.setName(getName());
    t.setDescription(getDescription());
    t.setLocation(getLocation());
    t.setLatitude(getLatitude());
    t.setLongitude(getLongitude());
    t.setPlaceId((long) getPlaceId());
    t.setNorthLatitude(getNorthLatitude());
    t.setSouthLatitude(getSouthLatitude());
    t.setEastLongitude(getEastLongitude());
    t.setWestLongitude(getWestLongitude());
    t.setStartDate(getStartDate());
    t.setDuration(getDuration());
    t.setVersion(getVersion());
    t.setThumbsUp(getThumbsUp());
    t.setThumbsDown(getThumbsDown());
    t.setOwnerId(getOwnerId());
    t.setOwnerName(getOwnerName());
    t.setContributorIds(getContributorIds());
    t.setViewerIds(getViewerIds());
    t.setAddedOn(getAddedOn());
    t.setLastModified(getLastModified());
    t.setLastModifiedBy(getLastModifiedBy());
    t.setStatus(getStatus());
    t.setTripItemIds(getTripItemIds());
    t.setCommentIds(getCommentIds());
    t.setUpdated(isUpdated());
    t.setCommentsUpdated(isCommentsUpdated());
    return t;
  }

  private final String nullToEmpty(String s) {
    if (s == null) {
      return "";
    }
    return s;
  }

  private final double nullToEmpty(double f) {
    return f;
  }

  private final Long nullToEmpty(Long l) {
    return l == null ? 0L : l;
  }

  private final HashSet<String> nullToEmpty(HashSet<String> ss) {
    if (ss == null) {
      return new HashSet<String>();
    }
    return ss;
  }

  private final boolean nullToEmpty(boolean b) {
    return b;
  }

  private final int nullToEmpty(int i) {
    return i;
  }

  private final Date nullToEmpty(Date d) {
    if (d == null) {
      return new Date();
    }
    return d;
  }

  private Status nullToEmpty(Status status) {
    if (status == null) {
      return Status.ACTIVE;
    }
    return status;
  }

  private IdDateTupleList nullToEmpty(IdDateTupleList commentIds) {
    if (commentIds == null) {
      return new IdDateTupleList(new ArrayList<IdDateTuple>());
    }
    return commentIds;
  }

  private IdDayDateTupleList nullToEmpty(IdDayDateTupleList tripItemIds) {
    if (tripItemIds == null) {
      return new IdDayDateTupleList(new ArrayList<IdDayDateTuple>());
    }
    return tripItemIds;
  }
}
