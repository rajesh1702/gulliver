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
  var serverMsg = _gel('server-msg');
  serverMsg.innerHTML = '';
  serverMsg.style.display = 'none';
  _gel('loading-container').innerHTML = _gel('tpl-loading').value;
  poiCategory = target;
  doLatLangLookup();
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
    obj.dataSource = Datasource.LONELY;
    gCurrentTripsData.arrSearchResults[i] = obj;
  }
  addMarkersPOI();
  _gel('loading-container').innerHTML = '';
  if (!poiItemsLength) {
    localSearch(LONELY_IMG[poiCategory].name);
    var tplData = {
      message: prefs.getMsg('no_connection_search_google_data')
    };
    showServerMessage(tplData);
  } else {
    hightLightSelectedItem(0);
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
  letteredIcon.shadow =
      'http://www.gstatic.com/ig/modules/trippy/pin_sml_shadow.cache.png';
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
    name: markerName,
    dataSource: Datasource.LONELY
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
    addBlueMarker(gTripItemDB[j], j);
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
  var timeOutId = setTimeout(function() {
                    handleErrorInPOI(currentMarker.index);
                  }, 5000);
  gadgets.io.makeRequest(url,
      _IG_Callback(parseLonelyPlanetData, currentMarker.index, timeOutId),
      params);
}

/**
 * Callback method to parse lonely planet result xml and
 * put values in global array.
 * @param {Object} data The response data to be parsed.
 * @param {number} index Index of the item selected.
 * @param {number} timeOutId Id of the timer.
 */
function parseLonelyPlanetData(data, index, timeOutId) {
  try {
    // If response is received clear the timer.
    clearTimeout(timeOutId);
    domParser(data);

    var obj = gCurrentTripsData.arrSearchResults[index];
    var dataFetched = obj.dataFetched;
    obj.dataFetched = true;
    var items = xmlDoc.getElementsByTagName('poi');
    for (var i = 0, j = items.length; i < j; i++) {
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
        var link = getNodeData('url', 0);
        if (link) {
          obj.link = 'http://' + link;
        }
      }
      xmlDoc = tempXmlDoc;
      obj.review = getNodeData('review', i);
      obj.lat = getNodeData('digital-latitude', i);
      obj.lng = getNodeData('digital-longitude', i);
      obj.dataSource = Datasource.LONELY;
      obj.category = poiCategory;
      result = xmlDoc.getElementsByTagName('representations')[i];
      if (result && result.childNodes[0]) {
        // result.getElementsByTagName('representation') returns array of
        // 3 elements, in which 3rd element refers to the details of the item in
        // lonely planet site.
        // eg: http://touch.lonelyplanet.com/et-1000226350
        var lonelyLink =
            result.getElementsByTagName('representation')[2] || '';
        lonelyLink = lonelyLink.getAttribute('href');
        // Check whether Id of the item is defined or not.
        if (lonelyLink != 'http://touch.lonelyplanet.com/et-') {
          obj.lonelyLink = lonelyLink;
        }
      }
      var telePhone = xmlDoc.getElementsByTagName('telephone')[i];
      if (telePhone && telePhone.childNodes[0]) {
        var areaCode = getNodeData('area-code', 0);
        var number = getNodeData('number', 0);
        var separator = areaCode ? ' - ' : '';
        obj.address += '<br/>' + areaCode + separator + number;
      }
    }
    if (!dataFetched) {
      showMarkerInfo(index);
    }
  } catch (err) {
    handleErrorInPOI(index);
  }
}

/**
 * Handles any error while trying to fetch/parse POI item details.
 * @param {number} index Index of the item selected.
 */
function handleErrorInPOI(index) {
  var obj = gCurrentTripsData.arrSearchResults[index];
  var dataFetched = obj.dataFetched;
  obj.dataFetched = true;
  if (!dataFetched) {
    showMarkerInfo(index);
  }
}

// Export
window.searchPoi = searchPoi;
