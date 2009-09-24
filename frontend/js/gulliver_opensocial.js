/**
 * @fileoverview Code related to opensocial trip data.
 * @author gadgetfactory@google.com (gadgetfactory)
 */

/**
 * Sends viewer request.
 */
function getViewer() {
  var reqViewer = opensocial.newDataRequest();
  reqViewer.add(reqViewer.newFetchPersonRequest('VIEWER'), 'viewer');
  reqViewer.add(reqViewer.newFetchPeopleRequest(opensocial.newIdSpec({
                                    'userId': 'VIEWER',
                                    'groupId': 'FRIENDS'
                                  })), 'groupPeople');
  reqViewer.send(handleViewRequest);
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
 * Find the list of friends with whom the application is being shared.
 */
function loadFriends() {
  var req = opensocial.newDataRequest();
  if (wave && wave.isInWaveContainer()) {
    wave.setStateCallback(waveParticipants);
  } else if (_unesc(_args()['synd']) == 'ig') {
    req.add(req.newFetchPersonRequest('VIEWER'), 'viewer');
    req.add(req.newFetchPeopleRequest(opensocial.newIdSpec({
                                      'userId': 'VIEWER',
                                      'groupId': 'FRIENDS'
                                    })), 'groupPeople');
  } else {
    var id = opensocial.newIdSpec({'userId': 'OWNER', 'groupId': 'admins'});
    req.add(req.newFetchPeopleRequest(id), 'groupPeople');
    getViewer();
  }
  req.send(handleGroupRequest);
}

/**
 * Call back for list of friends.
 * @param {Object} response Response object.
 */
function handleGroupRequest(response) {
  var gulliverData = JGulliverData.getInstance();
  if (!response.hadError()) {
    if (!gOpenSocial.viewer) {
      gOpenSocial.viewer = response.get('viewer').getData();
      gViewer = gOpenSocial.viewer.getDisplayName();
    }
    var data = [];
    if (_unesc(_args()['synd']) == 'ig') {
      data.push(gViewer);
      if (!gOpenSocial.viewerFriends){
        gOpenSocial.viewerFriends = response.get('groupPeople').getData();
      }
      if (gOpenSocial.viewerFriends) {
        gOpenSocial.viewerFriends.each(function(person) {
          data.push(person.getDisplayName());
        });
      }
    } else {
      data = response.get('groupPeople').getData().asArray();
     }
    gulliverData.fetchAllTrips(data);
  }
}

/**
 * List of friends for wave container.
 */
function waveParticipants() {
  var friendsList = [];
  var participants = wave.getParticipants();
  var groupData = participants;
  var numPeople = groupData.length;
  gViewer = wave.getViewer().getDisplayName();
  for (var k = 0; k < numPeople; ++k) {
    friendsList.push('\'' + groupData[k].getDisplayName() + '\'');
  }
  friendsList = friendsList.join(',');
  var gulliverData = JGulliverData.getInstance();
  gulliverData.nSelectedTripIndex = -1;
  gulliverData.fetchAllTrips(friendsList);
}

/**
 * Activity message display.
 * @param {string} data Data for error message.
 */
function onActivityPosted(data) {
  var objTrip = JGulliverData.getCurrentTrip();
  var objItem = objTrip.arrItem[gPopUpIndex];
  _gel('msgContainer').style.display = 'block';
  var html = ACTION_MSG_TPL;
  if (data.hadError()) {
    var message = (data.getErrorCode() ==
        opensocial.ResponseItem.Error.LIMIT_EXCEEDED) ?
        prefs.getMsg('limit_exceed_error') :
        message = prefs.getMsg('activity_error');
    html = html.replace(/%MESSAGE%/, message);
  } else {
    html = html.replace(/%MESSAGE%/, prefs.getMsg('activity'));
    if (gPopUpIndex > -1) {
      if (_gel('item_thumb_up').checked) {
        objItem.Item_thumb_up = objItem.Item_thumb_up + 1;
      } else {
        objItem.Item_thumb_down = objItem.Item_thumb_down + 1;
      }
     _gel('unscheduleItemBox').innerHTML = '';
     createScheduledItemBox(objTrip.sdate, objTrip.edate, objTrip.duration);
     objTrip.showAllItems();
     strSelectedItemId = '';
    } else {
       _gel('thumb').style.display = '';
      if (_gel('thumb_up').checked) {
      objTrip.thumb_up = objTrip.thumb_up + 1;
      _gel('thum_up').innerHTML = objTrip.thumb_up;
      } else {
      objTrip.thumb_down = objTrip.thumb_down + 1;
      _gel('thum_down').innerHTML = objTrip.thumb_down;
      }
    }
  }
  timerMsg.createTimerMessage(html, 5);
  closeInfoWindow();
  hideViewDateDialogBox();
}

/**
 * Posting activity.
 * @param {number} index Index of trip item.
 */
function postActivity(index) {
  gPopUpIndex = index;
  hideViewDateDialogBox();
  var objTrip = JGulliverData.getCurrentTrip();
  var objItem = objTrip.arrItem[index];
  var data = {};
  var params = {};
  var thumb_up = objTrip.thumb_up;
  var thumb_down = objTrip.thumb_down;
  if (!objTrip.thumb_up) {
    objTrip.thumb_up = 0;
  }
  if (!objTrip.thumb_down) {
    objTrip.thumb_down = 0;
  }
  var thumbsUP;
  if (gPopUpIndex > -1) {
    thumb_up = objItem.Item_thumb_up;
    thumb_down = objItem.Item_thumb_down;
    if (_gel('item_thumb_up').checked) {
      thumb_up = objItem.Item_thumb_up + 1;
    } else {
      thumb_down = objItem.Item_thumb_down + 1;
    }
    thumbsUP = _gel('item_thumb_up').checked ?
               prefs.getMsg('thumbs_up') :
               prefs.getMsg('thumbs_down');
  } else {
     if (_gel('thumb_up').checked) {
      thumb_up = objTrip.thumb_up + 1;
    } else {
      thumb_down = objTrip.thumb_down + 1;
    }
   thumbsUP = _gel('thumb_up').checked ? prefs.getMsg('thumbs_up') :
                                         prefs.getMsg('thumbs_down');
  }
  var body = _gel('addVote').value;
  var title = thumbsUP + ' ' + prefs.getMsg('thumbs_title') + '<span><b>' +
              ' ' + objTrip.name + '</b></span>';
  params[gadgets.io.RequestParameters.METHOD] = gadgets.io.MethodType.POST;
  var postdata = gadgets.io.encodeValues({
      'user_id': objTrip.ownerId,
      'trip_id': objTrip.id,
      'thumb_up': thumb_up,
      'thumb_down': thumb_down
    });

  var thumb_url = '/saveThumb'
  if (index > -1) {
    objItem =
      postdata = gadgets.io.encodeValues({
        'item_id': objItem.id,
        'Item_thumb_up': thumb_up,
        'Item_thumb_down': thumb_down
      });
    thumb_url = '/saveItemThumb'
    title = thumbsUP + ' ' + prefs.getMsg('thumbs_item_titled') +
            '&nbsp;&nbsp;<span><b>' + objTrip.arrItem[index].name +
            '</b></span>&nbsp;&nbsp;' +
            prefs.getMsg('thumbs_item_trip_titled') +
            '&nbsp;<span><b>' + objTrip.name + '</b></span>';
  }
  params[gadgets.io.RequestParameters.POST_DATA] = postdata;
  var url = BASE_URL + thumb_url;
  gadgets.io.makeRequest(url, function(response) {
    var responseData = gadgets.json.parse(response.data);
    _gel('msgContainer').style.display = 'block';
    var html = ACTION_MSG_TPL;
    if (responseData.error == enDBTransaction.ERROR) {
      html = html.replace(/%MESSAGE%/, prefs.getMsg('activity_error'));
      timerMsg.createTimerMessage(html, 10);
      return false;
    } else {
      data[opensocial.Activity.Field.TITLE] = title;
      data[opensocial.Activity.Field.BODY] = body;
      data[opensocial.Activity.Field.BODY_ID] = body;
      var activity = opensocial.newActivity(data);
      opensocial.requestCreateActivity(
      activity,
      opensocial.CreateActivityPriority.HIGH,
      onActivityPosted);
    }
  }, params);
}

function activityPostingCheck() {
  var container = (isIGoogle == 'ig') ? 'iGoogle' : 'Wave'
  var message = 'Activity posting is not available on ' + container;
  _gel('loading-container').innerHTML = '';
  _gel('serverMsg').innerHTML = '';
  _gel('msgContainer').style.display = 'block';
  var html = ACTION_MSG_TPL;
  html = html.replace(/%MESSAGE%/, message);
  timerMsg.createTimerMessage(html, 10);
}

/**
 * Dialog for add your vote.
 */
function addYourVote() {
  if (wave && wave.isInWaveContainer()) {
    activityPostingCheck();
  } else {
    // Close if any popup window is there.
    closeInfoWindow();
    var addVoteInfo = '<div style="color:#000;width:380px;"><div ' +
        '><table cellpadding="0" cellspacing="0" ' +
        'style="font-size:14px;font-weight:bold;height:25px;" width="100%" ' +
        'class="dialog-title"><tr><td align="left">' +
        prefs.getMsg('your_vote') + '</td><td align="right">' +
        '<span class="link" onclick="javascript:hideViewDateDialogBox();">' +
        '<img border="0" style="width:15px;padding-left:1px;" ' +
        'src="' + FILE_SERVER + '/images/close_promo.gif"/>' +
        '</span></td></tr></table></div><div style="padding:8px 0 5px 5px;">' +
        prefs.getMsg('comments') + '</div><div style="padding:0 5px;">' +
        '<textarea cols="31" name="comment"' +
        ' rows="3" id="addVote"></textarea></div>' +
        '<div style="padding:0 5px;"><input type="radio"' +
        ' id="thumb_up" name="thumb_up" value="1" checked/>' +
        prefs.getMsg('thumbs_up') +
        '</div><div style="padding:0 5px;">' +
        '<input type="radio" name="thumb_up" value="0"/>' +
        prefs.getMsg('thumbs_down') +
        '</div><div style="padding:5px 5px;"><table><tr><td align="left">' +
        '<input type="button" value="' + prefs.getMsg('vote') +
        '" onclick="postActivity(-1)">' +
        '</td><td align="right"><input type="button" value="' +
        prefs.getMsg('cancel') +
        '" onclick="hideViewDateDialogBox()"></td></tr></table></div></div>';
    showDialog(addVoteInfo);
    _gel('addVote').focus();
  }
}

/**
* Shows the popup window on click of thumb image.
* @param {number} index Position of div.
*/
function addVotePopUP(index) {
  if (wave && wave.isInWaveContainer()) {
    activityPostingCheck();
  } else {
    closeInfoWindow();
    var itemInfo;
    var html = [];
    var addVoteInfo = '<div style="color:#000;width:380px;"><div>' +
        '<table cellpadding="0" cellspacing="0" ' +
        'style="font-size:14px;font-weight:bold;height:25px;" width="100%" ' +
        'class="dialog-title-send"><tr><td align="left">' +
        prefs.getMsg('your_vote') + '</td><td align="right">' +
        '<span class="link" onclick="javascript:hideViewDateDialogBox();">' +
        '<img border="0" style="width:15px;padding-left:1px;"' +
        'src="' + FILE_SERVER + '/images/close_promo.gif"/></span>' +
        '</td></tr></table></div><div style="padding:8px 0 5px 5px;">' +
        prefs.getMsg('comments') + '</div><div style="padding:0 5px;">' +
        '<textarea cols="31" name="comment" rows="3" id="addVote"></textarea>' +
        '</div><div style="padding:0 5px;"><input type="radio" ' +
        'id="item_thumb_up" name="thumb_up" value="1" checked />' +
        prefs.getMsg('thumbs_up') + '</div><div style="padding:0 5px;">' +
        '<input type="radio" name="thumb_up" value="0"/>' +
        prefs.getMsg('thumbs_down') +
        '</div><div style="padding:5px 5px;"><table><tr><td align="left">' +
        '<input type="button" value="' + prefs.getMsg('vote') +
        '" onclick="postActivity(' + index + ')">' +
        '</td><td align="right"><input type="button" value="' +
        prefs.getMsg('cancel') +
        '" onclick="hideViewDateDialogBox()"></td></tr></table></div></div>';

      html = ['<div style="border:1px solid #000;',
              'background-color:#fff;position:absolute;z-index:1000;',
              'padding-bottom:5px;width:267px;font-size:12px;">',
               addVoteInfo, '</div>'];
    showDialog(addVoteInfo);
    _gel('addVote').focus();
  }
}
