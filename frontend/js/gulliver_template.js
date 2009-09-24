/**
 * @fileoverview File containing all templates of Gulliver Gadget.
 * @author gadgetfactory@google.com (gadgetfactory)
 */

/** @const */
var TAB_HEADER_TPL = '<table width="100%" border="0" cellspacing="0" ' +
    'cellpadding="0"><tr><td width="80%" style="font-weight:bold;' +
    'font-size:12px;" valign="top">' + prefs.getMsg('search_item') + '&nbsp;' +
    '<input onblur="if(this.value==\'\') this.className=\'google-search\'" ' +
    'onfocus="this.className=\'\'" type="text" class="google-search" value=""' +
    ' id="search-box" style="width:35%;border: 1px solid #7e9db9;"/>&nbsp;' +
    '<input type="button" onclick="searchMapContent(1, null, false)" value="' +
    prefs.getMsg('search') +
    '" style="width:65px;"><br><span class="egText"><nobr>' +
    prefs.getMsg('eg') +
    '<b>:</b> <a href="javascript:searchPOI(\'eat\');">' + prefs.getMsg('eat') +
    '</a>, <a href="javascript:searchPOI(\'sleep\');">' + prefs.getMsg('sleep') +
    '</a>, <a href="javascript:searchPOI(\'see\');">' + prefs.getMsg('see') +
    '</a>, <a href="javascript:searchPOI(\'shop\');">' + prefs.getMsg('shop') +
    '</a>, <a href="javascript:searchPOI(\'do\');">' + prefs.getMsg('do') +
    '</a>, <a href="javascript:searchPOI(\'night\');">' +
    prefs.getMsg('night') + '</a></nobr></span></td><td align="right" ' +
    'width="20%" style="vertical-align: top;">' +
    '<input type="button" value="Sync trip updates" id="refreshUpdates" ' +
    'onclick="callbackTrips(true);"></td>' +
    '<td style="vertical-align: top;" align="right">' +
    '<a style="text-decoration:none" href="http://maps.google.com/"' +
    ' target="__blank" id="exportLink" ><input type="button" ' +
    'value="Export trip" id="query_button"></a></td>' +
    '</tr></table>';

/** @const */
var TAB_HEADER_TPL_NPL = '<table width="100%" border="0" cellspacing="0" ' +
    'cellpadding="0"><tr><td width="80%" style="font-weight:bold;' +
    'font-size:12px;" valign="top">' + prefs.getMsg('search_item') +
    '&nbsp;<input ' +
    'onblur="if(this.value==\'\') this.className=\'google-search\'" ' +
    'onfocus="this.className=\'\'" type="text" class="google-search" value=""' +
    ' id="search-box" style="width:35%;border: 1px solid #7e9db9;"/>&nbsp;' +
    '<input type="button" onclick="searchMapContent(1, null, false)" ' +
    'value="' + prefs.getMsg('search') + '" style="width:65px;"><br>' +
    '</td><td align="right" width="20%" style="vertical-align:top;">' +
    '<input type="button" value="Sync trip updates" id="refreshUpdates" ' +
    'onclick="callbackTrips(true);"></td>' +
    '<td style="vertical-align: top;" align="right">' +
    '<a style="text-decoration:none" href="http://maps.google.com/"' +
    ' target="__blank" id="exportLink" ><input type="button" ' +
    'value="Export trip" id="query_button"></a></td>' +
    '</tr></table>';

/** @const */
var ADD_TO_TRIP_TPL = '<div id="resultPopup%INDEX%" ' +
    'style="display:none;width:100%;"></div><div id="resultItem%INDEX%" ' +
    'style="padding-bottom:3px;cursor:move;"><table cellspacing="2" ' +
    'cellpadding="1" width="100%" border="0" style="font-size:12px;"><tr>' +
    '<td rowspan="2" valign="top" width="5%"><img src="' +
    'http://www.google.com/mapfiles/marker%ICON%.png" ' +
    'onmouseover="addHighLightmarker(this,%INDEX%);" ' +
    'onmouseout="removeHighLightMarker(this,%INDEX%);"></td>' +
    '<td valign="top" width="70%">' +
    '<span class="itemTitle" id="itemTitle%INDEX%" ' +
    'onclick="showPopup(%INDEX%,true);">%ITEM_NAME%</span></td>' +
    '<td align="right" valign="top" width="25%">-<span class="link" ' +
    'style="color:#00c;"' +
    'onclick="addItem(\'resultItem%INDEX%\');">' + prefs.getMsg('addtotrip') +
    '</span></td></tr><tr><td colspan="2" width="100%">%CONTACT_INFO%<span ' +
    'style="text-align:right;padding-left:10px;">%WEB_URL%</span></td></tr>' +
    '</table></div>';

