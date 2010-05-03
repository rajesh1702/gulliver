/**
 * @fileoverview Code contains canvas view related functionality.
 * @author gadgetfactory@google.com (gadgetfactory)
 */

/**
 * Flag to decide whether we have to move to trip page or trips listing.
 * @type {boolean}
 */
var isGetData = true;

/**
 * Array to hold friends list for a user.
 * @type {Array}
 */
var friendsData = [];

/**
 * To hold scheduled and unscheduled items count.
 * @type {number}
 */
var scheduledItems, unScheduledItems;

/**
 * To hold id of last opened item.
 */
var lastOpenedItem = -1;

/**
 * Url of marker.
 */
var MARKER_IMG = 'http://www.google.com/mapfiles/marker.png';

/**
 * Url of blank image.
 */
var BLANK_IMG = 'http://www.google.com/c.gif';

/**
 * Url of shadow image.
 */
var SHADOW_IMG = 'http://www.google.com/mapfiles/shadow50.png'

/**
 * Validates duration for a trip when new dates are selected through dialog box.
 * @param {Object} domElement Duration html element.
 */
function validateUpdatedDuration(domElement) {
  var duration = domElement.value;
  var warningMsg = _gel('warning-msg');
  if (!_trim(duration) || !Util.isNumeric(duration)) {
    if (warningMsg) {
      warningMsg.innerHTML = prefs.getMsg('invalid_duration');
      warningMsg.style.visibility = 'visible';
      return;
    }
  }
  var tripDuration = parseInt(duration, 10);
  var startDate = _gel('startDate').value;
  _gel('endDate').value = Util.isEmpty(startDate) ?
      prefs.getMsg('unspecified') :
      DateLib.addDaysToDate(startDate, (tripDuration - 1), '{m}/{d}/{Y}');
  if (warningMsg) {
    if (tripDuration > getTripById(gCurrentTripsData.currentTripId).duration) {
      warningMsg.style.visibility = 'hidden';
    } else {
      warningMsg.style.visibility = 'visible';
      warningMsg.innerHTML = prefs.getMsg('less_trip_duration');
    }
  }
}

/**
 * Method to search matching trips among trips in canvas section.
 */
function searchTrips() {
  searchedTrips = [];
  searchFlag = false;
  var searchTxt = _trim(_gel('searchTripText').value).toLowerCase();
  if (!searchTxt) {
    return;
  }
  var tripData;
  var sortedTripsLength = gCurrentTripsData.sortedTrips.length;
  for (var i = 0; i < sortedTripsLength; i++) {
    tripData = gCurrentTripsData.sortedTrips[i];
    if (tripData.name.toLowerCase().indexOf(searchTxt) == -1) {
      continue;
    }
    searchedTrips.push(tripData);
  }
  showListings(false, true, true);
}

/**
 * It returns the html string for popup of an item.
 * @param {string} id Item id.
 * @param {boolean} flag Flag to decide which template to use.
 * @return {string} Returns html for an item as astring.
 */
function getItemHtml(id, flag) {
  var tplHtml = _gel('tpl-popup-info').value;
  var tripItem = getItemById(id);
  var imgUrl = MARKER_IMG;
  var className = '';
  if (tripItem.dataSource == 'lonely') {
    imgUrl = BLANK_IMG;
    className = LONELY_IMG[tripItem.category].className;
  }
  var tplData = {
    title: _unesc(tripItem.name),
    webUrl: tripItem.weburl || '',
    ownerName: tripItem.ownerName,
    display: 'none',
    index: tripItem.id,
    thumbUp: tripItem.thumbUp || 0,
    thumbDown: tripItem.thumbDown || 0,
    imgUrl: imgUrl,
    className: className
  };

  var address1 = '';
  // Getting the index of <br/> tag.
  var newLineIndex = tripItem.address.search(/<br\/>/);
  var zip = '';
  if (newLineIndex != -1) {
    zip = tripItem.address.substr(newLineIndex);
    address1 = tripItem.address.substr(0, newLineIndex);
  } else {
    address1 = tripItem.address;
  }
  if (address1) {
    tplData.contactInfo = getContactInfo(address1, zip);
  } else {
    tplData.contactInfo = '';
  }
  return Util.supplant(tplHtml, tplData);
}

/**
 * Function to show marker information as a popup on map.
 * @param {number} id Id of marker.
 */
function showItemMarker(id) {
  var marker = gCurrentTripsData.itemMarkers[id];
  if (marker) {
    marker.openInfoWindowHtml(getPopupHtmlItem(getItemById(id)));
  }
}

/**
 * Function to hide or display content in itinerary area.
 * @param {number} index Index of an element to toggle.
 * @param {boolean} flag Flag to hide or show element.
 */
