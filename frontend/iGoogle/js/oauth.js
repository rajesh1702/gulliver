/**
 * @fileoverview Code contains Oauth related functionality.
 * Source path:- http://code.google.com/apis/gadgets/docs/oauth.html#sample.
 * @author gadgetfactory@google.com (gadgetfactory)
 */

/**
 * Friend's contact object
 * @type {Array}
 */
var friendsContactData = [];

/**
 * If the user has opened the popup window but hasn't yet approved access,
 * display text prompting the user to confirm that s/he approved access to data.
 * The user may not ever need to click this link, if the gadget is able to
 * automatically detect when the user has approved access, but showing the link
 * gives users an option to fetch their data even if the automatic detection
 * fails. When the user confirms access, the fetchData() function is invoked
 * again to obtain and display the user's data.
 * @param {string} toShow Container to show.
 */
function showOneSection(toShow) {
  var sections = isHomeView ?
      ['home-content', 'approval', 'waiting'] :
      ['canvas-content', 'approval', 'waiting'];
  var sectionsLength = sections.length;
  var showView, element;
  for (var i = 0; i < sectionsLength; ++i) {
    showView = sections[i];
    element = _gel(showView);
    element.style.display = (showView == toShow) ? 'block' : 'none';
  }
  _IG_AdjustIFrameHeight();
}

/**
 * Process returned user google contacts feed to get current viewer of the
 * gadget. Also pushes for a user all his/her contacts data in an array.
 * @param {Object} result JSON object of user contacts data.
 */
function showResults(result) {
  _gel('approval').style.display = 'none';
  _gel('terms-services').style.display = 'none';
  var regExp = new RegExp(/.*.@(gmail||google).com$/);
  var viewName = isHomeView ? 'home-content' : 'canvas-content';
  showOneSection(viewName);
  gViewer = result.feed.author[0].name.$t;
  gOwnerId = result.feed.author[0].email.$t;
  var friendsNode = result.feed.title.$t;
  var list = result.feed.entry;
  var listLength = list.length;
  var obj;
  for (var i = 0; i < listLength; i++) {
    var entry = list[i];
    if (entry.gd$email) {
      obj = {};
      obj.emailId = entry.gd$email[0].address;
      obj.name = entry.title.$t;
      if (obj.emailId != gOwnerId && obj.emailId.match(regExp)) {
        friendsContactData.push(obj);
      }
    }
  }
  friendsContactData.sort(sortFriendsList);
  initializeTrip();
}

/**
 * Invoke makeRequest() to fetch data from the service provider endpoint.
 * Depending on the results of makeRequest, decide which version of the UI
 * to ask showOneSection() to display. If user has approved access to his
 * or her data, display data. If the user hasn't approved access yet,
 * response.oauthApprovalUrl contains a URL that includes a Google-supplied
 * request token. This is presented in the gadget as a link that the user clicks
 * to begin the approval process.
 */
function fetchOauthData() {
  var params = {};
  params[gadgets.io.RequestParameters.AUTHORIZATION] =
      gadgets.io.AuthorizationType.SIGNED;
  var url = 'http://www.google.com/m8/feeds/contacts/default/base?alt=json&max-results=10000';
  params[gadgets.io.RequestParameters.CONTENT_TYPE] =
      gadgets.io.ContentType.JSON;
  params[gadgets.io.RequestParameters.AUTHORIZATION] =
      gadgets.io.AuthorizationType.OAUTH;
  params[gadgets.io.RequestParameters.OAUTH_SERVICE_NAME] = 'google';
  params[gadgets.io.RequestParameters.OAUTH_USE_TOKEN] = 'always';
  params[gadgets.io.RequestParameters.METHOD] = gadgets.io.MethodType.GET;

  gadgets.io.makeRequest(url, function(response) {
    var viewName = isHomeView ? 'home-content' : 'canvas-content';
    if (response.oauthApprovalUrl) {
      // Create the popup handler. The onOpen function is called when the user
      // opens the popup window. The onClose function is called when the popup
      // window is closed.
      var popup = shindig.oauth.popup({
        destination: response.oauthApprovalUrl,
        windowOptions: null,
        onOpen: function() { showTermsText(true); },
        onClose: function() { fetchOauthData(); }
      });
      // Use the popup handler to attach onclick handlers to UI elements.  The
      // createOpenerOnClick() function returns an onclick handler to open the
      // popup window.  The createApprovedOnClick function returns an onclick
      // handler that will close the popup window and attempt to fetch the
      // user's data again.
      var personalize = document.getElementById('personalize');
      personalize.onclick = popup.createOpenerOnClick();
      var approvaldone = document.getElementById('approvaldone');
      approvaldone.onclick = popup.createApprovedOnClick();
      showOneSection('approval');
    } else if (response.data) {
      showOneSection(viewName);
      showResults(response.data);
    }
  }, params);
}

/**
 * To check whether opensocial feature in user container is
 * available or not.
 * @return {boolean} Returns true if container supports opensocial
 *     feature and views else returns false.
 */
function isServiceAvailableForUser() {
  try {
    return _args().is_signedin;
  } catch (ex) {
    return false;
  }
}

/**
 * Used for creating, initializing and sending the viewer data.
 */
function requestData() {
  if (!isServiceAvailableForUser()) {
    var containerName = isHomeView ? 'home-content' : 'main-container';
    _gel(containerName).innerHTML =
        _gel('user-not-signed-in').innerHTML;
  } else {
    fetchOauthData();
  }
}

/**
 * Used for sorting friends data based on friends name and email id.
 * @param {Object} obj1 The first friend's data object.
 * @param {Object} obj2 The friend's data object.
 * @return {number} 1 if first object is greater or -1 if lesser.
 */
function sortFriendsList(obj1, obj2) {
  var name1 = obj1.name;
  var emailId1 = obj1.emailId;
  var name2 = obj2.name;
  var emailId2 = obj2.emailId;
  if (name1 && name2) {
    return name1.toUpperCase() > name2.toUpperCase() ? 1 : -1;
  } else if (name1) {
    return name1.toUpperCase() > emailId2.toUpperCase() ? 1 : -1;
  } else if (name2) {
    return emailId1.toUpperCase() > name2.toUpperCase() ? 1 : -1;
  } else {
    return emailId1.toUpperCase() > emailId2.toUpperCase() ? 1 : -1;
  }
}
