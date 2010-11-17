/*
 * Copyright 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.mobile.trippy.web.server.service;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.mobile.trippy.web.server.i18n.Message;
import com.google.mobile.trippy.web.shared.SharedConstants;
import com.google.mobile.trippy.web.shared.exception.AuthorizationException;
import com.google.mobile.trippy.web.shared.exception.TransactionFailedException;
import com.google.mobile.trippy.web.shared.exception.TripVersionException;
import com.google.mobile.trippy.web.shared.models.Comment;
import com.google.mobile.trippy.web.shared.models.CommentUpdateResult;
import com.google.mobile.trippy.web.shared.models.IdDateTuple;
import com.google.mobile.trippy.web.shared.models.IdDateTupleList;
import com.google.mobile.trippy.web.shared.models.IdDayDateTuple;
import com.google.mobile.trippy.web.shared.models.Status;
import com.google.mobile.trippy.web.shared.models.Trip;
import com.google.mobile.trippy.web.shared.models.TripItem;
import com.google.mobile.trippy.web.shared.models.TripItemUpdateResult;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import net.oauth.OAuthServiceProvider;
import net.oauth.SimpleOAuthValidator;
import net.oauth.signature.RSA_SHA1;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Servlet to share trip as collaborator and/or viewer.
 *
 *
 */
@SuppressWarnings("serial")
public class IGoogleServlet extends HttpServlet {

  private final TripUtils tripUtils = new TripUtils();
  private final TripItemUtils tripItemUtils = new TripItemUtils();
  private final CommentUtils commentUtils = new CommentUtils();

  private static final String OP = "action";
  private static final String OP_GET_TRIPS_FOR_USER = "getTrips";
  private static final String OP_GET_TRIP = "getTrip";
  private static final String OP_ADD_TRIP = "addTrip";
  private static final String OP_UPDATE_TRIP = "updateTrip";
  private static final String OP_DELETE_TRIP = "deleteTrip";
  private static final String OP_COPY_TRIP = "copyTrip";
  private static final String OP_SHARE_TRIP = "shareTrip";
  private static final String OP_MAIL_TRIP = "mailTrip";
  private static final String OP_GET_ITEMS_FOR_TRIP = "getItems";
  private static final String OP_RESCHEDULE_ITEMS_OF_TRIP = "rescheduleItems";
  private static final String OP_GET_TRIP_ITEM = "getItem";
  private static final String OP_ADD_TRIP_ITEM = "addItem";
  private static final String OP_UPDATE_TRIP_ITEM = "updateItem";
  private static final String OP_DELETE_TRIP_ITEM = "deleteItem";
  private static final String OP_ADD_COMMENT = "addComment";
  private static final String OP_GET_COMMENTS_FOR_TRIP_ITEM = "getComments";
  private static final String OP_DELETE_COMMENT = "deleteComment";

  private static final int ERROR_NONE = HttpServletResponse.SC_OK;
  private static final int ERROR_UNAUTHORIZED = HttpServletResponse.SC_UNAUTHORIZED;
  private static final int ERROR_UNAUTHENTICATED =
      HttpServletResponse.SC_PROXY_AUTHENTICATION_REQUIRED;
  private static final int ERROR_DATA_UNAVAILABLE = HttpServletResponse.SC_NO_CONTENT;
  private static final int ERROR_INVALID_PARAM = HttpServletResponse.SC_BAD_REQUEST;
  private static final int ERROR_UNKNOWN = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

  private String ldap;

