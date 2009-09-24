/**
 * @fileoverview Code handles map related functionality.
 * @author gadgetfactory@google.com (gadgetfactory)
 */

function addCustomMarker(index, point, imgUrl, id){
  var letteredIcon = new GIcon(baseIcon, imgUrl);
  var markerOptions = {icon: letteredIcon};
  var marker = new GMarker(point, {icon: letteredIcon, autoPan: false});
  gMap.addOverlay(marker);

  marker.value = index;

  //Adding data to global array
  arrSearchResults[index] = { id: id,
      err : true,
      lat: point.lat(),
      lng: point.lng(),
      name : arrSearchResults[index].name};
  GEvent.addListener(marker, 'mouseover', function(latlng) {
    // For masking
    if (index != -1) {
      markerIndex = index;
      var name = arrSearchResults[index].name;
      currentMarker = marker;
      currentMarkerIcon = imgUrl;
      moveMask(latlng);
      showToolTip(latlng, name);
      var html = [];
    }
  });
}

function removeIternaryMarkers() {
  for (var i = iternaryMarkers.length; i; i--) {
    var marker = iternaryMarkers[i - 1];
    gMap.removeOverlay(marker);
  }
  iternaryMarkers = [];
}

function addBlueMarker(index, objItem, baseIcon) {
  var point = new GLatLng(objItem.lat, objItem.lng);
  var marker = new GMarker(point, {icon: baseIcon, autoPan: false});
  iternaryMarkers.push(marker);
  gMap.addOverlay(marker);
  GEvent.addListener(marker, 'mouseover', function(latlng) {
    if (index != -1) {
      showToolTip(latlng, objItem.name);
    }
  });

  GEvent.addListener(marker, 'click', function(latlng) {
    // For masking
    if (index == -1) {
      return;
    } else {
      markerIndex = index;
      currentMarker = marker;
      _gel('msgContainer').style.display = 'none';
      var html = getPopupHtml(markerIndex, true, true);
      html = html.replace(/%CLOSE_IMG%/, '')
          .replace(currentMarkerIcon, '')
          .replace('<img src=""/>', '');
      iternaryMarkers[index].openInfoWindowHtml(html);
    }
  });
}

function showTripItemsOnmap() {
  removeIternaryMarkers();
  var objTrip = JGulliverData.getCurrentTrip();
  var baseIcon = new GIcon(G_DEFAULT_ICON);
  baseIcon.image = FILE_SERVER + '/images/blue-pushpin.png';
  // baseIcon.iconSize = new GSize(32, 32);
  for (var i = 0; i < objTrip.arrItem.length; i++) {
    addBlueMarker(i, objTrip.arrItem[i], baseIcon);
  }
}

function addMarker(index, point, imgUrl, name, description) {
  var baseIcon = new GIcon(G_DEFAULT_ICON);
  var bounds = gMap.getBounds();
  baseIcon.iconSize = new GSize(20, 34);
  baseIcon.shadowSize = new GSize(37, 34);
  var letteredIcon = new GIcon(baseIcon, imgUrl);
  var markerOptions = {icon: letteredIcon};
  if (bounds.contains(point) == true) {
    var marker = new GMarker(point, {icon: letteredIcon, autoPan: false});
    gMap.addOverlay(marker);
  }

  marker.value = index;
  GEvent.addListener(marker, 'mouseover', function(latlng) {
    if (index != -1) {
      markerIndex = index;
      currentMarker = marker;
      currentMarkerIcon = imgUrl;
      moveMask(latlng);
      showToolTip(latlng,name);
    }
  });
}

/**
 * Highlight marker on map.
 * @param {object} obj Current image element.
 * @param {string} i Position for result item in arrSearchResults.
 */
function addHighLightmarker(obj, i) {
  var point = new GLatLng(arrSearchResults[i].lat, arrSearchResults[i].lng);
  var letter = String.fromCharCode('A'.charCodeAt(0) + i);
  var icon = new GIcon(G_DEFAULT_ICON);
  icon.image = 'http://www.google.com/mapfiles/marker_green' + letter + '.png';
  obj.src = icon.image;
  highLightMarker = new GMarker(point, {icon: icon,
                                zIndexProcess: function() {
                                  return 100;}});
  gMap.addOverlay(highLightMarker);
}

