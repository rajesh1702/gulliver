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
 * JDO: Basic details of a POI(Points of Interest)
 * 
 */
@PersistenceCapable
public class POI implements Serializable {

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
  double latitude;
  @Persistent
  double longitude;

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

  @Override
  public boolean equals(Object object) {
    
    if (object == null || !(object instanceof POI)) {
      return false;
    }
    return id == (((POI) object).getId());
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
    buffer.append(" , latitude=" + latitude);
    buffer.append(" , longitude=" + longitude);
    
    return buffer.toString();
  }
}
