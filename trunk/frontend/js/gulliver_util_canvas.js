/**
 * @fileoverview Library of all utility methods for canvas view.
 * @author gadgetfactory@google.com (gadgetfactory)
 */

/**
 * This methods takes HTML as input and removes all data inside < and > tags.
 * @param {htmlString} htmlString as string.
 * @return {string} htmlString with no html tags.
 */
function stripHtml(htmlString) {
  return htmlString.replace(/<\/?[^>]+(>|$)/g, '');
}

/**
 * Converts the string response into DOM object.
 * @param {Object} data Response as string.
 * @return {Object} DOM object.
 */
function domParser(data) {
  var xmlDoc;
  if (window.ActiveXObject) {
    xmlDoc = new ActiveXObject('Microsoft.XMLDOM');
    xmlDoc.loadXML(data.text);
  } else {
    xmlDoc = new DOMParser().parseFromString(data.text, 'text/xml');
  }
  return xmlDoc;
}

/**
 * Trim spaces from both side of string.
 * @return {string} Replaced string.
 */
String.prototype.strip = function() {
  return this.replace(/^\s+/, '').replace(/\s+$/, '');
};

/**
 * Checks whether value is empty.
 * @param {string} value Value to be checked.
 * @return {boolean} Returns true if empty, else false.
 */
function isEmpty(value) {
  return (!value || value == null || value == undefined ||
          value == 'None' || value == prefs.getMsg('unspecified'));
}

/**
 * Check if passed character is numeric.
 * @param {string} strValue The string to be validated.
 * @return {boolean} True if numeric else false.
 */
function isNumeric(strValue) {
  var strInvalidChars = '0123456789';
  var strChar;
  var strValueLen = strValue.length;
  for (var i = 0; i < strValueLen; i++) {
    strChar = strValue.charAt(i);
    if (strInvalidChars.indexOf(strChar) != -1) {
      return true;
    }
  }
  return false;
}

/**
 * Validates email id.
 * @param {string} email Email id as string.
 * @return {boolean} True if email id list is valid.
 */
function validateEmailid(email) {
  var reg =
      /^(\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+(\ )*(\,)?(\;)?(\ )*)+$/;
  return reg.test(email);
}

/**
 * It formats the date in required format.
 * @param {string} strDate The date to be converted.
 * @param {string} format Date format.
 * @return {string} New date as string.
 */
function formatDate(strDate, format) {
  var strRetVal = '';
  var day, month, year;
  strDate = strDate.replace(/-/g, '/');
  var date = new Date(strDate);
  month = date.getMonth() + 1;
  if (month <= 9) {
    month = '0' + month;
  }
  day = date.getDate();
  year = date.getFullYear();
  if (day <= 9) {
    day = '0' + day;
  }
  if (format == '%m/%d/%Y') {
    strRetVal = month + '/' + day + '/' + date.getFullYear();
  } else if (format == '%m-%d-%Y') {
    strRetVal = month + '-' + day + '-' + date.getFullYear();
  } else if (format == '%M%d%Y') {
    strRetVal = monthArrayMed[parseInt(month, 10) - 1] +
                ' ' + day + ', ' + year;
  } else if (format == '%M%d') {
    strRetVal = monthArrayMed[parseInt(month, 10) - 1] +
                ' ' + day;
  } else if (format == '%MM%d') {
    strRetVal = monthArrayLong[parseInt(month, 10) - 1] +
                ' ' + day;
  } else if (format == '%MM%d%Y') {
    strRetVal = monthArrayLong[parseInt(month, 10) - 1] +
                ' ' + day + ', ' + year;
  }
  return strRetVal;
}

/**
 * Calcuates the difference between two dates.
 * @param {string} strDate1 first date as string.
 * @param {string} strDate2 second date as string.
 * @return {number} number of days.
 */
function getDateDiff(strDate1, strDate2) {
  strDate1 = strDate1.replace(/-/g, '/');
  strDate2 = strDate2.replace(/-/g, '/');
  var date1 = new Date(strDate1);
  var date2 = new Date(strDate2);
  var oneDay = 24 * 60 * 60 * 1000;
  var noOfDays = Math.ceil((date2.getTime() - date1.getTime()) / oneDay);
  return noOfDays;
}