var ITEM_INFO_DRAG_TPL = '<table cellspacing="0" ' +
   'cellpadding="0" border="0" style="font-size:12px;padding-bottom:4px;' +
   'width:265px; border-bottom:1px solid #d3e6d2">' +
   '<tr><td width="30%" class="link" align="left" ' +
   'id="link-info" style="padding-left: 4px;display:%DISPLAY%">%IMAGE%</td>' +
   '<td width="70%"><div><table width="100%" cellpadding="0" cellspacing="0">' +
   '<tr><td width="1%" style="cursor:pointer;" align="left" ' +
   'onclick="addItem(\'resultItem%INDEX%\');">%DRAG_IMG%' +
   '</td><td align="left"><table><tr><td align="right">%CLOSE_IMG%' +
   '</td></tr><tr><td class="egText" style="color:#00f;margin-left:0;" ' +
   'valign="bottom" align="left">' +
   '<a href="#" onclick="addItem(\'resultItem%INDEX%\');">' +
   '<nobr>%DRAG_MSG% </nobr></a></td></tr></table></td></tr></table></div>' +
   '<span class="itemTitle" onclick="javascript:closeInfoWindow();" ' +
   'style="text-decoration:none;color:#000; font-weight:bold;">' +
   '%TITLE%</span><br/>%CONTACT_INFO%' +
   '&nbsp;&nbsp;<span id="web-link" style="text-align:right;padding-left:0;' +
   'text-decoration:none;">%WEB_URL%</span></td></tr>' +
   '</table>' +
   '<div id="reviews-container" ' +
   'style="overflow-y:auto; overflow-x:hidden;height:%HEIGHT%;' +
   'padding-top:1px; width:340px;">' +
   '<table cellpadding="2"  cellspacing="1" border="0" ' +
   'style="font-size:12px;"><tr>' +
   '<td style="font-size:13px;font-weight:bold;color:#000;">' +
   '%REVIEWS_LABEL%</td></tr><tr><td><div>%REVIEWS%</div></td></tr></table>' +
   '</div>';

var ITEM_INFO_TPL = '<table cellspacing="1" ' +
    'cellpadding="2" border="0" style="font-size:12px;padding-bottom:2px;">' +
    '<tr><td rowspan="3" valign="top" width="5%"><span style="' +
    'padding-left:2px;padding-top:2px;"><img src="' +
    'http://www.google.com/mapfiles/marker%MARKER%.png"/></span></td><td ' +
    'width="*"><div class="itemTitle" onmousedown="closeInfoWindow(true);" ' +
    'style="text-decoration:none;color:#00f;">%TITLE%</div>' +
    '</td><td width="62px" align="right" valign="top" id="link-cell">' +
    '<span id="linkonPopup" class="link" ' +
    'onclick="addItem(\'resultItem%INDEX%\');">' + prefs.getMsg('addtotrip') +
    '&nbsp;</span></td><td width="5%" valign="top" align="right">' +
    '<span class="link" onmousedown="closeInfoWindow(true);">' +
    '<img src="' + FILE_SERVER + '/images/close_promo.gif" ' +
    'border="0"></span></td></tr>' +
    '<tr><td colspan="2">' +
    '%CONTACT_INFO%<span id="weblink" style="text-align:right;' +
    'padding-left:10px;">%WEB_URL%</span></td><td></td></tr><tr %NOURL%>' +
    '<td class="link" align="left" colspan="2" id="linkInfo" ' +
    'style="display:%DISPLAY%">%IMAGE%' +
    '<a href="%OVERVIEW_URL%" target="_blank">' + prefs.getMsg('overview') +
    '</a><br><a href="%DETAIL_URL%" target="_blank">' + prefs.getMsg('detail') +
    '</a><br><a href="%REVIEWS_URL%" target="_blank">' +
    prefs.getMsg('review') + '</a><br>' +
    '<a href="%PHOTOS_URL%" target="_blank">' + prefs.getMsg('photo-video') +
    '</a></td><td></td></tr></table>' +
    '<table width="100%" cellpadding="0" cellspacing="0"' +
    ' style="border-top:1px solid #f5eee6;" ' +
    '<tr><td><span style="font-size:11px;padding-left:10px;font-weight:bold">' +
    '<nobr>Item added by</nobr><span style="padding-left:4px;' +
    'color:#993300;">%OWNER_NAME%</span></span>' +
    '</span> </td></tr>' +
    '<tr><td style="padding-left:10px;"><span id="thumbItem">' +
    '<span style="font-size:10px;">' +
    '<nobr><b><span id="sitem_thum_up">%THUMB_UP%' +
    '</span></b> thumbs up, ' +
    '<b><span id="sitem_thum_down">%THUMB_DOWN%</span></b>' +
    ' thumbs down</nobr>' +
    '<span id="itemVoteNow" style="padding-left:5px;">' +
    '<a href="#" onmousedown="javascript:addVotePopUP(%INDEX%);">' +
    'Vote Now</a></span></span></span></td></tr></table>';

