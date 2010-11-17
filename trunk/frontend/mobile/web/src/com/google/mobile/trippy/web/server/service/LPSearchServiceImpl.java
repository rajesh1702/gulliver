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

package com.google.mobile.trippy.web.server.service;

import com.google.appengine.repackaged.com.google.common.base.Preconditions;
import com.google.appengine.repackaged.com.google.common.util.Base64;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.gwt.user.server.rpc.UnexpectedException;
import com.google.inject.Inject;
import com.google.mobile.trippy.web.client.service.LPSearchService;
import com.google.mobile.trippy.web.shared.exception.MissingAttributeException;
import com.google.mobile.trippy.web.shared.models.POI;
import com.google.mobile.trippy.web.shared.models.POIDetail;
import com.google.mobile.trippy.web.shared.models.Place;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;

/**
 * Search service to get Lonely Planet content.
 * 
 */
@SuppressWarnings("serial")
public class LPSearchServiceImpl extends RemoteServiceServlet implements LPSearchService {

  private DocumentBuilder dbfInst;

  @Inject
  public LPSearchServiceImpl(DocumentBuilder dbfInst) {
    this.dbfInst = dbfInst;
  }

  private static final Logger logger = Logger.getLogger(LPSearchServiceImpl.class.getName());

  // URL templates for Lonely Planet's REST api.
  private static final String LP_PLACE_URL = "http://apigateway.lonelyplanet.com/api/places?name=";
  private static final String LP_POI_URL =
      "http://apigateway.lonelyplanet.com/api/places/{placeID}/pois?poi_type={poiType}";
  private static final String LP_BOUNDING_BOX_POI_URL =
      "http://apigateway.lonelyplanet.com/api/bounding_boxes/"
          + "{north},{south},{east},{west}/pois?poi_type={type-name}";
  private static final String LP_POI_DETAIL_URL =
      "http://apigateway.lonelyplanet.com/api/pois/{poi-id}";

  // Response XML tags.
  private static final String LP_PLACE = "place";
  private static final String LP_PLACE_ID = "id";
  private static final String LP_PLACE_FULLNAME = "full-name";
  private static final String LP_PLACE_SHORTNAME = "short-name";
  private static final String LP_PLACE_NORTH_LAT = "north-latitude";
  private static final String LP_PLACE_SOUTH_LAT = "south-latitude";
  private static final String LP_PLACE_EAST_LONG = "east-longitude";
  private static final String LP_PLACE_WEST_LONG = "west-longitude";

  private static final String LP_POI = "poi";
  private static final String LP_POI_ID = "id";
  private static final String LP_POI_NAME = "name";
  private static final String LP_POI_TYPE = "poi-type";
  private static final String LP_POI_LAT = "latitude";
  private static final String LP_POI_LONG = "longitude";

  private static final String LP_POI_ADDR = "address";
  private static final String LP_POI_ADDR_STREET = "street";
  private static final String LP_POI_ADDR_LOCALITY = "locality";
  private static final String LP_POI_ADDR_POSTALCODE = "postcode";
  private static final String LP_POI_ADDR_EXTRAS = "extras";
  private static final String LP_POI_PHONES = "telephones";
  private static final String LP_POI_PHONE = "telephone";
  private static final String LP_POI_PHONE_AREACODE = "area-code";
  private static final String LP_POI_PHONE_NUMBER = "number";
  private static final String LP_POI_SEARCH_URLS = "representations";
  private static final String LP_POI_SEARCH_URL = "representation";
  private static final String LP_POI_SEARCH_URL_TYPE = "msite";
  private static final String LP_POI_REVIEW = "review";
  // TODO: place data store LP key here.
  private static final String LP_KEY = "dummy LP Key";

