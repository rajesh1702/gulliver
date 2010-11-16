/**
 * @fileoverview Code contains gadget featured services like mailing a trip,
 * printing trip, exporting trip on google maps, voting for trips or trip items.
 * @author
 */

/**
 * Opens mail sending dialog box.
 */
function openMailDialog() {
  // Close if any popup window is there.
  closeInfoWindow();
  showDialog(_gel('tpl-mail').value);
  _gel('emailId').focus();
}

/**
 * Send mail request to server.
 */
function sendMail() {
  var MAX_EMAIL_ADDRESS = 5;
  // To be extracted using open-social.
  var ownerMailId = '';
  var tripItem = gTripItemDB;
  var mailErr = _gel('mail-error');
  if (!tripItem.length) {
    mailErr.style.display = 'block';
    mailErr.innerHTML = prefs.getMsg('no_items_mail');
    return;
  }
  var postData;
  var emailId = _gel('emailId');
  if (Util.validateEmailId(emailId.value)) {
    var objTrip = getTripById(gCurrentTripsData.currentTripId);
    var toList = [];
    // Separating email ids on basis of separator.
    var tempArr = emailId.value.split(',');
    if (tempArr.length > MAX_EMAIL_ADDRESS) {
      mailErr.style.display = 'block';
      mailErr.innerHTML = prefs.getMsg('not_more') + MAX_EMAIL_ADDRESS + '  ' +
          prefs.getMsg('email_allow');
    } else {
      for (var key in tempArr) {
        toList.push(tempArr[key]);
      }
      var description = _gel('mailDescp').value;
      description = '<pre class="mail-style">' + description + '</pre>';
      var params = {};
      var sortOrder =
          Util.isEmpty(objTrip.sdate) && Util.isEmpty(objTrip.edate) ?
          'day' : 'date';

      var dataObj = gadgets.io.encodeValues({
        'message': gadgets.json.stringify(description),
        'action': Operation.MAIL_TRIP,
        'recipients': gadgets.json.stringify(toList),
        'id': gCurrentTripsData.currentTripId,
        'ldap': gOwnerId
      });
      params[gadgets.io.RequestParameters.POST_DATA] = dataObj;

      params[gadgets.io.RequestParameters.METHOD] = gadgets.io.MethodType.POST;
      params[gadgets.io.RequestParameters.AUTHORIZATION] =
          gadgets.io.AuthorizationType.SIGNED;
      var tplData;
      gadgets.io.makeRequest(BASE_URL, function(response) {
        var responseData = gadgets.json.parse(response.data);
        var serverMsg;
        if (responseData.error == transResponse.ERROR) {
          serverMsg = prefs.getMsg('server_error') + ' ' +
              prefs.getMsg('mail_not');
        } else {
          serverMsg = prefs.getMsg('mail_success');
        }
        tplData = {message: serverMsg};
        showServerMessage(tplData);
      }, params);
      hideDialog();
    }
  } else {
    mailErr.style.display = 'block';
    mailErr.innerHTML = prefs.getMsg('wrong_email');
    emailId.focus();
  }
}

/**
 * Function to display print items dialog.
 * @return {string} printHtml The print dialogue content as a string.
 */
function showPrintDialog() {
  scheduledItems = unScheduledItems = 0;
  var trip = getTripById(gCurrentTripsData.currentTripId);
  var currentDate = new Date();
  var currentDay = currentDate.getDay();
  currentDay = DateLib.longWeekDays[currentDay];
  currentDate = DateLib.formatDate(getDateString(currentDate), '{MM} {d}, {Y}');

  var printHtml = _gel('tpl-print-header').value;
  var tplData = {
    lat: trip.lat,
    lng: trip.lng,
    accuracy: trip.accuracy,
    tripName: trip.name,
    tripDateString: getTripDates(trip),
    currentDay: currentDay,
    currentDate: currentDate
  };
  printHtml = Util.supplant(printHtml, tplData);
  // Sort the itemArray.
  var tripItems = gTripItemDB;
  var tripItemsLength = tripItems.length;
  sortCol = enTripCol.DAY;
  isAscending = 1;
  sortByColumn(tripItems);

  printHtml = getPrintHtml(trip, tripItems, printHtml);
  showDialog(printHtml);
  var noItemsHtml = '<div style="font-weight:bold;padding-top:40px;" ' +
                  'align="center">%MESSAGE%</div>';
  if (!tripItems.length) {
    noItemsHtml =
        noItemsHtml.replace(/%MESSAGE%/, prefs.getMsg('no_items_added'));
     _gel('itemDetails').innerHTML = noItemsHtml;
  } else if (!scheduledItems) {
    noItemsHtml =
        noItemsHtml.replace(/%MESSAGE%/, prefs.getMsg('no_scheduled_items'));
    _gel('scheduled-item-details').innerHTML = noItemsHtml;
  } else if (!unScheduledItems) {
    noItemsHtml =
        noItemsHtml.replace(/%MESSAGE%/, prefs.getMsg('no_unscheduled_items'));
    _gel('unscheduled-item-details').innerHTML = noItemsHtml;
  }
  return printHtml;
}

