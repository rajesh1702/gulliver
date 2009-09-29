
/** @const */
var MAX_ITEM = 20;
var markerIndex;
var currentMarker;
var currentMarkerIcon;

var baseIcon = new GIcon(G_DEFAULT_ICON);
baseIcon.shadow = "http://www.google.com/mapfiles/shadow50.png";
baseIcon.iconSize = new GSize(20, 34);
baseIcon.shadowSize = new GSize(37, 34);
baseIcon.iconAnchor = new GPoint(9, 34);
baseIcon.infoWindowAnchor = new GPoint(9, 2);

/** @const */
var CLOSE_IMG = '<img src="http://gulliver.googlecode.com/svn/trunk/frontend/' +
                'wave/images/close_promo.gif" border="0">';

/**
 * Set the query to be searched.
 * @param {string} value Keyword to be searched.
 */
function setSearchKeyword(value) {
  _gel('search-box').value = value;
  _gel('search-box').className = '';
  localSearch();
}

/**
 * Search for a particular query.
 */
function localSearch() {
  var SEARCH_URL_TPL = 'http://www.google.com/local?q=%KEYWORD%&near=%LOCATION%' +
    '&start=1&num=25&output=kml';
  var mapLocation;
  var keyword = _gel('search-box').value;
  var loadingContainer = _gel('loading-container');
  var searchUrl = SEARCH_URL_TPL;
  if (keyword == '') {
    loadingContainer.innerHTML = '';
    return;
  }

  var bounds = gMap.getBounds();
  var sspnUrl = bounds.getCenter().toUrlValue();

  var params = {};
  params[gadgets.io.RequestParameters.CONTENT_TYPE] =
      gadgets.io.ContentType.DOM;

  searchUrl = searchUrl.replace(/%KEYWORD%/g, encodeURIComponent(keyword))
      .replace(/%LOCATION%/g, sspnUrl);
  loadingContainer.innerHTML = _gel('tpl-loading').value;
  gadgets.io.makeRequest(searchUrl, parseLocalSearchResults, params);
}

/**
 * Parse the local serach response.
 * @param {Object} feed Response object.
 */
function parseLocalSearchResults(feed) {
  try {
    _gel('loading-container').innerHTML = '';
    if (!feed.data) {
      var loadingContainer = _gel('loading-container');
      loadingContainer.innerHTML = '<b>No results found for "'+ _gel('search-box').value +'"</b>';
      _gel('resultBox').innerHTML = '';
      _gel('navBox').innerHTML = '';
      return;
    }

    var icon, title;
    var placemarks = feed.data.getElementsByTagName('Placemark');
    gMap.clearOverlays();
    var bounds = new GLatLngBounds();
    gData.arrSearchResults = [];

    var length = MAX_ITEM < placemarks.length ? MAX_ITEM : placemarks.length;
    trace('length ' + length);
    for (var i = 0; i < length; i++) {
      var placeMarkObj = parsePlaceMark(placemarks[i].childNodes);
      trace('review ' + i + ' : ' + placeMarkObj.review);
      var iconUrl = 'http://www.google.com/mapfiles/marker.png';
      addMarker(i, new GLatLng(placeMarkObj.lat, placeMarkObj.lng), iconUrl,
          placeMarkObj.name);
      bounds.extend(new GLatLng(placeMarkObj.lat, placeMarkObj.lng));
      placeMarkObj.dataSource = 'google';
      gData.arrSearchResults.push(placeMarkObj);
    }
    // To locate the added trip items on map.
    fillAllItems();
  } catch (err) {
    _gel('resultText').innerHTML = err + ' : ' + err.description;
    trace(err + ' : ' + err.description);
  }
}

/**
 * Parse individual placemark.
 * @param {Object} Individual placemark object.
 */
