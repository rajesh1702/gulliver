/**
 * @fileoverview Code contains canvas view related functionality.
 * @author
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
 * Selected comment section id.
 * @type {string}
 */
var selectedCommentIndex;

/**
 * Array to hold filtered categories shown in suggested categories list.
 * @type {Array}
 */
var filteredCategories = [];

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
 * To hold index of the category selected by user in auto select option of
 * local search.
 */
var arrowPosition = 0;

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
var SHADOW_IMG = 'http://www.google.com/mapfiles/shadow50.png';

/**
 * To hold list of filtered results while searching for friends in share dialog.
 */
var filteredListCount = 0;

/**
 * To hold cursor position while moving up down in share dialog options.
 */
var cursorPosition = 0;

/**
 * To hold email id value from user input box.
 */
var emailToshare = '';

/**
 * It looks for a list of friends selected from dialog box with whom user wants
 * to share this trip with. It then adds these friends as collaborators for
 * that trip.
 */
function addCollaborator() {
  var selectedFriends = [];
  for (var i = 0, count = friendsContactData.length; i < count; i++) {
    if (_gel('mail-id-' + i).checked) {
      selectedFriends.push(friendsContactData[i].emailId);
    }
  }
  var trip = getTripById(gCurrentTripsData.currentTripId);
  var currentCollaborators = trip.collaborators;
  var newFriends = [];
  var removeFriends = [];
  var areValidIds = true;
  var sendRequest = false;
  var sharedIds = emailToshare;
  if (sharedIds && sharedIds != prefs.getMsg('add_email')) {
    if (Util.validateEmailId(sharedIds)) {
      var allEmailIds = sharedIds.split(',');
      for (var i = 0; i < allEmailIds.length; i++) {
        // Trimming all white space from both ends of an input string.
        var currentId = trimAllSpaces(allEmailIds[i]);
        if (currentId.toLowerCase().indexOf('@google.com') == -1 &&
            currentId.toLowerCase().indexOf('@gmail.com') == -1) {
          areValidIds = false;
          break;
        } else {
          selectedFriends.push(currentId.toLowerCase());
          emailToshare = '';
        }
      }
    } else {
      var error = _gel('share-error');
      error.innerHTML = prefs.getMsg('valid_email');
      error.style.display = 'block';
      return;
    }
  }

  // Get new friends added for collaboration.
  for (var i = 0, len = selectedFriends.length; i < len; i++) {
    if (Util.indexOf(currentCollaborators, selectedFriends[i]) == -1) {
      newFriends.push(selectedFriends[i]);
    }
  }
  // Get already collaborated friends which have been unselected.
  for (var i = 0, len = currentCollaborators.length; i < len; i++) {
    if (Util.indexOf(selectedFriends, currentCollaborators[i]) == -1 &&
        currentCollaborators[i] != gOwnerId) {
      removeFriends.push(currentCollaborators[i]);
    }
  }
  hideViewDateDialogBox();
  var params = {};
  var postData = {
    'action': Operation.SHARE_TRIP,
    'id': gCurrentTripsData.currentTripId,
    'ldap': gOwnerId
  }
  if (areValidIds) {
    if (newFriends.length && removeFriends.length) {
      postData.add = gadgets.json.stringify(newFriends);
      sendRequest = true;
      postData.remove = gadgets.json.stringify(removeFriends);
    } else if (newFriends.length && !removeFriends.length) {
       postData.add = gadgets.json.stringify(newFriends);
       sendRequest = true;
    } else if (!newFriends.length && removeFriends.length) {
      postData.remove = gadgets.json.stringify(removeFriends);
      sendRequest = true;
    }
    if (sendRequest) {
      params[gadgets.io.RequestParameters.POST_DATA] =
        gadgets.io.encodeValues(postData);
      params[gadgets.io.RequestParameters.AUTHORIZATION] =
          gadgets.io.AuthorizationType.SIGNED;
      params[gadgets.io.RequestParameters.METHOD] =
                    gadgets.io.MethodType.POST;
      gadgets.io.makeRequest(BASE_URL, function(response) {
      var tplData = !response.error ?
            {message: prefs.getMsg('share_success')} :
            {message: prefs.getMsg('share_failure')};
        showServerMessage(tplData);
        var trip = getTripById(gCurrentTripsData.currentTripId);
        trip.collaborators = selectedFriends;
      }, params);
    }
  }
}

/**
 * Change class style when focus is on/out of input box.
 */
function changeClass() {
  var sharedIds = _gel('friend-finder');
  if (!sharedIds.value) {
    sharedIds.value = prefs.getMsg('add_email');
  } else {
    if (sharedIds.value == prefs.getMsg('add_email')) {
      sharedIds.value = '';
    }
    emailToshare = sharedIds.value;
  }
}

/**
 * Handles key event to call method when user types something in friends finder
 * search box.
 * @param {Object} event Window event.
 */
function handleKeyEvent(event) {
  event = event || window.event;
  var elementId = event['target'] ?
                  event['target']['id'] : event['srcElement']['id'];
  var keyCode = event.keyCode;
  // To handle escape, alphabet, backspace and delete key events.
  var ESCAPE_CODE = 27;
  var FIRST_ALPHABET_CODE = 65;
  var LAST_ALPHABET_CODE = 90;
  var BACKSPACE_CODE = 8;
  var DELETE_CODE = 46;
  var ENTER_CODE = 13;
  if (keyCode == ESCAPE_CODE) {
    if (elementId == 'search-box') {
      _gel('dropdown-' + elementId).style.display = 'none';
    } else if (elementId == 'friend-finder'){
      processAndRenderFriendsData();
    }
    return;
  } else if ((keyCode >= FIRST_ALPHABET_CODE &&
              keyCode <= LAST_ALPHABET_CODE) ||
              keyCode == BACKSPACE_CODE || keyCode == DELETE_CODE) {
    handleOtherEvents(elementId);
  }
  // To handle up arrow key press events.
  var UP_ARROW_CODE = 38;
  if (keyCode == UP_ARROW_CODE) {
    handleKeyUpEvent(elementId);
  }
  // To handle enter key event.
  if (keyCode == ENTER_CODE) {
    handleEnterEvent(elementId);
  }
  // To handle down arrow key press events.
  var DOWN_ARROW_CODE = 40;
  if (keyCode == DOWN_ARROW_CODE) {
    handleKeyDownEvent(elementId);
  }
}

