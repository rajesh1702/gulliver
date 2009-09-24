/**
 * @fileoverview Code to handle trip data.
 * @author gadgetfactory@google.com (gadgetfactory)
 */

/**
 * Class JTrip defines trip object.
 * @constructor.
 */
function JTrip() {
  this.id = '';
  this.ownerId = '';
  this.ownerName = '';
  this.name = '';
  this.lat = '';
  this.lng = '';
  this.loc = '';
  this.accuracy = 0;
  this.zoomlevel = DEFAULT_ZOOM_LEVEL;
  this.duration = DEFAULT_DURATION;
  this.rating = DEFAULT_RATING;
  this.sdate = '';
  this.edate = '';
  this.thumb_up = 0;
  this.thumb_down = 0;
  this.arrItem = [];
}

/**
 * Fetch all items related to current trip.
 */
JTrip.prototype.fetchAllItems = function() {
  var objTrip = this;
  var url = BASE_URL + '/getAllTripItems?trip_id=' + this.id +
            '&rand=' + Math.random();
  gadgets.io.makeRequest(url, function(response) {
    var tripItems = gadgets.json.parse(response.data);
    if (tripItems.length > 0) {
      for (var i = 0; i < tripItems.length; i++) {
        var objItem = {
            id: '', name: '', address: '', review: '',
            sdate: '', edate: '', duration: 1, category: '',
            lat: '', lng: '', link: '', day: 0, weburl: '',
            imgurl: '', Item_thumb_up: 0, Item_thumb_down: 0,
            bCustom: ''
        };
        objItem.id = tripItems[i].item_id;
        objItem.name = _unesc(tripItems[i].name);
        objItem.item_owner = _unesc(tripItems[i].item_owner);
        objItem.address = _unesc(tripItems[i].location);
        objItem.lat = tripItems[i].lat;
        objItem.lng = tripItems[i].lang;
        objItem.review = _unesc(tripItems[i].review);
        objItem.sdate = tripItems[i].sdate;
        objItem.edate = tripItems[i].fdate;
        objItem.duration = tripItems[i].duration;
        objItem.category = tripItems[i].category;
        objItem.day = tripItems[i].day;
        objItem.link = tripItems[i].link;
        objItem.weburl = tripItems[i].weburl;
        objItem.imgurl = tripItems[i].imgurl;
        objItem.bCustom = tripItems[i].custom;
        if (tripItems[i].Item_thumb_up) {
          objItem.Item_thumb_up = tripItems[i].Item_thumb_up;
        }
        if (tripItems[i].Item_thumb_down) {
          objItem.Item_thumb_down = tripItems[i].Item_thumb_down;
        }

        objTrip.arrItem.push(objItem);
        fillItem(i);
      }
      makeItemsDraggable();
      showTripItemsOnmap();
   }
  });
};


/**
 * It updates all values for a specific item in to db.
 * @param {number} nIndex index of the item.
 * @param {string} itemName Name of the item.
 * @param {string} itemDesp Description of the item.
 * @param {number} itemDay Day of the item.
 * @param {string} itemSdate Start date of the item.
 * @param {string} itemFdate Finish date of the item.
 */
JTrip.prototype.updateItemDetails = function(nIndex, itemName, itemDesp,
                                             itemDay, itemSdate, itemFdate) {
  var objTrip = this;
  var objItem = objTrip.arrItem[nIndex];
  var params = {};
  params[gadgets.io.RequestParameters.METHOD] = gadgets.io.MethodType.POST;
  var postdata = gadgets.io.encodeValues({
    'item_id': objItem.id,
    'sdate': itemSdate,
    'fdate': itemFdate,
    'name': itemName,
    'review': itemDesp,
    'day' : itemDay
  });
  params[gadgets.io.RequestParameters.POST_DATA] = postdata;
  var url = BASE_URL + '/updateItemInfo?rand=' + Math.random();
  gadgets.io.makeRequest(url, function(response) {
    _gel('msgContainer').style.display = 'block';
    var html = ACTION_MSG_TPL;
    var reaponseData = gadgets.json.parse(response.data);
    if (reaponseData.error == enDBTransaction.ERROR) {
      html = html.replace(/%MESSAGE%/, prefs.getMsg('itemdates_update_err'))
                 .replace(/%NAME%/, _unesc(objItem.name));
      _gel('serverMsg').innerHTML = '';
      timerMsg.createTimerMessage(html, 5);
      return;
    }
    html = html.replace(/%MESSAGE%/, prefs.getMsg('itemdates_updated'))
               .replace(/%NAME%/, _unesc(objItem.name));
    _gel('serverMsg').innerHTML = '';
    timerMsg.createTimerMessage(html, 5);

    objItem.name = itemName;
    objItem.review = itemDesp;
    objItem.sdate = itemSdate ? formatDate(itemSdate, '%M%d%Y') : '';
    objItem.edate = itemFdate ? formatDate(itemFdate, '%M%d%Y') : '';
    objItem.day = itemDay;

    _gel('unscheduleItemBox').innerHTML = '';
    createScheduledItemBox(objTrip.sdate, objTrip.edate, objTrip.duration);
    objTrip.showAllItems();
  }, params);
};


