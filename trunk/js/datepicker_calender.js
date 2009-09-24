/**
 * @fileoverview Library of all UI methods of Gulliver Gadget.
 * @author gadgetfactory@google.com (gadgetfactory)
 */

var datePickerDivID = "datepicker";
var datePickerDivID1 = "datepicker1";
var iFrameDivID = "datepickeriframe";
var dayArrayShort = ['S', 'M', 'T', 'W', 'T', 'F', 'S'];
var dayArrayMed = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
var dayArrayLong = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];
var monthArrayMed = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'June', 'July', 'Aug', 'Sept', 'Oct', 'Nov', 'Dec'];
var monthArrayLong = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'];
var defaultDateSeparator = "/";        // common values would be "/" or "."
var defaultDateFormat = "mdy"    // valid values are "mdy", "dmy", and "ymd"
var dateSeparator = defaultDateSeparator;
var dateFormat = defaultDateFormat;
var selectedDate;
var currentdate;
var onLoadingDate;
var prevFlag;
var CalenderType;
var curImage;
var onceupdate;
var tripdates = [];

/**
 * Display the date picker.
 * @param {string} dateFieldName date field name.
 * @param {string} displayBelowThisObject id below which date is displayed.
 * @param {number} caltype type of calender.
 * @param {string} onLoadDate onload date string.
 * @param {string} dtFormat date format.
 * @param {string} dtSep date separator.
 */
function displayDatePicker(dateFieldName, displayBelowThisObject, caltype, onLoadDate, dtFormat, dtSep) {
  selectedDate = null;
  currentdate = null;
  prevFlag = 0;

  curImage = displayBelowThisObject;

  onLoadingDate = onLoadDate;
  CalenderType = caltype;
  var targetDateField = document.getElementsByName (dateFieldName).item(0);
    displayBelowThisObject = targetDateField;
  if (dtSep)
    dateSeparator = dtSep;
  else
    dateSeparator = defaultDateSeparator;
  if (dtFormat)
    dateFormat = dtFormat;
  else
    dateFormat = defaultDateFormat;
  var x = displayBelowThisObject.offsetLeft;
  var y = displayBelowThisObject.offsetTop +
          displayBelowThisObject.offsetHeight ;
  var parent = displayBelowThisObject;
  while (parent.offsetParent) {
    parent = parent.offsetParent;
    x += parent.offsetLeft;
    y += parent.offsetTop ;
  }
  if(CalenderType == 0)
    y = y - 15;
  drawDatePicker(targetDateField, x, y);
}

/**
 * Draws the datepicker at x , y positon.
 * @params{string} targetDateField target date filed.
 * @params{number} x positon.
 * @params{number} y position.
 */
function drawDatePicker(targetDateField, x, y) {
  if (CalenderType != 0) {
    if (!_gel(datePickerDivID)) {
      var newNode = document.createElement('div');
      newNode.setAttribute('id', datePickerDivID);
      newNode.setAttribute('class', 'dpDiv');
      newNode.setAttribute('style', 'visibility: hidden;');
      document.body.appendChild(newNode);
    }
    var pickerDiv = _gel(datePickerDivID);
    pickerDiv.style.position = 'absolute';
    pickerDiv.style.left = x + 'px';
    pickerDiv.style.top = y + 'px';
    pickerDiv.style.visibility =
        (pickerDiv.style.visibility == 'visible' ? 'hidden' : 'visible');
    pickerDiv.style.display =
        (pickerDiv.style.display == 'block' ? 'none' : 'block');
    pickerDiv.style.zIndex = 10000;
    pickerDiv.style.paddingLeft = '5px';
    if (CalenderType != 2) {
      var dt;
      if (targetDateField.value == '' || targetDateField.value == 'unspecified') {
        dt= new Date();
      } else {
        dt = getFieldDate(targetDateField.value);
      }
      onLoadingDate = (getDateString(dt)).split('/');
    } else
        onLoadingDate = onLoadingDate.split('/');
    onLoadingDate[0] = onLoadingDate[0] - 1;
    refreshDatePickerForCalenderType(targetDateField.name,
        onLoadingDate[2], onLoadingDate[0], onLoadingDate[1]);

  } else {
    if (!_gel(datePickerDivID1)) {
      var newNode = document.createElement('div');
      newNode.setAttribute('id', datePickerDivID1);
      newNode.setAttribute('class', 'dpDiv');
      newNode.setAttribute('style', 'visibility: hidden;');
      document.body.appendChild(newNode);
    }
    var pickerDiv = _gel(datePickerDivID1);
    pickerDiv.style.visibility =
        (pickerDiv.style.visibility == 'visible' ? 'hidden' : 'visible');
    pickerDiv.style.display =
        (pickerDiv.style.display == 'block' ? 'none' : 'block');
    pickerDiv.style.zIndex = 10000;
    pickerDiv.style.paddingLeft = '5px';
    onLoadingDate = onLoadingDate.split('/');
    onLoadingDate[0] = onLoadingDate[0] - 1;
    onceupdate = 0;
    refreshDatePicker(targetDateField.name, onLoadingDate[2],
                      onLoadingDate[0], onLoadingDate[1]);
  }
}