function toggleDisplay(index, flag) {
  var toggleElement = _gel('trip-items-content-' + index).style;
  var tripItemElement = _gel('trip-items-num-' + index);
  var expandedItemElement = _gel('item-expand-' + index);
  if (toggleElement.display == 'none') {
    toggleElement.display = 'block';
    expandedItemElement.src = BLANK_IMG;
    expandedItemElement.className = 'zippy-minus';
    tripItemElement.style.display = 'none';
  } else {
    toggleElement.display = 'none';
    expandedItemElement.src = BLANK_IMG;
    expandedItemElement.className = 'zippy-plus';

    if (flag) {
      tripItemElement.style.display = 'none';
      tripItemElement.style.background = '#fff';
    } else {
      tripItemElement.style.display = '';
      tripItemElement.style.background = '#8d8d8d';
    }
  }
}

/**
 * Function to show dialog for custom item.
 */
function createCustomItemDialog() {
  var trip = getTripById(gCurrentTripsData.currentTripId);
  // Prepare html to display dates or days, based on trip date.
  var durationHtml = [];
  var duration = parseInt(trip.duration);
  var tempDate;
  if (Util.isEmpty(trip.sdate)) {
    durationHtml.push(prefs.getMsg('day'), ': <select style="width:25%;" ',
        'id="days_id" disabled>');
    // Loop start from 1 in order to show day1, day2.
    for (var i = 1; i <= duration; i++) {
      durationHtml.push('<option value="', i, '">', prefs.getMsg('day'),
          '&nbsp;', i, '</option>');
    }
  } else {
    durationHtml.push(prefs.getMsg('day'), ': <select style="width:35%;" ',
        'id="days_id" disabled>');
    for (var i = 1; i <= duration; i++) {
      tempDate = DateLib.addDaysToDate(trip.sdate, i - 1, '{m}/{d}/{Y}');
      if (DateLib.addDaysToDate(trip.sdate, 0, '{m}/{d}/{Y}') == tempDate) {
        durationHtml.push('<option value="', tempDate, '" selected>',
            tempDate, '</option>');
      } else {
        durationHtml.push('<option value="', tempDate, '">', tempDate,
            '</option>');
      }
    }
  }
  durationHtml.push('</select>');
  var tplHtml = _gel('tpl-item-creation').value;
  var tplData = {dateHtml: durationHtml.join('')};
  showDialog(Util.supplant(tplHtml, tplData));
  _gel('item-name').focus();
}

/**
 * Function which parse all trip items data and fill them into respective
 * containers and call method to add markers on map.
 */
function putItemsData() {
  var trip = getTripById(gCurrentTripsData.currentTripId);
  var htmlContent = [];
  var tplData = [];
  var toggleStatus = [];
  var allItemHtml = [];
  var content;
  var tplHtml = _gel('tpl-trip-items-container').value;
  var NO_DATA =
      '<div style="font-size:12px;padding:5px 30px">' +
      prefs.getMsg('no_item_added') + '</div>';
  var day;
  for (var i = 0; i <= trip.duration; i++) {
    day = (i == 0) ? prefs.getMsg('unsch') : !Util.isEmpty(trip.sdate) ?
        DateLib.addDaysToDate(trip.sdate,
                              i - 1,
                              '{MM} {dd} ({W})') : ('Day ' + i);
    htmlContent[i] = [];
    tplData.push({
      index: i,
      day: day,
      content: '',
      itemNumber: 0
    });
  }
  // Add marker on map for all items.
  var itemsLength = gTripItemDB.length;
  for (i = 0; i < itemsLength; i++) {
    htmlContent[gTripItemDB[i].day].push(getHtmlForOneItem(gTripItemDB[i]));
    tplData[gTripItemDB[i].day]['itemNumber'] += 1;
    addBlueMarker(gTripItemDB[i]);
  }

  for (i = 0; i <= trip.duration; i++) {
    content = htmlContent[i].join('') || NO_DATA;
    tplData[i].content = content;
    tplData[i].itemNumber = tplData[i].itemNumber || '';
    if (content == NO_DATA) {
      toggleStatus[i] = 1;
      tplData[i].itemFlag = 1;
    } else {
      toggleStatus[i] = 0;
      tplData[i].itemFlag = 0;
    }
    allItemHtml.push(Util.supplant(tplHtml, tplData[i]));
  }

  _gel('unscheduled-items-box').innerHTML = allItemHtml[0];
  allItemHtml[0] = '';
  _gel('scheduled-items-box').innerHTML = allItemHtml.join('');

  // Minimize when "No data found" for the day.
  for (i = 0; i <= trip.duration; i++) {
    if (toggleStatus[i]) {
      toggleDisplay(i, toggleStatus[i]);
    }
  }
  initDrag();
  initInterDrag();
  // Setting minimum height in canvas as 1000px.
  _IG_AdjustIFrameHeight(1000);
}

/**
 * Function to fill data for an item in its template.
 * @param {Object} tripItem Trip item object.
 * @return {string} The required html element as a string.
 */