/**
 * It updates name of current trip in to db.
 * @param {string} strName name of trip.
 */
JTrip.prototype.updateName = function(strName) {
  _gel('editdialog').innerHTML = '';
  var objTrip = this;
  var params = {};
  params[gadgets.io.RequestParameters.METHOD] = gadgets.io.MethodType.POST;
  var postdata = gadgets.io.encodeValues({
    'trip_id': this.id,
    'trip_name': strName
  });
  params[gadgets.io.RequestParameters.POST_DATA] = postdata;
  var url = BASE_URL + '/updateTripName?rand=' + Math.random();
  gadgets.io.makeRequest(url, function(response) {
    var reaponseData = gadgets.json.parse(response.data);
    if (reaponseData.error == enDBTransaction.ERROR) {
      _gel('msgContainer').style.display = 'block';
      var html = ACTION_MSG_TPL;
      html = html.replace(/%MESSAGE%/, prefs.getMsg('tripname_update_err'));
      _gel('serverMsg').innerHTML = '';
      timerMsg.createTimerMessage(html, 5);
      return;
    }
    objTrip.name = strName;
    var tripTitle = _gel('tripName');
    if (strName.length > MAX_TRIPITEM_NAME) {
      tripTitle.innerHTML = strName.substr(0, MAX_TRIPITEM_NAME) + '...';
      tripTitle.title = strName;
    } else {
      tripTitle.innerHTML = strName;
      tripTitle.title = '';
    }
  }, params);
};

/**
 * It updates location of current trip in to db.
 * @param {string} strLocation Location of trip.
 * @param {string} newLat Trip location latitude.
 * @param {string} newLang Trip location longitude.
 * @param {number} accuracy Trip loacation accuracy.
 */
JTrip.prototype.updateLocation = function(strLocation,
                                          newLat,
                                          newLang,
                                          accuracy) {
  var objTrip = this;
  var params = {};
  params[gadgets.io.RequestParameters.METHOD] = gadgets.io.MethodType.POST;
  var postdata = gadgets.io.encodeValues({
    'trip_id': this.id,
    'lat': newLat,
    'lng' : newLang,
    'location': strLocation
  });
  params[gadgets.io.RequestParameters.POST_DATA] = postdata;
  var url = BASE_URL + '/updateTripLocation?rand=' + Math.random();
  gadgets.io.makeRequest(url, function(response) {
    _gel('msgContainer').style.display = 'block';
    var html = ACTION_MSG_TPL;
    var responseData = gadgets.json.parse(response.data);
    if (responseData.error == enDBTransaction.ERROR) {
      html = html.replace(/%MESSAGE%/, prefs.getMsg('triploc_update_err'));
      _gel('serverMsg').innerHTML = '';
      timerMsg.createTimerMessage(html, 5);
      return false;
    }
    html = html.replace(/%MESSAGE%/, prefs.getMsg('triploc_updated'));
    _gel('serverMsg').innerHTML = '';
    timerMsg.createTimerMessage(html, 5);
    objTrip.loc = strLocation;
    objTrip.lng = newLang;
    objTrip.lat = newLat;
    gMap.clearOverlays();
    gMap.setCenter(new GLatLng(newLat, newLang), tabAccuracy[accuracy]);
    var start = new GLatLng(objTrip.lat, objTrip.lng);
  }, params);
};

/**
 * It updates rating of current trip in to db.
 * @param {number} rating Rating of trip.
 */
