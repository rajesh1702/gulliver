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

package com.google.mobile.trippy.web.server;

import com.google.appengine.api.users.UserServiceFactory;
import com.google.mobile.trippy.web.server.service.EulaUtil;
import com.google.mobile.trippy.web.server.service.Utils;
import com.google.mobile.trippy.web.shared.exception.AuthenticationException;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet to add user to EULA accepted user list.
 *
 */
@SuppressWarnings("serial")
public class EulaAcceptServlet extends HttpServlet {
  
  @Override
  protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
      throws IOException {
    try {
      String userEmail = Utils.currentUserEmail();
      new EulaUtil().addEulaUser(userEmail);
      resp.sendRedirect("/");
    } catch (AuthenticationException e) {
      final String query = req.getQueryString();
      final String url = req.getRequestURL().append(query == null ? "" : "?" + query).toString();
      resp.sendRedirect(UserServiceFactory.getUserService().createLoginURL(url));
    }
  }
  
}
