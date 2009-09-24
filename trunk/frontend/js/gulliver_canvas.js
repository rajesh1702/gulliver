/**
 * @fileoverview File contains canvas view related code.
 * @author gadgetfactory@google.com (gadgetfactory)
 */

/**
 * Executes whenever user moves mouse.
 */
document.onmousemove = mouseMove;

/**
 * Executes whenever mouseup event occurs.
 */
document.onmouseup = mouseUp;

/**
 * Executes whenever onclick event occurs .
 */
document.onclick = closeActionMenu;

/**
 * Get html string for a trip item.
 * @param {number} index index of item.
 * @param {string} title name of item.
 * @param {string} location Location of item.
 * @param {boolean} bScheduled flag to check whether item is scheduled.
 * @return {string} html string for an item.
 */
function getItemHtml(index, title, location, bScheduled) {
  var objTrip = JGulliverData.getCurrentTrip();
  var isWavelet = wave && wave.isInWaveContainer();
  var description;
  if (objTrip.arrItem[index].bCustom) {
    description = objTrip.arrItem[index].review;
  } else {
    description = objTrip.arrItem[index].review;
    description = description.replace(/<br\/>/g, ' ').replace(/<br>/g, ' ');
  }
  if (isEmpty(description)) {
    description = '';
  }

  var itemHtml;
  if (bScheduled) {
    itemHtml = isWavelet ? SCHEDULED_ITEM_TPL_NPL : SCHEDULED_ITEM_TPL;
  } else {
    itemHtml = isWavelet ? UNSCHEDULED_ITEM_TPL_NPL : UNSCHEDULED_ITEM_TPL;
  }
  itemHtml = itemHtml.replace(/%DRAG_ITEM_COUNTER%/g, dragItemCounter)
                     .replace(/%INDEX%/g, index);
  var wrappedTitle;
  if (title.length > MAX_DESC_LENGTH) {
    wrappedTitle = title.substr(0, MAX_DESC_LENGTH) + '...';
    itemHtml = itemHtml.replace(/%TITLE%/, _unesc(title));
  } else {
    wrappedTitle = title;
    itemHtml = itemHtml.replace(/%TITLE%/, '');
  }
  itemHtml = itemHtml.replace(/%NAME%/g, wrappedTitle);
  if (isEmpty(location))
    itemHtml = itemHtml.replace(/%CONTACT_INFO%/, '');
  else
    itemHtml = itemHtml.replace(/%CONTACT_INFO%/, location);
  dragItemCounter++;
  return itemHtml;
}

/**
 * Prepares html code of table on dropdown image click.
 */
function dropDown() {
  var tableBox = _gel('table-box');
  if (tableBox) {
    if (tableBox.style.display == 'block') {
      tableBox.style.display = 'none';
      return;
    }
  }
  var html = ['<div id="table-box" style="display:block;">',
              '<table class="select-option" cellspacing="3" cellpadding="0" ',
              'style="position:absolute;left:-19px;top:29px;',
              'border:1px solid #999;">'];
  var list = [];
  list[0] = prefs.getMsg('change_trip_loc');
  list[1] = prefs.getMsg('delete_trip');
  for (var i = 0; i <= 1; i++) {
    html.push('<tr><td id="option' + (i + 1) + '" align="left"',
        'style="vertical-align:top;" onclick="onTripAction(this.id);"' ,
        'onmouseover="this.style.backgroundColor=\'#D4E6FC\';"',
        'onmouseout="this.style.backgroundColor=\'#FFFFFF\';">',
        '<div style="width: 100%;"><nobr>' + list[i] + '</nobr></div></td>',
        '</tr>');
  }
  html.push('</tbody></table></div>');
  var element = _gel('table-data');
  element.style.display = '';
  element.innerHTML = html.join('');
}

/**
 * Function to print items.
 */
function printTripItems() {
  _gel('print_content').innerHTML = _gel('print_Dialog').innerHTML;
  hideDialogView();
  hideDialog();
  window.print();
}