/**
 * Refresh date picker.
 * @params{string} dateFieldName name of the date field.
 * @params{number} year The year.
 * @params{number} month The month.
 * @params{number} day The day.
 */
function refreshDatePickerForCalenderType(dateFieldName, year, month, day) {
  var thisDay = new Date();
  if ((month >= 0) && (year > 0)) {
    thisDay = new Date(year, month, 1);
  } else {
    day = thisDay.getDate();
    thisDay.setDate(1);
  }
  var crlf = '\r\n';
  var TABLE = '<table cols=7 class="dpTable" >' + crlf;
  var xTABLE = '</table>' + crlf;
  var TR = '<tr class="dpTR">';
  var TR_title = '<tr class="dpTitleTR">';
  var TR_days = '<tr class="dpDayTR">';
  var TR_todaybutton = '<tr class="dpTodayButtonTR">';
  var xTR = '</tr>' + crlf;
  var TD = '<td class="dpTD" ';
  var TD_title = '<td colspan=5 class="dpTitleTD">';
  var TD_buttons = '<td class="dpButtonTD">';
  var TD_todaybutton = '<td colspan=7 class="dpTodayButtonTD">';
  var TD_days = '<td class="dpDayTD">';
  var TD_selected = '<td class="dpDayHighlightTD" ';
  var xTD = '</td>' + crlf;
  var DIV_title = '<div class="dpTitleText">';
  var DIV_selected = '<div class="dpDayHighlight">';
  var xDIV = '</div>';
  var html = TABLE;
  html += TR_title;
  html += TD_buttons + getButtonCode(dateFieldName, thisDay, -1, '▲') + xTD;
  html += TD_title + DIV_title + monthArrayLong[ thisDay.getMonth()] + ' ' +
          thisDay.getFullYear() + xDIV + xTD;
  html += TD_buttons + getButtonCode(dateFieldName, thisDay, 1, '▼') + xTD;
  html += xTR;
  html += TR_days;
  for(i = 0; i < dayArrayShort.length; i++)
    html += TD_days + dayArrayShort[i] + xTD;
  html += xTR;
  html += TR;
  for (i = 0; i < thisDay.getDay(); i++)
    html += TD + "&nbsp;" + xTD;
  do {
    dayNum = thisDay.getDate();
    var flag = 0;
    for(var i = 0; i < tripdates.length; i++) {
      if(tripdates[i] == getDateString(thisDay)) {
        flag = 1;
        firstitem = i;
        break;
      } else {
        flag = 0;
      }
    }
    var dateinwords = dayNum +' '+ monthArrayLong[ thisDay.getMonth()] +' ' +
                      thisDay.getFullYear();
    var DayInWordsWithoutSpace = dayNum +''+
        monthArrayLong[ thisDay.getMonth()] +''+thisDay.getFullYear();
    TD_onclick = ' id=date'+DayInWordsWithoutSpace +
                 ' onclick=\'updateDateField("' + dateFieldName + '", "' +
                   getDateString(thisDay) + '", "' + dateinwords + '", "date' +
                   DayInWordsWithoutSpace+'");\'>';
    if (CalenderType == 2) {
      html += (flag == 1) ? TD_selected : TD;
    } else {
      html += (day == thisDay.getDate()) ? TD_selected : TD;
    }
    html +=  TD_onclick + dayNum + xTD;
    if (thisDay.getDay() == 6)
      html += xTR + TR;
    thisDay.setDate(thisDay.getDate() + 1);
  } while (thisDay.getDate() > 1)
  if (thisDay.getDay() > 0) {
    for (i = 6; i > thisDay.getDay(); i--)
      html += TD + '&nbsp;' + xTD;
  }
  html += xTR;
  html += xTD + xTR;
  html += xTABLE;
  _gel(datePickerDivID).innerHTML = html;
  adjustiFrame();
}


