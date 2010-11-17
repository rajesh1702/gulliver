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

package com.google.mobile.trippy.web.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.mobile.trippy.web.client.base.DefaultSingletonComponents;
import com.google.mobile.trippy.web.client.base.SingletonComponents;
import com.google.mobile.trippy.web.client.base.Utils;
import com.google.mobile.trippy.web.client.db.LocalDbService;
import com.google.mobile.trippy.web.client.db.TripService;
import com.google.mobile.trippy.web.client.event.ShowHomePageEvent;
import com.google.mobile.trippy.web.client.i18n.Message;
import com.google.mobile.trippy.web.client.widget.Toast;

import java.util.Date;

/**
 * Entry point for the class.
 *
 */
public class Trippy implements EntryPoint {
  
  //Sync db after every 60 secs.
  private static final int SYNC_PERIOD = 60000;
  
  private final SingletonComponents singletonComponent = new DefaultSingletonComponents();
  private HandlerManager eventBus;
  private static TripService tripService;
  private Toast toast;
  private Utils utils;
  private Message messages;
  
  /**
   * Entry point classes define <code>onModuleLoad()</code>.
   * 
   * Method acts as an entry point for the application.
   * 
   * The method initialize the necessary components in following sequence:
   * 1. Initialize Local DB and other components + Sync with Remote DB.
   * 2. Create Controller class.
   * 3. Bind Controller class for listening several Screen Level events.
   * 4. Create object of HistoryTokenHandler (ValueChangeHandler) over token 
   *    String. it will listen for Browser Back Button event.
   * 5. Adding above object to History.
   */
  public void onModuleLoad() {
    init();  
    
    // Creating controller class and binding it for listening several Screen Level events.
    Controller controller = new Controller(singletonComponent);
    controller.bind();
    
    HistoryTokenHandler historyTokenHandler = new HistoryTokenHandler(singletonComponent);
    History.addValueChangeHandler(historyTokenHandler);
    
    String token = Window.Location.getHash().replaceFirst("#", "");
    if (token != null && token.length() > 0) {
      historyTokenHandler.showPageForToken(token);
    } else {
      eventBus.fireEvent(new ShowHomePageEvent());
    }
  }
  
  private void init(){
    eventBus = singletonComponent.getEventBus();
    tripService = singletonComponent.getTripService();
    toast = singletonComponent.getToast();
    utils = singletonComponent.getUtils();
    messages = singletonComponent.getMessage();
    
    // Expose methods to android shell app.
    AndroidProxy proxy = new AndroidProxy(eventBus);
    proxy.setDoSearch(proxy);
    
    final String cls = Window.Location.getParameter("cls");
    if (cls != null && cls.toLowerCase().equals("true")) {
      LocalDbService.clearDb();
    }
    initDb();
    
    utils.addOnlineHandler(new ValueChangeHandler<Boolean>() {
      @Override
      public void onValueChange(final ValueChangeEvent<Boolean> event) {
        if (event.getValue()) {
          toast.showToast(messages.onLineStatusMsg());
        } else {
          toast.showToast(messages.offLineStatusMsg());
        }
      }

    });

    utils.startCheckOnlineTimer();
//    utils.startLocationUpdateTimer();
    
    toast.showLoading(messages.loading());
    syncFromRemoteDb(new AsyncCallback<Void>() {
      @Override
      public void onSuccess(Void result) {
        toast.hideLoading();
      }
      @Override
      public void onFailure(Throwable caught) {
        toast.hideLoading();
      }
    });
    
    final Timer timer = new Timer() {
      
      @Override
      public void run() {
        syncFromRemoteDb();
      }
    };
    // Sync db after every 60 secs.
    timer.scheduleRepeating(SYNC_PERIOD);
  }
  
  private void syncFromRemoteDb() {
    syncFromRemoteDb(new AsyncCallback<Void>() {
      @Override
      public void onSuccess(Void result) {
      }
      @Override
      public void onFailure(Throwable caught) {
      }
    });
  }
  
  private void initDb() {
    final String versionTime = getVersionTimestamp();
    final String serverVersion = getServerVersion();
    if (versionTime == null || serverVersion == null) {
      LocalDbService.clearDb();
      setVersionTimestamp(new Date().getTime() + "");
    } else if (versionTime.compareTo(serverVersion) < 0) {
      LocalDbService.clearDb();
      setVersionTimestamp(serverVersion);
    }
  }

  private native String getServerVersion() /*-{
    if (typeof($wnd.versionTime) === "undefined") {
    return null;
    }
    return $wnd.versionTime;
  }-*/;
  
  /**
   * Incremental sync from remote db to client db.
   */
  private void syncFromRemoteDb(final AsyncCallback<Void> callback) {
    tripService.syncFromRemoteDb(new AsyncCallback<Void>() {
      @Override
      public void onFailure(Throwable caught) {
        callback.onFailure(caught);
      }
      @Override
      public void onSuccess(Void result) {
        // TODO: Show message.
      }
    });
  }

  //TODO: make the string constant
  public static void setVersionTimestamp(final String versionTimestamp) {
    LocalDbService.makePersistent("app_version", versionTimestamp);
  }
  
  public static String getVersionTimestamp() {
    return LocalDbService.getPersistent("app_version");
  }
}
