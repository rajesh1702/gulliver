/**
 * @fileoverview Library of calendar events for the gadget.
 * 
 */

/**
 * Stores name of currently selected calendar type.
 * @type {string}
 */
var firstItem = 0;

/**
 * Default date sepeartor.
 * @type {string}
 */
var dateSeparator = '/';

/**
 * Object to hold calendar dates.
 * @type {Object}
 */
var trippyCalendar = {
  selectedDate: '',
  currentDate: '',
  defaultDate: ''
};

/**
 * Global variable to decide class name.
 * @type {boolean}
 */
var prevFlag;

/**
 * Global variable to decide calendar type.
 * @type {number}
 */
var calendarType;

/**
 * Flag to decide whether date on calendar is changed or not.
 * @type {boolean}
 */
var isDateChanged;

/**
 * Displays the date picker.
 * @param {string} dateFieldName Date field name.
 * @param {number} calType Type of calendar.
 * @param {string} onLoadDate Initial date to be shown on load of datepicker.
 */
function displayDatePicker(dateFieldName, calType, onLoadDate) {
  trippyCalendar.selectedDate = null;
  trippyCalendar.currentDate = null;
  prevFlag = 0;
  trippyCalendar.defaultDate = onLoadDate;
  calendarType = calType;
  var targetDateField = document.getElementsByName(dateFieldName).item(0);
  var x = targetDateField.offsetLeft;
  var y = targetDateField.offsetTop + targetDateField.offsetHeight;
  var parent = targetDateField;
  while (parent.offsetParent) {
    parent = parent.offsetParent;
    x += parent.offsetLeft;
    y += parent.offsetTop;
  }
  // Specific to item calendar.
  if (!calendarType) {
    y = y - 15;
  }
  drawDatePicker(targetDateField, x, y);
}

/**
 * Draws the datepicker at x , y positon.
 * @param {string} targetDateField target date filed.
 * @param {number} x X co-ordinate.
 * @param {number} y Y co-ordinate.
 */
function drawDatePicker(targetDateField, x, y) {
  var calendar = calendarType ? 'trip-calendar' : 'item-calendar';
  if (!_gel(calendar)) {
    var element = document.createElement('div');
    element.setAttribute('id', calendar);
    element.setAttribute('class', 'dpDiv');
    element.setAttribute('style', 'visibility:hidden;');
    document.body.appendChild(element);
  }
  var pickerDiv = _gel(calendar);
  pickerDiv.style.visibility =
      (pickerDiv.style.visibility == 'visible' ? 'hidden' : 'visible');
  pickerDiv.style.display =
      (pickerDiv.style.display == 'block' ? 'none' : 'block');
  pickerDiv.style.zIndex = 10000;
  pickerDiv.style.paddingLeft = '5px';
  var defaultDate;
  if (calendarType) {
    pickerDiv.style.position = 'absolute';
    pickerDiv.style.left = x + 'px';
    pickerDiv.style.top = y + 'px';
    defaultDate = targetDateField.value.split('/');
  } else {
    defaultDate = trippyCalendar.defaultDate.split('/');
  }
  // For index of month.
  defaultDate[0] -= 1;
  trippyCalendar.defaultDate = defaultDate;
  isDateChanged = 0;
  refreshDatePicker(targetDateField.name,
                    trippyCalendar.defaultDate[2],
                    trippyCalendar.defaultDate[0],
                    trippyCalendar.defaultDate[1]);
}

/**
 * Checks whether date falls within trip dates range.
 * @param {Date} curDate Current date.
 * @return {number} 1 if date is trip date else 0.
 */
function isTripDate(curDate) {
  for (var i = 0; i < tripDates.length; i++) {
    if (tripDates[i] == getDateString(curDate)) {
      firstItem = i;
      return 1;
    }
  }
  return 0;
}

/**
 * Refresh date picker.
 * @param {string} dateFieldName The name of the date filed.
 * @param {number} year The year.
 * @param {number} month The month.
 * @param {number} day The day.
 */
