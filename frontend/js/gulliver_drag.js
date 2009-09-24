/**
 * @fileoverview Code to handle drag functionality.
 * @author gadgetfactory@google.com (gadgetfactory)
 */

var iggd_a = false, iggd_ = null;
function _IG_DragPosition_left(a){
    var b = 0;
    if (a.offsetParent)
        for (; a.offsetParent;) {
            b += a.offsetLeft;
            a = a.offsetParent
        }
    else
        if (a.x)
            b = a.x;
    return b
}

function _IG_DragPosition_right(a){
    return _IG_DragPosition_left(a) + a.offsetWidth
}

function _IG_DragPosition_top(a){
    var b = 0;
    if (a.offsetParent)
        for (; a.offsetParent;) {
            b += a.offsetTop;
            a = a.offsetParent
        }
    else
        if (a.y)
            b = a.y;
    return b
}

function _IG_DragPosition_bottom(a){
    return _IG_DragPosition_top(a) + a.offsetHeight
}

function _IG_Drag(){
    this.surrogateOffsetY = this.surrogateOffsetX = 1;
    this.bottomMargin = this.topMargin = this.rightMargin = this.leftMargin = 2;
    this.yMapper = this.xMapper = iggd_;
    this.surrogateInitialY = this.surrogateInitialX = 0;
    this.curTargetId = this.curSource = this.surrogate = iggd_;
    this.isRightButton = this.hasDragged = this.isDragging = iggd_a;
    this.b = {};
    this.f = {};
    this.g = {};
    this.a = {};
    this.h = {};
    this.d = this.c = iggd_;
    this.j()
}