/**
 * To handle other keys events like character enter, delete etc.
 * @param {string} elementId Id of element.
 */
function handleOtherEvents(elementId) {
  if (elementId == 'search-box') {
    _gel('dropdown-' + elementId).style.display = 'none';
    var value = _gel(elementId).value;
    if (value) {
      arrowPosition = 1;
      filterCategories(value, elementId);
      if (filteredCategories.length > 0) {
        _gel('option1').style.backgroundColor = '#d4e6fc';
      } else {
        arrowPosition = 0;
      }
    }
  } else if (elementId == 'friend-finder') {
    var value = document.getElementById(elementId).value;
    if (value) {
      selectValue(value);
      filterFriends(value);
    } else {
      processAndRenderFriendsData();
    }
  }
}

/**
 * To handle enter key event.
 * @param {string} elementId Id of element.
 */
function handleEnterEvent(elementId) {
  if (elementId == 'search-box') {
    if (arrowPosition) {
      var id = arrowPosition;
      if (filteredCategories.length > 0) {
        onSelectCategory(id, elementId);
      }
    }
  } else if (elementId == 'friend-finder') {
    if (_gel('friend-row-' + cursorPosition)) {
      var value = _gel('friend-row-' + cursorPosition).innerHTML;
      selectValue(value);
      cursorPosition = 0;
      _gel('collaborator-button').focus();
    }
  }
}

/**
 * To handle keyup event.
 * @param {string} elementId Id of element.
 */
function handleKeyUpEvent(elementId) {
  if (elementId == 'search-box') {
    selectCategoryOption();
  } else if (elementId == 'friend-finder') {
    selectFriendsOption();
  }
}

/**
 * To handle keydown event.
 * @param {string} elementId Id of element.
 */
function handleKeyDownEvent(elementId) {
  if (elementId == 'search-box') {
    deSelectCategoryOption();
  } else if (elementId == 'friend-finder') {
    deSelectFriendOption();
  }
}

/**
 * To select a friend from share box.
 */
function selectFriendsOption() {
  if (!_gel('friend-row-' + cursorPosition)) {
    return;
  }
  if (cursorPosition == 0) {
    return;
  } else {
    _gel('friend-row-' + cursorPosition).style.backgroundColor = '#e0ecff';
    cursorPosition--;
    _gel('friend-row-' + cursorPosition).style.backgroundColor = '#c3d9ff';
  }
}

/**
 * To unselect a friend from share box.
 */
function deSelectFriendOption() {
  if (cursorPosition == 0 && cursorPosition != filteredListCount - 1) {
    if (!_gel('friend-row-' + cursorPosition)) {
      return;
    }
    _gel('friend-row-' + cursorPosition).style.backgroundColor = '#e0ecff';
    cursorPosition++;
    _gel('friend-row-' + cursorPosition).style.backgroundColor = '#c3d9ff';
  } else if (cursorPosition == filteredListCount - 1) {
    return;
  } else {
    _gel('friend-row-' + cursorPosition).style.backgroundColor = '#e0ecff';
    cursorPosition++;
    _gel('friend-row-' + cursorPosition).style.backgroundColor = '#c3d9ff';
  }
}

/**
 * To select a category from search box.
 */
function selectCategoryOption() {
  if (arrowPosition == 1) {
    return;
  }
  _gel('option' + arrowPosition).style.backgroundColor = '#fff';
  arrowPosition--;
  _gel('option' + arrowPosition).style.backgroundColor = '#d4e6fc';
}

/**
 * To unselect a category from search box.
 */
function deSelectCategoryOption() {
  if (arrowPosition == filteredCategories.length) {
    return;
  } else if (arrowPosition == 0) {
    _gel('option1').style.backgroundColor = '#d4e6fc';
    arrowPosition++;
    return;
  }
  _gel('option' + arrowPosition).style.backgroundColor = '#fff';
  arrowPosition++;
  _gel('option' + arrowPosition).style.backgroundColor = '#d4e6fc';
  return;
}

/**
 * Provides friend search based on user input match.
 * @param {string} value User input.
 */
function filterFriends(value) {
  var filteredList = [];
  var friendList = friendsContactData;
  var pattern, matchPattern;
  for (var key in friendList) {
    pattern = friendList[key].emailId.toLowerCase();
    matchPattern = (pattern.substr(0, value.length)).toLowerCase();
    if (value.toLowerCase() == matchPattern) {
      filteredList.push(friendList[key]);
    }
  }
  filteredListCount = filteredList.length;
  processAndRenderFriendsData(filteredList);
}

/**
 * Processes the friends response and renders it to create friends tab UI.
 * @param {Array} opt_filteredList Array of friend's data object.
 */
function processAndRenderFriendsData(opt_filteredList) {
  var html = [];
  html.push('<div id="friends-table">');
  var count = 0;
  var searchResutls = _gel('friends-search-result');
  if (!opt_filteredList) {
    searchResutls.innerHTML = '';
    searchResutls.style.display = 'none';
    cursorPosition = 0;
  }
  var filteredList = opt_filteredList;
  for (var key in filteredList) {
    if (!count) {
      html.push('<div id="friend-row-', count, '" ',
        'class="friend-row" style="background-color:#c3d9ff"',
        'onmouseover="this.style.backgroundColor=\'#c3d9ff\'"',
        'onmouseout="this.style.backgroundColor=\'#e0ecff\'" ',
        'value="', filteredList[key].emailId,
        '" onclick="selectValue(\'',
      filteredList[key].emailId, '\');">', filteredList[key].emailId, '</div>');
    } else {
      html.push('<div id="friend-row-', count, '" class="friend-row" ',
        'onmouseover="this.style.backgroundColor=\'#c3d9ff\'"',
        'onmouseout="this.style.backgroundColor=\'#e0ecff\'" ',
        'value="', filteredList[key].emailId,
        '" onclick="selectValue(\'', filteredList[key].emailId, '\');">',
        filteredList[key].emailId, '</div>');
    }
    count++;
  }
  searchResutls.innerHTML = html.join('');
  searchResutls.style.display = 'block';
}