  /**
   * Get places, matching given string, using LonelyPlanet RESTful service.
   * 
   * Sample XML :
   * 
   * <pre>
   * &lt;places&gt;
   *   &lt;place&gt;
   *      &lt;id&gt;31010&lt;/id&gt;
   *      &lt;full-name&gt;Pacific -&gt; Australia -&gt; New South Wales -&gt; Sydney&lt;
   *            /full-name&gt;
   *      &lt;short-name&gt;Sydney&lt;/short-name&gt;
   *      &lt;north-latitude&gt;-33.747799&lt;/north-latitude&gt;
   *      &lt;south-latitude&gt;-33.9673&lt;/south-latitude&gt;
   *      &lt;east-longitude&gt;151.376007&lt;/east-longitude&gt;
   *      &lt;west-longitude&gt;151.054993&lt;/west-longitude&gt;
   *   &lt;/place&gt;
   *   &lt;place&gt;
   *      &lt;id&gt;29135&lt;/id&gt;
   *      &lt;full-name&gt;North America -&gt; Canada -&gt; Nova Scotia -&gt; 
   *            Cape Breton Island -&gt; Sydney&lt;/full-name&gt;
   *      &lt;short-name&gt;Sydney&lt;/short-name&gt;
   *      &lt;north-latitude&gt;46.149038&lt;/north-latitude&gt;
   *      &lt;south-latitude&gt;46.110969&lt;/south-latitude&gt;
   *      &lt;east-longitude&gt;-60.161304&lt;/east-longitude&gt;
   *      &lt;west-longitude&gt;-60.230484&lt;/west-longitude&gt;
   *   &lt;/place&gt;
   * &lt;/places&gt;
   * </pre>
   * 
   * @return list of places
   */
  @Override
  public ArrayList<Place> getPlaces(String placeStr) throws MissingAttributeException, IOException {
    Preconditions.checkNotNull(placeStr, "Place must be provided.");
    Preconditions.checkArgument(!placeStr.isEmpty(), "Place must not be empty.");
    final ArrayList<Place> places = new ArrayList<Place>();
    try {
      final URL url = new URL(LP_PLACE_URL + placeStr);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      /*
       * mJ1pMpMk2AFq4EZ5dbuAoA => OAuth key for app "Trippy"
       * TfrrOxqWY7kGdvRwaSHWIX8E4RTgUSwFjX2Ojjhq9gA => consumer secret for app
       * "Trippy' The OAuth key is used as username and consumer secret is used
       * as password for basid authentication. The key and secret are provided
       * when a application is registered on lonely planet developer forum
       */
      connection.setRequestProperty("Authorization", "Basic " + Base64.encode((LP_KEY).getBytes()));
      final Document doc = dbfInst.parse(connection.getInputStream());

      final NodeList placeNodes = doc.getElementsByTagName(LP_PLACE);

      for (int i = 0, n = placeNodes.getLength(); i < n; ++i) {
        final Element placeElement = (Element) placeNodes.item(i);
        final Place place = new Place();

        final NodeList placeIdNodes = placeElement.getElementsByTagName(LP_PLACE_ID);
        if (placeIdNodes.getLength() == 0) {
          throw new MissingAttributeException(LP_PLACE_ID);
        }
        final Node placeIdNode = placeIdNodes.item(0);
        place.setId(Long.parseLong(placeIdNode.getFirstChild().getNodeValue()));

        final NodeList placeFullNameNodes = placeElement.getElementsByTagName(LP_PLACE_FULLNAME);
        if (placeFullNameNodes.getLength() == 0) {
          throw new MissingAttributeException(LP_PLACE_FULLNAME);
        }
        final Node placeFullNameNode = placeFullNameNodes.item(0);
        place.setFullName(placeFullNameNode.getFirstChild().getNodeValue());

        final NodeList placeShortNameNodes = placeElement.getElementsByTagName(LP_PLACE_SHORTNAME);
        if (placeShortNameNodes.getLength() == 0) {
          throw new MissingAttributeException(LP_PLACE_SHORTNAME);
        }
        final Node placeShortNameNode = placeShortNameNodes.item(0);
        place.setShortName(placeShortNameNode.getFirstChild().getNodeValue());

        final NodeList placeNorthLatNodes = placeElement.getElementsByTagName(LP_PLACE_NORTH_LAT);
        if (placeNorthLatNodes.getLength() == 0) {
          throw new MissingAttributeException(LP_PLACE_NORTH_LAT);
        }
        final Node placeNorthLatNode = placeNorthLatNodes.item(0).getFirstChild();
        if (placeNorthLatNode == null) {
          throw new MissingAttributeException(LP_PLACE_NORTH_LAT);
        }
        place.setNorthLatitude(Double.parseDouble(placeNorthLatNode.getNodeValue()));

        NodeList placeSouthLatNodes = placeElement.getElementsByTagName(LP_PLACE_SOUTH_LAT);
        if (placeSouthLatNodes.getLength() == 0) {
          throw new MissingAttributeException(LP_PLACE_SOUTH_LAT);
        }
        Node placeSouthLatNode = placeSouthLatNodes.item(0).getFirstChild();
        if (placeSouthLatNode == null) {
          throw new MissingAttributeException(LP_PLACE_SOUTH_LAT);
        }
        place.setSouthLatitude(Double.parseDouble(placeSouthLatNode.getNodeValue()));

        final NodeList placeEastLongNodes = placeElement.getElementsByTagName(LP_PLACE_EAST_LONG);
        if (placeEastLongNodes.getLength() == 0) {
          throw new MissingAttributeException(LP_PLACE_EAST_LONG);
        }
        final Node placeEastLongNode = placeEastLongNodes.item(0).getFirstChild();
        if (placeEastLongNode == null) {
          throw new MissingAttributeException(LP_PLACE_EAST_LONG);
        }
        place.setEastLongitude(Double.parseDouble(placeEastLongNode.getNodeValue()));

        final NodeList placeWestLongNodes = placeElement.getElementsByTagName(LP_PLACE_WEST_LONG);
        if (placeWestLongNodes.getLength() == 0) {
          throw new MissingAttributeException(LP_PLACE_WEST_LONG);
        }
        final Node placeWestLongNode = placeWestLongNodes.item(0).getFirstChild();
        if (placeWestLongNode == null) {
          throw new MissingAttributeException(LP_PLACE_WEST_LONG);
        }
        place.setWestLongitude(Double.parseDouble(placeWestLongNode.getNodeValue()));

        places.add(place);
      }
    } catch (MissingAttributeException e) {
      logger.warning(e.getMessage());
      throw e;
    } catch (IOException e) {
      e.printStackTrace();
      throw e;
    } catch (SAXException e) {
      e.printStackTrace();
      throw new UnexpectedException(e.getMessage(), e);
      // } catch (ParserConfigurationException e) {
      // e.printStackTrace();
      // throw new UnexpectedException(e.getMessage(), e);
    }
    return places;
  }

