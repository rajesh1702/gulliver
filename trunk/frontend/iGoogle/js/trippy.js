/**
 * @fileoverview Containes trip related functions.
 * 
 */

/**
 * Creates item object and returns it.
 * @return {Object} Returns item object.
 */
function getItemObject() {
  return {
    id: '',
    name: '',
    ownerId: '',
    ownerName: '',
    address: '',
    lat: '',
    lng: '',
    review: prefs.getMsg('no reviews'),
    sdate: '',
    fdate: '',
    duration: 1,
    category: '',
    link: '',
    day: 0,
    weburl: '',
    imgurl: '',
    thumbUp: 0,
    thumbDown: 0,
    isCustom: false,
    lastModified: '',
    dataSource: '',
    tripName: ''
  };
}

/**
 * Returns a trip by its id.
 * @param {string} id trip id.
 * @return {Array} Returns array of trip items.
 */
function getTripById(id) {
  return tripDB[id];
}

/**
 * Returns item object specified by id.
 * @param {string} id Item id.
 * @return {Object} Returns item object.
 */
function getItemById(id) {
  for (var i = 0, itemsLength = gTripItemDB.length; i < itemsLength; i++) {
    var tripItem = gTripItemDB[i];
    if (tripItem.id == id) {
      return tripItem;
    }
  }
}

/**
 * It deletes the current trip from db.
 * @param {string} deleteTripId Trip id.
 */
JGulliverData.prototype.deleteTrip = function(deleteTripId) {
  var allTripsData = this;
  var params = {};
  var objTrip = getTripById(deleteTripId);
  params[gadgets.io.RequestParameters.METHOD] = gadgets.io.MethodType.POST;
  var postData = gadgets.io.encodeValues({'trip_id': deleteTripId});
  params[gadgets.io.RequestParameters.POST_DATA] = postData;
  var url = BASE_URL + '/deleteTrip?rand=' + Math.random();
  var tplData;
  gadgets.io.makeRequest(url, function(response) {
    var responseData = gadgets.json.parse(response.data);
    if (responseData.error == transResponse.ERROR) {
      tplData = {
          message: Util.supplant(prefs.getMsg('trip_delete_err'),
                                 {name: _unesc(objTrip.name)})
      };
      showServerMessage(tplData);
    } else {
      allTripsData.arrTripData.splice(allTripsData.nSelectedTripIndex, 1);
      tplData = {
        message: Util.supplant(prefs.getMsg('trip_deleted'),
                               {name: _unesc(objTrip.name)})
      };
      showServerMessage(tplData);
      hideDialog();
      loadFriends();
    }
  }, params);
};

/**
 * Delete an item object or trip object.
 * @param {string} itemId Id of an item.
 * @param {boolean} isItem Flag to check if its an item or a trip id.
 */
function deleteSelectedItem(itemId, isItem) {
  hideDialog();
  if (isItem) {
    deleteItem(itemId);
  } else {// Trip needs to be deleted.
    JGulliverData.getInstance().deleteTrip(itemId);
  }
}

/**
 * Method to save modified item day in the database when its day of occurrence
 * is rescheduled by dragging or through dialog box.
 * @param {string} rescheduledItemId Id of item to be rescheduled.
 * @param {number} day The day at which to be rescheduled.
 */
function saveRescheduledItem(rescheduledItemId, day) {
  var rescheduledItem = getItemById(rescheduledItemId);
  if (!rescheduledItem) {
    return;
  }
  if (rescheduledItem.day != day) {
    var trip = getTripById(gCurrentTripsData.currentTripId);
    rescheduledItem.day = day;
    rescheduledItem.sdate = rescheduledItem.fdate =
        (!Util.isEmpty(trip.sdate) && day) ?
        DateLib.addDaysToDate(trip.sdate, day - 1, '{m}/{d}/{Y}') : '';
    updateItemDetails(rescheduledItem);
  }
}

/**
 * It updates dates value for items whose item day exceed trip duration.
 * @param {string} startDate start date of trip.
 * @param {string} endDate end date of trip.
 * @param {number} newDuration New duration of the trip.
 */
