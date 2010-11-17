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

import com.google.mobile.trippy.web.shared.models.EulaUser;

import java.util.Date;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;

/**
 * End User License Agreement Utilities.
 *
 */
public class EulaUtil {

  private final PersistenceManager pm = PMF.get().getPersistenceManager();
  
  public boolean isEulaUserExists(String userEmail) {
    try {
      final EulaUser user = pm.getObjectById(EulaUser.class, userEmail);
      return (user != null);
    } catch (JDOObjectNotFoundException e) {
      return false;
    }
  }

  public void addEulaUser(String userEmail) {
    final EulaUser user = new EulaUser();
    user.setUserEmail(userEmail);
    user.setAddedOn(new Date());
    pm.makePersistent(user);
    pm.detachCopy(user);
  }
}