/**
 * It selects a value from drop down list and fill it in dom element.
 * @param {string} value Value which is selected.
 */
function selectValue(value) {
  _gel('friend-finder').value = value || prefs.getMsg('add_email');
  emailToshare = value;
  _gel('friends-search-result').style.display = 'none';
}

/**
 * It removes a single collaborator from list of collaborators for a trip thus
 * making this trip unavailable to user.
 */
function removeCollaborator() {
  hideViewDateDialogBox();
  var removeId = [gOwnerId];
  var postData = gadgets.io.encodeValues({
    'action': Operation.SHARE_TRIP,
    'id': gCurrentTripsData.currentTripId,
    'ldap': gOwnerId,
    'remove': gadgets.json.stringify(removeId)
  });
  var params = {};
  params[gadgets.io.RequestParameters.POST_DATA] = postData;
  params[gadgets.io.RequestParameters.AUTHORIZATION] =
      gadgets.io.AuthorizationType.SIGNED;
  params[gadgets.io.RequestParameters.METHOD] = gadgets.io.MethodType.POST;
  gadgets.io.makeRequest(BASE_URL, initializeTrip, params);
}

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
 * To show the comments section of selected item.
 * @param {string} index Index of selected trip item.
 */
function showCommentsSection(index) {
  if (selectedCommentIndex) {
    _gel('comment-' + selectedCommentIndex).style.display = 'none';
    _gel('comments-link-' + selectedCommentIndex).className = 'link-hilight';
  }
  selectedCommentIndex = index;
  _gel('comments-link-' + index).className = 'link-unhighlight';
  _gel('comment-' + index).style.display = '';
  _gel('user-comment-' + index).focus();
}

/**
 * To restrict the max length of user comment.
 * @param {Object} event Window event.
 * @param {string} index Index of selected trip item.
 * @return {boolean} Boolean flag returns true if the comment is lessthan
 *     max length else false.
 */
function checkMaxLength(event, index) {
  event = event || window.event;
  var elementId = event['target'] ?
                  event['target']['id'] : event['srcElement']['id'];
  var keyCode = event.keyCode;
  var BACKSPACE_CODE = 8;
  // To allow the user to delete text after reaching the max limit.
  if (keyCode == BACKSPACE_CODE) {
    return true;
  }
  var COMMENT_MAX_LENGTH = 250;
  var userComment = _trim(_gel('user-comment-' + index).value);
  return userComment.length < COMMENT_MAX_LENGTH;
}

/**
 * Add user comment.
 * @param {string} index Id of selected trip item.
 * @param {number} itemIndex Index of selected trip item.
 */
function addComment(index, itemIndex) {
  var userComment = hesc(_trim(_gel('user-comment-' + index).value));
  if (!userComment) {
    return;
  }
  hideCommentsSection(index);
  var trip = getTripById(gCurrentTripsData.currentTripId);
  var tripItemObj = getItemById(index);
  var params = {};
  var updatedData = {
    'comment': userComment,
    'ownerId': trip.ownerId,
    'tripId': trip.id,
    'tripItemId': index,
    'userId': gOwnerId
  };
  var postData = gadgets.io.encodeValues({
    'data': gadgets.json.stringify(updatedData),
    'action': Operation.ADD_COMMENT,
    'ldap': gOwnerId
  });
  params[gadgets.io.RequestParameters.POST_DATA] = postData;
  params[gadgets.io.RequestParameters.AUTHORIZATION] =
      gadgets.io.AuthorizationType.SIGNED;
  params[gadgets.io.RequestParameters.METHOD] =
                gadgets.io.MethodType.POST;
  var tplData;
  gadgets.io.makeRequest(BASE_URL, function(response) {
    var responseData = gadgets.json.parse(response.data);
    if (responseData.error == transResponse.ERROR) {
      tplData = {
        message: prefs.getMsg('comment_update_err')
      };
      showServerMessage(tplData);
    } else {
      tplData = {
        message: prefs.getMsg('comment_added')
      };
      showServerMessage(tplData);
      getItemComments(index, itemIndex);
    }
  }, params);
}

/**
 * To hide the comment entry section.
 * @param {number} id Index of the selected item.
 */
function hideCommentsSection(id) {
  _gel('comment-' + id).style.display = 'none';
  _gel('comments-link-' + id).className = 'link-hilight';
  _gel('user-comment-' + id).value = '';
  selectedCommentIndex = '';
}

/**
 * Function to show marker information as a popup on map.
 * @param {string} id Selected trip item id.
 * @param {number} itemIndex Selected trip item index.
 */
function showItemMarker(id, itemIndex) {
  var MAX_ZOOM_LEVEL = 13;
  var marker = gCurrentTripsData.itemMarkers[id];
  if (marker) {
    marker.openInfoWindowHtml(getPopupHtmlItem(id, itemIndex, 0));
  }
  var point, bounds = new GLatLngBounds();
  for (var j = 0, count = gTripItemDB.length; j < count; j++) {
    point = new GLatLng(gTripItemDB[j].lat, gTripItemDB[j].lng);
    bounds.extend(point);
  }
  setMapZoomLevel(bounds);
  var curntZoomLevel = gMap.getZoom();
  if (curntZoomLevel > MAX_ZOOM_LEVEL) {
    gMap.setCenter(bounds.getCenter(), MAX_ZOOM_LEVEL);
  }
}

/**
 * To set the map zoom level according to the added trip items.
 * @param {Object} bounds Bounds to be set on map.
 */