function refreshDatePicker(dateFieldName, year, month, day) {
  var html = [], tplHtml, tplData, calCreated = false;
  firstItem = 0;

  var thisDay = new Date();
  if (month >= 0 && year > 0) {
    thisDay = new Date(year, month, 1);
  } else {
    day = thisDay.getDate();
    thisDay.setDate(1);
  }
  var emptyCells = [];
  for (var i = 0; i < thisDay.getDay(); i++) {
    emptyCells.push(_gel('tpl-empty-td').value);
  }
  var tplCalData = {
    buttonCodeUp: updateCalendar(dateFieldName, thisDay, -1, '▲'),
    buttonCodeDown: updateCalendar(dateFieldName, thisDay, 1, '▼'),
    dateString: DateLib.formatDate(thisDay.toDateString(), '{MM} {Y}')
  };
  var targetDateField = document.getElementsByName(dateFieldName).item(0);

  // A calendar has either 5 or 6 rows and each row containes maximum 7 days.
  for (var row = 0; row < 6; row++) {
    html.push('<tr class="dpTR">');
    // Add empty cells.
    var cols;
    for (cols = 0; cols < thisDay.getDay(); cols++) {
      html.push(_gel('tpl-empty-td').value);
    }
    // Add cells for weekdays.
    for (;cols < 7; cols++) {
      var dateinRange = isTripDate(thisDay);
      var dayNum = thisDay.getDate();
      var htmlDate = [
        dayNum,
        DateLib.formatDate(thisDay.toDateString(), '{MM}'),
        thisDay.getFullYear()
      ];
      var dateStr = getDateString(thisDay);
      var clsName;
      if (calendarType) {
        clsName =
            targetDateField.value == dateStr ? 'highlighted-day' : 'drop-td';
      } else {
        clsName = dateinRange ? 'highlighted-day' : 'drop-td';
      }
      tplHtml = _gel('tpl-calendar-cell').value;
      tplData = {
        id: htmlDate.join(''),
        className: clsName,
        field: dateFieldName,
        datemdY: dateStr,
        datedMYWS: htmlDate.join(' '),
        tripDate: calendarType ? 0 : dateinRange.toString(),
        day: dayNum
      };
      html.push(Util.supplant(tplHtml, tplData));
      if (dateinRange && !firstItem && !isDateChanged) {
        prevFlag = 1;
        trippyCalendar.selectedDate = 'date' + htmlDate.join('');
        trippyCalendar.currentDate = getDateString(thisDay);
      }
      thisDay.setDate(thisDay.getDate() + 1);
      if (thisDay.getDate() == 1) {
        calCreated = true;
        break;
      }
    }
    if (calCreated) {
      break;
    }
  }
  if (thisDay.getDay() > 0) {
    for (i = 6; i > thisDay.getDay(); i--) {
      html.push(_gel('tpl-empty-td').value);
    }
  }
  html.push('</tr>');

  tplHtml = _gel('tpl-calender-header').value;
  tplCalData.weekdaysCell = html.join('');
  if (!calendarType) {
    _gel('item-calendar').innerHTML = Util.supplant(tplHtml, tplCalData);
  } else {
    _gel('trip-calendar').innerHTML = Util.supplant(tplHtml, tplCalData);
  }
}
/**
 * Updates calendar month and year on click of up and down image.
 * @param {string} dateFieldName Name of the date field.
 * @param {string} dateVal Value of date.
 * @param {number} adjust The adjustment factor.
 * @param {string} label Label for calendar.
 * @return {string} Calendar html.
 */
function updateCalendar(dateFieldName, dateVal, adjust, label) {
  var newMonth = (dateVal.getMonth() + adjust) % 12;
  var newYear = dateVal.getFullYear() +
                parseInt((dateVal.getMonth() + adjust) / 12);
  if (newMonth < 0) {
    newMonth += 12;
    newYear += -1;
  }
  var calendarHtml = '<div onclick="refreshDatePicker(\'' + dateFieldName +
      '\',' + newYear + ',' + newMonth + ');">' + label + '</div>';
  return calendarHtml;
}

/**
 * Gets the date in string format.
 * @param {Date} dateVal Date object.
 * @return {string} Date string.
 */
function getDateString(dateVal) {
  return DateLib.formatDate(dateVal.toDateString());
}

/**
 * Updates date field.
 * @param {string} dateFieldName Date field name.
 * @param {string} dateString Date in string format.
 * @param {string} dateInWords Date in words.
 * @param {number} flag Flag holds the previous state.
 * @param {string} selectedTd Id of selected td.
 */
function updateDateField(dateFieldName,
                         dateString,
                         dateInWords,
                         flag,
                         selectedTd) {
  isDateChanged = 1;
  if (!calendarType) {
    var selectedElement = _gel(trippyCalendar.selectedDate);
    // Remove selection of previous selected date.
    if (selectedElement && selectedTd != trippyCalendar.selectedDate) {
      selectedElement.className = prevFlag ? 'highlighted-day' : 'drop-td';
    }
    // Select new date.
    selectedElement = _gel(selectedTd);
    if (selectedElement) {
      selectedElement.className =
           flag ? 'highlighted-day-selected' : 'drop-selected-td';
    }

    trippyCalendar.selectedDate = selectedTd;
    prevFlag = flag;
    var targetDateField = document.getElementsByName(dateFieldName).item(0);
    if (dateString) {
      targetDateField.value = dateString;
    }
    _gel('selecteddate').innerHTML = dateInWords;
    _gel('dateradio').checked = true;
  } else {
    if (_gel('startDate') || _gel('endDate')) {
      var durationElement = _gel('edit-days-box');
      if (durationElement) {
        var strDuration = durationElement.value;
        if (!strDuration) {
          strDuration = '1';
          durationElement.value = strDuration;
        }
        var days = parseInt(strDuration, 10) - 1;
        var date = new Date(dateString);
        if (dateFieldName == 'startdate') {
          date.setDate(date.getDate() + days);
          _gel('endDate').value = DateLib.formatDate(date.toDateString());
        } else {
          var sDate = _gel('startDate');
          if (Util.isEmpty(sDate.value)) {
            date.setDate(date.getDate() - days);
            sDate.value = DateLib.formatDate(date.toDateString());
          } else {
            var days = DateLib.daysBetween(date, new Date(sDate.value));
            durationElement.value = (days >= 0) ? days + 1 : 0;
          }
        }
      }
    }
    var targetDateField = document.getElementsByName(dateFieldName).item(0);
    if (dateString) {
      targetDateField.value = dateString;
    }
    var pickerDiv = _gel('trip-calendar');
    pickerDiv.style.visibility = 'hidden';
    pickerDiv.style.display = 'none';
  }
  trippyCalendar.currentDate = dateString;
}

// Exports
window.displayDatePicker = displayDatePicker;
window.updateDateField = updateDateField;