  /**
   * Search places-of-interest in the given bounding box values
   * ({north},{south},{east},{west}) and place of type (POIType).
   * 
   * Sample XML :
   * 
   * <pre>
   * &lt;pois&gt;
   *  &lt;poi&gt;
   *    &lt;id&gt;367907&lt;/id&gt;
   *    &lt;name&gt;Coogee Bay Hotel&lt;/name&gt;
   *    &lt;digital-latitude&gt;-33.920940998916&lt;/digital-latitude&gt;
   *    &lt;digital-longitude&gt;151.256557703018&lt;/digital-longitude&gt;
   *    &lt;poi-type&gt;Night&lt;/poi-type&gt;
   *  &lt;/poi&gt;
   *  &lt;poi&gt;
   *    &lt;id&gt;367711&lt;/id&gt;
   *    &lt;name&gt;New Theatre&lt;/name&gt;
   *    &lt;digital-latitude&gt;-33.9031756797962&lt;/digital-latitude&gt;
   *    &lt;digital-longitude&gt;151.179843842983&lt;/digital-longitude&gt;
   *    &lt;poi-type&gt;Night&lt;/poi-type&gt;
   *  &lt;/poi&gt;
   * &lt;/pois&gt;
   * </pre>
   */
  @Override
  public ArrayList<POI> searchPOIByBoundingBox(double north, double south, double east,
      double west, String poiType) throws MissingAttributeException, IOException {
    final ArrayList<POI> pois = new ArrayList<POI>();
    Preconditions.checkNotNull(north, "North-latitude must be provided.");
    Preconditions.checkNotNull(south, "South-latitude must be provided.");
    Preconditions.checkNotNull(east, "East-longitude must be provided.");
    Preconditions.checkNotNull(west, "West-longitude must be provided.");
    Preconditions.checkNotNull(poiType, "Place of intrest type must be provided.");
    Preconditions.checkArgument(!poiType.isEmpty(), "Place of intrest type must not be empty.");
    try {
      final URL url =
          new URL(LP_BOUNDING_BOX_POI_URL.replace("{north}", "" + north).replace("{south}",
              "" + south).replace("{east}", "" + east).replace("{west}", "" + west).replace(
              "{type-name}", poiType));

      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      /*
       * mJ1pMpMk2AFq4EZ5dbuAoA => OAuth key for app "Trippy"
       * TfrrOxqWY7kGdvRwaSHWIX8E4RTgUSwFjX2Ojjhq9gA => consumer secret for app
       * "Trippy' The OAuth key is used as username and consumer secret is used
       * as password for basid authentication. The key and secret are provided
       * when a application is registered on lonely planet developer forum
       */
      connection.setRequestProperty("Authorization", "Basic " + Base64.encode((LP_KEY).getBytes()));
      final Document doc = dbfInst.parse(connection.getInputStream());

      final NodeList poiNodes = doc.getElementsByTagName(LP_POI);
      pois.addAll(parseForPOI(poiNodes));
    } catch (MissingAttributeException e) {
      logger.warning(e.getMessage());
      throw e;
    } catch (IOException e) {
      e.printStackTrace();
      throw e;
    } catch (SAXException e) {
      e.printStackTrace();
      throw new UnexpectedException(e.getMessage(), e);
    }
    return pois;
  }

