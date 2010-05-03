/**
 * @fileoverview Code to handle local search and markers related functions.
 * 
 */

/**
 * Search for a particular query.
 */
function localSearch() {
  var mapLocation;
  var keyword = _gel('search-box').value;
  var searchUrl = 'http://www.google.com/local?q=%KEYWORD%&near=' +
      '%LOCATION%&start=1&num=20&output=kml';
  if (!keyword) {
    _gel('loading-container').innerHTML = '';
    return;
  }
  // Getting maps bounds for visible map area.
  var sspnUrl = gMap.getBounds().getCenter().toUrlValue();
  var params = {};
  params[gadgets.io.RequestParameters.CONTENT_TYPE] =
      gadgets.io.ContentType.DOM;

  searchUrl = searchUrl.replace(/%KEYWORD%/g, encodeURIComponent(keyword))
                       .replace(/%LOCATION%/g, sspnUrl);
  _gel('loading-container').innerHTML = _gel('tpl-loading').value;
  gadgets.io.makeRequest(searchUrl, parseLocalSearchResults, params);
}

/**
 * Parse the local search response.
 * @param {Object} feed Response object.
 */
function parseLocalSearchResults(feed) {
  try {
    _gel('loading-container').innerHTML = '';
    if (!feed || !feed.data) {
      _gel('loading-container').innerHTML = '<b>' +
      prefs.getMsg('no_results') + '"' + _gel('search-box').value +
      '"</b>';
      return;
    }
    var placemarks = feed.data.getElementsByTagName('Placemark');
    gMap.clearOverlays();
    var bounds = new GLatLngBounds();
    gCurrentTripsData.arrSearchResults = [];
    var length = Math.min(MAX_ITEM, placemarks.length);
    for (var i = 0; i < length; i++) {
      var placeMarkObj = parsePlaceMark(placemarks[i].childNodes);
      addMarker(i,
                new GLatLng(placeMarkObj.lat, placeMarkObj.lng),
                MARKER_IMG,
                placeMarkObj.name);
      bounds.extend(new GLatLng(placeMarkObj.lat, placeMarkObj.lng));
      placeMarkObj.dataSource = 'google';
      gCurrentTripsData.arrSearchResults.push(placeMarkObj);
    }
    // To locate the added trip items on map.
    for (var j = 0, count = gTripItemDB.length; j < count; j++) {
      addBlueMarker(gTripItemDB[j]);
    }
  } catch (err) {
    // Do nothing.
  }
}

/**
 * Parse individual placemark.
 * @param {Object} placemark Placemark object.
 * @return {Object} PlaceMark object.
 */
function parsePlaceMark(placemark) {
  var placeMarkObj = {};
  placeMarkObj.category = _gel('search-box').value;
  var placeMarksLength = placemark.length;
  var address;
  for (var j = 0; j < placeMarksLength; j++) {
    if (placemark[j].nodeType == 1) {
      switch (placemark[j].nodeName) {
        case 'name':
          placeMarkObj.name = placemark[j].childNodes[0].nodeValue;
          break;
        case 'Snippet':
          if (placemark[j].childNodes[0]) {
            address = placemark[j].childNodes[0].nodeValue;
            address = address.replace(/<br\/>/, ' ');
          } else {
            address = '';
          }
          break;
        case 'Point':
          var coordinates =
              placemark[j].childNodes[0].childNodes[0].nodeValue;
          // Extracting latitude and longitude.
          placeMarkObj.lat = coordinates.split(',')[1];
          placeMarkObj.lng = coordinates.split(',')[0];
        case 'StyleMap':
          var text = placemark[j].childNodes[0].
              getElementsByTagName('text')[0];
          if (text) {
            placeMarkObj = getStyleMap(text, placeMarkObj);
          }
          break;
      }
    }
  }
  placeMarkObj.address = address ? Util.stripHtml(address) : '';
  return placeMarkObj;
}

/**
 * Adds a marker to the map.
 * @param {number} index Index of the marker based on serach results.
 * @param {Object} point LatLng object indicates where the marker has to be
 *     added.
 * @param {string} imgUrl Url if the marker to be added.
 * @param {string} name Name to be shown in the tool tip of marker.
 */
function addMarker(index, point, imgUrl, name) {
  var baseIcon = new GIcon(G_DEFAULT_ICON);
  baseIcon.shadow = SHADOW_IMG;
  baseIcon.iconSize = new GSize(20, 34);
  baseIcon.shadowSize = new GSize(37, 34);
  baseIcon.iconAnchor = new GPoint(9, 34);
  baseIcon.infoWindowAnchor = new GPoint(9, 2);
  var bounds = gMap.getBounds();
  var letteredIcon = new GIcon(baseIcon, imgUrl);
  if (bounds.contains(point)) {
    var marker = new GMarker(point,
        {icon: letteredIcon, autoPan: false});
    gMap.addOverlay(marker);
    marker.value = index;
    if (index != -1) {
      GEvent.addListener(marker, 'mouseover', function(latlng) {
          currentMarker = {
            marker: marker,
            index: index,
            icon: imgUrl
          };
          moveMask(latlng);
          showToolTip(latlng, name);
      });
    }
  }
}

/**
 * Get attributes for a map placemark.
 * @param {Object} text DOM object.
 * @param {Object} placeMarkObj  The placemark object.
 * @return {Object} Returns the new placemark object.
 */
function getStyleMap(text, placeMarkObj) {
  var review = '';
  var index;
  if (text) {
    var textNode = text.childNodes[0].nodeValue;
    placeMarkObj.review = textNode ? Util.stripHtml(textNode) : '';
    // Extracting user reviews for a searched item.
    placeMarkObj.review = placeMarkObj.review.split('$')[0];
    if (textNode.length) {
      index = textNode.search(/<a href=/i);
      if (index != -1) {
        // Link for an item will start after 9th position in pattern.
        textNode = textNode.substr(index + 9);
        placeMarkObj.link =
            textNode.substr(0, textNode.indexOf('"'));
        index = textNode.search(/<tr/i);
        var row = '';
        if (index != -1) {
          // The desired string pattern will start after 3 positions
          // from string.
          textNode = textNode.substr(index + 3);
          index = textNode.search(/<\/tr>/);
          row = textNode.substr(0, index);
          index = row.search(/<a href=/i);
          if (index != -1) {
            row = row.substr(index);
            // The value for string weburl will start after 4 positions
            // from string.
            if (row.indexOf('style="color:#008000"') != -1) {
              placeMarkObj.weburl =
                  row.substr(0, row.indexOf('</a>') + 4);
              placeMarkObj.weburl = placeMarkObj.weburl.replace(
                  /<a href/i, '<a target="_blank" href');
            }
          }
        }
        index = textNode.search(/<img src="http:\/\/base.googl/i);
        if (index != -1) {
          // The value of url value start from 10th position.
          textNode = textNode.substr(index + 10);
          placeMarkObj.imgurl =
              textNode.substr(0, textNode.indexOf('"'));
        }
      }
    }
  }
  return placeMarkObj;
}

// Exports
window.localSearch = localSearch;
