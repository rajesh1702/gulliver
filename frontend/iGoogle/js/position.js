/**
 * @fileoverview Contains library functions for calculating
 * properties. i.e. Page height, Page's top position, Absolute position
 * of element etc.
 * 
 */

/**
 * Calculates the height of current page.
 * @return {number} Height of current page.
 */
function getPageHeight() {
  var docElement = document.documentElement;
  return window.innerHeight ?
      window.innerHeight :
      docElement && docElement.clientHeight ?
          docElement.clientHeight :
          document.body ? document.body.clientHeight : null;
}

/**
 * Calculates the vertical offset of current page.
 * @return {number} Returns the vertical offset.
 */
function getTopPosition() {
  return typeof window.pageYOffset != 'undefined' ?
      window.pageYOffset :
      document.documentElement && document.documentElement.scrollTop ?
          document.documentElement.scrollTop :
          document.body.scrollTop ? document.body.scrollTop : 0;
}

/**
 * Calculates the horizontal left offset of the current page.
 * @return {number} Returns the horizontal left offset.
 */
function getLeftPosition() {
  return typeof window.pageXOffset != 'undefined' ?
      window.pageXOffset :
      document.documentElement && document.documentElement.scrollLeft ?
          document.documentElement.scrollLeft :
          document.body.scrollLeft ? document.body.scrollLeft : 0;
}


/**
 * Returns the width of current page.
 * @return {number} Width of current page.
 */
function getPageWidth() {
  var docElement = document.documentElement;
  return window.innerWidth ?
      window.innerWidth :
      docElement && docElement.clientWidth ?
          docElement.clientWidth :
          document.body ? document.body.clientWidth : null;
}
