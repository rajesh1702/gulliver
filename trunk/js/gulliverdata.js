/**
 * @fileoverview File defines object to handle itinerary.
 * @author gadgetfactory@google.com (gadgetfactory)
 */

/**
 * Class defines singleton object for itinerary.
 * @constructor
 */
function JGulliverData() {
  this.nSelectedTripIndex = -1;
  this.arrTripData = [];
}

/**
 * Instance of JGulliverData class.
 * @type {JGulliverData}.
 */
JGulliverData.instance = null;

/**
 * It returns the instance of itinerary if exists else create new instance.
 * @return {object} object of JGulliverData.
 */
JGulliverData.getInstance = function() {
  if (!JGulliverData.instance) {
    JGulliverData.instance = new JGulliverData();
  }
  return JGulliverData.instance;
};

/**
 * It returns the selected trip in the itinerary.
 * @return {object} object of JTrip.
 */
JGulliverData.getCurrentTrip = function() {
  var gulliverData = JGulliverData.getInstance();
  var objTrip = null;
  if (gulliverData.nSelectedTripIndex != -1) {
    objTrip = gulliverData.arrTripData[gulliverData.nSelectedTripIndex];
  }
  return objTrip;
};

/**
 * It returns the index of selected trip in the itinerary.
 * @param {string} id The trip id.
 * @return {number} Trip index if found else -1.
 */
JGulliverData.prototype.getTripIndex = function(id) {
  var gulliverData = JGulliverData.getInstance();
  var objTrip = null;
  for (var i = 0; i < gulliverData.arrTripData.length; i++) {
    if (gulliverData.arrTripData[i].id == id) {
      return i;
    }
  }
  return -1;
};

/**
 * Fetches all trips from database and displays them.
 * @param {Object} groupData Collection of friends.
 */
JGulliverData.prototype.fetchAllTrips = function(groupData) {
  var gulliverData = this;
  gulliverData.arrTripData = [];

  var url;
  if (wave && wave.isInWaveContainer()) {
    url = BASE_URL + '/getAllTrips?friendsList=' + _esc(groupData) +
          '&rand=' + Math.random();
  } else if (_unesc(_args()['synd']) == 'ig') {
    var friendsList = [];
    for (var j = 0, length = groupData.length - 1; j <= length; j++) {
      friendsList.push('\'' + _esc(groupData[j]) + '\'');
    }
    friendsList = friendsList.join(',');
    url = BASE_URL + '/getAllTrips?friendsList=' + friendsList +
          '&rand=' + Math.random();
  } else {
    var friendsList = [];
    for (var j = 0, length = groupData.length - 1; j <= length; j++) {
      friendsList.push('\'' + _esc(groupData[j].getDisplayName()) + '\'');
    }
    friendsList = friendsList.join(',');
    url = BASE_URL + '/getAllTrips?friendsList=' + friendsList +
          '&rand=' + Math.random();
  }
  gadgets.io.makeRequest(url, function(response) {
    var allTrips = gadgets.json.parse(response.data);
    if (allTrips.error != undefined) {
      var html = '<div class="no_trip">' + prefs.getMsg('server_error') +
          '&nbsp;<span class="link" onclick="' +
          'javascript:callbackTrips();">' + prefs.getMsg('try_again') +
          '</span></div>';
      _gel('canvasContent').innerHTML = html;
      return;
    }
    if (allTrips.length == 0) {
      var element = _gel('loadingSection');
      if (element) {
        element.innerHTML = '';
      }
      createTripBox();
    } else {
      var objTrip;
      for (var i = 0; i < allTrips.length; i++) {
        objTrip = new JTrip();
        objTrip.id = allTrips[i].trip_id;
        objTrip.ownerId = allTrips[i].owner_id;
        objTrip.ownerName = allTrips[i].owner_name;
        objTrip.name = allTrips[i].trip_name;
        objTrip.lat = allTrips[i].lat;
        objTrip.lng = allTrips[i].lang;
        objTrip.loc = allTrips[i].location;
        objTrip.accuracy = allTrips[i].accuracy;
        objTrip.duration = allTrips[i].duration;
        objTrip.rating = allTrips[i].rating;
        if (allTrips[i].thumb_up) {
          objTrip.thumb_up = allTrips[i].thumb_up;
        }
        if (allTrips[i].thumb_down) {
          objTrip.thumb_down = allTrips[i].thumb_down;
        }
        if (!isEmpty(allTrips[i].sdate)) {
          objTrip.sdate = formatDate(allTrips[i].sdate, '%m/%d/%Y');
        }
        if (!isEmpty(allTrips[i].fdate)) {
          objTrip.edate = formatDate(allTrips[i].fdate, '%m/%d/%Y');
        }
        gulliverData.arrTripData.push(objTrip);
      }
    }
    if (refreshUIData) {
      exploreTrip(gCurrentTripId);
    } else {
      gulliverData.displayTrips();
      refreshUIData = true;
    }
  });
};

