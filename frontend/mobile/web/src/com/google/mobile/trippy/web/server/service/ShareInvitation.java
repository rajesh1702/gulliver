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
package com.google.mobile.trippy.web.server.service;

import com.google.appengine.api.datastore.Key;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class ShareInvitation {

  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  private Key key;
  @Persistent
  private String tripId;
  @Persistent
  private boolean collaborator;
  
  public ShareInvitation(String tripId, boolean collaborator) {
    super();
    setTripId(tripId);
    setCollaborator(collaborator);
  }

  public final String getTripId() {
    return tripId;
  }

  public final void setTripId(String tripId) {
    this.tripId = tripId;
  }

  public final boolean isCollaborator() {
    return collaborator;
  }

  public final void setCollaborator(boolean collaborator) {
    this.collaborator = collaborator;
  }

  public final Key getKey() {
    return key;
  }

}