function resetItemsDate(startDate, endDate, newDuration) {
  var objTrip = getTripById(gCurrentTripsData.currentTripId);
  var params = {};
  params[gadgets.io.RequestParameters.METHOD] = gadgets.io.MethodType.POST;
  var postData = gadgets.io.encodeValues({
    'trip_id': gCurrentTripsData.currentTripId,
    'sdate': startDate,
    'fdate': endDate,
    'days': newDuration,
    'duration': newDuration
  });
  params[gadgets.io.RequestParameters.POST_DATA] = postData;
  var url = BASE_URL + '/saveItemDatesAsZero?rand=' + Math.random();
  var tplData;
  gadgets.io.makeRequest(url, function(response) {
    var responseData = gadgets.json.parse(response.data);
    if (responseData.error == transResponse.ERROR) {
      tplData = {message: prefs.getMsg('tripdates_update_err')};
      showServerMessage(tplData);
    } else {
      tplData = {message: prefs.getMsg('allitems_updated')};
      showServerMessage(tplData);
      // Updating new trip dates in records.
      updateTripDate(startDate, endDate, newDuration);
      updateTripDateDisplay(startDate, endDate, newDuration, 'duration');
    }
  }, params);
}

/**
 * It updates dates value for current trip in to db.
 * @param {string} startDate Start date of trip.
 * @param {string} endDate End date of trip.
 * @param {number} tripDuration Duration of trip.
 */
function updateTripDate(startDate, endDate, tripDuration) {
  var objTrip = getTripById(gCurrentTripsData.currentTripId);
  var params = {};
  params[gadgets.io.RequestParameters.METHOD] = gadgets.io.MethodType.POST;
  var postData = gadgets.io.encodeValues({
    'trip_id': gCurrentTripsData.currentTripId,
    'sdate': startDate,
    'fdate': endDate,
    'duration': tripDuration
  });
  params[gadgets.io.RequestParameters.POST_DATA] = postData;
  var url = BASE_URL + '/updateTripDate?rand=' + Math.random();
  var tplData;
  gadgets.io.makeRequest(url, function(response) {
    var responseData = gadgets.json.parse(response.data);
    if (responseData.error == transResponse.ERROR) {
      tplData = {message: prefs.getMsg('tripdates_update_err')};
      showServerMessage(tplData);
    } else {
      updateTripDateDisplay(startDate, endDate, tripDuration, 'duration');
      fetchAllItems();
      hideViewDateDialogBox();
    }
  }, params);
}

/**
 * Method to update and display trip dates and duration.
 * @param {string} startDate The start date of the trip.
 * @param {string} endDate The end date of the trip.
 * @param {number} duration Trip duration.
 * @param {string} domElement Id of element used to show duartion of
 *     the trip.
 */
function updateTripDateDisplay(startDate, endDate, duration, domElement) {
  if (Util.isEmpty(startDate) && Util.isEmpty(endDate)) {
    _gel(domElement).innerHTML = '(' + duration + '&nbsp;' +
                                 prefs.getMsg('days') + ')';
  } else {
    _gel(domElement).innerHTML = DateLib.formatDate(startDate, '{M} {d}') +
        ' - ' + DateLib.formatDate(endDate, '{M} {d}, {Y}') + ' (' + duration +
        ' ' + prefs.getMsg('days') + ')';
  }
}

/**
 * Parse trips for listing page on canvas.
 * @param {Array} trips Trip array.
 * @param {boolean} isSearchedTrips Check whether trips data is an array of all
 *     trips or array of searched trips.
 * @param {boolean} isTripListing To check from where request has came.
 * @return {Array} Listing for trips.
 */
