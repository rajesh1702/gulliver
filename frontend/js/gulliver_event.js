/**
 * Close action menu if already open.
 * @param {object} event the click event.
 */
function closeActionMenu(event) {
  event = event || window.event;
  var id = event['target'] ? event['target']['id'] : event['srcElement']['id'];
  if (id == 'menuAction' || id == 'menuImg')
    return;

  var tableBox = _gel('table-box');
  if (tableBox && tableBox.style.display == 'block') {
    tableBox.style.display = 'none';
  }
}

/**
 * Registers keyup event.
 * @param {string} strId The element id.
 */
function addKeyListener(strId) {
  if (window.addEventListener) {
    _gel(strId).addEventListener('keyup', handleKeyEvent, false);
  } else {
    _gel(strId).attachEvent('onkeyup', handleKeyEvent);
  }
}

/**
 * Handles key events.
 * @param {object} event The keyup event.
 */
function handleKeyEvent(event) {
  if (!event) event = window.event;
  var id = event['target'] ? event['target']['id'] : event['srcElement']['id'];
  switch (event.keyCode) {
    case 13:
      if (id == 'search-box') {
        searchMapContent(1, null, false);
      }
      else if (id == 'searchTripText') {
        fillForSortedRecords(true);
      }
      else if (id == 'tripNameEdit') {
        updateTripName();
      }
      else if (id == 'create-location') {
        validateTrip();
      } else if (id == 'txtLocation') {
        changeTripLocation();
      }
      break;
    case 27:
      hideDialog();
      break;
  }
}

/**
 * Disable the days box in add trip days dialog.
 */
function disableDays() {
  var id = _gel('days_id');
  if (id) {
    id.disabled = true;
  }
  if (!isEmpty(_gel('startdate'))) {
    _gel('startdate').value = prefs.getMsg('unspecified');
  }
  hideCalender();
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
 * Executes while select scheduled check box in calendar.
 */
function selectAsScheduled() {
  // Below mentioned identifiers are defined in datepicker_calender.js file
  var date = _gel('selecteddate').innerHTML;
  date = date.replace(/&nbsp;/g, '').replace(/ /g, '');
  var strSelectedTd = 'date' + date;
  var tdElement = _gel(strSelectedTd);
  if (tdElement) {
    var onclickAttribs = tdElement.attributes.getNamedItem('onclick').value;
    onclickAttribs = onclickAttribs.replace(/updateDateField\(/, '');
    var index = onclickAttribs.indexOf(');');
    var clsName = '';
    if (index != -1) {
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
 * Executes while select unscheduled check box in calendar.
 */
function selectAsUnscheduled() {
  // Below mentioned identifiers are defined in datepicker_calender.js file
  var highlightedTd = _gel(selectedDate);
  if (highlightedTd) {
    if (highlightedTd.className == 'dpDayHighlightTDSelected') {
      highlightedTd.className = 'dpDayHighlightTD';
    } else if (highlightedTd.className == 'dpselectedTD') {
      highlightedTd.className = 'dpTD';
    }
  }
}

/**
 * Executes when scrolls item down or up.
 */
function onScroll() {
  if (strSelectedItemId.indexOf('scheduledItem') == -1)
    closeInfoWindow();
}

/**
 * Function for star over the rating.
 * @param {number} val Value of the star selected.
 */
function fStarOver(val) {
  for (var i = 1; i <= MAX_RATING; i++) {
    if (i <= val)
      _gel('img' + i).src = STAR_ON_IMG;
    else
      _gel('img' + i).src = STAR_OFF_IMG;
  }
}

/**
 * Function for onmouseout of the rating.
 */
function fStarOut() {
  for (var i = 1; i <= MAX_RATING; i++) {
    if (i <= JGulliverData.getCurrentTrip().rating)
      _gel('img' + i).src = STAR_ON_IMG;
    else
      _gel('img' + i).src = STAR_OFF_IMG;
  }
}

/**
 * Takes action on click of dropdown value.
 * @param {number} optionid Index of option.
 */
function onTripAction(optionid) {
  switch (optionid) {
    case 'option1':
      showLocationDialog();
      break;
    case 'option2':
      createDeleteTripBox();
      break;
  }
  _gel('table-box').style.display = 'none';
}

/**
 * Function for update trip name.
 */
function updateTripName() {
  var objTrip = JGulliverData.getCurrentTrip();
  var strName = _gel('tripNameEdit').value.strip();
  if (strName == '') {
    return;
  }
  if (strName != objTrip.name) {
    var gulliverData = JGulliverData.getInstance();
    var counter = gulliverData.arrTripData.length;
    var count = 0;
    for (var i = 0; i < counter; i++) {
      if (strName.toLowerCase() == gulliverData.arrTripData[i].name.substr(0,
                     strName.length).toLowerCase()) {
        count++;
      }
    }
    if (count != 0) {
      strName = strName + count;
    }
    objTrip.updateName(strName);
  }
  hideDialog();
}

/**
 * Explores the trip at specified index.
 * @param {string} id The trip id.
 */
function exploreTrip(id) {
  var gulliverData = JGulliverData.getInstance();
  var nIndex = gulliverData.getTripIndex(id);
  if (nIndex != -1) {
    gulliverData.nSelectedTripIndex = nIndex;
    var objTrip = JGulliverData.getCurrentTrip();
    gCurrentTripId = objTrip.id;
    objTrip.arrItem = [];
    callbackCreateTrip();
  }
}

/**
 * Change the rating.
 * @param {number} rating Rating of the trip.
 */
function changeRating(rating) {
  var objTrip = JGulliverData.getCurrentTrip();
  if (objTrip.rating != rating) {
    var tempRating = objTrip.rating;
    objTrip.rating = rating;
    objTrip.updateRating(tempRating);
  }
}

/**
 * Executes when clicks on cancel while creating trip.
 */
function cancelCreateTrip() {
  hideDialog();
  var gulliverData = JGulliverData.getInstance();
  if (gulliverData.arrTripData.length == 0) {
    gulliverData.displayTrips();
  }
}
