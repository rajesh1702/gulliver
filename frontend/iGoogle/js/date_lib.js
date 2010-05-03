/**
 * @fileoverview Contains date related common utility functions.
 * @author gadgetfactory@google.com (gadgetfactory)
 */

/**
 * Defines DateLib object.
 * @constructor
 */
function DateLib() {}

/**
 * List of short name of weekdays.
 * @type {Array}
 */
DateLib.shortWeekDays = ['S', 'M', 'T', 'W', 'T', 'F', 'S'];

/**
 * Array of full name of weekdays.
 * @type {Array}
 */
DateLib.longWeekDays = [
  'Sunday',
  'Monday',
  'Tuesday',
  'Wednesday',
  'Thursday',
  'Friday',
  'Saturday'
];

/**
 * Array of month names denoted by first three characters of a month.
 * @type {Array}
 */
DateLib.shortMonthNames = [
  'Jan',
  'Feb',
  'Mar',
  'Apr',
  'May',
  'Jun',
  'Jul',
  'Aug',
  'Sep',
  'Oct',
  'Nov',
  'Dec'
];

/**
 * Array of full names of month.
 */
DateLib.longMonthNames = [
  'January',
  'February',
  'March',
  'April',
  'May',
  'June',
  'July',
  'August',
  'September',
  'October',
  'November',
  'December'
];


/**
 * Returns the days count between two dates (date1 - date2).
 * @param {Date} date1 First date.
 * @param {Date} date2 Second date.
 * @return {number} Number of days between two days, where date1 > date2.
 */
DateLib.daysBetween = function(date1, date2) {
  // Number of milliseconds in a day.
  var MILLISECONDS_IN_ONE_DAY = 1000 * 60 * 60 * 24;
  var diffMilliSeconds = date1.getTime() - date2.getTime();
  return Math.round(diffMilliSeconds / MILLISECONDS_IN_ONE_DAY);
};

/**
 * Returns number of days between two dates.
 * @param {string} startDate Start date in formats like m/d/Y(04/12/2010),
 *     m-d-Y(04-12-2010).
 * @param {string} endDate End date.
 * @return {number} Number of days.
 */
DateLib.getDateDiff = function(startDate, endDate) {
  startDate = startDate.replace(/-/g, '/');
  endDate = endDate.replace(/-/g, '/');
  var date1 = new Date(startDate);
  var date2 = new Date(endDate);
  return DateLib.daysBetween(date2, date1);
};

/**
 * It convert single digit value in to two digits.
 * @param {number} num Positive integer between 0-9.
 * @return {number} Two digit number.
 */
DateLib.makeTwoDigit = function(num) {
  if (num > 0 && num < 10) {
    num = '0' + num;
  }
  return num;
};

/**
 * It formats the passed date. If date is 5 Apr 2010 then
 * format specifiers are -
 * W (Monday), MM (April), M (Apr), mm (04), m (4), dd (05), d (5) and
 * y & Y for 2010. All attributes to be passed in {} i.e {W}.
 * @param {string} inputDate The date to be formatted in formats like
 *     04/05/2010 or 04-05-2010.
 * @param {string} opt_format Date format (optional). e.g, to get date in
 *     following formats we pass opt_format as shown.
 *     (12/16/2010) opt_format will be "{m}/{d}/{Y}".
 *     (12-16-2010), opt_format will be "{m}-{d}-{Y}".
 *     (16 Dec, 2010), opt_format will be "{d} {M}, {Y}".
 *     (16 December, 2010), opt_format will be "{d} {MM}, {Y}".
 *     (Thursday, 16 December 2010), opt_format will be "{W}, {d} {MM} {Y}".
 * @return {string} Returns formatted date according to specified format.
 *     Default format is '{m}/{d}/{Y}'.
 */
DateLib.formatDate = function(inputDate, opt_format) {
  opt_format = opt_format || '{m}/{d}/{Y}';
  inputDate = inputDate.replace(/-/g, '/');
  var date = new Date(inputDate);
  var dataObj = {
    'W': DateLib.longWeekDays[date.getDay()],
    'MM': DateLib.longMonthNames[date.getMonth()],
    'M': DateLib.shortMonthNames[date.getMonth()],
    'mm': DateLib.makeTwoDigit(date.getMonth() + 1),
    'm': (date.getMonth() + 1),
    'dd': DateLib.makeTwoDigit(date.getDate()),
    'd': date.getDate(),
    'Y': date.getFullYear(),
    'y': date.getFullYear()
  };
  return Util.supplant(opt_format, dataObj);
};

/**
 * It adds the number of days to a date to get new date
 * e.g if 5 is added to 3/25/2009 it will become 3/30/2009.
 * @param {string} inputDate Date as string in mdy format.
 * @param {number} days Number of days.
 * @param {string} opt_format Date format (optional).
 * @return {string} New date as string.
 */
DateLib.addDaysToDate = function(inputDate, days, opt_format) {
  inputDate = inputDate.replace(/-/g, '/');
  var date = new Date(inputDate);
  date.setDate(date.getDate() + parseInt(days, 10));
  var dateStr = date.toDateString();
  return opt_format ? DateLib.formatDate(dateStr, opt_format) : dateStr;
};
