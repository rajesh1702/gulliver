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

import java.io.Serializable;
import java.util.ArrayList;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * JDO: Details of a POI(Points of Interest)
 * 
 */
@PersistenceCapable
public class POIDetail implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 10L;

  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  private Long id;
  @Persistent
  String poiType;
  @Persistent
  String name;
  @Persistent
  String address;
  @Persistent
  ArrayList<String> phones;
  @Persistent
  String searchResultUrl;
  @Persistent
  double latitude;
  @Persistent
  double longitude;
  @Persistent
  String review = "";

  public final long getId() {
    return id;
  }

  public final void setId(long id) {
    this.id = id;
  }

  public final String getPoiType() {
    return poiType;
  }

  public final void setPoiType(String poiType) {
    this.poiType = poiType;
  }

  public final String getName() {
    return name;
  }

  public final String getAddress() {
    return address;
  }

  public final void setAddress(String address) {
    this.address = address;
  }

  public final ArrayList<String> getPhones() {
    return phones;
  }

  public final void setPhones(ArrayList<String> phones) {
    this.phones = phones;
  }

  public final String getSearchResultUrl() {
    return searchResultUrl;
  }

  public final void setSearchResultUrl(String searchResultUrl) {
    this.searchResultUrl = searchResultUrl;
  }

  public final void setName(String name) {
    this.name = name;
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

  public final String getReview() {
    return review;
  }

  public final void setReview(String review) {
    this.review = review;
  }

  @Override
  public boolean equals(Object object) {
    if (object == null || !(object instanceof POIDetail)) {
      return false;
    }
    return id == (((POIDetail) object).getId());
  }

  @Override
  public int hashCode() {
    return 31 * 17 + (int) (id ^ (id >>> 32));
  }

  @Override
  public String toString() {
    StringBuffer buffer = new StringBuffer();
    buffer.append("id=" + id);
    buffer.append(" , poiType=" + poiType);
    buffer.append(" , name=" + name);
    buffer.append(" , address=" + address);
    buffer.append(" , phones=" + phones);
    buffer.append(" , searchResultUrl=" + searchResultUrl);
    buffer.append(" , latitude=" + latitude);
    buffer.append(" , longitude=" + longitude);
    
    return buffer.toString();
  }
}