/**
 * It creates a trip and add it to itinerary.
 * @param {string} location The location of trip.
 * @param {number} lat The latitude.
 * @param {number} lng The longitude.
 * @param {number} accuracy The accuracy of location.
 */
JGulliverData.prototype.createTrip = function(location, lat, lng, accuracy) {
  var gulliverData = this;
  var counter = gulliverData.arrTripData.length;
  var loc = location.toLowerCase();
  var count = 0;
  for (var i = 0; i < counter; i++) {
    if (loc == gulliverData.arrTripData[i].name.substr(0,
                   location.length).toLowerCase()) {
      count++;
    }
  }

  var tName = location + prefs.getMsg('trip');
  if (count) {
    tName = tName + count;
  }
  var req = opensocial.newDataRequest();
  req.add(req.newFetchPersonRequest('VIEWER'), 'viewer');
  req.send(function(response) {
    var ownerId;
    var ownerName;
    if (wave && wave.isInWaveContainer()) {
      ownerId = wave.getViewer().getId();
      ownerName = wave.getViewer().getDisplayName();
    } else {
      ownerId = response.get('viewer').getData().getId();
      ownerName = response.get('viewer').getData().getDisplayName();
    }
    var params = {};
    params[gadgets.io.RequestParameters.METHOD] = gadgets.io.MethodType.POST;
    post_data = gadgets.io.encodeValues({
      'owner_id': ownerId,
      'owner_name': ownerName,
      'trip_name': tName,
      'location': location,
      'lat': lat,
      'lang': lng,
      'accuracy': accuracy,
      'duration': DEFAULT_DURATION,
      'rating': DEFAULT_RATING
    });
    params[gadgets.io.RequestParameters.POST_DATA] = post_data;
    var url = BASE_URL + '/saveTrip?rand=' + Math.random();
    gadgets.io.makeRequest(url, function(response) {
      var html = ACTION_MSG_TPL;
      var responseData = gadgets.json.parse(response.data);
      if (responseData[0].error == enDBTransaction.ERROR) {
        html = html.replace(/%MESSAGE%/, prefs.getMsg('trip_create_err'))
                   .replace(/%NAME%/, tName);
        return;
      }
      html = html.replace(/%MESSAGE%/, prefs.getMsg('trip_created'));
      var objTrip = new JTrip();
      objTrip.id = responseData[0].tripId;
      objTrip.name = tName;
      objTrip.ownerName = ownerName;
      objTrip.loc = location;
      objTrip.lat = lat;
      objTrip.lng = lng;
      objTrip.accuracy = accuracy;
      objTrip.rating = DEFAULT_RATING;
      objTrip.duration = DEFAULT_DURATION;
      gulliverData.arrTripData.push(objTrip);
      gulliverData.nSelectedTripIndex = gulliverData.arrTripData.length - 1;
      hideDialog();
      showDragTip();
      callbackCreateTrip();
    }, params);
  });
};

/**
 * It deletes the current trip from db.
 */
