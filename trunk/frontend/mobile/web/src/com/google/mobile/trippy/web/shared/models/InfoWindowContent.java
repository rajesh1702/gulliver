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

/**
 * Data model for info window content
 * 
 */
public class InfoWindowContent {
  private String name;
  private String address;
  private String phone;
  private String schedule;
  private String url;

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

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getSchedule() {
    return schedule;
  }

  public void setSchedule(String schedule) {
    this.schedule = schedule;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }
  
  @Override
  public boolean equals(Object object) {
    
    if (!(object instanceof InfoWindowContent)) {
      return false;
    }
    InfoWindowContent content = (InfoWindowContent) object;
   
    if (!name.equals(content.getName())) {
      return false;
    }
    if (!address.equals(content.getAddress())) {
      return false;
    }
    if (!phone.equals(content.getPhone())) {
      return false;
    }
    if (!schedule.equals(content.getSchedule())) {
      return false;
    }
    if (!url.equals(content.getUrl())) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int result = 17;
    result = 31 * result + name.hashCode();
    result = 31 * result + address.hashCode();
    result = 31 * result + phone.hashCode();
    result = 31 * result + schedule.hashCode();
    result = 31 * result + url.hashCode();
    
    return result;
  }

  @Override
  public String toString() {
    StringBuffer buffer = new StringBuffer();
    buffer.append("name=" + name);
    buffer.append(" , address=" + address);
    buffer.append(" , phone=" + phone);
    buffer.append(" , schedule=" + schedule);
    buffer.append(" , url=" + url);
    
    return buffer.toString();
  }
}
