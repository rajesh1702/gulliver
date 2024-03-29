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

package com.google.mobile.trippy.web.client.screen.view;

import com.google.common.base.Preconditions;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.mobile.trippy.web.client.db.LocalDbService;
import com.google.mobile.trippy.web.client.presenter.FooterPresenter.Display;

/**
 *
 */
public class FooterView extends Composite implements Display {

  /** UI Binder. */
  @UiTemplate("FooterView.ui.xml")
  interface Binder extends UiBinder<Widget, FooterView> {}
  
  @UiField Label userName;
  @UiField Anchor logoutLink;

  public FooterView() {
    initWidget(uiBinder.createAndBindUi(this));
    logoutLink.addClickHandler(new ClickHandler() {
          @Override
          public void onClick(ClickEvent event) {
            LocalDbService.clearDb();
          }
    });
  }
  
  /** UIBinder instance. */
  private static Binder uiBinder = GWT.create(Binder.class);
  
  @Override
  public void setLogoutLink(String link) {
    Preconditions.checkNotNull(link);
    logoutLink.setHref(link);    
  }

  @Override
  public void setUserName(String title) {
    userName.setText(Preconditions.checkNotNull(title));
  }

  @Override
  public Widget asWidget() {
    return this;
  }

}
