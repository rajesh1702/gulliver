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

package com.google.mobile.trippy.web.client.presenter;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.mobile.trippy.web.client.base.Constants;
import com.google.mobile.trippy.web.client.base.SingletonComponents;
import com.google.mobile.trippy.web.client.base.Utils;
import com.google.mobile.trippy.web.client.db.TripItemService;
import com.google.mobile.trippy.web.client.event.ShowSearchItemDetailsEvent;
import com.google.mobile.trippy.web.client.event.ShowSearchResultsOnMapEvent;
import com.google.mobile.trippy.web.client.i18n.Message;
import com.google.mobile.trippy.web.client.service.LPSearchServiceAsync;
import com.google.mobile.trippy.web.client.view.View;
import com.google.mobile.trippy.web.client.widget.Toast;
import com.google.mobile.trippy.web.shared.UserUtils;
import com.google.mobile.trippy.web.shared.exception.AuthorizationException;
import com.google.mobile.trippy.web.shared.models.POIDetail;
import com.google.mobile.trippy.web.shared.models.POIType;
import com.google.mobile.trippy.web.shared.models.SearchItem;
import com.google.mobile.trippy.web.shared.models.SearchItem.SearchType;
import com.google.mobile.trippy.web.shared.models.Trip;
import com.google.mobile.trippy.web.shared.models.TripItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Presenter Class to handle event over SearchResultItemView.
 * 
 * This class will show SearchResult Details View. It will consists following:
 * 1. Name 2. Address 3. List of phone numbers. 4. A link for more info. 5. A
 * link to show the result over map. 6. A button to add this result to the
 * Unscheduled day of the trip. 7. A pop up to add this result to Some scheduled
 * day of the trip.
 * 
 */
