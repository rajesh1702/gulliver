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

import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.repackaged.com.google.common.base.Preconditions;
import com.google.mobile.trippy.web.server.i18n.Message;
import com.google.mobile.trippy.web.server.service.PMF;
import com.google.mobile.trippy.web.server.service.ShareInvitation;
import com.google.mobile.trippy.web.server.service.Utils;
import com.google.mobile.trippy.web.shared.exception.AuthenticationException;
import com.google.mobile.trippy.web.shared.models.Trip;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet to share trip as collaborator and/or viewer.
 * 
 */
@SuppressWarnings("serial")
public class ShareTripServlet extends HttpServlet {

  public static final String INVITATION_URL_PREFIX = "/invite?key=";
  
  /**
   * Generate invitation URL for a trip.
   * @throws UnsupportedEncodingException 
   */
  public static String createInvitationUrl(
      final String domain,
      final String tripId, final boolean collaborator)
      throws UnsupportedEncodingException {
    final PersistenceManager pm = PMF.get().getPersistenceManager();
    final ShareInvitation invitationWithKey
        = pm.makePersistent(new ShareInvitation(tripId, collaborator));
    final String inviteUrl = domain + INVITATION_URL_PREFIX
        + URLEncoder.encode(KeyFactory.keyToString(invitationWithKey.getKey()), "UTF-8");
    java.util.logging.Logger.getAnonymousLogger().warning(inviteUrl);
    return inviteUrl;
  }

  /**
   * Remove invitation when accepted or declined.
   */
  public static void removeInvitation(final Key key) {
    DatastoreServiceFactory.getDatastoreService().delete(key);
  }
  
  @Override
  protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
      throws ServletException, IOException {
    final String key = req.getParameter("key");
    String userEmail;
    try {
      userEmail = Utils.currentUserEmail();
      final ShareInvitation invitation = getInvitation(key);
      if (invitation.isCollaborator()) {
        addUserAsCollaborator(userEmail, invitation.getTripId());
        resp.sendRedirect("/");
      } else {
        addUserAsViewer(userEmail, invitation.getTripId());
        resp.sendRedirect("/");
      }
      removeInvitation(invitation.getKey());
    } catch (AuthenticationException e) {
      final String query = req.getQueryString();
      final String url = req.getRequestURL().append(query == null ? "" : "?" + query).toString();
      resp.sendRedirect(UserServiceFactory.getUserService().createLoginURL(url));
    } catch (JDOObjectNotFoundException e) {
      resp.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, Message.INVITE_EXPIRED);
    }
  }
  
  public ShareInvitation getInvitation(final String keyStr) throws JDOObjectNotFoundException {
    Preconditions.checkNotNull(keyStr);
    final PersistenceManager pm = PMF.get().getPersistenceManager();
    return pm.getObjectById(ShareInvitation.class, keyStr);
  }

  private void addUserAsViewer(final String userEmail, final String tripId) throws JDOObjectNotFoundException{
    final PersistenceManager pm = PMF.get().getPersistenceManager();
    final Trip trip = pm.detachCopy(pm.getObjectById(Trip.class, tripId));
    trip.addViewerId(userEmail);
    trip.setLastModified(new Date());
    trip.setLastModifiedBy(userEmail);
    pm.makePersistent(trip);
  }

  private void addUserAsCollaborator(final String userEmail, final String tripId)
      throws JDOObjectNotFoundException{
    final PersistenceManager pm = PMF.get().getPersistenceManager();
    final Trip trip = pm.detachCopy(pm.getObjectById(Trip.class, tripId));
    trip.addContributorId(userEmail);
    trip.addViewerId(userEmail);
    trip.setLastModified(new Date());
    trip.setLastModifiedBy(userEmail);
    pm.makePersistent(trip);
  }
  
}
