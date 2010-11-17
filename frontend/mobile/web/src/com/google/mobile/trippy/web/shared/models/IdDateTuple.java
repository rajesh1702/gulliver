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
public class IdDateTuple implements Serializable {
  
  private static final long serialVersionUID = 9142420800668978815L;
  private String id;
  private Date lastModified;
  
  public IdDateTuple() {
    super();
  }
  
  public IdDateTuple(final String id, final Date lastModified) {
    super();
    this.id = id;
    this.lastModified = lastModified;
  }

  public String getId() {
    return id;
  }

  public Date getLastModified() {
    return lastModified;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setLastModified(Date timestamp) {
    this.lastModified = timestamp;
  }
  
  @Override
  public boolean equals(Object o) {
    if (!(o instanceof IdDateTuple)) {
      return false;
    }
    return getId().equals(((IdDateTuple) o).getId());
  }
  
  @Override
  public int hashCode() {
    return getId().hashCode();
  }

}
