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

package com.google.mobile.trippy.web.client.widget;

import com.google.common.base.Preconditions;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.maps.client.HasMap;
import com.google.gwt.maps.client.base.HasLatLng;
import com.google.gwt.maps.client.base.HasPoint;
import com.google.gwt.maps.client.event.Event;
import com.google.gwt.maps.client.event.EventCallback;
import com.google.gwt.maps.client.overlay.OverlayView;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.mobile.trippy.web.client.TrippyBundle;
import com.google.mobile.trippy.web.client.base.NullMap;

/**
 * View for map info window
 * 
 * 
 */
public class InfoWindowView extends OverlayView implements InfoWindowPresenter.Display {

  static {
    // Create an inject decorator-specific resources.
    TrippyBundle resources = GWT.create(TrippyBundle.class);
    StyleInjector.inject(resources.commonStyle().getText());
  }

  /** Style for this view. */
  public static interface ContentStyle extends CssResource {
    String contentPanel();
    String noWhiteSpce();
  }
  
  /** Ui Binder. */
  @UiTemplate("InfoWindowView.ui.xml")
  interface Binder extends UiBinder<Widget, InfoWindowView> {

  }
  
  @UiField
  ContentStyle style;
  private static Binder uiBinder = GWT.create(Binder.class);
  private final Label infoWindowContent;
  private final HasMap map;
  private HasLatLng position;
  
  public InfoWindowView(HasMap map) {

    uiBinder.createAndBindUi(this);
    this.map = map;
    
    infoWindowContent = new Label() {
      private HandlerRegistration clickHandler;

      @Override
      public HandlerRegistration addClickHandler(ClickHandler handler) {
        if (clickHandler != null) {
          clickHandler.removeHandler();
        }
        clickHandler = super.addClickHandler(handler);
        return clickHandler;
      }
    };
    infoWindowContent.addStyleName(style.noWhiteSpce());
  }

  
  @Override
  public Widget asWidget() {
    return null;
  }

  @Override
  public void showPopup(final HasLatLng position) {
    Preconditions.checkNotNull(position);
    this.position = position;
    setMap(map);
  }

  @Override
  public void hidePopup() {
    setMap(new NullMap());
  }

  @Override
  public void setContent(String content) {
    infoWindowContent.setText(content);
  }

  @Override
  public String getContent() {
    return infoWindowContent.getText();
  }
 
  @Override
  public void draw() {
    
    HasPoint point = getProjection().fromLatLngToDivPixel(position);
    
    Style contentStyle = infoWindowContent.getElement().getStyle();
    contentStyle.setPosition(Position.ABSOLUTE);
    int mapWidth = getMap().getDiv().getClientWidth();
    
    int infoWidth = infoWindowContent.getOffsetWidth();
    int pos = (int) (point.getX() - (infoWidth / 2));
    int shift = infoWidth - (getMap().getDiv().getAbsoluteRight() - pos);
//    if (shift > 0) {
//      pos -= shift;
//    }
    contentStyle.setLeft(pos, Unit.PX);
    contentStyle.setTop(point.getY() - 70, Unit.PX);
  }

  @Override
  public void onAdd() {
    getPanes().getFloatPane().appendChild(infoWindowContent.getElement());
    infoWindowContent.setVisible(true);
    Event.addDomListener(infoWindowContent.getElement(), "click", new EventCallback() {
      
      @Override
      public void callback() {
        Event.trigger(InfoWindowView.this, "click");
      }
    });
  }

  @Override
  public void onRemove() {
//    infoWindowContent.removeFromParent();
    infoWindowContent.setVisible(false);
    
  }
  
  @Override
  public void setClickHandler(EventCallback callback) {
    Event.clearListeners(this, "click");
    Event.addListener(this, "click", callback);
  }
}
