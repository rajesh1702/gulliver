/**
 * @fileoverview Code to handle lonely planet related functionality.
 * 
 */

/**
 * Authorization key used for accessing lonely planet resources.
 * @const
 * @type {string}
 */
var AUTHORIZATION_KEY = 'KEY';

/**
 * Stores the timer id.
 * @const
 * @type {string}
 */
var timerLonelyCallback;

/**
 * To store current searched category for lonely planet search.
 */
var poiCategory;

/**
 * Holds response object.
 * @type {Object}
 */
var xmlDoc;

/**
 * Search the points of interest according to lonely planet api.
 * @param {string} target Search parameter e.g. london.
 */
function searchPoi(target) {
  _gel('loading-container').innerHTML = _gel('tpl-loading').value;
  poiCategory = target;
  var url = 'http://apigateway.lonelyplanet.com/api/places?name=' +
            _esc(getTripById(gCurrentTripsData.currentTripId).loc);
  var params = {};
  params[gadgets.io.RequestParameters.AUTHORIZATION] =
      gadgets.io.AuthorizationType.NONE;
  params[gadgets.io.RequestParameters.REFRESH_INTERVAL] = 1;
  params[gadgets.io.RequestParameters.HEADERS] =
      {'Authorization' : AUTHORIZATION_KEY};
  params[gadgets.io.RequestParameters.CONTENT_TYPE] =
      gadgets.io.ContentType.DOM;
  // Timeout after 15 seconds.
  timerLonelyCallback =
      window.setTimeout(showLPAuthorizationError, 15000);
  gadgets.io.makeRequest(url, callbackSearchPOI, params);
}

/**
 * Method to show lonely planet authorization error.
 */
function showLPAuthorizationError() {
  var message = prefs.getMsg('auth_error') +
      '<a href="javascript:window.location.reload();">' +
      prefs.getMsg('try_again') + '</a>';
  _gel('loading-container').innerHTML = '';
  _gel('server-msg').innerHTML = '';
  showServerMessage({message: message});
}

/**
 * Converts the string response into DOM object.
 * @param {Object} data Response as string.
 */
function domParser(data) {
  if (window.DOMParser) {
    xmlDoc = new DOMParser().parseFromString(data.text, 'text/xml');
  } else { // Internet Explorer
    xmlDoc = new ActiveXObject('Microsoft.XMLDOM');
    xmlDoc.loadXML(data.text);
  }
}

/**
 * Callback for searching the latitude and longitude.
 * @param {Object} data Response object.
 */
function callbackSearchPOI(data) {
  window.clearTimeout(timerLonelyCallback);
  var north, south, east, west;
  var mapLocation =
      getTripById(gCurrentTripsData.currentTripId).loc.toLowerCase();
  if (data.text) {
    var track = 0;
    domParser(data);
    if (xmlDoc) {
      var nodeData = '';
      var items = xmlDoc.getElementsByTagName('place');
      var shortName;
      if (items && items.length) {
        for (var i = 0, j = items.length; i < j; i++) {
          shortName = xmlDoc.getElementsByTagName('short-name')[i];
          if (shortName) {
            shortName = shortName.childNodes[0].nodeValue.toLowerCase();
            if (shortName == mapLocation) {
              // Used to check whether the results matches the
              //  map location otherwise it will pick the first result.
              track = i;
            }
            break;
          }
        }
        north = getNodeData('north-latitude', track);
        south = getNodeData('south-latitude', track);
        east = getNodeData('east-longitude', track);
        west = getNodeData('west-longitude', track);

      } else {
        showDataError(prefs.getMsg('no_data_found'));
      }
    } else {
      showDataError(prefs.getMsg('no_data_found'));
    }
    // Search for the POI id according to latitudes and longitudes.
    if (north && east && west && south) {
      doLatLangLookup();
    } else {
      showDataError(prefs.getMsg('no_data_found'));
    }
  } else {
    showDataError(prefs.getMsg('not_authenticate'));
  }
}

/**
 * Function to get a particular node data.
 * @param {string} nodeName name of the direction.
 * @param {number} index Index of node.
 * @return {string} Value of node.
 */