function getHtmlForOneItem(tripItem) {
  var tplHtml = _gel('tpl-unschedule-item').value;
  var imgSrc = BLANK_IMG;
  var className = 'blue-pushpin';
  if (tripItem.dataSource == 'lonely') {
    className = LONELY_IMG[tripItem.category].className;
  }
  var tplData = {
    dragItemCounter: tripItem.id,
    index: tripItem.id,
    name: tripItem.name,
    title: tripItem.name,
    contactInfo: tripItem.address,
    thumbUp: tripItem.Item_thumb_up,
    likeDisplay: tripItem.Item_thumb_up ? 'block' : 'none',
    imgSrc: imgSrc,
    className: className,
    html: ''
  };
  var categories = [
    prefs.getMsg('vote_up'),
    prefs.getMsg('vote_down'),
    prefs.getMsg('delete')
  ];
  var dropDownHtml =
      ['<table cellpadding="1" cellspacing="1" style="font-size:12px">'];
  for (var i = 0, length = categories.length; i < length; i++) {
    dropDownHtml.push(
        '<tr>' +
          '<td style="cursor:pointer;">' +
            '<nobr>' +
              '<a href="#" onclick="actionOnItemActivity(\'',
                  tripItem.id, '\',\'', categories[i], '\')">', categories[i],
              '</a>' +
            '</nobr>' +
          '</td>' +
        '</tr>');
  }
  dropDownHtml.push('</table>');
  tplData.html = dropDownHtml.join('');
  return Util.supplant(tplHtml, tplData);
}

/**
 * Method to show trips listing page.
 * @param {boolean} flag Flag to decide to hide or display element.
 * @param {boolean} isSearchedTrips Flag to decide if data is from home or
 *     canvas.
 * @param {boolean} isSearched Flag to decide if its listing page or search
       within listing page.
 */
function showListings(flag, isSearchedTrips, isSearched) {
  var friendsList = [];
  var tripListings = [];
  var trips;
  var tplHtml;
  for (var j = 0, length = friendsData.length - 1; j <= length; j++) {
    friendsList.push('\'' + _esc(friendsData[j]) + '\'');
  }
  friendsList = friendsList.join(',');
  var url = BASE_URL + '/getAllTrips?friendsList=' + friendsList +
      '&rand=' + Math.random();
  gadgets.io.makeRequest(url, function(response) {
    trips = gadgets.json.parse(response.data);
    if (trips.error != undefined) {
      _gel('main-container') = '<div class="no_trip">' +
          prefs.getMsg('server_error') + '&nbsp;<span class="link" onclick="' +
          'javascript:loadFriends();">' + prefs.getMsg('try_again') +
          '</span></div>';
      return;
    }
    // Checking trips length from response to either show trip listing or
    // create trip box.
    var element = _gel('main-container');
    if (!trips.length) {
      if (element) {
        var start = new GLatLng(40.7143, -95.712891);
        var mapControl = new GMap2(element);
        // Set the map at certain lat/long at zoom level 13.
        mapControl.setCenter(start, 13);
      }
      createTripBox();
      return;
    } else {
      element.innerHTML = LOADING_TPL;
      tplHtml = _gel('tpl-trip-listing').value;
      if (isSearchedTrips) {
        trips = isSearched ? searchedTrips : gCurrentTripsData.sortedTrips;
      } else {
        gCurrentTripsData.allTrips = [];
        gCurrentTripsData.sortedTrips = [];
      }
      tripListings = parseTripsList(trips, isSearchedTrips, true);
    }
    var tplData = {
      'tripListings': tripListings.join('') || _gel('tpl-no-trip-msg').value
    };
   element.innerHTML = Util.supplant(tplHtml, tplData);
    if (!flag) {
      var showAllTrips = _gel('show-all-trips');
      showAllTrips.style.display = '';
      showAllTrips.innerHTML = prefs.getMsg('view_all_trips');
    } else {
      _gel('show-all-trips').style.display = 'none';
      _gel('searchTripText').value = '';
    }
    // For IE issue.
    var userAgent = navigator.userAgent.toLowerCase();
    if (userAgent.indexOf('msie') != -1) {
      window.setTimeout(_IG_AdjustIFrameHeight, 0);
    } else {
      _IG_AdjustIFrameHeight();
    }
  });
}

/**
 * Function to add item by calling add dialog box method.
 * @param {number} index The index of item to be added.
 */
function addItem(index) {
  var title = gCurrentTripsData.arrSearchResults[index].name;
  // Limiting the length of title to 20 characters.
  title = (title.length > 21) ? title.substr(0, 21) + ' ...' : title;
  showAddItemDialog(title, index);
}

/**
 * It returns the html string for popup of an item.
 * @param {Object} objItem The object for which popup is shown.
 * @return {string} The html for an item as a string.
 */
