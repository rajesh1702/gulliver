/**
 * @fileoverview Library of all UI methods of Gulliver Gadget.
 * @author gadgetfactory@google.com (gadgetfactory)
 */

/**
 * It returns the html string for popup of an item.
 * @param {number} index Index of item.
 * @param {boolean} flag Flag to determine which template to choose.
 * @param {boolean} isBlue Flag to determine, whether dat is from lonely planet.
 * @return {string} itemInfo the generated html as string.
 */
function getPopupHtml(index, flag, isBlue) {
  var objTrip = JGulliverData.getCurrentTrip();
  var dataSource = isBlue ? objTrip.arrItem[index] : arrSearchResults[index]
  var itemInfo = flag ? ITEM_INFO_DRAG_TPL : ITEM_INFO_TPL;
  itemInfo = itemInfo.replace(/%INDEX%/g, index);
  var icon = String.fromCharCode('A'.charCodeAt(0) + index);
  strSelectedItemId = 'resultPopup' + index;
  // Overview url
  var url = dataSource.link + '&dtab=0&oi=';
  itemInfo = itemInfo.replace(/%OVERVIEW_URL%/g, url);

  // Detail url
  url = dataSource.link + '&dtab=1&oi=md_structdata';
  itemInfo = itemInfo.replace(/%DETAIL_URL%/g, url);

  // Reviews url
  var review = '';
  review = dataSource.review || '';
  var pattern = '&#160;&#160;';
  // Check for empty review.
  if (review && pattern != review) {
    itemInfo = itemInfo.replace(/%REVIEWS%/g, review)
        .replace(/%REVIEWS_LABEL%/g, prefs.getMsg('review'))
        .replace(/%HEIGHT%/g, '92px');
  } else {
    itemInfo = itemInfo.replace(/%REVIEWS%/g, '')
        .replace(/%REVIEWS_LABEL%/g, '')
        .replace(/%HEIGHT%/g, '0');
  }

  itemInfo = itemInfo.replace(/%REVIEWS%/g, review);

  //close image
  itemInfo = itemInfo.replace(/%CLOSE_IMG%/g, '');
  var dragMe = isBlue ? '' : prefs.getMsg('dragme');
  itemInfo = itemInfo.replace(/%DRAG_MSG%/g, dragMe);
  var image = isBlue ? '' : ('<img border="0" ' +
      'src="' + FILE_SERVER + '/images/curve_arrow.png"/>');
   itemInfo = itemInfo.replace(/%DRAG_IMG%/g, image);

  // Photos and videos url
  url = dataSource.link + '&dtab=3&oi=md_photos';
  itemInfo = itemInfo.replace(/%PHOTOS_URL%/g, url);

  url = dataSource.imgurl || '';
  if (url) {
    url = '<img src="' + url + '" align="left" ' +
          'width="70" height="70" ' +
          'style="margin-right:10px;cursor:pointer;" />';
    itemInfo = itemInfo.replace(/%DISPLAY%/g, 'block');
  } else {
    url = '';
    itemInfo = itemInfo.replace(/%DISPLAY%/g, 'none');
  }
  itemInfo = itemInfo.replace(/%IMAGE%/g, url)
      .replace(/%TITLE%/g, _unesc(dataSource.name ||
                                      prefs.getMsg('no_data_found')));

  var address1 = '';
  dataSource.address = dataSource.address ||
      dataSource.location ||
      prefs.getMsg('no_address_received');
  var newLineIndex = dataSource.address.search(/<br\/>/);
  var zip = '';
  if (newLineIndex != -1) {
    zip = dataSource.address.substr(newLineIndex);
    address1 = dataSource.address.substr(0, newLineIndex);
  } else {
    address1 = dataSource.address;
  }

  if (address1 != null && address1 != undefined) {
    var address;
    if (address1.length > 50) {
      var sIndex = address1.indexOf(' ', 49);
      if (sIndex != -1) {
        address = address1.substr(0, sIndex) + '<br>';
        address += address1.substr(sIndex + 1);
      } else {
        address = address1;
      }
    } else {
      address = address1;
    }
    if (zip != '')
      address += zip;
    itemInfo = itemInfo.replace(/%CONTACT_INFO%/g, address);
  } else {
    if (zip != '')
      address += zip;
    itemInfo = itemInfo.replace(/%CONTACT_INFO%/g, '');
  }
  itemInfo = itemInfo.replace(/%MARKER%/g, icon);
  if (dataSource.weburl) {
    //_gel('web-link').style.cursor = 'pointer'
    itemInfo = itemInfo.replace(/%WEB_URL%/g, dataSource.weburl || '');
  } else {
    itemInfo = itemInfo.replace(/%WEB_URL%/g, '');
  }
  return itemInfo;
}

