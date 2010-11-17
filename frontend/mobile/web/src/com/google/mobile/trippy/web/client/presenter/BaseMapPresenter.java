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
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.maps.client.base.HasLatLng;
import com.google.gwt.maps.client.base.HasLatLngBounds;
import com.google.gwt.maps.client.event.EventCallback;
import com.google.gwt.maps.client.overlay.HasMarker;
import com.google.mobile.trippy.web.client.TrippyBundle;
import com.google.mobile.trippy.web.client.base.Constants;
import com.google.mobile.trippy.web.client.base.Provider;
import com.google.mobile.trippy.web.client.base.SingletonComponents;
import com.google.mobile.trippy.web.client.event.ShowTripListEvent;
import com.google.mobile.trippy.web.client.presenter.MapPresenter.MapClickListener;
import com.google.mobile.trippy.web.client.presenter.MapPresenter.MarkerClickListener;
import com.google.mobile.trippy.web.client.presenter.header.MenuOptionsPresenter;
import com.google.mobile.trippy.web.client.view.View;
import com.google.mobile.trippy.web.client.widget.InfoWindowPresenter;
import com.google.mobile.trippy.web.client.widget.InfoWindowView;
import com.google.mobile.trippy.web.shared.models.SearchItem;
import com.google.mobile.trippy.web.shared.models.Trip;
import com.google.mobile.trippy.web.shared.models.TripItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Presenter for results on map.
 * 
 */
public abstract class BaseMapPresenter implements EventHandlerPresenter<BaseMapPresenter.Display> {

  /**
   * View interface for this presenter
   */
  public static interface Display extends View {
    BaseHeaderPresenter.Display getHeaderDisplay();

    MapPresenter.Display getMapDisplay();

    MenuOptionsPresenter.Display getHeaderOptionsDisplay();

    HasClickHandlers getPrevButton();

    HasClickHandlers getNextButton();

    HasChangeHandlers getPageList();

    int getSelectedPage();

    void setSelectedPage(int index);

    void setPrevButtonEnabled(boolean enabled);

    void setNextButtonEnabled(boolean enabled);

    void populatePageList(int totalPages);

    void setItemVisible(boolean visible);
  }

  /**
   * Class to act bridge between search item and trip item on map.
   */
  protected class Item {
    public Item(SearchItem item) {
      this.name = item.getName();
      this.address = item.getAddress();
      this.longitude = item.getLongitude();
      this.latitude = item.getLatitude();
      this.id = item.getId();
    }

    public Item(TripItem item) {
      this.name = item.getName();
      this.address = item.getAddress();
      this.longitude = item.getLongitude();
      this.latitude = item.getLatitude();
      this.id = item.getKey();
    }

    private final String name;
    private final String address;
    private final double longitude;
    private final double latitude;
    private final String id;

    public String getName() {
      return name;
    }

    public String getAddress() {
      return address;
    }

    public double getLongitude() {
      return longitude;
    }

    public double getLatitude() {
      return latitude;
    }

    public String getId() {
      return id;
    }
  }

  protected final Display display;
  protected final SingletonComponents singletonComponents;
  protected final BaseHeaderPresenter headerPresenter;
  protected final MapPresenter mapPresenter;
  protected final MenuOptionsPresenter headerOptions;
  protected final InfoWindowPresenter infoWindow;
  protected final List<HandlerRegistration> handlers;

  protected int currentItem;
  protected Trip trip;
  protected int tripDay;
  protected List<Item> shownItems;
  protected String resultPinUrl = null;
  private final boolean searchResultHeader;
  private final TripEditPopupPresenter tripEditPopupPresenter;

  public BaseMapPresenter(final Display display, final SingletonComponents singletonComponents,
      final Provider<BaseHeaderPresenter> headerProvider, final Provider<MapPresenter> mapProvider,
      final Provider<TripEditPopupPresenter> tripEditPopupPresenter,
      final Provider<? extends MenuOptionsPresenter> headerOptionsProvider,
      final boolean searchResultHeader) {
    this.display = display;
    this.singletonComponents = singletonComponents;
    this.headerPresenter = headerProvider.get();
    mapPresenter = mapProvider.get();
    this.tripEditPopupPresenter = tripEditPopupPresenter.get();
    this.headerOptions = headerOptionsProvider.get();
    // TODO: create through provider
    this.infoWindow =
        new InfoWindowPresenter(new InfoWindowView(mapPresenter.getDisplay().getMap()));
    this.handlers = new ArrayList<HandlerRegistration>();
    this.searchResultHeader = searchResultHeader;
  }

  @Override
  public Display getDisplay() {
    return display;
  }

