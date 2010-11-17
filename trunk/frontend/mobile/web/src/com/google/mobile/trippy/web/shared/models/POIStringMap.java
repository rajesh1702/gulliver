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

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * JDO: List of pois for a poi keying string.
 * 
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class POIStringMap  implements Serializable, IsSerializable {

  private static final long serialVersionUID = 1L;
  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  private Long id;
  @Persistent
  private String poiKeyString;
  @Persistent
  private ArrayList<POI> poiSets;

  public POIStringMap(String poiKeyString, ArrayList<POI> poiSets) {
    setKeyString(poiKeyString);
    setPOIs(poiSets);
  }

  public final long getId() {
    return id;
  }

  public final void setId(long id) {
    this.id = id;
  }

  public final String getKeyString() {
    return poiKeyString;
  }

  public final void setKeyString(String poiKeyString) {
    this.poiKeyString = poiKeyString;
  }

  public List<POI> getPOIs() {
    return poiSets;
  }

  public final void setPOIs(ArrayList<POI> poiSets) {
    this.poiSets = poiSets;
  }
}
