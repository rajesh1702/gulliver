/**
 * @fileoverview Code to handle search.
 * @author gadgetfactory@google.com (gadgetfactory)
 */

/**
 * Class JSearchResults defines atributes for search.
 * @constructor.
 */
function JSearchResults() {
  this.name = '';
  this.address = '';
  this.lat = '';
  this.lng = '';
  this.rating = 0;
  this.weburl = '';
  this.sdate = '';
  this.edate = '';
  this.category = '';
  this.imgurl = '';
  this.link = '';
  this.review = '';
}

function search(value) {
  _gel('search-box').value = value;
  searchMapContent(1, null, false);
}

/**
 * Search for a particular query and shows the result.
 * @param {number} nPageNum page number.
 * @param {string} locPoint location latitude & longitude.
 * @param {boolean} isPagination Flag indicates whether pagination to be shown.
 */
function searchMapContent(nPageNum, locPoint, isPagination) {
  var objTrip = JGulliverData.getCurrentTrip();
  var keyword = _gel('search-box').value;
  if (keyword.strip() == '') {
    return;
  }
  gMap.clearOverlays();
  showTripItemsOnmap();
  var searchUrl = SEARCH_URL_TPL;
  var start = (nPageNum - 1) * MAX_ITEM + 1;
  var bounds = gMap.getBounds();
  var sspnUrl = bounds.getCenter().toUrlValue();
  searchUrl = searchUrl.replace(/%KEYWORD%/g, encodeURIComponent(keyword))
      .replace(/%LOCATION%/g, sspnUrl)
      .replace(/%PAGE_NUM%/g, start);
  arrSearchResults = [];
  var results;
  var searchResults, itemHtml = '';
  _gel('loading-container').innerHTML = LOADING_TPL;
  _IG_FetchXmlContent(searchUrl, function(feed) {
    try {
      _gel('loading-container').innerHTML = '';
      if (feed == null || feed == undefined) {
        _gel('resultText').innerHTML = prefs.getMsg('no_result');
        _gel('navBox').innerHTML = '';
        return;
      }
      var html = [];
      var icon, title;
      var placemarks = feed.getElementsByTagName('Placemark');
      var i;
      for (i = 0; i < placemarks.length; i++) {
        var placemark = placemarks[i].childNodes;
        var index;
        searchResults = new JSearchResults();
        searchResults.category = keyword;
        for (var j = 0; j < placemark.length; j++) {
          if (placemark[j].nodeType == 1) {
            switch (placemark[j].nodeName) {
              case 'name':
                searchResults.name = placemark[j].childNodes[0].nodeValue;
                break;
              case 'Snippet':
                if (placemark[j].childNodes[0]) {
                  searchResults.address = placemark[j].childNodes[0].nodeValue;
                } else if (placemark[j].childNodes[1]) {
                  searchResults.address = placemark[j].childNodes[1].nodeValue;
                }
                searchResults.address = searchResults.address.replace(/<br\/>/,
                                                                     ' ');
                break;
              case 'Point':
                var coordinates =
                    placemark[j].childNodes[0].childNodes[0].nodeValue;
                searchResults.lat = coordinates.split(',')[1];
                searchResults.lng = coordinates.split(',')[0];
              case 'StyleMap':
                var stylemap = placemark[j].childNodes[0];
                var text = stylemap.getElementsByTagName('text')[0];
                var review = '';
                if (text != null) {
                  var strValue = text.childNodes[0].nodeValue;
                  var reviewSplit = '';
                  var reviewIndex = strValue.search(/<br\/><b/i);
                  searchResults.review = strValue.substr(reviewIndex + 5);
                  searchResults.review = stripHtml(searchResults.review);
                  reviewSplit = searchResults.review.split('$');
                  searchResults.review = reviewSplit[0];
                  if (strValue.length > 0) {
                    index = strValue.search(/<a href=/i);
                    if (index != -1) {
                      strValue = strValue.substr(index + 9);
                      searchResults.link =
                          strValue.substr(0, strValue.indexOf('"'));
                      index = strValue.search(/<tr/i);
                      var row = '';
                      if (index != -1) {
                        strValue = strValue.substr(index + 3);
                        index = strValue.search(/<\/tr>/);
                        row = strValue.substr(0, index);
                        index = row.search(/<a href=/i);
                        if (index != -1) {
                          row = row.substr(index);
                          if (row.indexOf('style="color:#008000"') != -1) {
                            searchResults.weburl = row.substr(0,
                                                   row.indexOf('</a>') + 4);
                            searchResults.weburl = searchResults.weburl.replace(
                                                   /<a href/i,
                                                   '<a target="_blank" href');
                          }
                        }
                      }
                      index = strValue.search(/<img src="http:\/\/base.googl/i);
                      if (index != -1) {
                        strValue = strValue.substr(index + 10);
                        var img = strValue.substr(0, strValue.indexOf('"'));
                        searchResults.imgurl = img;
                      }
                    }
                  }
                } else {
                  searchResults.rating = 0;
                }
                break;

            }
          }
        }
        results = i;
        icon = '';
        var iconUrl = 'http://www.google.com/mapfiles/marker' + icon + '.png';
        addMarker(i,
                  new GLatLng(searchResults.lat, searchResults.lng),
                  iconUrl,
                  searchResults.name);
        showTripItemsOnmap();
        itemHtml = ADD_TO_TRIP_TPL;
        itemHtml = itemHtml.replace(/%ICON%/g, icon)
            .replace(/%ITEM_NAME%/g, searchResults.name)
            .replace(/%CONTACT_INFO%/g, searchResults.address)
            .replace(/%WEB_URL%/g, searchResults.weburl)
            .replace(/%INDEX%/g, i);
        html.push(itemHtml);
        searchResults.data_source = 'google';
        arrSearchResults.push(searchResults);
      }
      gMap.setCenter(new GLatLng(objTrip.lat, objTrip.lng),
                     tabAccuracy[objTrip.accuracy]);
      gMap.panTo(new GLatLng(objTrip.lat, objTrip.lng));
      // setting the map view according to new bound.
      if (locPoint == null) {
        var newZoom = gMap.getBoundsZoomLevel(bounds);
        var newCenter = bounds.getCenter();
        gMap.setCenter(newCenter, newZoom);
        gMap.panTo(bounds.getCenter());
      }

      _gel('resultBox').innerHTML = html.join('');
      var maxItem = Math.min(MAX_ITEM, arrSearchResults.length);
      for (i = 0; i < maxItem; i++) {
        makeDraggable(_gel('resultItem' + i));
      }
      var end =
          maxItem < MAX_ITEM ? (start + maxItem - 1) : nPageNum * MAX_ITEM;
      _gel('resultText').innerHTML =
          prefs.getMsg('results') + start + ' - ' + end;
      if (nPageNum == 1) {
        html = ['<div align="center" style="padding-left:33px;">'];
      } else {
        html = ['<div align="center">'];
      }
      if (nPageNum > 1) {
        html.push('<span class="link" onclick="javascript:searchMapContent(',
            (nPageNum - 1), ', null, true);"><img src="' + FILE_SERVER +
            '/images/google_nav_prev.gif" ',
            'border="0" title="', prefs.getMsg('prev_results'), '"/></span>');
      } else {
        html.push('<img src="' + FILE_SERVER +
                  '/images/google_nav_first.gif" />');
      }
      html.push('<img src="' + FILE_SERVER + '/images/google_nav_page.gif" ',
          'border="0" title="oooo" />',
          '<img src="' + FILE_SERVER + '/images/google_nav_page.gif" ',
          'border="0" title="oooo"/>');
      if (results < MAX_ITEM - 1) {
        html.push('<img src="' + FILE_SERVER +
                  '/images/google_nav_last.gif" />');
      } else {
        html.push('<a href="javascript:searchMapContent(', (nPageNum + 1),
            ', null, true)"><img ',
            'src="' + FILE_SERVER + '/images/google_nav_next.gif" ',
            'border="0" title="', prefs.getMsg('next_results'), '" />',
            '</a>');
      }
      html.push('</div>');
      _gel('navBox').innerHTML = html.join('');
      _IG_AdjustIFrameHeight();
    } catch (err) {
      // error handling
    }
  });
}