_IG_Drag.prototype.noMoveMapper = function(a, b){
    return a - b
};
_IG_Drag.prototype.onDragStart = function(){
};
_IG_Drag.prototype.onDragTargetHit = function(){
};
_IG_Drag.prototype.onDragTargetLost = function(){
};
_IG_Drag.prototype.onDragEnd = function(){
};
_IG_Drag.prototype.onDragClick = function(){
};
_IG_Drag.prototype.i = function(){
    if (!this.surrogate) {
        this.surrogate = document.createElement("SPAN");
        this.surrogate.innerHTML = "";
        this.surrogate.style.position = "absolute";
        this.surrogate.style.cursor = "move";
        this.surrogate.style.left = this.surrogateInitialX + "px";
        this.surrogate.style.top = this.surrogateInitialY + "px";
        document.body.appendChild(this.surrogate)
    }
};
_IG_Drag.prototype.addSource = function(a, b, c){
    if (typeof b == "undefined")
        b = _gel(a);
    if (typeof this.b[a] != "undefined")
        return iggd_a;
    this.b[a] = b;
    if (typeof c != "undefined")
        b._IG_DragSurrogateView = c;
    this.f[a] = b.onmousedown;
    b.onmousedown = this.dragStart;
    this.g[a] = b.style.cursor;
    b.style.cursor = navigator.appName == "Microsoft Internet Explorer" ? "hand" : "pointer";
    return true
};
_IG_Drag.prototype.removeSource = function(a){
    if (typeof this.b[a] != "undefined") {
        this.b[a].onmousedown = this.f[a];
        if (typeof this.b[a].style != "undefined")
            this.b[a].style.cursor = this.g[a]
    }
    delete this.b[a]
};
_IG_Drag.prototype.removeAllSources = function(){
    for (var a in this.b) {
        this.b[a].onmousedown = this.f[a];
        if (typeof this.b[a].style != "undefined")
            this.b[a].style.cursor = this.g[a]
    }
    this.b = {}
};
_IG_Drag.prototype.addTarget = function(a, b, c){
    if (typeof b == "undefined")
        b = _gel(a);
    if (typeof c == "undefined")
        c = 0;
    this.a[a] = b;
    this.h[a] = c
};
_IG_Drag.prototype.removeTarget = function(a){
    delete this.a[a]
};
_IG_Drag.prototype.removeAllTargets = function(){
    this.a = {}
};
_IG_Drag.prototype.cacheTargets = function(){
    this.l = {};
    this.m = {};
    this.n = {};
    this.k = {};
    for (var a in this.a) {
        this.l[a] = _IG_DragPosition_left(this.a[a]) - this.leftMargin;
        this.m[a] = _IG_DragPosition_right(this.a[a]) + this.rightMargin;
        this.n[a] = _IG_DragPosition_top(this.a[a]) - this.topMargin;
        this.k[a] = _IG_DragPosition_bottom(this.a[a]) + this.bottomMargin
    }
};
_IG_Drag.prototype.e = function(a){
    if (typeof a == "undefined")
        a = window.event;
    if (typeof a.layerX == "undefined")
        a.layerX = a.offsetX;
    if (typeof a.layerY == "undefined")
        a.layerY = a.offsetY;
    return a
};
_IG_Drag.prototype.j = function(){
    var a = this;
    this.dragStart = function(b){
        a.isDragging && a.dragEnd();
        a.curSource = this;
        b = a.e(b);
        a.isDragging = true;
        a.i();
        var c = typeof a.curSource._IG_DragSurrogateView;
        if (c == "undefined")
            a.surrogate.innerHTML = a.curSource.innerHTML;
        else
            if (c == "boolean" || c == "number" || c == "string")
                a.surrogate.innerHTML = a.curSource._IG_DragSurrogateView;
            else {
                for (; a.surrogate.firstChild;)
                    a.surrogate.removeChild(a.surrogate.firstChild);
                a.surrogate.appendChild(a.curSource._IG_DragSurrogateView)
            }
        a.surrogateInitialX = _IG_DragPosition_left(a.curSource) + a.surrogateOffsetX;
        if (typeof a.curSource._IG_Drag_surrogateOffsetX != "undefined")
            a.surrogateInitialX += a.curSource._IG_Drag_surrogateOffsetX;
        a.surrogateInitialY = _IG_DragPosition_top(a.curSource) + a.surrogateOffsetY;
        if (typeof a.curSource._IG_Drag_surrogateOffsetY != "undefined")
            a.surrogateInitialY += a.curSource._IG_Drag_surrogateOffsetY;
        a.surrogate.style.left = a.surrogateInitialX + "px";
        a.surrogate.style.top = a.surrogateInitialY + "px";
        a.surrogate.lastMouseX = b.clientX;
        a.surrogate.lastMouseY = b.clientY;
        a.c = document.onmousemove;
        a.d = document.onmouseup;
        document.onmousemove = a.dragMove;
        document.onmouseup = a.dragEnd;
        a.isRightButton = iggd_a;
        if (b.which && b.which == 3)
            a.isRightButton = true;
        if (typeof b.button != "undefined" && b.button == 2)
            a.isRightButton = true;
        a.onDragStart(a.curSource);
        a.curTargetId = iggd_;
        a.onDragTargetLost(iggd_);
        a.hasDragged = iggd_a;
        a.cacheTargets();
        return iggd_a
    };
    this.dragMove = function(b){
        a.hasDragged = true;
        b = a.e(b);
        if (b.which == 0)
            return a.dragEnd();
        var c = b.clientY;
        var i = b.clientX;
        var f = parseInt(a.surrogate.style.top, 10);
        var g = parseInt(a.surrogate.style.left, 10);
        var j = a.xMapper ? a.xMapper(g, g - a.surrogateInitialX) : g + (i - a.surrogate.lastMouseX);
        var k = a.yMapper ? a.yMapper(f, f - a.surrogateInitialY) : f + (c - a.surrogate.lastMouseY);
        var l = j + a.surrogate.offsetWidth / 2, m = k + a.surrogate.offsetHeight / 2;
        a.surrogate.style.left = j + "px";
        a.surrogate.style.top = k + "px";
        a.surrogate.lastMouseX = i;
        a.surrogate.lastMouseY = c;
        var d = iggd_;
        for (var e in a.a)
            if (!(d != iggd_ && a.h[e] < a.h[d]))
                if (l >= a.l[e] && l <= a.m[e] && m >= a.n[e] && m <= a.k[e])
                    d = e;
        if (a.curTargetId != d)
            if (d == iggd_) {
                var h = a.a[a.curTargetId];
                a.curTargetId = iggd_;
                a.onDragTargetLost(h)
            }
            else
                if (a.curTargetId == iggd_) {
                    a.curTargetId = d;
                    a.onDragTargetHit(a.a[d], iggd_)
                }
                else {
                    h = a.a[a.curTargetId];
                    a.curTargetId = d;
                    a.onDragTargetHit(a.a[d], h)
                }
        a.c != iggd_ && a.c();
        return iggd_a
    };
    this.dragEnd = function(){
        document.onmousemove = a.c;
        document.onmouseup = a.d;
        a.surrogate.innerHTML = "";
        a.isDragging = iggd_a;
        var b = a.hasDragged;
        a.hasDragged = iggd_a;
        if (a.curSource != iggd_) {
            a.onDragEnd(a.curSource, a.a[a.curTargetId]);
            b || a.onDragClick(a.curSource)
        }
        a.curSource = iggd_;
        a.d != iggd_ && a.d();
        return iggd_a
    }
};

