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

package com.google.mobile.trippy.web.server.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.mobile.trippy.web.server.service.LPSearchServiceImpl;
import com.google.mobile.trippy.web.server.service.task.POIDbCacheManagerServlet;
import com.google.mobile.trippy.web.server.service.task.PlacesDbCacheManagerServlet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 */
public class TrippyServerModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(PlacesDbCacheManagerServlet.class).in(Singleton.class);
    bind(POIDbCacheManagerServlet.class).in(Singleton.class);
    bind(LPSearchServiceImpl.class).in(Singleton.class);
  }
  
  @Provides
  DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
    final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    return dbf.newDocumentBuilder();
  }
}
