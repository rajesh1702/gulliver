/**
 * @fileoverview Code to handle calendar related functionality.
 * @author gadgetfactory@google.com (gadgetfactory)
 */

/**
 * Function to Clear start date and end date.
 */
function clearDate() {
  hideCalender();
  _gel('endDate').value = '';
  _gel('startDate').value = '';
}

/**
 * Function to fill trip dates.
 */
function fillTripDates() {
  tripdates = [];
  var objTrip = JGulliverData.getCurrentTrip();
  for (var i = 0; i < objTrip.duration; i++) {
    tripdates.push(addDaysToDate(objTrip.sdate, i, '%m/%d/%Y'));
  }
}

/**
 * Function to fill calendar on dialog.
 */
function fillcalendar() {
  fillTripDates();
  var strStartDate = tripdates[0];
  var firstdate = strStartDate.split('/');
  var month = firstdate[0] - 1;
  var wholedate = firstdate[1] + '&nbsp;' +
      monthArrayLong[month] + '&nbsp;' + firstdate[2];
  _gel('selecteddate').innerHTML = wholedate;
  strStartDate = firstdate[0] + '/' + firstdate[1] + '/' + firstdate[2];
  displayDatePicker('ADate', '', 0, strStartDate);
}

/**
 * Function to add selected date to trip dates.
 */
function addItemDate() {
  var objTrip = JGulliverData.getCurrentTrip();
  var bSchedule = _gel('dateradio') ? _gel('dateradio').checked : false;
  var daySelected = 0;
  if (bSchedule) {
  if (_gel('selecteddate')) {
    var date2 = new Date(_gel('selecteddate').innerHTML);
    var date1 = new Date(objTrip.sdate);
    var oneDay = 24 * 60 * 60 * 1000;
    daySelected = Math.ceil((date2.getTime() - date1.getTime()) / oneDay) + 1;
  } else if (_gel('days_id')) {
    daySelected = _gel('days_id').selectedIndex + 1;
  } else if (_gel('startdate')) {
     var date2 = new Date(_gel('startdate').value);
     var date1 = new Date(objTrip.sdate);
     var oneDay = 24 * 60 * 60 * 1000;
     daySelected = Math.ceil((date2.getTime() - date1.getTime()) / oneDay) + 1;
  } else if (currentdate == undefined) {
      _gel('wrngmsg').style.visibility = 'visible';
      _gel('wrngmsg').innerHTML = prefs.getMsg('select_date');
      return;
    } else {
      var nDays = getDateDiff(objTrip.sdate, currentdate);
      var nDays1 = getDateDiff(currentdate, objTrip.edate);
      if (nDays < 0 || nDays1 < 0) {
        _gel('wrngmsg').style.visibility = 'visible';
        _gel('wrngmsg').innerHTML = prefs.getMsg('date_outof_range');
        return;
      }
   }
  }
  var nSelectedItemIndex = -1;
  if (strSelectedItemId.indexOf('resultItem') != -1) {
    nSelectedItemIndex = parseInt(strSelectedItemId.substr(10), 10);
    addNewItem(nSelectedItemIndex, bSchedule, daySelected);
  } else if (strSelectedItemId.indexOf('unscheduledItem') != -1) {
    if (bSchedule) {
      nSelectedItemIndex = parseInt(strSelectedItemId.substr(15), 10);
      if (nSelectedItemIndex >= 0) {
        if (!isEmpty(objTrip.sdate)){
          objTrip.updateItemDates(nSelectedItemIndex, currentdate);
        } else
          objTrip.updateItemDays(nSelectedItemIndex, daySelected);
        }
      }
  } else {
    // to be used for creating user defined items.
    var itemDescription = _gel('itemDescription').value;
    var itemName = _gel('itemName').value;
    var itemOwner = gViewer;
    var sdate = '';
    var edate = '';
    if (!isEmpty(objTrip.sdate)) {
      var date = _gel('startdate').value;
      sdate = date;
      edate = date;
    }
    var day = daySelected;
    getLatLangLocation(itemDescription, itemName, itemOwner, sdate, edate, day);
  }
  strSelectedItemId = '';
  hideDialogView();
}

/**
 * Executes when changes the value of duration in date dialog.
 * @param {object} obj Element id.
 */
function onUpdateDuration(obj) {
  var strDuration = obj.value;
  var idWarningMsg = _gel('wrngmsg');
  if (strDuration.strip() == '' || isNumeric(strDuration) == false) {
    if (idWarningMsg) {
      idWarningMsg.innerHTML = prefs.getMsg('invalid_duration1');
      idWarningMsg.style.visibility = 'visible';
    }
    return;
  }
  var nDuration = parseInt(strDuration, 10);
  var strStartDate = _gel('startDate').value;
  if (isEmpty(strStartDate)) {
    _gel('endDate').value = prefs.getMsg('unspecified');
  } else {
    _gel('endDate').value =
        addDaysToDate(strStartDate, (nDuration - 1), '%m/%d/%Y');
  }
  if (idWarningMsg) {
    if (nDuration > JGulliverData.getCurrentTrip().duration) {
      idWarningMsg.style.visibility = 'hidden';
      idWarningMsg.innerHTML = prefs.getMsg('invalid_duration');
    } else {
      idWarningMsg.style.visibility = 'visible';
    }
  }
}

/**
 * Enable unschedule radio button
 * Disable days radio button and list box
 */
function unscheduleRadioButton() {
  _gel('daysSelect').disabled = true;
  _gel('unscheduleCheck').checked = true;
  _gel('daysCheck').checked = false;
}

/**
 * Disable unschedule radio button
 * Enable days radio button and list box
 */
function daysRadioButton() {
  _gel('daysSelect').disabled = false;
  _gel('unscheduleCheck').checked = false;
  _gel('daysCheck').checked = true;
}