function parsePlaceMark(placemark) {
  var index;
  var placeMarkObj = {};
  placeMarkObj.category = _gel('search-box').value;;
  for (var j = 0; j < placemark.length; j++) {
    if (placemark[j].nodeType == 1) {
      switch(placemark[j].nodeName) {
        case 'name':
          placeMarkObj.name = placemark[j].childNodes[0].nodeValue;
          break;
        case 'Snippet':
          if (placemark[j].childNodes[0]) {
            placeMarkObj.address = placemark[j].childNodes[0].nodeValue;
          } else if (placemark[j].childNodes[1]) {
            placeMarkObj.address = placemark[j].childNodes[1].nodeValue;
          }
          placeMarkObj.address = placeMarkObj.address.replace(/<br\/>/,
                                                               ' ');
          break;
        case 'Point':
          var coordinates =
              placemark[j].childNodes[0].childNodes[0].nodeValue;
          placeMarkObj.lat = coordinates.split(',')[1];
          placeMarkObj.lng = coordinates.split(',')[0];
        case 'StyleMap':
          var stylemap = placemark[j].childNodes[0];
          var text = stylemap.getElementsByTagName('text')[0];
          var review ='';
          if (text != null) {
            var strValue = text.childNodes[0].nodeValue;
            var reviewSplit = '';
            //var reviewIndex = strValue.search(/<br\/><b/i);
            //placeMarkObj.review = strValue.substr(reviewIndex + 5);
            placeMarkObj.review = stripHtml(strValue);
            reviewSplit = placeMarkObj.review.split('$');
            placeMarkObj.review = reviewSplit[0];
            if (strValue.length > 0) {
              index = strValue.search(/<a href=/i);
              if (index != -1) {
                strValue = strValue.substr(index + 9);
                placeMarkObj.link =
                    strValue.substr(0, strValue.indexOf('"'));
                index = strValue.search(/<tr/i);
                var row = '';
                if (index != -1) {
                  strValue = strValue.substr(index + 3);
                  index = strValue.search(/<\/tr>/);
                  row = strValue.substr(0, index);
                  index = row.search(/<a href=/i);
                  if (index != -1) {
                    row = row.substr(index);
                    if (row.indexOf('style="color:#008000"') != -1) {
                      placeMarkObj.weburl = row.substr(0,
                                             row.indexOf('</a>') + 4);
                      placeMarkObj.weburl = placeMarkObj.weburl.replace(
                                             /<a href/i,
                                             '<a target="_blank" href');
                    }
                  }
                }
                index = strValue.search(/<img src="http:\/\/base.googl/i);
                if (index != -1) {
                  strValue = strValue.substr(index + 10);
                  var img = strValue.substr(0, strValue.indexOf('"'));
                  placeMarkObj.imgurl = img;
                }
              }
            }
          } else {
            placeMarkObj.rating = 0;
          }
          break;

      }
    }
  }
  placeMarkObj.review = stripHtml(placeMarkObj.review);
  placeMarkObj.address = stripHtml(placeMarkObj.address);
  return placeMarkObj;
}

/**
 * This methods takes HTML as input and removes all data inside < and > tags.
 * @param {htmlString} htmlString as string.
 * @return {string} htmlString with no html tags.
 */
function stripHtml(htmlString) {
  return htmlString.replace(/<\/?[^>]+(>|$)/g, '');
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
  var bounds = gMap.getBounds();
  baseIcon.iconSize = new GSize(20, 34);
  baseIcon.shadowSize = new GSize(37, 34);
  var letteredIcon = new GIcon(baseIcon, imgUrl);
  var markerOptions = {icon: letteredIcon};
  if (bounds.contains(point) == true) {
    var marker = new GMarker(point, {icon: letteredIcon, autoPan: false});
    gMap.addOverlay(marker);
    marker.value = index;
    GEvent.addListener(marker, 'mouseover', function(latlng) {
      if (index != -1) {
        markerIndex = index;
        currentMarker = marker;
        currentMarkerIcon = imgUrl;
        moveMask(latlng);
        showToolTip(latlng, name);
      }
    });
  }

}

/**
 * Shows tool tip on marker.
 * @param {Object} point LatLng object indicates where the marker has to be
 *     added.
 * @param {string} name Url if the marker to be added.
 */
function showToolTip(point, name) {
  var divPoint = gMap.fromLatLngToContainerPixel(point);
  var mapElementPosition = getPosition(_gel('map-container'));
  var ele = _gel('toolTip');
  var eleStyle = ele.style;
  eleStyle.display = 'block';
  eleStyle.zIndex = 10000;
  eleStyle.left = divPoint.x + mapElementPosition.x + 'px';
  eleStyle.top = divPoint.y + mapElementPosition.y - 40 + 'px';
  ele.innerHTML = name;
}

/**
 * Adds a mask over the marker selected which can be dragged.
 * @param {Object} point LatLng object indicates where the marker has to be
 *     added.
 */
function moveMask(point) {
  var divPoint = gMap.fromLatLngToContainerPixel(point);
  var mapElementPosition = getPosition(_gel('map-container'));
  var ele = _gel('mask');
  var eleStyle = ele.style;
  eleStyle.display = 'block';
  eleStyle.zIndex = 10000;
  eleStyle.left = divPoint.x + mapElementPosition.x - 10 + 'px';
  eleStyle.top = divPoint.y + mapElementPosition.y - 34 + 'px';
}

/**
 * Displays the info when clicked on the marker.
 */
function showMarkerInfo() {
  if (!currentMarker || markerIndex == -1) {
    return;
  }
  _gel('msgContainer').style.display = 'none';
trace('B4 getPopupHtml showMarkerInfo');
  var html = getPopupHtml(markerIndex, true, false);

trace('After getPopupHtml' + currentMarker);
  if (gData.arrSearchResults[markerIndex].dataSource = 'lonely') {
    currentMarker.openInfoWindowHtml(LOADING_TPL);
    html = html.replace(CLOSE_IMG, '')
        .replace(currentMarkerIcon, '')
        .replace('<img src=""/>', '');
    setTimeout(gMap.closeInfoWindow, 1000);
  }
  currentMarker.openInfoWindowHtml(html);
}

/**
 * Gives the x and y co-ordinates of mouse.
 * @param {object} element The element.
 * @return {object} The position of element.
 */
function getPosition(element) {
  var left = 0;
  var top = 0;
  while (element.offsetParent) {
    left += element.offsetLeft;
    top += element.offsetTop;
    element = element.offsetParent;
  }
  left += element.offsetLeft;
  top += element.offsetTop;
  return {x:left, y:top};
}

/**
 * Checks whether value is empty.
 * @param {string} value Value to be checked.
 * @return {boolean}.
 */
function isEmpty(value) {
  return (!value || value == null || value == undefined ||
          value == 'None' || value == 'unspecified');
}

/**
 * Trim spaces from both side of string.
 */
String.prototype.strip = function() {
  return this.replace(/^\s+/, '').replace(/\s+$/, '');
}

/**
 * It returns the html string for popup of an item.
 * @param {number} index Index of item.
 */
function getPopupHtml(index, flag) {
  var icon = String.fromCharCode('A'.charCodeAt(0) + index);
  strSelectedItemId = 'resultPopup' + index;
  var tplHtml =  _gel(flag ? 'tpl-item-info-drag' : 'tpl-item-info').value;
  trace('getPopupHtml');
  var tplData = {
    index: index,
    overviewUrl: gData.arrSearchResults[index].link + '&dtab=0&oi=',
    detailUrl: gData.arrSearchResults[index].link + '&dtab=1&oi=md_structdata',
    reviews: gData.arrSearchResults[index].review,
    reviewsLabel: 'Reviews',
    height: '84px',
    closeImage: '',
    dragMessage: 'Add me to your itinerary',
    dragImage: '<img border="0" src="http://gulliver.googlecode.com/svn/' +
               'trunk/frontend/wave/images/curve_arrow.png"/>',
    photosUrl: gData.arrSearchResults[index].link + '&dtab=3&oi=md_photos',
    image: '',
    display: 'none',
    title: _unesc(gData.arrSearchResults[index].name),
    marker: icon,
    webUrl: gData.arrSearchResults[index].weburl || '',
  };
  trace('getPopupHtml..');
  // Reviews url
  var review = '';
  review = gData.arrSearchResults[index].review || '';
  var pattern = '&#160;&#160;';
  // Check for empty review.
  if (!review || pattern == review) {
    tplData.reviews = '';
    tplData.reviewsLabel = '';
    tplData.height = '0';
  }
  trace('getPopupHtml...');
  var image = gData.arrSearchResults[index].imgurl;
  if (!isEmpty(image)) {
    tplData.image = '<img align="left" width="60" height="60" ' +
        'style="margin-right:10px;cursor:pointer;" src="' + image + '" />';
    tplData.display = 'block';
  }
  tplData.contactInfo = gData.arrSearchResults[index].address ||
      '<a href="#" onclick="showMarkerInfoByIndex(' + index + ')">See Reviews...</a>';
  return tplHtml.supplant(tplData);
}


function showMarkerInfoByIndex(index) {
  currentMarker = gData.searchMarkers[index];
  markerIndex = index;
  currentMarkerIcon = gData.searchMarkerIcons[index];
  trace('Leaving showMarkerInfoByIndex for' + index);
  showMarkerInfo();
}
/**
 * Closes the popup window opened on click of trip name.
 */
function closeInfoWindow() {
  _IG_AdjustIFrameHeight(_gel('main-container').offsetHeight);

  if (strSelectedItemId == '')
    return;
  if (_gel(strSelectedItemId) != null &&
      (strSelectedItemId.indexOf('itemPopup') != -1 ||
       strSelectedItemId.indexOf('resultPopup') != -1)) {
    _gel(strSelectedItemId).innerHTML = '';
    _gel(strSelectedItemId).style.display = 'none';
  }
  strSelectedItemId = '';
}

/**
 * Dialog for edit dates.
 */
function showLocationDialog() {
  var tplHtml = _gel('tpl-location-dialog').value;
  var tripData = getTripById(gData.currentTripId);
  tplHtml = tplHtml.supplant({location: tripData.loc});
  showDialog(tplHtml);
  var element = _gel('txtLocation');
  element.focus();
  element.select();
}

/**
 * This function will be used to change the trip location from canvas view.
 */
function changeTripLocation() {
  var idWarningMsg = _gel('wrngmsg');
  var strLocation = _gel('txtLocation').value;
  var tripData = getTripById(gData.currentTripId);
  if (strLocation.strip() != '') {
    var validLocation = isNumeric(strLocation);
    if (validLocation) {
      idWarningMsg.innerHTML = 'Enter Valid Location';
      idWarningMsg.style.visibility = 'visible';
    } else {
      if (strLocation != tripData.loc) {
        showAddress(strLocation);
      } else {
        hideDialog();
      }
    }
  } else {
    idWarningMsg.innerHTML = '__MSG_enter_loc__';
    idWarningMsg.style.visibility = 'visible';
  }
}

/**
 * Checks whether the location is valid and if valid,
 *     update the location.
 * @param {string} address Location entered by user.
 */
function showAddress(address) {
  var newLat, newLang;
  if (geocoder) {
    geocoder.getLocations(address, function(result) {
      if (result.Status.code == 200) {
        if (result.Placemark.length > 0) {
          var place = result.Placemark[0];
          var accuracy = place.AddressDetails.Accuracy;
          var p = result.Placemark[0].Point.coordinates;
          newLat = p[1];
          newLang = p[0];
          updateLocation(address, newLat, newLang, accuracy);
          hideDialog();
       }
      } else {
        if (nCreateTrip == 0) {
          _gel('wrngmsg').innerHTML = prefs.getMsg('loc_not_found');
          _gel('wrngmsg').style.visibility = 'visible';
        } else {
          var element = _gel('create-location');
          element.value = '';
          element.focus();
          var serverMsg = _gel('server_msg');
          serverMsg.style.display = 'block';
          serverMsg.innerHTML = prefs.getMsg('invalid_loc');
          _IG_AdjustIFrameHeight();
          return;
        }
      }
    });
  }
}

/**
 * Checks whether the location is valid and if valid,
 *     update the location.
 * @param {string} address Location entered by user.
 * @param {number} newLat Latitude of new location.
 * @param {number} newLang Longitude of new location.
 * @param {number} accuracy Accuracy of new location.
 */
function updateLocation(address, newLat, newLang, accuracy) {
  var trip = getTripById(gData.currentTripId);
  trace('location' + address)
  var oldLoc = trip.loc;
  trip.loc = address;
  trip.lat = newLat;
  trip.lng = newLang;
  trip.accuracy = accuracy;
  var start = new GLatLng(trip.lat, trip.lng);
  gMap.setCenter(start, tabAccuracy[trip.accuracy]);
  var state = wave.getState();
  trip.lastModified = (new Date()).getTime();
  var obj = {};
  var tripItems = getTrips().items || [];
  obj['trips'] = gadgets.json.stringify({
    'items': tripItems,
    'lastModified': (new Date()).getTime()
  });
  obj['logs'] = getLogObject('updated', ' the trip location of "' + trip.name + '" from "' +  oldLoc + '" to "' + address + '"');
  obj[gData.currentTripId] = gadgets.json.stringify(trip);
  state.submitDelta(obj);
}

/**
 * Check if passed character is numeric.
 * @param {string} char The string to be validated.
 * @return {boolean} True if numeric else false.
 */
function isNumeric(strValue) {
  var strInvalidChars = '0123456789';
  var strChar;
  for (var i = 0; i < strValue.length; i++) {
    strChar = strValue.charAt(i);
    if (strInvalidChars.indexOf(strChar) != -1)
      return true;
  }
  return false;
}


/**
 * Creates the dialog box to delete the item.
 */
function createDeleteItemBox(itemId) {
  itemId = itemId || '';
  var name = getItemById(itemId || gData.currentTripId).name;
  if (name.length > 15) {
    name = name.substr(0, 15) + '...';
  }
  var tplHtml = _gel('tpl-delete-item-dialog').value;
  var tplData = {
    itemName: name,
    itemId: itemId
  };
  tplHtml = tplHtml.supplant(tplData);
  showDialog(tplHtml);
  _gel('cancelButton').focus();
}

/**
 * Delete the item at specified index.
 */
function deleteSelectedItem(itemId) {
  var state = wave.getState();
  hideDialog();
  var obj = {};
  var items, trip;
  if (itemId) {// Item need to be deleted.
    trace('I want to delete item ' + itemId);
    trip = getTripById(gData.currentTripId);
    trace('B4 delete: '  + trip.items);
    trip.items.deleteElement(itemId);
    trace('after delete: '  + trip.items);
    obj[trip.id] = gadgets.json.stringify(trip);// To remove the item from the list.
    obj[itemId] = null;// To remove the item.
    obj['logs'] = getLogObject('deleted', ' a trip item "' + getItemById(itemId).name + '" of Trip "' + trip.name + '"');
  } else {// Trip needs to be deleted.
    trace('I want to delete trip ' + itemId);
    var trips = getTrips();
    trace(' curernt'  + gData.currentTripId);
    trace('B4 delete: '  + trips.items);
    trips.items.deleteElement(gData.currentTripId);
    trace('after delete: '  + trips.items);
    obj.trips = gadgets.json.stringify(trips);// To remove from list of trips.
    obj[gData.currentTripId] = null;//To remove the trip.
    trip = getTripById(gData.currentTripId);
    var keys = trip.items || [], item;
    for(var i =0; i < keys.length; i++) {
      item = getItemById(keys[i]);
      obj[item.id] = null;// To remove all items under the trip.
    }
    obj['logs'] = getLogObject('deleted', ' a trip "' + trip.name + '"');
  }
  trace('My Delta is ' + gadgets.json.stringify(obj));
  state.submitDelta(obj);
}

/**
 * Deletes specified element from the array.
 * @param {string} element String to be deleted.
 */
Array.prototype.deleteElement = function(element) {
  for (var i = 0; i < this.length; i++) {
    if (this[i].toString().toLowerCase() === element.toLowerCase()) {
      this.splice(i, 1);
      return;
    }
  }
};

/**
 * Dialog for edit.
 * @param {number} flag flag for dialogs.
 */
function showEditDialog() {
  var tplHtml = _gel('tpl-edit-trip-name-dialog').value;
  var tripData = getTripById(gData.currentTripId);
  var tplData = {tripName: tripData.name};
  tplHtml = tplHtml.supplant(tplData);
  showDialog(tplHtml);
  var element = _gel('tripNameEdit');
  element.focus();
  element.select();
}

/**
 * Updates the trip name.
 */
function updateTripName() {
  var name = _gel('tripNameEdit').value.strip();
  if (name == '') {
    return;
  }

  var trip = getTripById(gData.currentTripId);
  var oldName = trip.name;
  trip.name = name;
  var start = new GLatLng(trip.lat, trip.lng);
  var state = wave.getState();
  trip.lastModified = (new Date()).getTime();
  var obj = {};
  var tripItems = getTrips().items || [];
  obj['trips'] = gadgets.json.stringify({
    'items': tripItems,
    'lastModified': (new Date()).getTime()
  });
  obj['logs'] = getLogObject('changed', ' the name of the "' + oldName + '" to  "' + trip.name + '"');
  obj[gData.currentTripId] = gadgets.json.stringify(trip);
  state.submitDelta(obj);
  hideDialog();
}
