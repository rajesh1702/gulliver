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

import java.util.ArrayList;

/**
 * Class to act as a bridge between Search Results and its variants like LP
 * and Google.
 * 
 */
public class SearchItem {

  /**
   * Enum for Search result type.
   */
  public static enum SearchType {GOOGLE, LP}
  
  
  private String name;
  private String address;
  private ArrayList<String> phoneNumbers;
  private String url;
  private double longitude;
  private double latitude;
  private String id;
  private SearchType type = SearchType.GOOGLE;
  private long poiId;
  private String review = "";
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getAddress() {
    return address;
  }
  public void setAddress(String address) {
    this.address = address;
  }
  public ArrayList<String> getPhoneNumbers() {
    return phoneNumbers;
  }
  public void setPhoneNumbers(ArrayList<String> phoneNumbers) {
    this.phoneNumbers = phoneNumbers;
  }
  public String getUrl() {
    return url;
  }
  public void setUrl(String url) {
    this.url = url;
  }
  public double getLongitude() {
    return longitude;
  }
  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }
  public double getLatitude() {
    return latitude;
  }
  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }

  public long getPoiId() {
    return poiId;
  }
  
  public void setPoiId(long id) {
    this.poiId = id;
  }
  
  public SearchType getType() {
    return type;
  }

  public void setType(SearchType type) {
    this.type = type;
  }

  public final String getReview() {
    return review;
  }

  public final void setReview(String review) {
    this.review = review;
  }
}
