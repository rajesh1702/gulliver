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

package com.google.mobile.trippy.web.client.widget;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.maps.client.HasMap;
import com.google.gwt.maps.client.base.HasInfoWindow;
import com.google.gwt.maps.client.base.HasInfoWindowOptions;
import com.google.gwt.maps.client.base.HasLatLng;
import com.google.gwt.maps.client.event.EventCallback;
import com.google.gwt.maps.client.mvc.HasMVCObject;
import com.google.mobile.trippy.web.client.presenter.BasePresenter;
import com.google.mobile.trippy.web.client.view.View;

/**
 * Presenter for map marker info window
 * 
 *
 */
public class InfoWindowPresenter implements HasInfoWindow,
    BasePresenter<InfoWindowPresenter.Display> {

  /** Interface for view of this presenter*/
  public interface Display extends View {
    void setContent(String content);

    String getContent();

    void setClickHandler(EventCallback callback);

    void hidePopup();

    void showPopup(HasLatLng position);
  }

  private final Display display;
  private HasLatLng position;

  public InfoWindowPresenter(final Display display) {
    this.display = display;
  }

  @Override
  public void close() {
    display.hidePopup();
  }

  @Override
  public String getContent() {
    return display.getContent();
  }

  @Override
  public HasLatLng getPosition() {
    return position;
  }

  @Override
  public int getZIndex() {
    return 0;
  }

  @Override
  public void open(HasMap map, HasMVCObject anchor) {
    display.showPopup(position);
  }

  @Override
  public void setContent(String html) {
    if (html != null) {
      display.setContent(html);
    }
  }

  @Override
  public void setOptions(HasInfoWindowOptions options) {
    // no op
  }

  @Override
  public void setPosition(HasLatLng position) {
    this.position = position;
  }

  @Override
  public void setZIndex(int zIndex) {
    // no op
  }

  @Override
  public JavaScriptObject getJso() {
    throw new IllegalStateException("Not a JSO");
  }

  public void setClickHandler(EventCallback callback) {
    display.setClickHandler(callback);
  }

  @Override
  public Display getDisplay() {
    return display;
  }
}
