/**
 * @fileoverview Containes trip related functions.
 * @author
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
  params[gadgets.io.RequestParameters.AUTHORIZATION] =
      gadgets.io.AuthorizationType.SIGNED;
  var url = BASE_URL + '?action=' + Operation.DELETE_TRIP +
      '&id=' + deleteTripId + '&ldap=' + gOwnerId;
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
      handleResponse();
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
  var currentId = gCurrentTripsData.currentTripId;
  var objTrip = getTripById(currentId);
  var params = {};
  params[gadgets.io.RequestParameters.AUTHORIZATION] =
      gadgets.io.AuthorizationType.SIGNED;

  var url = BASE_URL + '?action=' + Operation.RESCHEDULE_TRIP_ITEMS +
      '&ldap=' + gOwnerId + '&id=' + currentId;
  var tplData;
  gadgets.io.makeRequest(url, function(response) {
    if (response.errors.length) {
      tplData = {message: prefs.getMsg('tripdates_update_err')};
      showServerMessage(tplData);
    } else {
      tplData = {message: prefs.getMsg('allitems_updated')};
      showServerMessage(tplData);
      // Updating new trip dates in records.
      updateTripDateDisplay(startDate, endDate, newDuration, 'duration');
      fetchAllItems();
      hideViewDateDialogBox();
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
  var trip = getTripById(gCurrentTripsData.currentTripId);
  var params = {};
  var updatedData = {
    'key': trip.id,
    'thumbsUp': trip_thumb_up || 0,
    'thumbsDown': trip_thumb_down || 0,
    'name': trip.name,
    'duration': tripDuration,
    'startDate': Util.isEmpty(startDate) ?
                 null : getDateObject(startDate),
    'ownerId': trip.ownerId,
    'location': trip.loc,
    'latitude': trip.lat,
    'longitude': trip.lng,
    'ownerName': trip.ownerName,
    'description': trip.description
  };
  var postData = gadgets.io.encodeValues({
    'data': gadgets.json.stringify(updatedData),
    'action': Operation.UPDATE_TRIP,
    'ldap': gOwnerId
  });
  params[gadgets.io.RequestParameters.AUTHORIZATION] =
      gadgets.io.AuthorizationType.SIGNED;
  params[gadgets.io.RequestParameters.POST_DATA] = postData;
  params[gadgets.io.RequestParameters.METHOD] = gadgets.io.MethodType.POST;
  gadgets.io.makeRequest(BASE_URL, function(response) {
    if (response.errors.length) {
      showServerMessage({message: prefs.getMsg('tripdates_update_err')});
    } else {
      resetItemsDate(startDate, endDate, tripDuration);
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
    _gel(domElement).innerHTML = duration + '&nbsp;' +
                                 prefs.getMsg('unscheduled_trip');
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
    tripName = currentTrip.name;
    // Shorten trip name if length exceeds 28.
    tripName = tripName.length > 28 ? tripName.substr(0, 28) + '...' : tripName;
    tripId = currentTrip.key || currentTrip.id;
    trip.id = tripId;
    trip.ownerId = currentTrip.ownerId;
    trip.ownerName = currentTrip.ownerName || currentTrip.ownerId;
    trip.name = tripName;
    trip.lat = currentTrip.latitude;
    trip.lng = currentTrip.longitude;
    trip.loc = currentTrip.location;
    trip.startDate = currentTrip.startDate;
    trip.location = currentTrip.location;
    trip.accuracy = currentTrip.accuracy;
    trip.duration = currentTrip.duration;
    trip.collaborators = currentTrip.contributorIds;
    trip.description = currentTrip.description;
    trip.sdate = Util.isEmpty(currentTrip.startDate) ?
        '' : getFormattedDate(currentTrip.startDate);
    trip.fdate = Util.isEmpty(currentTrip.startDate) ?
        '' :
        DateLib.addDaysToDate(trip.sdate, trip.duration - 1, '{m}-{d}-{Y}');
    period = trip.sdate ?
        (trip.sdate + ' - ' + trip.fdate) : prefs.getMsg('unsch');
    trip.thumb_up = Util.isEmpty(currentTrip.thumbsUp) ?
        0 : currentTrip.thumbsUp;
    trip.thumb_down = Util.isEmpty(currentTrip.thumbsDown) ?
        0 : currentTrip.thumbsDown;
    // Pushing trips in global array.
    tripDB[tripId] = trip;
    if (!isSearchedTrips) {
      gCurrentTripsData.allTrips[trip.id] = trip;
      gCurrentTripsData.sortedTrips.push(trip);
    }
    if (isTripListing) {
      period = '---';
      // Checking whether dates of the trip are defined.
      // If start date is defined then final date will be defined.
      if (!Util.isEmpty(trip.sdate)) {
        // The year of start date might be different from the year of the
        // end date, however, these are rare cases and the chance to confuse
        // users is low. So, we will just show the year of the end date only.
        period = trip.duration > 1 ?
            DateLib.formatDate(trip.sdate, '{M} {d}') +
            ' - ' + DateLib.formatDate(trip.fdate, '{M} {d}, {Y}') :
            DateLib.formatDate(trip.sdate, '{M} {d}, {Y}');
      }
      var oneRecordTplData = {
        index: i,
        tripId: trip.id,
        tripName: tripName,
        period: period,
        location: trip.loc,
        ownerName: trip.ownerName,
        tripDesc: trip.description ? trip.description : trip.name,
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
      tripItem.dataSource != Datasource.CUSTOM ||
      tripItem.dataSource != Datasource.LONELY) {
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
    day: itemDay,
    category: tripItem.category
  };
  if (tripItem.dataSource == Datasource.CUSTOM) {
    var OTHERS_CATEGORY_INDEX = 7;
    var category = _gel('item_category');
    var selectedCategory = category.selectedIndex == OTHERS_CATEGORY_INDEX ?
        _trim(_gel('user_entry_category').value) :
        category[category.selectedIndex].value;
    updatedObj.category = selectedCategory;
  }
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
  tripItem.category = updatedObj.category;
  // Check for two things.
  // 1. Sometimes we dont get details other than name from lonelyplanet. In that
  // case editing to reschedule items is not allowed.
  // 2. While rescheduling items from unschedule to schedule we are always
  // confirming address via google geocoder. However, it fails to recogonize
  // valid lonely planet addresses for which we are by passing this check.
  if (tripItem.dataSource == Datasource.LONELY ||
      tripItem.dataSource == Datasource.GOOGLE ||
      (tripItem.dataSource == Datasource.CUSTOM && !tripItem.address)) {
    // Updating item details.
    updateItemDetails(tripItem);
    hideDialog();
    return;
  }
  if (geocoder) {
    geocoder.getLocations(updatedObj.address, function(result) {
      if (result.Status.code == RESPONSE_SUCCESS && result.Placemark.length) {
        if (tripItem.dataSource == Datasource.CUSTOM) {
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
  params[gadgets.io.RequestParameters.AUTHORIZATION] =
      gadgets.io.AuthorizationType.SIGNED;
  var updatedData = {
    'tripId': gCurrentTripsData.currentTripId,
    'key': rescheduledItem.id,
    'name': rescheduledItem.name,
    'review': rescheduledItem.address,
    'startDay': rescheduledItem.day,
    'latitude': rescheduledItem.lat,
    'longitude': rescheduledItem.lng,
    'address': rescheduledItem.address,
    'thumbsUp': rescheduledItem.Item_thumb_up || 0,
    'thumbsDown': rescheduledItem.Item_thumb_down || 0,
    'ownerName': rescheduledItem.item_owner || gViewer,
    'ownerId': rescheduledItem.ownerId,
    'description': rescheduledItem.description,
    'dataSource': rescheduledItem.dataSource,
    'category': rescheduledItem.category
  };
  var postData = gadgets.io.encodeValues({
    'data': gadgets.json.stringify(updatedData),
    'action': Operation.UPDATE_TRIP_ITEM,
    'ldap': gOwnerId
  });
  params[gadgets.io.RequestParameters.POST_DATA] = postData;
  gadgets.io.makeRequest(BASE_URL, function(response) {
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
  var url = BASE_URL + '?action=' + Operation.DELETE_TRIP_ITEM +
      '&ldap=' + gOwnerId + '&id=' + itemId;
  params[gadgets.io.RequestParameters.AUTHORIZATION] =
      gadgets.io.AuthorizationType.SIGNED;
  gadgets.io.makeRequest(url, function(response) {
    var tplData;
    if (response.errors.length) {
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
  var updatedData = {
    'ownerId': trip.ownerId,
    'ownerName': gViewer,
    'tripId': trip.id,
    'custom': objItem.isCustom,
    'name': _hesc(objItem.name),
    'address': objItem.address,
    'latitude': objItem.lat,
    'longitude': objItem.lng,
    'duration': objItem.duration,
    'startDay': objItem.day,
    'category': objItem.category,
    'searchResultUrl': objItem.link,
    'imageUrl': objItem.imgurl,
    'dataSource': objItem.dataSource,
    'description': objItem.review
  };
  var postData = gadgets.io.encodeValues({
    'data': gadgets.json.stringify(updatedData),
    'action': Operation.ADD_TRIP_ITEM,
    'ldap': gOwnerId
  });
  params[gadgets.io.RequestParameters.METHOD] = gadgets.io.MethodType.POST;
  params[gadgets.io.RequestParameters.POST_DATA] = postData;
  // Make the request as signed.
  params[gadgets.io.RequestParameters.AUTHORIZATION] =
      gadgets.io.AuthorizationType.SIGNED;
  gadgets.io.makeRequest(BASE_URL, function(response) {
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

// Exports
window.deleteSelectedItem = deleteSelectedItem;
window.saveEditedItem = saveEditedItem;