/** @const */
var SCHEDULED_ITEM_TPL = '<div id="drag-%DRAG_ITEM_COUNTER%">' +
    '<div id="itemPopup%INDEX%"></div>' +
    '<div id="scheduledRow%INDEX%" style="width:100%;padding-bottom:5px;">' +
    '<table cellspacing="0" cellpadding="0" border="0" style="width:100%;">' +
    '<tr><td style="width:30px;"></td><td style="' +
    'width:70%;"><span class="itemTitle" id="scheduledItemTitle%INDEX%" ' +
    'onmousedown="showPopup(%INDEX%,false);return false;" title="%TITLE%">' +
    '%NAME%</span></td><td align="right" valign="top"><img src="' +
    FILE_SERVER + '/images/thumbsup.png" onmousedown="' +
    'addVotePopUP(%INDEX%)" style="cursor:pointer;' +
    'padding-right:5px;" title="' + prefs.getMsg('your_vote') + '" />' +
    '<img title="' + prefs.getMsg('edit_item') + '" ' +
    'src="' + FILE_SERVER + '/images/editIcon.gif" onmousedown="' +
    'editItemDialog(%INDEX%,true);" style="cursor:pointer;" /></td></tr>' +
    '<tr><td style="width:30px;"></td><td colspan="2" ' +
    'style="font-size:12px;">%CONTACT_INFO%</td></tr></table></div></div>';

/** @const */
var UNSCHEDULED_ITEM_TPL = '<div id="drag-%DRAG_ITEM_COUNTER%">' +
    '<div id="itemPopup%INDEX%"></div>' +
    '<div id="unscheduledRow%INDEX%" style="width:100%;padding-bottom:5px;">' +
    '<table cellspacing="0" cellpadding="0" border="0" style="width:100%;">' +
    '<tr><td style="width:30px;"></td><td style="width:70%;">' +
    '<span class="itemTitle" title="%TITLE%" id="unscheduledItem%INDEX%" ' +
    'onmousedown="showPopup(%INDEX%,false);return false;">%NAME%</span></td>' +
    '<td align="right" valign="top"><img src="' +
    FILE_SERVER + '/images/thumbsup.png" onmousedown="' +
    'addVotePopUP(%INDEX%)" style="cursor:pointer;padding-right:5px;' +
    '" title="' + prefs.getMsg('your_vote') + '"/><img title="' +
    prefs.getMsg('edit_item') + '" style="cursor:pointer;" src="' +
    FILE_SERVER + '/images/editIcon.gif" onmousedown="' +
    'editItemDialog(%INDEX%,false);"></td></tr><tr>' +
    '<td style="width:30px;"></td><td colspan="3" style="font-size:12px;">' +
    '%CONTACT_INFO%</td></tr></table></div></div>';

/** @const */
var SCHEDULED_ITEM_TPL_NPL = '<div id="drag-%DRAG_ITEM_COUNTER%">' +
    '<div id="itemPopup%INDEX%"></div>' +
    '<div id="scheduledRow%INDEX%" style="width:100%;padding-bottom:5px;">' +
    '<table cellspacing="0" cellpadding="0" border="0" style="width:100%;">' +
    '<tr><td style="width:30px;"></td><td style="' +
    'width:70%;"><span class="itemTitle" id="scheduledItemTitle%INDEX%" ' +
    'onmousedown="showPopup(%INDEX%,false);return false;" title="%TITLE%">' +
    '%NAME%</span></td><td align="right" valign="top">' +
    '<img title="' + prefs.getMsg('edit_item') + '" ' +
    'src="' + FILE_SERVER + '/images/editIcon.gif" onmousedown="' +
    'editItemDialog(%INDEX%,true);" style="cursor:pointer;" /></td></tr>' +
    '<tr><td style="width:30px;"></td><td colspan="2" ' +
    'style="font-size:12px;">%CONTACT_INFO%</td></tr></table></div></div>';