  /**
   * Search places-of-interest in the given place and of type (POIType).
   * 
   * Sample XML :
   * 
   * <pre>
   * &lt;pois&gt;
   *  &lt;poi&gt;
   *    &lt;id&gt;367907&lt;/id&gt;
   *    &lt;name&gt;Coogee Bay Hotel&lt;/name&gt;
   *    &lt;digital-latitude&gt;-33.920940998916&lt;/digital-latitude&gt;
   *    &lt;digital-longitude&gt;151.256557703018&lt;/digital-longitude&gt;
   *    &lt;poi-type&gt;Night&lt;/poi-type&gt;
   *  &lt;/poi&gt;
   *  &lt;poi&gt;
   *    &lt;id&gt;367711&lt;/id&gt;
   *    &lt;name&gt;New Theatre&lt;/name&gt;
   *    &lt;digital-latitude&gt;-33.9031756797962&lt;/digital-latitude&gt;
   *    &lt;digital-longitude&gt;151.179843842983&lt;/digital-longitude&gt;
   *    &lt;poi-type&gt;Night&lt;/poi-type&gt;
   *  &lt;/poi&gt;
   * &lt;/pois&gt;
   * </pre>
   * 
   * @throws MissingAttributeException
   */
  @Override
  public ArrayList<POI> searchPOI(long placeId, String poiType) throws MissingAttributeException,
      IOException {
    final ArrayList<POI> pois = new ArrayList<POI>();
    Preconditions.checkNotNull(placeId, "Place id must be provided.");
    Preconditions.checkNotNull(poiType, "Place of intrest type must be provided.");
    Preconditions.checkArgument(!poiType.isEmpty(), "Place of intrest type must not be empty.");
    try {
      final URL url =
          new URL(LP_POI_URL.replace("{placeID}", "" + placeId).replace("{poiType}", poiType));
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      /*
       * mJ1pMpMk2AFq4EZ5dbuAoA => OAuth key for app "Trippy"
       * TfrrOxqWY7kGdvRwaSHWIX8E4RTgUSwFjX2Ojjhq9gA => consumer secret for app
       * "Trippy' The OAuth key is used as username and consumer secret is used
       * as password for basid authentication. The key and secret are provided
       * when a application is registered on lonely planet developer forum
       */
      connection.setRequestProperty("Authorization", "Basic " + Base64.encode((LP_KEY).getBytes()));
      final Document doc = dbfInst.parse(connection.getInputStream());

      final NodeList poiNodes = doc.getElementsByTagName(LP_POI);
      pois.addAll(parseForPOI(poiNodes));
    } catch (MissingAttributeException e) {
      logger.warning(e.getMessage());
      throw e;
    } catch (IOException e) {
      e.printStackTrace();
      throw e;
    } catch (SAXException e) {
      e.printStackTrace();
      throw new UnexpectedException(e.getMessage(), e);
    }
    return pois;
  }

