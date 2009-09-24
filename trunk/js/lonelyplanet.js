/**
 * @fileoverview Code to handle lonely planet data.
 * @author gadgetfactory@google.com (gadgetfactory)
 */

/**
 * Search the points of interests according to lonely planet api.
 * @param {string} target Search parameter.
 */
function searchPOI(target) {
 _gel('loading-container').innerHTML = LOADING_TPL;
  itemsContainer = [];
  _gel('search-box').value = target;
  _gel('search-box').className = '';
  var trip = JGulliverData.getCurrentTrip();
  var mapLocation = trip.loc;
  var url = 'http://apigateway.lonelyplanet.com/api/places?name=' +
            _esc(mapLocation);
  var params = {};
  params[gadgets.io.RequestParameters.AUTHORIZATION] =
      gadgets.io.AuthorizationType.OAUTH;
  params[gadgets.io.RequestParameters.OAUTH_SERVICE_NAME] = 'lp-api-gateway';
  params[gadgets.io.RequestParameters.OAUTH_USE_TOKEN] = 'never';
  params[gadgets.io.RequestParameters.REFRESH_INTERVAL] = 1;
  params[gadgets.io.RequestParameters.CONTENT_TYPE] =
      gadgets.io.ContentType.DOM;
  gadgets.io.makeRequest(url, callbackSearchPOI, params);
  timerLonelyCallback = window.setTimeout(showLPAuthorizationError, 15000);
}
/**
 * To show LP aughorization error.
 */
function showLPAuthorizationError() {
  var message = 'Authentication Error. ' +
                '<a href="javascript:window.location.reload();">Try Again</a>';
  _gel('loading-container').innerHTML = '';
  _gel('serverMsg').innerHTML = '';
  _gel('msgContainer').style.display = 'block';
  var html = ACTION_MSG_TPL;
  html = html.replace(/%MESSAGE%/, message);
  timerMsg.createTimerMessage(html, 20);
}

/**
 * Callback for searching the latitude and longitude.
 * @param {Object} data Response object.
 */
function callbackSearchPOI(data) {
  clearTimeout(timerLonelyCallback);
  var north, south, east, west;
  var trip = JGulliverData.getCurrentTrip();
  var mapLocation = trip.loc;
  if (data.text) {
    var track = false;
    var xmlDoc = domParser(data);
    if (xmlDoc) {
      var nodeData = '';
      var items = xmlDoc.getElementsByTagName('place');
      if (items && items.length) {
        for (var i = 0, j = items.length; i < j; i++) {
          var shortName = xmlDoc.getElementsByTagName('short-name')[i];
          if (shortName) {
            shortName = shortName.childNodes[0].nodeValue.toLowerCase();
            if (shortName == mapLocation.toLowerCase()) {
              // Used to check whether the results matches the map location
              // otherwise it will pick the first result.
              track = true;
              var nodeData = xmlDoc.getElementsByTagName('north-latitude')[i];
              if (nodeData && nodeData.childNodes[0]) {
                north = nodeData.childNodes[0].nodeValue;
              }
              nodeData = xmlDoc.getElementsByTagName('south-latitude')[i];
              if (nodeData && nodeData.childNodes[0]) {
                south = nodeData.childNodes[0].nodeValue;
              }
              nodeData = xmlDoc.getElementsByTagName('east-longitude')[i];
              if (nodeData && nodeData.childNodes[0]) {
                east = nodeData.childNodes[0].nodeValue;
              }
              nodeData = xmlDoc.getElementsByTagName('west-longitude')[i];
              if (nodeData && nodeData.childNodes[0]) {
                west = nodeData.childNodes[0].nodeValue;
              }
            }
            break;
          }
        }
        if (!track) {
          var nodeData = xmlDoc.getElementsByTagName('north-latitude')[0];
          if (nodeData && nodeData.childNodes[0]) {
            north = nodeData.childNodes[0].nodeValue;
          }
          nodeData = xmlDoc.getElementsByTagName('south-latitude')[0];
          if (nodeData && nodeData.childNodes[0]) {
            south = nodeData.childNodes[0].nodeValue;
          }
          nodeData = xmlDoc.getElementsByTagName('east-longitude')[0];
          if (nodeData && nodeData.childNodes[0]) {
            east = nodeData.childNodes[0].nodeValue;
          }
          nodeData = xmlDoc.getElementsByTagName('west-longitude')[0];
          if (nodeData && nodeData.childNodes[0]) {
            west = nodeData.childNodes[0].nodeValue;
          }
        }
      }
    }
    // Search for the POI id according to latitudes and longitudes.
    if (north && east && west && south) {
      doLatLangLookup(north, east, west, south);
    } else {
      showDataError(prefs.getMsg('no_data_found'));
    }
  } else {
    showDataError(prefs.getMsg('not_authenticate'));
  }
}

