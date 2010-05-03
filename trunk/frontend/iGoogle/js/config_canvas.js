/**
 * @fileoverview Config file for the canvas view of gadget.
 * 
 */

/**
 * Global array to store searched results for google and lonely
 * planet searches.
 * @type {Array}
 */
var arrSearchResults = [];

/**
 * To hold all the trips used in canvas view.
 * @type {Object}
 */
var tripDB = {};

/**
 * Variable to store value of sorting column upon which sorting will be done.
 * Sorting will be done on first column by default.
 * @type {number}
 */
var sortCol = 1;

/**
 * Variable to store sorting order.
 * @type {boolean}
 */
var isAscending;

/**
 * Array to store searched trips.
 * @type {Array}
 */
var searchedTrips = [];

/**
 * Contains id of currently selected item.
 * @type {string}
 */
var selectedItemId;

/**
 * @const
 * @type {string}
 */
var ARROW_IMG = '<img width="7" height="6" src="http://www.google.com/c.gif"/>';

/**
 * @const
 * @type {number}
 */
var MAX_DESC_LENGTH = 35;

/**
 * Flag to check whether its search trip request or not.
 * @type {boolean}
 */
var searchFlag = true;

/**
 * @const
 * @type {number}
 */
var MAX_ITEM = 20;

/**
 * Global array to handle current marker information.
 * @type {Object}
 */
var currentMarker = {
  marker: '',
  icon: '',
  index: -1
};

/**
 * Defines constant for column names of trips for sorting.
 * @type {Object}
 */
var enTripCol = {
  NAME: 0, DATE: 1, LOCATION: 2, OWNER: 3, DAY: 4
};

/**
 * Default duration.
 * @const
 * @type {number}
 */
var DEFAULT_DURATION = 7;

/**
 * Flag for drop down list.
 * @type {boolean}
 */
var isOpened;

/**
 * Mapping for lonely planet search and categories image.
 * @enum {string}
 */
var LONELY_IMG = {
  'see': {
    'name': prefs.getMsg('sights'),
    'searchUrl': 'http://www.gstatic.com/ig/modules/trippy/pin_sml_sights.cache.png',
    'className' : 'lp-sights'
  },
  'shop': {
    'name': prefs.getMsg('shopping'),
    'searchUrl': 'http://www.gstatic.com/ig/modules/trippy/pin_sml_shops.cache.png',
    'className' : 'lp-shopping'
  },
  'eat': {
    'name': prefs.getMsg('restaurants'),
    'searchUrl': 'http://www.gstatic.com/ig/modules/trippy/pin_sml_restaurants.cache.png',
    'className' : 'lp-restaurants'
  },
  'night': {
    'name': prefs.getMsg('entertainment'),
    'searchUrl': 'http://www.gstatic.com/ig/modules/trippy/pin_sml_entertainment.cache.png',
    'className' : 'lp-entertainment'
  },
  'sleep': {
    'name': prefs.getMsg('hotels'),
    'searchUrl': 'http://www.gstatic.com/ig/modules/trippy/pin_sml_hotels.cache.png',
    'className' : 'lp-hotels'
  },
  'general': {
    'name': prefs.getMsg('general'),
    'searchUrl': 'http://www.gstatic.com/ig/modules/trippy/pin_sml_general.cache.png',
    'className' : 'lp-general'
  },
  'do': {
    'name': prefs.getMsg('activities'),
    'searchUrl': 'http://www.gstatic.com/ig/modules/trippy/pin_sml_do.cache.png',
    'className' : 'lp-activities'
  }
};

/**
 * To hold gadget parameters.
 * @type {Array}
 */
var gParams = gadgets.views.getParams();

/**
 * To hold for a trip its items.
 * @type {Array}
 */
var gTripItemDB = [];

/**
 * Global variable to hold trip dates.
 * @type {Array}
 */
var tripDates = [];

/**
 * To hold all current trips information i.e. Marker, current trips etc.
 * @type {Object}
 */
var gCurrentTripsData = {};
