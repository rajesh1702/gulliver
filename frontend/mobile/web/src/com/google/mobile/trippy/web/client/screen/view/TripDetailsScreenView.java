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

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.mobile.trippy.web.client.presenter.BaseHeaderPresenter;
import com.google.mobile.trippy.web.client.presenter.DayListPresenter;
import com.google.mobile.trippy.web.client.screen.presenter.TripDetailsScreenPresenter;

/**
 * This Class is responsible to show the TripDetail Screen.
 * 
 * 
 */
public class TripDetailsScreenView extends Composite implements TripDetailsScreenPresenter.Display {

  /** UI Binder. */
  @UiTemplate("ScreenView.ui.xml")
  interface Binder extends UiBinder<Widget, TripDetailsScreenView> {
  }

  @UiField FlowPanel header;
  @UiField FlowPanel body;

  private static Binder uiBinder = GWT.create(Binder.class);

  public TripDetailsScreenView() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  @Override
  public Widget asWidget() {
    return this;
  }

  @Override
  public void setHeader(final BaseHeaderPresenter.Display display) {
    header.clear();
    header.add(display.asWidget());
  }

  @Override
  public void setBody(final DayListPresenter.Display dayList) {
    body.clear();
    body.add(dayList.asWidget());
  }
}
