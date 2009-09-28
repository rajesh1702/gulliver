
/**
 * For development purpose.
 * @param {string} The message to be displayed in console window.
 */
function trace(msg) {
  _gel('debug-console').innerHTML += '<div>' + msg + '</div>';
}

/**
 * Pour the data in template string.
 * @param {Object} datObject The data object to be filled in template string.
 * @return {string} The new string created from template string and filled
 *     with the given data.
 */
String.prototype.supplant = function(datObject) {
  return this.replace(/{([^{}]*)}/g,
    function(match, firstSubMatch) {
      var replace = datObject[firstSubMatch];
      return (typeof replace === 'string' || typeof replace === 'number') ?
          replace : match;
    }
  );
};

// Third party includes.
// calculate the current window width
function pageWidth() {
  return window.innerWidth != null ? window.innerWidth : document.documentElement && document.documentElement.clientWidth ? document.documentElement.clientWidth : document.body != null ? document.body.clientWidth : null;
}

// calculate the current window height
function pageHeight() {
  return window.innerHeight != null? window.innerHeight : document.documentElement && document.documentElement.clientHeight ? document.documentElement.clientHeight : document.body != null? document.body.clientHeight : null;
}

// calculate the current window vertical offset
function topPosition() {
  return typeof window.pageYOffset != 'undefined' ? window.pageYOffset : document.documentElement && document.documentElement.scrollTop ? document.documentElement.scrollTop : document.body.scrollTop ? document.body.scrollTop : 0;
}

// calculate the position starting at the left of the window
function leftPosition() {
  return typeof window.pageXOffset != 'undefined' ? window.pageXOffset : document.documentElement && document.documentElement.scrollLeft ? document.documentElement.scrollLeft : document.body.scrollLeft ? document.body.scrollLeft : 0;
}
