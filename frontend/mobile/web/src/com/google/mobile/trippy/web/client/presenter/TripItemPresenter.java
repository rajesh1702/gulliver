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

import com.google.common.base.Preconditions;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.mobile.trippy.web.client.base.Constants;
import com.google.mobile.trippy.web.client.base.Provider;
import com.google.mobile.trippy.web.client.base.SingletonComponents;
import com.google.mobile.trippy.web.client.base.Utils;
import com.google.mobile.trippy.web.client.event.CommentAddedEvent;
import com.google.mobile.trippy.web.client.event.CommentAddedEventHandler;
import com.google.mobile.trippy.web.client.event.CommentDeletedEvent;
import com.google.mobile.trippy.web.client.event.CommentDeletedEventHandler;
import com.google.mobile.trippy.web.client.event.ShowTripItemCommentsEvent;
import com.google.mobile.trippy.web.client.event.ShowTripItemDetailsEvent;
import com.google.mobile.trippy.web.client.event.ShowTripOnMapEvent;
import com.google.mobile.trippy.web.client.event.TripItemUpdatedEvent;
import com.google.mobile.trippy.web.client.event.TripItemUpdatedEventHandler;
import com.google.mobile.trippy.web.client.i18n.Message;
import com.google.mobile.trippy.web.client.presenter.header.TripItemOptionsPresenter;
import com.google.mobile.trippy.web.client.view.View;
import com.google.mobile.trippy.web.client.widget.Toast;
import com.google.mobile.trippy.web.shared.exception.AuthorizationException;
import com.google.mobile.trippy.web.shared.models.Trip;
import com.google.mobile.trippy.web.shared.models.TripItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class is responsible to show the trip item details. This presenter is
 * also responsible to update the trip item when user reschedule the trip item
 * or like/dislike.
 * 
 */
public class TripItemPresenter implements EventHandlerPresenter<TripItemPresenter.Display> {

  /**
   * Interface for the view of this presenter
   */
  public static interface Display extends View {
    HasClickHandlers getCommentButton();
    HasClickHandlers getCommentIcon();
    HasClickHandlers getLike();
    HasClickHandlers getLikeIcon();
    HasClickHandlers getDislike();
    HasClickHandlers getShowDetails();
    HasClickHandlers getShowOnMapButton();
    HasClickHandlers getShowOnMapIcon();
    HasClickHandlers getRescheduleButton();
    HasClickHandlers getRescheduleIcon();
    HasClickHandlers getOptions();
    void setName(final String title);
    void setLocation(final String location, final double lat, final double lng);
    void showOnGoogleMapUrl(final String url);
    void setThumbUpCount(final int count);
    void setThumbDownCount(final int count);
    void setCommentCount(final int count);
    void setMoreInfoLink(final String href);
    void clear();
    void setDay(final String dayStr);
    void setSchedule(final String scheduleStr); 
    void addPhoneNumber(final String phoneNumbers);
    void setReview(final String html);
  }

  private final Display display;
  private final List<HandlerRegistration> handlers;
  private final SingletonComponents singletonComponents;
  private final DayPopupPresenter dayPopupPresenter;
  private final TripItemOptionsPresenter tripItemOptionsPresenter;
  private TripItem tripItem;
  private int commentCount;
  private boolean isPartial = false;

  public TripItemPresenter(final Display display, final SingletonComponents singletonComponents,
      final Provider<TripItemOptionsPresenter> tripItemOptionsPresenterProvider) {
    this.display = display;
    this.singletonComponents = singletonComponents;
    this.handlers = new ArrayList<HandlerRegistration>();
    this.dayPopupPresenter = new DayPopupProvider().get();
    this.tripItemOptionsPresenter = tripItemOptionsPresenterProvider.get();
  }