/**
 * Shows Add Items dialog box.
 * @param {string} itemName The name of item.
 */
function showAddItemDialog(itemName) {
  var objTrip = JGulliverData.getCurrentTrip();
  if (isEmpty(objTrip.sdate)) {
    var daysHtml = TRIP_DAYS_DIALOG_TPL;
    daysHtml = daysHtml.replace(/%ITEM_NAME%/, itemName);
    showDialog(daysHtml);
    _gel('cancelButton').focus();
    var html = ['<select style="width:25%;" id="days_id" disabled>'];
    for (var i = 1; i <= objTrip.duration; i++) {
      html.push('<option value="', i, '">' +
                prefs.getMsg('day') +
                '&nbsp;', i, '</option>');
    }
    html.push('</select>');
    _gel('daysoption').innerHTML = html.join('');
  } else {
    var calHtml = CALENDAR_TPL;
    calHtml = calHtml.replace(/%ITEM_NAME%/, itemName);
    showDialog(calHtml);
    _gel('cancelButton').focus();
    fillcalendar();
  }
}

/**
 * Opens mail sending dialog box.
 */
function openMailDialog() {
  // Close if any popup window is there.
  closeInfoWindow();
  var html = SEND_MAIL_DIALOG_TPL;
  showDialog(html);
  _gel('emailId').focus();
}

function showToolTip(point, name) {
  var divPoint = gMap.fromLatLngToContainerPixel(point);
  var mapElementPosition = getPosition(_gel('map-container'));
  var ele = _gel('toolTip');
  var eleStyle = ele.style;
  eleStyle.display = 'block';
  eleStyle.zIndex = 10000;
  eleStyle.left = divPoint.x + mapElementPosition.x + 'px';
  eleStyle.top = divPoint.y + mapElementPosition.y - 40 + 'px';
  ele.innerHTML = name;
}

/**
 * Creates the dialog box to delete the item.
 * @param {number} index index of trip item.
 * @param {boolean} bScheduled Flag to check if item is scheduled.
 */
function createDeleteItemBox(index, bScheduled) {
  // Close if any popup window is there.
  closeInfoWindow();

  var title = JGulliverData.getCurrentTrip().arrItem[index].name;
  if (title.length > 20)
    title = title.substr(0, 20) + '...';
  var html = DELETE_ITEM_DIALOG_TPL;
  html = html.replace(/%INDEX%/g, index)
             .replace(/%ITEM_NAME%/g, title)
             .replace(/%IS_SCHEDULED%/, bScheduled);
  showDialog(html);
  _gel('cancelButton').focus();
}

/**
 * Creates the dialog box to delete the trip.
 */
function createDeleteTripBox() {
  // Close if any popup window is there.
  closeInfoWindow();

  var title = JGulliverData.getCurrentTrip().name;
  if (title.length > 20)
    title = title.substr(0, 20) + '...';
  var html = DELETE_TRIP_DIALOG_TPL;
  html = html.replace(/%TRIP_NAME%/, title);
  showDialog(html);
  _gel('cancelButton').focus();
}

/**
 * Creates section for scheduled item.
 * @param {string} strStartDate start date of trip.
 * @param {string} strEndDate end date of trip.
 * @param {number} nDays duration of trip.
 */
