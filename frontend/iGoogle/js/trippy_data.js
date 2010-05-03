/**
 * @fileoverview Class for Gulliver gadget of type JGulliverData.
 * @author gadgetfactory@google.com (gadgetfactory)
 */

/**
 * Class defines singleton object for itinerary.
 * @constructor
 */
function JGulliverData() {
  this.nSelectedTripIndex = -1;
  this.arrTripData = [];
}

/**
 * Instance of JGulliverData class.
 * @type {JGulliverData}.
 * @constructor
 */
JGulliverData.instance = null;

/**
 * It returns the instance of itinerary if exists else create new
 * instance.
 * @return {Object} object of JGulliverData.
 */
JGulliverData.getInstance = function() {
  if (!JGulliverData.instance) {
    JGulliverData.instance = new JGulliverData();
  }
  return JGulliverData.instance;
};

/**
 * Creates a trip object and returns it.
 * @return {Object} Returns trip object.
 */
function getTripObject() {
  return {
    id: '',
    ownerId: '',
    ownerName: '',
    name: '',
    loc: '',
    lat: '',
    lng: '',
    accuracy: 0,
    zoomLevel: 12,
    duration: 7,
    sdate: '',
    fdate: '',
    thumbUp: 0,
    thumbDown: 0
  };
}
