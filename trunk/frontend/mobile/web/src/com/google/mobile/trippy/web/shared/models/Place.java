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

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * JDO: Basic details of a Place
 * 
 */
@PersistenceCapable
public class Place implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 10L;

  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  Long id;
  @Persistent
  String fullName;
  @Persistent
  String shortName;
  @Persistent
  double northLatitude;
  @Persistent
  double southLatitude;
  @Persistent
  double eastLongitude;
  @Persistent
  double westLongitude;
  @Persistent
  PlaceStringMap placeStringMap;

  public final long getId() {
    return id;
  }

  public final void setId(long id) {
    this.id = id;
  }

  public final String getFullName() {
    return fullName;
  }

  public final void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public final String getShortName() {
    return shortName;
  }

  public final void setShortName(String shortName) {
    this.shortName = shortName;
  }

  public final double getNorthLatitude() {
    return northLatitude;
  }

  public final void setNorthLatitude(double northLatitude) {
    this.northLatitude = northLatitude;
  }

  public final double getSouthLatitude() {
    return southLatitude;
  }

  public final void setSouthLatitude(double southLatitude) {
    this.southLatitude = southLatitude;
  }

  public final double getEastLongitude() {
    return eastLongitude;
  }

  public final void setEastLongitude(double eastLongitude) {
    this.eastLongitude = eastLongitude;
  }

  public final double getWestLongitude() {
    return westLongitude;
  }

  public final void setWestLongitude(double westLongitude) {
    this.westLongitude = westLongitude;
  }
}