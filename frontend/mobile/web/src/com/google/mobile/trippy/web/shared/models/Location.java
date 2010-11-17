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

import com.google.gwt.maps.client.base.HasLatLng;

/**
 * A client side-only data model to encapsulate location related info
 * 
 */
public class Location {
  private String name;
  private HasLatLng coordinates;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public HasLatLng getCoordinates() {
    return coordinates;
  }

  public void setCoordinates(HasLatLng coordinates) {
    this.coordinates = coordinates;
  }
  
  @Override
  public boolean equals(Object object) {
    
    if (!(object instanceof Location)) {
      return false;
    }
    Location location = (Location) object;
   
    if (!name.equals(location.getName())) {
      return false;
    }
    if (!coordinates.equals(location.getCoordinates())) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int result = 17;
    result = 31 * result + name.hashCode();
    result = 31 * result + coordinates.hashCode();
    
    return result;
  }

  @Override
  public String toString() {
    StringBuffer buffer = new StringBuffer();
    buffer.append("name=" + name);
    buffer.append(" , coordinates=" + coordinates);
    
    return buffer.toString();
  }
}
