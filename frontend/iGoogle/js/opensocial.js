/**
 * @fileoverview Code contains opensocial related functionality.
 * 
 */

/**
 * Contains the current view of the gadget.
 * @type {Object}
 */
var currentView = gadgets && gadgets.views &&
    gadgets.views.getCurrentView();

/**
 * Selected view name.
 * @type {string}
 */
var selectedView = currentView ?
    currentView.getName().toLowerCase() : 'home';

/**
 * Flag indicates whether user is in home view or canvas view.
 * @type {boolean}
 */
var isHomeView = selectedView == 'home';

/**
 * Used for creating, initializing and sending the viewer data.
 */
function requestData() {
  if (!isServiceAvailableForUser()) {
    var containerName = isHomeView ? 'home-content' : 'main-container';
    _gel(containerName).innerHTML =
        _gel('service_not_available').innerHTML;
  } else {
    checkSocialDataAvailable();
  }
}

/**
 * Checks if the user has social data available.
 */
function checkSocialDataAvailable(){
  var containerName = isHomeView ? 'home-content' : 'main-container';
  var req = opensocial.newDataRequest();
  req.add(req.newFetchPersonRequest(opensocial.IdSpec.PersonId.OWNER),
          'owner');
  req.send(function(data) {
    var owner = data.get('owner');
    if (owner.hadError()) {
      var error = owner.getErrorCode();
      if (error == opensocial.ResponseItem.Error.FORBIDDEN ||
          error == opensocial.ResponseItem.Error.UNAUTHORIZED) {
        _gel(containerName).innerHTML =
            _gel('user-not-authorized').innerHTML;
      }
      _IG_AdjustIFrameHeight();
    } else if (owner.getData().getId() == -1) {
      _gel(containerName).innerHTML =
          _gel('user-not-signed-in').innerHTML;
    } else {
      handleOpensocial();
    }
  });
}

/**
 * To check whether opensocial feature in user container is
 * available or not.
 * @return {boolean} Returns true if container supports opensocial
 *     feature and views else returns false.
 */
function isServiceAvailableForUser() {
  try {
    return gadgets.util.hasFeature('opensocial-0.8') &&
           gadgets.views.getSupportedViews().canvas &&
           _args().st;
  } catch (ex) {
    return false;
  }
}

/**
 * Find the list of friends with whom the application is being shared.
 */
function loadFriends() {
  var req = opensocial.newDataRequest();
  req.add(req.newFetchPersonRequest('VIEWER'), 'viewer');
  req.add(req.newFetchPeopleRequest(opensocial.newIdSpec({
      'userId': 'VIEWER',
      'groupId': 'FRIENDS'
  })), 'groupPeople');
  req.send(handleResponse);
}