function createScheduledItemBox(strStartDate, strEndDate, nDays) {
  var html = [];
  var i;
  if (isEmpty(strStartDate) && isEmpty(strEndDate)) {
    _gel('duration').innerHTML = '(' + nDays + '&nbsp;' +
                                 prefs.getMsg('days') + ')';
    for (i = 0; i < nDays; i++) {
      html.push('<div style="padding-bottom:5px;width:100%;height:55px;" ',
                'id="scheduledInfoBox', i, '"><div class="itemDay" id="day',
                i, '">' + prefs.getMsg('day') + ' ', (i + 1),
                '</div><div id="item', i, '"></div></div>');
    }
  } else {
    _gel('duration').innerHTML = formatDate(strStartDate, '%M%d') + ' - ' +
        formatDate(strEndDate, '%M%d%Y') + ' (' + nDays + ' ' +
                   prefs.getMsg('days') + ')';
    for (i = 0; i < nDays; i++) {
      html.push('<div style="padding-bottom:5px;width:100%;height:55px;" ',
                'id="scheduledInfoBox', i, '"><div class="itemDay" id="day',
                i, '">', addDaysToDate(strStartDate, i, '%MM%d%W'),
                '</div><div id="item', i, '"></div></div>');
    }
  }
  _gel('scheduleItemBox').innerHTML = html.join('');
  _IG_AdjustIFrameHeight();

  // To reset the drag item counter.
  dragItemCounter = 0;

  // Add target for drag an drop the items.
  dropTargets = [];
  addDropTarget(_gel('unscheduleItemBox'));
  for (i = 0; i < nDays; i++) {
    addDropTarget(_gel('scheduledInfoBox' + i));
  }
  addMarkersTarget(nDays);
}

/**
* Function to hide dialog.
*/
function hideViewDateDialogBox() {
  hideCalender();
  hideDialog();
  adjustiFrame();
}

/**
* Function to hide opened calender.
*/
function hideCalender() {
  if (_gel(datePickerDivID) != null) {
    var pickerDiv = _gel(datePickerDivID);
    pickerDiv.style.visibility = 'hidden';
    pickerDiv.style.display = 'none';
  }
}

/**
 * Function to hide dialog.
 */
function hideDialogView() {
  if (_gel(datePickerDivID1)) {
    var pickerDiv = _gel(datePickerDivID1);
    pickerDiv.style.visibility = 'hidden';
    pickerDiv.style.display = 'none';
  }
  adjustiFrame();
}

/**
 * Dialog for edit.
 */
function showEditDialog() {
  // Close if any popup window is there.
  closeInfoWindow();
  var editDlgInfo = EDIT_TRIP_NAME_DIALOG_TPL;
  editDlgInfo = editDlgInfo.replace(/%TRIP_NAME%/,
                                    JGulliverData.getCurrentTrip().name);
  showDialog(editDlgInfo);
  var element = _gel('tripNameEdit');
  element.focus();
  element.select();
  addKeyListener('tripNameEdit');
}

/**
 * Creates the dialog box to create the trip.
 */
function createTripBox() {
  // Close if any popup window is there.
  closeInfoWindow();
  var html = CREATE_TRIP_DIALOG_TPL;
  showDialog(html);
  _gel('create-location').focus();
}

/**
* Closes the popup window opened on click of trip name.
*/
function closeInfoWindow() {
  _IG_AdjustIFrameHeight(_gel('gadget-body').offsetHeight);
  if (strSelectedItemId == '')
    return;
  if (_gel(strSelectedItemId) != null &&
      (strSelectedItemId.indexOf('itemPopup') != -1 ||
       strSelectedItemId.indexOf('resultPopup') != -1)) {
    _gel(strSelectedItemId).innerHTML = '';
    _gel(strSelectedItemId).style.display = 'none';
  }
  strSelectedItemId = '';
}

/**
 * Creates the dialog box to delete the trip.
 */