  /**
   * Parses for POI given PoiNodes
   */
  private ArrayList<POI> parseForPOI(NodeList poiNodes) throws MissingAttributeException {
    final ArrayList<POI> pois = new ArrayList<POI>();
    try {
      for (int i = 0, n = poiNodes.getLength(); i < n; ++i) {
        final POI poi = new POI();
        final Element poiElement = (Element) poiNodes.item(i);

        final NodeList poiIdNodes = poiElement.getElementsByTagName(LP_POI_ID);
        if (poiIdNodes.getLength() == 0) {
          throw new MissingAttributeException(LP_POI_ID);
        }
        final Node poiIdNode = poiIdNodes.item(0);
        poi.setId(Long.parseLong(poiIdNode.getFirstChild().getNodeValue()));

        final NodeList poiNameNodes = poiElement.getElementsByTagName(LP_POI_NAME);
        if (poiNameNodes.getLength() == 0) {
          throw new MissingAttributeException(LP_POI_NAME);
        }
        final Node poiNameNode = poiNameNodes.item(0);
        poi.setName(poiNameNode.getFirstChild().getNodeValue());

        final NodeList poiTypeNodes = poiElement.getElementsByTagName(LP_POI_TYPE);
        if (poiTypeNodes.getLength() == 0) {
          throw new MissingAttributeException(LP_POI_TYPE);
        }
        final Node poiTypeNode = poiTypeNodes.item(0);
        poi.setPoiType(poiTypeNode.getFirstChild().getNodeValue());

        final NodeList poiLatNodes = poiElement.getElementsByTagName(LP_POI_LAT);
        if (poiLatNodes.getLength() == 0) {
          throw new MissingAttributeException(LP_POI_LAT);
        }
        final Node poiLatNode = poiLatNodes.item(0).getFirstChild();
        if (poiLatNode == null) {
          continue;
        }
        poi.setLatitude(Double.parseDouble(poiLatNode.getNodeValue()));

        final NodeList poiLngNodes = poiElement.getElementsByTagName(LP_POI_LONG);
        if (poiLngNodes.getLength() == 0) {
          throw new MissingAttributeException(LP_POI_LONG);
        }
        final Node poiLngNode = poiLngNodes.item(0).getFirstChild();
        if (poiLngNode == null) {
          continue;
        }
        poi.setLongitude(Double.parseDouble(poiLngNode.getNodeValue()));

        pois.add(poi);
      }
    } catch (MissingAttributeException e) {
      logger.warning(e.getMessage());
      throw e;
    }
    return pois;
  }

