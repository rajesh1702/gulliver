/**
 * @fileoverview Code to handle home view of Gulliver Gadget.
 * @author gadgetfactory@google.com (gadgetfactory)
 */


/**
 * Used for creating, initializing and sending the viewer data.
 */
function requestData() {
  if (!isServiceAvailableForUser()) {
    _gel('homeContent').innerHTML = _gel('service_not_available').innerHTML;
  } else {
    var req = opensocial.newDataRequest();
    req.add(req.newFetchPersonRequest('VIEWER'), 'viewer');
    req.add(req.newFetchPeopleRequest(opensocial.newIdSpec({
                                      'userId': 'VIEWER',
                                      'groupId': 'FRIENDS'
                                     })), 'groupPeople');
    req.send(callbackAllTrips);
  }
}

/**
 * To check whether opensocial feature in user container is available
 * or not.
 * @return {boolean} Returns true if container supports opensocial feature
 *     and views else returns false.
 */
function isServiceAvailableForUser() {
  try {
    return gadgets.util.hasFeature('opensocial-0.8') &&
           gadgets.views.getSupportedViews().canvas &&
           _args().st;
  } catch (ex) {
    return false;
  }
}

/**
 * Callback function for all trips data of the user.
 * @param {Object} response Response of opensocial request.
 */
function callbackAllTrips(response) {
  var data = [];
  if (!gOpenSocial.viewer) {
    gOpenSocial.viewer = response.get('viewer').getData();
    var displayName = gOpenSocial.viewer.getDisplayName();
    gViewer = displayName;
    data.push(gViewer);
  }
  gOpenSocial.viewerFriends = response.get('groupPeople').getData();
  if (gOpenSocial.viewerFriends) {
    gOpenSocial.viewerFriends.each(function(person) {
      data.push(person.getDisplayName());
    });
  }
  if (response.hadError()) {
    switch (opensocial.ResponseItem.getErrorCode) {
      case opensocial.ResponseItem.Error.UNAUTHORIZED :
        _gel('homeContent').innerHTML = _gel('user_not_authorized').innerHTML;
        break;
      default:
        _gel('homeContent').innerHTML = _gel('user_not_authorized').innerHTML;
        break;
    }
    _IG_AdjustIFrameHeight();
  } else if (gOpenSocial.viewer.getId() == -1) {
    _gel('homeContent').innerHTML = _gel('user_not_signed_in').innerHTML;
  } else {
    var friendsList = [];
    for (var j = 0, length = data.length - 1; j <= length; j++) {
      friendsList.push('\'' + _esc(data[j]) + '\'');
    }
    friendsList = friendsList.join(',');
    url = BASE_URL + '/getAllTrips?friendsList=' + friendsList +
        '&rand=' + Math.random();
    gadgets.io.makeRequest(url, initializeData);
 }
}

/**
 * Initializes the trip data.
 * @param {object} response Response data.
 */
function initializeData(response) {
  var owner_trips = gadgets.json.parse(response.data);
  if (owner_trips != '') {
    gTripData = owner_trips;
    showTrips();
  } else {
    gTripData = owner_trips;
    callbackCreate();
  }
}

/**
 * Used for displaying the trips of the owner.
 */
