/**
 * @fileoverview Code to handle home view of gadget.
 * @author gadgetfactory@google.com (gadgetfactory)
 */

/**
 * Number of trips in home view.
 * @const
 * @type {number}
 */
var TRIPS_IN_HOME = 5;

/**
 * Default error value.
 * @const
 * @type {number}
 */
var ERROR_VALUE = 1;

/**
 * Array to hold current trip related information.
 * @type {Object}
 */
var selectedTrip = {};

/**
 * Callback function for all trips data of the user.
 * @param {Object} response Response of opensocial request.
 */
function callbackAllTrips(response) {
  var data = [];
  if (!gOpenSocial.viewer) {
    gOpenSocial.viewer = response.get('viewer').getData();
    gViewer = gOpenSocial.viewer.getDisplayName();
    data.push(gViewer);
  }
  gOpenSocial.viewerFriends = response.get('groupPeople').getData();
  if (gOpenSocial.viewerFriends) {
    gOpenSocial.viewerFriends.each(function(person) {
      data.push(person.getDisplayName());
    });
  }
  var friendsList = [];
  for (var j = 0, length = data.length - 1; j <= length; j++) {
    friendsList.push('\'' + _esc(data[j]) + '\'');
  }
  var url = BASE_URL + '/getAllTrips?friendsList=' +
      friendsList.join(',') + '&rand=' + Math.random();
  gadgets.io.makeRequest(url, initializeData);
}

/**
 * Callback function for handling the response of load friends function.
 * @param {Object} response Response of opensocial request.
 */
function handleResponse(response) {
  callbackAllTrips(response);
}
/**
 * Initializes the trip data.
 * @param {Object} response Response data.
 */
function initializeData(response) {
  // Global variable to store all trips as an array.
  var owner_trips = gadgets.json.parse(response.data);
  gTripData = owner_trips;
  if (owner_trips != '') {
    showTrips();
  } else {
    callbackCreate();
  }
}

/**
 * Used for displaying the trips of the owner.
 */
function showTrips() {
  _gel('server-msg').style.display = 'none';
  var html = [];
  var tripDataLen = gTripData.length;
  if (tripDataLen) {
    html.push(_gel('tpl-trip-header').value);
    tripDataLen = Math.min(TRIPS_IN_HOME, tripDataLen);
    for (var i = 0; i < tripDataLen; i++) {
      html.push(getDetailsSection(i));
    }
    html.push(_gel('tpl-create-callback').value);
  } else {
    html.push(_gel('tpl-create-trip').value);
  }
  _gel('home-content').innerHTML = html.join('');
  _IG_AdjustIFrameHeight();
}

/**
 * Used to get the html of the details of the expanded trip.
 * @param {string} id Id of the current trip.
 * @return {string} Details of the expanded trip.
 */
function getDetailsSection(id) {
  var html = [];
  var MAX_NAME_LENGTH = 16;
  var tripData = gTripData[id];
  var tripName = tripData.name;
  if (tripName.length > MAX_NAME_LENGTH) {
    tripName = tripName.substring(0, MAX_NAME_LENGTH) + '...';
  }
  var tplHtml = _gel('tpl-trip-row').value;
  var tplData = {
    id: id,
    tripName: tripName,
    sDate: Util.isEmpty(tripData.sdate) ?
        '' : DateLib.formatDate(tripData.sdate, '{M} {d}'),
    eDate: Util.isEmpty(tripData.fdate) ?
        '' : DateLib.formatDate(tripData.fdate, '{M} {d}'),
    location: tripData.loc
  };
  return Util.supplant(tplHtml, tplData);
}

/**
 * Display the trip details.
 * @param {number} nIndex Index of the current trip.
 */
function showTripDetails(nIndex) {
  var tripData = gTripData[nIndex];
  selectedTrip = {
    ownerName: tripData.ownerName,
    name: tripData.name,
    id: tripData.id,
    location: tripData.loc,
    lat: tripData.lat,
    lng: tripData.lng,
    accuracy: tripData.accuracy,
    duration: tripData.duration,
    sdate: tripData.sdate,
    edate: tripData.fdate
  };
  goToCanvas(false);
}

/**
 * Check for the response and handles the error.
 * @param {Object} response Server response.
 */