function createDeleteTripBox() {
  // Close if any popup window is there.
  closeInfoWindow();

  var title = JGulliverData.getCurrentTrip().name;
  if (title.length > 20)
    title = title.substr(0, 20) + '...';
  var html = DELETE_TRIP_DIALOG_TPL;
  html = html.replace(/%TRIP_NAME%/, title);
  showDialog(html);
  _gel('cancelButton').focus();
}

/**
 * Dialog for edit dates.
 */
function showDateDialog() {
  // Close if any popup window is there.
  closeInfoWindow();

  var objTrip = JGulliverData.getCurrentTrip();
  var strStartDate = isEmpty(objTrip.sdate) ?
                     prefs.getMsg('unspecified') : objTrip.sdate;
  var strEndDate = isEmpty(objTrip.edate) ?
                   prefs.getMsg('unspecified') : objTrip.edate;
  var days = isEmpty(objTrip.duration) ? DEFAULT_DURATION : objTrip.duration;

  var html = DATE_DIALOG_TPL;
  html = html.replace(/%DURATION%/, days)
      .replace(/%START_DATE%/, strStartDate)
      .replace(/%END_DATE%/, strEndDate);
  showDialog(html);
  _gel('cancelButton').focus();
}

/**
 * Dialog for edit dates.
 */
function showLocationDialog() {
  // Close if any popup window is there.
  closeInfoWindow();

  var objTrip = JGulliverData.getCurrentTrip();
  var html = LOCATION_DIALOG_TPL;
  html = html.replace(/%LOCATION%/, objTrip.loc);
  showDialog(html);
  addKeyListener('txtLocation');
  var element = _gel('txtLocation');
  element.focus();
  element.select();
}

/**
 * Function to open Create New Item Dialog.
 */
function createCustomItemDialog() {
  // Close if any popup window is there.
  closeInfoWindow();

  var objTrip = JGulliverData.getCurrentTrip();
  // Prepare html to display dates or days, based on trip date.
  var html = [];
  if (isEmpty(objTrip.sdate)) {
    html = [prefs.getMsg('day') +
            ': <select style="width:25%;" id="days_id" disabled>'];
    for (var i = 1; i <= objTrip.duration; i++) {
      html.push('<option value="', i, '">' + prefs.getMsg('day') +
                '&nbsp;', i, '</option>');
    }
    html.push('</select>');
  } else {
    fillTripDates();
    html.push(prefs.getMsg('date') + '&nbsp;<input type="text" ',
    'id="startdate" name="startdateview" value="' +
    prefs.getMsg('unspecified') +
    '" style="width:80px;" readonly>&nbsp;<img name="calender2view"',
    'src="' + FILE_SERVER + '/images/calender.png" ',
    'onclick="_gel(\'dateradio\').checked=true; ',
    'displayDatePicker(\'startdateview\',\'calender2view\',2,\'',
    tripdates[0], '\');" width="19" height="19" align="top">');
  }
  html = html.join('');
  var dialogHtml = CUSTOM_ITEM_DIALOG_TPL;
  dialogHtml = dialogHtml.replace(/%DATE_HTML%/, html);
  showDialog(dialogHtml);
  _gel('itemName').focus();
}

/**
 * To display Edit Item Dialog.
 * @param  {number} index Index of item.
 * @param {boolean} bScheduled Flag to determine whether its a scheduled item.
 */