/** @const */
var UNSCHEDULED_ITEM_TPL_NPL = '<div id="drag-%DRAG_ITEM_COUNTER%">' +
    '<div id="itemPopup%INDEX%"></div>' +
    '<div id="unscheduledRow%INDEX%" style="width:100%;padding-bottom:5px;">' +
    '<table cellspacing="0" cellpadding="0" border="0" style="width:100%;">' +
    '<tr><td style="width:30px;"></td><td style="width:70%;">' +
    '<span class="itemTitle" title="%TITLE%" id="unscheduledItem%INDEX%" ' +
    'onmousedown="showPopup(%INDEX%,false);return false;">%NAME%</span></td>' +
    '<td align="right" valign="top"><img  title="' + prefs.getMsg('edit_item')
    '" style="cursor:pointer;" ' +
    'src="' + FILE_SERVER + '/images/editIcon.gif" onmousedown="' +
    'editItemDialog(%INDEX%,false);"></td></tr><tr>' +
    '<td style="width:30px;"></td><td colspan="3" style="font-size:12px;">' +
    '%CONTACT_INFO%</td></tr></table></div></div>';

/** @const */
var CALENDAR_TPL =
    '<div id="maindialog"><div class="title" valign="middle">' +
    prefs.getMsg('add_item') + '</div><div style="padding:2px 0pt 0pt 4px;' +
    'visibility:visible;display:block;z-index:10000;"></div>' +
    '<div id="datepicker1" class="dpDiv" ' +
    'style="visibility:hidden;"></div><input name="ADate" style="' +
    'visibility:hidden;width:1px;height:1px;">' +
    '<div id="wrngmsg" style="visibility:hidden;"></div>' +
    '<div style="padding-left:5px;color:#000;font-size:12px;' +
    'position:absolute;top:210px;"><b>%ITEM_NAME%</b></div>' +
    '<div style="top:225px;font-size:11px;position:absolute;' +
    'color:#000;"><input type="radio" id="dateradio" ' +
    'name="group1" onclick="selectAsScheduled();">' + prefs.getMsg('on') +
    '&nbsp;<span id="selecteddate"></span><br><input ' +
    'type="radio" name="group1" onclick="selectAsUnscheduled();" checked>' +
    prefs.getMsg('as_unscheduled') + '<br><div style="padding-top:3px;"><span ' +
    'align="right" float="left" style="padding-left:2px;"><input' +
    ' type="button" style="width:60px;" onclick="javascript:' +
    'hideDialogView();" id="cancelButton" value="' + prefs.getMsg('cancel') +
    '"></span><span align="right" style="padding-left:28px;">' +
    '<input onclick="javascript:addItemDate();hideDialog();" ' +
    'style="width:60px;" type="button" value="' + prefs.getMsg('add_days') +
    '"></span></div></div></div>';

/** @const */
var TRIP_DAYS_DIALOG_TPL =
    '<div id="unscheduleItem_dlg"><div class="title-addItem" valign="middle" ' +
    '><div style="float:left;">' +
    prefs.getMsg('add_item') + '</div><div style="float:right;"><img ' +
    'style="width:15px;cursor:pointer;padding-left:1px;" ' +
    'src="' + FILE_SERVER + '/images/close_promo.gif" ' +
    'onclick="javascript:hideDialogView();"></div></div><div ' +
    'style="color:#666;padding-left:5px;" class="hint">' +
    '<span style="color:#f00;">' + prefs.getMsg('tip') +
    '</span> ' + prefs.getMsg('drag_tip') + '</div>' +
    '<div style="padding-left:5px;color:#000;' +
    'padding-top: 4px;padding-bottom:4px;"><b>%ITEM_NAME%</b></div>' +
    '<div><table border="0" cellpadding="0" cellspacing="0" ' +
    'style="font-size:11px;color:#000;"><tr><td colspan="2">' +
    '<span style="float:left;margin-top:2px;">' +
    '<input type="radio" id="dateradio" name="group" ' +
    'onclick="enableDays();"></span>&nbsp;<span>' +
    prefs.getMsg('as_scheduled') + '</span><span align="center" style="' +
    'padding-left:20px;color:#000;font-size:13px;">' +
    '<span id="daysoption"></span></span></td></tr><tr><td width="1px">' +
    '<span style="float:left;' +
    'margin-top:2px;"><input type="radio" name="group" id="unsched" ' +
    'onclick="disableDays();" checked></span></td>' +
    '<td style="padding-top:4px;padding-left:3px"><span>' +
    prefs.getMsg('as_unscheduled') + '</span></td></tr></table>' +
    '</div><div style="padding-top:8px;"><span style="padding-left:2px;' +
    'float:left;"><input type="button" style="width:60px;" ' +
    'onclick="javascript:hideDialogView();" id="cancelButton" value="' +
    prefs.getMsg('cancel') + '"></span><span style="float:right;"><input ' +
    'onclick="javascript:addItemDate();hideDialog();" style="width:60px;" ' +
    'type="button" value="' + prefs.getMsg('add_days') +
    '"></span></div></div>';