function getPopupHtmlItem(objItem) {
  if (!objItem) {
    return;
  }
  var className = '';
  var imgUrl = MARKER_IMG;
  if (objItem.dataSource == 'lonely') {
    imgUrl = BLANK_IMG;
    className = LONELY_IMG[objItem.category].className;
  }
  var tplHtml = _gel('tpl-item-info').value;
  var tplData = {
    ownerName: objItem.item_owner,
    overviewUrl: objItem.link + '&dtab=0&oi=',
    detailUrl: objItem.link + '&dtab=1&oi=md_structdata',
    reviews: objItem.review,
    reviewsLabel: prefs.getMsg('review'),
    height: '84px',
    closeImage: '',
    dragMessage: '',
    photosUrl: objItem.link + '&dtab=5&oi=md_photos&sa=X',
    image: '',
    display: 'none',
    title: _unesc(objItem.name),
    marker: '',
    imgUrl: imgUrl,
    className: className,
    webUrl: objItem.weburl || '',
    thumbUp: objItem.Item_thumb_up || 0,
    thumbDown: objItem.Item_thumb_down || 0,
    reviewsUrl: objItem.link + '%3Dmd_reviews&dtab=2&oi=md_reviews&sa=X'
  };

  // Reviews url.
  var review = objItem.review;
  var pattern = '&#160;&#160;';

  // Check for empty review.
  if (!review || pattern == review) {
    tplData.reviews = '';
    tplData.reviewsLabel = '';
    tplData.height = '0';
  }

  if (objItem.imgurl) {
    tplData.image = '<img align="left" width="70" height="70" ' +
        'style="margin-right:10px;cursor:pointer;" src="' +
        objItem.imgurl + '" />';
    tplData.display = 'block';
  }

  var address1 = '';
  var newLineIndex = objItem.address.search(/<br\/>/);
  var zip = '';
  if (newLineIndex != -1) {
    zip = objItem.address.substr(newLineIndex);
    address1 = objItem.address.substr(0, newLineIndex);
  } else {
    address1 = objItem.address;
  }

  if (address1) {
    tplData.contactInfo = getContactInfo(address1, zip);
  } else {
    tplData.contactInfo = '';
  }
  return Util.supplant(tplHtml, tplData);
}

/**
 * Returns complete address for an item.
 * @param {string} address Address of an item.
 * @param {string} zipCode Zip code of an item.
 * @return {string} Complete address.
 */
function getContactInfo(address, zipCode) {
  // Maximum length for a trip name is 50.
  if (address.length > 50) {
    var sIndex = address.indexOf(' ', 49);
    if (sIndex != -1) {
      address = address.substr(0, sIndex) + '<br>';
      address += address.substr(sIndex + 1);
    }
  }
  if (zipCode) {
    address += zipCode;
  }
  return address;
}

/**
 * Function to add selected date to trip dates.
 * @param {number} index The index of item to be added.
 */
function addItemDate(index) {
  var trip = getTripById(gCurrentTripsData.currentTripId);
  var isSchedule = _gel('dateradio') ? _gel('dateradio').checked : false;
  var daySelected = 0;
  var currentDate;
  var date1, date2;
  var ONE_DAY = 1000 * 60 * 60 * 24;
  var wrngMsg = _gel('warning-msg');
  if (isSchedule) {
    var selectedDateElement = _gel('selecteddate');
    if (selectedDateElement) {
      date2 = new Date(trippyCalendar.currentDate);
      date1 = new Date(DateLib.formatDate(trip.sdate));
      if (!checkDateValidity(trip, date2)) {
        return;
      }
      daySelected =
          Math.ceil((date2.getTime() - date1.getTime()) / ONE_DAY) + 1;
    } else if (_gel('days_id')) {
      daySelected = _gel('days_id').selectedIndex + 1;
    } else if (_gel('startDate')) {
      date2 = new Date(_gel('startDate').value);
      date1 = new Date(trip.sdate);
      if (!checkDateValidity(trip, date2)) {
        return;
      }
      daySelected =
        Math.ceil((date2.getTime() - date1.getTime()) / ONE_DAY) + 1;
    } else if (Util.isEmpty(currentDate)) {
      wrngMsg.style.visibility = 'visible';
      wrngMsg.innerHTML = prefs.getMsg('select_date');
      return;
    }
  } else {
    daySelected = 0;
  }
  // Adding item to  itinerary .
  addToItinerary(index, daySelected);
  hideDialog();
  // To hide info window, if there are any opened.
  gMap.closeInfoWindow();
}

/**
 * Checks the validity of trip dates.
 * @param {Object} trip Trip object.
 * @param {string} otherDate Date with which trip dates are to be validated.
 * @return {boolean} True or false according to validation result.
 */
function checkDateValidity(trip, otherDate) {
  var startDay, endDay;
  var currentDate = (otherDate.getMonth() + 1) + '-' + otherDate.getDate() +
      '-' + otherDate.getFullYear();
  startDay = DateLib.getDateDiff(trip.sdate, currentDate);
  endDay = DateLib.getDateDiff(currentDate, trip.fdate);
  if (startDay < 0 || endDay < 0) {
    var wrngMsg = _gel('warning-msg');
    wrngMsg.style.visibility = 'visible';
    wrngMsg.innerHTML = prefs.getMsg('date_outof_range');
    return false;
  }
  return true;
}

/**
 * Display category list drop down.
 * @param {string} id Id of an item.
 */