function editItemDialog(index, bScheduled) {
  // Close if any popup window is there.
  closeInfoWindow();

  var daysOption = [];
  var tempDate = '';

  strSelectedItemId = 'resultPopup' + index;
  var objTrip = JGulliverData.getCurrentTrip();

  if (!isEmpty(objTrip.sdate)) {
    for (var i = 0;i < objTrip.duration; i++) {
      tempDate = addDaysToDate(objTrip.sdate, i, '%m/%d/%Y');

      if (addDaysToDate(objTrip.arrItem[index].sdate, 0, '%m/%d/%Y') ==
          tempDate) {
         daysOption.push('<option value="', tempDate, '" selected>',
            tempDate, '</option>');
      } else {
        daysOption.push('<option value="', tempDate, '">',
                        tempDate, '</option>');
      }
    }
  } else {
    for (var i = 1; i <= objTrip.duration; i++) {
      if (objTrip.arrItem[index].day == i)
        daysOption.push('<option value="', i, '" selected>' +
                        prefs.getMsg('day') + ' ' , i,
                        '</option>');
      else
        daysOption.push('<option value="', i, '">' +
                        prefs.getMsg('day') + ' ' , i,
                        '</option>');
    }
  }

  var description;
  if (objTrip.arrItem[index].bCustom) {
    description = objTrip.arrItem[index].review;
  } else {
    description = objTrip.arrItem[index].address;
    description = description.replace('<br/>', '');
  }
  if (isEmpty(description))
    description = '';

  var html = EDIT_ITEM_DIALOG_TPL;
  html = html.replace(/%INDEX%/g, index)
      .replace(/%ITEM_NAME%/g, _hesc(_unesc(objTrip.arrItem[index].name)))
      .replace(/%DESCRIPTION%/, _unesc(description))
      .replace(/%DAYSOPTION%/, daysOption.join(''));
  if (!objTrip.arrItem[index].bCustom) {
    html = html.replace(/%READONLY%/, 'readonly="readonly"');
  } else {
    html = html.replace(/%READONLY%/, '');
  }
  showDialog(html);
  _gel('cancelButton').focus();
  if (!bScheduled) {
    unscheduleRadioButton();
  } else {
    daysRadioButton();
  }
}

/**
 * Function to display print items dialog.
 */
