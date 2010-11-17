/*
 * Copyright 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lonelyplanet.trippy.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;
import android.webkit.WebView;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.lonelyplanet.trippy.android.framework.AppEngineAccountManager;
import com.lonelyplanet.trippy.android.framework.AppEngineAccountManager.AppEngineAuthTokenListener;
import com.lonelyplanet.trippy.android.framework.TrippyWebViewInitializer;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Android-specific shell for Trippy webapp.
 */
public class TrippyActivity extends Activity implements AppEngineAuthTokenListener {
  /**
   * URL to load for Trippy web.
   */
  public static final String URL = "http://trippy-lp.appspot.com/";

  /**
   * URL to load on start.
   */
  public static final String SPLASHSCREEN_URL = "file:///android_asset/splash.html";

  /**
   * URL to load on failures.
   */
  public static final String OFFLINE_URL = "file:///android_asset/offline.html";

  /**
   * User Agent (prefixed to webkit default) used for Trippy web.
   */
  private static final String USER_AGENT = "trippyandroid";

  /**
   * Name of JS Interface
   */
  private static final String JS_INTERFACE_NAME = "ANDROID";

  /**
   * Directory on sdcard where photos are saved.
   */
  private static final String TRIPPY_PHOTO_DIR = "DCIM/Camera/Trippy/";
  private static final String DATE_FORMAT = "yyyy-MM-dd HH.mm.ss";
  private static final String LOG_TAG = "Trippy";
  private static final int DIALOG_PHOTOS = 1;
  private static final int DIALOG_NAVIGATE = 2;
  private static final String STATE_LAST_TRIPID = "tripId";
  private static final String STATE_LAST_SAVED_FILE = "savedFile";