function parseTripsList(trips, isSearchedTrips, isTripListing) {
  var tripListings = [];
  var tripsLength = trips.length;
  var trip, currentTrip;
  var period, tripName, tripId;
  var oneRecordTpl = _gel('tpl-trip-listing-one-record').value;
  for (var i = 0; i < tripsLength; i++) {
    trip = getTripObject();
    currentTrip = trips[i];
    period = currentTrip.sdate ?
        (currentTrip.sdate + ' - ' + currentTrip.fdate) : prefs.getMsg('unsch');
    tripName = currentTrip.name;
    // Shorten trip name if length exceeds 28.
    tripName = tripName.length > 28 ? tripName.substr(0, 28) + '...' : tripName;
    tripId = currentTrip.id;
    trip.id = tripId
    trip.ownerId = currentTrip.ownerId;
    trip.ownerName = currentTrip.ownerName;
    trip.name = tripName;
    trip.lat = currentTrip.lat;
    trip.lng = currentTrip.lng;
    trip.loc = currentTrip.loc;
    trip.accuracy = currentTrip.accuracy;
    trip.duration = currentTrip.duration;
    trip.sdate = currentTrip.sdate;
    trip.fdate = currentTrip.fdate;
    trip.thumb_up = Util.isEmpty(currentTrip.thumb_up) ?
        0 : currentTrip.thumb_up;
    trip.thumb_down = Util.isEmpty(currentTrip.thumb_down) ?
        0 : currentTrip.thumb_down;
    // Pushing trips in global array.
    tripDB[tripId] = trip;
    if (!isSearchedTrips) {
      gCurrentTripsData.allTrips[trip.id] = trip;
      gCurrentTripsData.sortedTrips.push(trip);
    }
    if (isTripListing) {
      var oneRecordTplData = {
        index: i,
        tripId: trip.id,
        tripName: tripName,
        period: period,
        location: trip.loc,
        ownerName: trip.ownerName,
        flag: 0
      };
      tripListings.push(Util.supplant(oneRecordTpl, oneRecordTplData));
    }
  }
  if (isTripListing) {
    return tripListings;
  }
}

/**
 * Fill the trips dates for a particular trip.
 */
function fillTripDates() {
  tripDates = [];
  var trip = getTripById(gCurrentTripsData.currentTripId);
  for (var i = 0; i < trip.duration; i++) {
    tripDates.push(DateLib.addDaysToDate(trip.sdate, i, '{m}/{d}/{Y}'));
  }
}

/**
 * Save edited item info.
 * @param {number} index Index of an item.
 */
function saveEditedItem(index) {
  var itemDescElement = _gel('item-description');
  var message = _gel('warning-msg');
  // shorten the item description if its length exceeds 200.
  if (itemDescElement.value.length > 200) {
    message.style.visibility = 'visible';
    message.innerHTML = prefs.getMsg('desc_error_message');
    return;
  }
  var itemName, itemDesp, itemDay, itemStartDate, itemEndDate;
  var tripItem = getItemById(index);
  var itemNameElement = _gel('item-name');
  if (itemNameElement.value) {
    itemName = itemNameElement.value;
  } else {
    message.innerHTML = prefs.getMsg('field_empty');
    message.style.display = 'block';
    itemNameElement.focus();
    return;
  }
  if (!Util.isEmpty(itemDescElement.value) ||
      tripItem.dataSource != 'custom' ||
      tripItem.dataSource != 'lonely') {
    itemDesp = itemDescElement.value;
  } else {
    message.innerHTML = prefs.getMsg('field_empty');
    message.style.display = 'block';
    itemDescElement.focus();
    return;
  }
  if (_gel('unscheduleCheck').checked) {
    itemStartDate = '';
    itemEndDate = '';
    itemDay = 0;
  } else {
    var daySelectElement = _gel('daysSelect');
    var trip = getTripById(gCurrentTripsData.currentTripId);
    if (!Util.isEmpty(trip.sdate)) {
      itemEndDate = itemStartDate =
          daySelectElement[daySelectElement.selectedIndex].value;
      if (trip.sdate.indexOf('-') > -1) {
        trip.sdate = DateLib.formatDate(trip.sdate);
      }
      itemDay = DateLib.getDateDiff(trip.sdate, itemStartDate) + 1;
    } else {
      itemDay = daySelectElement[daySelectElement.selectedIndex].value;
      itemEndDate = itemStartDate = '';
    }
  }
  var updatedObj = {
    name: itemName,
    address: itemDesp,
    sdate: itemStartDate,
    edate: itemEndDate,
    day: itemDay
  };
  updateEditedItems(updatedObj, index);
}

/**
 * Calls method to updated edited item.
 * @param {Object} updatedObj Item object to be updated.
 * @param {number} index Index of item.
 */