  private static final String IGOOGLE_CERTIFICATE =
      "-----BEGIN CERTIFICATE-----\n"
          + "MIIDBDCCAm2gAwIBAgIJAK8dGINfkSTHMA0GCSqGSIb3DQEBBQUAMGAxCzAJBgNV\n"
          + "BAYTAlVTMQswCQYDVQQIEwJDQTEWMBQGA1UEBxMNTW91bnRhaW4gVmlldzETMBEG\n"
          + "A1UEChMKR29vZ2xlIEluYzEXMBUGA1UEAxMOd3d3Lmdvb2dsZS5jb20wHhcNMDgx\n"
          + "MDA4MDEwODMyWhcNMDkxMDA4MDEwODMyWjBgMQswCQYDVQQGEwJVUzELMAkGA1UE\n"
          + "CBMCQ0ExFjAUBgNVBAcTDU1vdW50YWluIFZpZXcxEzARBgNVBAoTCkdvb2dsZSBJ\n"
          + "bmMxFzAVBgNVBAMTDnd3dy5nb29nbGUuY29tMIGfMA0GCSqGSIb3DQEBAQUAA4GN\n"
          + "ADCBiQKBgQDQUV7ukIfIixbokHONGMW9+ed0E9X4m99I8upPQp3iAtqIvWs7XCbA\n"
          + "bGqzQH1qX9Y00hrQ5RRQj8OI3tRiQs/KfzGWOdvLpIk5oXpdT58tg4FlYh5fbhIo\n"
          + "VoVn4GvtSjKmJFsoM8NRtEJHL1aWd++dXzkQjEsNcBXwQvfDb0YnbQIDAQABo4HF\n"
          + "MIHCMB0GA1UdDgQWBBSm/h1pNY91bNfW08ac9riYzs3cxzCBkgYDVR0jBIGKMIGH\n"
          + "gBSm/h1pNY91bNfW08ac9riYzs3cx6FkpGIwYDELMAkGA1UEBhMCVVMxCzAJBgNV\n"
          + "BAgTAkNBMRYwFAYDVQQHEw1Nb3VudGFpbiBWaWV3MRMwEQYDVQQKEwpHb29nbGUg\n"
          + "SW5jMRcwFQYDVQQDEw53d3cuZ29vZ2xlLmNvbYIJAK8dGINfkSTHMAwGA1UdEwQF\n"
          + "MAMBAf8wDQYJKoZIhvcNAQEFBQADgYEAYpHTr3vQNsHHHUm4MkYcDB20a5KvcFoX\n"
          + "gCcYtmdyd8rh/FKeZm2me7eQCXgBfJqQ4dvVLJ4LgIQiU3R5ZDe0WbW7rJ3M9ADQ\n"
          + "FyQoRJP8OIMYW3BoMi0Z4E730KSLRh6kfLq4rK6vw7lkH9oynaHHWZSJLDAp17cP\n" + "j+6znWkN9/g=\n"
          + "-----END CERTIFICATE-----";

  private static final Logger log = Logger.getLogger(IGoogleServlet.class.getName());

