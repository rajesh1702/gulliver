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

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * Merge client resources to reduce load time.
 * 
 */
public interface TrippyBundle extends ClientBundle {
  public static final TrippyBundle INSTANCE = GWT.create(TrippyBundle.class);

  ImageResource arrowIcon();
  
  ImageResource bluePushPin();

  ImageResource buttonNormal();
  
  ImageResource buttonPressed();
  
  ImageResource callIcon();
  
  ImageResource editIcon();
  
  ImageResource greenDot();
  
  ImageResource homeIcon();

  ImageResource iconActivities();

  ImageResource iconEntertainment();

  ImageResource iconGeneral();
  
  ImageResource iconHotel();
  
  ImageResource iconLonelyPlanet();
  
  ImageResource iconMinus();
  
  ImageResource iconPlus();
  
  ImageResource iconRestaurant();
  
  ImageResource iconSearch();
  
  ImageResource iconShopping();
  
  ImageResource iconShowDetails();
  
  ImageResource iconShowOnMap();
  
  ImageResource iconSights();

  ImageResource iconStopWhite();

  ImageResource infoIcon();

  ImageResource listIcon();

  ImageResource mapItemIcon();
  
  ImageResource mapMarkerHighlighted();
  
  ImageResource mapMarkerNormal();
  
  ImageResource nextItem();
  
  ImageResource pinLPdo();
  
  ImageResource pinLPentertainment();
  
  ImageResource pinLPgeneral();
  
  ImageResource pinLPhotels();
  
  ImageResource pinLPrestaurants();
  
  ImageResource pinLPshopping();
  
  ImageResource pinLPsights();

  ImageResource prevItem();

  ImageResource plusIcon();
    
  ImageResource shareIcon();

  ImageResource signoutIcon();

  ImageResource trash();
  
  ImageResource showOnMap();
  
  ImageResource calendar();

  ImageResource commentIcon();

  ImageResource thumbsupIcon();
  
  ImageResource downArrow();

  /**
   * Common css styles needed in multiple views	
   */
  CommonCss commonStyle();
  interface CommonCss extends CssResource {
    String button();
    String backNavigatorButton();
    String baseFont();
    String titleFont();
    String colorFont();
  }
}