function categoryList(id) {
  var dropDown = _gel('dropdown-location-' + id);
  var dropDownStyle = dropDown.style.display;

  if (id == lastOpenedItem && dropDownStyle == '') {
    dropDown.style.display = 'none';
    return;
  }
  var lastSelectedElement = _gel('dropdown-location-' + lastOpenedItem);
  if (lastSelectedElement) {
    lastSelectedElement.style.display = 'none';
  }
  lastOpenedItem = id;
  dropDown.style.display = '';
}

/**
 * It updates name of current trip in to db.
 */
function updateTripName() {
  // Update trip name.
  var tripName = _trim(_gel('tripNameEdit').value);
  if (!tripName) {
    return;
  }
  hideDialog();
  var params = {};
  params[gadgets.io.RequestParameters.METHOD] = gadgets.io.MethodType.POST;
  var postData = gadgets.io.encodeValues({
    'trip_id': gCurrentTripsData.currentTripId,
    'trip_name': tripName
  });
  params[gadgets.io.RequestParameters.POST_DATA] = postData;
  var url = BASE_URL + '/updateTripName?rand=' + Math.random();
  var tplData;
  gadgets.io.makeRequest(url, function(response) {
    var responseData = gadgets.json.parse(response.data);
    if (responseData.error == transResponse.ERROR) {
      tplData = {
        message: prefs.getMsg('tripname_update_err')
      };
      showServerMessage(tplData);
    } else {
      tplData = {
        message: prefs.getMsg('updated_name') + ' ' + tripName
      };
      showServerMessage(tplData);
      var objTrip = getTripById(gCurrentTripsData.currentTripId);
      objTrip.name = tripName;
      tripDB[objTrip.id] = objTrip;
      _gel('trip-name').innerHTML = tripName;
    }
  }, params);
}

/**
 * It returns the html string for popup of an item.
 * @param {number} index Index of item.
 * @param {boolean} flag The flag to decide which template to use.
 * @return {string} The HTML for an item as a string.
 */
function getPopupHtml(index, flag) {
  var icon = String.fromCharCode('A'.charCodeAt(0) + index);
  selectedItemId = 'resultPopup' + index;
  var tplHtml = _gel(flag ? 'tpl-item-info-drag' : 'tpl-item-info').value;
  var itemData = gCurrentTripsData.arrSearchResults[index];
  var tplData = {
    index: index,
    overviewUrl: itemData.link + '&dtab=0&oi=',
    detailUrl: itemData.link + '&dtab=1&oi=md_structdata',
    reviews: itemData.review,
    reviewsLabel: prefs.getMsg('review'),
    height: '84px',
    closeImage: '',
    dragMessage: prefs.getMsg('dragme'),
    photosUrl: itemData.link + '&dtab=3&oi=md_photos',
    image: '',
    display: 'none',
    title: _unesc(itemData.name),
    marker: icon,
    webUrl: itemData.weburl || ''
  };
  // Reviews url
  var review = '';
  review = itemData.review || '';
  var pattern = '&#160;&#160;';
  // Check for empty review.
  if (!review || pattern == review) {
    tplData.reviews = '';
    tplData.reviewsLabel = '';
    tplData.height = '0';
  }
  var image = itemData.imgurl;
  if (image) {
    tplData.image = '<img align="left" width="60" height="60" ' +
        'style="margin-right:10px;cursor:pointer;" src="' + image + '" />';
    tplData.display = 'block';
  }
  tplData.contactInfo = itemData.address ||
      '<a href="#" onclick="getMarkerInfoToDisplay(' + index +
      ')">' + prefs.getMsg('see_reviews') + '</a>';
  tplData.lonelyMore = itemData.lonelyLink || '';
  return Util.supplant(tplHtml, tplData);
}

/**
 * Displays the info when clicked on the marker.
 */
function showMarkerInfo() {
  var currMarker = currentMarker.marker;
  if (!currMarker || currentMarker.index == -1) {
    return;
  }
  var html = getPopupHtml(currentMarker.index, true, false);
  if (gCurrentTripsData.arrSearchResults[currentMarker.index].
      dataSource == 'lonely') {
    currMarker.openInfoWindowHtml(LOADING_TPL, {maxWidth: '400'});
    html = html.replace(currentMarker.icon, '')
        .replace('<img src=""/>', '');
  }
  currMarker.openInfoWindowHtml(html, {maxWidth: '400'});
}

/**
 * Get marker info to display.
 * @param {number} index Index of item to be shown on marker.
 */
function getMarkerInfoToDisplay(index) {
  currentMarker = {
    marker: gCurrentTripsData.searchMarkers[index],
    index: index,
    icon: gCurrentTripsData.searchMarkerIcons[index]
  };
  showMarkerInfo();
}

/**
 * Create a new item in a trip.
 */
