// global variables
var TIMER = 10;
var SPEED = 100;
var WRAPPER = 'content';

// calculate the current window width
function pageWidth() {
  return window.innerWidth != null ? window.innerWidth : document.documentElement && document.documentElement.clientWidth ? document.documentElement.clientWidth : document.body != null ? document.body.clientWidth : null;
}

// calculate the current window height
function pageHeight() {
  return window.innerHeight != null? window.innerHeight : document.documentElement && document.documentElement.clientHeight ? document.documentElement.clientHeight : document.body != null? document.body.clientHeight : null;
}

// calculate the current window vertical offset
function topPosition() {
  return typeof window.pageYOffset != 'undefined' ? window.pageYOffset : document.documentElement && document.documentElement.scrollTop ? document.documentElement.scrollTop : document.body.scrollTop ? document.body.scrollTop : 0;
}

// calculate the position starting at the left of the window
function leftPosition() {
  return typeof window.pageXOffset != 'undefined' ? window.pageXOffset : document.documentElement && document.documentElement.scrollLeft ? document.documentElement.scrollLeft : document.body.scrollLeft ? document.body.scrollLeft : 0;
}

/**
 * Show the dialog box, populate the data and call the fadeDialog.
 * @param{string} message dialog content.
 */
function showDialog(message) {
  var dialog;
  var dialogcontent;
  var dialogmask;
  if(!_gel('dialog')) {
    dialog = document.createElement('div');
    dialog.id = 'dialog';
    dialogcontent = document.createElement('div');
    dialogcontent.innerHTML = message;
    dialogcontent.id = 'dialog-content';
    dialogmask = document.createElement('div');
    dialogmask.id = 'dialog-mask';
    document.body.appendChild(dialogmask);
    document.body.appendChild(dialog);
    dialog.appendChild(dialogcontent);
  } else {
    dialog = _gel('dialog');
    dialogcontent = _gel('dialog-content');
    dialogcontent.innerHTML = message;
    dialogmask = _gel('dialog-mask');
    dialogmask.style.visibility = 'visible';
    dialog.style.visibility = 'visible';
  }
  dialog.style.opacity = .00;
  dialog.style.filter = 'alpha(opacity=-15)';
  dialog.alpha = 0;
  var width = pageWidth();
  var height = pageHeight();
  var left = leftPosition();
  var top = topPosition();
  var dialogwidth = dialog.offsetWidth;
  var dialogheight = dialog.offsetHeight;
  var topposition = (top + (height / 2) - (dialogheight / 2))/2;
  var leftposition = left + (width / 2) - (dialogwidth / 2);
  dialog.style.top = topposition + 'px';
  dialog.style.left = leftposition + 'px';
  var content = _gel(WRAPPER);
  dialog.timer = setInterval('fadeDialog(1)', TIMER);
  addKeyListener('dialog');
}

/**
 * Hides the dialog box.
 */
function hideDialog() {
  var dialog = _gel('dialog');
  clearInterval(dialog.timer);
  dialog.timer = setInterval('fadeDialog(0)', TIMER);
}

/**
 * Fade-in the dialog box
 * @param{number} flag Flag to check whether dialog is to be fadded in.
 */
function fadeDialog(flag) {
  if(flag == null) {
    flag = 1;
  }
  var dialog = _gel('dialog');
  var value;
  if(flag == 1) {
    value = dialog.alpha + SPEED;
  } else {
    value = dialog.alpha - SPEED;
  }
  dialog.alpha = value;
  dialog.style.opacity = (value / 100);
  dialog.style.filter = 'alpha(opacity=' + value + ')';
  if(value >= 99) {
    clearInterval(dialog.timer);
    dialog.timer = null;
  } else if(value <= 1) {
    dialog.style.visibility = 'hidden';
    _gel('dialog-mask').style.visibility = 'hidden';
    clearInterval(dialog.timer);
  }
}
