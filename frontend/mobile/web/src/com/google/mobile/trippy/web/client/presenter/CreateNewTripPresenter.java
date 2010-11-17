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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.maps.client.base.HasLatLngBounds;
import com.google.gwt.maps.client.geocoder.GeocoderCallback;
import com.google.gwt.maps.client.geocoder.HasGeocoder;
import com.google.gwt.maps.client.geocoder.HasGeocoderGeometry;
import com.google.gwt.maps.client.geocoder.HasGeocoderRequest;
import com.google.gwt.maps.client.geocoder.HasGeocoderResult;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.mobile.trippy.web.client.base.Constants;
import com.google.mobile.trippy.web.client.base.Provider;
import com.google.mobile.trippy.web.client.base.SingletonComponents;
import com.google.mobile.trippy.web.client.event.ShowHomePageEvent;
import com.google.mobile.trippy.web.client.event.ShowTripScheduleEvent;
import com.google.mobile.trippy.web.client.service.TripCopyService;
import com.google.mobile.trippy.web.client.service.TripCopyServiceAsync;
import com.google.mobile.trippy.web.client.view.View;
import com.google.mobile.trippy.web.client.widget.Toast;
import com.google.mobile.trippy.web.shared.models.Trip;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This presenter is responsible for creating a new unschedule trip by taking 7
 * days as duration. For this, it takes the location for the trip as input and
 * pass this info to the db service to create a new trip. This presenter is also
 * responsible for creating a new unschedule trip by clicking on the suggested
 * location on the screen.
 * 
 */