function createNewItem() {
  var address = _gel('item-description').value;
  var itemName = _gel('item-name').value;
  var wrngMsg = _gel('warning-msg');
  // To check whether mandatory field are filled or not.
  if (!address || !itemName) {
    wrngMsg.style.visibility = 'visible';
    wrngMsg.innerHTML = prefs.getMsg('name_addr_empty');
    return;
  }
  var trip = getTripById(gCurrentTripsData.currentTripId);
  if (geocoder) {
    geocoder.getLocations(address, function(result) {
      if (result.Status.code == RESPONSE_SUCCESS && result.Placemark.length) {
        var place = result.Placemark[0];
        var point = place.Point.coordinates;
        var tripItem = getItemObject();
        tripItem.ownerName = gViewer;
        tripItem.name = itemName;
        tripItem.address = address ? Util.stripHtml(address) : '';
        tripItem.lat = point[1];
        tripItem.lng = point[0];
        tripItem.isCustom = true;
        tripItem.dataSource = 'custom';
        tripItem.tripName = trip.name;
        var dateElement = _gel('dateradio');
        var isSchedule = dateElement.checked;
        if (!isSchedule) {
          tripItem.day = 0;
        } else if (!Util.isEmpty(trip.sdate)) {
          if (trip.sdate.indexOf('-') > -1) {
            trip.sdate = DateLib.formatDate(trip.sdate);
          }
          var daysId = _gel('days_id');
          tripItem.sdate = tripItem.fdate = daysId[daysId.selectedIndex].value;
          tripItem.day =
              DateLib.getDateDiff(trip.sdate, tripItem.sdate) + 1;
        } else {
          var daysId = _gel('days_id');
          tripItem.day = daysId[daysId.selectedIndex].value || 0;
        }
        saveItem(tripItem);
        hideViewDateDialogBox();
      } else {
        wrngMsg.style.visibility = 'visible';
        wrngMsg.innerHTML = prefs.getMsg('loc_not_found');
      }
    });
  }
}

/**
 * Returns trip dates and duration if trip is scheduled else duration only.
 * @param {Object} trip Trip object.
 * @return {string} Returns trip dates.
 */
function getTripDates(trip) {
  var tripDateString = '';
  if (!Util.isEmpty(trip.sdate) && !Util.isEmpty(trip.fdate)) {
    tripDateString = DateLib.formatDate(trip.sdate, '{MM} {d}') + ' - ' +
        DateLib.formatDate(trip.fdate, '{MM} {d}, {Y}') +
        ' / ' + trip.duration + ' days';
  } else {
    tripDateString = trip.duration + ' days';
  }
  return tripDateString;
}

/**
 * Returns item address if avaiable else its review.
 * @param {Object} tripItem Trip item object.
 * @return {string} Returns address or review.
 */
function getItemAddressHtml(tripItem) {
  var tplData = {
    itemName: _unesc(tripItem.name),
    address: tripItem.address ? tripItem.address : _unesc(tripItem.review)
  };
  var tplHtml = _gel('tpl-address-header').value;
  return Util.supplant(tplHtml, tplData);
}

/**
 * Method to initialize global variables on gadget reload.
 */
function initializeTripData() {
  timerMsg = new _IG_MiniMessage(null, _gel('server-msg'));
  geocoder = new GClientGeocoder();
  gOpenSocial = [];
  gTripItemDB = [];
  tripDB = {};
  gCurrentTripsData = {
    allTrips: {},
    sortedTrips: [],
    itemMarkers: {}
  };
  loadFriends();
  _IG_AdjustIFrameHeight();
}

/**
 * Call back for list of friends.
 * @param {Object} response Response object.
 */
function handleViewRequest(response) {
  if (!gOpenSocial.viewer) {
    gOpenSocial.viewer = response.get('viewer').getData();
    gViewer = gOpenSocial.viewer.getDisplayName();
  }
}

/**
 * Callback method for fetching list of friends.
 * @param {Object} response Response object.
 */
function handleResponse(response) {
  handleGroupRequest(response);
}

/**
 * Callback method for fetching list of friends in the container.
 * @param {Object} response Response object.
 */
function handleGroupRequest(response) {
  friendsData = [];
  if (!response.hadError()) {
    var viewer = response.get('viewer').getData();
    friendsData.push(viewer.getDisplayName());
    gViewer = viewer.getDisplayName();
    var viewerFriends = response.get('groupPeople').getData();
    if (viewerFriends) {
      viewerFriends.each(function(person) {
        friendsData.push(person.getDisplayName());
      });
    }
    // Check to decide whether we have to move to trip page or trips listing.
    if (!isGetData) {
      showListings(true, false, false);
    } else {
      isGetData = false;
      fetchTripsData();
    }
  }
}

/**
 * Method to fetch trips data and fill the array.
 */