JTrip.prototype.updateRating = function(rating) {
  var objTrip = this;
  var params = {};
  params[gadgets.io.RequestParameters.METHOD] = gadgets.io.MethodType.POST;
  var postdata = gadgets.io.encodeValues({
    'trip_id': this.id,
    'rating': this.rating
  });
  params[gadgets.io.RequestParameters.POST_DATA] = postdata;
  var url = BASE_URL + '/updateTripRating?rand=' + Math.random();

  gadgets.io.makeRequest(url, function(response) {
    var reaponseData = gadgets.json.parse(response.data);
    if (reaponseData.error == enDBTransaction.ERROR) {
      _gel('msgContainer').style.display = 'block';
      var html = ACTION_MSG_TPL;
      html = html.replace(/%MESSAGE%/, prefs.getMsg('rating_update_err'));
      _gel('serverMsg').innerHTML = '';
      timerMsg.createTimerMessage(html, 5);
      objTrip.rating = rating
      fStarOut();
    }
  }, params);
};

/**
 * It updates duration value for current trip in to db.
 * @param {number} nDays duration of trip.
 */
JTrip.prototype.updateDuration = function(nDays) {
  var objTrip = this;
  var params = {};
  params[gadgets.io.RequestParameters.METHOD] = gadgets.io.MethodType.POST;
  var postdata = gadgets.io.encodeValues({
    'trip_id': this.id,
    'duration': nDays
  });
  params[gadgets.io.RequestParameters.POST_DATA] = postdata;
  var url = BASE_URL + '/updateTripDate?rand=' + Math.random();
  gadgets.io.makeRequest(url, function(response) {
    var reaponseData = gadgets.json.parse(response.data);
    if (reaponseData.error == enDBTransaction.ERROR) {
      _gel('msgContainer').style.display = 'block';
      var html = ACTION_MSG_TPL;
      html = html.replace(/%MESSAGE%/, prefs.getMsg('tripduration_update_err'));
      _gel('serverMsg').innerHTML = '';
      timerMsg.createTimerMessage(html, 5);
      return;
    }

    // Update the items which goes outside the range of trip duration.
    for (var i = 0; i < objTrip.arrItem.length; i++) {
      if (objTrip.arrItem[i].day > nDays)
        objTrip.arrItem[i].day = 0;
    }
    objTrip.duration = nDays;
    _gel('unscheduleItemBox').innerHTML = '';
    createScheduledItemBox(null, null, nDays);
    objTrip.showAllItems();
  }, params);
};

/**
 * It updates dates value for current trip in to db.
 * @param {string} strStartDate start date of trip.
 * @param {string} strEndDate end date of trip.
 * @param {number} nDays duration of trip.
 */
JTrip.prototype.updateDates = function(strStartDate, strEndDate, nDays) {
  var objTrip = this;
  var params = {};
  params[gadgets.io.RequestParameters.METHOD] = gadgets.io.MethodType.POST;
  var postdata = gadgets.io.encodeValues({
    'trip_id': this.id,
    'sdate': strStartDate,
    'fdate': strEndDate,
    'duration': nDays
  });
  params[gadgets.io.RequestParameters.POST_DATA] = postdata;
  var url = BASE_URL + '/updateTripDate?rand=' + Math.random();
  gadgets.io.makeRequest(url, function(response) {
    var reaponseData = gadgets.json.parse(response.data);
    if (reaponseData.error == enDBTransaction.ERROR) {
      _gel('msgContainer').style.display = 'block';
      var html = ACTION_MSG_TPL;
      html = html.replace(/%MESSAGE%/, prefs.getMsg('tripdates_update_err'));
      _gel('serverMsg').innerHTML = '';
      timerMsg.createTimerMessage(html, 5);
      return;
    }
    objTrip.sdate = strStartDate;
    objTrip.edate = strEndDate;
    objTrip.duration = nDays;
    _gel('unscheduleItemBox').innerHTML = '';
    createScheduledItemBox(strStartDate, strEndDate, nDays);
    objTrip.showAllItems();
  }, params);
};

/**
 * Send mail request to server.
 */