/**
 * Displays the records in the Trip tab.
 * @param {number} pageNumber Page Number of trip.
 * @param {boolean} bShowAll flag to check if search is based on crieria or not.
 */
function displayTripRecords(pageNumber, bShowAll) {
  nCurrentPageNum = pageNumber;
  var html = [];
  var imgPath = 'http://maps.google.com//intl/en_us/mapfiles/transparent.png';

  var strStartTrip = ((nCurrentPageNum - 1) * MAX_SHOWN_TRIPS);
  var counter = 0;
  var i, j;
  for (i = strStartTrip; i < arrSortedTrips.length; i++) {
    var displayDt = arrSortedTrips[i].sdate;
    var finaldate = arrSortedTrips[i].edate;
    if (isEmpty(displayDt))
      displayDt = ''
    else
      displayDt = formatDate(displayDt, '%M%d%Y');
    if (isEmpty(finaldate))
      finaldate = '';
    else
      finaldate = formatDate(finaldate, '%M%d%Y');

    html.push('<div id="row', i, '" style="width:100%;"',
        '<table width="100%"><tr><td width="27%" onclick="exploreTrip(\'',
        arrSortedTrips[i].id, '\');" style="padding-left:4px;cursor:pointer" >',
        '<span class="subhead" >');
    var tripName = arrSortedTrips[i].name;
    var MAX_NAME_LENGTH = 25;
    if (tripName.length > MAX_NAME_LENGTH) {
      tripName = tripName.substr(0, MAX_NAME_LENGTH) + '...';
    }
    html.push(tripName,
        '</span></a></td><td width="24%" style="padding-left:6px;">',
        '<span class="subhead2">', displayDt, ' - ', finaldate, '</span></td>',
        '<td width="24%" style="padding-left:6px;"><span class="subhead2">',
        arrSortedTrips[i].loc, '</span></td>',
        '<td width="24%" style="padding-left:6px;"><span class="subhead2">',
        arrSortedTrips[i].ownerName, '</span></td>');
    html.push('</tr></table></div>');

    if (++counter == MAX_SHOWN_TRIPS)
      break;
  }

  _gel('displayRec').innerHTML = html.join('');

  if (counter > 0) {
    if (!bShowAll) {
      _gel('showAllTrips').style.display = '';
      _gel('showAllTrips').innerHTML = prefs.getMsg('show_all_trips');
    } else {
      _gel('showAllTrips').style.display = 'none';
      _gel('searchTripText').value = '';
    }
    var lastTrip = strStartTrip + counter;
    var strNumText = (strStartTrip + 1) + ' - ' + lastTrip + ' of ' +
                     arrSortedTrips.length + ' Trips';
    _gel('showingTripText').innerHTML = strNumText;

    var strPageText = ['<table width=100%><tr>'];
    if (nCurrentPageNum > 1) {
      strPageText.push('<td align="right" width="99%">',
                      '<a href="javascript:displayTripRecords(',
                      (nCurrentPageNum - 1), ',', bShowAll, ')">',
                      '<font size=2px;>' + prefs.getMsg('previous') +
                      '</font></a>&nbsp;</td>');
    }
    var lastRecord =
        ((nCurrentPageNum - 1) * MAX_SHOWN_TRIPS) + MAX_SHOWN_TRIPS;
    if (lastRecord < arrSortedTrips.length) {
      strPageText.push('<td align="right"><a href="javascript:',
          'displayTripRecords(', (nCurrentPageNum + 1), ',',
          bShowAll, ')"><font size=2px;>' + prefs.getMsg('next') +
          '</font></a></td>');
    }
    strPageText.push('</tr></table>');
    _gel('showPagination').innerHTML = strPageText.join('');

  } else {
    _gel('showAllTrips').style.display = '';
    _gel('showAllTrips').innerHTML = prefs.getMsg('show_all_trips');
  }

  _IG_AdjustIFrameHeight();
}


