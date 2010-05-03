/**
 * @fileoverview Code related to drag functionality.
 * @author gadgetfactory@google.com (gadgetfactory)
 */

/**
 * This function overrides the function inside 'drag' library, to fix
 * the problem when the drag target element is inside a 'scrollable' container.
 * @param {HTMLElement} element The HTML element.
 * @return {number} Returns the top position of the passed element in pixels.
 */
function _IG_DragPosition_top(element) {
  var top = 0;
  if (element.offsetParent) {
    var dragElement = element;
    for (; element.offsetParent; element = element.offsetParent) {
      top += element.offsetTop;
    }
    for (; dragElement.parentNode; dragElement = dragElement.parentNode) {
      if (dragElement.parentNode.scrollTop) {
        top -= dragElement.parentNode.scrollTop;
      }
    }
  } else if (element.y) {
    top = element.y;
  }
  return top;
}

/**
 * Function to initialize drag for searched elements. This method is called
 * when searched result is dragged to itinerary area.
 */
function initDrag() {
  var duration = getTripById(gCurrentTripsData.currentTripId).duration;
  var dragRef = new _IG_Drag();
  dragRef.addSource('overlayDrag', _gel('mask'),
      '<img class="drag" src="http://www.google.com/mapfiles/marker.png"/>');
  for (var i = duration; i >= 0; i--) {
    var targetId = i ? 'trip-items-container-' + i : 'unscheduled-items-box';
    dragRef.addTarget(targetId);
  }
  dragRef.onDragStart = markerDragStart;
  dragRef.onDragTargetHit = commonDragTargetHit;
  dragRef.onDragTargetLost = commonDragTargetLost;
  dragRef.onDragEnd = markerDragEnd;
}

/**
 * Function to initialize drag targets between unscheduled and scheduled items.
 * This method is called for already added items.
 */
function initInterDrag() {
  var itemDragRef = new _IG_Drag();
  var trip = getTripById(gCurrentTripsData.currentTripId);
  var tripItemKey;
  var duration = getTripById(gCurrentTripsData.currentTripId).duration;
  for (var i = 0, count = gTripItemDB.length; i < count; i++) {
    tripItemKey = gTripItemDB[i].id;
    var sourceElement = _gel('drag-' + tripItemKey);
    var handleContent = _gel('drag-handle-' + tripItemKey).innerHTML;
    itemDragRef.addSource(tripItemKey, sourceElement, handleContent);
  }
  for (var i = duration; i >= 0; i--) {
    var targetId = i ? 'trip-items-container-' + i : 'unscheduled-items-box';
    itemDragRef.addTarget(targetId);
  }
  itemDragRef.onDragTargetHit = commonDragTargetHit;
  itemDragRef.onDragTargetLost = commonDragTargetLost;
  itemDragRef.onDragEnd = itemDragEnd;
}

/**
 * This method does data fetching for lonely planet search results
 * when marker is dragged.
 * @param {_IG_Drag} newSource The draggable marker.
 */
function markerDragStart(newSource) {
  var dragStart = gCurrentTripsData.arrSearchResults[currentMarker.index];
  if (dragStart.err) {
    doPoiByIdLookup(dragStart.id);
  }
}

/**
 * This method assign class name to appropriate targets.
 * @param {_IG_Drag} lastTarget The draggable marker.
 */
function commonDragTargetLost(lastTarget) {
  if (!lastTarget) {
    return;
  }
  lastTarget.className = 'trip-items-container';
}

/**
 * This method assign class name to appropriate targets.
 * @param {_IG_Drag} newTarget The draggable marker.
 * @param {_IG_Drag} lastTarget The draggable marker.
 */
function commonDragTargetHit(newTarget, lastTarget) {
  if (newTarget) {
    newTarget.className = 'trip-items-container drop-highlight';
  }
  if (lastTarget) {
    lastTarget.className = 'trip-items-container';
  }
}

/**
 * This method is called when an item is dragged from search results to
 * itinerary area.
 * @param {_IG_Drag} source Marker to be dragged.
 * @param {_IG_Drag} target Target where its dropped.
 */
function markerDragEnd(source, target) {
  if (!target) {
    return;
  }
  target.className = 'trip-items-container';
  var targetId = target.id;
  // Position at which day for an item is extracted.
  var day = parseInt(targetId.split('-')[3] || '0');
  addToItinerary(currentMarker.index, day);
}

/**
 * This method is called when an item added in itinerary is dragged from
 * one day to another.
 * @param {HTMLElement} source The source DOM element from where it is dragged.
 * @param {HTMLElement} target The target DOM element from where it is dropped.
 */
function itemDragEnd(source, target) {
  if (!target) {
    return;
  }
  target.className = 'trip-items-container';
  var targetId = target.id;
  // Position at which day for an item is extracted.
  var day = parseInt(targetId.split('-')[3] || '0');
  // Position at which id for an item starts in string.
  var rescheduledItemId = source.id.substring(5);
  // Sending the item to modify id and day.
  saveRescheduledItem(rescheduledItemId, day);
}