function updateEditedItems(updatedObj, index) {
  var tripItem = getItemById(index);
  var message = _gel('warning-msg');
  tripItem.name = updatedObj.name;
  tripItem.address = updatedObj.address;
  tripItem.sdate = updatedObj.sdate;
  tripItem.fdate = updatedObj.edate;
  tripItem.day = updatedObj.day;
  // Check for two things.
  // 1. Sometimes we dont get details other than name from lonelyplanet. In that
  // case editing to reschedule items is not allowed.
  // 2. While rescheduling items from unschedule to schedule we are always
  // confirming address via google geocoder. However, it fails to recogonize
  // valid lonely planet addresses for which we are by passing this check.
  if (tripItem.dataSource == 'lonely' ||
      tripItem.dataSource == 'google' ||
      (tripItem.dataSource == 'custom' && !tripItem.address)) {
    // Updating item details.
    updateItemDetails(tripItem);
    hideDialog();
    return;
  }
  if (geocoder) {
    geocoder.getLocations(updatedObj.address, function(result) {
      if (result.Status.code == RESPONSE_SUCCESS && result.Placemark.length) {
        if (tripItem.dataSource == 'custom') {
          var place = result.Placemark[0];
          var point = place.Point.coordinates;
          tripItem.lat = point[1];
          tripItem.lng = point[0];
        }
        // Updating item details.
        updateItemDetails(tripItem);
        hideDialog();
      } else {
        message.style.visibility = 'visible';
        message.innerHTML = prefs.getMsg('loc_not_found');
      }
    });
  } else {
    message.style.visibility = 'visible';
    message.innerHTML = prefs.getMsg('map_not_loaded');
  }
}

/**
 * It updates all values for a specific item in to db.
 * @param {Object} rescheduledItem Object of item to be rescheduled.
 */
function updateItemDetails(rescheduledItem) {
  var params = {};
  params[gadgets.io.RequestParameters.METHOD] = gadgets.io.MethodType.POST;
  var postData = gadgets.io.encodeValues({
    'item_id': rescheduledItem.id,
    'sdate': rescheduledItem.sdate,
    'fdate': rescheduledItem.fdate,
    'name': rescheduledItem.name,
    'review': rescheduledItem.address,
    'day' : rescheduledItem.day,
    'lat' : rescheduledItem.lat,
    'lang' : rescheduledItem.lng
  });
  params[gadgets.io.RequestParameters.POST_DATA] = postData;
  var url = BASE_URL + '/updateItemInfo?rand=' + Math.random();
  gadgets.io.makeRequest(url, function(response) {
    var tplData;
    var responseData = gadgets.json.parse(response.data);
    if (responseData.error == transResponse.ERROR) {
      tplData = {
        message: Util.supplant(prefs.getMsg('itemdates_update_err'),
                               {name: _unesc(rescheduledItem.name)})
      };
      showServerMessage(tplData);
    } else {
      tplData = {
        message: Util.supplant(prefs.getMsg('itemdates_updated'),
                               {name: _unesc(rescheduledItem.name)})
      };
      showServerMessage(tplData);
      fetchAllItems();
    }
  }, params);
}

/**
 * It deletes the item for current trip in to db.
 * @param {number} itemId Id of an item.
 */
function deleteItem(itemId) {
  var itemObject = getItemById(itemId);
  var params = {};
  params[gadgets.io.RequestParameters.METHOD] = gadgets.io.MethodType.POST;
  var postData = gadgets.io.encodeValues({'item_id': itemObject.id});
  params[gadgets.io.RequestParameters.POST_DATA] = postData;
  var url = BASE_URL + '/deleteItem?rand=' + Math.random();
  gadgets.io.makeRequest(url, function(response) {
    var tplData;
    var responseData = gadgets.json.parse(response.data);
    if (responseData.error == transResponse.ERROR) {
      tplData = {
        message: Util.supplant(prefs.getMsg('item_delete_err'),
                               {name: _unesc(itemObject.name)})
      };
      showServerMessage(tplData);
    } else {
      tplData = {
        message: Util.supplant(prefs.getMsg('item_deleted'),
                               {name: _unesc(itemObject.name)})
      };
      showServerMessage(tplData);
      // Adding item marker in an array of markers to be deleted,
      gDeletedMarkers.push(itemObject.id);
      if (gDeletedMarkers.length) {
        var itemId = gDeletedMarkers[0];
        gMap.removeOverlay(gCurrentTripsData.itemMarkers[itemId]);
      }
      gDeletedMarkers = [];
      fetchAllItems();
   }
  }, params);
}

/**
 * It saves the item to current trip.
 * @param {Object} objItem object of JTripItem.
 */