function showPrintDialog() {
  // close if any dialog is opened
  closeInfoWindow();
  var scheduledItems = 0;
  var unScheduledItems = 0;
  var objTrip = JGulliverData.getCurrentTrip();
  var arrTripItem = [];
  var currentItemSdate;
  var sdate, edate;
  var currentDate = new Date();
  var currentDay = currentDate.getDay();
  currentDay = dayArrayLong[currentDay];
  currentDate = formatDate(getDateString(currentDate), '%MM%d%Y');
  var tripDateString = '';
  if (!isEmpty(objTrip.sdate)) {
    tripDateString = formatDate(objTrip.sdate, '%MM%d') + ' - ' +
                     formatDate(objTrip.edate, '%MM%d%Y') +
                     ' / ' + objTrip.duration + ' days';

  } else {
    tripDateString = objTrip.duration + ' days';
  }
  var unscheduledHtml = ['</ul></div><ul><div style="padding-right:2px;">',
      '<br><div class="print_subTitle"><u>',
      prefs.getMsg('unscheduled_items'), '</u></div></ul>',
      '<div id="unscheduled-item-details" ',
      'style="font-family:arial,sans-serif;font-size:15px;color:#000;">'];

  // NEED TO VALIDATE AND FIX THIS HTML
  var wrappedName = objTrip.name;
  var wrappedName = wrappedName.split(' ');
  for (var k = 0; k < wrappedName.length; k++) {
    if (wrappedName[k].length > MAX_DESC_LENGTH)
        wrappedName[k] = wrapText(wrappedName[k]);
  }
  wrappedName = wrappedName.join(' ');
  var printHtml = [
      '<div id="print_Trip_Items_Dialog">',
      '<div class="printTitle" width="100%">',
      '<div align="right" style="color:#fff">',
      '<div align="left" class="dialog-title" ',
      'style="float:left;padding-left:3px">',
      prefs.getMsg('print_title'), '</div>',
      '<input type="checkbox" id="print_Map" ',
      'onclick="javascript:showStaticMap(', objTrip.lat, ',', objTrip.lng, ',',
      objTrip.accuracy, ');">&nbsp;', prefs.getMsg('include_large_map'),
      '<input type="button" style="width:70px;margin-left:16px;" ',
      'value="', prefs.getMsg('print_item'), '" onclick="printTripItems()" ',
      'id="printDetails"><img onclick="javascript:hideDialogView();',
      'hideDialog();" class="print_close_img" ',
      'src="' + FILE_SERVER + '/images/close_promo.gif"/></div>',
      '</div><div style="background: #f3f3f3;padding:10px;">',
      '<div id="print_Dialog"><table id="printDialogHeader" ',
      'style="padding-top:10px;',
      'font-size:15px;width:100%" cellspacing="0" cellpadding="2"><tr>',
      '<td width="5%"></td><td width="25%"><img src=',
      '"' + FILE_SERVER + '/images/googleltineraries.png"/>',
      '</td>',
      '<td width="40%" valign="center">',
      '<div style="font-weight:bold;font-size:15px;">', wrappedName, '</div>',
      '<div style="font-size:13px;">', tripDateString, '</div></td>',
      '<td style="color:#666;font-size:12px;" valign="top">',
      currentDay, '\, ', currentDate, '</td></tr>',
      '</table><div id="staticMap" style="margin-left:150px;margin-top:20px;">',
      '</div><div style="color:#000;font-size:12px;"><div id="itemDetails">',
      '<ul><div class="print_subTitle"><u>', prefs.getMsg('scheduled_items'),
      '</u></div></ul><div id="scheduled-item-details" ',
      'style= font-size:15px;">'];

      sortCol = (!isEmpty(objTrip.sdate)) ? enTripCol.DATE : enTripCol.DAY;
      bAscending = true;
      arrTripItem = objTrip.arrItem.sort(sortHandler);
  var itemSday, itemSdate, currentTripDay;
  for (var i = 0; i < arrTripItem.length; i++) {
    if (!isEmpty(objTrip.sdate)) { //main if
      if (!isEmpty(arrTripItem[i].sdate)) {
        scheduledItems = 1;
        if (currentItemSdate != arrTripItem[i].sdate) {
             printHtml.push('</ul>');
         }
        itemSdate = formatDate(arrTripItem[i].sdate, '%m/%d/%Y');
        itemSdate = new Date(itemSdate);
        itemSday = itemSdate.getDay();
        itemSdate = (getDateString(itemSdate)).split('/');
        var itemMonth = parseInt(itemSdate[0], 10) - 1;
        itemMonth = monthArrayLong[itemMonth];
        itemSdate[0] = itemMonth + ' ' +
        itemSdate[1] + ' (' + dayArrayLong[itemSday] + ')';
        if (arrTripItem[i].sdate != currentItemSdate) {
          printHtml.push('<ul><b>', itemSdate[0], '</b>');
          currentItemSdate = arrTripItem[i].sdate;
        }
      if (isEmpty(arrTripItem[i].address)) {
        var printAddOrDesc = _unesc(arrTripItem[i].review);
      } else {
        var printAddOrDesc = arrTripItem[i].address;
      }
      printHtml.push('<div style="padding-left:15px;">',
        '<div style="color:#000;font-size:15px;font-weight:bold;">',
        _unesc(arrTripItem[i].name), '</div>',
        '<div style="color:#000;font-size:12px;">', printAddOrDesc, '</div>',
        '</div>');
      if (arrTripItem[i].sdate == currentItemSdate) {
        printHtml.push('<br>');
      }
    } else {
      unScheduledItems = 1;
      unscheduledHtml.push('<ul><b>', _unesc(arrTripItem[i].name), '</b>',
          '<div style="color:#000;padding-left:15px;">');
      if (isEmpty(arrTripItem[i].address)) {
        unscheduledHtml.push(_unesc(arrTripItem[i].review));
      } else {
        unscheduledHtml.push(arrTripItem[i].address);
      }
      unscheduledHtml.push('</div></ul>');
    }
    } else { //main else

        if (arrTripItem[i].day > 0) {
          if (currentTripDay != arrTripItem[i].day) {
             printHtml.push('</ul>');
         }
          if (currentTripDay != arrTripItem[i].day) {
            printHtml.push('<ul><b>Day ', arrTripItem[i].day, '</b>');
            currentTripDay = arrTripItem[i].day;
          }
         scheduledItems = 1;
         printHtml.push('<div style="padding-left:15px;">',
           '<div style="color:#000;font-size:15px;font-weight:bold;">',
           _unesc(arrTripItem[i].name), '</div>',
           '<div style="color:#000;font-size:12px;">', arrTripItem[i].address,
           '</div></div>');
         if (arrTripItem[i].day == currentTripDay) {
           printHtml.push('<br>');
         }
      }
      else {
        unScheduledItems = 1;
        unscheduledHtml.push('<ul><b>', _unesc(arrTripItem[i].name), '</b>',
          '<div style="color:#000;padding-left:15px;">');
        if (isEmpty(arrTripItem[i].address)) {
          unscheduledHtml.push(_unesc(arrTripItem[i].review));
        } else {
          unscheduledHtml.push(arrTripItem[i].address);
        }
        unscheduledHtml.push('</div></ul>');
      }
    }
  }
  unscheduledHtml.push('</div></div><br><br>');
  printHtml.push(unscheduledHtml.join(''),
      '</div></div></div>');
  showDialog(printHtml.join(''));
  var noItemsHtml = '<div style="font-weight:bold;padding-top:40px;" ' +
                  'align="center">%MESSAGE%</div>';
  if (arrTripItem.length == 0) {
    noItemsHtml =
        noItemsHtml.replace(/%MESSAGE%/, prefs.getMsg('no_items_added'));
     _gel('itemDetails').innerHTML = noItemsHtml;
  } else if (scheduledItems == 0) {
    noItemsHtml =
        noItemsHtml.replace(/%MESSAGE%/, prefs.getMsg('no_scheduled_items'));
    _gel('scheduled-item-details').innerHTML = noItemsHtml;
  } else if (unScheduledItems == 0) {
    noItemsHtml =
        noItemsHtml.replace(/%MESSAGE%/, prefs.getMsg('no_unscheduled_items'));
    _gel('unscheduled-item-details').innerHTML = noItemsHtml;
  }
  _gel('printDetails').focus();
}