/**
 * Returns html for printing trip items.
 * @param {Object} trip Trip object.
 * @param {Array} tripItems Array of trip items.
 * @param {string} printHtml Html for print.
 * @return {string} The required html for printing.
 */
function getPrintHtml(trip, tripItems, printHtml) {
  var currentItemDay, itemStartDate, itemDate;
  var tripItemsLength = tripItems.length;
  var unscheduledHtml = _gel('tpl-unscheduled-print').value;
  for (var i = 0; i < tripItemsLength; i++) {
    if (!Util.isEmpty(trip.sdate)) {
      if (tripItems[i].day) {
        itemDate =
            DateLib.addDaysToDate(trip.sdate,
                                  tripItems[i].day - 1,
                                  '{m}/{d}/{Y}');
        scheduledItems = 1;
        if (itemDate != itemStartDate) {
          printHtml += '</ul><ul style="color:#000"><b>' + itemDate + '</b>';
          itemStartDate = itemDate;
        }
        printHtml += getItemAddressHtml(tripItems[i]);
        // If current item date is equal to itemStartDate, its entry will be
        // put in next line.
        if (tripItems[i].sdate == itemStartDate) {
          printHtml += '<br>';
        }
      } else {
        unScheduledItems = 1;
        unscheduledHtml += '<ul style="font-size:13px">' +
            _unesc(tripItems[i].name) + '<div class="print-address">';
        // If address is available for an item put its address into html
        // else review.
        var address = Util.isEmpty(tripItems[i].address) ?
            _unesc(tripItems[i].review) : tripItems[i].address;
        unscheduledHtml += address;
        unscheduledHtml += '</div></ul>';
      }
    } else { // main else
      if (tripItems[i].day) {
        if (currentItemDay != tripItems[i].day) {
          printHtml += '</ul><ul><b>Day ' + tripItems[i].day + '</b>';
          currentItemDay = tripItems[i].day;
        }
        scheduledItems = 1;
        printHtml += getItemAddressHtml(tripItems[i]);
        if (tripItems[i].day == currentItemDay) {
          printHtml += '<br>';
        }
      } else {
        unScheduledItems = 1;
        unscheduledHtml += '<ul><b>' + _unesc(tripItems[i].name) + '</b>' +
            '<div style="color:#000;padding-left:15px;">';
        // If address is available for an item its put into html else review.
        var address = Util.isEmpty(tripItems[i].address) ?
            _unesc(tripItems[i].review) : tripItems[i].address;
        unscheduledHtml += address;
        unscheduledHtml += '</div></ul>';
      }
    }
  }
  unscheduledHtml += '</div></div><br><br>';
  printHtml += unscheduledHtml + '</div></div>';
  return printHtml;
}

/**
 * Method to export trip items on maps.google.com.
 */
function exportTripsOnMap() {
  var trip = getTripById(gCurrentTripsData.currentTripId);
  var tripId = gCurrentTripsData.currentTripId;
  var post_data = gadgets.io.encodeValues({
    'id': tripId
  });
  var params = {};
  params[gadgets.io.RequestParameters.METHOD] = gadgets.io.MethodType.POST;
  params[gadgets.io.RequestParameters.POST_DATA] = post_data;

  // Fix for submission issue in chrome browser if we click multiple time.
  window.open('', 'google');

  _gel('export-map-query').value =
      BASE_URL + '/exportAllTripItems?trip_id=' + tripId;
  var form = _gel('export-map-form');
  form.target = 'google';
  form.submit();
}

/**
 * Perform activity on item if any of the option is selected.
 * @param {string} id Id of item.
 * @param {string} activityType Type of activity.
 */
function actionOnItemActivity(id, activityType) {
  if (activityType == prefs.getMsg('delete')) {
    createDeleteItemBox(id, 1);
  } else {
    postActivity(id, activityType);
  }
}

/**
 * Posting like or dislike activity for a trip or an item.
 * @param {number} index Id of an item.
 * @param {number} activityType Id of an item. If its value is -1 this means
 *     its a trip.
 */
