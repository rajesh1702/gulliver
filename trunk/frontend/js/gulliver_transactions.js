/**
 * @fileoverview Library of all database update methods of Gulliver Gadget.
 * @author gadgetfactory@google.com (gadgetfactory)
 */

/**
 * Function to create new item and save it to db.
 * @param {number} nSelectedItemIndex Index of item.
 * @param {boolean} bSchedule the flag for scheduling the item.
 * @param {number} nDaySelected The day to which item is being added.
 */
function addNewItem(nSelectedItemIndex, bSchedule, nDaySelected) {
  if (nSelectedItemIndex >= 0) {
    var objTrip = JGulliverData.getCurrentTrip();
    var objItem = {
      id: '', name: '', address: '', review: '',
      sdate: '', edate: '', duration: 1, category: '',
      lat: '', lng: '', link: '', day: 0, weburl: '',
      imgurl: '', Item_thumb_up: 0, Item_thumb_down: 0,
      bCustom: ''
    };
    objItem.name = arrSearchResults[nSelectedItemIndex].name;
    objItem.item_owner = gViewer;
    objItem.lat = arrSearchResults[nSelectedItemIndex].lat;
    objItem.lng = arrSearchResults[nSelectedItemIndex].lng;
    objItem.address = arrSearchResults[nSelectedItemIndex].address;
    if (arrSearchResults[nSelectedItemIndex].review == undefined) {
      objItem.review = '';
    } else {
      objItem.review = arrSearchResults[nSelectedItemIndex].review;
    }
    objItem.category = arrSearchResults[nSelectedItemIndex].category;
    objItem.rating = arrSearchResults[nSelectedItemIndex].rating;
    var itemDate = addDaysToDate(objTrip.sdate, nDaySelected - 1, '%m/%d/%Y');
    if (!isEmpty(objTrip.sdate) && bSchedule) {
      objItem.sdate = itemDate;
      objItem.edate = itemDate;
    }
    objItem.day = nDaySelected;
    objItem.link = arrSearchResults[nSelectedItemIndex].link || '';
    objItem.imgurl = arrSearchResults[nSelectedItemIndex].imgurl || '';
    objItem.weburl = arrSearchResults[nSelectedItemIndex].weburl || '';
    objItem.data_source =
        arrSearchResults[nSelectedItemIndex].data_source || '';
    objTrip.saveItem(objItem);
    showTripItemsOnmap();
  }
}

/**
 * Save edited Item info.
 * @param {number} index Index of item.
 */
function saveEditItem(index) {
  if (_gel('itemDesp').value.length > 200) {
    var message = _gel('wrngmsg');
    message.style.visibility = 'visible';
    message.innerHTML = prefs.getMsg('desc_error_message');
    return;
  }
  var objTrip = JGulliverData.getCurrentTrip();
  var itemName, itemDesp, itemDay, itemSDate, itemFDate;

  if (_gel('itemName').value != '')
    itemName = _gel('itemName').value;
  else {
    _gel('wrngmsg').innerHTML = prefs.getMsg('field_empty');
    _gel('wrngmsg').style.display = 'block';
    _gel('itemName').focus();
    return;
  }

  if (_gel('itemDesp').value != '')
    itemDesp = _gel('itemDesp').value;
  else {
    _gel('wrngmsg').innerHTML = prefs.getMsg('field_empty');
    _gel('wrngmsg').style.display = 'block';
    _gel('itemDesp').focus();
    return;
  }

  if (_gel('unscheduleCheck').checked) {
    itemSDate = '';
    itemFDate = '';
    itemDay = 0;
  } else {
    if (!isEmpty(objTrip.sdate)) {
      itemDay = 0;
      itemFDate = itemSDate =
          _gel('daysSelect')[_gel('daysSelect').selectedIndex].value;
    } else {
      itemDay = _gel('daysSelect')[_gel('daysSelect').selectedIndex].value;
      itemFDate = itemSDate = '';
    }
  }
  itemDesp = _esc(itemDesp);
  hideDialog();
  objTrip.updateItemDetails(index,
                            itemName,
                            itemDesp,
                            itemDay,
                            itemSDate,
                            itemFDate
                            );
}

/**
 * Function to save newley created item.
 */