function checkResponseTrip(response) {
  var responseData = gadgets.json.parse(response.data);
  var serverMsgRef = _gel('server-msg');
  if (responseData[0].error != ERROR_VALUE &&
      responseData[0].error != 'undefined') {
    serverMsgRef.style.display = 'none';
    selectedTrip.id = responseData[0].tripId;
    goToCanvas(false);
  } else {
    serverMsgRef.style.display = 'block';
    serverMsgRef.innerHTML = '<b>' + prefs.getMsg('server_error') +
        '<b>&nbsp;<a href="javascript:void(0);" ' +
        'onclick="createTripHome(\'' +
        selectedTrip.name + '\');">' +
        prefs.getMsg('try_again') + '</a>';
    _IG_AdjustIFrameHeight();
  }
}

/**
 * Checks whether the location is valid, and if valid creates trip.
 * @param {string} address Location entered by user.
 */
function validateAddress(address) {
  if (geocoder) {
    geocoder.getLocations(address, function(result) {
      if (result.Status.code != RESPONSE_SUCCESS) {
        displayMessage('invalid_loc');
        return;
      }
      if (result.Placemark.length) {
        var place = result.Placemark[0];
        selectedTrip.accuracy = place.AddressDetails.Accuracy;
        var p = result.Placemark[0].Point.coordinates;
        selectedTrip.lat = p[1];
        selectedTrip.lng = p[0];
        selectedTrip.name = address;
        selectedTrip.location = address;
        // Default duration for a trip.
        selectedTrip.duration = 7;
        _gel('location').className = 'text-box';
        _gel('server-msg').style.display = 'none';
        createTripHome(selectedTrip.name);
      }
    });
  }
}

/**
 * Displays error message.
 * @param {string} msg Message to be displayed.
 */
function displayMessage(msg) {
  var locationRef = _gel('location');
  var serverMsgRef = _gel('server-msg');
  locationRef.value = '';
  locationRef.focus();
  serverMsgRef.style.display = 'block';
  serverMsgRef.innerHTML = prefs.getMsg(msg);
  _IG_AdjustIFrameHeight();
}

/**
 * Validates the trip name so that it doesn't contain numeric fields.
 */
function validateTripHome() {
  var locationRef = _gel('location');
  var tripLoc = _trim(locationRef.value);
  if (!tripLoc) {
    locationRef.focus();
    return;
  }
  if (Util.isNumeric(tripLoc)) {
    displayMessage('numeric_err');
    return;
  }
  validateAddress(tripLoc);
}

/**
 * Callback function to create home UI.
 */
function callbackCreate() {
  var html = [_gel('tpl-where-visit').value];
  if (gTripData.length) {
    html.push(_gel('tpl-view-trips').value);
  }
  html.push('</tr></table>');
  _gel('home-content').innerHTML = html.join('');
  addKeyListener('location');
  _IG_AdjustIFrameHeight();
}

/**
 * Creates the trip and saves it in the database.
 * @param {string} location Trip location.
 */
