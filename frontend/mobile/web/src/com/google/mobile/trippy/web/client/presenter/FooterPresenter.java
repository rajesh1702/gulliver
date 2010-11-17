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

package com.google.mobile.trippy.web.client.presenter;

import com.google.common.base.Preconditions;
import com.google.gwt.event.shared.HandlerManager;
import com.google.mobile.trippy.web.client.base.SingletonComponents;
import com.google.mobile.trippy.web.client.view.View;

/**
 * Creates footer with the username of logged in user and logout link. 
 * 
 */
public class FooterPresenter implements EventHandlerPresenter<FooterPresenter.Display>  {

  /** Interface for view of footer panel. */
  public static interface Display extends View {
    void setUserName(final String title);
    void setLogoutLink(final String title);
  }
  
  protected final Display display;
  protected final HandlerManager eventBus;

  
  public FooterPresenter(final Display display, final SingletonComponents singletonComponents) {
    Preconditions.checkNotNull(singletonComponents, "singletonComponents");
    
    this.display = Preconditions.checkNotNull(display, "display");
    this.eventBus = Preconditions.checkNotNull(singletonComponents.getEventBus());
    
    display.setUserName(singletonComponents.getUtils().getUserEmail());
    display.setLogoutLink(singletonComponents.getUtils().getLogoutUrl());
  }
  
  @Override
  public void bind() {
    // no-op.    
  }

  @Override
  public HandlerManager getEventBus() {
    return eventBus;
  }

  @Override
  public void release() {
    // no-op.
  }

   @Override
  public Display getDisplay() {
    return display;
  }
}