/** @const */
var SEND_MAIL_DIALOG_TPL = '<div id="viewedititemdialogbox">' +
    '<div id="sendMailBox" class="title_viewDatesDialog" valign="middle">' +
    '<table class="dialog-title-send" width="100%"><tr><td align="left">' +
    prefs.getMsg('send_mail') + '</td><td align="right">' +
    '<span class="link" onclick="javascript:hideViewDateDialogBox();">' +
    '<img border="0" style="width:15px;padding-left:1px;"' +
    'src="' + FILE_SERVER + '/images/close_promo.gif"/></span>' +
    '</td></tr></table></div><table style="width:380px;" class="tabledata" ' +
    'cellspacing="4" cellpadding="0" height="85%"><tr><td align="left" ' +
    'width="25%" style="font-family:arial;color:#666;padding-left:1px;">' +
    prefs.getMsg('to') + '<span style="color:#ff0000">*</span> : </td>' +
    '<td colspan="2"><input value="" name="emailId" id="emailId" type="text" ' +
    'style="width:99%;font-family:arial,sans-serif;font-size:12px;"></td>' +
    '</tr><tr><td align="left" width="25%" ' +
    'valign="top" style="font-family:arial;color:#666;' +
    'padding-left:1px;">' + prefs.getMsg('item_desc') + ' : ' +
    '</td><td colspan="2"><textarea id="mailDescp"' +
    'style="width:99%;font-family:arial,sans-serif;font-size:12px;" ' +
    'rows="5" cols="38"></textarea></td></tr>' +
    '<tr><td align="left" style="padding-left:1px;">' +
    '<input type="button" id="cancelButton" ' +
    'onclick="javascript:hideViewDateDialogBox();" value="' +
    prefs.getMsg('cancel') + '"></td><td align="center"><div id="mailErr" ' +
    'class="err" style="display:none;"></div></td><td align="right">' +
    '<input type="button" style="width:36px;" value="' +
    prefs.getMsg('ok') +
    '" onclick="JGulliverData.getCurrentTrip().sendMail();">' +
    '</td></tr></table></div>';

/** @const */
var LOCATION_DIALOG_TPL = '<div class="change-loc-dlg">' +
    '<div class="dialog-title"><div style="padding-left:2px;float:left;">' +
    prefs.getMsg('changelocation') + '</div><div style="float:right;">' +
    '<img src="' + FILE_SERVER + '/images/close_promo.gif" ' +
    'onclick="javascript:hideDialog();" border="0" ' +
    'style="width:15px;cursor:pointer;margin-left:3px;padding-left:1px;" />' +
    '</div></div>' +
    '<table cellspacing="1" border="0" cellpadding="1" width="100%">' +
    '<tr><td colspan="2" ' +
    'style="padding-left:5px;"><font class="heading">' +
    prefs.getMsg('where_visit') +
    '</font></td></tr><tr><td style="padding-left:5px;">' +
    '<input type="text" id="txtLocation" width="70%" class="txtBox" ' +
    'value="%LOCATION%" /><font class="hint" style="color:#666;">' +
    prefs.getMsg('egLocation') + '</font></td></tr><tr><td colspan="2">' +
    '<div id="wrngmsg" style="visibility:hidden;" ' +
    'align="center">' + prefs.getMsg('invalid_location') + '</div></td></tr>' +
    '<tr><td align="left"><input type="button" name="" ' +
    'onclick="javascript:hideViewDateDialogBox();" id="cancelButton" ' +
    'value="' + prefs.getMsg('cancel') +
    '"></td><td align="right"><input type="button" ' +
    'style="width:36px;align:right;" name="" value="' + prefs.getMsg('ok') +
    '" onclick="changeTripLocation();"></td></tr></table></div>';