  /**
   * Get complete detail for a place.
   * 
   * getPlaces above returns only short snippets of a POI. This service is used
   * on event to add search result to the trip.
   * 
   * Sample :
   * 
   * <pre>
   *  &lt;poi&gt;
   *    &lt;name&gt;Cannon Bar&lt;/name&gt;
   *    &lt;alt-name/&gt;
   *    &lt;digital-latitude&gt;36.1394949838271&lt;/digital-latitude&gt;
   *    &lt;digital-longitude&gt;-5.3531742095947&lt;/digital-longitude&gt;
   *    &lt;emails&gt;
   *    &lt;/emails&gt;
   *    &lt;hours/&gt;
   *    &lt;prac-string&gt;
   *      &lt;icon type=&quot;phone&quot;&gt;tel&lt;/icon&gt;77288; 27 Cannon Lane; mains £5.50-9.50
   *    &lt;/prac-string&gt;
   *    &lt;price-range&gt;2&lt;/price-range&gt;
   *    &lt;price-string/&gt;
   *    &lt;urls&gt;
   *    &lt;/urls&gt;
   *    &lt;poi-type&gt;Eat&lt;/poi-type&gt;
   *    &lt;address&gt;
   *      &lt;street&gt;27 Cannon Lane&lt;/street&gt;
   *      &lt;locality&gt;Town Centre&lt;/locality&gt;
   *      &lt;postcode/&gt;
   *      &lt;extras/&gt;
   *    &lt;/address&gt;
   *    &lt;review&gt;
   *      &lt;p&gt;Justly famous for some of the best fish and chips in town, and 
   *            in big portions.&lt;/p&gt; 
   *    &lt;/review&gt;
   *    &lt;telephones&gt;
   *      &lt;telephone&gt;
   *        &lt;area-code/&gt;
   *        &lt;number&gt;77288&lt;/number&gt;
   *        &lt;text&gt;tel, info&lt;/text&gt;
   *      &lt;/telephone&gt;
   *    &lt;/telephones&gt;
   *    &lt;transports&gt;
   *    &lt;/transports&gt;
   *    &lt;representations&gt;
   *      &lt;representation type=&quot;msite&quot; href=&quot;http://m.lonelyplanet.com/
   *            et-1000587244&quot;/&gt;
   *      &lt;representation type=&quot;msite.touch&quot; href=&quot;http://touch.lonelyplanet.com/
   *            et-1000587244&quot;/&gt;
   *      &lt;representation type=&quot;lp.com&quot; href=&quot;http://www.lonelyplanet.com/
   *            poiRedirector?poiId=363499&quot;/&gt;
   *    &lt;/representations&gt;
   *  &lt;/poi&gt;
   * </pre>
   */
  public POIDetail getPOI(final long poiId) throws MissingAttributeException, IOException {
    final POIDetail detail = new POIDetail();
    Preconditions.checkNotNull(poiId, "Place of intrest id must be provided.");
    try {
      final URL url = new URL(LP_POI_DETAIL_URL.replace("{poi-id}", "" + poiId));

      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      /*
       * mJ1pMpMk2AFq4EZ5dbuAoA => OAuth key for app "Trippy"
       * TfrrOxqWY7kGdvRwaSHWIX8E4RTgUSwFjX2Ojjhq9gA => consumer secret for app
       * "Trippy' The OAuth key is used as username and consumer secret is used
       * as password for basid authentication. The key and secret are provided
       * when a application is registered on lonely planet developer forum
       */
      connection.setRequestProperty("Authorization", "Basic " + Base64.encode((LP_KEY).getBytes()));

      final Document doc = dbfInst.parse(connection.getInputStream());

      final NodeList poiNodes = doc.getElementsByTagName(LP_POI);

      String docText = doc.getTextContent();
      // System.out.println(docText);

      if (poiNodes.getLength() == 0) {
        throw new MissingAttributeException(LP_POI);
      }
      final Element poiElement = (Element) poiNodes.item(0);

      detail.setId(poiId);

      final NodeList poiNameNodes = poiElement.getElementsByTagName(LP_POI_NAME);
      if (poiNameNodes.getLength() == 0) {
        throw new MissingAttributeException(LP_POI_NAME);
      }
      final Node poiNameNode = poiNameNodes.item(0);
      detail.setName(poiNameNode.getFirstChild().getNodeValue());

      final NodeList poiTypeNodes = poiElement.getElementsByTagName(LP_POI_TYPE);
      if (poiTypeNodes.getLength() == 0) {
        throw new MissingAttributeException(LP_POI_TYPE);
      }
      final Node poiTypeNode = poiTypeNodes.item(0);
      detail.setPoiType(poiTypeNode.getFirstChild().getNodeValue());

      final NodeList poiLatNodes = poiElement.getElementsByTagName(LP_POI_LAT);
      if (poiLatNodes.getLength() == 0) {
        throw new MissingAttributeException(LP_POI_LAT);
      }
      final Node poiLatNode = poiLatNodes.item(0).getFirstChild();
      if (poiLatNode == null) {
        throw new MissingAttributeException(LP_POI_LAT);
      }
      detail.setLatitude(Double.parseDouble(poiLatNode.getNodeValue()));

      final NodeList poiLngNodes = poiElement.getElementsByTagName(LP_POI_LONG);
      if (poiLngNodes.getLength() == 0) {
        throw new MissingAttributeException(LP_POI_LONG);
      }
      final Node poiLngNode = poiLngNodes.item(0).getFirstChild();
      if (poiLngNode == null) {
        throw new MissingAttributeException(LP_POI_LAT);
      }
      detail.setLongitude(Double.parseDouble(poiLngNode.getNodeValue()));

      final NodeList poiAddressNodes = poiElement.getElementsByTagName(LP_POI_ADDR);
      if (poiAddressNodes.getLength() > 0) {
        detail.setAddress(getAddress((Element) poiAddressNodes.item(0)));
      }

      final NodeList poiPhonesNodes = poiElement.getElementsByTagName(LP_POI_PHONES);
      if (poiPhonesNodes.getLength() > 0) {
        detail.setPhones(getPhones((Element) poiPhonesNodes.item(0)));
      }

      final NodeList poiSearchUrlNodes = poiElement.getElementsByTagName(LP_POI_SEARCH_URLS);
      if (poiSearchUrlNodes.getLength() > 0) {
        detail.setSearchResultUrl(getSearchUrl((Element) poiSearchUrlNodes.item(0)));
      }

      final NodeList poiReviewNodes = poiElement.getElementsByTagName(LP_POI_REVIEW);
      if (poiReviewNodes.getLength() > 0) {
        detail.setReview(getReview((Element) poiReviewNodes.item(0)));
      }
    } catch (MissingAttributeException e) {
      e.printStackTrace();
      throw e;
    } catch (IOException e) {
      e.printStackTrace();
      throw e;
    } catch (SAXException e) {
      e.printStackTrace();
      throw new UnexpectedException(e.getMessage(), e);
    }
    return detail;
  }