/**
 * Refresh date picker.
 * @params{string} dateFieldName The name of the date filed.
 * @params{number} year The year.
 * @params{number} month The month.
 * @params{number} day The day.
 */
function refreshDatePicker(dateFieldName, year, month, day) {
  var thisDay = new Date();
  if ((month >= 0) && (year > 0)) {
    thisDay = new Date(year, month, 1);
  } else {
    day = thisDay.getDate();
    thisDay.setDate(1);
  }
  var crlf = '\r\n';
  var TABLE = '<table cols=7 class="dpTable" >' + crlf;
  var xTABLE = '</table>' + crlf;
  var TR = '<tr class="dpTR">';
  var TR_title = '<tr class="dpTitleTR">';
  var TR_days = '<tr class="dpDayTR">';
  var TR_todaybutton = '<tr class="dpTodayButtonTR">';
  var xTR = '</tr>' + crlf;
  var TD = '<td class="dpTD" ';
  var TD_title = '<td colspan=5 class="dpTitleTD">';
  var TD_buttons = '<td class="dpButtonTD">';
  var TD_todaybutton = '<td colspan=7 class="dpTodayButtonTD">';
  var TD_days = '<td class="dpDayTD">';
  var TD_selected = '<td class="dpDayHighlightTD" ';
  var TD_Highlight_Select = '<td class="dpDayHighlightTDSelected" ';
  var xTD = '</td>' + crlf;
  var DIV_title = '<div class="dpTitleText">';
  var DIV_selected = '<div class="dpDayHighlight">';
  var xDIV = '</div>';
  var html = TABLE;
  html += TR_title;
  html += TD_buttons + getButtonCode(dateFieldName, thisDay, -1, "▲") + xTD;
  html += TD_title + DIV_title + monthArrayLong[ thisDay.getMonth()] + " " + thisDay.getFullYear() + xDIV + xTD;
  html += TD_buttons + getButtonCode(dateFieldName, thisDay, 1, "▼") + xTD;
  html += xTR;
  html += TR_days;
  for(i = 0; i < dayArrayShort.length; i++)
    html += TD_days + dayArrayShort[i] + xTD;
  html += xTR;
  html += TR;
  for (i = 0; i < thisDay.getDay(); i++)
    html += TD + '&nbsp;' + xTD;
  var firstitem = 0;
  do {
    dayNum = thisDay.getDate();
    var flag = 0;
    for(var i=0; i < tripdates.length; i++) {
      if(tripdates[i] == getDateString(thisDay)) {
        flag = 1;
        firstitem = i;
        break;
      } else {
        flag = 0;
      }
    }

    var dateinwords = dayNum + ' ' + monthArrayLong[ thisDay.getMonth()] +
                      ' '+thisDay.getFullYear();
    var DayInWordsWithoutSpace = dayNum + '' +
                                 monthArrayLong[ thisDay.getMonth()] + ''+
                                 thisDay.getFullYear();
    TD_onclick = " id=date" + DayInWordsWithoutSpace +
                 " onclick=\"updateDateField('" + dateFieldName + "','" +
                   getDateString(thisDay) + "', '" + dateinwords + "','" +
                   flag + "','date" + DayInWordsWithoutSpace +
                   "');this.className=\'dpselectedTD\';\">";
    TD_onclick_selected = "id=date" + DayInWordsWithoutSpace +
                          " onclick=\"updateDateField('" + dateFieldName +
                          "','" + getDateString(thisDay) + "','" +
                          dateinwords + "','" + flag + "','date" +
                          DayInWordsWithoutSpace +
                          "');this.className=\'dpDayHighlightTDSelected\';\">";
    if (flag == 1) {
      if(firstitem == 0 && onceupdate == 0) {
        html += TD_selected + TD_onclick_selected + DIV_selected + dayNum + xDIV + xTD;
        prevFlag = 1;
        selectedDate = "date" + DayInWordsWithoutSpace;
        currentdate = getDateString(thisDay);
      } else {
         html += TD_selected + TD_onclick_selected + DIV_selected + dayNum + xDIV + xTD;
      }
    } else {
      html += TD + TD_onclick + dayNum + xTD;
    }
    if (thisDay.getDay() == 6)
      html += xTR + TR;
    thisDay.setDate(thisDay.getDate() + 1);
  } while (thisDay.getDate() > 1)

  if (thisDay.getDay() > 0) {
    for (i = 6; i > thisDay.getDay(); i--)
      html += TD + "&nbsp;" + xTD;
  }
  html += xTR;
  html += xTD + xTR;
  html += xTABLE;
  _gel(datePickerDivID1).innerHTML = html;

  var element = _gel(selectedDate);
  if (element) {
    if(onceupdate == 1) {
      if(prevFlag == 1) {
        element.className="dpDayHighlightTDSelected";
      } else {
        element.className="dpselectedTD";
      }
    }
  }
  adjustiFrame();

}

