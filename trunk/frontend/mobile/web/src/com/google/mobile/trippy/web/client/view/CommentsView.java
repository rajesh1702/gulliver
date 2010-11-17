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

package com.google.mobile.trippy.web.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.mobile.trippy.web.client.presenter.CommentListItemPresenter;
import com.google.mobile.trippy.web.client.presenter.CommentsPresenter;

/**
 * View for comments screen
 * 
 *
 */
public class CommentsView extends Composite implements CommentsPresenter.Display {

  /**
   * Binder to bind Java and XML code
   */
  @UiTemplate("CommentsView.ui.xml")
  interface Binder extends UiBinder<Widget, CommentsView> {
  }

  @UiField VerticalPanel listPanel;

  private static Binder uiBinder = GWT.create(Binder.class);

  public CommentsView() {
    initWidget(uiBinder.createAndBindUi(this));
  }
  
  @Override
  public Widget asWidget() {
    return this;
  }

  @Override
  public CommentListItemPresenter.Display newCommentListItemView() {
    return new CommentListItemView();
  }

  @Override
  public void addCommentListItem(final CommentListItemPresenter.Display listItemDisplay) {
    listPanel.add(listItemDisplay.asWidget());
  }
  
  @Override
  public void removeCommentListItem(int index) {
    listPanel.remove(index);
  }
  
  @Override
  public void clear() {
    listPanel.clear();
  }
}