function showTrips() {
  _gel('server_msg').style.display = 'none';
  var html = [];
  if (gTripData.length > 0) {
    html.push('<table border="0" cellpadding="0" cellspacing="0" width="100%">',
              '<tr class="head">',
              '<td valign="top" width="32%">' + prefs.getMsg('trip_name') +
              '</td><td valign="top" width="32%">' + prefs.getMsg('date') +
              '</td><td valign="top" width="36%">' + prefs.getMsg('location') +
              '</td></tr></table>');
    var text;
    var tripDataLen = gTripData.length;
    for (i = 0; i < 5 && i < tripDataLen; i++) {
      text = getDetailsSection(i);
      html.push(text);
    }
    html.push('<table border="0" cellpadding="0" cellspacing="0" ',
              ' style="padding-top:5px;" width="100%"><tr>',
              '<td align="left" width="30%">',
              '<input type=button ',
              'onClick="callbackCreate();" value="' +
              prefs.getMsg('create_trip') + '"></td>',
              '<td align="center" width="30%">',
              '<a href="mailto:developer.gadget@gmail.com" ' ,
              'class="links">' + prefs.getMsg('feedback') + '</a></td>',
              '<td align="right" width="30%">',
              '<span id="viewAll" class="links" ',
              'onClick="goToCanvas();">' +
              prefs.getMsg('view_all_trips') +
              '</span></td></tr></table>');
  } else {
    html.push('<table border="0" cellpadding="0" cellspacing="0" ',
              'width="100%"><tr>',
              '<td align="center" class="hint">' + prefs.getMsg('no_trips') +
              '&nbsp;&nbsp;<a href="#" ',
              ' onClick="callbackCreate;">' + prefs.getMsg('createTrip') +
              '</a></td></tr>',
              '<tr><td align="right" width="30%" colspan="2">',
              '<a href="mailto:developer.gadget@gmail.com" ' ,
              'class="links">' + prefs.getMsg('feedback') + '</a></td>',
              '</tr></table>');
  }
  _gel('homeContent').innerHTML = html.join('');
  _IG_AdjustIFrameHeight();
}

/**
 * Used to get the html of the details of the expanded trip.
 * @param {string} id Id of the current trip.
 * @return {string} html Details of the expanded trip.
 */
function getDetailsSection(id) {
  var html = [];
  var nameMaxLength = 16;

  var tripName = gTripData[id].trip_name;
  var tripID = gTripData[id].trip_id;
  if (tripName.length > nameMaxLength) {
    tripName = tripName.substring(0, nameMaxLength);
    tripName += '...';
  }
  var sDate = gTripData[id].sdate;
  if (!sDate || sDate == 'None' || sDate == undefined)
    sDate = '';
  else
    sDate = formatDate(sDate);
  var eDate = gTripData[id].fdate;
  if (!eDate || eDate == 'None' || eDate == undefined)
    eDate = '';
  else
    eDate = formatDate(eDate);

  html.push('<div id="trip', id, '" style="padding-top:5px;',
            ' border-bottom:1px #ccc dotted;">',
            '<table border="0" cellpadding="0" cellspacing="0" width="100%">',
            '</td><td valign="top" width="32%" class="subhead" >',
            '<a href=',
            '"javascript:showTripDetails(', id, ');" ',
            'style="text-decoration:none;" title=" ' ,
            gTripData[id].trip_name, '">', tripName, '&nbsp;</a></td>',
            '<td valign="top" width="32%" class="subhead2" >', sDate,
            ' - ', eDate, '</td><td valign="top" width="36%" class="subhead2">',
            gTripData[id].location, '</td>',
            '</tr></table></div>');
  return (html.join(''));
}

/**
 * Converts the date in format MON DD, YYYY.
 * @param {string} dateStr Date to be converted.
 * @return {string} Converted date.
 */
function formatDate(dateStr) {
  if (dateStr != undefined) {
    var monthArray = [
        'Jan',
        'Feb',
        'Mar',
        'Apr',
        'May',
        'Jun',
        'Jul',
        'Aug',
        'Sep',
        'Oct',
        'Nov',
        'Dec'];
    dateStr = dateStr.split('-');
    var month = dateStr[1] - 1;
    var finaldate = monthArray[month] + ' ' + dateStr[2];
    return finaldate;
  } else {
    return '';
  }
}

/**
 * Display the trip details.
 * @param {number} nIndex Index of the current trip.
 */
function showTripDetails(nIndex) {
  ownerName = gTripData[nIndex].owner_name;
  tripName = gTripData[nIndex].trip_name;
  tripId = gTripData[nIndex].trip_id;
  tripLocation = gTripData[nIndex].location;
  lat = gTripData[nIndex].lat;
  lang = gTripData[nIndex].lang;
  accuracy = gTripData[nIndex].accuracy;
  duration = gTripData[nIndex].duration;
  gStartDate = gTripData[nIndex].sdate;
  gEndDate = gTripData[nIndex].fdate;
  gRating = gTripData[nIndex].rating;
  fromTripHome = true;
  goToCanvas();
}