/**
 * Fills array of required length with sorted records.
 * @param {boolean} bVal Flag to check if search is based on crieria or not.
 */
function fillForSortedRecords(bVal) {
  arrSortedTrips = [];
  if (ownerTrips.length == 0) {
    _gel('displayRec').innerHTML = '<span style="font-size:13px;' +
        'padding-left:5px;">' + prefs.getMsg('no_trips') + '</span>';
    return;
  }

  var searchTxt = '';
  if (bVal) {
    searchTxt = _gel('searchTripText').value.strip().toLowerCase();
    if (searchTxt.strip() == '')
      return;
  }
  var j = 0;
  for (var i = 0; i < ownerTrips.length; i++) {
    if (bVal) {
      if (ownerTrips[i].name.toLowerCase().indexOf(searchTxt) == -1)
        continue;
    }
    arrSortedTrips[j++] = ownerTrips[i];
  }
  arrSortedTrips = arrSortedTrips.sort(sortHandler);
  displayTripRecords(1, !bVal);
}

/**
 * To display the required icon while sorting.
 * @param {string} element element id sent.
 * @param {number} val column number.
 */
function changeSign(element, val) {
  var imgdivsrc = _gel('img_' + val).innerHTML;
  if (val == 1) sortCol = enTripCol.NAME;
  if (val == 2) sortCol = enTripCol.DATE;
  if (val == 3) sortCol = enTripCol.LOCATION;
  if (val == 4) sortCol = enTripCol.OWNER;
  if (imgdivsrc.match('down') == null) {
    _gel('img_' + val).innerHTML = DOWN_ARROW_IMG;
    bAscending = false;
  } else if (imgdivsrc.match('up') == null) {
    _gel('img_' + val).innerHTML = UP_ARROW_IMG;
    bAscending = true;
  }

  for (var i = 1; i <= 4; i++) {
    if (i != val)
      _gel('img_' + i).innerHTML = '';
  }
  arrSortedTrips = arrSortedTrips.sort(sortHandler);
  displayTripRecords(nCurrentPageNum, true);
}

/**
 * Show drag tip on create a new trip.
 */
function showDragTip() {
  _gel('msgContainer').style.display = 'block';
  var html = ACTION_MSG_TPL;
  html = html.replace(/%MESSAGE%/, prefs.getMsg('drag_tip1'));
  timerMsg.createTimerMessage(html, 5);
}

/**
 * Callback function for create Trip tab.
 */