/**
* Shows the popup window on click of trip name.
* @param {number} index Position of div.
* @param {boolean} bSearchItem Flag to check if clicked on search item.
*/
function showPopup(index, bSearchItem) {
  closeInfoWindow();
  var itemInfo;
  var html = [];
  if (bSearchItem) {
    itemInfo = getPopupHtml(index);
    html = ['<div style="border:1px solid #000;',
        'background-color:#fff;position:absolute;z-index:1000;',
        'padding-bottom:5px;width:267px;">', itemInfo, '</div>'];

  } else {
    itemInfo = ITEM_INFO_TPL;
    itemInfo = itemInfo.replace(/%INDEX%/g, index);
    strSelectedItemId = 'itemPopup' + index;
    var objTrip = JGulliverData.getCurrentTrip();
    var objItem = objTrip.arrItem[index];
    itemInfo = itemInfo.replace(/%MARKER%/g, '')
        .replace(/%THUMB_UP%/, objItem.Item_thumb_up)
        .replace(/%THUMB_DOWN%/, objItem.Item_thumb_down)
        .replace(/%OWNER_NAME%/, objItem.item_owner);
    // Overview url
    var url, baseUrl;
    var bCustom = objTrip.arrItem[index].bCustom;
    if (bCustom == '' || bCustom == false) {
      baseUrl = objTrip.arrItem[index].link;
      if (objTrip.arrItem[index].data_source == 'google') {
        url = baseUrl + '&dtab=0&oi=';
        itemInfo = itemInfo.replace(/%OVERVIEW_URL%/g, url);

        // Detail url
        url = baseUrl + '&dtab=1&oi=md_structdata';
        itemInfo = itemInfo.replace(/%DETAIL_URL%/g, url);

        // Photos and videos url
        url = baseUrl + '&dtab=5&oi=md_photos&sa=X';
        itemInfo = itemInfo.replace(/%PHOTOS_URL%/g, url);

        // Reviews url
        url = baseUrl + '&dtab=2&oi=md_reviews&sa=X';
        itemInfo = itemInfo.replace(/%REVIEWS_URL%/g, url);

        itemInfo = itemInfo.replace(/%NOURL%/g, '');
      } else {
        itemInfo = itemInfo.replace(/%NOURL%/g, 'style="display:none"');
      }
      // For Image section.
      url = objTrip.arrItem[index].imgurl;
      if (!isEmpty(url)) {
        url = '<img src="' + objTrip.arrItem[index].imgurl +
              '" align="left" width="70" height="70" ' +
              'style="margin-right:10px;cursor:pointer;" />';
        itemInfo = itemInfo.replace(/%DISPLAY%/g, 'block');
      } else {
        url = '';
        itemInfo = itemInfo.replace(/%DISPLAY%/g, 'none');
      }
      itemInfo = itemInfo.replace(/%IMAGE%/g, url);
    }
    itemInfo = itemInfo.replace(/%DRAG_MSG%/g, '').replace(/%DRAG_IMG%/g, '');

    //close image
    url = '<img border="0" onmousedown="javascript:closeInfoWindow();"' +
          'src="' + FILE_SERVER + '/images/close_promo.gif"/>';
    itemInfo = itemInfo.replace(/%CLOSE_IMG%/g, url);

    var wrapTitle = objTrip.arrItem[index].name.split(' ');
    for (i = 0; i < wrapTitle.length; i++) {
      if (wrapTitle[i].length > MAX_STRING_LENGTH)
        wrapTitle[i] = wrapText(wrapTitle[i]);
    }
    var title = wrapTitle.join(' ');
    if (title.length > MAX_DESC_LENGTH) {
      title = title.substr(0, MAX_DESC_LENGTH) + '...';
    }

    itemInfo = itemInfo.replace(/%TITLE%/g, _unesc(title));

    if (objTrip.arrItem[index].address != null)
      itemInfo = itemInfo.replace(/%CONTACT_INFO%/g,
                                  objTrip.arrItem[index].address);
    else
      itemInfo = itemInfo.replace(/%CONTACT_INFO%/g, '');

    itemInfo = itemInfo.replace(/%INDEX%/g, index);
    if (objTrip.arrItem[index].weburl)
      itemInfo = itemInfo.replace(/%WEB_URL%/g, objTrip.arrItem[index].weburl);
    else
      itemInfo = itemInfo.replace(/%WEB_URL%/g, '');

    html = ['<div style="border:1px solid #000;',
        'background-color:#fff;position:absolute;z-index:1000;',
        'padding-bottom:5px;width:267px;">', itemInfo, '</div>'];
  }

  var popupWindow = _gel(strSelectedItemId);
  popupWindow.style.display = '';
  var popupx1y1 = getPosition(popupWindow);
  var popupx2y2 = {
    x: popupx1y1.x + popupWindow.offsetWidth,
    y: popupx1y1.y + popupWindow.offsetHeight
  };
  popupWindow.innerHTML = html.join('');
  if (bCustom) {
    var wrapDescription = _unesc(objTrip.arrItem[index].review);
    wrapDescription = wrapDescription.split(' ');

    for (var i = 0; i < wrapDescription.length; i++) {
      if (wrapDescription[i].length > MAX_DESC_LENGTH)
        wrapDescription[i] = wrapText(wrapDescription[i]);
    }
    wrapDescription = wrapDescription.join(' ');
    var linkElement = _gel('linkInfo');
    linkElement.style.textDecoration = 'none';
    linkElement.style.color = '#000';
    linkElement.innerHTML = '<div style="font-weight:bold;cursor:auto;">' +
        prefs.getMsg('item_desc') + ' : </div><div style="cursor:auto;" ' +
        'style="padding-left:10px;">' + wrapDescription;
    _gel('weblink').style.display = 'none';
  }

  if (!bSearchItem) {
    _gel('link-cell').style.display = 'none';
  }
  var height = Math.max(popupx2y2.y, _gel('gadget-body').offsetHeight);
  _IG_AdjustIFrameHeight(height);
}