/**
 * To show data error message.
 * @param {string} message Message content.
 */
function showDataError(message) {
  _gel('loading-container').innerHTML = '';
  _gel('serverMsg').innerHTML = '';
  _gel('msgContainer').style.display = 'block';
  var html = ACTION_MSG_TPL;
  html = html.replace(/%MESSAGE%/, message);
  timerMsg.createTimerMessage(html, 5);
}

/**
 * Searches the POI id by sending the latitude and langitude values.
 * @param {string} north North coordinates.
 * @param {string} east East coordinates.
 * @param {string} west West coordinates.
 * @param {string} south South coordinates.
 */
function doLatLangLookup(north, east, west, south) {
  var poiType = _gel('search-box').value;
  var bounds = gMap.getBounds();
  var southWest = bounds.getSouthWest();
  var northEast = bounds.getNorthEast();
  var south1 = southWest.lat();
  var west1 = southWest.lng();
  var north1 = northEast.lat();
  var east1 = northEast.lng();
  var url = 'http://apigateway.lonelyplanet.com/api/bounding_boxes/' +
            north1 + ',' + south1 + ',' + east1 + ',' + west1 +
            '/pois?poi_type=' + poiType;
  var params = {};
  params[gadgets.io.RequestParameters.AUTHORIZATION] =
      gadgets.io.AuthorizationType.OAUTH;
  params[gadgets.io.RequestParameters.OAUTH_SERVICE_NAME] = 'lp-api-gateway';
  params[gadgets.io.RequestParameters.OAUTH_USE_TOKEN] = 'never';
  params[gadgets.io.RequestParameters.REFRESH_INTERVAL] = 1;
  gadgets.io.makeRequest(url, searchPOIId, params);
}

/**
 * To search POI id according to data.
 * @param {Object} data Response object.
 */
function searchPOIId(data) {
  _gel('search-box').style.className = '';
  _gel('map-container').style.width = '100%';
  _gel('result-section').style.display = 'none';
  _gel('resultBox').style.display = 'none';
  _gel('navBox').style.display = 'none';
  var totalItems;
  var xmlDoc = domParser(data);
  var items = xmlDoc.getElementsByTagName('poi');
  var MAX_ITEMS = 7;
  var poiData = [];
  var obj = {};
  for (var i = 0, j = items.length; i < j; i++) {
    obj = {};
    var nodeData = xmlDoc.getElementsByTagName('id')[i];
    if (nodeData && nodeData.childNodes[0]) {
      obj.id = nodeData.childNodes[0].nodeValue;
    }
    nodeData = xmlDoc.getElementsByTagName('name')[i];
    if (nodeData && nodeData.childNodes[0]) {
      obj.name = nodeData.childNodes[0].nodeValue;
    }
    nodeData = xmlDoc.getElementsByTagName('digital-latitude')[i];
    if (nodeData && nodeData.childNodes[0]) {
      obj.lat = nodeData.childNodes[0].nodeValue;
    }

    nodeData = xmlDoc.getElementsByTagName('digital-longitude')[i];
    if (nodeData && nodeData.childNodes[0]) {
      obj.lng = nodeData.childNodes[0].nodeValue;
    }
    poiData.push(obj);
    arrSearchResults[i] = obj;
  }
  addMarkersPOI();
  _gel('loading-container').innerHTML = '';
}