function createTripHome(location) {
  var req = opensocial.newDataRequest();
  req.add(req.newFetchPersonRequest('VIEWER'), 'viewer');
  req.send(function(response) {
    selectedTrip.ownerId = response.get('viewer').getData().getId();
    selectedTrip.ownerName = response.get('viewer').getData().getDisplayName();
    gViewer = selectedTrip.ownerName;
    var params = {currentViewer: selectedTrip.ownerName};
    // Creates an instance of trip object.
    var objTrip = getTripObject();
    if (geocoder) {
      geocoder.getLocations(location, function(result) {
        // If location of created trip is found.
        if (result.Status.code == RESPONSE_SUCCESS) {
          if (result.Placemark.length) {
            var place = result.Placemark[0];
            selectedTrip.accuracy = place.AddressDetails.Accuracy;
            var pointCoordinates = result.Placemark[0].Point.coordinates;
            selectedTrip.name = location + ' ' + prefs.getMsg('trip');
            var params = {};
            params[gadgets.io.RequestParameters.METHOD] =
                gadgets.io.MethodType.POST;
            var postData = gadgets.io.encodeValues({
              'owner_id': selectedTrip.ownerId,
              'owner_name': selectedTrip.ownerName,
              'trip_name': selectedTrip.name,
              'location': location,
              'lat': pointCoordinates[1],
              'lang': pointCoordinates[0],
              'accuracy': selectedTrip.accuracy,
              'duration': selectedTrip.duration,
              'isListing': false
            });
            params[gadgets.io.RequestParameters.POST_DATA] = postData;
            var url = BASE_URL + '/saveTrip?rand=' + Math.random();
            gadgets.io.makeRequest(url, function(response) {
              var tplData;
              var responseData = gadgets.json.parse(response.data);
              if (responseData[0].error == transResponse.ERROR) {
                tplData = {
                  message:
                      Util.supplant(prefs.getMsg('trip_create_err'),
                                   {name: location})
                };
                showServerMessage(tplData);
                return;
              }
              tplData = {message: prefs.getMsg('trip_created')};
              showServerMessage(tplData);
              selectedTrip.id = responseData[0].tripId;
              objTrip.trip_id = selectedTrip.id;
              objTrip.trip_name = selectedTrip.name;
              objTrip.owner_name = selectedTrip.ownerName;
              objTrip.owner_id = selectedTrip.ownerId;
              objTrip.loc = location;
              objTrip.lat = pointCoordinates[1];
              objTrip.lang = pointCoordinates[0];
              objTrip.accuracy = selectedTrip.accuracy;
              objTrip.thumb_up = 0;
              objTrip.thumb_down = 0;
              objTrip.sdate = selectedTrip.sdate;
              objTrip.fdate = selectedTrip.edate;
              objTrip.thumb_down = 0;
              gTripData.push(objTrip);
              goToCanvas(false);
            }, params);
          }
        } else {
          _gel('server-msg').innerHTML = prefs.getMsg('loc_not_found');
        }
      });
    } else {
      _gel('server-msg').innerHTML = prefs.getMsg('map_not_loaded');
    }
  });
}

/**
 * First method call on page load to check browser comaptibility and
 * request for user data.
 */
function init() {
  if (GBrowserIsCompatible()) {
    geocoder = new GClientGeocoder();
  }
  _gel('home-content').innerHTML = LOADING_TPL;
  requestData();
}

function handleOpensocial() {
  var req = opensocial.newDataRequest();
      req.add(req.newFetchPersonRequest('VIEWER'), 'viewer');
      req.add(req.newFetchPeopleRequest(opensocial.newIdSpec({
          'userId': 'VIEWER',
          'groupId': 'FRIENDS'
      })), 'groupPeople');
      req.send(callbackAllTrips);
}

/**
 * Initializes the params and sends the control to canvas view.
 * @param {boolean} listing Check to decide whether to move to
 *     listing page in canvas view or trip canvas view.
 */
function goToCanvas(listing) {
  var params = {isListing: !listing};
  if (!listing) {
    params = {
      tripId: selectedTrip.id,
      tripName: selectedTrip.name,
      tripLocation: selectedTrip.location,
      ownerName: selectedTrip.ownerName,
      lat: selectedTrip.lat,
      lng: selectedTrip.lng,
      accuracy: selectedTrip.accuracy,
      duration: selectedTrip.duration,
      sdate: selectedTrip.sdate,
      fdate: selectedTrip.edate,
      currentViewer: gViewer
    };
  }
  _gel('home-content').innerHTML = '';
  gadgets.views.requestNavigateTo(new gadgets.views.View('canvas'), params);
}

/**
 * Registers keyup event.
 * @param {string} id Element id.
 */
function addKeyListener(id) {
  // For mozilla, firefox, opera.
  if (window.addEventListener) {
    _gel(id).addEventListener('keyup', handleKeyEvent, false);
  } else { // For IE
    _gel(id).attachEvent('onkeyup', handleKeyEvent);
  }
}

/**
 * Calls validateTripHome if return key is pressed.
 * @param {Object} event Keyup event.
 */
function handleKeyEvent(event) {
  if (!event) {
    event = window.event;
  }
  // keycode for return key.
  if (event.keyCode == '13') {
    // "target" for mozilla, forefox, opera and "srcelement" for IE.
    var id =
        event['target'] ? event['target']['id'] : event['srcElement']['id'];
    // Registering event for location element.
    if (id == 'location') {
      validateTripHome();
    }
  }
}

// Entry point.
_IG_RegisterOnloadHandler(init);

// Exports
window.goToCanvas = goToCanvas;
window.validateTripHome = validateTripHome;
window.callbackCreate = callbackCreate;
window.showTripDetails = showTripDetails;
window.createTripHome = createTripHome;