/**
 * Add targets for marker being shown on map.
 * @param {number} nDays duration of trip.
 */
function addMarkersTarget(nDays) {
  var dragXWindow = new _IG_Drag();
  dragXWindow.addSource("overlayDrag",
      _gel('mask'),
      '<img style="border:0 none;margin:0;padding:0;width:20px;height:34px;' +
          'z-index:10000;cursor:move;" ' +
          'src="http://www.google.com/mapfiles/marker.png"/>');
  for (var i = 0; i < nDays; i++) {
    dragXWindow.addTarget('scheduledInfoBox' + i);
  }
  dragXWindow.addTarget('unscheduleItemBox');
  dragXWindow.onDragTargetHit = function(newTarget, lastTarget) {
    if (newTarget) {
      newTarget.className = 'drop-highlight';
    }
    if (lastTarget) {
      lastTarget.className = '';
    }
  };
  dragXWindow.onDragTargetLost = function(lastTarget) {
    if (!lastTarget) return;
    lastTarget.className = '';
  };
  dragXWindow.onDragEnd = function(source, target) {
    if (!target) {
      return;
    }
    target.className = '';
    var targetId = target.id;
    var schedule = (targetId != 'unscheduleItemBox');
    var daySelected = schedule ? parseInt(targetId.substring(16), 10) + 1 : 0;
    addNewItem(markerIndex, schedule, daySelected);
  };
  dragXWindow.onDragStart = function(source, target) {
    if(arrSearchResults[markerIndex].err) {
      var id = arrSearchResults[markerIndex].id;
      doPoiByIdLookup(id);
    }

  };
}

/**
 * This function overrides the function inside 'drag' library, to fix
 * the problem when the drag target element is inside a 'scrollable' container.
 * @param {HTMLElement} element The HTML element.
 * @return {number} Returns the top position of the passed element in pixels.
 */
function _IG_DragPosition_top(element) {
  var top = 0;
  if (element.offsetParent) {
    var ele = element;
    for (; element.offsetParent; element = element.offsetParent) {
      top += element.offsetTop;
    }
    for(; ele.parentNode; ele = ele.parentNode) {
      if (ele.parentNode.scrollTop) {
        top -= ele.parentNode.scrollTop;
      }
    }
  }
  else {
    if (element.y) {
      top = element.y;
    }
  }
  return top;
}

/**
 * Gives the offset.
 * @param {object} target The element.
 * @param {object} event The event.
 * @return {object} The offset.
 */
