/**
 * @fileoverview Utility methods for canvas view.
 * @author gadgetfactory@google.com (gadgetfactory)
 */

/**
 * Shows tooltip for map markers.
 * @param {Object} point LatLng object indicates marker coordinates
 *     on map.
 * @param {string} name Name of item shown as marker.
 */
function showToolTip(point, name) {
  var divPoint = gMap.fromLatLngToContainerPixel(point);
  var mapElementPosition = Util.getElementPosition(_gel('map-container'));
  var element = _gel('tool-tip');
  var eleStyle = element.style;
  eleStyle.display = 'block';
  eleStyle.zIndex = 10000;
  eleStyle.left = divPoint.x + mapElementPosition.x + 'px';
  eleStyle.top = divPoint.y + mapElementPosition.y - 40 + 'px';
  element.innerHTML = name;
}

/**
 * Adds a mask over the selected marker which helps in dragging the
 * marker.
 * @param {Object} point LatLng object indicates marker coordinates
 *     on map.
 */
function moveMask(point) {
  var divPoint = gMap.fromLatLngToContainerPixel(point);
  var mapElementPosition = Util.getElementPosition(_gel('map-container'));
  var eleStyle = _gel('mask').style;
  eleStyle.display = 'block';
  eleStyle.zIndex = 10000;
  eleStyle.left = divPoint.x + mapElementPosition.x - 10 + 'px';
  eleStyle.top = divPoint.y + mapElementPosition.y - 34 + 'px';
}

/**
 * Method to sort an array based on sort type.
 * @param {Array} unsortedData The unsorted data.
 */
function sortByColumn(unsortedData) {
  unsortedData.sort(function(obj1, obj2) {
    var returnValue = 0;
    switch (sortCol) {
      case enTripCol.NAME:
        returnValue = obj1.name.toLowerCase() >
            obj2.name.toLowerCase() ? 1 : -1;
        break;
      case enTripCol.LOCATION:
        returnValue = obj1.loc.toLowerCase() >
            obj2.loc.toLowerCase() ? 1 : -1;
        break;
      case enTripCol.DATE:
        returnValue = obj1.sdate > obj2.sdate ? 1 : -1;
        break;
      case enTripCol.OWNER:
        returnValue = obj1.ownerName.toLowerCase() >
            obj2.ownerName.toLowerCase() ? 1 : -1;
        break;
      case enTripCol.DAY:
        returnValue = obj1.day > obj2.day ? 1 : -1;
        break;
    }
    return isAscending ? returnValue : 0;
  });
}

/**
 * To display the up/down arrow sign while sorting.
 * @param {number} val Column number.
 */
function displayArrowSign(val) {
  var imgDivSrc = _gel('img-' + val);
  sortCol = val;
  if (!imgDivSrc.innerHTML.match('down')) {
    imgDivSrc.innerHTML = ARROW_IMG;
    imgDivSrc.className = 'down-arrow';
    isAscending = 0;
  } else if (!imgDivSrc.innerHTML.match('up')) {
    imgDivSrc.innerHTML = ARROW_IMG;
    imgDivSrc.className = 'up-arrow';
    isAscending = 1;
  }
  sortByColumn(searchFlag ? gCurrentTripsData.sortedTrips : searchedTrips);
  showListings(searchFlag, true, false);
  // 4 refers to number of columns.
  for (var i = 0; i < 4; i++) {
    var element = _gel('img-' + i);
    element.innerHTML = ARROW_IMG;
    element.className = (sortCol == i) ?
        isAscending ? 'up-arrow' : 'down-arrow' : '';
  }
}

/**
 * Function to clear start date and end date.
 */
function clearDate() {
  _gel('endDate').value = '';
  _gel('startDate').value = '';
}

/**
 * Gives duration of trip if trip dates are valid else shows warning message.
 * @return {number} Returns valid trip days.
 */
function getNumDays() {
  var idWarningMsg = _gel('warning-msg');
  var tripDays = _gel('edit-days-box').value;
  if (Util.isEmpty(_trim(tripDays))) {
    idWarningMsg.innerHTML = prefs.getMsg('invalid_duration');
    idWarningMsg.style.visibility = 'visible';
    return;
  }
  var days = parseInt(tripDays, 10);
  if (days <= 0 || days == Number.NaN && idWarningMsg) {
    idWarningMsg.innerHTML = prefs.getMsg('invalid_duration');
    idWarningMsg.style.visibility = 'visible';
    return;
  }
  // Maximum trip duration can be of 60 days.
  if (days > 60 && idWarningMsg) {
    idWarningMsg.innerHTML = prefs.getMsg('duration_limit_exceeds');
    idWarningMsg.style.visibility = 'visible';
    return;
  }

  return days;
}

