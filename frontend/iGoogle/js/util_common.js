/**
 * @fileoverview Contains common utility functions.
 * @author
 */

/**
 * Used to show server response for any data store operation.
 * @param {Object} tplData Data object.
 */
function showServerMessage(tplData) {
  var tplHtml = _gel('tpl-server-msg').value;
  tplHtml = Util.supplant(tplHtml, tplData);
  _gel('server-msg').innerHTML = '';
  _gel('server-msg').style.display = 'block';
  timerMsg.createTimerMessage(tplHtml, TIME_OUT, hideTimerMessage);
}

/**
 * Hide the dismissable message after some time is elapsed.
 */
function hideTimerMessage() {
  _gel('server-msg').innerHTML = '';
  _IG_AdjustIFrameHeight();
}

/**
 * Gets date in format like Jun 28, 2010 11:24:29 AM.
 * @param {string} inputDate Date as string in mdy format.
 * @return {string} Date string as in Jun 28, 2010 11:24:29 AM.
 */
function getDateObject(inputDate) {
  inputDate = inputDate.replace(/-/g, '/');
  var startDate = new Date(inputDate);
  var hour = startDate.getHours();
  // Jun 28, 2010 11:24:29 AM
  var secondsStamp = startDate.getSeconds();
  var hourStamp = startDate.getHours();
  var minStamp = startDate.getMinutes();
  // Making hour and minutes as two digit numbers in case they are not.
  hourStamp = hourStamp < 10 ? '0' + hourStamp : hourStamp;
  minStamp = minStamp < 10 ? '0' + minStamp : minStamp;
  var meridian = hourStamp < 12 ? 'AM' : 'PM';
  return DateLib.shortMonthNames[startDate.getMonth()] + ' ' +
    startDate.getDate() + ', ' + startDate.getFullYear() + ' ' +
    hourStamp + ':' + minStamp + ':' + secondsStamp + ' ' + meridian;
}

/**
 * To enable/disable continue button on click of check box of terms
 * and services.
 * @param {boolean} isEnable Flag specifies whether to enable or disable button.
 */
function enableContinueBtn(isEnable) {
  var personalizeRef = _gel('personalize');
  var checkTermsRef = _gel('check_terms_services');
  if (isEnable) {
    personalizeRef.disabled = false;
    checkTermsRef.onclick = function() {
      enableContinueBtn(false);
    };
  } else {
    personalizeRef.disabled = true;
    checkTermsRef.onclick = function() {
      enableContinueBtn(true);
    };
  }
}

/**
 * To show terms and services text.
 * @param {boolean} showTerms Boolean value indicating whether to show terms or
 *     not.
 */
function showTermsText(showTerms) {
  var approvalRef = document.getElementById('approval');
  var termsRef = document.getElementById('terms-services');
  if (showTerms) {
    approvalRef.style.display = 'none';
    termsRef.style.display = '';
    var linkText = '<a id="terms_service_link" href="#" ' +
        'onclick="showLicenseText(true)">' +
        prefs.getMsg('terms_service') + '</a>';
    _gel('terms_services_text').innerHTML = prefs.getMsg('terms_service_text')
        .replace('%TERMS_SERVICES_LINK%', linkText);
  } else {
    approvalRef.style.display = '';
    termsRef.style.display = 'none';
  }
  _IG_AdjustIFrameHeight();
}

/**
 * To show licence text.
 * @param {boolean} showLicense Boolean value indicating whether to show
 *     licence information or not.
 */
function showLicenseText(showLicense) {
  var licenseTextRef = _gel('license-text');
  var licenseTextLinkRef = _gel('terms_service_link');
  if (showLicense) {
    licenseTextRef.style.display = '';
    licenseTextLinkRef.onclick = function() {
      showLicenseText(false);
    };
  } else {
    licenseTextRef.style.display = 'none';
    licenseTextLinkRef.onclick = function() {
      showLicenseText(true);
    };
  }
  _IG_AdjustIFrameHeight();
}

/**
 * Gets formated date in mm-dd-yyyy format.
 * @param {string} inputDate Date as string in mdy format.
 * @return {string} Date string.
 */
function getFormattedDate(inputDate) {
  var startDate = new Date(inputDate);
  var month = startDate.getMonth() + 1;
  return month + '-' + startDate.getDate() + '-' + startDate.getFullYear();
}

// Export
window.showTermsText = showTermsText;