  //////////////////////
  // Browser Activity for Web App
  //////////////////////

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    Log.d(LOG_TAG, "new intent " + intent);
    String dataString = intent.getDataString();
    if (null != dataString && dataString.startsWith(URL)) {
      targetUrl = dataString;
    }
  }

  @Override
  public void onCreate(Bundle icicle) {
    Log.d(LOG_TAG, "Create Trippy");
    super.onCreate(icicle);
    getWindow().requestFeature(Window.FEATURE_PROGRESS);
    setContentView(R.layout.main);
    setProgressBarVisibility(true);
    webview = (WebView) findViewById(R.id.webview);
    TrippyWebViewInitializer.initialize(this, LOG_TAG, USER_AGENT, webview);
    gestureDetector = new GestureDetector(
        this, TrippyWebViewInitializer.getGestureListener(webview, LOG_TAG));

    tripItems = new TripItemsDB(this);
    tripItems.open();
    canNavigate = getPackageManager().resolveActivity(new Intent(Intent.ACTION_VIEW,
        Uri.parse("google.navigation:")), 0) != null;
    canTakePicture = getPackageManager().resolveActivity(new Intent(
        MediaStore.ACTION_IMAGE_CAPTURE), 0) != null;
    Log.d(LOG_TAG, "Can Navigate: " + canNavigate + " Can Take Picture: " + canTakePicture);

    // Using addJavascriptInterface() allows JavaScript to control application.
    // This can be a very useful feature or a dangerous security issue.
    // When the HTML in the WebView is untrustworthy (for example, part or all
    // of the HTML is provided by some person or process), then an attacker
    // could inject HTML that will execute your code and possibly any code of
    // the attacker's choosing.
    proxy = new AndroidProxy(this, new Handler(), tripItems, canNavigate, canTakePicture);
    webview.addJavascriptInterface(proxy, JS_INTERFACE_NAME);

    photos = new PhotosDB(this);
    photos.open();

    if (null != icicle) {
      lastTripId = icicle.getString(STATE_LAST_TRIPID);
      String savedPath = icicle.getString(STATE_LAST_SAVED_FILE);
      if (null != savedPath) {
        File file = new File(savedPath);
        if (file.canRead()) {
          lastSavedFile = file;
          storePhoto();
        }
      }
    }
    Toast.makeText(this, R.string.loading, Toast.LENGTH_LONG).show();
  }

  @Override
  protected void onResume() {
    Log.d(LOG_TAG, "Resume Trippy");
    super.onResume();
    if (!targetUrl.equals(webview.getUrl())) {
      AppEngineAccountManager.getNewAuthToken(this, this);
    }
  }

  public void onAuthTokenReady(String authToken) {
    // Load Trippy once AuthToken is ready.
    if (authToken == null) {
      webview.loadUrl(targetUrl);
    } else {
      Log.d(LOG_TAG, "Loading " + targetUrl);
      webview.loadUrl(URL + "/_ah/login?continue=" +
                      URLEncoder.encode(targetUrl) + "&auth=" + authToken);
    }
  }

  private boolean isSavableUrl(String url) {
    return null != url && !url.equals(SPLASHSCREEN_URL) && !url.equals(OFFLINE_URL);
  }

  @Override
  protected void onPause() {
    Log.d(LOG_TAG, "Pause Trippy");
    super.onPause();
    String url = webview.getUrl();
    if (isSavableUrl(url)) {
      targetUrl = url;
    }
  }

  @Override
  protected void onSaveInstanceState(Bundle icicle) {
    Log.d(LOG_TAG, "Save Instance Trippy");
    super.onSaveInstanceState(icicle);
    icicle.putString(STATE_LAST_TRIPID, lastTripId);
    if (null != lastSavedFile) {
      icicle.putString(STATE_LAST_SAVED_FILE, lastSavedFile.getPath());
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    Log.d(LOG_TAG, "Destroy Trippy");
  }

  private String getTripId() {
    String url = webview.getUrl();
    if (url != null) {
      Matcher m = tripIdPattern.matcher(url);
      if (m.find()) {
        lastTripId = m.group(1);
        return lastTripId;
      }
    }
    lastTripId = null;
    return lastTripId;
  }

  /////////////////////
  // Navigation
  /////////////////////
  /**
   * Launch Google navigation
   * @param name
   * @param address
   * @param latLong
   */
  void navigate(String name, String address, String latLong) {
    Log.d(TrippyActivity.LOG_TAG, "Navigate!");

    String destination;
    String url = "google.navigation:";
    if (0 == latLong.length() || numbersPattern.matcher(address).find()) {
      destination = "" + name + ", " + address;
      url += "q=" + URLEncoder.encode(destination);
      if (0 != latLong.length()) {
        url += "&sll=" + URLEncoder.encode(latLong);
      }
    } else {
      destination = latLong;
      url += "q=" + URLEncoder.encode(destination);
    }
    Log.d(LOG_TAG, "URL: " + url);
    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    startActivity(intent);
  }

  //////////////////////////
  // Photos
  /////////////////////////
  /**
   * Launch Camera
   */
  void takePhoto() {
    Log.d(TrippyActivity.LOG_TAG, "TakePhotos!");
    Uri uri;
    Date d = new Date();
    DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

    File saveDir = getSaveDir();
    lastSavedFile = new File(saveDir, dateFormat.format(d) + ".jpeg");
    uri = Uri.fromFile(lastSavedFile);
    Intent send = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

    send.putExtra(MediaStore.EXTRA_OUTPUT, uri);
    startActivityForResult(send, 0);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data){
    Log.d(TrippyActivity.LOG_TAG, "intent data" + data);
    if (RESULT_OK == resultCode) {
      showDialog(DIALOG_PHOTOS);
    }
  }

  private void storePhoto(String tripItemName, String tripItemId) {
    ContentValues values = new ContentValues(5);
    values.put(Images.Media.TITLE, tripItemName);
    values.put(Images.Media.DISPLAY_NAME, tripItemName);
    values.put(Images.Media.DATE_TAKEN, System.currentTimeMillis());
    values.put(Images.Media.MIME_TYPE, "image/jpeg");
    values.put(Images.Media.DATA, lastSavedFile.getPath());
    LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(
        new Criteria(), true));
    if (null != location) {
      try {
        ExifInterface exif = new ExifInterface(lastSavedFile.getPath());
        double lat = location.getLatitude();
        if (lat < 0) {
          exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "S");
          lat = -lat;
        } else {
          exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "N");
        }
        exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE,
            formatLatLongString(lat));
        double lon = location.getLongitude();
        if (lon < 0) {
          exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "W");
          lon = -lon;
        } else {
          exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "E");
        }
        exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE,
            formatLatLongString(lon));
        exif.saveAttributes();
      } catch (IOException e) {
        // Do not store exif on error
      }
      values.put(Images.Media.LATITUDE, location.getLatitude());
      values.put(Images.Media.LONGITUDE, location.getLongitude());
    }
    Uri uri = getContentResolver().insert(Images.Media.EXTERNAL_CONTENT_URI, values);
    photos.addPhotos(lastTripId, tripItemId, uri, lastSavedFile);
    lastSavedFile = null;
  }

  private void storePhoto() {
    String tripItemName = lastSavedFile.getName().substring(0, DATE_FORMAT.length());
    storePhoto(tripItemName, TripItemsDB.NULL_TRIP_ID);
  }

  private static String formatLatLongString(double d) {
    StringBuilder b = new StringBuilder();
    b.append((int) d);
    b.append("/1,");
    d = (d - (int) d) * 60;
    b.append((int) d);
    b.append("/1,");
    d = (d - (int) d) * 60000;
    b.append((int) d);
    b.append("/1000");
    return b.toString();
  }

  /**
   * Launch Activity to Share Photos
   */
  void sharePhotos() {
    Log.d(TrippyActivity.LOG_TAG, "Share!");
    Intent send = new Intent(Intent.ACTION_SEND_MULTIPLE);
    send.setType("image/*");
    ArrayList<Parcelable> list = photos.getPhotoUris(lastTripId);
    send.putParcelableArrayListExtra(Intent.EXTRA_STREAM, list);
    startActivity(send);
  }

  private File getSaveDir() {
    File saveDir = new File(Environment.getExternalStorageDirectory(), TRIPPY_PHOTO_DIR);
    Log.d(TrippyActivity.LOG_TAG, "saveDir: " + saveDir);

    if (!saveDir.isDirectory() && !saveDir.mkdirs()) {
      saveDir = null;
      throw new IllegalStateException("couldn't mkdirs ");
    }
    return saveDir;
  }

  //////////////////////
  // Dialogs and Menu
  //////////////////////
  @Override
  protected Dialog onCreateDialog (int id) {
    final ListAdapter listAdapter = tripItems.getListAdapter();
    switch(id) {
    case DIALOG_PHOTOS: {
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setTitle(R.string.photo_dialog);
      builder.setAdapter(listAdapter, new OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
          Cursor cursor = (Cursor) listAdapter.getItem(which);
          String tripItemName = cursor.getString(TripItemsDB.VIEW_TRIP_ITEMS_INDEX_NAME);
          String tripItemId = cursor.getString(TripItemsDB.VIEW_TRIP_ITEMS_INDEX_TRIP_ITEM_ID);
          storePhoto(tripItemName, tripItemId);
        }
      });
      builder.setOnCancelListener(new OnCancelListener() {
        public void onCancel(DialogInterface arg0) {
          storePhoto();
        }
      });
      return builder.create();
    }
    case DIALOG_NAVIGATE: {
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setTitle(R.string.navigate_dialog);
      builder.setAdapter(listAdapter, new OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
          Cursor cursor = (Cursor) listAdapter.getItem(which);
          String name = cursor.getString(TripItemsDB.VIEW_TRIP_ITEMS_INDEX_NAME);
          String location = cursor.getString(TripItemsDB.VIEW_TRIP_ITEMS_INDEX_LOCATION);
          String latLong = cursor.getString(TripItemsDB.VIEW_TRIP_ITEMS_INDEX_LAT_LONG);
          navigate(name, location, latLong);
        }
      });
      return builder.create();
    }
    }
    return null;
  }

  @Override
  protected void onPrepareDialog(int id, Dialog dialog) {
    switch(id) {
    case DIALOG_PHOTOS:
    case DIALOG_NAVIGATE:
      tripItems.updateAdapter(lastTripId);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    super.onPrepareOptionsMenu(menu);
    String tripId = getTripId();
    menu.findItem(R.id.menu_photos).setEnabled(canTakePicture && tripId != null &&
        android.os.Environment.getExternalStorageState().equals(
            android.os.Environment.MEDIA_MOUNTED));
    menu.findItem(R.id.menu_share).setEnabled(tripId != null &&
        android.os.Environment.getExternalStorageState().equals(
            android.os.Environment.MEDIA_MOUNTED) &&
        photos.hasPhotos(tripId));
    menu.findItem(R.id.menu_nav).setEnabled(canNavigate && tripId != null);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    case R.id.menu_refresh:
      targetUrl = URL;
      AppEngineAccountManager.getNewAuthToken(this, this);
      return true;
    case R.id.menu_photos:
      takePhoto();
      return true;
    case R.id.menu_share:
      sharePhotos();
      return true;
    case R.id.menu_nav:
      showDialog(DIALOG_NAVIGATE);
      return true;
    }
    return false;
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent e){
    gestureDetector.onTouchEvent(e);
    return super.dispatchTouchEvent(e);
  }

  /**
   * Pattern for determining tripId from WebApp url
   */
  private final Pattern tripIdPattern = Pattern.compile("[?&]t=([^&]*)");

  /**
   * Pattern for finding numbers in address
   */
  private final Pattern numbersPattern = Pattern.compile("\\d");
  /**
   * Last photo not saved to Media.Images and Trippy photos db.
   */
  private File lastSavedFile = null;
  /**
   * TripId used to take last photo.
   */
  private String lastTripId = null;

  /**
   * Url to load in onResume.  Set in onCreate, onPause, and onNewIntent.
   */
  private String targetUrl = URL;

  private AndroidProxy proxy;
  private WebView webview;
  private GestureDetector gestureDetector;
  private boolean canNavigate;
  private boolean canTakePicture;
  private PhotosDB photos;
  private TripItemsDB tripItems;
}
