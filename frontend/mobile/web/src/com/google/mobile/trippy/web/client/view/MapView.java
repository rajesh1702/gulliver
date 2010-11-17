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

package com.google.mobile.trippy.web.client.view;

import com.google.gwt.maps.client.ControlPosition;
import com.google.gwt.maps.client.HasMap;
import com.google.gwt.maps.client.HasMapOptions;
import com.google.gwt.maps.client.HasMapTypeControlOptions;
import com.google.gwt.maps.client.HasNavigationControlOptions;
import com.google.gwt.maps.client.HasScaleControlOptions;
import com.google.gwt.maps.client.MapOptions;
import com.google.gwt.maps.client.MapTypeControlOptions;
import com.google.gwt.maps.client.MapTypeControlStyle;
import com.google.gwt.maps.client.MapTypeId;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.NavigationControlOptions;
import com.google.gwt.maps.client.NavigationControlStyle;
import com.google.gwt.maps.client.ScaleControlOptions;
import com.google.gwt.maps.client.ScaleControlStyle;
import com.google.gwt.maps.client.base.HasLatLng;
import com.google.gwt.maps.client.base.HasLatLngBounds;
import com.google.gwt.maps.client.base.LatLng;
import com.google.gwt.maps.client.base.LatLngBounds;
import com.google.gwt.maps.client.event.Event;
import com.google.gwt.maps.client.event.HasMouseEvent;
import com.google.gwt.maps.client.event.MouseEventCallback;
import com.google.gwt.maps.client.impl.ControlPositionImpl;
import com.google.gwt.maps.client.impl.MapTypeControlStyleImpl;
import com.google.gwt.maps.client.impl.NavigationControlStyleImpl;
import com.google.gwt.maps.client.impl.ScaleControlStyleImpl;
import com.google.gwt.maps.client.overlay.HasMarker;
import com.google.gwt.maps.client.overlay.HasMarkerImage;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.MarkerImage;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.mobile.trippy.web.client.TrippyBundle;
import com.google.mobile.trippy.web.client.base.NullMap;
import com.google.mobile.trippy.web.client.presenter.MapPresenter;
import com.google.mobile.trippy.web.client.presenter.MapPresenter.MapClickListener;
import com.google.mobile.trippy.web.client.presenter.MapPresenter.MarkerClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * View for the map presenter. Performs operations on the map
 * 
 */
public class MapView extends Composite implements MapPresenter.Display {

  public static final HasLatLng DEFAULT_CENTER = new LatLng(28.613889, 77.208889);
  public static final int DEFAULT_ZOOM = 13;

  private MapWidget mapWidget;

  private List<HasMarker> markers;
  
  public MapView() {
    this(DEFAULT_CENTER, DEFAULT_ZOOM);
  }

  public MapView(final HasLatLng center, final int zoom) {
    super();
    mapWidget = createMapWidget(DEFAULT_CENTER, DEFAULT_ZOOM);
    mapWidget.setSize("100%", "100%");
    getMap().setCenter(center);
    getMap().setZoom(zoom);
    
    initWidget(mapWidget);
  }
 
  @Override
  public Widget asWidget() {
    return this;
  }
  
  @Override
  public final void clearOverlays() {
    if (markers == null || markers.isEmpty()) {
      return;
    }
    NullMap nullMap = new NullMap();
    for (HasMarker marker : markers) {
      marker.setMap(nullMap);
    }
    markers.clear();
    nullMap = null;
  }

  @Override
  public final HasMap getMap() {
    return mapWidget.getMap();
  }

  @Override
  public HasLatLng newLatLng(double lat, double lng) {
    return new LatLng(lat, lng);
  }
  
  @Override
  public HasLatLngBounds newLatLngBounds(HasLatLng southWest, HasLatLng northEast) {
    return new LatLngBounds(southWest, northEast);
  }
  
  /**
   * Create a new marker on the map.
   * The marker is by default clickable and non-draggable
   */
  @Override
  public final HasMarker newMarker(HasLatLng position) {
    HasMarker marker = new Marker();
    marker.setPosition(position);
    marker.setMap(getMap());
    marker.setDraggable(false);
    marker.setClickable(true);
    marker.setIcon(
        new MarkerImage.Builder(TrippyBundle.INSTANCE.mapMarkerNormal().getURL()).build());
    if (markers == null) {
      markers = new ArrayList<HasMarker>();
    }
    markers.add(marker);
    return marker;
  }
  
  @Override
  public void setBounds(HasLatLngBounds bounds) {
    mapWidget.fitBounds(bounds);
    
  }

  @Override
  public void setCenter(HasLatLng center) {
    getMap().setCenter(center);
    
  }
  
  @Override
  public void setMarkerClickListener(
      final HasMarker marker, final MarkerClickListener markerClickListener) {
    Event.addListener(marker, HasMarker.Event.CLICK.getValue(), new MouseEventCallback() {

      @Override
      public void callback(HasMouseEvent event) {
        markerClickListener.onMarkerClick(marker);
      }
    });
  }
  
  @Override
  public void setMapClickListener(final MapClickListener mapClickListener) {
    Event.addListener(getMap(), "click", new MouseEventCallback() {
      
      @Override
      public void callback(HasMouseEvent event) {
        mapClickListener.onMapClick(event.getLatLng());
      }
    });
  }

  private MapWidget createMapWidget(final HasLatLng center, final int zoom) {

    String mapTypeId = new MapTypeId().getRoadmap();

    HasNavigationControlOptions navOptions =
        new NavigationControlOptions(new ControlPositionImpl(), new NavigationControlStyleImpl());
    navOptions.setPosition(ControlPosition.BOTTOM);
    navOptions.setStyle(NavigationControlStyle.ANDROID);

    HasScaleControlOptions scaleOptions =
        new ScaleControlOptions(new ControlPositionImpl(), new ScaleControlStyleImpl());
    scaleOptions.setStyle(ScaleControlStyle.DEFAULT);

    HasMapTypeControlOptions mapTypeOptions =
        new MapTypeControlOptions(new ControlPositionImpl(), new MapTypeControlStyleImpl());
    mapTypeOptions.setStyle(MapTypeControlStyle.DROPDOWN_MENU);

    HasMapOptions options = new MapOptions();
    options.setCenter(center);
    options.setZoom(zoom);
    options.setMapTypeId(mapTypeId);
    options.setBackgroundColor("white");
    options.setNavigationControl(true);
//    options.setNavigationControlOptions(navOptions);
    options.setScaleControl(false);
//    options.setScaleControlOptions(scaleOptions);
    options.setMapTypeControl(true);
    options.setMapTypeControlOptions(mapTypeOptions);
    options.setDraggable(true);
    return new MapWidget(options);

  }

  @Override
  public void setMarkerImage(int markerIndex, String imageUrl)
      throws ArrayIndexOutOfBoundsException {
    if (markers != null && !markers.isEmpty()) {
      HasMarker marker = markers.get(markerIndex);
      final HasMarkerImage image = new MarkerImage.Builder(imageUrl).build();
      marker.setIcon(image);
    }
  }

  @Override
  public HasMarker getMarker(int index)
      throws ArrayIndexOutOfBoundsException {
    if (markers != null && !markers.isEmpty()) {
      return markers.get(index);
    }
    return null;
  }
  
  @Override
  public HasLatLngBounds getMapBounds() {
    return getMap().getBounds();
  }
}
