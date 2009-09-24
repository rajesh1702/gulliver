/**
 * @fileoverview Code to handle config values of gulliver gadget
 * home view functionality.
 * @author gadgetfactory@google.com (gadgetfactory)
 */

/**
 * Default error value.
 * @const
 * @type {number}
 */
var ERROR_VALUE = 1;

/**
 * User prefs.
 * @type {_IG_Prefs}
 */
var prefs = new _IG_Prefs();

/**
 * Appengine base url.
 * @const
 * @type {string}
 */
var BASE_URL = 'http://travel-gadget1.appspot.com';

/**
 * Default duration.
 * @const
 * @type {number}
 */
var DEFAULT_DURATION = 7;

/**
 * Default duration.
 * @const
 * @type {number}
 */
var DEFAULT_RATING = 3;

/**
 * Array to store trip data.
 * @type {Array}
 */
var gTripData = [];

/**
 * Flag to check whether from home trip or not.
 * @type {boolean}
 */
var fromTripHome = false;

/**
 * Id of a trip.
 * @type {string}
 */
var tripId = null;

/**
 * To store current trip name.
 * @type {string}
 */
var tripName = null;

/**
 * To store the id of the owner.
 * @type {number}
 */
var ownerId = null;

/**
 * To store owner name.
 * @type {string}
 */
var ownerName = null;

/**
 * To store the trip location.
 * @type {string}
 */
var tripLocation = null;

/**
 * Latitude of current trip location.
 * @type {string}
 */
var lat = null;

/**
 * Longitude of current trip location.
 * @type {string}
 */
var lang = null;

/**
 * To store current trip location accuracy.
 * @type {number}
 */
var accuracy = null;

/**
 * Object for GClientGeocoder.
 * @type {Object}
 */
var geocoder = null;

/**
 * To store current trip duration.
 * @type {number}
 */
var duration;

/**
 * To store current trip start and end dates.
 * @type {string}
 */
var gStartDate = gEndDate = null;

/**
 * To store current trip rating.
 * @type {number}
 */
var gRating = null;

/**
 * Array to store opensocial data.
 * @type {Array}
 */
var gOpenSocial = [];

/**
 * To hold current viewer.
 * @type {Object}
 */
var gViewer;