/** @const */
var EDIT_ITEM_DIALOG_TPL = '<div id="viewedititemdialogbox">' +
    '<div id="editItemDialogBox" class="title_viewDatesDialog" ' +
    'valign="middle"><table class="dialog-title" ' +
    'width="100%" style="padding:2px 3px 2px 0;"><tr><td align="left">' +
    prefs.getMsg('edit_item') + '</td><td align="right">' +
    '<span class="link" onclick="javascript:hideViewDateDialogBox();">' +
    '<img border="0" style="width:15px;padding-left:1px;"' +
    'src="' + FILE_SERVER + '/images/close_promo.gif"/></span>' +
    '</td></tr></table></div><table class="tabledata" ' +
    'cellspacing="4" cellpadding="0" width="395px"><tr><td style="' +
    'color:#666;font-size:13px;">' + prefs.getMsg('item_name') +
    '<span style="color:#ff0000">*</span> : </td><td>' +
    '<textarea style="height:22px; width:305px;font-size:12px;' +
    'font-family:arial,san-serif;" rows="1" cols="35"' +
    ' id="itemName" maxlength="45">%ITEM_NAME%</textarea></td></tr>' +
    '<tr><td style="color:#666;font-size:13px;" valign="top">' +
    prefs.getMsg('item_addr') +
    '<span style="color:#f00">*</span> : </td><td>' +
    '<textarea %READONLY% ; style="width:305px;font-size:12px;' +
    'font-family:arial,san-serif" ' +
    'rows="3" cols="35" id="itemDesp">%DESCRIPTION%</textarea></td></tr>' +
    '<tr><td></td><td align="left" ><table cellpadding="0"' +
    'style="font-size:12px;font-family: arial,sans-serif;"><tr>' +
    '<td><input type="radio" name="unscheduleCheck" ' +
    'onclick="unscheduleRadioButton();" id="unscheduleCheck">' +
    prefs.getMsg('unsch') +
    '</td><td><input type="radio" name="daysCheck" id="daysCheck" ' +
    'onclick="daysRadioButton();">' + prefs.getMsg('day') +
    ':&nbsp;<SELECT NAME="daysSelect"' +
    'id="daysSelect">%DAYSOPTION%</SELECT></td></tr><tr><td><button onclick="' +
    'createDeleteItemBox(%INDEX%,true)" style="width:129px;padding-left:0;">' +
    '<img src="' + FILE_SERVER + '/images/trash.gif" ' +
    'width="13" height="13"/>&nbsp;' + prefs.getMsg('delete_this_item') +
    '</button></td></tr><tr><td colspan="2">' +
    '<div id="wrngmsg" style="display:hidden;"></div></td></tr></table>' +
    '</td></tr><tr><td align="left"><input type="button" name="" ' +
    'onclick="javascript:hideViewDateDialogBox();" id="cancelButton" ' +
    'value="' + prefs.getMsg('cancel') + '"></td><td align="right">' +
    '<input type="button" value="' + prefs.getMsg('save') +
    '" onClick="saveEditItem(%INDEX%);"></td></tr></table></div>';