/**
 * It adds the number of days in to a date.
 * @param {string} strDate date as string.
 * @param {number} days number of days.
 * @param {string} format Date format.
 * @return {string} new date as string.
 */
function addDaysToDate(strDate, days, format) {
  strDate = strDate.replace(/-/g, '/');
  var date = new Date(strDate);
  date.setDate(date.getDate() + days);
  if (format == '%m/%d/%Y') {
    var month;
    var tdate;
    if (date.getMonth() < 10) {
      month = '0' + (date.getMonth() + 1);
    } else {
      month = (date.getMonth() + 1).toString();
    }
    if (date.getDate() < 10) {
      tdate = '0' + date.getDate();
    } else {
      tdate = date.getDate().toString();
    }
    var dateInFormat = month + '/' + tdate + '/' + date.getFullYear();
    return dateInFormat;
  } else if ('%MM%d%W') {
    return (monthArrayLong[date.getMonth()] + ' ' +
            date.getDate() + ' (' + dayArrayLong[date.getDay()] + ')');
  } else {
    return date.toDateString();
  }
}

/**
 * Handler for sorting.
 * @param {object} Obj1 The first object of trip.
 * @param {object} Obj2 The second object of trip.
 * @return {num} Returns the code.
 */
function sortHandler(Obj1, Obj2) {
  var retVal;
  switch (sortCol) {
    case enTripCol.NAME:
      if (Obj1.name.toLowerCase() > Obj2.name.toLowerCase()) {
        retVal = 1;
      } else if (Obj1.name.toLowerCase() < Obj2.name.toLowerCase()) {
        retVal = -1;
      }
      break;
    case enTripCol.LOCATION:
      if (Obj1.loc.toLowerCase() > Obj2.loc.toLowerCase()) {
        retVal = 1;
      } else if (Obj1.loc.toLowerCase() < Obj2.loc.toLowerCase()) {
        retVal = -1;
      }
      break;
    case enTripCol.DATE:
      if (Obj1.sdate > Obj2.sdate) {
        retVal = 1;
      } else if (Obj1.sdate < Obj2.sdate) {
        retVal = -1;
      }
      break;
    case enTripCol.OWNER:
      if (Obj1.ownerName.toLowerCase() > Obj2.ownerName.toLowerCase()) {
        retVal = 1;
      } else if (Obj1.ownerName.toLowerCase() < Obj2.ownerName.toLowerCase()) {
        retVal = -1;
      }
      break;
    case enTripCol.RATING:
      if (Obj1.rating > Obj2.rating) {
        retVal = 1;
      } else if (Obj1.rating < Obj2.rating) {
        retVal = -1;
      }
      break;
    case enTripCol.DAY:
      if (Obj1.day > Obj2.day) {
        retVal = 1;
      } else if (Obj1.day < Obj2.day) {
        retVal = -1;
      }
      break;
  }
  if (retVal == 1) {
    return (bAscending) ? 1 : -1;
  } else if (retVal == -1) {
    return (bAscending) ? -1 : 1;
  } else {
    return 0;
  }
}

/**
 * Function to wrap long text.
 * @param {string} strText Text which needs to be wrapped.
 * @return {string} wrapped string.
 */
function wrapText(strText) {
  var resultString = [];
  while (strText.length > MAX_STRING_LENGTH) {
    resultString.push(strText.substr(0, MAX_STRING_LENGTH));
    resultString.push('<br>');
    strText = strText.substr(MAX_STRING_LENGTH);
  }
  resultString.push(strText);
  return resultString.join('');
}

/**
 * Validates the required fields.
 */
function validateTrip() {
  var createLocRef = _gel('create-location');
  if (createLocRef.value != '') {
    var tripLoc = _gel('create-location').value;
    var bInvalid = isNumeric(tripLoc);
    if (!bInvalid) {
      showAddress(tripLoc, 1);
    } else {
      createLocRef.value = '';
      createLocRef.focus();
      _gel('server_msg').style.display = 'block';
      _gel('server_msg').innerHTML = prefs.getMsg('numeric_err');
      _IG_AdjustIFrameHeight();
    }
  } else {
    createLocRef.focus();
  }
}
