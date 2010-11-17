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

import com.google.common.base.Preconditions;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.mobile.trippy.web.client.service.RemoteTripService;
import com.google.mobile.trippy.web.server.ShareTripServlet;
import com.google.mobile.trippy.web.server.i18n.Message;
import com.google.mobile.trippy.web.shared.exception.AuthenticationException;
import com.google.mobile.trippy.web.shared.exception.AuthorizationException;
import com.google.mobile.trippy.web.shared.exception.TransactionFailedException;
import com.google.mobile.trippy.web.shared.exception.TripNotFoundException;
import com.google.mobile.trippy.web.shared.exception.TripVersionException;
import com.google.mobile.trippy.web.shared.models.Comment;
import com.google.mobile.trippy.web.shared.models.CommentUpdateResult;
import com.google.mobile.trippy.web.shared.models.IdDayDateTupleList;
import com.google.mobile.trippy.web.shared.models.Trip;
import com.google.mobile.trippy.web.shared.models.TripFetchResult;
import com.google.mobile.trippy.web.shared.models.TripItem;
import com.google.mobile.trippy.web.shared.models.TripItemUpdateResult;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * 
 */
public class TripServiceImpl extends RemoteServiceServlet implements
    RemoteTripService {
  
  public static final int MAX_INVITE_EMAILS = 5;
  
  private final TripUtils tripUtils = new TripUtils();
  private final TripItemUtils tripItemUtils = new TripItemUtils();
  private final CommentUtils commentUtils = new CommentUtils();

  @Override
  public CommentUpdateResult addComment(Comment comment) throws AuthorizationException,
      AuthenticationException, TransactionFailedException {
    final String userEmail = Utils.currentUserEmail();
    return commentUtils.addComment(comment, userEmail);
  }

  @Override
  public Trip addTrip(Trip trip) throws AuthenticationException {
    final String userEmail = Utils.currentUserEmail();
    return tripUtils.addTrip(trip, userEmail);
  }

  @Override
  public TripItemUpdateResult addTripItem(TripItem tripItem) throws AuthorizationException,
      AuthenticationException, TransactionFailedException {
    final String userEmail = Utils.currentUserEmail();
    return tripItemUtils.addTripItem(tripItem, userEmail);
  }

  @Override
  public CommentUpdateResult deleteComment(Comment comment) throws AuthorizationException,
      AuthenticationException, TransactionFailedException {
    final String userEmail = Utils.currentUserEmail();
    return commentUtils.deleteComment(comment, userEmail);
  }

  @Override
  public void deleteTrip(Trip trip) throws AuthorizationException,
      AuthenticationException {
    final String userEmail = Utils.currentUserEmail();
    tripUtils.deleteTrip(trip, userEmail);
  }

  @Override
  public TripItemUpdateResult deleteTripItem(TripItem tripItem) throws AuthorizationException,
      AuthenticationException, TransactionFailedException {
    final String userEmail = Utils.currentUserEmail();
    return tripItemUtils.deleteTripItem(tripItem, userEmail);
  }

  @Override
  public ArrayList<Comment> fetchComments(ArrayList<String> ids) {
    return commentUtils.fetchComments(ids);
  }

  @Override
  public ArrayList<TripItem> fetchTripItems(ArrayList<String> ids) {
    return tripItemUtils.fetchTripItems(ids);
  }

  @Override
  public TripFetchResult fetchUpdatedTrips(Date from, String cursor, int limit)
      throws AuthenticationException {
    final String userEmail = Utils.currentUserEmail();
    return tripUtils.fetchUpdatedTrips(from, userEmail, cursor, limit);
  }

  @Override
  public String sendInvite(String tripId, ArrayList<String> emails,
      boolean collaborator) throws IllegalArgumentException {
    Preconditions.checkArgument(emails.size() <= MAX_INVITE_EMAILS,
        "Maximum of " + MAX_INVITE_EMAILS + " invites are allowed in one request");
    final StringBuilder invitationSentTo = new StringBuilder();
    invitationSentTo.append("Invite(s) sent to ");
    final String inviteFrom = Utils.getCurrentUser().getEmail();
    for (String email : emails) {
      try {
        final String domain
            = getThreadLocalRequest().getScheme() + "://" + getThreadLocalRequest().getServerName();
        sendInviteMail(inviteFrom, email,
            ShareTripServlet.createInvitationUrl(domain, tripId, collaborator));
        invitationSentTo.append(email + ", ");
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      } catch (MessagingException e) {
        e.printStackTrace();
      }
    }
    return invitationSentTo.toString();
  }

  @Override
  public Trip updateTrip(Trip updatedTrip)
          throws AuthorizationException, AuthenticationException {
    final String userEmail = Utils.currentUserEmail();
    return tripUtils.updateTrip(updatedTrip, userEmail);
  }

  @Override
  public TripItemUpdateResult updateTripItem(TripItem tripItem) throws AuthorizationException,
      AuthenticationException, TransactionFailedException {
    final String userEmail = Utils.currentUserEmail();
    return tripItemUtils.updateTripItem(tripItem, userEmail);
  }
  
  void sendInviteMail(final String inviteFrom, final String invite, final String inviteUrl)
      throws MessagingException, UnsupportedEncodingException {
    final Properties props = new Properties();
    final Session session = Session.getDefaultInstance(props, null);

    final StringBuilder msgBody = new StringBuilder();
    msgBody.append(Message.inviteMsgLine1(inviteFrom) + "\n");
    msgBody.append(Message.inviteMsgLine2(inviteUrl));

    final javax.mail.Message msg = new MimeMessage(session);
    msg.setFrom(new InternetAddress(Message.INVITE_SENDER_EMAIL, Message.INVITE_SENDER_NAME));
    msg.addRecipient(javax.mail.Message.RecipientType.TO,
                     new InternetAddress(invite));
    msg.setSubject(Message.inviteSubject(inviteFrom));
    msg.setText(msgBody.toString());
    Transport.send(msg);
  }

  @Override
  public Trip updateTripItemsTuple(final String tripKey, final Long clientTripVersion, 
      final IdDayDateTupleList itemsByDay)
      throws AuthorizationException, AuthenticationException, TripNotFoundException,
      TripVersionException {
    final String userEmail = Utils.currentUserEmail();
    return tripUtils.updateTripItemsTuple(tripKey, clientTripVersion, itemsByDay, userEmail);
  }

}