JTrip.prototype.sendMail = function() {
  var bValidate = validateEmailid(_gel('emailId').value);
  // to be extracted using open-social
  var ownerMailId = '';
  var mailErrRef = _gel('mailErr');

  if (bValidate) {
    var objTrip = JGulliverData.getCurrentTrip();
    var toList = _gel('emailId').value;
    var tempArr = toList.split(',');
    if (tempArr.length > MAX_EMAILS) {
      mailErrRef.style.display = 'block';
      mailErrRef.innerHTML = prefs.getMsg('not_more') + ' ' + MAX_EMAILS +
                                  ' ' + prefs.getMsg('email_allow');
    } else {
      var description = _gel('mailDescp').value;
      description = '<pre style="font-family:arial;' +
                    'font-weight:bold;font-size:15px;">' + description +
                    '</pre>';
      var sortOrder = ''
      if (objTrip.sdate == '' && objTrip.edate == '')
        sortOrder = 'day';
      else
        sortOrder = 'date';

      var params = {};
      params[gadgets.io.RequestParameters.METHOD] = gadgets.io.MethodType.POST;
      post_data = gadgets.io.encodeValues({
        'owner_id': objTrip.ownerId,
        'owner_name': objTrip.ownerName,
        'trip_id': objTrip.id,
        'toList': toList,
        'description': description,
        'ownerMailId': ownerMailId,
        'sortOrder': sortOrder
      });
      params[gadgets.io.RequestParameters.POST_DATA] = post_data;
      var url = BASE_URL + '/mailTrip?rand=' + Math.random();
      gadgets.io.makeRequest(url, function(response) {
        var responseData = gadgets.json.parse(response.data);
        _gel('msgContainer').style.display = 'block';
        var html = ACTION_MSG_TPL;
        if (responseData.error == enDBTransaction.ERROR) {
          html = html.replace(/%MESSAGE%/,
                              prefs.getMsg('server_error') + ': ' +
                              prefs.getMsg('mail_not') + '.');
        } else {
          html = html.replace(/%MESSAGE%/, prefs.getMsg('mail_success'));
        }
        _gel('serverMsg').innerHTML = '';
        timerMsg.createTimerMessage(html, 5);
      }, params);
      hideDialog();
    }
  } else {
    mailErrRef.style.display = 'block';
    mailErrRef.innerHTML = prefs.getMsg('wrong_email');
    _gel('emailId').focus();
  }
};

/**
 * It shows all items of trip.
 */
JTrip.prototype.showAllItems = function() {
  for (var i = 0; i < this.arrItem.length; i++) {
    fillItem(i);
  }
  makeItemsDraggable();
};

/**
 * It saves the item to current trip.
 * @param {object} objItem object of JTripItem.
 */
JTrip.prototype.saveItem = function(objItem) {
  var objTrip = this;
  objTrip.arrItem.push(objItem);
  fillItem(objTrip.arrItem.length - 1);
  makeItemsDraggable();
  if (objItem.review) {
    objItem.review = (stripHtml(objItem.review)).substring(0, 450);
  }
  var params = {};
  params[gadgets.io.RequestParameters.METHOD] = gadgets.io.MethodType.POST;
  if (!isEmpty(objItem.sdate) && !isEmpty(objItem.edate)) {
    var postdata = gadgets.io.encodeValues({
      'user_id': objTrip.ownerId,
      'item_owner': gViewer,
      'trip_id': objTrip.id,
      'custom': objItem.bCustom,
      'name': _hesc(objItem.name),
      'location': objItem.address,
      'lat': objItem.lat,
      'lang': objItem.lng,
      'review': objItem.review,
      'sdate': objItem.sdate,
      'fdate': objItem.edate,
      'duration': objItem.duration,
      'day': objItem.day,
      'category': objItem.category,
      'link': objItem.link,
      'weburl': objItem.weburl,
      'imgurl': objItem.imgurl,
      'data_source': objItem.data_source
    });
  } else {
    var postdata = gadgets.io.encodeValues({
      'user_id': objTrip.ownerId,
      'item_owner': gViewer,
      'trip_id': objTrip.id,
      'custom': objItem.bCustom,
      'name': objItem.name,
      'location': objItem.address,
      'lat': objItem.lat,
      'lang': objItem.lng,
      'review': objItem.review,
      'duration': objItem.duration,
      'day': objItem.day,
      'category': objItem.category,
      'link': objItem.link,
      'weburl': objItem.weburl,
      'imgurl': objItem.imgurl,
      'data_source': objItem.data_source
    });
  }
  params[gadgets.io.RequestParameters.POST_DATA] = postdata;
  var url = BASE_URL + '/saveItemToTrip?rand=' + Math.random();
  gadgets.io.makeRequest(url, function(response) {
    var responseData = gadgets.json.parse(response.data);
    _gel('msgContainer').style.display = 'block';
    var html = ACTION_MSG_TPL;
    if (responseData.error == enDBTransaction.ERROR) {
      // if transaction fails revert back to previous result
      objTrip.arrItem.splice(objTrip.arrItem.length - 1, 1);
      _gel('unscheduleItemBox').innerHTML = '';
      createScheduledItemBox(strStartDate, strEndDate, nDays);
      objTrip.showAllItems();
      html = html.replace(/%MESSAGE%/, prefs.getMsg('item_add_err'));
    } else {
      html = html.replace(/%MESSAGE%/, prefs.getMsg('item_added'));
      objTrip.arrItem[objTrip.arrItem.length - 1].id = responseData.id;
    }
    html = html.replace(/%NAME%/, _unesc(objItem.name));
    _gel('serverMsg').innerHTML = '';
    timerMsg.createTimerMessage(html, 5);
  }, params);
};