  /**
   * Build address string out of given XML Element Tree.
   * 
   * Sample :
   * 
   * <pre>
   *    &lt;address&gt;
   *      &lt;street&gt;27 Cannon Lane&lt;/street&gt;
   *      &lt;locality&gt;Town Centre&lt;/locality&gt;
   *      &lt;postcode/&gt;
   *      &lt;extras/&gt;
   *    &lt;/address&gt;
   * </pre>
   */
  private String getAddress(Element address) {
    final StringBuilder addressStr = new StringBuilder();
    final NodeList poiStreetNodes = address.getElementsByTagName(LP_POI_ADDR_STREET);
    if (poiStreetNodes.getLength() > 0) {
      final Node addressLineNode = poiStreetNodes.item(0).getFirstChild();
      addressStr.append(addressLineNode == null ? "" : addressLineNode.getNodeValue());
    }
    final NodeList poiLocalityNodes = address.getElementsByTagName(LP_POI_ADDR_LOCALITY);
    if (poiLocalityNodes.getLength() > 0) {
      final Node addressLineNode = poiLocalityNodes.item(0).getFirstChild();
      final String addressToAppend = addressLineNode == null ? "" : addressLineNode.getNodeValue();
      if (!addressStr.toString().trim().isEmpty() && !addressToAppend.trim().isEmpty()) {
        addressStr.append(", ");
      }
      addressStr.append(addressToAppend);
    }
    final NodeList poiPostalCodeNodes = address.getElementsByTagName(LP_POI_ADDR_POSTALCODE);
    if (poiPostalCodeNodes.getLength() > 0) {
      final Node addressLineNode = poiPostalCodeNodes.item(0).getFirstChild();
      final String addressToAppend = addressLineNode == null ? "" : addressLineNode.getNodeValue();
      if (!addressStr.toString().trim().isEmpty() && !addressToAppend.trim().isEmpty()) {
        addressStr.append(", ");
      }
      addressStr.append(addressToAppend);
    }
    final NodeList poiExtrasNodes = address.getElementsByTagName(LP_POI_ADDR_EXTRAS);
    final Node addressLineNode = poiExtrasNodes.item(0).getFirstChild();
    final String addressToAppend = addressLineNode == null ? "" : addressLineNode.getNodeValue();
    if (!addressStr.toString().trim().isEmpty() && !addressToAppend.trim().isEmpty()) {
      addressStr.append(", ");
    }
    addressStr.append(addressToAppend);
    return addressStr.toString();
  }

