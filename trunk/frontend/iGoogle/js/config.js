/**
 * @fileoverview Config file for gadget.
 * 
 */

/**
 * User prefs.
 * @type {_IG_Prefs}
 */
var prefs = new _IG_Prefs();

/**
 * Base url of data server.
 * @const
 * @type {string}
 */
var BASE_URL = 'http://trippy-app.appspot.com';

/**
 * Enum for transaction response.
 * @type {Object}
 */
var transResponse = {
  SUCCESS: 0,
  ERROR: 1
};

/**
 * Response code for successful request.
 * @const
 * @type {number}
 */
var RESPONSE_SUCCESS = 200;

/**
 * Variable to store Time out value in seconds.
 * @const
 * @type {number}
 */
var TIME_OUT = 10;

/**
 * Variable to hold minimessage element.
 * @type {Object}
 */
var timerMsg = new _IG_MiniMessage(null, _gel('server-msg'));

/**
 * Array to store Owner's trip data used in Home view.
 * @type {Array}
 */
var gTripData = [];

/**
 * Variable to hold object of GMap2 type.
 * @type {Object}
 */
var gMap;

/**
 * Global array for holding refernce for deleted markers.
 * @type {Array}
 */
var gDeletedMarkers = [];

/**
 * Array to identify zoom level for a trip based on accuracy level.
 * @type {Array}
 */
var tripAccuracy = [2, 4, 6, 10, 12, 13, 16, 16, 16, 16];

/**
 * Object for GClientGeocoder.
 * @type {Object}
 */
var geocoder;

/**
 * To hold current viewer.
 * @type {Object}
 */
var gViewer;

/**
 * Array to store opensocial data.
 * @type {Array}
 */
var gOpenSocial = [];
/**
 * Variables to hold voting values for a trip.
 * @type {number}
 */
var trip_thumb_up, trip_thumb_down;


/**
 * Loading template.
 * @const
 * @type {string}
 */
var LOADING_TPL = '<div style="text-align:center"><span class="loading">' +
    '<img src="http://www.google.com/ig/images/spinner.gif"/>&nbsp;&nbsp;' +
    prefs.getMsg('loading') + '</span></div>';