/**
 * Updates calender on click of image
 * @params{string} dateFieldName naem of the date filed.
 * @params{string} dateVal.
 * @params{number} adjust.
 * @params{string} label.
 * @return {string} calender html.
 */
function getButtonCode(dateFieldName, dateVal, adjust, label) {
  var newMonth = (dateVal.getMonth () + adjust) % 12;
  var newYear = dateVal.getFullYear() + parseInt((dateVal.getMonth() + adjust) / 12);
  if (newMonth < 0) {
    newMonth += 12;
    newYear += -1;
  }
  if(CalenderType == 0)
    return '<div onClick="refreshDatePicker(\'' + dateFieldName + '\', ' + newYear + ', ' + newMonth + ');">' + label + '</div>';
  else
    return '<div onClick="refreshDatePickerForCalenderType(\'' + dateFieldName + '\', ' + newYear + ', ' + newMonth + ');">' + label + '</div>';
}

/**
 * Gets the date in string format.
 * @params{string} dateval full date format.
 */
function getDateString(dateVal) {
  var dayString = "00" + dateVal.getDate();
  var monthString = "00" + (dateVal.getMonth()+1);
  dayString = dayString.substring(dayString.length - 2);
  monthString = monthString.substring(monthString.length - 2);
  switch (dateFormat) {
    case "dmy" :
      return dayString + dateSeparator + monthString + dateSeparator +
             dateVal.getFullYear();
    case "ymd" :
      return dateVal.getFullYear() + dateSeparator + monthString +
             dateSeparator + dayString;
    case "mdy" :
    default :
      return monthString + dateSeparator + dayString + dateSeparator +
             dateVal.getFullYear();
  }
}


/**
 * Convert a string to a JavaScript Date object.
 * @params{string} dateString date in string format
 * @return{number} dateval.
*/
function getFieldDate(dateString) {
  var dateVal;
  var dArray;
  var d, m, y;
  try {
    dArray = splitDateString(dateString);
    if (dArray) {
      switch (dateFormat) {
        case 'dmy' :
          d = parseInt(dArray[0], 10);
          m = parseInt(dArray[1], 10) - 1;
          y = parseInt(dArray[2], 10);
          break;
        case 'ymd' :
          d = parseInt(dArray[2], 10);
          m = parseInt(dArray[1], 10) - 1;
          y = parseInt(dArray[0], 10);
          break;
        case 'mdy' :
        default :
          d = parseInt(dArray[1], 10);
          m = parseInt(dArray[0], 10) - 1;
          y = parseInt(dArray[2], 10);
          break;
      }
      dateVal = new Date(y, m, d);
    } else if (dateString) {
      dateVal = new Date(dateString);
    } else {
      dateVal = new Date();
    }
  } catch(e) {
    dateVal = new Date();
  }
  return dateVal;
}

/**
 * Splits datestring.
 * @params{string} dateString date in string format
 * @return{array} dArray.
*/
function splitDateString(dateString) {
  var dArray;
  if (dateString.indexOf('/') >= 0)
    dArray = dateString.split('/');
  else if (dateString.indexOf('.') >= 0)
    dArray = dateString.split('.');
  else if (dateString.indexOf('-') >= 0)
    dArray = dateString.split('-');
  else if (dateString.indexOf('\\') >= 0)
    dArray = dateString.split('\\');
  else
    dArray = false;
  return dArray;
}