public class SearchResultItemPresenter implements
    EventHandlerPresenter<SearchResultItemPresenter.Display> {

  /**
   * Interface to view the search result item.
   */
  public static interface Display extends View {

    public static final int MAX_PHONES = 2;
    HasClickHandlers getAddButton();
    HasClickHandlers getAddIcon();
    HasClickHandlers getShowDetailsButton();
    HasClickHandlers getShowOnMap();
    HasClickHandlers getShowOnMapIcon();
    void setName(final String str);
    void setAddress(final String str);
    void setPhoneNumbers(final ArrayList<String> numbers);
    void setMoreInfoURL(final String targetUrl);
    void setAddButtonVisible(final boolean visible);
    void setReview(final String html);
    void showOnGoogleMapUrl(final String url);
  }

  /** Object to contain the reference of associated view. */
  private final Display display;

  /** Common container for all the singleton components of this application. */
  private final Utils defaultUtils;
  private final UserUtils defaultUserUtils;
  private final TripItemService itemService;
  private final Message messages;
  private final HandlerManager eventBus;
  private final Toast toast;
  private final AddItemPopupPresenter addItemPopupPresenter;
  private final LPSearchServiceAsync lpSearchServiceAsync;
  private final List<HandlerRegistration> handlers;

  private Trip trip;
  private int tripDay;
  private SearchItem searchItem;

  /** Boolean to check whether current user is authorized or not. */
  private boolean isAuthorized;

  public SearchResultItemPresenter(final Display display,
      final SingletonComponents singeltonComponent,
      final AddItemPopupProvider addItemPopupProvider,
      final LPSearchServiceAsync lpSearchServiceAsync) {
    this.display = display;
    this.defaultUtils = singeltonComponent.getUtils();
    this.defaultUserUtils = singeltonComponent.getUserUtils();
    this.itemService = singeltonComponent.getTripItemService();
    this.messages = singeltonComponent.getMessage();
    this.eventBus = singeltonComponent.getEventBus();
    this.toast = singeltonComponent.getToast();
    this.addItemPopupPresenter = addItemPopupProvider.get();
    this.lpSearchServiceAsync = lpSearchServiceAsync;
    this.handlers = new ArrayList<HandlerRegistration>();
  }

  /**
   * Method takes the responsibility for handling all the events and firing the
   * events.
   * 
   * This method also checks whether trip has been set or not. it should be
   * called once Trip has been set using setTrip(Trip trip).
   * 
   * Click Handlers: 1) AddClickHandler: It will have a click handler on Click
   * event of Add/Remove button, which will show pop up for adding search result
   * to trip, if it is not already there. if it is already present it will go
   * for removing the trip item, assuming button was showing title as Remove. 2)
   * 
   */
  @Override
  public void bind() {
    Preconditions.checkNotNull(trip);
    
    // Click Handler for Show Details.
    handlers.add(display.getShowDetailsButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        eventBus.fireEvent(new ShowSearchItemDetailsEvent(searchItem.getId(), trip, tripDay));
      }
    }));
    
    //Operations available only if the current user is authorized
    if (isAuthorized) {
      addItemPopupPresenter.bind();
      
//      if (tripDay == Constants.NO_SELECTED_DAY) {
        addItemPopupPresenter.setDays(trip.getDuration());
       // Click Handlers to add in a particular day.
        
        handlers.add(addItemPopupPresenter.getAddItems().addClickHandler(new ClickHandler() {

          @Override
          public void onClick(ClickEvent event) {
            final List<Integer> days = addItemPopupPresenter.getSelectedDay();
            addItemPopupPresenter.hidePopup();
            if (searchItem.getType().equals(SearchItem.SearchType.GOOGLE)) {
              for (final Integer day : days) {
                addGoogleSearchResultItem(day);
              }
            } else if (searchItem.getType().equals(SearchItem.SearchType.LP)) {
              for (final Integer day : days) {
                addLpSearchResultItem(day);
              }
            }
          }
        }));
      // Click Handler for Add/Remove Button.
      handlers.add(display.getAddButton().addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          addItemPopupPresenter.showPopup();
        }
      }));

      handlers.add(display.getAddIcon().addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          addItemPopupPresenter.showPopup();
        }
      }));
      
    }//authorized operations end

    handlers.add(display.getShowOnMap().addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        eventBus.fireEvent(new ShowSearchResultsOnMapEvent(trip, tripDay, searchItem.getId()));
      }
    }));

    handlers.add(display.getShowOnMapIcon().addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        eventBus.fireEvent(new ShowSearchResultsOnMapEvent(trip, tripDay, searchItem.getId()));
      }
    }));
  }

  public Display getDisplay() {
    return display;
  }

  @Override
  public HandlerManager getEventBus() {
    return eventBus;
  }

  @Override
  public void release() {
    addItemPopupPresenter.release();
    
    for (HandlerRegistration handler : handlers) {
      if (handler != null) {
        handler.removeHandler();
      }
    }
    handlers.clear();
  }

  public void setTrip(final SearchItem item, final Trip trip, final int day) {
    this.searchItem = item;
    this.trip = trip;
    this.tripDay = day;
    this.isAuthorized = defaultUserUtils.isContributor(trip, defaultUtils.getUserEmail());
    populateView();
  }

  /**
   * Method will be called after creating the presenter and view to populate the
   * view. It also checks whether trip has been set or not.
   */
  @VisibleForTesting
  void populateView() {
    Preconditions.checkNotNull(trip);

    if (searchItem.getName() != null) {
      display.setName(searchItem.getName());
      final String url = "http://maps.google.com/maps?q="
          + searchItem.getName()
          + (searchItem.getAddress() == null ||
          searchItem.getAddress().trim().isEmpty() ? "" : ", "
          + searchItem.getAddress()) + "&sll="
          + searchItem.getLatitude() + ","
          + searchItem.getLongitude();
      display.showOnGoogleMapUrl(url);
    }
    if (searchItem.getUrl() != null) {
      display.setMoreInfoURL(searchItem.getUrl());
    }
    display.setAddress(searchItem.getAddress());
    final ArrayList<String> phones = searchItem.getPhoneNumbers();
    display.setPhoneNumbers(phones);
    display.setReview(searchItem.getReview());

    display.setAddButtonVisible(isAuthorized);
  }

  /**
   * Method returns a trip item from the trip having trip id for this item.
   * 
   * It does this by assuming that each trip item will have unique and non NULL
   * URL.
   */
  @VisibleForTesting
  TripItem checkExistingItems() {
//    final ArrayList<TripItem> items = itemService.getTripItems(trip.getKey());
//    for (final TripItem item : items) {
//      if (item.getSearchResultUrl().equals(searchItem.getUrl())) {
//        return item;
//      }
//    }
    return null;
  }

  /**
   * Method add a trip item in the trip having trip id for this item.
   */
  @VisibleForTesting
  void addTripItem(final TripItem tripItem) {
    try {
      toast.showLoading(messages.adding(tripItem.getName()));
      itemService.addTripItem(tripItem, new AsyncCallback<TripItem>() {
        @Override
        public void onSuccess(TripItem result) {
          toast.hideLoading();
          if (tripItem.getStartDay() == Constants.UNSCHEDULED_DAY) {
            toast.showToast(messages.tripItemAddedUnschedule());
          } else {
            toast.showToast(messages.tripItemAdded(tripItem.getStartDay()));
          }
        }

        @Override
        public void onFailure(Throwable caught) {
          toast.hideLoading();
          if (caught instanceof AuthorizationException) {
            toast.showToast(messages.unauthorizedAdd("trip Item"));
          } else {
            toast.showToast(messages.tripItemSaveFailed());
          }
        }
      });
    } catch (AuthorizationException e) {
      toast.hideLoading();
      toast.showToast(e.getMessage());
    }
  }


  /**
   * Method added a Google Search Result item in trip having trip id for this
   * result after creating it from search item.
   */
  @VisibleForTesting
  void addGoogleSearchResultItem(final int day) {
    final TripItem tripItem = new TripItem();
    tripItem.setName(searchItem.getName());
    tripItem.setSearchResultUrl(searchItem.getUrl());
    tripItem.setLatitude(searchItem.getLatitude());
    tripItem.setLongitude(searchItem.getLongitude());
    tripItem.setTripId(trip.getKey());
    tripItem.setStartDay(day);
    tripItem.setAddress(searchItem.getAddress());
    tripItem.setPhoneNumbers(searchItem.getPhoneNumbers());
    
    if (searchItem.getType().equals(SearchType.GOOGLE)) {
      tripItem.setDataSource(SearchItem.SearchType.GOOGLE.toString());
    }
    //app engine doesnt support string more than 500 chars long
    String review = searchItem.getReview();
    if (review.length() > 500) {
      review = review.substring(0, 496) + "...";
    }
    tripItem.setDescription(review);
    addTripItem(tripItem);
  }

  /**
   * Method added a LP Search Result item in trip having trip id for this result
   * after creating it from search item.
   */
  @VisibleForTesting
  void addLpSearchResultItem(final int day) {
    final TripItem tripItem = new TripItem();
    tripItem.setName(searchItem.getName());
    tripItem.setLatitude(searchItem.getLatitude());
    tripItem.setLongitude(searchItem.getLongitude());
    tripItem.setTripId(trip.getKey());
    tripItem.setStartDay(day);
    
    if (searchItem.getType().equals(SearchType.LP)) {
      tripItem.setDataSource(SearchItem.SearchType.LP.toString());
      if (searchItem.getId().contains(POIType.DO.toString())) {
        tripItem.setCategory(POIType.DO.toString());
      } else if (searchItem.getId().contains(POIType.EAT.toString())) {
        tripItem.setCategory(POIType.EAT.toString());
      } else if (searchItem.getId().contains(POIType.GENERAL.toString())) {
        tripItem.setCategory(POIType.GENERAL.toString());
      } else if (searchItem.getId().contains(POIType.NIGHT.toString())) {
        tripItem.setCategory(POIType.NIGHT.toString());
      } else if (searchItem.getId().contains(POIType.SEE.toString())) {
        tripItem.setCategory(POIType.SEE.toString());
      } else if (searchItem.getId().contains(POIType.SHOP.toString())) {
        tripItem.setCategory(POIType.SHOP.toString());
      } else if (searchItem.getId().contains(POIType.SLEEP.toString())) {
        tripItem.setCategory(POIType.SLEEP.toString());
      }
    }
    lpSearchServiceAsync.getPOI(searchItem.getPoiId(), new AsyncCallback<POIDetail>() {
      @Override
      public void onSuccess(POIDetail result) {
        searchItem.setUrl(result.getSearchResultUrl());
        tripItem.setSearchResultUrl(result.getSearchResultUrl());
        tripItem.setAddress(result.getAddress());
        tripItem.setPhoneNumbers(result.getPhones());
        String review = result.getReview();
        //app engine doesnt support string more than 500 chars long
        if (review.length() > 500) {
          review = review.substring(0, 496) + "...";
        }
        tripItem.setDescription(review);
        addTripItem(tripItem);
      }

      @Override
      public void onFailure(Throwable caught) {
        addTripItem(tripItem);
      }
    });
  }

  @VisibleForTesting
  void setAuthorization(final boolean authorize) {
    isAuthorized = authorize;
  }

  @VisibleForTesting
  void setSearchItem(SearchItem data) {
    searchItem = data;
  }
}
