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

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.mobile.trippy.web.shared.UserUtils;
import com.google.mobile.trippy.web.shared.exception.AuthenticationException;

/**
 * Server utility class.
 * 
 * Get current user.
 * 
 */
public class Utils {

  private static final UserService userService;
  
  static {
    userService = UserServiceFactory.getUserService();
  }
  
  /**
   * Get email of the logged in user.
   * 
   * Assumes that user is logged in before calling this method.
   * @throws AuthenticationException 
   */
  public static String currentUserEmail() throws AuthenticationException {
    final User user = getCurrentUser();
    if (user == null) {
      throw new AuthenticationException();
    }
    return user.getEmail();
  }
  
  public static User getCurrentUser() {
    return userService.getCurrentUser();
  }

  public static String getLoginUrl() {
    return getLoginUrl(UserUtils.AUTH_DESTINATION_URL_DUMMY);
  }
  
  public static String getLogoutUrl() {
    return getLogoutUrl(UserUtils.AUTH_DESTINATION_URL_DUMMY);
  }

  public static String getLoginUrl(String dest) {
    return userService.createLoginURL(dest);
  }
  
  public static String getLogoutUrl(String dest) {
    return userService.createLogoutURL(dest);
  }
}