public class CreateNewTripPresenter implements
    EventHandlerPresenter<CreateNewTripPresenter.Display> {

  /**
   * Interface to view the create trip screen.
   */
  public static interface Display extends View {

    HasClickHandlers getCreateNewTripButton();

    HasClickHandlers getClosePopUp();

    List<HasClickHandlers> getTopDestinations();

    List<HasClickHandlers> getSuggestedTripList();

    List<String> getTopDestinationsText();

    String getLocationText();

    void setLocationBoxEmpty();

    void setTopDestinations(final String topDestinations[]);

    void addSuggestedTripList(final List<String> suggestedTripList);

    // TODO: Create separate presenter for suggested location.
    void showPopup();

    void hidePopup();

    void setLocationEmptyErrorMsg();

    void clearErrorMsg();
  }

  private static final int STATUS_TIME_MILLIS = 2000;

  private final Display display;

  /* Common container for all the singleton components of the application. */
  private final SingletonComponents singletonComponents;

  private final HasGeocoder geocoder;

  private final Provider<HasGeocoderRequest> geocoderRequestProvider;

  private final List<HandlerRegistration> handlers;

  public CreateNewTripPresenter(final Display display,
      final SingletonComponents singletonComponents, final HasGeocoder geocoder,
      final Provider<HasGeocoderRequest> geocoderRequestProvider) {
    this.display = display;
    this.singletonComponents = singletonComponents;
    this.geocoder = geocoder;
    this.geocoderRequestProvider = geocoderRequestProvider;
    this.handlers = new ArrayList<HandlerRegistration>();
  }

  /**
   * Method binds the handlers with event.
   * 
   * This method takes the responsibility for handling all the events and firing
   * the events.
   * 
   * Events fired : None.
   * 
   */
  @Override
  public void bind() {
    display.hidePopup();

    // Set the top destination and then fetch the click handlers.
    display.setTopDestinations(singletonComponents.getUtils().getTopDestination());
    final List<HasClickHandlers> handlersList = display.getTopDestinations();
    // Separate list for strings if customize destinations available.
    final List<String> topDestinationTxt = display.getTopDestinationsText();
    final int length = singletonComponents.getUtils().getTopDestination().length;
    for (int i = 0; i < length; i++) {
      final int index = i;
      handlers.add(handlersList.get(i).addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          singletonComponents.getToast().hideLoading();
          createLpTrip(topDestinationTxt.get(index).trim());
        }
      }));
    }

    handlers.add(display.getCreateNewTripButton().addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        singletonComponents.getToast().hideLoading();
        if (display.getLocationText().isEmpty()) {
          display.setLocationEmptyErrorMsg();
        } else {
          display.clearErrorMsg();
          createTrip(display.getLocationText().trim());
        }
      }
    }));

    handlers.add(display.getClosePopUp().addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        display.hidePopup();
      }
    }));
  }

  @Override
  public HandlerManager getEventBus() {
    return singletonComponents.getEventBus();
  }

  @Override
  public void release() {
    for (HandlerRegistration handler : handlers) {
      handler.removeHandler();
    }
    handlers.clear();
  }

  @Override
  public Display getDisplay() {
    return display;
  }

  /**
   * Creates the trip by providing the location trip using service.
   */
  void createTrip(final String location) {
    final HasGeocoderRequest geocoderRequest = geocoderRequestProvider.get();
    geocoderRequest.setAddress(location);
    singletonComponents.getToast().showLoading(
        singletonComponents.getMessage().toastMsgResolvingName());
    geocoder.geocode(geocoderRequest, new GeocoderCallback() {

      @Override
      public void callback(final List<HasGeocoderResult> responses, final String status) {
        singletonComponents.getToast().hideLoading();

        switch (responses.size()) {
          case 0: // no locations found
            singletonComponents.getToast().showToast(
                singletonComponents.getMessage().noTripLocations(), STATUS_TIME_MILLIS);
            break;

          case 1: // one location found create trip directly
            final HasGeocoderGeometry geometry = responses.get(0).getGeometry();
            if (geometry.getBounds() != null) {
              final String address = responses.get(0).getAddressComponents().get(0).getLongName();
              addTripToDb(location, address, geometry.getBounds());
            } else {
              singletonComponents.getToast().showToast(
                  singletonComponents.getMessage().noTripLocations(), STATUS_TIME_MILLIS);
            }
            break;

          default: // more than one location found, allow user to choose one
            if (registerSuggestedHandlers(responses)) {
              display.showPopup();
            } else {
              singletonComponents.getToast().showToast(
                  singletonComponents.getMessage().noTripLocations(), STATUS_TIME_MILLIS);
            }
            break;
        }
      }
    });
  }

  /**
   * Creates the trip by providing the location trip using service.
   */
  void createLpTrip(final String location) {
    //TODO: move GWT call outside the presenter.
    TripCopyServiceAsync lpTripService =
        (TripCopyServiceAsync) GWT.create(TripCopyService.class);
    final Toast toast = singletonComponents.getToast();
    toast.showLoading(singletonComponents.getMessage().toastMsgCreatingTrip());
    lpTripService.createLpTrip(location, new AsyncCallback<Trip>() {
      @Override
      public void onSuccess(final Trip result) {
        singletonComponents.getTripService().addTripToLocalDb(result);
        singletonComponents.getTripItemService().sync(result);
        toast.hideLoading();
        toast.showToast(singletonComponents.getMessage().tripCreated());
        singletonComponents.getEventBus().fireEvent(new ShowTripScheduleEvent(result, true));
      }

      @Override
      public void onFailure(final Throwable caught) {
        toast.hideLoading();
        toast.showToast(singletonComponents.getMessage().tripSaveFailed());
        singletonComponents.getEventBus().fireEvent(new ShowHomePageEvent());
      }
    });
  }

  /**
   * Registering the pop up click handlers.
   */
  @SuppressWarnings("deprecation")
  boolean registerSuggestedHandlers(final List<HasGeocoderResult> responses) {
    final List<HasGeocoderResult> validLocations = new ArrayList<HasGeocoderResult>();
    for (final HasGeocoderResult suggestedTrip : responses) {
      final HasLatLngBounds bounds = suggestedTrip.getGeometry().getBounds();
      final String address = suggestedTrip.getAddressComponents().get(0).getLongName();
      if (bounds != null && address != null) {
        validLocations.add(suggestedTrip);
      }
    }

    final List<String> suggestedTripList = new ArrayList<String>();
    for (HasGeocoderResult suggestedTrip : validLocations) {
      suggestedTripList.add(suggestedTrip.getFormattedAddress());
    }
    if (!suggestedTripList.isEmpty()) {
      display.addSuggestedTripList(suggestedTripList);
      List<HasClickHandlers> suggestedHandlers = display.getSuggestedTripList();
      for (int i = 0; i < validLocations.size(); i++) {
        final HasGeocoderResult suggestedTrip = validLocations.get(i);
        final HasLatLngBounds bounds = suggestedTrip.getGeometry().getBounds();
        final String address = suggestedTrip.getAddressComponents().get(0).getLongName();
        final String location = suggestedTrip.getFormattedAddress();
        suggestedHandlers.get(i).addClickHandler(new ClickHandler() {
          @Override
          public void onClick(ClickEvent event) {
            addTripToDb(location, address, bounds);
          }
        });
      }
      return true;
    }
    return false;
  }

  /**
   * Create Trip and Add Trip to the Db.
   */
  void addTripToDb(final String location, final String address, final HasLatLngBounds latlngBound) {
    final Trip trip = new Trip();
    trip.setLocation(location);
    trip.setDuration(Constants.DEFAULT_TRIP_DURATION);
    trip.setLatitude(latlngBound.getCenter().getLatitude());
    trip.setLongitude(latlngBound.getCenter().getLongitude());

    // LP place details.
    trip.setPlaceId(0L);
    trip.setNorthLatitude(latlngBound.getNorthEast().getLatitude());
    trip.setSouthLatitude(latlngBound.getSouthWest().getLatitude());
    trip.setEastLongitude(latlngBound.getNorthEast().getLongitude());
    trip.setWestLongitude(latlngBound.getSouthWest().getLongitude());

    trip.setStartDate(Constants.UNSCHEDULED_DATE);
    trip.setLastModified(new Date());
    trip.setAddedOn(new Date());
    trip.setName(location);
    trip.setDescription("");
    trip.setThumbsUp(0);
    trip.setThumbsDown(0);

    final Toast toast = singletonComponents.getToast();
    toast.showLoading(singletonComponents.getMessage().toastMsgCreatingTrip());
    singletonComponents.getTripService().addTrip(trip, new AsyncCallback<Trip>() {

      @Override
      public void onSuccess(final Trip result) {
        toast.hideLoading();
        toast.showToast(singletonComponents.getMessage().tripCreated());
        singletonComponents.getEventBus().fireEvent(new ShowTripScheduleEvent(result, true));
      }

      @Override
      public void onFailure(final Throwable caught) {
        toast.hideLoading();
        toast.showToast(singletonComponents.getMessage().tripSaveFailed());
        singletonComponents.getEventBus().fireEvent(new ShowHomePageEvent());
      }
    });
  }
}