/** @const */
var DATE_DIALOG_TPL = '<div id="viewdatedialogbox">' +
    '<div class="dialog-title" style="padding-left:1px;">' +
    '<div style="float:left;">' +
    prefs.getMsg('setthedates_msg') + '</div><div style="float:right;">' +
    '<img style="width:15px;cursor:pointer;padding-left:1px;" ' +
    'src="' + FILE_SERVER + '/images/close_promo.gif" ' +
    'onclick="hideViewDateDialogBox();"/></div></div>' +
    '<table class="tabledata" width="100%" cellspacing="2" cellpadding="2">' +
    '<tr><td colspan="2" width="100%">' + prefs.getMsg('no_of_days') +
    '&nbsp;<input maxlength="3" onkeyup="onUpdateDuration(this);" ' +
    'id="editDaysBox" type="text" size="3" value="%DURATION%" ' +
    'style="width:35px;height:19px;"></td></tr><tr><td width="50%">' +
    'Start Date: <input type="text" id="startDate" name="startdate" value="' +
    '%START_DATE%" style="width:75px;height:20px;" readonly><img name="' +
    'calenderimg1" src="' + FILE_SERVER + '/images/calender.png" ' +
    'onclick="displayDatePicker(\'startdate\'\,\'calenderimg1\',1);" ' +
    'width="19" height="19" align="top"></img></td><td width="50%">' +
    prefs.getMsg('end_date') +
    '<input type="text" id="endDate" name="enddate" ' +
    'value="%END_DATE%" style="width:75px;height:20px;" readonly>' +
    '<img name="calenderimg1" ' +
    'src="' + FILE_SERVER + '/images/calender.png" align="top" ' +
    'width="19" height="19" ' +
    'onclick="displayDatePicker(\'enddate\'\,\'calenderimg2\',1);"></img>' +
    '</td></tr><tr><td colspan="2" align="center"><input type="button" ' +
    'value="' + prefs.getMsg('clear_start_end_dates') +
    '" onclick="javascript:clearDate();" ' +
    'style="width:186px;"></td></tr><tr><td colspan="2">' +
    '<div id="wrngmsg" style="visibility:hidden;" ' +
    'align="center">' + prefs.getMsg('invalid_duration') + '</div></td>' +
    '</tr><tr><td align="left"><input type="button" id="cancelButton" ' +
    'onclick="javascript:hideViewDateDialogBox();" ' +
    'value="' + prefs.getMsg('cancel') + '"></td><td align="right">' +
    '<input style="width:36px;" type="button" value="' + prefs.getMsg('ok') +
    '" onclick="saveTripDates();"></td></tr></table></div>';

/** @const */
var CUSTOM_ITEM_DIALOG_TPL = '<div id="create-new-item" >' +
    '<div class="dialog-title"><div style="float:left;">' +
    prefs.getMsg('create_item') +
    '</div><div style="float:right;"><img border="0" ' +
    'src="' + FILE_SERVER + '/images/close_promo.gif" ' +
    'style="width:15px;cursor:pointer;padding-left:1px;" ' +
    'onclick="javascript:hideViewDateDialogBox();"/></div></div>' +
    '<div><table class="tableCreateItemDialog" width="100%"><tr>' +
    '<td width="25%" valign="top" style="color:#666;">' +
    prefs.getMsg('item_name') + '<span style="color:#ff0000">*</span> : </td>' +
    '<td valign="top"><input type="text" name="itemName" style="width:99%;" ' +
    'id="itemName" maxlength="64"></td></tr><tr><td width="25%" ' +
    'valign="top" style="color:#666;">' + prefs.getMsg('item_addr') +
    '<span style="color:#ff0000">*</span> : </td><td valign="top">' +
    '<textarea name="description" style="width:99%;" rows="3" ' +
    'id="itemDescription"></textarea></td></tr><tr><td width="20%"/><td>' +
    '<div id="wrngmsg" style="visibility:hidden;" ' +
    'align="center"></div></td></tr><tr>' +
    '<td width="20%"></td><td valign="top"><input onclick="disableDays();" ' +
    'type="radio" name="group1" checked>' +
    prefs.getMsg('unsch') + '</td></tr><tr>' +
    '<td width="20%"></td><td valign="top"><input onclick="enableDays();" ' +
    'type="radio" name="group1" id="dateradio">%DATE_HTML%</td>' +
    '</tr><tr><td colspan="2"></td></tr><tr><td align="left">' +
    '<input id="cancelButton" type="button" value="' + prefs.getMsg('cancel') +
    '" onclick="javascript:hideViewDateDialogBox();"></td>' +
    '<td align="right"><input type="button" value="' + prefs.getMsg('save') +
    '" onclick="javascript:saveNewItem();"></td></tr></table></div></div>';

/** @const */
var DELETE_ITEM_DIALOG_TPL = '<div id="delete_item" class="delete_item" ' +
    'style="height:100%;"><table ' +
    'width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td ' +
    'class="dialog-title" align="left">' + prefs.getMsg('delete_item') +
    '&nbsp;-&nbsp;%ITEM_NAME%</td></tr><tr><td valign="middle" ' +
    'height="60px" class="tableCreateItemDialog" align="center">' +
    prefs.getMsg('delete_item_confirm') + '</td></tr><tr><td width="100%">' +
    '<input type="button" style="width:40px;float:right;margin-right:5px;" ' +
    'onclick="deleteSelectedItem(%INDEX%);" value="' + prefs.getMsg('yes') +
    '">&nbsp;<input type="button" value="' + prefs.getMsg('no') +
    '" id="cancelButton" ' +
    'onclick="hideDialog();" style="width:40px;"></td></tr></table>';