/**
 * Registers keyup event.
 * @param {string} strId Element id.
 */
function addKeyListener(strId) {
  if (window.addEventListener) {
    _gel(strId).addEventListener('keyup', handleKeyEvent, false);
  } else {
    _gel(strId).attachEvent('onkeyup', handleKeyEvent);
  }
}

/**
 * Handles key event.
 * @param {object} event Keyup event.
 */
function handleKeyEvent(event) {
  if (!event) event = window.event;
  switch (event.keyCode) {
    case 13:
      var id =
          event['target'] ? event['target']['id'] : event['srcElement']['id'];
      if (id == 'location') {
        validateTrip();
      }
      break;
    default:
      break;
  }
}

/**
 * Initializes the params and sends the control to canvas view.
 */
function goToCanvas() {
  var params = {
    tripId: tripId,
    tripName: tripName,
    tripLocation: tripLocation,
    ownerId: ownerId,
    ownerName: ownerName,
    lat: lat,
    lng: lang,
    accuracy: accuracy,
    duration: duration,
    sdate: gStartDate,
    fdate: gEndDate,
    rating: gRating,
    currentViewer: gViewer
  };
  _gel('homeContent').innerHTML = '';
  gadgets.views.requestNavigateTo(new gadgets.views.View('canvas'),
      params);
}

/**
 * Check for the response and handles the error.
 * @param {object} response Server response.
 */
function checkResponseTrip(response) {
  var responseData = gadgets.json.parse(response.data);
  var serverMsgRef = _gel('server_msg');
  if (responseData[0].error != ERROR_VALUE &&
      responseData[0].error != 'undefined') {
    serverMsgRef.style.display = 'none';
    tripId = responseData[0].tripId;
    gRating = DEFAULT_RATING;
    goToCanvas();
  } else {
    serverMsgRef.style.display = 'block';
    serverMsgRef.innerHTML = '<b>' + prefs.getMsg('server_error') +
                             '<b>&nbsp;<a href="javascript:void(0);" ' +
                             'onclick="createTrip(\'' +
                             tripName + '\');">' +
                             prefs.getMsg('try_again') + '</a>';
    _IG_AdjustIFrameHeight();
  }
}

/**
 * Converts the date in a particular format.
 * @param {string} d Date to be formatted.
 * @return {string} Formatted date.
 */
function convertFormat(d) {
  var mdate = new Date(d);
  var mon = mdate.getMonth() + 1;
  if (mon < 10)
    mon = '0' + mon;
  var year = mdate.getFullYear();
  day = mdate.getDate();
  if (day < 10) {
    day = '0' + day;
  }
  finaldate = mon + '/' + day + '/' + year;
  return finaldate;
}

/**
 * Creates the trip and saves it in the database.
 * @param {string} tName Trip name.
 */
function createTrip(tName) {
  var count = 0;
  var nameLength = tName.length;
  var tripDataLength = gTripData.length;
  for (var i = 0; i < tripDataLength; i++) {
    tripName = gTripData[i].trip_name;
    if (tName.toLowerCase() == tripName.substr(0, nameLength).toLowerCase()) {
      count++;
    }
  }

  tripName = tName + ' ' + prefs.getMsg('trip');
  if (count != 0) {
    tripName = tripName + count;
  }

  var req = opensocial.newDataRequest();
  req.add(req.newFetchPersonRequest('VIEWER'), 'viewer');
  req.send(function(response) {
    ownerId = response.get('viewer').getData().getId();
    ownerName = response.get('viewer').getData().getDisplayName();
    gViewer = ownerName;
    var params = {currentViewer: ownerName};

    params[gadgets.io.RequestParameters.METHOD] = gadgets.io.MethodType.POST;
    post_data = gadgets.io.encodeValues({
      'owner_id': ownerId,
      'owner_name': ownerName,
      'trip_name': tripName,
      'location' : tripLocation,
      'lat' : lat,
      'lang' : lang,
      'accuracy' : accuracy,
      'duration': duration,
      'rating': DEFAULT_RATING
    });
    params[gadgets.io.RequestParameters.POST_DATA] = post_data;
    var url = BASE_URL + '/saveTrip?rand=' + Math.random();
    gadgets.io.makeRequest(url, checkResponseTrip, params);
  });
  _IG_AdjustIFrameHeight();
}

