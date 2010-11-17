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

package com.google.mobile.trippy.web.client.base;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.mobile.trippy.web.client.db.CommentService;
import com.google.mobile.trippy.web.client.db.DefaultCommentService;
import com.google.mobile.trippy.web.client.db.DefaultTripItemService;
import com.google.mobile.trippy.web.client.db.DefaultTripService;
import com.google.mobile.trippy.web.client.db.TripItemService;
import com.google.mobile.trippy.web.client.db.TripService;
import com.google.mobile.trippy.web.client.i18n.Message;
import com.google.mobile.trippy.web.client.widget.DefaultToast;
import com.google.mobile.trippy.web.client.widget.Toast;
import com.google.mobile.trippy.web.shared.DefaultUserUtils;
import com.google.mobile.trippy.web.shared.UserUtils;

/**
 * Implementation of container for all the singleton components of trippy app
 * 
 *
 */
public class DefaultSingletonComponents implements SingletonComponents {
  private final Message messages = GWT.create(Message.class);
  private final HandlerManager eventBus = new HandlerManager(null);
  private final CommentService commentService = new DefaultCommentService(eventBus);
  private final TripItemService tripItemService = new DefaultTripItemService(eventBus);
  private final TripService tripService = new DefaultTripService(eventBus);
  private final Utils utils = DefaultUtils.getInstance();
  private final UserUtils userUtils = DefaultUserUtils.getInstance();
  private final Toast toast = new DefaultToast();
  
  private static SingletonComponents instance = new DefaultSingletonComponents();

  public static SingletonComponents getInstance() {
    return instance;
  }

  @Override
  public CommentService getCommentService() {
    return commentService;
  }

  @Override
  public HandlerManager getEventBus() {
    return eventBus;
  }

  @Override
  public Message getMessage() {
    return messages;
  }

  @Override
  public TripItemService getTripItemService() {
    return tripItemService;
  }

  @Override
  public TripService getTripService() {
    return tripService;
  }

  @Override
  public Utils getUtils() {
    return utils;
  }

  @Override
  public UserUtils getUserUtils() {
    return userUtils;
  }

  @Override
  public Toast getToast() {
    return toast;
  }
}