function fetchTripsData() {
  // Global trip array.
  tripDB = {};
  var friendsList = [];
  // Creating trip object and filling it.
  var trip = getTripObject();
  var id = gParams['tripId'];
  trip.id = id;
  trip.name = gParams['tripName'];
  trip.ownerId = gParams['ownerId'];
  trip.ownerName = gParams['ownerName'];
  trip.loc = gParams['tripLocation'];
  trip.lat = gParams['lat'];
  trip.lng = gParams['lng'];
  trip.accuracy = gParams['accuracy'];
  trip.duration = gParams['duration'];
  trip.isListing = gParams['isListing'];

  var startDate = gParams['sdate'];
  if (!Util.isEmpty(startDate)) {
    trip.sdate = DateLib.formatDate(startDate, '{m}/{d}/{Y}');
  }
  var endDate = gParams['sdate'];
  if (!Util.isEmpty(endDate)) {
    trip.fdate = DateLib.formatDate(endDate, '{m}/{d}/{Y}');
  }

  var trips;
  for (var j = 0, length = friendsData.length - 1; j <= length; j++) {
    friendsList.push('\'' + _esc(friendsData[j]) + '\'');
  }
  friendsList = friendsList.join(',');
  var url = BASE_URL + '/getAllTrips?friendsList=' + friendsList +
      '&rand=' + Math.random();
  gadgets.io.makeRequest(url, function(response) {
    var responseData = gadgets.json.parse(response.data);
    if (!responseData.length) {
      showListings(true, false, false);
    } else if (responseData[0].error != transResponse.SUCCESS) {
      trips = gadgets.json.parse(response.data);
      parseTripsList(trips, false, false);
      if (id) {
        exploreTrip(id);
        return;
      } else {
        showListings(true, false, false);
      }
      var gulliverData = JGulliverData.getInstance();
      gulliverData.arrTripData.push(trip);
      gulliverData.nSelectedTripIndex = gulliverData.arrTripData.length - 1;
    }
  });
}

/**
 * Navigate to details page for a trip.
 * @param {string} tripId The trip id.
 */
function exploreTrip(tripId) {
  var trip = getTripById(tripId);
  if (gCurrentTripsData.currentTripId != tripId) {
    // Resetting trip votes count while moving from canvas to trip list page.
    trip_thumb_up = trip_thumb_down = 0;
  }
  gCurrentTripsData.currentTripId = tripId;
  var tplHtml = _gel('tpl-canvas').value;
  var tplData = {
    tripOwnerName: trip.ownerName,
    tripThumbUp: trip.thumb_up || 0,
    tripThumbDown: trip.thumb_down || 0,
    tripNameTitle: trip.name
  };
  _gel('main-container').innerHTML = Util.supplant(tplHtml, tplData);
  var start = new GLatLng(trip.lat, trip.lng);
  // Global GMap2 object.
  gMap = new GMap2(_gel('map-box-area'));
  gMap.setCenter(start, tripAccuracy[trip.accuracy]);
  gMap.addControl(new GSmallMapControl());
  gMap.panTo(start);

  updateTripDateDisplay(trip.sdate, trip.fdate, trip.duration, 'duration');
  fetchAllItems();
  _IG_AdjustIFrameHeight();
}

/**
 * Fetch all items related to current trip.
 */
function fetchAllItems() {
  gTripItemDB = [];
  var url = BASE_URL + '/getAllTripItems?trip_id=' +
      gCurrentTripsData.currentTripId + '&rand=' + Math.random();
  gadgets.io.makeRequest(url, function(response) {
    var tripItems = gadgets.json.parse(response.data);
    var tripItemsLength = tripItems.length;
    for (var i = 0; i < tripItemsLength; i++) {
      var objItem = getItemObject();
      objItem.id = tripItems[i].item_id;
      objItem.name = _unesc(tripItems[i].name);
      objItem.item_owner = _unesc(tripItems[i].item_owner);
      objItem.address = _unesc(tripItems[i].location);
      objItem.lat = tripItems[i].lat;
      objItem.lng = tripItems[i].lang;
      objItem.review = _unesc(tripItems[i].review);
      objItem.sdate = tripItems[i].sdate;
      objItem.fdate = tripItems[i].fdate;
      objItem.duration = tripItems[i].duration;
      objItem.category = tripItems[i].category;
      objItem.day = tripItems[i].day;
      objItem.link = tripItems[i].link;
      objItem.weburl = tripItems[i].weburl;
      objItem.imgurl = tripItems[i].imgurl;
      objItem.dataSource = tripItems[i].dataSource;
      objItem.isCustom = tripItems[i].custom;
      if (tripItems[i].Item_thumb_up) {
        objItem.Item_thumb_up = tripItems[i].Item_thumb_up;
      }
      if (tripItems[i].Item_thumb_down) {
        objItem.Item_thumb_down = tripItems[i].Item_thumb_down;
      }
      gTripItemDB.push(objItem);
    }
    putItemsData();
  });
}

/**
 * Creates the trip and saves it in the database.
 * @param {boolean} isNotFromTripBox Check whether input is from trip box or
 *     search box.
 */
