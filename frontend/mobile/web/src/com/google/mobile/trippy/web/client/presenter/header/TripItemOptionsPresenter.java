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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.mobile.trippy.web.client.TrippyBundle;
import com.google.mobile.trippy.web.client.base.Constants;
import com.google.mobile.trippy.web.client.base.Provider;
import com.google.mobile.trippy.web.client.base.SingletonComponents;
import com.google.mobile.trippy.web.client.event.BackEvent;
import com.google.mobile.trippy.web.client.event.SearchClickEvent;
import com.google.mobile.trippy.web.client.event.SearchClickEventHandler;
import com.google.mobile.trippy.web.client.event.ShareTripEvent;
import com.google.mobile.trippy.web.client.event.ShowTripItemDetailsEvent;
import com.google.mobile.trippy.web.client.event.ShowTripOnMapEvent;
import com.google.mobile.trippy.web.client.presenter.DayPopupPresenter;
import com.google.mobile.trippy.web.client.presenter.MenuPresenter;
import com.google.mobile.trippy.web.client.presenter.SearchBarPresenter;
import com.google.mobile.trippy.web.client.widget.Toast;
import com.google.mobile.trippy.web.shared.exception.AuthorizationException;
import com.google.mobile.trippy.web.shared.models.Trip;
import com.google.mobile.trippy.web.shared.models.TripItem;

import java.util.List;

/**
 * Presenter for showing header options for screens showing trip item data.
 * 
 */
public class TripItemOptionsPresenter extends MenuOptionsPresenter {

  private final SearchBarPresenter searchPresenter;
  private final DayPopupPresenter dayPopupPresenter;
  private TripItem tripItem;

  public TripItemOptionsPresenter(final MenuOptionsPresenter.Display display,
      final SingletonComponents singletonComponents,
      final Provider<SearchBarPresenter> searchProvider,
      final Provider<MenuPresenter> menuProvider, 
      final Provider<DayPopupPresenter> dayPopupProvider) {
    super(display);
    super.singletonComponents = singletonComponents;
    this.searchPresenter = searchProvider.get();
    super.menuPresenter = menuProvider.get();
    this.dayPopupPresenter = dayPopupProvider.get();
  }

  public void setTripItem(final TripItem item) {
    Preconditions.checkNotNull(item);
    tripItem = item;
    final Trip trip = singletonComponents.getTripService().getTrip(tripItem.getTripId());
    searchPresenter.setTrip(trip, tripItem.getStartDay(), null);
    dayPopupPresenter.setDays(trip.getDuration());
    
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
   * search button if useHardwareKey is true.
   */
  public void bind(boolean useHardwareKey) {
    super.bind();
    searchPresenter.bind();
    dayPopupPresenter.bind();

    List<HasClickHandlers> dayHandlers = dayPopupPresenter.getDayClickHandlers();
    for (int day = 0, size = dayHandlers.size(); day < size; day++) {
      final int constDay = day;
      HANDLERS.add(dayHandlers.get(day).addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          dayPopupPresenter.hidePopup();
          tripItem.setStartDay(constDay);
          updateCurrentItem();
        }
      }));
    }
    
    // For hardware search action.
    if (useHardwareKey) {
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
    dayPopupPresenter.release();
  }

  @Override
  public void addMenu() {
    menuPresenter.clear();
    final Trip trip = singletonComponents.getTripService().getTrip(tripItem.getTripId());
    if (isMapScreen()) {
      // SHOW DETAILS
      menuPresenter.addMenuItem(Constants.SHOW_DETAILS_STR,
          TrippyBundle.INSTANCE.iconShowDetails(), new Runnable() {
            @Override
            public void run() {
              singletonComponents.getEventBus().fireEvent(new ShowTripItemDetailsEvent(tripItem));
            }
          });
    } else {
      // SHOW ON MAP
      menuPresenter.addMenuItem(Constants.SHOW_ON_MAP_STR, TrippyBundle.INSTANCE.iconShowOnMap(),
          new Runnable() {
            @Override
            public void run() {
              singletonComponents.getEventBus().fireEvent(
                  new ShowTripOnMapEvent(trip, tripItem.getStartDay(), tripItem.getKey()));
            }
          });
    }

    final boolean isContributor =
        singletonComponents.getUserUtils().isContributor(trip,
            singletonComponents.getUtils().getUserEmail());

    menuPresenter.addMenuItem(Constants.DELETE_STR, TrippyBundle.INSTANCE.trash(), !isContributor,
        new Runnable() {
          @Override
          public void run() {
            if (isContributor) {
              if (Window.confirm(singletonComponents.getMessage().confirmTripItemDelete(
                  tripItem.getName()))) {
                final Toast toast = singletonComponents.getToast();
                try {
                  toast.showLoading(singletonComponents.getMessage().deleting(tripItem.getName()));
                  singletonComponents.getTripItemService().deleteTripItem(tripItem,
                      new AsyncCallback<Void>() {

                        @Override
                        public void onSuccess(Void result) {
                          toast.hideLoading();
                          toast.showToast(singletonComponents.getMessage().tripItemDeleted());
                          singletonComponents.getEventBus().fireEvent(new BackEvent());
                        }

                        @Override
                        public void onFailure(Throwable caught) {
                          toast.hideLoading();
                          if (caught instanceof AuthorizationException) {
                            toast.showToast(singletonComponents.getMessage().unauthorizedDelete(
                                Constants.TRIP_ITEM_STR));
                          } else {
                            toast.showToast(singletonComponents.getMessage()
                                .tripItemDeletionFailed());
                          }
                        }
                      });

                } catch (AuthorizationException e) {
                  toast.hideLoading();
                  toast.showToast(singletonComponents.getMessage().tripItemDeletionFailed());
                }
              }
            } else {
              singletonComponents.getToast().showToast(
                  singletonComponents.getMessage().unauthorizedDelete(
                      Constants.SHARED_TRIP_ITEM_STR));
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

    // EDIT TRIP ITEM
    menuPresenter.addMenuItem(Constants.RESCHEDULE_STR, TrippyBundle.INSTANCE.editIcon(),
        !isContributor, new Runnable() {
          @Override
          public void run() {
            if (isContributor) {
              dayPopupPresenter.showPopup();
            } else {
              singletonComponents.getToast()
                  .showToast(
                      singletonComponents.getMessage().unauthorizedEdit(
                          Constants.SHARED_TRIP_ITEM_STR));
            }
          }
        });
    menuPresenter.setPopupTitle(Constants.TRIP_ITEM_MENU_STR);
    super.addMenu();
  }

  private void updateCurrentItem() {
    final Toast toast = singletonComponents.getToast();
    try {
      toast.showLoading(singletonComponents.getMessage().updating(tripItem.getName()));
      singletonComponents.getTripItemService().updateTripItem(tripItem,
          new AsyncCallback<TripItem>() {

            @Override
            public void onFailure(Throwable caught) {
              toast.hideLoading();
              if (caught instanceof AuthorizationException) {
                toast.showToast(singletonComponents.getMessage().unauthorizedEdit(
                    Constants.TRIP_ITEM_STR));
              } else {
                toast.showToast(singletonComponents.getMessage().tripItemSaveFailed());
              }
            }

            @Override
            public void onSuccess(TripItem result) {
              toast.hideLoading();
              toast.showToast(singletonComponents.getMessage().tripItemModified());
            }
          });
    } catch (AuthorizationException e) {
      toast.hideLoading();
      singletonComponents.getToast().showToast(
          singletonComponents.getMessage().unauthorizedEdit(Constants.TRIP_ITEM_STR));
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