function callbackCreateTrip() {
  var objTrip = JGulliverData.getCurrentTrip();
  _gel('canvasContent').style.height = '';
  var replaceText = _gel('messageContainer').innerHTML;
  var queryURL = EXPORT_SERVER + '?trip_id=' + objTrip.id;
  var encodedURL = 'http://maps.google.com?q=' + encodeURIComponent(queryURL);
  _gel('exportLink').href = encodedURL;
  _gel('messageContainer').style.display = '';
  objTrip.arrItem = [];
  if (!objTrip) {
    var html = ['<table width="100%"><tr><td colspan="2">',
        '<font class="heading">' + prefs.getMsg('where_visit') +
        '</font></td></tr><tr><td width="70%">' +
        '<input type="text" id="location" class="txtBox">',
        '<font class="hint" style="color:#666;">' + prefs.getMsg('egLocation') +
        '</font></td><td width="30%" valign="top"><input type="button" ',
        'value="' + prefs.getMsg('create') +
        '" onClick=""/></td></tr></table>'];
    _gel('canvasContent').innerHTML = html.join('');
  } else {

    var tripNameTitle = objTrip.name;
    var i = 0;
    var html = ['<table cellpadding="0" cellspacing="0" border="0" ',
        'width="100%" style="background:#058a9d;padding-bottom:2px;">',
        '<tr><td valign="top" ',
        'style="padding:3px 7px 5px 0;" width="61%"><div style="',
        'padding-left:5px;color:#fff;" width="100%"><span id="tripName" '];
    if (tripNameTitle.length > 25) {
      html.push('title="', objTrip.name, '"');
      tripNameTitle = tripNameTitle.substr(0, 25) + '...';
    }
    html.push('>', tripNameTitle, '</span > - <span class="link" ',
        'id="editTrip" onclick="showEditDialog();">',
        '<nobr>' + prefs.getMsg('edit_trip') + '</nobr></span>',
        '</span></div>',
        '<div id="editdialog" style="padding-left:120px;"></div>',
        '</td><td align="right" style="font-size:12px;',
        'padding: 3px 7px 0 0;" valign="top"',
        ' rowspan="2" ><table border="0" cellpadding="0" ',
        'cellspacing="0" width="100%"><tr><td colspan="2">',
        '&nbsp;<span class="link" onclick=',
        '"javascript:callbackTrips();" style="padding-top:4px;">',
        '<nobr>' + prefs.getMsg('view_other') + '</nobr></span>&nbsp;&nbsp;',
        '&nbsp;</td><td><span class="link">',
        '<img src="' + FILE_SERVER + '/images/print_icon.gif" ',
        'style="vertical-align:bottom;cursor:pointer;" ',
        'onclick="showPrintDialog();"/>&nbsp;<a href="',
        'javascript:showPrintDialog();" style="color:#fff;">' +
        prefs.getMsg('print_item') + '</a></span>&nbsp;&nbsp;</td><td>',
        '<span class="link"><img src="',
        'http://www.google.com/mapfiles/email_icon.gif" style="',
        'vertical-align:bottom;cursor:pointer;" onclick="',
        'javascript:openMailDialog();"/>&nbsp;<a href="',
        'javascript:openMailDialog();" style="color:#fff;">' +
        prefs.getMsg('send') +
        '</a></span>&nbsp;&nbsp;</td><td style="padding-top:2px;">',
        '<span id="menuAction" class="trip-action" ',
        'onclick="javascript:dropDown();" style="padding-left:5px"><nobr>' +
        prefs.getMsg('trip_actions') +
        '&nbsp;&nbsp;&nbsp;<img id="menuImg" style="cursor:pointer;"' ,
        'src="http://www.google.com/base/s2/images/down_arrow.gif"></nobr>',
        '&nbsp;</span><div id="table-data" class="drop-down-table" ',
        'style="display:none;"></div></td></tr></table></td><tr>',
        '<td colspan="3" valign="center">',
        '<span style="font-size:12px;color:#fff;padding-left:5px;">',
        '<nobr>Trip owner: </nobr><span style="padding-left:4px;',
        ' font-weight:bold;">', objTrip.ownerName, '&nbsp;&nbsp;</span></span>',
        '<span id="thumb" style="position:relative;top:0">',
        '<span style="padding-left:5px;font-size:12px;color:#fff;">',
        '<nobr><b><span id="thum_up">', 'Votes: ', objTrip.thumb_up,
        '</span></b> thumbs up, ',
        '<b><span id="thum_down">', objTrip.thumb_down, '</span></b>',
        ' thumbs down</nobr></span></span><span id ="banInWave" ',
        'style="padding-left:8px;position:relative;top:0" ',
        'class="link" onclick="addYourVote()">',
        '<nobr>Vote Now</nobr></span></td></tr></tr></table>',
        '<table cellpadding="0" cellspacing="0" border="0" width="100%">',
        '<tr><td width="55%" style="height:100%;padding-left:5px;',
        'border:1px solid #058a9d;border-top:0; border-right:0;',
        'background:#f5eee6;" valign="top">',
        '<table width="100%" border="0" cellspacing="0" ',
        'cellpadding="0"><tr>',
        '<td valign="top" rowspan="2" id="map-container" ',
        'style="padding:5px 5px 0 0;"><div id="mapBox"></div>',
        '</td><td style="height:22px;background:#eef9f9;',
        'vertical-align:top;border:1px solid #058a9d;border-bottom:0;',
        'display:none;" id="result-section"><span id="resultText"></span>',
        '<br><div style="font-size:12px;font-size:',
        '12px;padding:0 0 4px 6px; display:none;" id="tip-container">',
        '<span style="color:#f00;">' + prefs.getMsg('tip') +
        '</span>&nbsp;' + prefs.getMsg('drag_tip') + '</div></td></tr><tr>',
        '<td id="result-container" valign="top" ',
        'style="background:#eef9f9;border:1px solid #058a9d;border-top:0;',
        'display:none;padding-bottom:3px;">',
        '<div id="resultBox">',
        '</div><div id="navBox" ',
        'style="display:none;padding:4px 0;"></div></td></tr>',
        '<tr><td height="5px;"></td></tr></table></td><td width="30%"',
        ' valign="top" style="background:#f5eee6;',
        'border:1px solid #058a9d;border-top:0;border-left:0">',
        '<div id="durationSection" style="background:#f5eee6;">',
        '<table border="0" width="100%"><tr>',
        '<td width="80%" style="padding-top:6px;">',
        '<div id="duration" style="float:left;font-size:13px;"></div>&nbsp;',
        '<img src="' + FILE_SERVER + '/images/calender.png" ',
        'onclick="javascript:showDateDialog();" style="cursor:pointer;"/>',
        '&nbsp;<span id="editDays" class="link" ',
        'style="vertical-align:top;color:#00c;" ',
        'onclick="javascript:showDateDialog();">' + prefs.getMsg('edit') +
        '</span></td></tr></table></div><span ',
        'style="font-size:13px;font-weight:bold;',
        'background:#f5eee6;float:left;padding:10px 0 0 5px;">' +
        prefs.getMsg('unscheduled_items') +
        '</span><span style="float:right;padding:5px;">',
        '<input type="button" value="' + prefs.getMsg('createitem') +
        '" style="font-size:11px;" onclick="',
        'javascript:createCustomItemDialog();"></span><br style="clear:both"/>',
        '<div style="padding:5px;background:#f5eee6">',
        '<div style="background:#fff;"><div id="unscheduleItemBox" ',
        'style="text-align:left;"></div></div></div>',
        '<div style="background:#f5eee6;padding:5px;">',
        '<div id="scheduleItemBox" style="',
        'height:333px;background:#fff;"></div>',
        '<div style="height:5px;background:#f5eee6;"></div>',
        '</td></tr></table>');

    _gel('canvasContent').innerHTML = html.join('');

    //To hide Vote now section on top in Wave Container.
    if (wave && wave.isInWaveContainer()) {
      _gel('banInWave').style.display = 'none';
      _gel('thumb').style.display = 'none';
    } else if (!objTrip.thumb_up && !objTrip.thumb_down) {
      _gel('thumb').style.display = 'none';
    }
    addKeyListener('search-box');
    createScheduledItemBox(objTrip.sdate, objTrip.edate, objTrip.duration);

    _gel('unscheduleItemBox').onscroll = onScroll;
    _gel('scheduleItemBox').onscroll = onScroll;

    // show location and marker on map.
    var start = new GLatLng(objTrip.lat, objTrip.lng);
    gMap = new GMap2(_gel('mapBox'));
    gMap.setCenter(start, tabAccuracy[objTrip.accuracy]);
    gMap.addControl(new GSmallMapControl());
    gMap.panTo(start);
    objTrip.fetchAllItems();
  }
  _IG_AdjustIFrameHeight();
}