/**
 * Highlight marker on map.
 * @param {object} obj Current image element.
 * @param {string} index Index of the icon which is to be used now.
 */
function removeHighLightMarker(obj, index) {
  gMap.removeOverlay(highLightMarker);
  var letter = String.fromCharCode('A'.charCodeAt(0) + index);
  obj.src = 'http://www.google.com/mapfiles/marker' + letter + '.png';
}


function getLatLangLocation(address, itemName, itemOwner, sdate, edate, day) {
  var newLat, newLang;
  if (geocoder) {
    geocoder.getLocations(address, function(result) {
      if (result.Status.code == 200) {
        if (result.Placemark.length > 0) {
          var objTrip = JGulliverData.getCurrentTrip();
          var place = result.Placemark[0];
          var accuracy = place.AddressDetails.Accuracy;
          var p = result.Placemark[0].Point.coordinates;
          var lat = p[1];
          var lang = p[0];
          var objItem = {
            id: '', name: '', address: '', review: '',
            sdate: '', edate: '', duration: 1, category: '',
            lat: '', lng: '', link: '', day: 0, weburl: '',
            imgurl: '', Item_thumb_up: 0, Item_thumb_down: 0,
            bCustom: ''
          };
          objItem.name = itemName;
          objItem.item_owner = itemOwner;
          objItem.review = address;
          objItem.address = address;
          objItem.sdate = sdate;
          objItem.edate = edate;
          objItem.lat = lat;
          objItem.lng = lang;
          objItem.day = day;
          objItem.bCustom = 1;
          objItem.data_source = 'custom';
          objTrip.saveItem(objItem);
          strSelectedItemId = '';
          hideDialogView();
        }
      } else {
        // to do - checking
        _gel('wrngmsg').innerHTML = prefs.getMsg('loc_not_found');
        _gel('wrngmsg').style.visibility = 'visible';

      }
    });
  }
}

/**
 * Function to show map on print preview dialog.
 * @param {number} lat The latitude for trip.
 * @param {number} lng The longitude for trip.
 * @param {number} accuracy Accuracy on map.
 */
function showStaticMap(lat, lng, accuracy) {
  _gel('print_content').innerHTML = '';
  var printMap;
  var mapContent = _gel('staticMap');
  if (_gel('print_Map').checked) {
    mapContent.style.display = 'block';
    mapContent.style.height = '400px';
    mapContent.style.width = '500px';
    printMap = new GMap2(mapContent);
      printMap.setCenter(new GLatLng(lat, lng), tabAccuracy[accuracy]);
      var bounds = printMap.getBounds();
      printMap.panTo(bounds.getCenter());
  } else {
    _gel('print_Dialog').scrollTop = 0;
    mapContent.style.display = 'none';
  }
}

/**
 * Checks whether the location is valid and if valid,
 *     update the location.
 * @param {string} address Location entered by user.
 * @param {number} nCreateTrip Flag to check whether trip is to be created.
 */
function showAddress(address, nCreateTrip) {
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
          if (nCreateTrip == 0) {
            var objTrip = JGulliverData.getCurrentTrip();
            objTrip.updateLocation(address, newLat, newLang, accuracy);
            _gel('resultBox').innerHTML = '';
            _gel('navBox').innerHTML = '';
            _gel('resultText').innerHTML = '';
            _gel('search-box').value = '';
          } else {
            JGulliverData.getInstance().createTrip(address,
                                                   newLat, newLang, accuracy);
          }
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

function showMarkerInfo() {
  if (!currentMarker || markerIndex == -1) {
    return;
  }
  _gel('msgContainer').style.display = 'none';
  var html = getPopupHtml(markerIndex, true, false);
  if (arrSearchResults[markerIndex].data_source = 'lonely') {
    currentMarker.openInfoWindowHtml(LOADING_TPL);
    html = html.replace(/%CLOSE_IMG%/, '')
        .replace(currentMarkerIcon, '')
        .replace('<img src=""/>', '');
    setTimeout(gMap.closeInfoWindow, 1000);
  }
  currentMarker.openInfoWindowHtml(html);
}