/** @const */
var DELETE_TRIP_DIALOG_TPL = '<div id="delete_trip" class="delete_item" ' +
    'style="height:100%;"><table ' +
    'width="100%" cellpadding="0" cellspacing="0" border="0">' +
    '<tr class="dialog-title" style="height: 20px;vertical-align:top;">' +
    '<td align="left" style="padding-left:2px;">' +
    prefs.getMsg('delete_trip') + '&nbsp;-&nbsp;%TRIP_NAME%</td>' +
    '<td align="right">' +
    '<img src="' + FILE_SERVER + '/images/close_promo.gif" ' +
    'onclick="javascript:hideDialog();" border="0" ' +
    'style="width:15px;cursor:pointer;padding-left:1px;" /></td></tr><tr>' +
    '<td valign="middle" colspan="2" height="60px" ' +
    'class="tableCreateItemDialog" align="center">' +
    prefs.getMsg('delete_trip_confirm') + '</td></tr><tr><td align="left" ' +
    'style="padding-right:2px;padding-left:2px;">' +
    '<input id="cancelButton" type="button" ' +
    'value="' + prefs.getMsg('no') + '" onclick="hideDialog();" ' +
    'style="width:40px;"></td><td align="right" ' +
    'style="padding-right:2px;"><input type="button" style="width:40px;" ' +
    'onclick="JGulliverData.getInstance().deleteTrip();" value="' +
    prefs.getMsg('yes') + '"></td></tr></table>';

/** @const */
var ACTION_MSG_TPL = '<span style="font-weight:bold;position:' +
    'relative;top:3px"> %MESSAGE%&nbsp;<a href="javascript:void(0);" ' +
    'style="text-decoration:none;"</span>';

/** @const */
var CREATE_TRIP_DIALOG_TPL = '<div id="create-trip" ' +
    'class="create-new-item"><div class="dialog-title" style="' +
    'width:307px;">' + prefs.getMsg('createTrip') +
    '</div><div><table border="0" width="307px"><tr>' +
    '<td width="100%"><font class="heading">' + prefs.getMsg('where_visit') +
    '</font></td></tr><tr><td width="100%"><input ' +
    'type="text" id="create-location" name="create-location" class="txtBox"/>' +
    '<font class="hint" style="color:#666;">' + prefs.getMsg('egLocation') +
    '</font></td></tr><tr><td width="100%">' +
    '<div id="server_msg" class="server_msg"></div></td>' +
    '</tr><tr><td valign="top" align="right" width="100%"><input ' +
    'type="button" value="' + prefs.getMsg('create') +
    '" onClick="validateTrip();"/>&nbsp;' +
    '<input id="cancel" type="button" value="' + prefs.getMsg('cancel') +
    '" onclick="cancelCreateTrip();" style="width:60px;"></td></tr></table>' +
    '</div>';

/** @const */
var EDIT_TRIP_NAME_DIALOG_TPL = '<div id="edit-trip-name"><table ' +
    'width="100%;" cellpadding="0" cellspacing="0" border="0">' +
    '<tr class="dialog-title" style="height:25px;">' +
    '<td align="left" style="padding-left: 2px;">' +
    prefs.getMsg('edit_trip_name') + '</td><td align="right"><img border="0" ' +
    'style="margin-right:4px;width:15px;cursor:pointer;padding-left:1px;" ' +
    'src="' + FILE_SERVER + '/images/close_promo.gif" ' +
    'onclick="javascript:hideDialog();"></img></td></tr><tr>' +
    '<td valign="middle" height="60px" class="tableCreateItemDialog" ' +
    'align="center" colspan="2">' + prefs.getMsg('trip_name') +
    ':&nbsp;<input type="text" ' +
    'maxlength="64" value="%TRIP_NAME%" id="tripNameEdit"></td>' +
    '</tr><tr><td align="left" style="padding:0 2px 0 2px;">' +
    '<input id="cancelButton" type="button" value="' +
    prefs.getMsg('cancel') +
    '" onclick="hideDialog();" style="width:60px;"></td><td align="right" ' +
    'style="padding-right:2px;"><input type="button" style="width:36px;" ' +
    'onclick="javascript:updateTripName();" value="' + prefs.getMsg('ok') +
    '"></td></tr></table></div>';