/**
 * Fill item on interface.
 * @param {number} nIndex Position of item.
 */
function fillItem(nIndex) {
  var objTrip = JGulliverData.getCurrentTrip();
  var objItem = objTrip.arrItem[nIndex];
  var address = (objItem.address == null) ? '' : _unesc(objItem.address);

  var unscheduledItemHtml = [];
  var element;
  /** If trip date is found, we have to check for item dates
     for scheduling the item else check for days */
  if (isEmpty(objTrip.sdate) == false) {
    if (isEmpty(objItem.sdate) == false && isEmpty(objItem.edate) == false) {
      var divIndex = getDateDiff(objTrip.sdate, objItem.sdate);
      if (divIndex >= 0) {
        element = _gel('item' + divIndex);
        if (element.innerHTML.strip() == '')
          _gel('scheduledInfoBox' + divIndex).style.height = '';
        element.innerHTML += getItemHtml(nIndex, objItem.name, address, true);
      } else {
        unscheduledItemHtml.push(getItemHtml(nIndex,
                                             objItem.name, address, false));
      }
    } else {
       unscheduledItemHtml.push(getItemHtml(nIndex,
                                            objItem.name, address, false));
    }
  } else {
    var divIndex = objItem.day - 1;
    if (divIndex >= 0 && divIndex < objTrip.duration) {
      element = _gel('item' + divIndex);
      if (element.innerHTML.strip() == '')
        _gel('scheduledInfoBox' + divIndex).style.height = '';

      element.innerHTML +=
          getItemHtml(nIndex, _unesc(objItem.name), address, true);
    } else {
      unscheduledItemHtml.push(getItemHtml(nIndex, _unesc(objItem.name),
                                           address, false));
    }
  }
  _gel('unscheduleItemBox').innerHTML += unscheduledItemHtml.join('');
}

