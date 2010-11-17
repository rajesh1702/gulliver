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
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.google.mobile.trippy.web.client.TrippyBundle;
import com.google.mobile.trippy.web.client.i18n.Message;
import com.google.mobile.trippy.web.client.presenter.CommentAddPresenter;

/**
 * This class is responsible to show the comments screen to the user.
 * 
 */
public class CommentAddView extends Composite implements CommentAddPresenter.Display {

  static {
    // Create an inject decorator-specific resources.
    TrippyBundle resources = GWT.create(TrippyBundle.class);
    StyleInjector.inject(resources.commonStyle().getText());
  }

  /**
   * Binder to bind Java and XML code
   */
  @UiTemplate("CommentAddView.ui.xml")
  interface Binder extends UiBinder<Widget, CommentAddView> {
  }

  @UiField
  TextArea commentText;
  @UiField
  Label add;

  private static Binder uiBinder = GWT.create(Binder.class);

  public CommentAddView() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  @Override
  public HasClickHandlers getAddButton() {
    return add;
  }

  @Override
  public String getCommentText() {
    return commentText.getText();
  }

  @Override
  public Widget asWidget() {
    return this;
  }

  @Override
  public void setAddButtonVisible(final boolean visible) {
    add.setVisible(visible);
  }

  @Override
  public void setTextAreaVisible(final boolean visible) {
    commentText.setVisible(visible);
  }

  @Override
  public void resetCommentText() {
    commentText.setText("");
  }
}
