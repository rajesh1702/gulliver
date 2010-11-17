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

import com.google.gwt.maps.client.geocoder.HasGeocoder;
import com.google.gwt.maps.client.geocoder.HasGeocoderRequest;
import com.google.mobile.trippy.web.client.base.Provider;
import com.google.mobile.trippy.web.client.base.SingletonComponents;
import com.google.mobile.trippy.web.client.view.CreateNewTripView;

/**
 *  Provider for Create New Trip Presenter.
 * 
 *
 */
public class CreateNewTripProvider implements Provider<CreateNewTripPresenter> {

  private final SingletonComponents singletonComponents;
  private final HasGeocoder geocoder;
  private final Provider<HasGeocoderRequest> geocoderRequestProvider;
  
  public CreateNewTripProvider(final SingletonComponents singletonComponents,
      final HasGeocoder geocoder,
      final Provider<HasGeocoderRequest> geocoderRequestProvider) {
    this.singletonComponents = singletonComponents;
    this.geocoder = geocoder;
    this.geocoderRequestProvider = geocoderRequestProvider; 
  }

  @Override
  public CreateNewTripPresenter get() {
    final CreateNewTripView display = new CreateNewTripView();
    return new CreateNewTripPresenter(display, singletonComponents, geocoder,
        geocoderRequestProvider);
  }

}