  /**
   * Handle user-events happening on view and update view in response to app
   * events
   * 
   * Events fired : None; Events listened : None
   */
  @Override
  public void bind() {
    tripEditPopupPresenter.bind();
    headerPresenter.bind();
    headerOptions.bind();
    headerOptions.setSearchClickListener(new MenuOptionsPresenter.SearchClickListener() {
      @Override
      public void onSearchClick(SearchBarPresenter searchBar) {
        searchBar.setTrip(trip, tripDay, mapPresenter.getMapBounds());
      }
    });

    handlers.add(display.getPrevButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        int indexToHighlight = currentItem - 1;
        if (indexToHighlight == -1) {
          indexToHighlight = shownItems.size() - 1;
        }
        highlightResultIndex(indexToHighlight);
      }
    }));

    handlers.add(display.getNextButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        int indexToHighlight = currentItem + 1;
        if (indexToHighlight == shownItems.size()) {
          indexToHighlight = 0;
        }
        highlightResultIndex(indexToHighlight);
      }
    }));

    // show user selected page
    handlers.add(display.getPageList().addChangeHandler(new ChangeHandler() {
      @Override
      public void onChange(ChangeEvent event) {
        showPageItems(display.getSelectedPage());
      }
    }));

    // close info window when user clicks on map
    mapPresenter.setMapClickListener(new MapClickListener() {
      @Override
      public void onMapClick(HasLatLng position) {
        infoWindow.close();
      }
    });
  }

  @Override
  public HandlerManager getEventBus() {
    return singletonComponents.getEventBus();
  }

  @Override
  public void release() {
    tripEditPopupPresenter.release();
    headerPresenter.release();
    headerOptions.release();
    for (final HandlerRegistration handler : handlers) {
      if (handler != null) {
        handler.removeHandler();
      }
    }
    handlers.clear();
  }
  
  protected void setMap(final Trip trip, int day, final Runnable runnable) {
    Preconditions.checkNotNull(trip);
    this.trip = trip;
    this.tripDay = day;
    tripEditPopupPresenter.setTrip(trip);
    headerPresenter.setTitleString(trip.getName(), new Runnable() {
      @Override
      public void run() {
        tripEditPopupPresenter.showPopup();
      }
    });
    
    if (searchResultHeader) {
      headerPresenter.setNavigation(Constants.TRIPS_STR, new Runnable() {

        @Override
        public void run() {
          getEventBus().fireEvent(new ShowTripListEvent());
        }
      });
      headerPresenter.setSubNavigation(Constants.ITINERARY_STR, runnable);
    } else {
      headerPresenter.setNavigation(Constants.ITINERARY_STR, runnable);
    }
    headerPresenter.setOptions(headerOptions, true);
    headerOptions.setMapScreen(true);
  }

  @VisibleForTesting
  protected List<HasLatLng> populate() {
    final List<HasLatLng> points = new ArrayList<HasLatLng>();

    // Clear existing content
    infoWindow.close();
    mapPresenter.clearMap();
    display.setPrevButtonEnabled(false);
    display.setNextButtonEnabled(false);

    if (shownItems.isEmpty()) {
      mapPresenter.setCenter(mapPresenter.getLatLng(trip.getLatitude(), trip.getLongitude()));
      display.setItemVisible(false);
    } else {
      display.setItemVisible(true);
    } 
    
    for (int i = 0; i < shownItems.size(); i++) {
      final Item result = shownItems.get(i);
      final HasLatLng point = mapPresenter.getLatLng(result.getLatitude(), result.getLongitude());
      final HasMarker marker = mapPresenter.addMarker(result.getLatitude(), result.getLongitude());
      mapPresenter.setMarkerImage(i, resultPinUrl);
      final int resultIndex = i;
      final EventCallback callback;
      mapPresenter.setMarkerClickListener(marker, new MarkerClickListener() {
        @Override
        public void onMarkerClick(HasMarker marker) {
          highlightResultIndex(resultIndex);
        }
      });
      points.add(point);
    }
    
    if (shownItems.size() > 1) {
      display.setPrevButtonEnabled(true);
      display.setNextButtonEnabled(true); 
    } 
    return points;
  }

  protected void highlightResultIndex(final int index, final Item item, 
      EventCallback eventCallback) {
    mapPresenter.setMarkerImage(currentItem, resultPinUrl);
    mapPresenter.setMarkerImage(index, TrippyBundle.INSTANCE.mapMarkerHighlighted().getURL());
    final HasLatLng point = mapPresenter.getLatLng(item.getLatitude(), item.getLongitude());
    HasLatLngBounds mapBounds = mapPresenter.getMapBounds();
    if (mapBounds.getJso() != null && !mapBounds.contains(point)) {
      mapPresenter.setCenter(point);
    }

    // show info window for highlighted item
    infoWindow.setContent(item.getName());
    infoWindow.setPosition(point);
    infoWindow.setClickHandler(eventCallback);
    infoWindow.open(null, null);
    currentItem = index;
  }
  
  protected abstract void highlightResultIndex(final int index);
  
  protected abstract void showPageItems(int selectedPage);
}
