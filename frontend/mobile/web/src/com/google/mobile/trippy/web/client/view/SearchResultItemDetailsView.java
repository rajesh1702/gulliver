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
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.mobile.trippy.web.client.TrippyBundle;
import com.google.mobile.trippy.web.client.base.Constants;
import com.google.mobile.trippy.web.client.base.HtmlStripper;
import com.google.mobile.trippy.web.client.i18n.Message;
import com.google.mobile.trippy.web.client.presenter.SearchResultItemPresenter;

import java.util.ArrayList;

/**
 * Class having panel for showing each search Result Item.
 * 
 */
public class SearchResultItemDetailsView extends Composite implements
    SearchResultItemPresenter.Display {

  /** CSS Style for the view*/
  public static interface Style extends CssResource {
    String iconPhn();
    String phoneAnchor();
    String addressName();
    String txtDecorationNone();
    String iconBase();
    String reviewTextPara();
  }

  /** UI Binder. */
  @UiTemplate("SearchResultItemDetailsView.ui.xml")
  interface Binder extends UiBinder<Widget, SearchResultItemDetailsView> {
  }

  @UiField
  Label lblName;
  @UiField
  FlowPanel lblAddress;
  @UiField
  FlowPanel htmPhones;
  @UiField
  HTMLPanel reviewPanel;
  @UiField
  HTML review;
  @UiField
  VerticalPanel reviewList;
  @UiField
  Anchor showOnMap;
  @UiField
  Anchor anrMoreInfo;
  @UiField
  Anchor btnAddRemove;
  @UiField
  HTMLPanel panelAddRemove;
  @UiField Style style;
  @UiField Image imgAdd;
  @UiField Anchor imgShowOnMap;
  @UiField Anchor anrMoreInfoIcon;
  @UiField HTMLPanel showOnMapPanel;
  @UiField Anchor showOnTrippyMap;
  @UiField Anchor imgShowOnTrippyMap;

  private static Binder uiBinder = GWT.create(Binder.class);
  private static final Message messages = GWT.create(Message.class);

  public SearchResultItemDetailsView() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  @Override
  public HasClickHandlers getShowDetailsButton() {
    return new Button();
  }

  @Override
  public HasClickHandlers getAddButton() {
    return btnAddRemove;
  }
  
  @Override
  public void setName(final String itemName) {
    lblName.setText(itemName);
  }

  @Override
  public void setAddress(final String itemAddress) {
    lblAddress.clear();
    if (itemAddress != null && itemAddress.trim().length() > 0) {
      lblAddress.setVisible(true);
      final Label locationAnchor = new Label(itemAddress);
      locationAnchor.setStyleName(style.addressName());
      locationAnchor.addStyleName(TrippyBundle.INSTANCE.commonStyle().baseFont());
      //TODO: Remove the commented code after discussing the mocks.
//      locationAnchor.setHref(address);
      lblAddress.add(locationAnchor);
    } else {
      lblAddress.add(new Label(""));
    }
  }

  @Override
  public void setPhoneNumbers(final ArrayList<String> phoneNumbers) {
    if (phoneNumbers != null && !phoneNumbers.isEmpty()) {
      htmPhones.setVisible(true);
      htmPhones.clear();
      for (String phoneNumber : phoneNumbers) {
        if (phoneNumber != null) {
          final Image imgPhn = new Image();
          imgPhn.setUrl(TrippyBundle.INSTANCE.callIcon().getURL());
          final Anchor phoneLink = new Anchor(phoneNumber);
          phoneLink.setStyleName(style.phoneAnchor());
          phoneLink.addStyleName(TrippyBundle.INSTANCE.commonStyle().colorFont());
          phoneLink.setHref("tel:" + phoneNumber);
          imgPhn.setStyleName(style.iconPhn());
          final FlowPanel phone = new FlowPanel();
          phone.add(imgPhn);
          phone.add(phoneLink);
          HTML seperator = new HTML();
          seperator.setHTML("<br/>");
          phone.add(seperator);
          htmPhones.add(phone);
        }
      }
    } else {
      htmPhones.setVisible(false);
    }
  }

  @Override
  public void setMoreInfoURL(final String url) {
    if (url.contains(Constants.MAP_LINK_LP)) {
      anrMoreInfo.setText(messages.moreInfoOnLP());
    } else {
      anrMoreInfo.setText(messages.moreInfoOnGoogle());
    }
    anrMoreInfo.addStyleName(style.txtDecorationNone());
    anrMoreInfo.setHref(url);
    anrMoreInfoIcon.setHref(url);
  }

  @Override
  public Widget asWidget() {
    return this;
  }

  @Override
  public void setAddButtonVisible(boolean visible) {
    panelAddRemove.setVisible(visible);
  }
  
  public void setReview(String html) {
    if (html == null || html.trim().length() == 0) {
      review.setHTML("");
      reviewPanel.setVisible(false);
      reviewList.clear();
      return;
    }
    reviewPanel.setVisible(true);
    review.setHTML("<h4>Review</h4>");
    
    reviewList.clear();
    String reviewTextStr = HtmlStripper.stripHtml(html);
    String[] reviewParas = reviewTextStr.split("\n\n");
    
    for (String para : reviewParas) {
      Label reviewPara = new Label(para);
      reviewPara.addStyleName(style.reviewTextPara());
      reviewPara.addStyleName(TrippyBundle.INSTANCE.commonStyle().baseFont());
      reviewList.add(reviewPara);
    }
  }

  @Override
  public void showOnGoogleMapUrl(final String url) {
    showOnMap.setHref(url);
    showOnMapPanel.setVisible(true);
    showOnMap.addStyleName(style.txtDecorationNone());
    imgShowOnMap.setHref(url);
  }

  @Override
  public HasClickHandlers getAddIcon() {
    return imgAdd;
  }

  @Override
  public HasClickHandlers getShowOnMap() {
    return showOnTrippyMap;
  }

  @Override
  public HasClickHandlers getShowOnMapIcon() {
    return imgShowOnTrippyMap;
  }
}
