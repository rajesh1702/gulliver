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

package com.google.mobile.trippy.web.client.presenter.header;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.mobile.trippy.web.client.presenter.EventHandlerPresenter;
import com.google.mobile.trippy.web.client.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all header options presenters.
 * 
 */
public abstract class BaseHeaderOptionsPresenter implements
    EventHandlerPresenter<BaseHeaderOptionsPresenter.Display> {
  
  /**
   * Base view interface for Header options.
   */
  public interface Display extends View {
    HasClickHandlers getSearchOption();
  }
  
  protected static final List<HandlerRegistration> HANDLERS = new ArrayList<HandlerRegistration>();
  private final Display display;

  protected BaseHeaderOptionsPresenter(final Display display) {
    this.display = display;
  }
  
  public void setSearchAction(final Runnable action) {
    HANDLERS.add(display.getSearchOption().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        action.run();
      }
    }));
  }

  @Override
  public void release() {
    for (HandlerRegistration handler : HANDLERS) {
      if (handler != null) {
        handler.removeHandler();
      }
    }
    HANDLERS.clear();    
  }
}
