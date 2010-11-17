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

package com.google.mobile.trippy.web.client.presenter;

import com.google.mobile.trippy.web.client.base.Provider;
import com.google.mobile.trippy.web.client.base.SingletonComponents;
import com.google.mobile.trippy.web.client.view.BaseHeaderView;

/**
 * This class is responsible to provide an instance of BaseHeaderPresenter; 
 * 
 */
public class BaseHeaderProvider implements Provider<BaseHeaderPresenter> {

  private final BaseHeaderPresenter.Display display;
  private final SingletonComponents singletonComponents;
  
  public BaseHeaderProvider(final SingletonComponents singletonComponents) {
    this(null, singletonComponents);
  }
  
  public BaseHeaderProvider(final BaseHeaderPresenter.Display display, 
      final SingletonComponents singletonComponents) {
    this.display = display;
    this.singletonComponents = singletonComponents;
  }
  
  @Override
  public BaseHeaderPresenter get() {
    if (display != null) {
      return new BaseHeaderPresenter(display, singletonComponents);
    } else {
      return new BaseHeaderPresenter(new BaseHeaderView(), singletonComponents);
    }
  }
}
