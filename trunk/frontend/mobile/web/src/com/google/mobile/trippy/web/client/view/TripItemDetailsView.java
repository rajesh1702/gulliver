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
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Anchor;
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
import com.google.mobile.trippy.web.client.presenter.TripItemPresenter;


/**
 * View to show trip item in detail
 * 
 * 
 */
public class TripItemDetailsView extends Composite implements TripItemPresenter.Display {

  /**
   * Css resources for this view
   */
  public static interface Style extends CssResource {
    String imgPhone();
    String phoneAnchor();
    String txtDecorationNone();
    String reviewTextPara();
  }

  /** UI Binder. */
  @UiTemplate("TripItemDetailsView.ui.xml")
  interface Binder extends UiBinder<Widget, TripItemDetailsView> {
  }
  
  @UiField
  Label lblName;
  @UiField
  Label lblDay;
  @UiField
  Label lblSchedule;
  @UiField
  FlowPanel lblAddress;
  @UiField
  Anchor reschedule;
  @UiField
  Anchor showOnMap;
  @UiField
  Anchor showOnGoogleMap;
  @UiField
  Label lblThumbUpCount;
  @UiField
  Label lblThumbDownCount;
  @UiField
  Anchor anchorMoreInfo;
  @UiField
  Anchor anchorCommentsLink;
  @UiField 
  FlowPanel htmPhones;
  @UiField
  HTMLPanel reviewPanel;
  @UiField
  VerticalPanel reviewList;
  @UiField
  HTML review;
  @UiField Style style;
  @UiField Image commentIcon;
  @UiField Image rescheduleIcon;
  @UiField Image showOnMapIcon;
  @UiField Image imgThumbUpIcon;
  @UiField Anchor showOnGoogleMapIcon;
  @UiField Anchor anchorMoreInfoIcon;

  private static Binder uiBinder = GWT.create(Binder.class);
  private static final Message messages = GWT.create(Message.class);

  private final HasClickHandlers dummyHandler = new HasClickHandlers() {

    @Override
    public void fireEvent(GwtEvent<?> event) {
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
      return null;
    }
  };

  public TripItemDetailsView() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  @Override
  public void setCommentCount(final int count) {
    if (count == 0) {
      anchorCommentsLink.setText(Constants.ADD_COMMENT_STR);
    } else {
      anchorCommentsLink.setText(Constants.COMMENTS_STR + " (" + count + ")");
    }
  }

  @Override
  public void setLocation(final String location, final double lat, final double lng) {
    lblAddress.clear();
    if (location != null && location.trim().length() > 0) {
      lblAddress.setVisible(true);
      final Label lblLocation = new Label(location);
      lblLocation.addStyleName(TrippyBundle.INSTANCE.commonStyle().baseFont());
      lblAddress.add(lblLocation);
    } else {
      lblAddress.setVisible(false);
    }
  }

  @Override
  public void showOnGoogleMapUrl(final String url) {
    showOnGoogleMap.setHref(url);
    showOnGoogleMap.addStyleName(style.txtDecorationNone());
    showOnGoogleMapIcon.setHref(url);
  }

  @Override
  public void setMoreInfoLink(final String href) {
    if (href.contains(Constants.MAP_LINK_LP)) {
      anchorMoreInfo.setText(messages.moreInfoOnLP());
    } else {
      anchorMoreInfo.setText(messages.moreInfoOnGoogle());
    }
    anchorMoreInfo.addStyleName(style.txtDecorationNone());
    anchorMoreInfo.setHref(href);
    anchorMoreInfoIcon.setHref(href);
  }

  @Override
  public void setThumbDownCount(final int count) {
    lblThumbDownCount.setText(Constants.DISLIKE_STR + " (" + count + ")");
  }

  @Override
  public void setThumbUpCount(final int count) {
    lblThumbUpCount.setText(Constants.LIKE_STR + " (" + count + ")");
  }

  @Override
  public Widget asWidget() {
    return this;
  }

  @Override
  public HasClickHandlers getRescheduleButton() {
    return reschedule;
  }
  
  @Override
  public HasClickHandlers getShowOnMapButton() {
    return showOnMap;
  }
  
  @Override
  public HasClickHandlers getCommentButton() {
    return anchorCommentsLink;
  }

  @Override
  public HasClickHandlers getShowDetails() {
    return dummyHandler;
  }
  
  @Override
  public HasClickHandlers getDislike() {
    return dummyHandler;//lblThumbDownCount;
  }

  @Override
  public HasClickHandlers getLike() {
    return lblThumbUpCount;
  }

  @Override
  public void addPhoneNumber(final String phoneNumber) {
    if (phoneNumber != null && !phoneNumber.isEmpty()) {
      htmPhones.setVisible(true);
      final Anchor phonelink = new Anchor(phoneNumber);
      phonelink.setStyleName(style.phoneAnchor());
      final Image imgPh = new Image();
      imgPh.setUrl(TrippyBundle.INSTANCE.callIcon().getURL());
      imgPh.setStyleName(style.imgPhone());
      phonelink.addStyleName(TrippyBundle.INSTANCE.commonStyle().colorFont());
      final FlowPanel phonePanel = new FlowPanel();
      phonelink.setHref("tel:" + phoneNumber);
      phonePanel.add(imgPh);
      phonePanel.add(phonelink);
      HTML seperator = new HTML();
      seperator.setHTML("<br/>");
      phonePanel.add(seperator);
      htmPhones.add(phonePanel);
    } else {
      htmPhones.setVisible(false);
    }
  }
  
  @Override
  public void clear() {
    htmPhones.clear();
  }
  
  @Override
  public void setDay(final String dateStr) {
    lblDay.setText(dateStr);
  }

  @Override
  public void setSchedule(final String scheduleStr) {
    lblSchedule.setText(scheduleStr);
  }

  public void setReview(String html) {
    if (html == null || html.trim().length() == 0) {
      review.setHTML("");
      reviewList.clear();
      reviewPanel.setVisible(false);
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
  public void setName(final String itemName) {
    lblName.setText(itemName);
  }

  @Override
  public HasClickHandlers getCommentIcon() {
    return commentIcon;
  }

  @Override
  public HasClickHandlers getRescheduleIcon() {
    return rescheduleIcon;
  }

  @Override
  public HasClickHandlers getShowOnMapIcon() {
    return showOnMapIcon;
  }

  @Override
  public HasClickHandlers getLikeIcon() {
    return imgThumbUpIcon;
  }
  
  @Override
  public HasClickHandlers getOptions() {
    return dummyHandler;
  }
}