/**
 * Validates and update new trip dates, duration and days and shows
 * warning message if either is invalid.
 * @param {number} days Valid trip days.
 * @return {boolean} Returns true if trip dates are valid else false.
 */
function validateTripDuration(days) {
  var objTrip = getTripById(gCurrentTripsData.currentTripId);
  var idWarningMsg = _gel('warning-msg');
  // Getting trip new start and end dates.
  var tripStartDate, tripEndDate;
  if (objTrip.sdate && objTrip.fdate) {
    tripStartDate = objTrip.sdate;
    tripEndDate = objTrip.fdate;
    if (tripStartDate.indexOf('-') > -1) {
      DateLib.formatDate(tripStartDate);
    }
    if (tripEndDate.indexOf('-') > -1) {
      DateLib.formatDate(tripEndDate);
    }
  }
  var startDate = _gel('startDate').value;
  var endDate = _gel('endDate').value;
  if (tripStartDate || tripEndDate) {
    if (!startDate || !endDate) {
      idWarningMsg.innerHTML = prefs.getMsg('no_empty_dates');
      idWarningMsg.style.visibility = 'visible';
      return false;
    }
  }
  if (!Util.isEmpty(startDate) && !Util.isEmpty(endDate)) {
    var dateDiff = DateLib.getDateDiff(getDateString(new Date()), startDate);
    if (dateDiff >= 0) {
      idWarningMsg.style.visibility = 'hidden';
    } else {
      idWarningMsg.innerHTML = prefs.getMsg('date_expired');
      idWarningMsg.style.visibility = 'visible';
      return false;
    }
    if ((startDate != tripStartDate) ||
        (endDate != tripEndDate) ||
        (days != objTrip.duration)) {
      objTrip.sdate = startDate;
      objTrip.fdate = endDate;
      objTrip.duration = days;
      hideViewDateDialogBox();
    }
  } else if (!Util.isEmpty(tripStartDate) && !Util.isEmpty(tripEndDate)) {
    endDate = DateLib.addDaysToDate(tripStartDate, days - 1, '{m}/{d}/{Y}');
    objTrip.fdate = endDate;
    objTrip.duration = days;
  } else if (days != objTrip.duration) {
    objTrip.duration = days;
  }
  return true;
}

/**
 * To save trip dates, when user modifies it.
 */
function saveTripDates() {
  var objTrip = getTripById(gCurrentTripsData.currentTripId);
  var oldDuration = objTrip.duration;
  var days = getNumDays();
  if (days && validateTripDuration(days)) {
    // To move items whose date or day of occurrence is greater than
    // trip end date or day, to unscheduled section.
    if (oldDuration > days) {
      resetItemsDate(objTrip.sdate, objTrip.fdate, days);
    } else {
      // Updating new trip dates in records.
      updateTripDate(objTrip.sdate, objTrip.fdate, days);
    }
  }
}

/**
 * Add new item to the itinerary and call method to save it to database.
 * @param {number} index The index of an item.
 * @param {number} day The day for an item.
 */
function addToItinerary(index, day) {
  var tripItem = getNewItemObjectByIndex(index, day);
  saveItem(tripItem);
}

/**
 * Returns trip item object.
 * @param {number} index The index of an item.
 * @param {number} opt_day The day for an item (optional).
 * @return {Object} Trip item object.
 */
function getNewItemObjectByIndex(index, opt_day) {
  var tripItem = getItemObject();
  var trip = getTripById(gCurrentTripsData.currentTripId);
  // Filling trip item with values.
  tripItem.ownerName = gViewer;
  var searchData = gCurrentTripsData.arrSearchResults[index];
  tripItem.name = searchData.name;
  var address = searchData.address;
  tripItem.address = address ? Util.stripHtml(address) : '';
  var review = searchData.review;
  tripItem.review = review ? Util.stripHtml(searchData.review) : '';
  tripItem.lat = searchData.lat;
  tripItem.lng = searchData.lng;
  tripItem.category = searchData.category || '';
  tripItem.isCustom = false;
  tripItem.weburl = searchData.weburl || '';
  tripItem.imgurl = searchData.imgurl || '';
  tripItem.link = searchData.link || '';
  tripItem.day = opt_day || 0;
  tripItem.dataSource = searchData.dataSource || 'custom';
  tripItem.tripName = trip.name;
  if (!Util.isEmpty(trip.sdate) && opt_day) {
    var formatDate =
        DateLib.addDaysToDate(trip.sdate, tripItem.day - 1, '{m}/{d}/{Y}');
    tripItem.sdate = tripItem.fdate = formatDate;
  }
  return tripItem;
}

// Exports
window.displayArrowSign = displayArrowSign;
window.clearDate = clearDate;
window.saveTripDates = saveTripDates;
window.hideCalender = hideCalender;
