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
import com.google.mobile.trippy.web.client.presenter.CommentListItemPresenter.Display;
import com.google.mobile.trippy.web.client.view.CommentListItemView;

/**
 * Provider for Comment item Presenter.
 * 
 *
 */
public class CommentListItemPresenterProvider implements Provider<CommentListItemPresenter> {

  private final SingletonComponents singletonComponents;
  
  public CommentListItemPresenterProvider(SingletonComponents singletonComponent){
    this.singletonComponents = singletonComponent;
  }

  @Override
  public CommentListItemPresenter get() {
    Display display = new CommentListItemView();
    return new CommentListItemPresenter(display, singletonComponents);
  }
}