function getNodeData(nodeName, index) {
  var node = xmlDoc.getElementsByTagName(nodeName)[index] || '';
  return node && node.firstChild && node.firstChild.nodeValue || '';
}

/**
 * Function to show error messages
 * @param {string} message The error message to be shown.
 */
function showDataError(message) {
  _gel('loading-container').innerHTML = '';
  _gel('server-msg').innerHTML = '';
  _gel('loading-container').style.display = 'block';
  _gel('loading-container').innerHTML = message;
}

/**
 * Function to fetch POI(Points of Intrest) data.
 */
function doLatLangLookup() {
  var bounds = gMap.getBounds();
  var southWest = bounds.getSouthWest();
  var northEast = bounds.getNorthEast();
  var url = 'http://apigateway.lonelyplanet.com/api/bounding_boxes/' +
            northEast.lat() + ',' + southWest.lat() + ',' +
            northEast.lng() + ',' + southWest.lng() +
            '/pois?poi_type=' + poiCategory;
  var params = {};
  params[gadgets.io.RequestParameters.AUTHORIZATION] =
      gadgets.io.AuthorizationType.NONE;
  params[gadgets.io.RequestParameters.REFRESH_INTERVAL] = 1;
  params[gadgets.io.RequestParameters.HEADERS] =
      {'Authorization' : AUTHORIZATION_KEY};
  params[gadgets.io.RequestParameters.CONTENT_TYPE] =
      gadgets.io.ContentType.DOM;
  gadgets.io.makeRequest(url, searchPOIId, params);
}

/**
 * Function to parse POI data and push data into search results array.
 * @param {Object} data The reponse data to be parsed.
 */
function searchPOIId(data) {
  gCurrentTripsData.arrSearchResults = [];
  gCurrentTripsData.searchMarkers = [];
  gCurrentTripsData.searchMarkerIcons = [];
  _gel('search-box').style.className = '';
  _gel('search-box').value = ''
  _gel('map-container').style.width = '100%';
  domParser(data);
  var items = xmlDoc.getElementsByTagName('poi');
  var obj = {};
  var poiItemsLength = items.length;
  var nodeData;
  for (var i = 0, j = poiItemsLength; i < j; i++) {
    obj = {};
    obj.id = getNodeData('id', i);
    obj.name = getNodeData('name', i);
    obj.lat = getNodeData('digital-latitude', i);
    obj.lng = getNodeData('digital-longitude', i);
    gCurrentTripsData.arrSearchResults[i] = obj;
  }
  addMarkersPOI();
  _gel('loading-container').innerHTML = '';
  if (!poiItemsLength) {
    _gel('loading-container').innerHTML =
        '<b>' + prefs.getMsg('no_results') +
        ' "' + LONELY_IMG[poiCategory].name + '"</b>';
  }
}

/**
 * Method to add custom markers on map.
 * @param {number} index The index of marker.
 * @param {Object} point The latitude and longitude of marker.
 * @param {string} imgUrl The index of marker.
 * @param {number} id The id of object.
 */
function addCustomMarker(index, point, imgUrl, id){
  var baseIcon = new GIcon(G_DEFAULT_ICON);
  baseIcon.shadow = SHADOW_IMG;
  baseIcon.iconSize = new GSize(20, 34);
  baseIcon.shadowSize = new GSize(37, 34);
  baseIcon.iconAnchor = new GPoint(9, 34);
  baseIcon.infoWindowAnchor = new GPoint(9, 2);
  var letteredIcon = new GIcon(baseIcon, imgUrl);
  letteredIcon.iconSize = new GSize(12, 22);
  letteredIcon.shadow = 'http://www.gstatic.com/ig/modules/trippy/pin_sml_shadow.cache.png';
  letteredIcon.shadowSize = new GSize(23, 22);
  letteredIcon.iconAnchor = new GPoint(9, 23);
  letteredIcon.infoWindowAnchor = new GPoint(9, 2);
  var marker = new GMarker(point, {icon: letteredIcon, autoPan: false});
  gMap.addOverlay(marker);

  marker.value = index;
  gCurrentTripsData.searchMarkers[index] = marker;
  gCurrentTripsData.searchMarkerIcons[index] = imgUrl;
  var markerName = gCurrentTripsData.arrSearchResults[index].name;
  // Adding data to global array.
  gCurrentTripsData.arrSearchResults[index] = {
    id: id,
    err: true,
    lat: point.lat(),
    lng: point.lng(),
    name: markerName
  };
  // For masking.
  if (index != -1) {
    GEvent.addListener(marker, 'mouseover', function(latlng) {
        currentMarker = {
          marker: marker,
          index: index,
          icon: imgUrl
        };
        moveMask(latlng);
        showToolTip(latlng, markerName);
    });
  }
}