function saveItem(objItem) {
  var trip = getTripById(gCurrentTripsData.currentTripId);
  _gel('trip-items-content-' + objItem.day || 0).innerHTML +=
      getHtmlForOneItem(objItem);

  if (objItem.review) {
    objItem.review = Util.stripHtml(objItem.review).substring(0, 450);
  }
  var params = {};
  params[gadgets.io.RequestParameters.METHOD] = gadgets.io.MethodType.POST;
  var postData = {
    'user_id': trip.ownerId,
    'item_owner': gViewer,
    'trip_id': trip.id,
    'custom': objItem.isCustom,
    'name': _hesc(objItem.name),
    'location': objItem.address,
    'lat': objItem.lat,
    'lang': objItem.lng,
    'review': objItem.review,
    'duration': objItem.duration,
    'day': objItem.day,
    'category': objItem.category,
    'link': objItem.link,
    'weburl': objItem.weburl,
    'imgurl': objItem.imgurl,
    'dataSource': objItem.dataSource,
    'sdate': '',
    'fdate': ''
  };
  if (objItem.sdate && objItem.fdate) {
    postData.fdate = objItem.fdate;
    postData.sdate = objItem.sdate;
  }
  params[gadgets.io.RequestParameters.POST_DATA] =
      gadgets.io.encodeValues(postData);
  var url = BASE_URL + '/saveItemToTrip?rand=' + Math.random();
  gadgets.io.makeRequest(url, function(response) {
    var responseData = gadgets.json.parse(response.data);
    var tplData;
    if (responseData.error == transResponse.ERROR) {
      tplData = {message: prefs.getMsg('item_add_err')};
      showServerMessage(tplData);
    } else {
      tplData = {
        message: Util.supplant(prefs.getMsg('item_added'),
                               {name: _unesc(objItem.name)})
      };
      showServerMessage(tplData);
      fetchAllItems();
    }
  }, params);
}

/**
 * This function will be used to change the trip location from canvas view.
 */
function changeTripLocation() {
  var idWarningMsg = _gel('wrngmsg');
  var location = _gel('txtLocation').value;
  var tripData = getTripById(gCurrentTripsData.currentTripId);
  if (_trim(location) != '') {
    var validLocation = Util.isNumeric(location);
    if (validLocation) {
      idWarningMsg.innerHTML = prefs.getMsg('invalid_loc');
      idWarningMsg.style.visibility = 'visible';
    } else {
      if (location != tripData.loc) {
        validateLocation(location);
      } else {
        hideDialog();
      }
    }
  } else {
    idWarningMsg.innerHTML = prefs.getMsg('enter_loc');
    idWarningMsg.style.visibility = 'visible';
  }
}

/**
 * Checks whether the location is valid and if valid,
 *     update the location.
 * @param {string} address Location entered by user.
 */
function validateLocation(address) {
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
        _gel('wrngmsg').innerHTML = prefs.getMsg('loc_not_found');
        _gel('wrngmsg').style.visibility = 'visible';
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
  var tripId = gCurrentTripsData.currentTripId;
  var trip = getTripById(tripId);
  var oldLoc = trip.loc;
  trip.loc = address;
  trip.lat = newLat;
  trip.lng = newLang;
  trip.accuracy = accuracy;
  var start = new GLatLng(trip.lat, trip.lng);
  gMap.setCenter(start, tripAccuracy[trip.accuracy]);
  var params = {};
  params[gadgets.io.RequestParameters.METHOD] = gadgets.io.MethodType.POST;
  var postdata = gadgets.io.encodeValues({
    'trip_id': tripId,
    'lat': newLat,
    'lng' : newLang,
    'location': address
  });
  params[gadgets.io.RequestParameters.POST_DATA] = postdata;
  var url = BASE_URL + '/updateTripLocation?rand=' + Math.random();
  var tplData;
  gadgets.io.makeRequest(url, function(response) {
    var responseData = gadgets.json.parse(response.data);
    if (responseData.error == transResponse.ERROR) {
      tplData = {
        message: Util.supplant(prefs.getMsg('triploc_update_err'))
      };
      showServerMessage(tplData);
      return false;
    }
    tplData = {
        message: Util.supplant(prefs.getMsg('triploc_updated'))
    };
    showServerMessage(tplData);
    trip.loc = address;
    trip.lng = newLang;
    trip.lat = newLat;
    gMap.clearOverlays();
    gMap.setCenter(new GLatLng(newLat, newLang), tripAccuracy[accuracy]);
  }, params);
}
// Exports
window.deleteSelectedItem = deleteSelectedItem;
window.saveEditedItem = saveEditedItem;
