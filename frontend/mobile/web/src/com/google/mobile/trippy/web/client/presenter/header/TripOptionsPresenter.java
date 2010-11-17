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

package com.google.mobile.trippy.web.client.presenter.header;

import com.google.common.base.Preconditions;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.mobile.trippy.web.client.TrippyBundle;
import com.google.mobile.trippy.web.client.base.Constants;
import com.google.mobile.trippy.web.client.base.Provider;
import com.google.mobile.trippy.web.client.base.SingletonComponents;
import com.google.mobile.trippy.web.client.event.SearchClickEvent;
import com.google.mobile.trippy.web.client.event.SearchClickEventHandler;
import com.google.mobile.trippy.web.client.event.ShareTripEvent;
import com.google.mobile.trippy.web.client.event.ShowHomePageEvent;
import com.google.mobile.trippy.web.client.event.ShowTripOnMapEvent;
import com.google.mobile.trippy.web.client.event.ShowTripScheduleEvent;
import com.google.mobile.trippy.web.client.presenter.MenuPresenter;
import com.google.mobile.trippy.web.client.presenter.SearchBarPresenter;
import com.google.mobile.trippy.web.client.presenter.TripEditPopupPresenter;
import com.google.mobile.trippy.web.client.widget.Toast;
import com.google.mobile.trippy.web.shared.exception.AuthorizationException;
import com.google.mobile.trippy.web.shared.models.Trip;

/**
 * Presenter for options header for screens showing trip data.
 * 
 */
public class TripOptionsPresenter extends MenuOptionsPresenter {

  private final SearchBarPresenter searchPresenter;
  private final TripEditPopupPresenter tripEditPopupPresenter;
  private Trip trip;
  private int tripDay;

  public TripOptionsPresenter(final MenuOptionsPresenter.Display display,
      final SingletonComponents singletonComponents,
      final Provider<SearchBarPresenter> searchProvider,
      final Provider<TripEditPopupPresenter> tripEditProvider,
      final Provider<MenuPresenter> menuProvider) {
    super(display);
    super.singletonComponents = singletonComponents;
    this.searchPresenter = searchProvider.get();
    this.tripEditPopupPresenter = tripEditProvider.get();
    super.menuPresenter = menuProvider.get();
  }

  public void setTrip(final Trip trip, final int day) {
    this.trip = trip;
    this.tripDay = day;
    searchPresenter.setTrip(trip, tripDay, null);
    tripEditPopupPresenter.setTrip(trip);

    setSearchAction(new Runnable() {
      @Override
      public void run() {
        doSearch();
      }
    });
    
    addMenu();
  }
  
  /**
   * Handle user events on header.
   * 
   * Events Fired: None 
   * 
   * Events listened: SearchClickEvent, it is fired by clicking on native 
   * search button.
   */
  @Override
  public void bind() {
    bind(true);
  }
  
  /**
   * Handle user events on header.
   * 
   * Events Fired: None 
   * 
   * Events listened: SearchClickEvent, it is fired by clicking on native 
   * search button if useHardwareKey
   */
  public void bind(boolean useHardwareKey) {
    super.bind();
    searchPresenter.bind();
    tripEditPopupPresenter.bind();
    
    if (useHardwareKey) {
      // For hardware search action.
      HANDLERS.add(singletonComponents.getEventBus().addHandler(SearchClickEvent.getType(),
          new SearchClickEventHandler() {
            @Override
            public void onSearch(SearchClickEvent event) {
              doSearch();
            }
          }));
    }
  }

  @Override
  public void release() {
    super.release();
    searchPresenter.release();
    tripEditPopupPresenter.release();
  }

  @Override
  public void addMenu() {
    Preconditions.checkNotNull(trip);
    menuPresenter.clear();

    if (isMapScreen()) {
      menuPresenter.addMenuItem(Constants.SHOW_IN_LIST_STR, TrippyBundle.INSTANCE.listIcon(),
          new Runnable() {
            @Override
            public void run() {
              singletonComponents.getEventBus().fireEvent(new ShowTripScheduleEvent(trip));
            }
          });
    } else {
      menuPresenter.addMenuItem(Constants.SHOW_ON_MAP_STR, TrippyBundle.INSTANCE.iconShowOnMap(),
          new Runnable() {
            @Override
            public void run() {
              singletonComponents.getEventBus().fireEvent(
                  new ShowTripOnMapEvent(trip, tripDay, null/*selected trip Item key*/));
            }
          });
    }
    
    final boolean isContributor =
        singletonComponents.getUserUtils().isContributor(trip,
            singletonComponents.getUtils().getUserEmail());
    final boolean isOwner =
        singletonComponents.getUserUtils().isOwner(trip,
            singletonComponents.getUtils().getUserEmail());
    menuPresenter.addMenuItem(Constants.DELETE_STR, TrippyBundle.INSTANCE.trash(), !isOwner,
        new Runnable() {
          @Override
          public void run() {
            if (isOwner) {
              if (Window
                  .confirm(singletonComponents.getMessage().confirmTripDelete(trip.getName()))) {
                final Toast toast = singletonComponents.getToast();
                try {
                  toast.showLoading(singletonComponents.getMessage().deleting(trip.getName()));
                  singletonComponents.getTripService().deleteTrip(trip, new AsyncCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                      toast.hideLoading();
                      toast.showToast(singletonComponents.getMessage().tripDeleted());
                      singletonComponents.getEventBus().fireEvent(new ShowHomePageEvent());
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                      toast.hideLoading();
                      if (caught instanceof AuthorizationException) {
                        toast.showToast(singletonComponents.getMessage().unauthorizedDelete(
                            Constants.TRIP_STR));
                      } else {
                        toast.showToast(singletonComponents.getMessage().tripDeletionFailed());
                      }
                    }
                  });
                } catch (AuthorizationException e) {
                  toast.hideLoading();
                  toast.showToast(singletonComponents.getMessage().tripDeletionFailed());
                }
              }
            } else {
              singletonComponents.getToast().showToast(
                  singletonComponents.getMessage().unauthorizedDelete(Constants.SHARED_TRIP_STR));
            }
          }
        });

    // "Share" trip item
    menuPresenter.addMenuItem(Constants.INVITE, TrippyBundle.INSTANCE.shareIcon(), !isContributor,
        new Runnable() {
          @Override
          public void run() {
            if (isContributor) {
              singletonComponents.getEventBus().fireEvent(new ShareTripEvent(trip));
            } else {
              singletonComponents.getToast().showToast(
                  singletonComponents.getMessage().unauthorizedShare());
            }
          }
        });

    // "Edit" trip item
    menuPresenter.addMenuItem(Constants.EDIT_STR, TrippyBundle.INSTANCE.editIcon(), !isContributor,
        new Runnable() {

          @Override
          public void run() {
            if (isContributor) {
              tripEditPopupPresenter.showPopup();
            } else {
              singletonComponents.getToast().showToast(
                  singletonComponents.getMessage().unauthorizedEdit(Constants.SHARED_TRIP_STR));
            }
          }
        });
    menuPresenter.setPopupTitle(Constants.TRIP_MENU_STR);
    super.addMenu();
  }

  public void showEditPopUp(final boolean visible) {
    if (visible) {
      tripEditPopupPresenter.showPopup();
    }
  }

  private void doSearch() {
    if (searchClickListener != null) {
      searchClickListener.onSearchClick(searchPresenter);
    }
    searchPresenter.clearDisplay();
    searchPresenter.showPopup();
  }
}