/**
 * Add markers on the poi result items.
 */
function addMarkersPOI() {
  gMap.clearOverlays();
  // To locate the added trip items on map.
  for (var j = 0, count = gTripItemDB.length; j < count; j++) {
    addBlueMarker(gTripItemDB[j]);
  }
  var searchResultsLength = gCurrentTripsData.arrSearchResults.length;
  var addMarkerData, start, iconUrl;
  if (searchResultsLength) {
    for (var i = 0; i < searchResultsLength; i++) {
      addMarkerData = gCurrentTripsData.arrSearchResults[i];
      start = new GLatLng(addMarkerData.lat, addMarkerData.lng);
      iconUrl = LONELY_IMG[poiCategory].searchUrl;
      addCustomMarker(i, start, iconUrl, addMarkerData.id);
    }
  }
}

/**
 * Search the points of interests according to lonely planet api.
 * @param {string} id Search parameter.
 */
function doPoiByIdLookup(id) {
  var url = 'http://apigateway.lonelyplanet.com/api/pois/' + id;
  var params = {};
  params[gadgets.io.RequestParameters.AUTHORIZATION] =
      gadgets.io.AuthorizationType.NONE;
  params[gadgets.io.RequestParameters.REFRESH_INTERVAL] = 1;
  params[gadgets.io.RequestParameters.HEADERS] =
      {'Authorization' : AUTHORIZATION_KEY};
  params[gadgets.io.RequestParameters.CONTENT_TYPE] =
      gadgets.io.ContentType.DOM;
  gadgets.io.makeRequest(url, parseLonelyPlanetData, params);
}

/**
 * Callback method to parse lonely planet result xml and
 * put values in global array.
 * @param {Object} data The reponse data to be parsed.
 */
function parseLonelyPlanetData(data) {
  domParser(data);
  var items = xmlDoc.getElementsByTagName('poi');
  var obj = {};
  for (var i = 0, j = items.length; i < j; i++) {
    obj = {};
    obj.ownerId = getTripById(gCurrentTripsData.currentTripId).ownerId;
    obj.ownerName = gViewer;
    obj.name = getNodeData('name', i);
    var tempXmlDoc = xmlDoc;
    var result = tempXmlDoc.getElementsByTagName('address')[i];
    if (result && result.childNodes[0]) {
      xmlDoc = result;
      var street = getNodeData('street', 0);
      var locality = getNodeData('locality', 0);
      obj.address = street + ' ' + locality;
    }
    result = tempXmlDoc.getElementsByTagName('urls')[i];
    var url = '';
    if (result && result.childNodes[0]) {
      xmlDoc = result;
      obj.weburl = getNodeData('url', 0);
      obj.weburl = '<a style="cursor:pointer;color:green;" ' +
          'target=_blank href=http://' + obj.weburl + '>' +
          obj.weburl + '</a>';
      obj.link = obj.weburl;
    }
    xmlDoc = tempXmlDoc;
    obj.review = getNodeData('review', i);
    obj.lat = getNodeData('digital-latitude', i);
    obj.lng = getNodeData('digital-longitude', i);
    obj.dataSource = 'lonely';
    obj.category = poiCategory;
    result = xmlDoc.getElementsByTagName('representations')[i];
    if (result && result.childNodes[0]) {
      var lonelyLink =
          result.getElementsByTagName('representation')[2] || '';
      lonelyLink = lonelyLink.getAttribute('href');
      obj.lonelyLink = '<a style="cursor:pointer;color:#00c;" ' +
          'target=_blank href=' + lonelyLink + '>' +
          prefs.getMsg('more_link') + '&raquo;</a>';
    }
    gCurrentTripsData.arrSearchResults[currentMarker.index] = obj;
  }
}
// Export
window.searchPoi = searchPoi;