JGulliverData.prototype.deleteTrip = function() {
  var gulliverData = this;
  var objTrip = gulliverData.arrTripData[gulliverData.nSelectedTripIndex];
  var params = {};
  params[gadgets.io.RequestParameters.METHOD] = gadgets.io.MethodType.POST;
  var postData = gadgets.io.encodeValues({'trip_id': objTrip.id});
  params[gadgets.io.RequestParameters.POST_DATA] = postData;
  var url = BASE_URL + '/deleteTrip?rand=' + Math.random();
  gadgets.io.makeRequest(url, function(response) {
    var reaponseData = gadgets.json.parse(response.data);
    if (reaponseData.error == enDBTransaction.ERROR) {
      _gel('msgContainer').style.display = 'block';
      var html = ACTION_MSG_TPL;
      html = html.replace(/%MESSAGE%/, prefs.getMsg('trip_delete_err'))
                 .replace(/%NAME%/, _unesc(objTrip.name));
      _gel('serverMsg').innerHTML = '';
      timerMsg.createTimerMessage(html, 5);
      return;
    }

    gulliverData.arrTripData.splice(gulliverData.nSelectedTripIndex, 1);
    hideDialog();
    callbackTrips();
  }, params);
};

/**
 * Used for displaying he all trips.
 */
JGulliverData.prototype.displayTrips = function() {
  this.nSelectedTripIndex = -1;
  ownerTrips = this.arrTripData;

  var html = ['<div style="padding:7px 0;background:#f5eee6;">',
      '<table width="100%" cellpadding="0" cellspacing="0"><tr><td ',
      'align="left" width="35%" style="padding-left:7px;">',
      '<input type="text" id="searchTripText" class="txtBox" style="',
      'width:100%;"/></td><td valign="middle">&nbsp;<input type="button" ',
      'style="width:91px;" value="' + prefs.getMsg('search_trips') + '" ',
      'onclick="fillForSortedRecords(true);"/>&nbsp;<nobr>',
      '<span id="showAllTrips" style="display:none;" ',
      'onclick="fillForSortedRecords(false);">',
      '</span></nobr></td><td width="190px" align="right">',
      '<span style="float:left;padding-top:2px;"><input style="width:88px;" ',
      'type="button" value="' + prefs.getMsg('createTrip') + '" ',
      'onclick="createTripBox();"></span><nobr><span ',
      'id="showingTripText" style="float:right;margin:6px 5px;">',
      '</span></nobr></td></tr></table></div>',
      '<div><table width="100%" style="backgroundColor:#0080ff;">',
      '<tr><td width="100%" colspan="3" align="center">',
      '<div id="error_container" style="display:none;" class="msg_container">',
      '<center><span id="error_msg" class="server_msg"></span></center></div>',
      '</td></tr><tr><td width="27%" align="right" class="tripcols" ',
      'onclick="changeSign(\'col_1\',1);"><div id="col_1" ',
      'style="float:left;">' + prefs.getMsg('name') +
      '&nbsp;</div><div style="float:left;" id="img_1"><div>',
      '</td><td width="24%" align="left" ',
      'class="tripcols" onclick="changeSign(\'col_2\',2);">',
      '<div id="col_2" style="float:left;">' + prefs.getMsg('date') +
      '&nbsp;</div><div style="float:left;" id="img_2">', DOWN_ARROW_IMG,
      '<div></td><td width="24%" align="left" class="tripcols" ',
      'onclick="changeSign(\'col_3\',3);"><div id="col_3" ',
      'style="float:left;">' + prefs.getMsg('location') + '&nbsp;</div><div ',
      'style="float:left;" id="img_3"><div></td>',
      '<td width="24%" align="left" class="tripcols" ',
      'onclick="changeSign(\'col_4\',4);"><div id="col_4" ',
      'style="float:left;">' + prefs.getMsg('owner') +
      '&nbsp;</div><div style="float:left;" ',
      'id="img_4"><div></td></tr></table><div id="displayRec"></div>',
      '<div id="showPagination"></div></table></div>'];

  _gel('canvasContent').innerHTML = html.join('');
  addKeyListener('searchTripText');

  sortCol = enTripCol.DATE;
  bAscending = false;
  ownerTrips = ownerTrips.sort(sortHandler);
  fillForSortedRecords(false);

  _IG_AdjustIFrameHeight();
};
