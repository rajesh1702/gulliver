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
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.google.mobile.trippy.web.client.TrippyBundle;
import com.google.mobile.trippy.web.client.presenter.TripSharePresenter.Display;


/**
 * This class is responsible to show the view for TripSharePresenter.
 * 
 *
 */
public class TripShareView extends Composite implements Display {

  static {
    // Create an inject decorator-specific resources.
    TrippyBundle resources = GWT.create(TrippyBundle.class);
    StyleInjector.inject(resources.commonStyle().getText());
  }

  /**
   * Css resources for this view
   */
  public static interface Style extends CssResource {
    String buttonPressed();
    String shareIdCss();
  }

  /** UI Binder. */
  @UiTemplate("TripShareView.ui.xml")
  interface Binder extends UiBinder<Widget, TripShareView> {
  }

  @UiField Label lblErrorMsg;
  @UiField Label btnSkipTripDetail;
  @UiField Label btnSave;
  @UiField TextArea txtAreaEmailId;
  @UiField Style style;
  @UiField FlowPanel listOfSharedIds;
  @UiField FlowPanel listOfContributorIds;
  @UiField FlowPanel listOfViewerIds;
  @UiField Label titleContributorPanel;
  @UiField Label titleViewerPanel;
  @UiField CheckBox tickCollaborator;

  private static Binder uiBinder = GWT.create(Binder.class);

  @SuppressWarnings("deprecation")
  public TripShareView() {
    initWidget(uiBinder.createAndBindUi(this));
    tickCollaborator.setChecked(true);
  }

  @Override
  public Widget asWidget() {
    return this;
  }

  @Override
  public HasClickHandlers getSave() {
    return btnSave;
  }

  @Override
  public HasClickHandlers getCancel() {
    return btnSkipTripDetail;
  }

  @Override
  public String getEmailIds() {
    return txtAreaEmailId.getText();
  }

  @Override
  public void setEmailIdErrorMsgLabel(final String errorMsg) {
    lblErrorMsg.setText(errorMsg);
  }

  @Override
  public void setEmailTextArea(final String text) {
    txtAreaEmailId.setText(text);
  }

  @Override
  public HasClickHandlers getEmailTxtBox() {
    return txtAreaEmailId;
  }

  @Override
  public void addContributorUserId(final String id) {
    final Label contributorId = new Label(id);
    contributorId.setStyleName(style.shareIdCss());
    listOfContributorIds.add(contributorId);
  }

  @Override
  public void addViewerUserId(final String id) {
    final Label viewerId = new Label(id);
    viewerId.setStyleName(style.shareIdCss());
    listOfViewerIds.add(viewerId);
  }

  @Override
  public void showSharedUserIds() {
    titleContributorPanel.setVisible(false);
    titleViewerPanel.setVisible(false);
    int numberOfViewers = listOfViewerIds.getWidgetCount();
    int numberOfcontributors = listOfContributorIds.getWidgetCount();
    if (numberOfViewers + numberOfcontributors > 0) {
    
      if (numberOfcontributors > 0) {
        titleContributorPanel.setVisible(true);
      }
      
      if (numberOfViewers > 0) {
        titleViewerPanel.setVisible(true);
      }
    }
  } 

  @Override
  public void clear() {
    lblErrorMsg.setText("");
    listOfContributorIds.clear();
    listOfViewerIds.clear();
  }

  @SuppressWarnings("deprecation")
  @Override
  public boolean isChecked() {
    return tickCollaborator.isChecked();
  }
}
