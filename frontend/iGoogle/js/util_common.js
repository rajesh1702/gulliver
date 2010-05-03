/**
 * @fileoverview Contains common utility functions.
 * 
 */

/**
 * Used to show server response for any data store operation.
 * @param {Object} tplData Data object.
 */
function showServerMessage(tplData) {
  var tplHtml = _gel('tpl-server-msg').value;
  tplHtml = Util.supplant(tplHtml, tplData);
  _gel('server-msg').innerHTML = '';
  timerMsg.createTimerMessage(tplHtml, TIME_OUT, hideTimerMessage);
}

/**
 * Hide the dismissable message after some time is elapsed.
 */
function hideTimerMessage() {
  _gel('server-msg').innerHTML = '';
  _IG_AdjustIFrameHeight();
}
