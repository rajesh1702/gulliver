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

import com.google.mobile.trippy.web.client.base.Provider;
import com.google.mobile.trippy.web.client.view.MapView;

/**
 * Provider for Map Presenter.
 * 
 *
 */
public class MapProvider implements Provider<MapPresenter> {

  private MapPresenter.Display display;
  public MapProvider(MapPresenter.Display display) {
    this.display = display;
  }
  
  /**
   * Create a new presenter. If a specific display was given in constructor use
   * that, else create a new display
   */
  @Override
  public MapPresenter get() {
    if (display == null) {
      display = new MapView();
    }
    return new MapPresenter(display);
  }
}
