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
import java.util.Date;

/**
 * 
 * 
 */
public class IdDayDateTuple implements Serializable {

  private String id;
  private int day;
  private Date lastModified;

  public IdDayDateTuple() {
    super();
  }

  public IdDayDateTuple(final String id, final int day, final Date lastModified) {
    super();
    this.id = id;
    this.day = day;
    this.lastModified = lastModified;
  }

  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public int getDay() {
    return day;
  }
  
  public void setDay(int day) {
    this.day = day;
  }
  
  public Date getLastModified() {
    return lastModified;
  }
  
  public void setLastModified(Date timestamp) {
    this.lastModified = timestamp;
  }
    
  @Override
  public boolean equals(Object o) {
    if (!(o instanceof IdDayDateTuple)) {
      return false;
    }
    final IdDayDateTuple otherObj = (IdDayDateTuple) o;
    return getId().equals(otherObj.getId()) ;
  }

  @Override
  public int hashCode() {
    return getId().hashCode();
  }
}
