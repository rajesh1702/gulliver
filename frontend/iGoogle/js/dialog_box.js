/**
 * @fileoverview Code related to dialog box and related functions.
 * @author gadgetfactory@google.com (gadgetfactory)
 */

/**
 * Disable the days box in add trip days dialog.
 */
function disableDays() {
  var id = _gel('days_id');
  if (id) {
    id.disabled = true;
  }
  var startDate = _gel('startDate');
  if (!Util.isEmpty(startDate)) {
    startDate.value = prefs.getMsg('unspecified');
  }
  hideCalender('trip-calendar');
}

/**
 * Enable the days box in add trip days dialog.
 */
function enableDays() {
  var id = _gel('days_id');
  if (id) {
    id.disabled = false;
  }
}

/**
 * This method is executed when while adding item through calendar we make it
 * as a scheduled item.
 */
function selectAsScheduled() {
  // Below mentioned identifiers are defined in datepicker_calender.js file
  var date = _gel('selecteddate').innerHTML;
  date = date.replace(/\s/g, '');
  var tdElement = _gel('date' + date);
  if (tdElement) {
    var onclickAttribs = tdElement.attributes.getNamedItem('onclick').value;
    onclickAttribs = onclickAttribs.replace(/updateDateField\(/, '');
    var index = onclickAttribs.indexOf(');');
    var clsName = '';
    if (index != -1) {
      // Position from where class name string starts.
      clsName = onclickAttribs.substr(index + 18);
      clsName = clsName.substr(0, clsName.length - 2);
      onclickAttribs = onclickAttribs.substr(0, index);
    }
    var params = onclickAttribs.replace(/'/g, '').split(',');
    updateDateField(params[0], params[1], params[2], params[3], params[4]);
    tdElement.className = clsName;
  }
}

/**
 * This method is executed when while adding item through calendar we make it
 * as an unscheduled item.
 */
function selectAsUnscheduled() {
  // Below mentioned identifiers are defined in datepicker_calender.js file
  var highlightedTd = _gel('selectedDate');
  if (highlightedTd) {
    if (highlightedTd.className == 'highlighted-day-selected') {
      highlightedTd.className = 'highlighted-day';
    } else if (highlightedTd.className == 'drop-selected-td') {
      highlightedTd.className = 'drop-td';
    }
  }
}

/**
 * Select unschedule radio button and disable list box.
 */
function unscheduleRadioButton() {
  _gel('daysSelect').disabled = true;
  _gel('unscheduleCheck').checked = true;
  _gel('daysCheck').checked = false;
}

/**
 * Select unschedule radio button and enable list box.
 */
function daysRadioButton() {
  _gel('daysSelect').disabled = false;
  _gel('unscheduleCheck').checked = false;
  _gel('daysCheck').checked = true;
}

/**
 * Function to fill calendar on dialog.
 */
function fillcalendar() {
  fillTripDates();
  var sDate = tripDates[0];
  var firstDate = sDate.split('/');
  _gel('selecteddate').innerHTML = DateLib.formatDate(sDate, '{d} {MM} {Y}');
  displayDatePicker('itemDate', 0, sDate);
}

/**
 * Function to hide calendar for trip date.
 * @param {string} calendarId Id of calendar.
 */
function hideCalender(calendarId) {
  var calendar = _gel(calendarId);
  if (calendar) {
    calendar.style.visibility = 'hidden';
    calendar.style.display = 'none';
  }
}

/**
 * Function to hide dialog.
 */
function hideViewDateDialogBox() {
  hideCalender('trip-calendar');
  hideDialog();
}

/**
 * Dialog for edit dates.
 */
function showLocationDialog() {
  var tplHtml = _gel('tpl-location-dialog').value;
  var tripData = getTripById(gCurrentTripsData.currentTripId);
  var tplData = {location: tripData.loc};
  showDialog(Util.supplant(tplHtml, tplData));
  var element = _gel('txtLocation');
  element.focus();
  element.select();
}

/**
 * Closes any opened popup window.
 */
function closeInfoWindow() {
  _IG_AdjustIFrameHeight(1000);
  if (!selectedItemId) {
    return;
  }
  var selectedElement = _gel(selectedItemId);
  if (selectedElement &&
      (selectedItemId.indexOf('itemPopup') != -1 ||
      selectedItemId.indexOf('resultPopup') != -1)) {
    selectedElement.innerHTML = '';
    selectedElement.style.display = 'none';
  }
  selectedItemId = '';
}

/**
 * Creates the dialog box to delete an item.
 * @param {string} itemId Id of an item.
 * @param {boolean} isItem Flag to check if its an item or a trip.
 */
function createDeleteItemBox(itemId, isItem) {
  itemId = itemId || '';
  var name = isItem ? getItemById(itemId).name : getTripById(itemId).name;
  if (name.length > 15) {
    name = name.substr(0, 15) + '...';
  }
  var tplHtml = _gel('tpl-delete-item-dialog').value;
  var tplData = {
    itemName: name,
    itemId: itemId,
    flag: isItem
  };
  showDialog(Util.supplant(tplHtml, tplData));
  _gel('cancelButton').focus();
}

/**
 * Dialog for editing trip name.
 */
function showEditDialog() {
  var tplHtml = _gel('tpl-edit-trip-name-dialog').value;
  var tripData = getTripById(gCurrentTripsData.currentTripId);
  var tplData = {tripName: tripData.name};
  tplHtml = Util.supplant(tplHtml, tplData);
  showDialog(tplHtml);
  var element = _gel('tripNameEdit');
  element.focus();
  element.select();
}

/**
 * Creates the dialog box to create the trip.
 */
function createTripBox() {
  // Showing create trip dialog by filling data from template.
  showDialog(_gel('tpl-create-trip').value);
  _gel('server-msg').style.display = 'none';
  _gel('create-location').focus();
  _gel('cancel-trip-btn').style.display = 'none';
}

/**
 * Dialog for editing dates for a trip.
 */
function showDateDialog() {
  var objTrip = getTripById(gCurrentTripsData.currentTripId);
  var tripStartDate = objTrip.sdate;
  var tripEndDate = objTrip.fdate;
  if (!Util.isEmpty(tripStartDate) && !Util.isEmpty(tripEndDate)) {
    tripStartDate = tripStartDate.indexOf('-') > -1 ?
                    DateLib.formatDate(tripStartDate) : tripStartDate;
    tripEndDate = tripEndDate.indexOf('-') > -1 ?
                  DateLib.formatDate(tripEndDate) : tripEndDate;
  } else {
    tripEndDate = tripStartDate = prefs.getMsg('unspecified');
  }
  var tplHtml = _gel('tpl-date-dialog').value;
  var tplData = {
    duration:
        Util.isEmpty(objTrip.duration) ? DEFAULT_DURATION : objTrip.duration,
    startDate: tripStartDate,
    endDate: tripEndDate
  };
  showDialog(Util.supplant(tplHtml, tplData));
  _gel('cancelButton').focus();
}

/**
 * To display Edit Item Dialog.
 * @param {string} id Id of an item.
 */
function editItemDialog(id) {
  var daysOption = [];
  var tempDate = '';
  var objTrip = getTripById(gCurrentTripsData.currentTripId);
  var tripItem = getItemById(id);
  if (!tripItem) {
    return;
  }

  if (!Util.isEmpty(objTrip.sdate)) {
    for (var i = 0; i < objTrip.duration; i++) {
      tempDate = DateLib.addDaysToDate(objTrip.sdate, i, '{m}/{d}/{Y}');
      if (DateLib.addDaysToDate(tripItem.sdate, 0, '{m}/{d}/{Y}') == tempDate) {
        daysOption.push('<option value="', tempDate, '" selected>',
            tempDate, '</option>');
      } else {
        daysOption.push('<option value="', tempDate, '">',
            tempDate, '</option>');
      }
    }
  } else {
    for (var i = 1; i <= objTrip.duration; i++) {
      if (tripItem.day == i) {
        daysOption.push('<option value="', i, '" selected>',
            prefs.getMsg('day'), '&nbsp;', i, '</option>');
      } else {
        daysOption.push('<option value="', i, '">', prefs.getMsg('day'),
            '&nbsp;', i, '</option>');
      }
    }
  }
  var description = tripItem.address.replace('<br/>', '');
  if (Util.isEmpty(description)) {
    description = '';
  }
  var tplHtml = _gel('edit-item-tpl').value;
  var tplData = {
    index: id,
    itemName: _hesc(_unesc(tripItem.name)),
    description: _unesc(description),
    daysOption: daysOption.join('')
  };
  tplData.readOnly = tripItem.isCustom ? '' : 'disabled';
  showDialog(Util.supplant(tplHtml, tplData));
  _gel('cancelButton').focus();
  if (tripItem.day) {
    daysRadioButton();
  } else {
    unscheduleRadioButton();
  }
}

/**
 * Shows Add Items dialog box.
 * @param {string} itemName The name of an item.
 * @param {string} index The index of an item.
 */
function showAddItemDialog(itemName, index) {
  var trip = getTripById(gCurrentTripsData.currentTripId);
  var tplData = {
    itemName: itemName,
    index: index
  };
  if (Util.isEmpty(trip.sdate)) {
    // Shows popup by putting data in the template.
    showDialog(Util.supplant(_gel('tpl-trip-days').value, tplData));
    _gel('cancelButton').focus();
    var dialogHtml = ['<select style="width:32%;" id="days_id" disabled>'];
    for (var i = 1; i <= trip.duration; i++) {
      dialogHtml.push('<option value="', i, '">', prefs.getMsg('day'),
          '&nbsp;', i, '</option>');
    }
    dialogHtml.push('</select>');
    _gel('daysoption').innerHTML = dialogHtml.join('');
  } else {
    showDialog(Util.supplant(_gel('tpl-calendar').value, tplData));
    _gel('cancelButton').focus();
    fillcalendar();
  }
}

/**
 * Used to display dialog box.
 * @param {string} html The HTML content for dialog box.
 */
function showDialog(html) {
  _gel('dialog-content').innerHTML = html;
  var dialogMask = _gel('dialog-mask');
  dialogMask.style.display = '';
  var mainHeight = _gel('main-container').offsetHeight;
  dialogMask.style.height = mainHeight + 'px';
  var dialog = _gel('dialog');
  dialog.style.display = '';
  dialog.style.left = getLeftPosition() + (getPageWidth() / 2) -
      (dialog.offsetWidth / 2) + 'px';
  dialog.style.top = (mainHeight / 2) <= dialog.offsetHeight ? '50px' : '220px';
}

/**
 * Function to hide dialog mask.
 */
function hideDialog() {
  _gel('dialog-content').innerHTML = '';
  _gel('dialog-mask').style.display = 'none';
  _gel('dialog').style.display = 'none';
}

// Exports
window.showEditDialog = showEditDialog;
window.showDateDialog = showDateDialog;
window.disableDays = disableDays;
window.enableDays = enableDays;
window.selectAsScheduled = selectAsScheduled;
window.selectAsUnscheduled = selectAsUnscheduled;
window.editItemDialog = editItemDialog;
window.hideViewDateDialogBox = hideViewDateDialogBox;
window.createDeleteItemBox = createDeleteItemBox;
