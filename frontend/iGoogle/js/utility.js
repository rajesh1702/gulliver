/**
 * @fileoverview Contains common utility functions.
 * 
 */

/**
 * Defines Util object.
 * @constructor
 */
function Util() {}

/**
 * Check if passed character is numeric.
 * @param {string} passedString The string to be validated.
 * @return {boolean} Returns true if numeric else returns false.
 */
Util.isNumeric = function(passedString) {
  var regEx = /^\-?\d*\.?\d+$/;
  return regEx.test(passedString);
};

/**
 * Checks whether value is empty.
 * @param {string} value Value to be checked.
 * @return {boolean} Returns true if value is empty else returns false.
 */
Util.isEmpty = function(value) {
  return ((value != '0') &&
      (!value || value == 'None' || value == 'unspecified'));
};

/**
 * Strips out everything between < and >.
 * @param {htmlString} htmlString Input string.
 * @return {string} String if data is not inside < and > else empty string.
 */
Util.stripHtml = function(htmlString) {
  return htmlString.replace(/<\/?[^>]+(>|$)/g, '');
};

/**
 * Validates single or multiple email id.
 * @param {string} emailId Comma separated email ids.
 * @return {boolean} Returns true if email id is valid else returns
 *     false.
 */
Util.validateEmailId = function(emailId) {
  emailId = emailId + ';';
  var reg = /^(\w+([.-]?\w+)*@\w+([.-]?\w+)*(\.\w{2,3})+[\s,;]+)+$/;
  return reg.test(emailId);
};

/**
 * Gives the absolute left and top position of an element.
 * @param {Object} element Element whose position is to be calculated.
 * @return {Object} Position of the element.
 */
Util.getElementPosition = function(element) {
  var left = 0;
  var top = 0;
  while (element) {
    left += element.offsetLeft;
    top += element.offsetTop;
    element = element.offsetParent;
  }
  return {x: left, y: top};
};

/**
 * Pour the data in template string.
 * @param {string} sourceString String in which replace is to be made.
 * @param {Object} dataObject The data object to be filled in template
 *     string.
 * @return {string} The new string created from template string and
 *     filled with the given data.
 */
Util.supplant = function(sourceString, dataObject) {
  // Replaces {key} with the corresponding value in object.
  return sourceString.replace(/{([^{}]+)}/g,
    function(match, firstSubMatch) {
      var replace = dataObject[firstSubMatch];
      return (typeof replace === 'string' ||
              typeof replace === 'number') ?
          replace : match;
    }
  );
};
