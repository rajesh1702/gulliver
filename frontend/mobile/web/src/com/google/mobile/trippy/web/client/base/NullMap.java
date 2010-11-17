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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.maps.client.HasMap;
import com.google.gwt.maps.client.HasMapOptions;
import com.google.gwt.maps.client.base.HasLatLng;
import com.google.gwt.maps.client.base.HasLatLngBounds;
import com.google.gwt.maps.client.overlay.HasProjection;
import com.google.gwt.user.client.Element;

public class NullMap implements HasMap {

  @Override
  public void fitBounds(HasLatLngBounds bounds) {
    throw new IllegalStateException("Illegal invocation");
  }

  @Override
  public HasLatLngBounds getBounds() {
    throw new IllegalStateException("Illegal invocation");
  }

  @Override
  public HasLatLng getCenter() {
    throw new IllegalStateException("Illegal invocation");
  }

  @Override
  public Element getDiv() {
    throw new IllegalStateException("Illegal invocation");
  }

  @Override
  public String getMapTypeId() {
    throw new IllegalStateException("Illegal invocation");
  }

  @Override
  public int getZoom() {
    throw new IllegalStateException("Illegal invocation");
  }

  @Override
  public void panBy(int x, int y) {
    throw new IllegalStateException("Illegal invocation");
  }

  @Override
  public void panTo(HasLatLng latLng) {
    throw new IllegalStateException("Illegal invocation");
  }

  @Override
  public void panToBounds(HasLatLngBounds bounds) {
    throw new IllegalStateException("Illegal invocation");
  }

  @Override
  public void setCenter(HasLatLng latLng) {
    throw new IllegalStateException("Illegal invocation");
  }

  @Override
  public void setMapTypeId(String mapTypeId) {
    throw new IllegalStateException("Illegal invocation");
  }

  @Override
  public void setOptions(HasMapOptions options) {
    throw new IllegalStateException("Illegal invocation");
  }

  @Override
  public void setZoom(int zoom) {
    throw new IllegalStateException("Illegal invocation");
  }

  @Override
  public JavaScriptObject getJso() {
    return null;
  }

  @Override
  public HasProjection getProjection() {
    throw new IllegalStateException("Illegal invocation");
  }
}
