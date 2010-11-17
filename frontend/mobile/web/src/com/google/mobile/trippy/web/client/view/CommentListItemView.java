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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowResizeListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.mobile.trippy.web.client.TrippyBundle;
import com.google.mobile.trippy.web.client.presenter.CommentListItemPresenter;

/**
 * Display one comment
 * 
 */
@SuppressWarnings("deprecation")
public class CommentListItemView extends Composite implements CommentListItemPresenter.Display {

  static {
    // Create an inject decorator-specific resources.
    TrippyBundle resources = GWT.create(TrippyBundle.class);
    StyleInjector.inject(resources.commonStyle().getText());
  }

  /**
   * Binder to bind Java and XML code
   */
  @UiTemplate("CommentListItemView.ui.xml")
  interface Binder extends UiBinder<Widget, CommentListItemView> {
  }

  @UiField Label description;
  @UiField Label user;
  @UiField Label date;
  @UiField Image delete;

  private static Binder uiBinder = GWT.create(Binder.class);

  public CommentListItemView() {
    initWidget(uiBinder.createAndBindUi(this));
    Window.addWindowResizeListener(new WindowResizeListener() {

      @Override
      public void onWindowResized(int width, int height) {
        int txtTitleWidth = width  - 100;
        user.setWidth(txtTitleWidth + "px");
        date.setWidth(60 + "px");
      }
    });
  }

  @Override
  public HasClickHandlers getDeleteButton() {
    return delete;
  }

  @Override
  public void setDeleteButtonVisible(final boolean visible) {
    delete.setVisible(visible);
  }

  @Override
  public void setCommentText(final String text) {
    description.setText(text);
  }

  @Override
  public void setDate(final String dateString) {
    date.setText(dateString);
  }

  @Override
  public void setUser(final String userString) {
    user.setText(userString);
  }

  @Override
  public Widget asWidget() {
    return this;
  }

  @Override
  protected void onAttach() {
    super.onAttach();
    final int windowWidth = this.getOffsetWidth();
    final int txtTitleWidth = windowWidth - 100;
    user.setWidth(txtTitleWidth + "px");
    date.setWidth(60 + "px");
  }
}