function createTrip(isNotFromTripBox) {
  var locationRef = isNotFromTripBox ?
      _gel('searchTripText') : _gel('create-location');
  var location = locationRef.value;
  var msgElement = isNotFromTripBox ? _gel('server-msg') : _gel('warning-msg');
  if (!location) {
    locationRef.focus();
    return;
  }
  if (Util.isNumeric(location)) {
    locationRef.value = '';
    locationRef.focus();
    msgElement.style.display = 'block';
    msgElement.innerHTML = prefs.getMsg('numeric_err');
    _IG_AdjustIFrameHeight();
    return;
  }
  var req = opensocial.newDataRequest();
  req.add(req.newFetchPersonRequest('VIEWER'), 'viewer');
  req.send(function(response) {
    var ownerId = response.get('viewer').getData().getId();
    var ownerName = response.get('viewer').getData().getDisplayName();
    gViewer = ownerName;
    //gOwnerId = ownerId;
    var params = {currentViewer: ownerName};
    // Creates an instance of trip object.
    var objTrip = getTripObject();
    if (geocoder) {
      geocoder.getLocations(location, function(result) {
        // If location of created trip is found.
        if (result.Status.code == RESPONSE_SUCCESS) {
          _gel('server-msg').style.display = 'none';
          if (result.Placemark.length) {
            var place = result.Placemark[0];
            var accuracy = place.AddressDetails.Accuracy;
            var point = result.Placemark[0].Point.coordinates;
            var params = {};
            params[gadgets.io.RequestParameters.METHOD] =
                gadgets.io.MethodType.POST;
            var postData = gadgets.io.encodeValues({
              'owner_id': ownerId,
              'owner_name': ownerName,
              'trip_name': location + ' ' + prefs.getMsg('trip'),
              'location': location,
              'lat': point[1],
              'lang': point[0],
              'accuracy': accuracy,
              'duration': objTrip.duration
            });
            params[gadgets.io.RequestParameters.POST_DATA] = postData;
            var url = BASE_URL + '/saveTrip?rand=' + Math.random();
            var tplData;
            gadgets.io.makeRequest(url, function(response) {
              var responseData = gadgets.json.parse(response.data);
              if (responseData[0].error == transResponse.ERROR) {
                tplData = {
                  message: Util.supplant(prefs.getMsg('trip_create_err'),
                                         {name: location})
                };
                showServerMessage(tplData);
                return;
              }
              tplData = {message: prefs.getMsg('trip_created')};
              showServerMessage(tplData);
              objTrip.id = responseData[0].tripId;
              objTrip.name = location + ' trip';
              objTrip.ownerName = ownerName;
              objTrip.loc = location;
              objTrip.lat = point[1];
              objTrip.lng = point[0];
              objTrip.accuracy = accuracy;
              hideDialog();
              tripDB[objTrip.id] = objTrip;
              exploreTrip(objTrip.id);
            }, params);
          }
        } else {
          msgElement.innerHTML = prefs.getMsg('loc_not_found');
          msgElement.style.display = 'block';
        }
      });
    } else {
      _gel('server-msg').innerHTML = prefs.getMsg('map_not_loaded');
    }
  });
}

/**
 * First method to be called in canvas view on page load.
 */
function init() {
  document.onclick = function() {
    _gel('tool-tip').style.display = 'none';
  };
  requestData();
}

function handleOpensocial(){
  initializeTripData();
}

/**
 * Method to show markers on the map.
 * @param {Object} objItem The object for which popup is shown.
 */
function addBlueMarker(objItem) {
  var blueIcon = new GIcon(G_DEFAULT_ICON);
  blueIcon.shadow = SHADOW_IMG;
  blueIcon.shadowSize = new GSize(37, 34);
  blueIcon.iconAnchor = new GPoint(9, 34);
  blueIcon.infoWindowAnchor = new GPoint(9, 2);
  blueIcon.image =
      'http://maps.google.com/mapfiles/ms/icons/blue-pushpin.png';

  var point = new GLatLng(objItem.lat, objItem.lng);
  var marker = new GMarker(point, {icon: blueIcon, autoPan: false});

  if (gCurrentTripsData.itemMarkers[objItem.id]) {
    gMap.removeOverlay(gCurrentTripsData.itemMarkers[objItem.id]);
  }
  gMap.addOverlay(marker);
  gCurrentTripsData.itemMarkers[objItem.id] = marker;

  GEvent.addListener(marker, 'mouseover', function(latlng) {
    showToolTip(latlng, objItem.name);
  });

  GEvent.addListener(marker, 'click', function(latlng) {
    var html = getPopupHtmlItem(objItem);
    marker.openInfoWindowHtml(html);
  });
}

// Entry point.
_IG_RegisterOnloadHandler(init);

// Exports
window.createCustomItemDialog = createCustomItemDialog;
window.createNewItem = createNewItem;
window.addItemDate = addItemDate;
window.updateTripName = updateTripName;
window.showItemMarker = showItemMarker;
window.categoryList = categoryList;
window.searchTrips = searchTrips;
window.validateUpdatedDuration = validateUpdatedDuration;
window.getMarkerInfoToDisplay = getMarkerInfoToDisplay;