function getMouseOffset(target, event) {
  event = event || window.event;
  var docPos = getPosition(target);
  var mousePos = mouseCoords(event);
  return {x:mousePos.x - docPos.x, y:mousePos.y - docPos.y};
}

/**
 * Gives the x and y co-ordinates of the element.
 * @param {object} element The element.
 * @return {object} The position of element.
 */
function getPosition(element) {
  var left = 0;
  var top = 0;
  while (element.offsetParent) {
    left += element.offsetLeft;
    top += element.offsetTop;
    element = element.offsetParent;
  }
  left += element.offsetLeft;
  top += element.offsetTop;
  return {x:left, y:top};
}

/**
 * Executes when mouse move happens.
 * @param {object} event The mouse move event.
 * @return {boolean} false if drag object is found else true.
 */
function mouseMove(event) {
  event = event || window.event;
  var mousePos = mouseCoords(event);
  if (dragObject) {
    dragObject.style.position = 'absolute';
    dragObject.style.display = '';
    dragObject.style.top = mousePos.y - mouseOffset.y;
    dragObject.style.left = mousePos.x - mouseOffset.x;
    for (var i = 0; i < dropTargets.length; i++) {
      var curTarget = dropTargets[i];
      var targPos = getPosition(curTarget);
      if (curTarget.id.indexOf('unscheduleItemBox') == -1) {
        targPos.y -= _gel('scheduleItemBox').scrollTop;
      }
      var targWidth = parseInt(curTarget.offsetWidth, 10);
      var targHeight = parseInt(curTarget.offsetHeight, 10);
      if ((mousePos.x > targPos.x) &&
         (mousePos.x < (targPos.x + targWidth)) &&
         (mousePos.y > targPos.y) &&
         (mousePos.y < (targPos.y + targHeight))) {
        if (highlightedItem)
          highlightedItem.className = '';
        highlightedItem = curTarget;
        highlightedItem.className = 'drop-highlight';
        break;
      } else {
        if (highlightedItem)
          highlightedItem.className = '';
      }
    }
    return false;
  }
  return true;
}

/**
 * Gives the x and y co-ordinates of mouse on the page.
 * @param {object} event The mouse event.
 * @return {object} The mouse co-ordinates.
 */
function mouseCoords(event) {
  if (!event) {
    return;
  }
  if (event.pageX || event.pageY) {
    return {x:event.pageX, y:event.pageY};
  }
  return {
    x:event.clientX + document.body.scrollLeft - document.body.clientLeft,
    y:event.clientY + document.body.scrollTop - document.body.clientTop
  };
}

/**
 * Make an element draggable.
 * @param {object} item The item which is to be dragged.
 */
function makeDraggable(item) {
  if (!item) return;
  item.onmousedown = function(event) {
    closeInfoWindow();
    strSelectedItemId = this.id;
    this.className = 'dragobj';
    dragObject = _gel('dragHelper');
    dragObject.style.position = 'absolute';
    dragObject.style.top = this.style.top;
    dragObject.style.left = this.style.left;
    var html = this.innerHTML;
    dragObject.innerHTML = html.replace(/\sonmouseover=".*?"/i, '')
                               .replace(/\sonmouseout=".*?"/i, '');
    mouseOffset = getMouseOffset(this, event);
    return false;
  }
}

/**
 * Add an element on which drap event is to be occurred.
 * @param {object} dropTarget The element.
 */
function addDropTarget(dropTarget) {
  dropTargets.push(dropTarget);
}

/**
 * Executes when mouse up happens.
 * @param {object} event The mouse up event.
 */