function saveNewItem() {
  if (_gel('itemDescription').value.length > 200) {
    var message = _gel('wrngmsg');
    message.style.visibility = 'visible';
    message.innerHTML = prefs.getMsg('desc_error_message');
    return;
  }
  hideCalender();
  var itemname = _gel('itemName').value;
  var itemdescription = _gel('itemDescription').value;
  var date;
  var message = _gel('wrngmsg');
  if (_gel('startdate')) {
    date = _gel('startdate').value;
  }
  if (itemname == '' || itemdescription == '') {
    message.style.visibility = 'visible';
    message.innerHTML = prefs.getMsg('field_empty');
  } else if (_gel('dateradio').checked && date == prefs.getMsg('unspecified')) {
    message.style.visibility = 'visible';
    message.innerHTML = prefs.getMsg('date_empty');
  } else {
    addItemDate();
  }
}


/**
 * Executes when save date values for a trip.
 */
function saveTripDates() {
  var idWarningMsg = _gel('wrngmsg');
  var strDays = _gel('editDaysBox').value;
  if (strDays.strip() == '') {
    return;
  }
  var days = parseInt(strDays, 10);
  if (days <= 0 || days == Number.NaN) {
    if (idWarningMsg) {
      idWarningMsg.innerHTML = prefs.getMsg('invalid_duration1');
      idWarningMsg.style.visibility = 'visible';
    }
    return;
  }
  if (days > MAX_TRIP_DURATION) {
    if (idWarningMsg) {
      idWarningMsg.innerHTML = prefs.getMsg('duration_limit_exceeds');
      idWarningMsg.style.visibility = 'visible';
    }
    return;
  }
  var sdate = _gel('startDate').value;
  var edate = _gel('endDate').value;
  var objTrip = JGulliverData.getCurrentTrip();
  if (objTrip.sdate || objTrip.edate) {
    if (!sdate || !edate) {
      idWarningMsg.innerHTML = prefs.getMsg('no_empty_dates');
      idWarningMsg.style.visibility = 'visible';
      return;
    }
  }

  if (!isEmpty(sdate) && !isEmpty(edate)) {
    var curdate = new Date();
    curdate = getDateString(curdate);
    var dateDiff = getDateDiff(curdate, sdate);
    if (dateDiff >= 0) {
      idWarningMsg.style.visibility = 'hidden';
    } else {
      idWarningMsg.innerHTML = prefs.getMsg('date_expired');
      idWarningMsg.style.visibility = 'visible';
      return;
    }
    if ((sdate != objTrip.sdate) ||
        (edate != objTrip.edate) || (days != objTrip.duration))
      objTrip.updateDates(sdate, edate, days);
  } else if (!isEmpty(objTrip.sdate) && !isEmpty(objTrip.edate)) {
    edate = addDaysToDate(objTrip.sdate, days - 1, '%m/%d/%Y');
    objTrip.updateDates(objTrip.sdate, edate, days);
  } else if (days != objTrip.duration) {
    objTrip.updateDuration(days);
  }
  hideViewDateDialogBox();
}

/**
 * It adds item to current trip.
 * @param {string} id id of selected item of search result.
 */
function addItem(id) {
  // Close if any popup window is here opened.
  closeInfoWindow();
  strSelectedItemId = id;

  // id is having 10 characters + index of item.
  var index = parseInt(id.substr(10), 10);
  var title = arrSearchResults[index].name;
  title = (title.length > 21) ? title.substr(0, 21) + ' ...' : title;
  showAddItemDialog(title);
}

/**
 * Delete the item at specified index.
 * @param {number} index index of trip item.
 */
function deleteSelectedItem(index) {
  hideDialog();
  var objTrip = JGulliverData.getCurrentTrip();
  objTrip.deleteItem(index);
}

/**
 * This function will be used to change the trip location from canvas view.
 */
function changeTripLocation() {
  var idWarningMsg = _gel('wrngmsg');
  var strLocation = _gel('txtLocation').value;
  if (strLocation.strip() != '') {
    var validLocation = isNumeric(strLocation);
    if (validLocation) {
      idWarningMsg.innerHTML = prefs.getMsg('enter_valid_loc');
      idWarningMsg.style.visibility = 'visible';
    } else {
      if (strLocation != JGulliverData.getCurrentTrip().loc)
        showAddress(strLocation, 0);
      else
        hideDialog();
    }
  } else {
    idWarningMsg.innerHTML = prefs.getMsg('enter_loc');
    idWarningMsg.style.visibility = 'visible';
  }
}
