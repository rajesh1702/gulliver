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
package com.lonelyplanet.trippy.android.framework;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AccountsException;
import android.app.Activity;
import android.os.Bundle;

import java.io.IOException;

/**
 * Sets up authority token for appengine.  Should be run in UI thread.
 */
public class AppEngineAccountManager {
  /**
   * Url used if automatic login fails.
   */
  public static final String LOGIN_URL = "https://www.google.com/accounts/ServiceLogin";
  /**
   * Url used to logout.
   */
  public static final String LOGOUT_URL = "https://www.google.com/accounts/Logout";
  private static final String ACCOUNT_TYPE = "com.google";
  private static final String TOKEN_TYPE = "ah";

  private AppEngineAccountManager() {}

  /**
   * Listener for AuthToken
   */
  public interface AppEngineAuthTokenListener {
    /**
     * Called when authtoken is ready.
     * @param authToken the authtoken.
     */
    void onAuthTokenReady(String authToken);
  }

  /**
   * Request authToken.
   * @param activity activity for AccountManager
   * @param listener listener to inform when authtoken is ready
   */
  private static void getAuthToken(final Activity activity,
                                   final AppEngineAuthTokenListener listener) {
    final AccountManager mgr = AccountManager.get(activity);
    final Account[] accts = mgr.getAccountsByType(ACCOUNT_TYPE);
    if (0 == accts.length) {
      listener.onAuthTokenReady(null);
      return;
    }
    Account account = accts[0];

    mgr.getAuthToken(account, TOKEN_TYPE, null, activity,
        new AccountManagerCallback<Bundle>() {
          public void run(AccountManagerFuture<Bundle> mgrFuture) {
            try {
              Bundle bundle = mgrFuture.getResult();
              String authToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
              listener.onAuthTokenReady(authToken);
            } catch (AccountsException e) {
              listener.onAuthTokenReady(null);
            } catch (IOException e) {
              listener.onAuthTokenReady(null);
            }
          }
        }, null);
  }

  /**
   * Request new authToken.
   * @param activity activity for AccountManager
   * @param listener listener to inform when authtoken is ready
   */
  public static void getNewAuthToken(final Activity activity,
                                     final AppEngineAuthTokenListener listener) {
    getAuthToken(activity, new AppEngineAuthTokenListener() {
      public void onAuthTokenReady(String authToken) {
        if (null != authToken) {
          AccountManager mgr = AccountManager.get(activity);
          mgr.invalidateAuthToken(ACCOUNT_TYPE, authToken);
        }
        getAuthToken(activity, listener);
      }
    });
}


}