  /**
   * Build phones list out of given Element Tree.
   * 
   * Sample :
   * 
   * <pre>
   *    &lt;telephones&gt;
   *      &lt;telephone&gt;
   *        &lt;area-code/&gt;
   *        &lt;number&gt;77288&lt;/number&gt;
   *        &lt;text&gt;tel, info&lt;/text&gt;
   *      &lt;/telephone&gt;
   *    &lt;/telephones&gt;
   * </pre>
   */
  private ArrayList<String> getPhones(final Element telephones) {
    final ArrayList<String> phones = new ArrayList<String>();
    final NodeList phoneNodes = telephones.getElementsByTagName(LP_POI_PHONE);

    for (int i = 0, n = phoneNodes.getLength(); i < n; ++i) {
      final Element phoneElement = (Element) phoneNodes.item(i);
      final StringBuilder phoneStr = new StringBuilder();
      final NodeList phoneAreaCodeNodes = phoneElement.getElementsByTagName(LP_POI_PHONE_AREACODE);
      if (phoneAreaCodeNodes.getLength() > 0) {
        final Node phoneAreaCodeNode = phoneAreaCodeNodes.item(0).getFirstChild();
        phoneStr.append(phoneAreaCodeNode == null ? "" : phoneAreaCodeNode.getNodeValue());
      }
      final NodeList phoneNumberNodes = phoneElement.getElementsByTagName(LP_POI_PHONE_NUMBER);
      if (phoneNumberNodes.getLength() > 0) {
        final Node phoneNumberNode = phoneNumberNodes.item(0).getFirstChild();
        phoneStr.append(phoneNumberNode == null ? "" : phoneNumberNode.getNodeValue());
      }

      phones.add(phoneStr.toString());
    }
    return phones;
  }

  /**
   * Build Search url out of given Element tree.
   * 
   * Sample XML :
   * 
   * <pre>
   *    &lt;representations&gt;
   *      &lt;representation type=&quot;msite&quot; href=&quot;http://m.lonelyplanet.com/
   *            et-1000587244&quot;/&gt;
   *      &lt;representation type=&quot;msite.touch&quot; href=&quot;http://touch.lonelyplanet.com/
   *            et-1000587244&quot;/&gt;
   *      &lt;representation type=&quot;lp.com&quot; href=&quot;http://www.lonelyplanet.com/
   *            poiRedirector?poiId=363499&quot;/&gt;
   *    &lt;/representations&gt;
   * </pre>
   */
  private String getSearchUrl(Element reprs) {
    final NodeList repsentations = reprs.getElementsByTagName(LP_POI_SEARCH_URL);
    for (int i = 0, n = repsentations.getLength(); i < n; ++i) {
      final Node repsentation = repsentations.item(i);
      if (repsentation.getAttributes().getNamedItem("type").getNodeValue().equals(
          LP_POI_SEARCH_URL_TYPE)) {
        return repsentation.getAttributes().getNamedItem("href").getNodeValue();
      }
    }
    return "";
  }

  private String getReview(Element rev) {
    return rev.getFirstChild().getNodeValue();
  }
}