function setMapZoomLevel(bounds) {
  var MAX_ZOOM = 14;
  // Setting new zoom level to map.
  var newZoom = Math.min(gMap.getBoundsZoomLevel(bounds), MAX_ZOOM);
  var newCenter = bounds.getCenter();
  gMap.setCenter(newCenter, newZoom);
  gMap.panTo(bounds.getCenter());
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
    durationHtml.push(prefs.getMsg('day'), '&nbsp;<select style="width:28%;" ',
        'id="days_id" disabled>');
    // Loop start from 1 in order to show day1, day2.
    for (var i = 1; i <= duration; i++) {
      durationHtml.push('<option value="', i, '">', prefs.getMsg('day'),
          '&nbsp;', i, '</option>');
    }
  } else {
    durationHtml.push(prefs.getMsg('day'), '&nbsp;<select style="width:40%;" ',
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
  var index = gCurrentTripsData.currentTripId;
  var trip = getTripById(index);
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
  // gTripItemDB is declared in config_canvas.js which holds items of the trip.
  var itemsLength = gTripItemDB.length;
  for (i = 0; i < itemsLength; i++) {
    if (gTripItemDB[i].day > trip.duration) {
      gTripItemDB[i].day = 0;
      updateItemDetails(gTripItemDB[i]);
    }
    htmlContent[gTripItemDB[i].day].push(getHtmlForOneItem(gTripItemDB[i], i));
    tplData[gTripItemDB[i].day]['itemNumber'] += 1;
    addBlueMarker(gTripItemDB[i], i);
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
 * @param {number} itemIndex Selected trip item index.
 * @return {string} The required html element as a string.
 */
function getHtmlForOneItem(tripItem, itemIndex) {
  var tplHtml = _gel('tpl-unschedule-item').value;
  var imgSrc = BLANK_IMG;
  var className = '';
  if (tripItem.dataSource == Datasource.LONELY ||
      tripItem.dataSource || Datasource.CUSTOM) {
    // Checking for object property.
    var category;
    if (tripItem.category == undefined) {
      className = 'blue-pushpin';
    } else {
      category = tripItem.category.toLowerCase();
      if (!Util.isEmpty(category) && LONELY_IMG[category]) {
        className = LONELY_IMG[category].className;
      } else {
        className = 'blue-pushpin';
      }
    }
  }
  var likesMsg = tripItem.Item_thumb_up > 1 ?
                 prefs.getMsg('likes') : prefs.getMsg('like');
  if (tripItem.Item_thumb_down >= 1) {
    likesMsg += ',&nbsp;';
  }
  var dislikeMsg = tripItem.Item_thumb_down > 1 ?
                 prefs.getMsg('dislikes') : prefs.getMsg('dislike');
  var likesDislikesDisplay = (!tripItem.Item_thumb_up &&
                             !tripItem.Item_thumb_down) ? 'none' : '';
  var tplData = {
    dragItemCounter: tripItem.id,
    index: tripItem.id,
    name: tripItem.name,
    title: tripItem.name,
    contactInfo: tripItem.address,
    thumbUp: tripItem.Item_thumb_up,
    likeDisplay: tripItem.Item_thumb_up ? '' : 'none',
    dislikeDisplay: tripItem.Item_thumb_down ? '' : 'none',
    imgSrc: imgSrc,
    className: className,
    html: '',
    likesDislikesDisplay: likesDislikesDisplay,
    likesMsg: likesMsg.replace('{count}', tripItem.Item_thumb_up),
    dislikesMsg: dislikeMsg.replace('{count}', tripItem.Item_thumb_down),
    itemIndex: itemIndex
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
  var url = BASE_URL + '?action=' + Operation.GET_TRIPS + '&ldap=' + gOwnerId;
  var params = {};
  params[gadgets.io.RequestParameters.AUTHORIZATION] =
      gadgets.io.AuthorizationType.SIGNED;
  gadgets.io.makeRequest(url, function(response) {
    trips = eval(response.data);
    if (trips.error != undefined) {
      _gel('main-container') = '<div class="no_trip">' +
          prefs.getMsg('server_error') + '&nbsp;<span class="link" onclick="' +
          'javascript:handleResponse();">' + prefs.getMsg('try_again') +
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
  }, params);
}

/**
 * Function to add item by calling add dialog box method.
 * @param {number} index The index of item to be added.
 */
function addItem(index) {
  var MAX_SCHEDULED_TITLE = 35;
  var MAX_UNSCHEDULED_TITLE = 21;
  var trip = getTripById(gCurrentTripsData.currentTripId);
  var title = gCurrentTripsData.arrSearchResults[index].name;
  var titleLength = Util.isEmpty(trip.sdate) ?
                    MAX_SCHEDULED_TITLE : MAX_UNSCHEDULED_TITLE;
  // Limiting the length of title.
  title = (title.length > titleLength) ?
          title.substr(0, titleLength) + ' ...' : title;
  showAddItemDialog(title, index);
}

/**
 * To show the comments on info window.
 * @param {string} id Selected trip id.
 * @param {number} itemIndex Selected trip item index.
 * @param {number} crntCmntIndex Index of current comment.
 */
function showComment(id, itemIndex, crntCmntIndex) {
  var popUpHtml = getPopupHtmlItem(id, itemIndex, crntCmntIndex);
  if (!popUpHtml) {
    return;
  }
  gCurrentTripsData.itemMarkers[id] .openInfoWindowHtml(popUpHtml);
}

/**
 * It returns the html string for popup of an item.
 * @param {string} id Selected trip id.
 * @param {number} itemIndex Selected trip item index.
 * @param {number} crntCmntIndex Index of current comment.
 * @return {string} The html for an item as a string.
 */
function getPopupHtmlItem(id, itemIndex, crntCmntIndex) {
  var objItem = getItemById(id);
  var nxtIndex = 0, prevIndex = 0;
  var cmntOwnerName = '', date = '', comment = '', commentId = '';
  var nxtLinkClass = '', prevLinkClass = '', lstModified = '';
  if (!objItem) {
    return;
  }
  var itemComments = gTripItemDB[itemIndex].comments;
  var cmntsLength = itemComments.length;
  if (cmntsLength) {
    if (!itemComments[crntCmntIndex]) {
      return;
    }
    nxtIndex = crntCmntIndex + 1;
    prevIndex = crntCmntIndex - 1;
    cmntOwnerName = itemComments[crntCmntIndex].modifiedBy.replace(/@.*/, '');
    date = itemComments[crntCmntIndex].addedOn;
    comment = wrapText(itemComments[crntCmntIndex].comment);
    commentId = itemComments[crntCmntIndex].commentId;
    lstModified = itemComments[crntCmntIndex].modifiedBy;
  }
  var MAX_COMMENT_TEXT_HEIGHT = 200;
  var commentTextHeight = comment.length > MAX_COMMENT_TEXT_HEIGHT ?
                          'height:84px' : '';
  var className = '';
  var imgUrl = MARKER_IMG;
  var category = objItem.category.toLowerCase();
  if (objItem.dataSource == Datasource.LONELY ||
      objItem.dataSource == Datasource.CUSTOM) {
    imgUrl = BLANK_IMG;
    className = LONELY_IMG[category] ?
        LONELY_IMG[category].className : 'blue-pushpin';
  }
  var likesMsg = (objItem.Item_thumb_up > 1 || objItem.Item_thumb_up == 0) ?
                 prefs.getMsg('likes') : prefs.getMsg('like');
  var dislikesMsg =
      (objItem.Item_thumb_down > 1 || objItem.Item_thumb_down == 0) ?
      prefs.getMsg('dislikes') : prefs.getMsg('dislike');
  var tplHtml = _gel('tpl-item-info').value;
  var moreLinkText = (objItem.dataSource == Datasource.GOOGLE) ?
      prefs.getMsg('more_info_google_maps') : prefs.getMsg('more_link');
  var tplData = {
    ownerName: objItem.item_owner,
    reviews: objItem.review,
    reviewsLabel: prefs.getMsg('review'),
    displayLink: objItem.weburl ? '' : 'none',
    moreLinkText: moreLinkText,
    title: _unesc(objItem.name),
    imgUrl: imgUrl,
    className: className,
    webUrl: objItem.weburl || '',
    itemId: id,
    itemIndex: itemIndex,
    thumbUpMsg: likesMsg.replace('{count}', objItem.Item_thumb_up || 0),
    thumbDownMsg: dislikesMsg.replace('{count}', objItem.Item_thumb_down || 0),
    showCommentsBlock: cmntsLength ? '' : 'none',
    prevIndex: prevIndex,
    nxtIndex: nxtIndex,
    nxtLinkClass: crntCmntIndex + 1 >= cmntsLength ?
                  'nxt-prev-link-disable' : 'cmntlink-enable',
    prevLinkClass: crntCmntIndex - 1 < 0 ?
                  'nxt-prev-link-disable' : 'cmntlink-enable',
    cmntOwnerName: prefs.getMsg('by_owner').replace('{OWNER}', cmntOwnerName),
    displayNxtPrvLink: cmntsLength > 1 ? '' : 'none',
    date: date.replace(/,.*/, ''),
    comment: comment,
    commentId: commentId,
    commentTextHeight: commentTextHeight,
    displayTrashBtn: lstModified == gOwnerId ? '' : 'none',
    commentIndexInfo: prefs.getMsg('page_no')
                      .replace('{currentIndex}', crntCmntIndex + 1)
                      .replace('{totalComments}', cmntsLength)
  };

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

  tplData.contactInfo = address1 ? getContactInfo(address1, zip) : '';
  return Util.supplant(tplHtml, tplData);
}

/**
 * To delete comment.
 * @param {string} id Trip item id.
 * @param {number} itemIndex Index of selected trip item.
 * @param {string} commentId Id of comment which is to be deleted.
 */
function deleteComment(id, itemIndex, commentId) {
  var params = {};
  params[gadgets.io.RequestParameters.AUTHORIZATION] =
      gadgets.io.AuthorizationType.SIGNED;
  var url = BASE_URL + '?action=' + Operation.DELETE_COMMENT + '&id=' +
      commentId + '&ldap=' + gOwnerId;
  gadgets.io.makeRequest(url, function(response) {
    var tplData = !response.error ?
            {message: prefs.getMsg('comment_deleted')} :
            {message: prefs.getMsg('comment_delete_err')};
      showServerMessage(tplData);
      getItemComments(id, itemIndex);
  }, params);
}

/**
 * To get the comments for selected trip item.
 * @param {string} id Selected trip item id.
 * @param {number} itemIndex Index of selected trip item.
 */
function getItemComments(id, itemIndex) {
  var comments, obj;
  var params = {};
  params[gadgets.io.RequestParameters.AUTHORIZATION] =
      gadgets.io.AuthorizationType.SIGNED;
  var url = BASE_URL + '?action=' + Operation.GET_COMMENTS + '&id=' +
      id + '&ldap=' + gOwnerId;
  gadgets.io.makeRequest(url, function(response) {
    gTripItemDB[itemIndex].comments = [];
    comments = eval(response.data);
    for (var i = 0, count = comments.length; i < count; i++) {
      obj = {};
      obj.comment = comments[i].comment;
      obj.modifiedBy = comments[i].lastModifiedBy;
      obj.addedOn = comments[i].addedOn;
      obj.commentId = comments[i].key;
      gTripItemDB[itemIndex].comments.push(obj);
    }
    gTripItemDB[itemIndex].comments.sort(function(a, b) {
      return new Date(b.addedOn) - new Date(a.addedOn);
    });
    showItemMarker(id, itemIndex);
  }, params);
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
      address = address.substr(0, sIndex);
      address += '<br>' + address.substr(sIndex + 1);
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
  var objTrip = getTripById(gCurrentTripsData.currentTripId);
  // Update trip name.
  var tripName = _trim(_gel('tripNameEdit').value);
  if (!tripName) {
    return;
  }
  var tripDescription = _trim(_gel('tripDescription').value);
  // We are storing maximum of 500 characters in description.
  if (tripDescription.length > 495) {
    var errMsg = _gel('warning-msg');
    errMsg.style.display = 'block';
    errMsg.innerHTML = prefs.getMsg('description_length');
    return;
  }
  hideDialog();
  var params = {};
  // Updating new trip name in the object.
  objTrip.name = tripName;
  objTrip.description = tripDescription;
  var updatedData = {
    'name': objTrip.name,
    'description': objTrip.description,
    'location': objTrip.loc,
    'latitude': objTrip.lat,
    'longitude': objTrip.lng,
    'ownerName': objTrip.ownerName,
    'duration': objTrip.duration,
    'ownerId': objTrip.ownerId,
    'key': objTrip.id,
    'startDate': Util.isEmpty(objTrip.sdate) ?
                 null : getDateObject(objTrip.sdate)
  };
  var postData = gadgets.io.encodeValues({
    'data': gadgets.json.stringify(updatedData),
    'action': Operation.UPDATE_TRIP,
    'ldap': gOwnerId
  });
  params[gadgets.io.RequestParameters.POST_DATA] = postData;
  params[gadgets.io.RequestParameters.AUTHORIZATION] =
      gadgets.io.AuthorizationType.SIGNED;
  params[gadgets.io.RequestParameters.METHOD] =
                gadgets.io.MethodType.POST;
  var tplData;
  gadgets.io.makeRequest(BASE_URL, function(response) {
    var responseData = gadgets.json.parse(response.data);
    if (responseData.error == transResponse.ERROR) {
      tplData = {
        message: prefs.getMsg('tripname_update_err')
      };
      showServerMessage(tplData);
    } else {
      tplData = {
        message: prefs.getMsg('updated_name')
      };
      showServerMessage(tplData);
      objTrip.name = tripName;
      tripDB[objTrip.id] = objTrip;
      _gel('trip-name').innerHTML = tripName;
    }
  }, params);
}

/**
 * It returns the html string for popup of an item from lonely planet search.
 * @param {number} index Index of item.
 * @return {string} The HTML for an item as a string.
 */
function getPopupHtmlLP(index) {
  var icon = String.fromCharCode('A'.charCodeAt(0) + index);
  selectedItemId = 'resultPopup' + index;
  var tplHtml = _gel('tpl-item-info-lp').value;
  var itemData = gCurrentTripsData.arrSearchResults[index];
  var tplData = {
    index: index,
    reviews: itemData.review,
    dragMessage: prefs.getMsg('dragme'),
    title: _unesc(itemData.name),
    location: _esc(getTripById(gCurrentTripsData.currentTripId).loc),
    contactInfo: itemData.address,
    lonelyMore: itemData.lonelyLink || '',
    displayLPMore: itemData.lonelyLink ? '' : 'none'
  };

  if (itemData.dataFetched) {
    tplData.displayReview = '';
    // Reviews url.
    var review = itemData.review || '';
    var pattern = '&#160;&#160;';
    // Check for empty review.
    if (!review || pattern == review) {
      tplData.reviews = prefs.getMsg('no reviews');
    }
    tplData.contactInfo = itemData.address;
  } else {
    tplData.displayReview = 'none';
    tplData.contactInfo = _gel('tpl-loading').value;
  }
  return Util.supplant(tplHtml, tplData);
}

/**
 * It returns the html string for popup of an item from google local search.
 * @param {number} index Index of item.
 * @return {string} The HTML for an item as a string.
 */
function getPopupHtmlGoogle(index) {
  var icon = String.fromCharCode('A'.charCodeAt(0) + index);
  selectedItemId = 'resultPopup' + index;
  var tplHtml = _gel('tpl-item-info-google').value;
  var itemData = gCurrentTripsData.arrSearchResults[index];
  var tplData = {
    index: index,
    dragMessage: prefs.getMsg('dragme'),
    title: _unesc(itemData.name),
    location: _esc(getTripById(gCurrentTripsData.currentTripId).loc),
    detailUrl: itemData.link + '&dtab=1&oi=md_structdata',
    photosUrl: itemData.link + '&dtab=5&oi=md_photos&sa=X',
    title: _unesc(itemData.name),
    webUrl: itemData.review || '',
    reviewsUrl: itemData.link + '%3Dmd_reviews&dtab=2&oi=md_reviews&sa=X',
    displayImage: itemData.imgurl ? '' : 'none',
    imageUrl: itemData.imgurl || '',
    contactInfo: itemData.address,
    location: _esc(getTripById(gCurrentTripsData.currentTripId).loc)
  };
  return Util.supplant(tplHtml, tplData);
}

/**
 * Increments zoom level by one zoom level and centers the map to selected item.
 * @param {number} index Index of item.
 */
function zoomIn(index) {
  currentMarker = {
    marker: gCurrentTripsData.searchMarkers[index],
    index: index,
    icon: gCurrentTripsData.searchMarkerIcons[index]
  };
  var MAX_ZOOM = 18;
  var newZoom = gMap.getZoom();
  if (newZoom > MAX_ZOOM) {
    return;
  }
  newZoom = Math.min(newZoom + 2, MAX_ZOOM);
  gMap.setCenter(gCurrentTripsData.searchMarkers[index].getLatLng(), newZoom);
  showMarkerInfo(index);
}

/**
 * Displays the info when clicked on the marker.
 * @param {number} opt_index Index of the item selected.
 */
function showMarkerInfo(opt_index) {
  if (opt_index) {
    currentMarker = {
      marker: gCurrentTripsData.searchMarkers[opt_index],
      index: opt_index,
      icon: gCurrentTripsData.searchMarkerIcons[opt_index]
    };
  }
  var currMarker = currentMarker.marker;
  if (!currMarker || currentMarker.index == -1) {
    return;
  }

  // Show the selected item in the navigation section.
  document.getElementById('search-results-navigation').style.display = 'block';
  document.getElementById('current-item-shown').innerHTML =
      gCurrentTripsData.arrSearchResults[currentMarker.index].name;
  var html;
  if (gCurrentTripsData.arrSearchResults[currentMarker.index].dataSource ==
      Datasource.GOOGLE) {
    html = getPopupHtmlGoogle(currentMarker.index);
  } else {
    html = getPopupHtmlLP(currentMarker.index);
  }
  currMarker.openInfoWindowHtml(html, {maxWidth: '400'});
}

/**
 * To enable user entry category text box, if the selected category is others.
 */
function enableUserEntry() {
  var OTHERS_CATEGORY_INDEX = 7;
  var category = _gel('item_category');
  _gel('user_entry_category').style.display =
      category.selectedIndex == OTHERS_CATEGORY_INDEX ? '' : 'none';
}

/**
 * Create a new item in a trip.
 */
function createNewItem() {
  var OTHERS_CATEGORY_INDEX = 7;
  var address = _gel('item-description').value;
  var itemName = _gel('item-name').value;
  var wrngMsg = _gel('warning-msg');
  var phnNo = _gel('itemPhoneNo').value;
  // To check whether mandatory field are filled or not.
  if (!address || !itemName) {
    wrngMsg.style.visibility = 'visible';
    wrngMsg.innerHTML = prefs.getMsg('name_addr_empty');
    return;
  }
  if (phnNo) {
    if (checkPhone(phnNo)) {
      address += ', ' + prefs.getMsg('phn_no') + ' - ' + phnNo;
    } else {
      wrngMsg.style.visibility = 'visible';
      wrngMsg.innerHTML = prefs.getMsg('valid_phone');
      return;
    }
  }
  var category = _gel('item_category');
  var selectedCategory = category.selectedIndex == OTHERS_CATEGORY_INDEX ?
      _trim(_gel('user_entry_category').value) :
      category[category.selectedIndex].value;
  var trip = getTripById(gCurrentTripsData.currentTripId);
  if (geocoder) {
    geocoder.getLocations(address, function(result) {
      if (result.Status.code == RESPONSE_SUCCESS && result.Placemark.length) {
        var place = result.Placemark[0];
        var point = place.Point.coordinates;
        var tripItem = getItemObject();
        tripItem.ownerName = gViewer;
        tripItem.ownerId = gOwnerId;
        tripItem.name = itemName;
        tripItem.address = address ? Util.stripHtml(address) : '';
        tripItem.lat = point[1];
        tripItem.lng = point[0];
        tripItem.isCustom = true;
        tripItem.dataSource = Datasource.CUSTOM;
        tripItem.tripName = trip.name;
        tripItem.category = selectedCategory;
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
 * Validates a phone number.
 * @param {string} phoneNum Phone number to be validated.
 * @return {boolean} Returns true if number is valid else returns false.
 */
function checkPhone(phoneNum) {
  var phone = '^([\+][0-9]{1,3}([ \.\-])?)?([\(]{1}[0-9]{3}[\)])?([0-9A-Z \.\-]{1,32})((x|ext|extension)?[0-9]{1,4}?)$';
  if (phoneNum.match(phone)) {
    return true;
  } else {
    return false;
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
 * Returns item address if available else its review.
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
function initializeTrip() {
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
  handleResponse();
  _IG_AdjustIFrameHeight();
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
  friendsData.push(gOwnerId);
  // Check to decide whether we have to move to trip page or trips listing.
  if (!isGetData) {
    showListings(true, false, false);
  } else {
    isGetData = false;
    fetchTripsData();
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
  var url = BASE_URL + '?action=' + Operation.GET_TRIPS + '&ldap=' + gOwnerId;
  var params = {};
  params[gadgets.io.RequestParameters.AUTHORIZATION] =
      gadgets.io.AuthorizationType.SIGNED;
  gadgets.io.makeRequest(url, function(response) {
    var responseData = eval(response.data);
    if (!responseData.length) {
      showListings(true, false, false);
    } else if (responseData[0].error != transResponse.SUCCESS) {
      trips = eval(response.data);
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
  }, params);
}

/**
 * Navigate to details page for a trip.
 * @param {string} tripId The trip id.
 */
function exploreTrip(tripId) {
  selectedCommentIndex = '';
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
  trip.accuracy = trip.accuracy || 5;
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
  var index = gCurrentTripsData.currentTripId;
  var trip = getTripById(index);
  var tripId = gCurrentTripsData.currentTripId;
  var params = {};
  params[gadgets.io.RequestParameters.AUTHORIZATION] =
      gadgets.io.AuthorizationType.SIGNED;
  var url = BASE_URL + '?action=' + Operation.GET_TRIP_ITEMS + '&id=' +
      tripId + '&ldap=' + gOwnerId;
  gadgets.io.makeRequest(url, function(response) {
    gTripItemDB = [];
    var tripItems = eval(response.data);
    var tripItemsLength = tripItems.length;
    for (var i = 0; i < tripItemsLength; i++) {
      var objItem = getItemObject();
      objItem.id = tripItems[i].key;
      objItem.name = _unesc(tripItems[i].name).replace(/\?/g, '\'');
      if (tripItems[i].ownerName) {
        objItem.item_owner = _unesc(tripItems[i].ownerName);
      } else if (tripItems[i].ownerId) {
        objItem.item_owner = tripItems[i].ownerId;
      } else {
        objItem.item_owner = '';
      }
      objItem.address = _unesc(tripItems[i].address);
      objItem.lat = tripItems[i].latitude;
      objItem.lng = tripItems[i].longitude;
      objItem.category = tripItems[i].category;
      objItem.day = tripItems[i].startDay;
      objItem.link = tripItems[i].searchResultUrl;
      objItem.weburl = tripItems[i].searchResultUrl;
      objItem.imgurl = tripItems[i].imageUrl;
      objItem.dataSource = tripItems[i].dataSource;
      objItem.isCustom = tripItems[i].custom ? tripItems[i].custom : false;
      if (tripItems[i].thumbsUp) {
        objItem.Item_thumb_up = tripItems[i].thumbsUp;
      }
      if (tripItems[i].thumbsDown) {
        objItem.Item_thumb_down = tripItems[i].thumbsDown;
      }
      gTripItemDB.push(objItem);
    }
    putItemsData();
  }, params);
}

/**
 * Used to filter categories according to the entered character in name text
 * box.
 * @param {string} value Text box value.
 * @param {string} textBoxId Text box id.
 */
function filterCategories(value, textBoxId) {
  // Maximum number of categories to be displayed in dropdown.
  var MAX_CATEGORIES = 6, obj;
  filteredCategories = [];
  for (var i in categories) {
    var category = categories[i].name;
    if (category.toLowerCase().indexOf(value.toLowerCase()) != -1) {
      obj = {'name': categories[i].name, 'type': categories[i].type};
      filteredCategories.push(obj);
    }
    if (filteredCategories.length == MAX_CATEGORIES) {
      break;
    }
  }
  if (filteredCategories.length > 0) {
    enableCategories(filteredCategories, textBoxId);
  }
}

/**
 * Used to enable categories list dropdown box on click of text box.
 * @param {Array} categoriesNames Collection of categories names.
 * @param {string} textBoxId Text box id.
 */
function enableCategories(categoriesNames, textBoxId) {
  var value = _gel(textBoxId).value, name;
  var tableData = _gel('dropdown-' + textBoxId);

  var html = [
    '<table id="categories-dropdown">'
  ];
  var rg = new RegExp(value, 'ig');
  for (var i = 0, j = categoriesNames.length; i < j; i++) {
    name = categoriesNames[i].name.replace(rg, '<b>' + value + '</b>');
    html.push('<tr><td class="list-categories" ' +
      'style="cursor:pointer;" id="option', (i + 1), '" ' +
      'onmouseover="this.style.backgroundColor=\'#d4e6fc\'"' +
      ' onmouseout="this.style.backgroundColor=\'#fff\'" ' +
      'onclick="onSelectCategory(', i + 1, ', \'', textBoxId, '\');">',
      name + ' (' + categoriesNames[i].type + ')' +
      '</td></tr>');
  }
  html.push('</table>');
  tableData.style.display = 'block';
  var searchBox = _gel('search-box');
  var width = searchBox.offsetWidth;
  var left = Util.getElementPosition(searchBox).x;
  tableData.innerHTML = html.join('');
  var table = _gel('categories-dropdown');
  table.style.width = width + 'px';
  _gel('dropdown-search-box').style.left = left + 'px';
}

/**
 * Used to fill name text box with selected category.
 * @param {number} index Index of the element selected.
 * @param {string} textBoxId Id of the textbox.
 */
function onSelectCategory(index, textBoxId) {
  arrowPosition = 0;
  if (_gel(textBoxId).value) {
    var selectedName = filteredCategories[index - 1].name;
    var tableData = _gel('dropdown-' + textBoxId);

    _gel(textBoxId).value = rmhtml(selectedName);
    tableData.style.display = 'none';
    tableData.innerHTML = '';
  }
}

/**
 * Used to hide dropdown.
 * @param {string} containerId Id of the friend's name text box.
 */
function hideDropDown(containerId) {
  if (containerId == 'dropdown-search-box') {
    arrowPosition = 0;
    this.className = 'google-search';
  } else {
    cursorPosition = 0;
  }
  // To delay the onblur call on select of dropdown.
  setTimeout(function() {
    if (_gel(containerId)) {
      _gel(containerId).innerHTML = '';
    }
  }, 500);
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
  var params = {};
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
          var bounds = result.Placemark[0].ExtendedData.LatLonBox;
          var params = {};
          params[gadgets.io.RequestParameters.METHOD] =
              gadgets.io.MethodType.POST;
          var postData = {
            'ownerName': gViewer,
            'ownerId': gOwnerId,
            'name': location + ' ' + prefs.getMsg('trip'),
            'location': location,
            'latitude': point[1],
            'longitude': point[0],
            'duration': objTrip.duration,
            'eastLongitude': bounds.east,
            'northLatitude': bounds.north,
            'southLatitude': bounds.south,
            'westLongitude': bounds.west
          };
          var dataObj = gadgets.io.encodeValues({
            'data': gadgets.json.stringify(postData),
            'action': Operation.ADD_TRIP,
            'ldap': gOwnerId
          });
          params[gadgets.io.RequestParameters.POST_DATA] = dataObj;
          params[gadgets.io.RequestParameters.AUTHORIZATION] =
              gadgets.io.AuthorizationType.SIGNED;
          var url = BASE_URL;
          var tplData;
          gadgets.io.makeRequest(url, function(response) {
            if (response.error) {
              tplData = {
                message: Util.supplant(prefs.getMsg('trip_create_err'),
                                       {name: location})
              };
              showServerMessage(tplData);
              return;
            }
            tplData = {
              message: Util.supplant(prefs.getMsg('trip_created'),
                                {name: location})
            };
            showServerMessage(tplData);
            objTrip.id = response.data;
            objTrip.name = location + prefs.getMsg('trip');
            objTrip.ownerName = gViewer;
            objTrip.loc = location;
            objTrip.lat = point[1];
            objTrip.lng = point[0];
            objTrip.accuracy = accuracy;
            objTrip.collaborators = gOwnerId;
            objTrip.ownerId = [gOwnerId];
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

/**
 * Method to show markers on the map.
 * @param {Object} objItem The object for which popup is shown.
 * @param {number} itemIndex Index of selected trip item.
 */
function addBlueMarker(objItem, itemIndex) {
  var blueIcon = new GIcon(G_DEFAULT_ICON);
  blueIcon.shadow = SHADOW_IMG;
  blueIcon.iconSize = new GSize(32, 34);
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
    getItemComments(objItem.id, itemIndex);
  });
}

/**
 * Selects previous element in navigation of search items.
 */
function selectPrevItem() {
  var index = currentMarker.index - 1;
  index = index < 0 ? gCurrentTripsData.arrSearchResults.length - 1 : index;
  hightLightSelectedItem(index);
}

/**
 * Selects next element in navigation of search items.
 */
function selectNextItem() {
  var index = currentMarker.index + 1;
  index = (index == gCurrentTripsData.arrSearchResults.length) ? 0 : index;
  hightLightSelectedItem(index);
}

/**
 * Selects given element in navigation of search items anc shows the infowindow.
 * @param {number} index Index of the selected element.
 */
function hightLightSelectedItem(index) {
  currentMarker = {
    marker: gCurrentTripsData.searchMarkers[index],
    index: index,
    icon: gCurrentTripsData.searchMarkerIcons[index]
  };
  showMarkerInfo(index);
  if (gCurrentTripsData.arrSearchResults[currentMarker.index].dataSource ==
      Datasource.LONELY) {
    markerDragStart();
  }
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