function mouseUp(event) {
  event = event || window.event;
  try {
    if (dragObject !=  null) {
      var mousePos = mouseCoords(event);
      var bSchedule = false;
      var nDay = 0, index = -1;
      for (var i = 0; i < dropTargets.length; i++) {
        var curTarget = dropTargets[i];
        var targPos = getPosition(curTarget);
        if (curTarget.id.indexOf('unscheduleItemBox') == -1) {
          targPos.y -= _gel('scheduleItemBox').scrollTop;
        }
        var targWidth = parseInt(curTarget.offsetWidth, 10);
        var targHeight = parseInt(curTarget.offsetHeight, 10);
        if ((mousePos.x > targPos.x) &&
           (mousePos.x < (targPos.x + targWidth)) &&
           (mousePos.y > targPos.y) &&
           (mousePos.y < (targPos.y + targHeight))) {
          var objTrip = JGulliverData.getCurrentTrip();
          if (curTarget.id.indexOf('scheduledInfoBox') != -1) {
            var index = parseInt(curTarget.id.substr(16), 10);
            bSchedule = true;
            nDay = index + 1;
            if (!isEmpty(objTrip.sdate)) {
              currentdate = addDaysToDate(objTrip.sdate, index, '%m/%d/%Y');
            }
          }
          addNewItem(strSelectedItemId.substr(10), bSchedule, nDay);
          highlightedItem.className = '';
          break;
        }
      }
      var element = _gel(strSelectedItemId);
      if (element != null && element != undefined)
        element.className = '';
      dragObject.innerHTML = '';
      dragObject.style.display = 'none';
      dragObject = null;
    }
  } catch (err) {}
}

function moveMask(point) {
  var divPoint = gMap.fromLatLngToContainerPixel(point);
  var mapElementPosition = getPosition(_gel('map-container'));
  var ele = _gel('mask');
  var eleStyle = ele.style;
  eleStyle.display = 'block';
  eleStyle.zIndex = 10000;
  eleStyle.left = divPoint.x + mapElementPosition.x - 10 + 'px';
  eleStyle.top = divPoint.y + mapElementPosition.y - 34 + 'px';
}

function makeItemsDraggable() {
  var objTrip = JGulliverData.getCurrentTrip();
  var nDays = objTrip.duration;
  var dragTargets = ['unscheduleItemBox'];
  var dragSources = [];
  for (var i = 0; i < nDays; i++) {
    dragTargets.push('scheduledInfoBox' + i);
  }
  for (var i = 0; i < objTrip.arrItem.length; i++) {
    dragSources[i] = 'drag-' + i;
    var srcElement = _gel(dragSources[i]);
    var dragItem = new _IG_Drag();
    dragItem.addSource('itemDrag' + i, srcElement, srcElement.innerHTML);

    for (var j = 0; j < dragTargets.length; j++) {
      if (srcElement.parentNode.id != dragTargets[j]) {
        dragItem.addTarget(dragTargets[j]);
      }
    }
    dragItem.onDragTargetHit = function(newTarget, lastTarget) {
      if (newTarget) {
        newTarget.className = 'drop-highlight';
      }
      if (lastTarget) {
        lastTarget.className = '';
      }
    };
    dragItem.onDragTargetLost = function(lastTarget) {
      if (!lastTarget) return;
      lastTarget.className = '';
    };
    dragItem.onDragEnd = function(source, target) {
      if (!target) {
        return;
      }
      target.className = '';
      var targetId = target.id;
      var schedule = (targetId != 'unscheduleItemBox');
      var daySelected = schedule ? parseInt(targetId.substring(16), 10) + 1 : 0;
      var index = parseInt(source.id.substring(5),10);
      var objItem = objTrip.arrItem[index];
      var itemName = objItem.name;
      var itemDesp = objItem.address;
      var itemSDate = '';
      if (daySelected && !isEmpty(objTrip.sdate)) {
        itemSDate = addDaysToDate(objTrip.sdate, daySelected - 1, '%m/%d/%Y');
      }
      var itemFDate = itemSDate;
      objTrip.updateItemDetails(index, itemName, itemDesp, daySelected, itemSDate,itemFDate);
    };
  }
}