  /**
   * Method binds the presenter with the view.
   * 
   * This method takes the responsibility for handling all the events and firing
   * the events. Events listened : 1) TripItemUpdatedEvent: This event will be
   * listen when the trip item is updated, then updates trip item on the
   * display.
   * 
   * 2) CommentAddedEvent: This event will be listen when some new comment is
   * added for the trip item, then updates trip item on the display.
   * 
   * 3) CommentDeletedEvent: This event will be listen when some comment is
   * deleted for the trip item, then updates trip item on the display.
   * 
   * Events fired : 1) ShowTripItemDetailsEvent: This event will be fired to
   * show trip item detail when one clicks on the trip item.
   * 
   * 2) ShowTripItemCommentsEvent: This event will be fired to show trip item
   * comments when one clicks on the comment button.
   * 
   * 3) ShowTripItemOnMapEvent: This event will be fired to show trip item on
   * map when one clicks on the map button.
   * 
   * 4) ShowTripItemEditScreenEvent: This event will be fired to show trip item
   * edit screen when one clicks on the edit button.
   */
  @Override
  public void bind() {
    tripItemOptionsPresenter.bind(false);
    
    handlers.add(singletonComponents.getEventBus().addHandler(TripItemUpdatedEvent.getType(),
        new TripItemUpdatedEventHandler() {

          @Override
          public void onTripItemUpdated(TripItemUpdatedEvent event) {
            if (event.getTripItem().equals(tripItem)) {
              release();
              setTripItem(event.getTripItem());
              bind();
            }
          }
        }));

    handlers.add(display.getShowDetails().addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        singletonComponents.getEventBus().fireEvent(new ShowTripItemDetailsEvent(tripItem));
      }
    }));
    
    handlers.add(display.getOptions().addClickHandler(new ClickHandler() {
      
      @Override
      public void onClick(ClickEvent event) {
        tripItemOptionsPresenter.getMenuPresenter().showMenu();
      }
    }));
    
    if (tripItem != null && !isPartial) {
      final Trip trip = singletonComponents.getTripService().getTrip(tripItem.getTripId());
      dayPopupPresenter.bind();
      
      List<HasClickHandlers> dayHandlers = dayPopupPresenter.getDayClickHandlers();
      for (int day = 0, size = dayHandlers.size(); day < size; day++) {
        final int constDay = day;
        handlers.add(dayHandlers.get(day).addClickHandler(new ClickHandler() {
          @Override
          public void onClick(ClickEvent event) {
            dayPopupPresenter.hidePopup();
            tripItem.setStartDay(constDay);
            updateCurrentItem();
          }
        }));
      }

      handlers.add(display.getRescheduleButton().addClickHandler(new ClickHandler() {
  
        @Override
        public void onClick(ClickEvent event) {
          doReschedule(trip);
        }
      }));

      handlers.add(display.getRescheduleIcon().addClickHandler(new ClickHandler() {

        @Override
        public void onClick(ClickEvent event) {
          doReschedule(trip);
        }
      }));

      handlers.add(display.getShowOnMapButton().addClickHandler(new ClickHandler() {

        @Override
        public void onClick(ClickEvent event) {
          singletonComponents.getEventBus().fireEvent(new ShowTripOnMapEvent(trip, 
              tripItem.getStartDay(), tripItem.getKey()));
        }
      }));

      handlers.add(display.getShowOnMapIcon().addClickHandler(new ClickHandler() {

        @Override
        public void onClick(ClickEvent event) {
          singletonComponents.getEventBus().fireEvent(new ShowTripOnMapEvent(trip, 
              tripItem.getStartDay(), tripItem.getKey()));
        }
      }));

      handlers.add(singletonComponents.getEventBus().addHandler(CommentAddedEvent.getType(),
          new CommentAddedEventHandler() {
  
            @Override
            public void onCommentAdded(CommentAddedEvent event) {
              if (event.getComment().getTripItemId().equals(tripItem.getKey())) {
                display.setCommentCount(commentCount++);
              }
            }
          }));

      handlers.add(singletonComponents.getEventBus().addHandler(CommentDeletedEvent.getType(),
          new CommentDeletedEventHandler() {
  
            @Override
            public void onCommentDeleted(CommentDeletedEvent event) {
              if (event.getComment().getTripItemId().equals(tripItem.getKey())) {
                commentCount = commentCount > 0 ? commentCount - 1 : 0;
                display.setCommentCount(commentCount);
              }
            }
          }));

      handlers.add(display.getCommentButton().addClickHandler(new ClickHandler() {
  
        @Override
        public void onClick(ClickEvent event) {
          singletonComponents.getEventBus().fireEvent(new ShowTripItemCommentsEvent(tripItem));
        }
      }));

      handlers.add(display.getCommentIcon().addClickHandler(new ClickHandler() {

        @Override
        public void onClick(ClickEvent event) {
          singletonComponents.getEventBus().fireEvent(new ShowTripItemCommentsEvent(tripItem));
        }
      }));

      final boolean isAuthorized =
        singletonComponents.getUserUtils().isContributor(trip,
            singletonComponents.getUtils().getUserEmail());
      final Message messages = singletonComponents.getMessage();

      handlers.add(display.getLike().addClickHandler(new ClickHandler() {

        @Override
        public void onClick(ClickEvent event) {
          // Owner/contributers operations
          if (isAuthorized) {
            tripItem.setThumbsUp(tripItem.getThumbsUp() + 1);
            updateCurrentItem();
          } else {
            singletonComponents.getToast().showToast(
                singletonComponents.getMessage().unauthorizedEdit(messages.sharedTripItem()));
          }
        }
      }));

      handlers.add(display.getLikeIcon().addClickHandler(new ClickHandler() {

        @Override
        public void onClick(ClickEvent event) {
          // Owner/contributers operations
          if (isAuthorized) {
            tripItem.setThumbsUp(tripItem.getThumbsUp() + 1);
            updateCurrentItem();
          } else {
            singletonComponents.getToast().showToast(
                singletonComponents.getMessage().unauthorizedEdit(messages.sharedTripItem()));
          }
        }
      }));
      //TODO: Remove dislike functionality if not rquired.
    }
  }

  /**
   * Set trip item.
   * 
   */
  public void setTripItem(final TripItem item, final boolean isPartial) {
    this.isPartial = isPartial;
    setTripItem(item);
  }
  
  /**
   * Sets the trip item id, trip id and trip item details to display on the
   * screen.
   * 
   */
  public void setTripItem(final TripItem item) {
    Preconditions.checkNotNull(item, "Received tripItem is null");
    this.tripItem = item;
    tripItemOptionsPresenter.setTripItem(tripItem);
    commentCount =
      singletonComponents.getCommentService().getComments(
          tripItem.getTripId(), tripItem.getKey()).size();
    if (isPartial) {
      display.clear();
      display.setName(tripItem.getName());
      display.setLocation(tripItem.getAddress(), tripItem.getLatitude(), tripItem.getLongitude());
      display.setThumbUpCount(tripItem.getThumbsUp());
      display.setThumbDownCount(tripItem.getThumbsDown());
      display.setCommentCount(commentCount);
    } else {
      final Trip trip = singletonComponents.getTripService().getTrip(tripItem.getTripId());
      dayPopupPresenter.setDays(trip.getDuration());
      populateView();
    }
  }
  
  private void populateView() {
    display.clear();
    display.setName(tripItem.getName());
    display.setLocation(tripItem.getAddress(), tripItem.getLatitude(), tripItem.getLongitude());
    display.setThumbUpCount(tripItem.getThumbsUp());
    display.setThumbDownCount(tripItem.getThumbsDown());
    display.setCommentCount(commentCount);
    display.setReview(tripItem.getDescription());
    display.setMoreInfoLink(tripItem.getSearchResultUrl());

    final Trip trip = singletonComponents.getTripService().getTrip(tripItem.getTripId());
    Date startDate = trip.getStartDate();
    String dateStr = Constants.UNSCHEDULED_STR;
    final Utils utils = singletonComponents.getUtils();
    if (startDate != null && tripItem.getStartDay() != Constants.UNSCHEDULED_DAY
        && tripItem.getStartDay() <= trip.getDuration()) {
      dateStr = Constants.DAY_LABEL_STR + " " + tripItem.getStartDay();
      if (!startDate.equals(Constants.UNSCHEDULED_DATE)) {
        Date itemdate = (Date) startDate.clone();
        itemdate = utils.addDaysToDate(itemdate, tripItem.getStartDay() - 1);
        display.setSchedule(utils.getTripItemDate(itemdate));
      }
    } else {
      display.setSchedule("");
    }
    for (final String phoneNumber : tripItem.getPhoneNumbers()) {
      if (phoneNumber != null) {
        display.addPhoneNumber(phoneNumber);
      }
    }
    if (tripItem.getPhoneNumbers().isEmpty()) {
      display.addPhoneNumber(null);
    }
    display.setDay(dateStr);
    final String url = "http://maps.google.com/maps?q="
        + tripItem.getName()
        + (tripItem.getAddress() == null || tripItem.getAddress().trim().isEmpty() ? "" : ", "
        + tripItem.getAddress()) + "&sll="
        + tripItem.getLatitude() + "," + tripItem.getLongitude();
    display.showOnGoogleMapUrl(url);
  }

  @Override
  public HandlerManager getEventBus() {
    return singletonComponents.getEventBus();
  }

  @Override
  public void release() {
    for (HandlerRegistration handler : handlers) {
      if (handler != null) {
        handler.removeHandler();
      }
    }
    handlers.clear();
    dayPopupPresenter.release();
    tripItemOptionsPresenter.release();
  }

  @Override
  public Display getDisplay() {
    return display;
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
                toast.showToast(singletonComponents.getMessage().unauthorizedEdit(Constants.TRIP_ITEM_STR));
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

  private void doReschedule(final Trip trip) {
    final Message messages = singletonComponents.getMessage();
    final boolean isContributor = 
        singletonComponents.getUserUtils().isContributor(trip, 
        singletonComponents.getUtils().getUserEmail());
    if (isContributor) {
      dayPopupPresenter.showPopup();
    } else {
      singletonComponents.getToast().showToast(
          singletonComponents.getMessage().unauthorizedEdit(messages.sharedTripItem()));
    }
  }
}