/**
 * Updates date field.
 * @params{string} dateFieldName date field name.
 * @params{string} dateString date in string format.
 * @params{string} dateinwords date in words.
 * @params{number} flag
 * @params{string} selectedTDID id of selected td.
*/
function updateDateField(dateFieldName, dateString,
                         dateinwords, flag, selectedTdId) {
  onceupdate = 1;
  if(CalenderType == 0) {
    if(_gel(selectedDate) != null) {
      if(selectedDate != undefined && prevFlag != undefined ) {
        if(selectedTdId != selectedDate && prevFlag == 1) {
        _gel(selectedDate).className="dpDayHighlightTD";
        }
        if(selectedTdId != selectedDate && prevFlag == 0) {
        _gel(selectedDate).className="dpTD";
        }
      }
    }
    selectedDate = selectedTdId;
    prevFlag = flag;
    var targetDateField = document.getElementsByName (dateFieldName).item(0);
    if (dateString)
      targetDateField.value = dateString;
    _gel('selecteddate').innerHTML = dateinwords;
    _gel('dateradio').checked = true;
    if ((dateString) && (typeof(datePickerClosed) == "function"))
      datePickerClosed(targetDateField);
  } else {
    if(_gel('startDate') != null || _gel('endDate') != null) {
      var durationElement = _gel('editDaysBox');
      if (durationElement) {
        var strDuration = durationElement.value;
        if (strDuration == '') {
          strDuration = '1';
          durationElement.value = strDuration;
        }
        var days = parseInt(strDuration, 10) - 1;
        var date = new Date(dateString);
        if(curImage == 'calenderimg1') {
          date.setDate(date.getDate() + days);
          var stdate = formatCalDate(date.getMonth() + 1, date.getDate(),
                       date.getFullYear());
          _gel('endDate').value = stdate;
        } else {
          var sdate = _gel('startDate');
          if (sdate.value == '' || sdate.value == 'unspecified') {
            date.setDate(date.getDate() - days);
            var endate = formatCalDate(date.getMonth() + 1, date.getDate(),
                         date.getFullYear());
            sdate.value = endate;
          } else {
            var days = days_between(date, new Date(sdate.value));
            if (days >= 0)
              days += 1;
            else
              days = 0;

            durationElement.value = days ;
          }
        }
      }
    }
    var targetDateField = document.getElementsByName (dateFieldName).item(0);
    if (dateString)
      targetDateField.value = dateString;
    var pickerDiv = _gel(datePickerDivID);
    pickerDiv.style.visibility = 'hidden';
    pickerDiv.style.display = 'none';
  }
  currentdate = dateString;
}

/**
 * Finds days between dates.
 * @param{date} date1
 * @param{date} date2
 * @return{} difference.
 */
function days_between(date1, date2) {
    var ONE_DAY = 1000 * 60 * 60 * 24
    var date1_ms = date1.getTime()
    var date2_ms = date2.getTime()
    var difference_ms = date1_ms - date2_ms
    return Math.round(difference_ms/ONE_DAY)
}
/**
 * Adjusts frame.
 * @param{string} pickerDiv id of date picker.
 * @param{string} iFrameDiv id of frame div.
 */
function adjustiFrame(pickerDiv, iFrameDiv) {
  var is_opera = (navigator.userAgent.toLowerCase().indexOf("opera") != -1);
  if (is_opera)
    return;
  try {
    if (!_gel(iFrameDivID)) {
      FrameDivID + "' src='javascript:false;' scrolling='no' frameborder='0'>";
      var newNode = document.createElement("iFrame");
      newNode.setAttribute('id', iFrameDivID);
      newNode.setAttribute('src', 'javascript:false;');
      newNode.setAttribute('scrolling', 'no');
      newNode.setAttribute ('frameborder', '0');
      document.body.appendChild(newNode);
    }
    if(CalenderType != 0) {
      if (!pickerDiv)
      pickerDiv = _gel(datePickerDivID);
    } else {
      if (!pickerDiv)
      pickerDiv = _gel(datePickerDivID1);
    }
    if (!iFrameDiv)
      iFrameDiv = _gel(iFrameDivID);
    try {
      iFrameDiv.style.position = 'absolute';
      iFrameDiv.style.width = pickerDiv.offsetWidth;
      iFrameDiv.style.height = pickerDiv.offsetHeight ;
      iFrameDiv.style.top = pickerDiv.style.top;
      iFrameDiv.style.left = pickerDiv.style.left;
      iFrameDiv.style.zIndex = pickerDiv.style.zIndex - 1;
      iFrameDiv.style.visibility = pickerDiv.style.visibility ;
      iFrameDiv.style.display = pickerDiv.style.display;
    } catch(e) {
    }
  } catch (ee) {
  }
}

/**
 * Function to format date in 0m/0d/Y
 * @param {integer} day
 * @param {integer} month
 * @param {string} year
 * @return{string} datestring date in required format
 */
function formatCalDate(month, day, year) {
  var dateString;
  if(day < 10)
    day = '0'+ day;
  if(month < 10)
    month = '0' + month;
  dateString = month +'/'+day+'/'+year;
  return dateString;
 }