/**
 * It notify the concurrent users about updates on this trip.
 * @param {object} objItem object of trip item.
 */
JTrip.prototype.notifyAllConcurrentUsers = function(objItem) {
  var objTrip = this;
  var params = {};
  params[gadgets.io.RequestParameters.METHOD] = gadgets.io.MethodType.POST;
  var postdata = gadgets.io.encodeValues({
    'item_owner': gViewer,
    'trip_id': objTrip.id
  });
  params[gadgets.io.RequestParameters.POST_DATA] = postdata;
  var url = BASE_URL + '/notifyAllUsers?rand=' + Math.random();
  gadgets.io.makeRequest(url, function(response) {
    var responseData = gadgets.json.parse(response.data);
  }, params);
};

/**
 * It gives all the concurrent user of current trip.
 * @param {object} objItem object of trip item.
 */
JTrip.prototype.getAllConcurrentUsers = function() {
  var objTrip = this;
  var params = {};
  params[gadgets.io.RequestParameters.METHOD] = gadgets.io.MethodType.POST;
  var postdata = gadgets.io.encodeValues({
    'item_owner': gViewer,
    'trip_id': objTrip.id
  });
  params[gadgets.io.RequestParameters.POST_DATA] = postdata;
  var url = BASE_URL + '/getAllUsers?rand=' + Math.random();
  gadgets.io.makeRequest(url, function(response) {
    var responseData = gadgets.json.parse(response.data);
    if(responseData) {
      gFetchedData = responseData;
    }
    alert(gFetchedData + '    ' + gWhoAllWorking);
    if(gFetchedData != gWhoAllWorking) {
      _gel('msgContainer').style.display = 'block';
      var html = ACTION_MSG_TPL;
      html = html.replace(/%MESSAGE%/,
                          gFetchedData + ' is also working on this trip');
      _gel('serverMsg').innerHTML = '';
      timerMsg.createTimerMessage(html, 5);
      gWhoAllWorking = gFetchedData;
    }
  }, params);
};

/**
 * It updates date values for a specific item in to db.
 * @param {number} nIndex index of the item.
 * @param {string} strDate date to be updated for the item.
 */
JTrip.prototype.updateItemDates = function(nIndex, strDate) {
  var objTrip = this;
  var objItem = objTrip.arrItem[nIndex];
  var params = {};
  params[gadgets.io.RequestParameters.METHOD] = gadgets.io.MethodType.POST;
  var postdata = gadgets.io.encodeValues({
    'item_id': objItem.id,
    'sdate': strDate,
    'fdate': strDate
  });
  params[gadgets.io.RequestParameters.POST_DATA] = postdata;
  var url = BASE_URL + '/updateItemDate?rand=' + Math.random();
  gadgets.io.makeRequest(url, function(response) {
    var reaponseData = gadgets.json.parse(response.data);
    if (reaponseData.error == enDBTransaction.ERROR) {
      _gel('msgContainer').style.display = 'block';
      var html = ACTION_MSG_TPL;
      html = html.replace(/%MESSAGE%/, prefs.getMsg('itemdates_update_err'))
                 .replace(/%NAME%/, _unesc(objItem.name));
      _gel('serverMsg').innerHTML = '';
      timerMsg.createTimerMessage(html, 5);
      return;
    }
    objItem.sdate = strDate;
    objItem.edate = strDate;
    _gel('unscheduleItemBox').innerHTML = '';
    createScheduledItemBox(objTrip.sdate, objTrip.edate, objTrip.duration);
    objTrip.showAllItems();
    strSelectedItemId = '';
  }, params);
};