/**
 * Callback function for trips tab.
 * @param {boolean} setFlag Flag indicates whether UI is to be refreshed or not.
 */
function callbackTrips(setFlag) {
  refreshUIData = setFlag || false;
  var canvasDiv = _gel('canvasContent');
  canvasDiv.style.height = '610px';
  canvasDiv.innerHTML = LOADING_TPL;
  _gel('messageContainer').style.display = 'none';
  _gel('search-box').value = '';
  if (_gel('serverMsg')) {
    _gel('serverMsg').innerHTML = '';
    _gel('msgContainer').style.display = 'none';
  }
  loadFriends();
}

/**
 * Initialize all parameters and preferences.
 */
function init() {
  var isWavelet = wave && wave.isInWaveContainer();
  timerMsg = new _IG_MiniMessage(null, _gel('serverMsg'));
  geocoder = new GClientGeocoder();
  gViewer = gParams['currentViewer'];
  var id = gParams['tripId'];
  if (id) {
    var trip = new JTrip();
    trip.id = id;
    trip.name = gParams['tripName'];
    trip.ownerId = gParams['ownerId'];
    trip.ownerName = gParams['ownerName'];
    trip.loc = gParams['tripLocation'];
    trip.lat = gParams['lat'];
    trip.lng = gParams['lng'];
    trip.accuracy = gParams['accuracy'];
    trip.duration = gParams['duration'];
    if (!isEmpty(gParams['sdate']))
      trip.sdate = formatDate(gParams['sdate'], '%m/%d/%Y');
    if (!isEmpty(gParams['fdate']))
      trip.edate = formatDate(gParams['fdate'], '%m/%d/%Y');
    trip.rating = gParams['rating'];
    var gulliverData = JGulliverData.getInstance();
    gulliverData.arrTripData.push(trip);
    gulliverData.nSelectedTripIndex = 0;
  }
  if (isWavelet) {
    _gel('messageContainer').innerHTML = TAB_HEADER_TPL_NPL;
  } else {
    _gel('messageContainer').innerHTML = TAB_HEADER_TPL;
  }
  _gel('messageContainer').className = 'tablib_emptyTab_canvas';
  if (isEmpty(id)) {
    callbackTrips();
  } else {
    showDragTip();
    callbackCreateTrip();
  }
  _IG_AdjustIFrameHeight();
}

//Entry point.
_IG_RegisterOnloadHandler(init);