/**
 * Checks whether the location is valid and if valid,
 * sends the control to canvas view.
 * @param {string} address Location entered by user.
 */
function showAddress(address) {
  if (geocoder) {
    var locationRef = _gel('location');
    var serverMsgRef = _gel('server_msg');
    geocoder.getLocations(address, function(result) {
      if (result.Status.code == 200) {
        if (result.Placemark.length > 0) {
          var place = result.Placemark[0];
          accuracy = place.AddressDetails.Accuracy;
          accuracy = accuracy;
          var p = result.Placemark[0].Point.coordinates;
          lat = p[1];
          lang = p[0];
          tripName = address;
          tripLocation = address;
          duration = DEFAULT_DURATION;
          locationRef.className = 'txtBox';
          serverMsgRef.style.display = 'none';
          createTrip(tripName);
        }
      } else {
        locationRef.value = '';
        locationRef.focus();
        serverMsgRef.style.display = 'block';
        serverMsgRef.innerHTML = prefs.getMsg('invalid_loc');
        _IG_AdjustIFrameHeight();
        return;
      }
    });
  }
}

/**
 * Check for numeric string.
 * @param {string} strString String to be validated.
 * @return {boolean} Returns false if no invalid characters are found.
 */
function isNumeric(strString) {
  var strInvalidChars = '0123456789';
  var strChar;
  var strStringLength = strString.length;
  for (i = 0; i < strStringLength; i++) {
    strChar = strString.charAt(i);
    if (strInvalidChars.indexOf(strChar) != -1) {
      return true;
    }
  }
  return false;
}

/**
 * Validates the required fields.
 */
function validateTrip() {
  var locationRef = _gel('location');
  var serverMsgRef = _gel('server_msg');
  if (_gel('location').value != '') {
    var tripLoc = _gel('location').value;
    var bInvalid = isNumeric(tripLoc);
    if (!bInvalid) {
      showAddress(tripLoc);
    } else {
      locationRef.value = '';
      locationRef.focus();
      serverMsgRef.style.display = 'block';
      serverMsgRef.innerHTML = prefs.getMsg('numeric_err');
      _IG_AdjustIFrameHeight();
    }
  } else {
    locationRef.focus();
  }
}

/**
 * Callback function for Create Trip tab.
 */
function callbackCreate() {
  var html = ['<table width="100%"><tr><td colspan="2">',
              '<font class="heading">' + prefs.getMsg('where_visit') +
              '</font></td></tr><tr><td width="90%">',
              '<input type="text" id="location" class="txtBox" />',
              '<font class="hint" style="color:#666;">' +
              prefs.getMsg('egLocation') +
              '</font></td><td width="30%" valign="top">',
              '<input type="button" value="' + prefs.getMsg('create') +
              '" onClick="validateTrip();"/>',
              '</td></tr><tr><td align="left" width="30%">',
              '<a href="mailto:developer.gadget@gmail.com" ' ,
              'target="_blank" class="links">' + prefs.getMsg('feedback') +
              '</a></td>'];
  if (gTripData.length > 0) {
    html.push('<td align="right" width="*">',
              '<span id="viewAll" class="links" ',
              'onClick="goToCanvas();">' +
              prefs.getMsg('view_all_trips') + '</span></td>');
  }
  html.push('</tr></table>');

  _gel('homeContent').innerHTML = html.join('');
  addKeyListener('location');
  _IG_AdjustIFrameHeight();
}

/**
 * Draw the tabs dynamically.
 */
function init() {
  if (GBrowserIsCompatible()) {
    geocoder = new GClientGeocoder();
  }
  requestData();
  _IG_AdjustIFrameHeight();
}

// Entry point.
_IG_RegisterOnloadHandler(init);
