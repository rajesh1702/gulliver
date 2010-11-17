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
import com.google.gwt.maps.client.HasMap;
import com.google.gwt.maps.client.base.HasLatLng;
import com.google.gwt.maps.client.base.HasLatLngBounds;
import com.google.gwt.maps.client.overlay.HasMarker;
import com.google.mobile.trippy.web.client.view.View;

import java.util.List;

/**
 * Presenter to control the map
 * 
 * 
 */
public class MapPresenter implements BasePresenter<MapPresenter.Display> {

  /**
   * Interface to allow setting click listeners on map
   */
  public interface MapClickListener {
    void onMapClick(HasLatLng position);
  }

  /**
   * Interface to allow setting click listeners on map
   */
  public interface MarkerClickListener {
    void onMarkerClick(HasMarker marker);
  }
  /**
   * Display for presenter
   */
  public interface Display extends View {
    HasMarker newMarker(HasLatLng position);

    void setCenter(HasLatLng center);

    void setBounds(HasLatLngBounds bounds);

    void clearOverlays();

    HasMap getMap();

    HasLatLng newLatLng(double lat, double lng);

    HasLatLngBounds newLatLngBounds(HasLatLng southWest, HasLatLng northEast);

    void setMapClickListener(final MapClickListener mapClickListener);
    
    void setMarkerClickListener(HasMarker marker, MarkerClickListener markerClickListener);
    
    void setMarkerImage(int markerIndex, String imageUrl);

    HasLatLngBounds getMapBounds();

    HasMarker getMarker(int index);
  }

  private final Display display;

  public MapPresenter(Display display) {
    this.display = display;
  }
  
  /**
   * Add a marker to the map
   */
  public HasMarker addMarker(double lat, double lng) {
    return display.newMarker(getLatLng(lat, lng));
  }
  
  public void clearMap() {
    display.clearOverlays();
  }
  
  /**
   * Calculate the bounds that best fits the specified points
   */
  public HasLatLngBounds getBounds(List<HasLatLng> points) {
    if (points != null && points.size() > 1) {
      HasLatLng pointZero = points.get(0);
      HasLatLng pointOne = points.get(1);
      double northEastLat = Math.max(pointZero.getLatitude(), pointOne.getLatitude());
      double northEastLng = Math.max(pointZero.getLongitude(), pointOne.getLongitude());
      double southWestLat = Math.min(pointZero.getLatitude(), pointOne.getLatitude());
      double southWestLng = Math.min(pointZero.getLongitude(), pointOne.getLongitude());

      for (int i = 2, n = points.size(); i < n; i++) {
        final HasLatLng point = points.get(i);
        final double lat = point.getLatitude();
        final double lng = point.getLongitude();
        if (lat > northEastLat) {
          northEastLat = lat;
        }
        if (lng > northEastLng) {
          northEastLng = lng;
        }
        if (lat < southWestLat) {
          southWestLat = lat;
        }
        if (lng < southWestLng) {
          southWestLng = lng;
        }
      }
      final HasLatLng northEast = display.newLatLng(northEastLat, northEastLng);
      final HasLatLng southWest = display.newLatLng(southWestLat, southWestLng);
      return display.newLatLngBounds(southWest, northEast);
    } else {
      return null;
    }
  }
  
  @Override
  public Display getDisplay() {
    return display;
  }

  /**
   * Create a HasLatLng from double values
   */
  public HasLatLng getLatLng(double lat, double lng) {
    return display.newLatLng(lat, lng);
  }

  /**
   * Create a HasLatLngBounds from double values
   */
  public HasLatLngBounds getLatLngBounds(double southLat, double westLng, double northLat,
      double eastLng) {
    return display.newLatLngBounds(getLatLng(southLat, westLng), getLatLng(northLat, eastLng));
  }
 
  /**
   * Calculate bounds for the points and set that bounds on the map
   */
  public void setBounds(List<HasLatLng> points) {
    Preconditions.checkNotNull(points);
    
    switch(points.size()) {
      case 0:
        throw new IllegalArgumentException("Insufficient points");
      case 1:
        setCenter(points.get(0));
        break;
      default:
        display.setBounds(getBounds(points));
    }
  }
  
  /**
   * Set the center of the map
   */
  public void setCenter(HasLatLng center) {
    display.setCenter(center);
  }

  /**
   * Set a click listener on the map
   */
  public void setMapClickListener(MapClickListener mapClickListener) {
    display.setMapClickListener(mapClickListener);
  }
  
  /**
   * Set a click listener on the map
   */
  public void setMarkerClickListener(HasMarker marker, MarkerClickListener markerClickListener) {
    display.setMarkerClickListener(marker, markerClickListener);
  }
  
  public void setMarkerImage(int index, String imageUrl) {
    display.setMarkerImage(index, imageUrl);
  }
  
  public HasLatLngBounds getMapBounds() {
    return display.getMapBounds();
  }
  
  public HasMarker getMarker(int index) {
    return display.getMarker(index);
  }
}