  @Override
  protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) {

    log.setLevel(Level.INFO);

    try {
      verifyRequest(req);
    } catch (OAuthException e) {
      log.log(Level.INFO, e.getMessage());
      handleStatusCode(resp, ERROR_UNAUTHENTICATED);
      return;
    } catch (URISyntaxException e) {
      log.log(Level.INFO, e.getMessage());
      handleStatusCode(resp, ERROR_INVALID_PARAM);
      return;
    } catch (IOException e) {
      log.log(Level.INFO, e.getMessage());
      handleStatusCode(resp, ERROR_UNKNOWN);
      return;
    }

    ldap = req.getParameter("ldap");
    log.log(Level.INFO, "ldap = " + ldap);
    if (ldap == null) {
      handleStatusCode(resp, ERROR_UNAUTHENTICATED);
      return;
    }

    int resultCode = ERROR_NONE;
    String operation = req.getParameter(OP);
    log.log(Level.INFO, "action = " + operation);
    if (operation == null) {
      resultCode = ERROR_INVALID_PARAM;
      handleStatusCode(resp, resultCode);
      return;
    }

    // ********** TRIP OPERATIONS ******************
    if (operation.equals(OP_GET_TRIPS_FOR_USER)) {
      ArrayList<Trip> activeTrips = getTrips();
      if (activeTrips != null) {
        ArrayList<Trip> dateModifiedActiveTrips = new ArrayList<Trip>();
        for (Trip trip : activeTrips) {
          if (trip.getStartDate().equals(SharedConstants.UNSCHEDULED_DATE)) {
            trip.setStartDate(null);
          }
          dateModifiedActiveTrips.add(trip);
        }
        Gson gson = new GsonBuilder().create();
        String responseStr = gson.toJson(dateModifiedActiveTrips);
        sendResponse(resp, responseStr);
      } else {
        resultCode = ERROR_DATA_UNAVAILABLE;
      }
    } else if (operation.equals(OP_GET_TRIP)) {
      String tripId = req.getParameter("id");
      log.log(Level.INFO, "trip id = " + tripId);
      if (tripId != null) {
        Trip trip = getTrip(tripId);
        log.log(Level.INFO, "trip = " + trip);
        if (trip != null) {
          if (trip.getStartDate().equals(SharedConstants.UNSCHEDULED_DATE)) {
            trip.setStartDate(null);
          }
          Gson gson = new GsonBuilder().create();
          sendResponse(resp, gson.toJson(trip));
        } else {
          resultCode = ERROR_DATA_UNAVAILABLE;
        }
      } else {
        resultCode = ERROR_INVALID_PARAM;
      }
    } else if (operation.equals(OP_RESCHEDULE_ITEMS_OF_TRIP)) {
      String tripId = req.getParameter("id");
      log.log(Level.INFO, "trip id = " + tripId);
      if (tripId != null) {
        Trip trip = getTrip(tripId);
        log.log(Level.INFO, "trip = " + trip);
        if (trip != null) {
          final PersistenceManager pm = PMF.get().getPersistenceManager();
          ArrayList<TripItem> tripItems = getTripItems(trip);
          ArrayList<TripItem> newUnscheduledItems = new ArrayList<TripItem>();
          for (TripItem item : tripItems) {
            if (item.getStartDay() > trip.getDuration()) {
              item.setStartDay(SharedConstants.UNSCHEDULED_DAY);
              newUnscheduledItems.add(item);
            }
          }
          log.log(Level.INFO, " items to be unscheduled = " + newUnscheduledItems.size());
          for (TripItem logItem : newUnscheduledItems) {
            log.log(Level.INFO, " newly unscheduled item = " + logItem.getName());
          }
          final Transaction tx = pm.currentTransaction();
          try {
            tx.begin();
            pm.makePersistentAll(newUnscheduledItems);
            tx.commit();
          } catch (Throwable t) {
            log.log(Level.INFO, t.getMessage());
            resultCode = ERROR_UNKNOWN;
          } finally {
            if (tx.isActive()) {
              tx.rollback();
            }
          }
        } else {
          resultCode = ERROR_DATA_UNAVAILABLE;
        }
      } else {
        resultCode = ERROR_INVALID_PARAM;
      }
    } else if (operation.equals(OP_DELETE_TRIP)) {
      String tripId = req.getParameter("id");
      log.log(Level.INFO, "trip id = " + tripId);
      if (tripId != null) {
        Trip trip = getTrip(tripId);
        log.log(Level.INFO, "trip = " + trip);
        if (trip != null) {
          try {
            tripUtils.deleteTrip(trip, ldap);
            sendResponse(resp, "0");
          } catch (AuthorizationException e) {
            log.log(Level.INFO, e.getMessage());
            resultCode = ERROR_UNAUTHORIZED;
          }
        } else {
          resultCode = ERROR_DATA_UNAVAILABLE;
        }
      } else {
        resultCode = ERROR_INVALID_PARAM;
      }
      // ***** Make a copy of specified master trip and add it as user-owned trip ******
    } else if (operation.equals(OP_COPY_TRIP)) {
      String tripId = req.getParameter("id");
      log.log(Level.INFO, "trip id = " + tripId);
      if (tripId != null) {
        String username = req.getParameter("ownername");
        log.log(Level.INFO, "owner display name = " + username);
        Trip copiedTrip = tripUtils.copyTrip(tripId, ldap, username);
        log.log(Level.INFO, "newly created trip = " + copiedTrip);
        
        if (copiedTrip != null) {
          if (copiedTrip.getStartDate().equals(SharedConstants.UNSCHEDULED_DATE)) {
            copiedTrip.setStartDate(null);
          }

          Gson gson = new GsonBuilder().create();
          sendResponse(resp, gson.toJson(copiedTrip));
        } else {
          resultCode = ERROR_UNKNOWN;
        }
      } else {
        resultCode = ERROR_INVALID_PARAM;
      }
      // ************ TRIP ITEM OPERATIONS ******************
    } else if (operation.equals(OP_GET_ITEMS_FOR_TRIP)) {
      String tripId = req.getParameter("id");
      log.log(Level.INFO, "trip id = " + tripId);
      if (tripId != null) {
        Trip trip = getTrip(tripId);
        log.log(Level.INFO, "trip = " + trip);
        if (trip != null) {
          ArrayList<TripItem> tripItems = getTripItems(trip);
          log.log(Level.INFO, " total items = " + tripItems.size());
          for (TripItem logItem : tripItems) {
            log.log(Level.INFO, " item = " + logItem.getName());
          }
          Gson gson = new GsonBuilder().create();
          String responseStr = gson.toJson(tripItems);
          sendResponse(resp, responseStr);
        } else {
          resultCode = ERROR_DATA_UNAVAILABLE;
        }
      } else {
        resultCode = ERROR_INVALID_PARAM;
      }
    } else if (operation.equals(OP_GET_TRIP_ITEM)) {
      String itemId = req.getParameter("id");
      log.log(Level.INFO, "trip item id = " + itemId);
      if (itemId != null) {
        TripItem tripItem = getTripItem(itemId);
        log.log(Level.INFO, "trip item = " + tripItem);
        if (tripItem != null) {
          Gson gson = new GsonBuilder().create();
          String respStr = gson.toJson(tripItem);
          sendResponse(resp, respStr);
        } else {
          resultCode = ERROR_DATA_UNAVAILABLE;
        }
      } else {
        resultCode = ERROR_INVALID_PARAM;
      }
    } else if (operation.equals(OP_DELETE_TRIP_ITEM)) {
      String tripItemId = req.getParameter("id");
      log.log(Level.INFO, "trip item id = " + tripItemId);
      if (tripItemId != null) {
        // fetch the trip item
        TripItem tripItem = getTripItem(tripItemId);
        log.log(Level.INFO, "trip item = " + tripItem);
        if (tripItem != null) {
          try {
            TripItemUpdateResult result = tripItemUtils.deleteTripItem(tripItem, ldap);
          } catch (AuthorizationException e) {
            log.log(Level.INFO, e.getMessage());
            resultCode = ERROR_UNAUTHORIZED;
          } catch (TransactionFailedException e) {
            log.log(Level.INFO, e.getMessage());
            resultCode = ERROR_UNKNOWN;
          }
        } else {
          resultCode = ERROR_DATA_UNAVAILABLE;
        }
      } else {
        resultCode = ERROR_INVALID_PARAM;
      }
    } else if (operation.equals(OP_GET_COMMENTS_FOR_TRIP_ITEM)) {
      String tripItemId = req.getParameter("id");
      log.log(Level.INFO, "trip item id = " + tripItemId);
      if (tripItemId != null) {
        TripItem tripItem = getTripItem(tripItemId);
        log.log(Level.INFO, "trip item= " + tripItem);
        if (tripItem != null) {
          ArrayList<Comment> comments = getComments(tripItem);
          log.log(Level.INFO, " total comments = " + comments.size());
          for (Comment logComment : comments) {
            log.log(Level.INFO, " comment = " + logComment.getComment());
          }
          Gson gson = new GsonBuilder().create();
          String responseStr = gson.toJson(comments);
          sendResponse(resp, responseStr);
        } else {
          resultCode = ERROR_DATA_UNAVAILABLE;
        }
      } else {
        resultCode = ERROR_INVALID_PARAM;
      }
    } else if (operation.equals(OP_DELETE_COMMENT)) {
      String commentId = req.getParameter("id");
      log.log(Level.INFO, "comment id = " + commentId);
      if (commentId != null) {
        // fetch the trip item
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final Comment comment = pm.getObjectById(Comment.class, KeyFactory.stringToKey(commentId));
        log.log(Level.INFO, "comment = " + comment);
        if (comment != null) {
          try {
            CommentUpdateResult result = commentUtils.deleteComment(comment, ldap);
          } catch (AuthorizationException e) {
            log.log(Level.INFO, e.getMessage());
            resultCode = ERROR_UNAUTHORIZED;
          } catch (TransactionFailedException e) {
            log.log(Level.INFO, e.getMessage());
            resultCode = ERROR_UNKNOWN;
          }
        } else {
          resultCode = ERROR_DATA_UNAVAILABLE;
        }
      } else {
        resultCode = ERROR_INVALID_PARAM;
      }
    }

    // If a error has occurred then send proper response to client
    handleStatusCode(resp, resultCode);
  }

  @Override
  protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) {

    log.setLevel(Level.INFO);
    try {
      verifyRequest(req);
    } catch (OAuthException e) {
      log.log(Level.INFO, e.getMessage());
      handleStatusCode(resp, ERROR_UNAUTHENTICATED);
      return;
    } catch (URISyntaxException e) {
      log.log(Level.INFO, e.getMessage());
      handleStatusCode(resp, ERROR_INVALID_PARAM);
      return;
    } catch (IOException e) {
      log.log(Level.INFO, e.getMessage());
      handleStatusCode(resp, ERROR_UNKNOWN);
      return;
    }

    ldap = req.getParameter("ldap");
    log.log(Level.INFO, "ldap = " + ldap);
    if (ldap == null) {
      handleStatusCode(resp, ERROR_UNAUTHENTICATED);
      return;
    }

    int resultCode = ERROR_NONE;

    String operation = req.getParameter(OP);
    log.log(Level.INFO, "action = " + operation);
    if (operation == null) {
      resultCode = ERROR_INVALID_PARAM;
      handleStatusCode(resp, resultCode);
      return;
    }
    // ********** ADD TRIP ******************
    if (operation.equals(OP_ADD_TRIP)) {
      String tripJsonStr = req.getParameter("data");
      log.log(Level.INFO, "JSON received = " + tripJsonStr);
      if (tripJsonStr != null) {
        try {
          Gson gson = new GsonBuilder().create();
          Trip trip = gson.fromJson(tripJsonStr, Trip.class);
          log.log(Level.INFO, "trip = " + trip);
          if (trip.getStartDate() == null) {
            trip.setStartDate(SharedConstants.UNSCHEDULED_DATE);
          }
          Trip addedTrip = tripUtils.addTrip(trip, ldap);
          sendResponse(resp, addedTrip.getKey());
        } catch (JsonParseException e) {
          log.log(Level.INFO, e.getMessage());
          resultCode = ERROR_UNKNOWN;
        }
      } else {
        resultCode = ERROR_INVALID_PARAM;
      }
      // ********** UPDATE TRIP ******************
    } else if (operation.equals(OP_UPDATE_TRIP)) {
      String tripJsonStr = req.getParameter("data");
      log.log(Level.INFO, "JSON received = " + tripJsonStr);
      if (tripJsonStr != null) {
        Gson gson = new GsonBuilder().create();
        try {
          Trip trip = gson.fromJson(tripJsonStr, Trip.class);
          log.log(Level.INFO, "trip = " + trip);
          if (trip.getStartDate() == null) {
            trip.setStartDate(SharedConstants.UNSCHEDULED_DATE);
          }
          Trip updatedTrip = tripUtils.updateTrip(trip, ldap);
          sendResponse(resp, gson.toJson(updatedTrip));
        } catch (JsonParseException e) {
          log.log(Level.INFO, e.getMessage());
          resultCode = ERROR_UNKNOWN;
        } catch (AuthorizationException e) {
          log.log(Level.INFO, e.getMessage());
          resultCode = ERROR_UNAUTHORIZED;
        }
      } else {
        resultCode = ERROR_INVALID_PARAM;
      }
      // ********* SHARE TRIP *******************
    } else if (operation.equals(OP_SHARE_TRIP)) {

      String tripId = req.getParameter("id");
      log.log(Level.INFO, "trip id = " + tripId);
      if (tripId != null) {
        Gson gson = new GsonBuilder().create();
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final Trip trip = pm.detachCopy(pm.getObjectById(Trip.class, tripId));
        log.log(Level.INFO, "trip before sharing action= " + trip);
        if (trip != null) {
          HashSet<String> contributers = trip.getContributorIds();
          HashSet<String> viewers = trip.getViewerIds();
          String addJsonStr = req.getParameter("add");
          if (addJsonStr != null) {
            try {
              String[] addUsers = gson.fromJson(addJsonStr, String[].class);
              for (int i = 0; i < addUsers.length; i++) {
                String user = addUsers[i];
                if (!contributers.contains(user)) {
                  contributers.add(user);
                }
                if (!viewers.contains(user)) {
                  viewers.add(user);
                }
              }
            } catch (JsonParseException e) {
              log.log(Level.INFO, e.getMessage());
              resultCode = ERROR_UNKNOWN;
            }
          }
          String removeJsonStr = req.getParameter("remove");
          if (removeJsonStr != null) {
            try {
              String[] removeUsers = gson.fromJson(removeJsonStr, String[].class);
              for (int i = 0; i < removeUsers.length; i++) {
                String user = removeUsers[i];
                if (contributers.contains(user)) {
                  contributers.remove(user);
                }
                if (viewers.contains(user)) {
                  viewers.remove(user);
                }
              }
            } catch (JsonParseException e) {
              log.log(Level.INFO, e.getMessage());
              resultCode = ERROR_UNKNOWN;
            }
          }
          trip.setContributorIds(contributers);
          trip.setViewerIds(viewers);
          trip.setLastModified(new Date());
          trip.setLastModifiedBy(ldap);
          log.log(Level.INFO, "trip after sharing action= " + trip);
          pm.makePersistent(trip);
        } else {
          resultCode = ERROR_DATA_UNAVAILABLE;
        }
      } else {
        resultCode = ERROR_INVALID_PARAM;
      }
      // ********* SEND TRIP DETAILS IN MAILS *****************
    } else if (operation.equals(OP_MAIL_TRIP)) {
      String tripId = req.getParameter("id");
      log.log(Level.INFO, "trip id = " + tripId);
      if (tripId != null) {
        Trip trip = getTrip(tripId);
        log.log(Level.INFO, "trip = " + trip);
        if (trip != null) {
          Gson gson = new GsonBuilder().create();
          String recipientsJsonStr = req.getParameter("recipients");
          log.log(Level.INFO, "recipientsJsonStr = " + recipientsJsonStr);
          if (recipientsJsonStr != null) {
            try {
              final String[] recipients = gson.fromJson(recipientsJsonStr, String[].class);
              log.log(Level.INFO, "recipients = " + recipients);
              sendTripDetailsMail(trip, recipients, req.getParameter("message"));
            } catch (JsonParseException e) {
              resultCode = ERROR_UNKNOWN;
              e.printStackTrace();
            }
          } else {
            resultCode = ERROR_INVALID_PARAM;
          }
        } else {
          resultCode = ERROR_DATA_UNAVAILABLE;
        }
      } else {
        resultCode = ERROR_INVALID_PARAM;
      }
      // ********** ADD TRIP ITEM ******************
    } else if (operation.equals(OP_ADD_TRIP_ITEM)) {
      String tripItemJsonStr = req.getParameter("data");
      log.log(Level.INFO, "JSON received = " + tripItemJsonStr);
      if (tripItemJsonStr != null) {
        Gson gson = new GsonBuilder().create();
        try {
          TripItem tripItem = gson.fromJson(tripItemJsonStr, TripItem.class);
          log.log(Level.INFO, "trip item = " + tripItem);
          try {
            TripItemUpdateResult itemAddResult = tripItemUtils.addTripItem(tripItem, ldap);
            sendResponse(resp, gson.toJson(itemAddResult.getTripItem()));
          } catch (AuthorizationException e) {
            log.log(Level.INFO, e.getMessage());
            resultCode = ERROR_UNAUTHORIZED;
          } catch (TransactionFailedException e) {
            log.log(Level.INFO, e.getMessage());
            resultCode = ERROR_UNKNOWN;
          } catch (TripVersionException e) {
            log.log(Level.INFO, e.getMessage());
            resultCode = ERROR_UNKNOWN;
          }
        } catch (JsonParseException e) {
          log.log(Level.INFO, e.getMessage());
          resultCode = ERROR_UNKNOWN;
        }
      } else {
        resultCode = ERROR_INVALID_PARAM;
      }
      // ********** UPDATE TRIP ITEM ******************
    } else if (operation.equals(OP_UPDATE_TRIP_ITEM)) {
      String tripItemJsonStr = req.getParameter("data");
      log.log(Level.INFO, "JSON received = " + tripItemJsonStr);
      if (tripItemJsonStr != null) {
        Gson gson = new GsonBuilder().create();
        try {
          TripItem tripItem = gson.fromJson(tripItemJsonStr, TripItem.class);
          log.log(Level.INFO, "trip item = " + tripItem);
          TripItemUpdateResult itemUpdateResult = tripItemUtils.updateTripItem(tripItem, ldap);
          sendResponse(resp, gson.toJson(itemUpdateResult.getTripItem()));
        } catch (AuthorizationException e) {
          log.log(Level.INFO, e.getMessage());
          resultCode = ERROR_UNAUTHORIZED;
        } catch (TransactionFailedException e) {
          log.log(Level.INFO, e.getMessage());
          resultCode = ERROR_UNKNOWN;
        } catch (JsonParseException e) {
          log.log(Level.INFO, e.getMessage());
          resultCode = ERROR_UNKNOWN;
        }
      } else {
        resultCode = ERROR_INVALID_PARAM;
      }
    } else if (operation.equals(OP_ADD_COMMENT)) {
      String commentJsonStr = req.getParameter("data");
      log.log(Level.INFO, "JSON received = " + commentJsonStr);
      if (commentJsonStr != null) {
        Gson gson = new GsonBuilder().create();
        try {
          Comment comment = gson.fromJson(commentJsonStr, Comment.class);
          log.log(Level.INFO, "comment = " + comment);
          try {
            CommentUpdateResult commentAddResult = commentUtils.addComment(comment, ldap);
            sendResponse(resp, gson.toJson(commentAddResult.getComment()));
          } catch (AuthorizationException e) {
            log.log(Level.INFO, e.getMessage());
            resultCode = ERROR_UNAUTHORIZED;
          } catch (TransactionFailedException e) {
            log.log(Level.INFO, e.getMessage());
            resultCode = ERROR_UNKNOWN;
          }
        } catch (JsonParseException e) {
          log.log(Level.INFO, e.getMessage());
          resultCode = ERROR_UNKNOWN;
        }
      } else {
        resultCode = ERROR_INVALID_PARAM;
      }
    }
    // If a error has occurred then send proper response to client
    handleStatusCode(resp, resultCode);
  }

  private ArrayList<Trip> getTrips() {
    // send latest 20 trips accessible to user
    ArrayList<Trip> allTrips = tripUtils.fetchUpdatedTrips(new Date(0), ldap, null, 20).getTrips();
    ArrayList<Trip> activeTrips = new ArrayList<Trip>();
    for (Trip trip : allTrips) {
      if (trip.getStatus().equals(Status.ACTIVE)) {
        activeTrips.add(trip);
      }
    }
    return activeTrips;
  }

  private Trip getTrip(String tripId) {
    final PersistenceManager pm = PMF.get().getPersistenceManager();
    final Key key = KeyFactory.stringToKey(tripId);
    final Trip trip = pm.getObjectById(Trip.class, key);
    return pm.detachCopy(trip);
  }

  @SuppressWarnings("unchecked")
  TripItem getTripItem(final String itemId) {
    final PersistenceManager pm = PMF.get().getPersistenceManager();
    final ArrayList<Key> keys = new ArrayList<Key>();
    final Key k = KeyFactory.stringToKey(itemId);
    keys.add(k);
    Query q = pm.newQuery("select from " + TripItem.class.getName() + " where key == :keys");
    ArrayList<TripItem> items =
        (ArrayList<TripItem>) pm.detachCopyAll((List<TripItem>) q.execute(keys));
    return items.get(0);
  }

  @SuppressWarnings("unchecked")
  ArrayList<TripItem> getTripItems(final Trip trip) {
    final ArrayList<IdDayDateTuple> tuples = trip.getTripItemIds().getTuples();
    final ArrayList<Key> keys = new ArrayList<Key>(tuples.size());
    for (IdDayDateTuple tuple : tuples) {
      final Key k = KeyFactory.stringToKey(tuple.getId());
      keys.add(k);
    }
    if (keys.isEmpty()) {
      return new ArrayList<TripItem>();
    }

    final PersistenceManager pm = PMF.get().getPersistenceManager();
    Query q = pm.newQuery("select from " + TripItem.class.getName() + " where key == :keys");
    ArrayList<TripItem> items =
        (ArrayList<TripItem>) pm.detachCopyAll((List<TripItem>) q.execute(keys));
    return items;
  }

  @SuppressWarnings("unchecked")
  ArrayList<Comment> getComments(final TripItem tripItem) {
    final Trip trip = getTrip(tripItem.getTripId());
    final ArrayList<String> tripCommentIds = new ArrayList<String>();
    IdDateTupleList commentsTuple = trip.getCommentIds();
    for (IdDateTuple commentTuple : commentsTuple.getTuples()) {
      tripCommentIds.add(commentTuple.getId());
    }
    final ArrayList<Comment> allTripItemComments = commentUtils.fetchComments(tripCommentIds);
    final ArrayList<Comment> specifiedItemComments = new ArrayList<Comment>();
    for (Comment comment : allTripItemComments) {
      if (comment.getTripItemId().equals(tripItem.getKey())) {
        specifiedItemComments.add(comment);
      }
    }
    return specifiedItemComments;
  }

  private void sendTripDetailsMail(
      final Trip trip, final String[] recipients, final String userMessage) {
    log.log(Level.WARNING,
        "Number of recipients , allowed = " + TripServiceImpl.MAX_INVITE_EMAILS + ", found = "
            + recipients.length);
    Preconditions.checkArgument(recipients.length <= TripServiceImpl.MAX_INVITE_EMAILS,
        "Maximum of " + TripServiceImpl.MAX_INVITE_EMAILS + " invites are allowed in one request");


    final Properties props = new Properties();
    final Session session = Session.getDefaultInstance(props, null);
    for (String email : recipients) {
      final javax.mail.Message msg = new MimeMessage(session);

      try {
        msg.setFrom(new InternetAddress(Message.INVITE_SENDER_EMAIL, Message.INVITE_SENDER_NAME));
        msg.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(email));
        msg.setSubject(Message.inviteSubject(
            "Trippy itineraries : " + trip.getName() + " by " + trip.getOwnerName()));
        String msgText = "Trip data = " + trip.toString();
        if (userMessage != null) {
          msgText += (" User Message = " + userMessage);
        }
        msg.setText(msgText);
        Transport.send(msg);
      } catch (AddressException e) {
        e.printStackTrace();
      } catch (MessagingException e) {
        e.printStackTrace();
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }
    }
  }

  private void sendResponse(HttpServletResponse resp, String responseStr) {
    if (responseStr != null) {
      // set response length
      resp.setContentLength(responseStr.length());
      resp.setContentType("text/html");
      ServletOutputStream sos = null;
      try {
        // write the response
        sos = resp.getOutputStream();
        sos.write(responseStr.getBytes());
        sos.flush();
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        // close the stream
        if (sos != null) {
          try {
            sos.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    }
  }

  private void handleStatusCode(HttpServletResponse resp, int code) {
    log.log(Level.INFO, "Response code = " + code);
    try {
      switch (code) {
        case ERROR_NONE:
          break;
        case ERROR_UNAUTHORIZED:
          resp.sendError(
              HttpServletResponse.SC_UNAUTHORIZED, "Not authorized to perform this operation.");
          break;
        case ERROR_UNAUTHENTICATED:
          resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthenticated user");
          break;
        case ERROR_DATA_UNAVAILABLE:
          resp.sendError(HttpServletResponse.SC_NO_CONTENT, "No data available for given request.");
          break;
        case ERROR_INVALID_PARAM:
          resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid param(s).");
          break;
        case ERROR_UNKNOWN:
          resp.sendError(
              HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Some internal error occured.");
          break;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Referenced from
   * http://wiki.opensocial.org/index.php?title=Validating_Signed_Requests
   */
  private void verifyRequest(HttpServletRequest request)
      throws OAuthException, IOException, URISyntaxException {

    String method = request.getMethod();
    String requestUrl = getRequestUrl(request);
    List<OAuth.Parameter> requestParameters = getRequestParameters(request);

    OAuthServiceProvider provider = new OAuthServiceProvider(null, null, null);

    OAuthConsumer consumer = new OAuthConsumer(null, "www.google.com", null, provider);
    consumer.setProperty(RSA_SHA1.X509_CERTIFICATE, IGOOGLE_CERTIFICATE);

    OAuthMessage message = new OAuthMessage(method, requestUrl, requestParameters);

    OAuthAccessor accessor = new OAuthAccessor(consumer);

    message.validateMessage(accessor, new SimpleOAuthValidator());
  }

  /**
   * Constructs and returns the full URL associated with the passed request
   * object. Referenced from
   * http://wiki.opensocial.org/index.php?title=Validating_Signed_Requests
   *
   * @param request Servlet request object with methods for retrieving the
   *        various components of the request URL
   */
  public static String getRequestUrl(HttpServletRequest request) {
    StringBuilder requestUrl = new StringBuilder();
    String scheme = request.getScheme();
    int port = request.getLocalPort();

    requestUrl.append(scheme);
    requestUrl.append("://");
    requestUrl.append(request.getServerName());

    requestUrl.append(request.getContextPath());
    requestUrl.append(request.getServletPath());

    return requestUrl.toString();
  }

  /**
   * Constructs and returns a List of OAuth.Parameter objects, one per parameter
   * in the passed request. Referenced from
   * http://wiki.opensocial.org/index.php?title=Validating_Signed_Requests
   *
   * @param request Servlet request object with methods for retrieving the full
   *        set of parameters passed with the request
   */
  public static List<OAuth.Parameter> getRequestParameters(HttpServletRequest request) {

    List<OAuth.Parameter> parameters = new ArrayList<OAuth.Parameter>();

    for (Object e : request.getParameterMap().entrySet()) {
      Map.Entry<String, String[]> entry = (Map.Entry<String, String[]>) e;

      for (String value : entry.getValue()) {
        parameters.add(new OAuth.Parameter(entry.getKey(), value));
      }
    }
    return parameters;
  }
}