function postActivity(index, activityType) {
  var params = {};
  var postData;
  var trip = getTripById(gCurrentTripsData.currentTripId);
  trip_thumb_up = trip_thumb_up || trip.thumb_up;
  trip_thumb_down = trip_thumb_down || trip.thumb_down;
  var tripItem = getItemById(index);
  if (index != -1) { // Voting for a trip item.
      var upCount = tripItem.Item_thumb_up;
      var downCount = tripItem.Item_thumb_down;
      var itemVotes = updateItemVotes(activityType, upCount, downCount);
      var updatedData = {
        'key': tripItem.id,
        'thumbsUp': itemVotes.up,
        'thumbsDown': itemVotes.down,
        'tripId': gCurrentTripsData.currentTripId,
        'name': tripItem.name,
        'startDay': tripItem.day,
        'latitude': tripItem.lat,
        'longitude': tripItem.lng,
        'address': tripItem.address,
        'ownerName': tripItem.item_owner,
        'dataSource': tripItem.dataSource,
        'category': tripItem.category,
        'searchResultUrl': tripItem.weburl
      };
      postData = gadgets.io.encodeValues({
        'data': gadgets.json.stringify(updatedData),
        'action': Operation.UPDATE_TRIP_ITEM,
        'ldap': gOwnerId
      });
  } else { // Voting for a trip.
    if (index == -1) {
      updateTripVotes(activityType);
      var updatedData = {
        'key': trip.id,
        'thumbsUp': trip_thumb_up || 0,
        'thumbsDown': trip_thumb_down || 0,
        'name': trip.name,
        'duration': trip.duration,
        'startDate': Util.isEmpty(trip.startDate) ?
                     null : getDateObject(trip.startDate),
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
    }
  }
  params[gadgets.io.RequestParameters.AUTHORIZATION] =
      gadgets.io.AuthorizationType.SIGNED;
  params[gadgets.io.RequestParameters.METHOD] = gadgets.io.MethodType.POST;
  params[gadgets.io.RequestParameters.POST_DATA] = postData;
  gadgets.io.makeRequest(BASE_URL, function(response) {
    var responseData = gadgets.json.parse(response.data);
    var serverMsg, tplData;
    if (responseData.error == transResponse.ERROR) {
      serverMsg = prefs.getMsg('activity_error');
      tplData = {message: serverMsg};
      showServerMessage(tplData);
      return false;
    } else if (index == -1) {
      serverMsg = prefs.getMsg('voting_success') + ' ' + _unesc(trip.name);
      tplData = {message: serverMsg};
      showServerMessage(tplData);
    }
    if (index != -1) { // Fetching updated data for trip items.
      fetchAllItems();
    } else {
      _gel('thum_up').innerHTML = trip_thumb_up || 0;
      _gel('thum_down').innerHTML = trip_thumb_down || 0;
    }
  }, params);
  hideViewDateDialogBox();
}

/**
 * Get trip votes count.
 * @param {boolean} activityType Vote up or vote down.
 */
function updateTripVotes(activityType) {
  if (activityType) {
    trip_thumb_up = getVotesCount(trip_thumb_up);
  } else {
    trip_thumb_down = getVotesCount(trip_thumb_down);
  }
}

/**
 * Gets the vote count for a trip or an item of a trip.
 * @param {number} voteCount Current vote count.
 * @return {number} New vote count.
 */
function getVotesCount(voteCount) {
  return Util.isEmpty(voteCount) ? 1 : voteCount + 1;
}

/**
 * Function to print trip  itinerary.
 */
function printTripItems() {
  _gel('print-content').innerHTML = _gel('print-dialog').innerHTML;
  window.print();
  hideCalender('item-calendar');
  hideDialog();
}

/**
 * Function to show map on print preview dialog.
 * @param {number} lat The latitude for trip.
 * @param {number} lng The longitude for trip.
 * @param {number} accuracy Accuracy on map.
 */
function showStaticMap(lat, lng, accuracy) {
  var mapContent = _gel('staticMap');
  if (_gel('print_Map').checked) {
    mapContent.style.display = 'block';
    mapContent.style.height = '400px';
    mapContent.style.width = '500px';
    var printMap = new GMap2(mapContent);
    printMap.setCenter(new GLatLng(lat, lng), tripAccuracy[accuracy]);
    printMap.panTo(printMap.getBounds().getCenter());
  } else {
    _gel('print-dialog').scrollTop = 0;
    mapContent.style.display = 'none';
  }
}

/**
 * Shows the popup window to vote for a trip.
 * @param {number} id Id of trip.
 */
function showVotingDialog(id) {
  // To hide the info window, if opened.
  closeInfoWindow(false);
  var tplHtml = _gel('tpl-item-vote').value;
  var tplData = {id: id};
  showDialog(Util.supplant(tplHtml, tplData));
}

/**
 * Get item votes count.
 * @param {string} activityType Type of activity like vote up or down.
 * @param {number} upCount Number of up votes.
 * @param {number} downCount Number of up votes.
 * @return {Object} Vote count for an item.
 */
function updateItemVotes(activityType, upCount, downCount) {
  var itemVote = {
    up: '',
    down: ''
  };
  if (activityType) {
    itemVote.down = downCount;
    itemVote.up = getVotesCount(upCount);
    if (Util.isEmpty(itemVote.down)) {
      itemVote.down = 0;
    }
  } else {
    itemVote.up = upCount;
    itemVote.down = getVotesCount(downCount);
    if (Util.isEmpty(itemVote.up)) {
      itemVote.up = 0;
    }
  }
  return itemVote;
}

// Exports
window.exportTripsOnMap = exportTripsOnMap;
window.openMailDialog = openMailDialog;
window.sendMail = sendMail;
window.showPrintDialog = showPrintDialog;
window.actionOnItemActivity = actionOnItemActivity;
window.printTripItems = printTripItems;
window.showStaticMap = showStaticMap;
window.showVotingDialog = showVotingDialog;