/**
 * It updates day values for current item in to db.
 * @param {number} nIndex index of the item.
 * @param {number} nDay day to be updated for the item.
 */
JTrip.prototype.updateItemDays = function(nIndex, nDay) {
  var objTrip = this;
  var objItem = objTrip.arrItem[nIndex];
  var params = {};
  params[gadgets.io.RequestParameters.METHOD] = gadgets.io.MethodType.POST;
  var postdata = gadgets.io.encodeValues({
    'item_id': this.arrItem[nIndex].id,
    'day': nDay
  });
  params[gadgets.io.RequestParameters.POST_DATA] = postdata;
  var url = BASE_URL + '/updateItemDay?rand=' + Math.random();
  gadgets.io.makeRequest(url, function(response) {
    var reaponseData = gadgets.json.parse(response.data);
    if (reaponseData.error == enDBTransaction.ERROR) {
      _gel('msgContainer').style.display = 'block';
      var html = ACTION_MSG_TPL;
      html = html.replace(/%MESSAGE%/, prefs.getMsg('itemdates_update_err'))
                 .replace(/%NAME%/, _unesc(objItem.name));
      _gel('serverMsg').innerHTML = '';
      timerMsg.createTimerMessage(html, 5);
      return;
    }
    objItem.day = nDay;
    _gel('unscheduleItemBox').innerHTML = '';
    createScheduledItemBox(objTrip.sdate, objTrip.edate, objTrip.duration);
    objTrip.showAllItems();
    strSelectedItemId = '';

  }, params);
};

/**
 * It deletes the item for current trip in to db.
 * @param {number} nIndex index of the item for current trip.
 */
JTrip.prototype.deleteItem = function(nIndex) {
  var objTrip = this;
  var itemObject = objTrip.arrItem[nIndex];
  var params = {};
  params[gadgets.io.RequestParameters.METHOD] = gadgets.io.MethodType.POST;
  var post_data = gadgets.io.encodeValues({'item_id': itemObject.id});
  params[gadgets.io.RequestParameters.POST_DATA] = post_data;
  var url = BASE_URL + '/deleteItem?rand=' + Math.random();
  gadgets.io.makeRequest(url, function(response) {
    _gel('msgContainer').style.display = 'block';
    var html = ACTION_MSG_TPL;
    var reaponseData = gadgets.json.parse(response.data);
    if (reaponseData.error == enDBTransaction.ERROR) {
      html = html.replace(/%MESSAGE%/, prefs.getMsg('item_delete_err'))
                 .replace(/%NAME%/, _unesc(itemObject.name));
      _gel('serverMsg').innerHTML = '';
      timerMsg.createTimerMessage(html, 5);
      return;
    }

    html = html.replace(/%MESSAGE%/, prefs.getMsg('item_deleted'))
               .replace(/%NAME%/, _unesc(itemObject.name));
    _gel('serverMsg').innerHTML = '';
    timerMsg.createTimerMessage(html, 5);
    objTrip.arrItem.splice(nIndex, 1);
    _gel('unscheduleItemBox').innerHTML = '';
    createScheduledItemBox(objTrip.sdate, objTrip.edate, objTrip.duration);
    objTrip.showAllItems();
    strSelectedItemId = '';
    showTripItemsOnmap();
  }, params);
};

/**
 * To mark items on the map.
 */
JTrip.prototype.markItemsOnMap = function() {
  arrSearchResults = [];
  var objTrip = this;
  var url = BASE_URL + '/getAllTripItems?trip_id=' + this.id +
            '&rand=' + Math.random();
  gadgets.io.makeRequest(url, function(response) {
    var tripItems = gadgets.json.parse(response.data);
    gMap.clearOverlays();
    if (tripItems.length > 0) {
      var iconUrl = FILE_SERVER + '/images/marker_blue.png';
      for (var i = 0; i < tripItems.length; i++) {
        addMarker(i, new GLatLng(tripItems[i].lat, tripItems[i].lang), iconUrl,
                  _unesc(tripItems[i].name), _unesc(tripItems[i].review));
        arrSearchResults.push(tripItems[i]);
      }
    }
  });
};
