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

package com.google.mobile.trippy.web.server.i18n;


/**
 * Messages to be localized.
 * 
 */
public class Message {
  
  public static final String PRECONDITION_TRIP_NAME_NULL = "Trip name must be provided.";
  public static final String PRECONDITION_TRIP_LOCATION_NULL = "Trip location must be provided.";
  public static final String PRECONDITION_TRIP_LATITUDE_NULL = "Trip latitude must be provided.";
  public static final String PRECONDITION_TRIP_LONGITUDE_NULL = "Trip latitude must be provided.";
  public static final String PRECONDITION_TRIP_PLACEID_NULL = "Trip lonely planet place id must be provided.";
  public static final String PRECONDITION_TRIP_NOTH_LATITUDE_NULL = "Trip north boundary latitude must be provided.";
  public static final String PRECONDITION_TRIP_SOUTH_LATITUDE_NULL = "Trip south boundary latitude must be provided.";
  public static final String PRECONDITION_TRIP_EAST_LONGITUDE_NULL = "Trip east boundary longitude must be provided.";
  public static final String PRECONDITION_TRIP_WEST_LONGITUDE_NULL = "Trip east boundary longitude must be provided.";
  
  public static final String PRECONDITION_ADDED_ON_NULL = "Added-on property must be provided.";
  public static final String PRECONDITION_LAST_MODIFIED_NULL = "Last modified property must be provided.";
  public static final String PRECONDITION_LAST_MODIFIED_BY_NULL = "Last modified-by property must be provided.";
  public static final String PRECONDITION_STATUS_NULL = "Status property must be provided.";
  
  public static final String INVITE_SENDER_EMAIL = "trippy-admin@google.com";
  public static final String INVITE_SENDER_NAME = "Trippy Admin";
  public static final String INVITE_EXPIRED = "Your invitation has expired or already used.";
  
  public static final String inviteMsgLine1(String inviteFrom) {
    return "Trippy invitation from " + inviteFrom;
  }
  public static final String inviteMsgLine2(String inviteUrl) {
    return "Open following link in your mobile browser to accept the invitation : " + inviteUrl;
  }
  public static final String inviteSubject(String inviteFrom) {
    return "Trippy invite from " + inviteFrom;
  }
}
