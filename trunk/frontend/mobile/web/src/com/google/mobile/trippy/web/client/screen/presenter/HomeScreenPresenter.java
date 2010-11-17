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

package com.google.mobile.trippy.web.client.screen.presenter;

import com.google.gwt.event.shared.HandlerManager;
import com.google.mobile.trippy.web.client.base.Provider;
import com.google.mobile.trippy.web.client.presenter.EventHandlerPresenter;
import com.google.mobile.trippy.web.client.presenter.FooterPresenter;
import com.google.mobile.trippy.web.client.presenter.HomeHeaderPresenter;
import com.google.mobile.trippy.web.client.presenter.HomePanelPresenter;
import com.google.mobile.trippy.web.client.view.View;
import com.google.mobile.trippy.web.shared.models.Trip;

import java.util.List;

/**
 * This class is responsible to show Home Screen which integrates the home page.
 * 
 * 
 */
public class HomeScreenPresenter implements EventHandlerPresenter<HomeScreenPresenter.Display> {

  /**
   * Interface to view the create trip screen.
   */
  public static interface Display extends View {
    void setHeader(final HomeHeaderPresenter.Display diplay);

    void setBody(final HomePanelPresenter.Display display);
    
    void setFooter(final FooterPresenter.Display display);
  }

  private Display display;
  private final HomeHeaderPresenter homeHeaderPresenter;
  private final HomePanelPresenter homePanelPresenter;
  private final FooterPresenter footerPresenter;

  public HomeScreenPresenter(Display display,
      final Provider<HomeHeaderPresenter> tripListHeaderProvider,
      final Provider<HomePanelPresenter> homePanelPresenterProvider,
      final Provider<FooterPresenter> footerPresenterProvider) {
    this.display = display;
    this.homeHeaderPresenter = tripListHeaderProvider.get();
    this.homePanelPresenter = homePanelPresenterProvider.get();
    this.footerPresenter = footerPresenterProvider.get();
  }

  @Override
  public void bind() {
    homeHeaderPresenter.bind();
    homePanelPresenter.bind();
    footerPresenter.bind();
  }

  /**
   * Set the trip list to show the up coming trip list.
   * 
   */
  public void setTrips(List<Trip> trips) {
    homePanelPresenter.setTrips(trips);
    populateView();
  }

  @Override
  public HandlerManager getEventBus() {
    return homePanelPresenter.getEventBus();
  }

  @Override
  public void release() {
    homeHeaderPresenter.release();
    homePanelPresenter.release();
    footerPresenter.release();
  }

  @Override
  public Display getDisplay() {
    return display;
  }

  /**
   * Show the view for home Panel.
   */
  private void populateView() {
    homeHeaderPresenter.populateView();
    display.setHeader(homeHeaderPresenter.getDisplay());
    display.setBody(homePanelPresenter.getDisplay());
    display.setFooter(footerPresenter.getDisplay());
  }
}
