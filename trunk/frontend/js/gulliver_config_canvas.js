/**
 * @fileoverview Code to handle config values of gulliver gadget
 * home view functionality.
 * @author gadgetfactory@google.com (gadgetfactory)
 */

/**
 * User prefs.
 * @type {_IG_Prefs}
 */
var prefs = new _IG_Prefs();

var timerMsg;
var gViewer;
var gPopUpIndex = -1;
var markerIndex;
var currentMarker;
var currentMarkerIcon;
var dragItemCounter = 0;
var gCurrentTripId = '';
var refreshUIData;
var tabAccuracy = [2, 4, 6, 10, 12, 13, 16, 16, 16, 16];
var gParams = gadgets.views.getParams();
var strSelectedItemId = '';
var gMap = null;
var arrSearchResults = [];
var geocoder = null;
var sortCol;
var bAscending = true;
var highLightMarker;
var arrSortedTrips = [];
var nCurrentPageNum = 1;
var highlightedItem;
var timerLonelyCallback = 0;
var gOpenSocial = [];
var iternaryMarkers = [];
var dragObject = null;
var mouseOffset = null;
var dropTargets = [];
var gWhoAllWorking = [];
var gFetchedData = [];

/**
 * Base path for all images and raw code files.
 * @const
 */
var FILE_SERVER = 'http://gulliver.googlecode.com/svn/trunk/frontend';

/** @const */
var BASE_URL = 'http://travel-gadget1.appspot.com';
/** @const */
var EXPORT_SERVER = 'http://travel-gadget1.appspot.com/exportAllTripItems';
/** @const */
var MAX_EMAILS = 5;
/** @const */
var MAX_TRIP_DURATION = 60;
/** @const */
var DEFAULT_ZOOM_LEVEL = 12;
/** @const */
var MAX_ITEM = 20;
/** @const */
var MAX_SHOWN_TRIPS = 20;
/** @const */
var DEFAULT_DURATION = 7;
/** @const */
var DEFAULT_RATING = 3;
/** @const */
var MAX_RATING = 5;
/** @const */
var STAR_OFF_IMG = 'http://www.google.com/images/syt-star-off.gif';
/** @const */
var STAR_ON_IMG = 'http://www.google.com/images/syt-star-on.gif';
/** @const */
var MAX_STRING_LENGTH = 40;
/** @const */
var MAX_DESC_LENGTH = 35;
/** @const */
var MAX_TRIPITEM_NAME = 25;

/** @const */
var UP_ARROW_IMG = '<img width="7" height="6" ' +
    'src="' + FILE_SERVER + '/images/up_arrow.gif" ' +
    'style="margin-top:5px;"/>';

/** @const */
var DOWN_ARROW_IMG = '<img width="7" height="6" ' +
    'src="' + FILE_SERVER + '/images/down_arrow.gif" ' +
    'style="margin-top:5px;"/>';

/** @const */
var LOADING_TPL = '<div id="loadingSection" class="loading"><img src="' +
    'http://www.google.com/ig/images/spinner.gif"/>&nbsp;' +
    prefs.getMsg('loading') + '</div>';

/** @const */
var SEARCH_URL_TPL = 'http://www.google.com/local?q=%KEYWORD%&near=%LOCATION%' +
    '&start=%PAGE_NUM%&num=20&output=kml';

/** Map icon */
var baseIcon = new GIcon(G_DEFAULT_ICON);

/**
 * Sets the shadow property of marker.
 */
baseIcon.shadow = 'http://www.google.com/mapfiles/shadow50.png';

/**
 * Sets the size of icon.
 */
baseIcon.iconSize = new GSize(20, 34);

/**
 * Sets the size of shadow.
 */
baseIcon.shadowSize = new GSize(37, 34);

/**
 * Sets the anchor property of marker.
 */
baseIcon.iconAnchor = new GPoint(9, 34);

/**
 * Sets the info window point for marker.
 */
baseIcon.infoWindowAnchor = new GPoint(9, 2);


/**
 * Sets the variable if container is iGoogle.
 */
var isIGoogle = _unesc(_args()['synd']);

/**
 * Enum for column of trips for sorting.
 * @enum {number}.
 */
var enTripCol = {
  NAME: 0, DATE: 1, LOCATION: 2, OWNER: 3, RATING: 4
};

/**
 * Enum for transaction with database.
 * @enum {number}.
 */
var enDBTransaction = {
  SUCCESS: 0, ERROR: 1
};