/**
 * Add markers on the poi result items.
 */
function addMarkersPOI() {
  var objTrip = JGulliverData.getCurrentTrip();
  gMap.clearOverlays();
  if (_gel('resultBox')) {
    _gel('resultBox').innerHTML = '';
    itemsContainer = [];
  }
  //var bounds = new GLatLngBounds();
  if (arrSearchResults.length) {
    var counter = 0;
    var max_result = arrSearchResults.length;
     for (var i = 0, j = max_result; i < j; i++, counter++) {
      var start = new GLatLng(arrSearchResults[i].lat, arrSearchResults[i].lng);
      var iconUrl = 'http://www.google.com/mapfiles/marker.png';
      addCustomMarker(i, start, iconUrl, arrSearchResults[i].id);
    }
  }
}

/**
 * Search the points of interests according to lonely planet api.
 * @param {string} id Search parameter.
 */
function doPoiByIdLookup(id) {
  itemsContainer = [];
  var poiId = id;
  var url = 'http://apigateway.lonelyplanet.com/api/pois/' + poiId;
  var params = {};
  params[gadgets.io.RequestParameters.AUTHORIZATION] =
      gadgets.io.AuthorizationType.OAUTH;
  params[gadgets.io.RequestParameters.OAUTH_SERVICE_NAME] = 'lp-api-gateway';
  params[gadgets.io.RequestParameters.OAUTH_USE_TOKEN] = 'never';
  params[gadgets.io.RequestParameters.REFRESH_INTERVAL] = 1;
  gadgets.io.makeRequest(url, displayPingResult, params);
}

/**
 * To display ping results.
 * @param {Object} data Response object.
 */
function displayPingResult(data) {
  var xmlDoc = domParser(data);
  var items = xmlDoc.getElementsByTagName('poi');
  var obj = {};
  for (var i = 0, j = items.length; i < j; i++) {
    obj = {};
    obj.item_owner = gViewer;
    var result = xmlDoc.getElementsByTagName('name')[i];
    if (result && result.childNodes[0]) {
      obj.name = result.childNodes[0].nodeValue;
    }
    result = xmlDoc.getElementsByTagName('address')[i];
    if (result && result.childNodes[0]) {
      var street = result.getElementsByTagName('street')[0] || '';
      street = street && street.firstChild && street.firstChild.nodeValue || '';
      var locality = result.getElementsByTagName('locality')[0] || '';
      locality = locality &&
                 locality.firstChild && locality.firstChild.nodeValue || '';
      totAddress = street + ' ' + locality;
      obj.address = totAddress;
    }
    //todo
    result = xmlDoc.getElementsByTagName('urls')[i];
    if (result && result.childNodes[0]) {
      var url = result.getElementsByTagName('url')[0] || '';
      obj.weburl = url && url.firstChild && url.firstChild.nodeValue || '';
      obj.weburl = '<a style="cursor:pointer;color:green;" ' +
          'target=_blank href=http://' + obj.weburl + '>' + obj.weburl + '</a>';
      obj.link = obj.weburl;
    }
    result = xmlDoc.getElementsByTagName('review')[i];
    if (result && result.childNodes[0]) {
      obj.review = result.childNodes[0].nodeValue;
    }
    result = xmlDoc.getElementsByTagName('digital-latitude')[i];
    if (result && result.childNodes[0]) {
      obj.lat = result.childNodes[0].nodeValue;
    }

    result = xmlDoc.getElementsByTagName('digital-longitude')[i];
    if (result && result.childNodes[0]) {
      obj.lng = result.childNodes[0].nodeValue;
    }
    obj.data_source = 'lonely';
    arrSearchResults[markerIndex] = obj;
  }
}
