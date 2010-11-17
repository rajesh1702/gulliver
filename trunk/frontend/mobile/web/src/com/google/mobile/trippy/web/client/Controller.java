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

package com.google.mobile.trippy.web.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.maps.client.base.HasLatLngBounds;
import com.google.gwt.maps.client.base.LatLng;
import com.google.gwt.maps.client.base.LatLngBounds;
import com.google.gwt.maps.client.geocoder.Geocoder;
import com.google.gwt.maps.client.geocoder.HasGeocoder;
import com.google.gwt.maps.client.geocoder.HasGeocoderRequest;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.mobile.trippy.web.client.base.Constants;
import com.google.mobile.trippy.web.client.base.Provider;
import com.google.mobile.trippy.web.client.base.SingletonComponents;
import com.google.mobile.trippy.web.client.db.DefaultSearchService;
import com.google.mobile.trippy.web.client.db.SearchService;
import com.google.mobile.trippy.web.client.db.SearchService.SearchItemListener;
import com.google.mobile.trippy.web.client.db.SearchService.SearchResultsListener;
import com.google.mobile.trippy.web.client.db.TripService;
import com.google.mobile.trippy.web.client.event.BackEvent;
import com.google.mobile.trippy.web.client.event.BackEventHandler;
import com.google.mobile.trippy.web.client.event.SearchQueryEvent;
import com.google.mobile.trippy.web.client.event.SearchQueryEventHandler;
import com.google.mobile.trippy.web.client.event.ShareTripEvent;
import com.google.mobile.trippy.web.client.event.ShareTripEventHandler;
import com.google.mobile.trippy.web.client.event.ShowCreateTripEvent;
import com.google.mobile.trippy.web.client.event.ShowCreateTripEventHandler;
import com.google.mobile.trippy.web.client.event.ShowFilteredTripListEvent;
import com.google.mobile.trippy.web.client.event.ShowFilteredTripListEventHandler;
import com.google.mobile.trippy.web.client.event.ShowHomePageEvent;
import com.google.mobile.trippy.web.client.event.ShowHomePageEventHandler;
import com.google.mobile.trippy.web.client.event.ShowSearchItemDetailsEvent;
import com.google.mobile.trippy.web.client.event.ShowSearchItemDetailsEventHandler;
import com.google.mobile.trippy.web.client.event.ShowSearchResultsInListEvent;
import com.google.mobile.trippy.web.client.event.ShowSearchResultsInListEventHandler;
import com.google.mobile.trippy.web.client.event.ShowSearchResultsOnMapEvent;
import com.google.mobile.trippy.web.client.event.ShowSearchResultsOnMapEventHandler;
import com.google.mobile.trippy.web.client.event.ShowTripItemCommentsEvent;
import com.google.mobile.trippy.web.client.event.ShowTripItemCommentsEventHandler;
import com.google.mobile.trippy.web.client.event.ShowTripItemDetailsEvent;
import com.google.mobile.trippy.web.client.event.ShowTripItemDetailsEventHandler;
import com.google.mobile.trippy.web.client.event.ShowTripListEvent;
import com.google.mobile.trippy.web.client.event.ShowTripListEventHanlder;
import com.google.mobile.trippy.web.client.event.ShowTripOnMapEvent;
import com.google.mobile.trippy.web.client.event.ShowTripOnMapEventHandler;
import com.google.mobile.trippy.web.client.event.ShowTripScheduleEvent;
import com.google.mobile.trippy.web.client.event.ShowTripScheduleEventHandler;
import com.google.mobile.trippy.web.client.i18n.Message;
import com.google.mobile.trippy.web.client.presenter.AddItemPopupProvider;
import com.google.mobile.trippy.web.client.presenter.BaseHeaderPresenter;
import com.google.mobile.trippy.web.client.presenter.BaseHeaderProvider;
import com.google.mobile.trippy.web.client.presenter.CommentAddPresenter;
import com.google.mobile.trippy.web.client.presenter.CommentAddPresenterProvider;
import com.google.mobile.trippy.web.client.presenter.CommentListItemPresenter;
import com.google.mobile.trippy.web.client.presenter.CommentListItemPresenterProvider;
import com.google.mobile.trippy.web.client.presenter.CommentsPresenter;
import com.google.mobile.trippy.web.client.presenter.CommentsPresenterProvider;
import com.google.mobile.trippy.web.client.presenter.CreateNewTripProvider;
import com.google.mobile.trippy.web.client.presenter.DayListItemPresenter;
import com.google.mobile.trippy.web.client.presenter.DayListItemProvider;
import com.google.mobile.trippy.web.client.presenter.DayListPresenter;
import com.google.mobile.trippy.web.client.presenter.DayListProvider;
import com.google.mobile.trippy.web.client.presenter.DayPopupPresenter;
import com.google.mobile.trippy.web.client.presenter.DayPopupProvider;
import com.google.mobile.trippy.web.client.presenter.FooterPresenterProvider;
import com.google.mobile.trippy.web.client.presenter.HasGeocoderRequestProvider;
import com.google.mobile.trippy.web.client.presenter.HomeHeaderPresenter;
import com.google.mobile.trippy.web.client.presenter.HomeHeaderProvider;
import com.google.mobile.trippy.web.client.presenter.HomePanelProvider;
import com.google.mobile.trippy.web.client.presenter.MapPresenter;
import com.google.mobile.trippy.web.client.presenter.MapProvider;
import com.google.mobile.trippy.web.client.presenter.MenuItemPresenter;
import com.google.mobile.trippy.web.client.presenter.MenuItemProvider;
import com.google.mobile.trippy.web.client.presenter.MenuPresenter;
import com.google.mobile.trippy.web.client.presenter.MenuProvider;
import com.google.mobile.trippy.web.client.presenter.SearchBarPresenter;
import com.google.mobile.trippy.web.client.presenter.SearchBarProvider;
import com.google.mobile.trippy.web.client.presenter.SearchResultItemPresenter;
import com.google.mobile.trippy.web.client.presenter.SearchResultItemProvider;
import com.google.mobile.trippy.web.client.presenter.SearchResultsListPresenter;
import com.google.mobile.trippy.web.client.presenter.SearchResultsListProvider;
import com.google.mobile.trippy.web.client.presenter.SearchResultsMapPresenter;
import com.google.mobile.trippy.web.client.presenter.TripEditPopupPresenter;
import com.google.mobile.trippy.web.client.presenter.TripEditPopupProvider;
import com.google.mobile.trippy.web.client.presenter.TripItemPresenter;
import com.google.mobile.trippy.web.client.presenter.TripItemProvider;
import com.google.mobile.trippy.web.client.presenter.TripItemProvider.ViewType;
import com.google.mobile.trippy.web.client.presenter.TripListFilterPresenter;
import com.google.mobile.trippy.web.client.presenter.TripListFilterProvider;
import com.google.mobile.trippy.web.client.presenter.TripListItemPresenter;
import com.google.mobile.trippy.web.client.presenter.TripListItemPresenterProvider;
import com.google.mobile.trippy.web.client.presenter.TripListPresenterProvider;
import com.google.mobile.trippy.web.client.presenter.TripMapPresenter;
import com.google.mobile.trippy.web.client.presenter.TripSharePresenter;
import com.google.mobile.trippy.web.client.presenter.TripShareProvider;
import com.google.mobile.trippy.web.client.presenter.header.LogOutOptionsPresenter;
import com.google.mobile.trippy.web.client.presenter.header.LogOutOptionsProvider;
import com.google.mobile.trippy.web.client.presenter.header.SearchResultsOptionsPresenter;
import com.google.mobile.trippy.web.client.presenter.header.SearchResultsOptionsProvider;
import com.google.mobile.trippy.web.client.presenter.header.TripItemOptionsPresenter;
import com.google.mobile.trippy.web.client.presenter.header.TripItemOptionsProvider;
import com.google.mobile.trippy.web.client.presenter.header.TripListOptionsPresenter;
import com.google.mobile.trippy.web.client.presenter.header.TripListOptionsProvider;
import com.google.mobile.trippy.web.client.presenter.header.TripOptionsPresenter;
import com.google.mobile.trippy.web.client.presenter.header.TripOptionsProvider;
import com.google.mobile.trippy.web.client.screen.presenter.CreateNewTripScreenPresenter;
import com.google.mobile.trippy.web.client.screen.presenter.HomeScreenPresenter;
import com.google.mobile.trippy.web.client.screen.presenter.SearchResultItemScreenPresenter;
import com.google.mobile.trippy.web.client.screen.presenter.SearchResultsListScreenPresenter;
import com.google.mobile.trippy.web.client.screen.presenter.TripDetailsScreenPresenter;
import com.google.mobile.trippy.web.client.screen.presenter.TripItemCommentsScreenPresenter;
import com.google.mobile.trippy.web.client.screen.presenter.TripItemDetailsScreenPresenter;
import com.google.mobile.trippy.web.client.screen.presenter.TripListScreenPresenter;
import com.google.mobile.trippy.web.client.screen.presenter.TripShareScreenPresenter;
import com.google.mobile.trippy.web.client.screen.view.CreateNewTripScreenView;
import com.google.mobile.trippy.web.client.screen.view.HomeScreenView;
import com.google.mobile.trippy.web.client.screen.view.SearchResultItemScreenView;
import com.google.mobile.trippy.web.client.screen.view.SearchResultsListScreenView;
import com.google.mobile.trippy.web.client.screen.view.TripDetailsScreenView;
import com.google.mobile.trippy.web.client.screen.view.TripItemCommentsScreenView;
import com.google.mobile.trippy.web.client.screen.view.TripItemDetailsScreenView;
import com.google.mobile.trippy.web.client.screen.view.TripListScreenView;
import com.google.mobile.trippy.web.client.screen.view.TripShareScreenView;
import com.google.mobile.trippy.web.client.service.RemoteTripService;
import com.google.mobile.trippy.web.client.service.RemoteTripServiceAsync;
import com.google.mobile.trippy.web.client.view.SearchResultsListView;
import com.google.mobile.trippy.web.client.view.SearchResultsMapView;
import com.google.mobile.trippy.web.client.view.TripMapView;
import com.google.mobile.trippy.web.client.widget.Toast;
import com.google.mobile.trippy.web.shared.models.POIType;
import com.google.mobile.trippy.web.shared.models.SearchItem;
import com.google.mobile.trippy.web.shared.models.SearchItem.SearchType;
import com.google.mobile.trippy.web.shared.models.Trip;
import com.google.mobile.trippy.web.shared.models.TripItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * This class handles screen change events.
 * 
 * 
 */
public class Controller {

  private final SingletonComponents singletonComponents;
  private final TripService tripService;
  private final SearchService searchService;
  private final Toast toast;
  private final Message messages;
  private final RemoteTripServiceAsync remoteTripService;

  // Screens
  private HomeScreenPresenter homeScreen;
  private TripListScreenPresenter triplistScreen;
  private TripItemDetailsScreenPresenter tripItemDetailsScreen;
  private SearchResultsMapPresenter searchResultsMapPresenter;
  private TripItemCommentsScreenPresenter commentsScreen;
  private TripShareScreenPresenter tripShareScreenPresenter;
  private CreateNewTripScreenPresenter createTripScreenPresenter;
  private TripDetailsScreenPresenter tripDetailsScreenPresenter;
  private SearchResultsListScreenPresenter searchResultsListScreenPresenter;
  private SearchResultItemScreenPresenter searchItemScreen;
  private TripMapPresenter tripMapPresenter;

  public Controller(SingletonComponents singletonComponent) {
    this.singletonComponents = singletonComponent;
    this.tripService = singletonComponent.getTripService();
    this.searchService = new DefaultSearchService();
    this.toast = singletonComponent.getToast();
    this.messages = singletonComponent.getMessage();
    this.remoteTripService = (RemoteTripServiceAsync) GWT.create(RemoteTripService.class);
  }

  public void bind() {
    final HandlerManager eventBus = singletonComponents.getEventBus();

    eventBus.addHandler(BackEvent.getType(), new BackEventHandler() {
      @Override
      public void onBack(BackEvent event) {
        History.back();
      }
    });

    eventBus.addHandler(ShowHomePageEvent.getType(), new ShowHomePageEventHandler() {
      @Override
      public void showHomePage(ShowHomePageEvent event) {
        showHomeScreen(event);
      }
    });

    eventBus.addHandler(ShowCreateTripEvent.getType(), new ShowCreateTripEventHandler() {
      @Override
      public void onShowCreateTrip(ShowCreateTripEvent event) {
        showCreateTripScreen(event);
      }
    });

    eventBus.addHandler(ShowFilteredTripListEvent.getType(),
        new ShowFilteredTripListEventHandler() {
          @Override
          public void onShowFilteredTripList(ShowFilteredTripListEvent event) {

            final List<Trip> trips = tripService.getTrips();
            final ArrayList<Trip> filteredTrips = new ArrayList<Trip>();
            final String query = event.getSearchTripQuery();
            String titleString = Constants.MY_TRIPS_STR;
            
            if (query.equals(Constants.UPCOMING_TRIPS_STR)) {
              final Date currentDate = new Date();
              for (final Trip trip : trips) {
                final Date startDate = trip.getStartDate();
                if (startDate.after(currentDate)) {
                  filteredTrips.add(trip);
                }
              }
              titleString = Constants.UPCOMING_TRIPS_STR;
            } else if (query.equals(Constants.CURRENT_TRIPS_STR)) {
              final Date currentDate = new Date();
              for (final Trip trip : trips) {
                if (trip.getStartDate().before(currentDate) && 
                    currentDate.before(singletonComponents.getUtils().addDaysToDate(
                        trip.getStartDate(), trip.getDuration()))) {
                  filteredTrips.add(trip);
                }
              }
              titleString = Constants.CURRENT_TRIPS_STR;
            } else if (query.equals(Constants.PAST_TRIPS_STR)) {
              final Date currentDate = new Date();
              for (final Trip trip : trips) {
                final Date startDate = trip.getStartDate();
                if ((singletonComponents.getUtils().addDaysToDate(
                    startDate, trip.getDuration())).before(currentDate) && 
                    !startDate.equals(Constants.UNSCHEDULED_DATE)) {
                  filteredTrips.add(trip);
                }
              }
              titleString = Constants.PAST_TRIPS_STR;
            } else {  
              for (Trip trip : trips) {
                if (trip.getName().toLowerCase().contains(query.toLowerCase())) {
                  filteredTrips.add(trip);
                }
              }
            }

            showTripListScreen(filteredTrips, titleString, event.isHistoryEvent());

            final Map<String, String[]> listParamMap = new HashMap<String, String[]>();
            listParamMap.put(Page.PAGE, new String[] {"" + Page.PAGE_FILTERED_TRIP_LIST});
            listParamMap.put(Page.SEARCH_QUERY, new String[] {event.getSearchTripQuery()});
            History.newItem(createTokenHash(listParamMap), false);
          }
        });

    eventBus.addHandler(ShowTripListEvent.getType(), new ShowTripListEventHanlder() {
      @Override
      public void onShowTripList(ShowTripListEvent event) {

        showTripListScreen(tripService.getTrips(), Constants.MY_TRIPS_STR, event.isHistoryEvent());

        Map<String, String[]> listParamMap = new HashMap<String, String[]>();
        listParamMap.put(Page.PAGE, new String[] {"" + Page.PAGE_TRIP_LIST});
        History.newItem(createTokenHash(listParamMap), false);
      }
    });

    eventBus.addHandler(ShowTripScheduleEvent.getType(), new ShowTripScheduleEventHandler() {
      @Override
      public void onShowTripSchedule(ShowTripScheduleEvent event) {
        showTripScheduleScreen(event);
      }
    });

    eventBus.addHandler(ShowTripOnMapEvent.getType(), new ShowTripOnMapEventHandler() {
      @Override
      public void onShowTripOnMap(ShowTripOnMapEvent event) {
        showTripOnMapScreen(event);
      }
    });

    eventBus.addHandler(ShareTripEvent.getType(), new ShareTripEventHandler() {
      @Override
      public void onShareTrip(ShareTripEvent event) {
        showTripShareScreen(event);
      }
    });

    eventBus.addHandler(ShowTripItemDetailsEvent.getType(), new ShowTripItemDetailsEventHandler() {
      @Override
      public void onShowTripItemDetails(ShowTripItemDetailsEvent event) {
        showTripItemDetailsScreen(
            singletonComponents.getTripItemService().getTripItem(event.getTripItem().getKey()), 
            event.isHistoryEvent());
      }
    });
   
    eventBus.addHandler(SearchQueryEvent.getType(), new SearchQueryEventHandler() {
      @Override
      public void onSearchRequest(SearchQueryEvent event) {
        showSearchResultsMapScreen(event);
      }
    });

    eventBus.addHandler(ShowSearchResultsOnMapEvent.getType(),
        new ShowSearchResultsOnMapEventHandler() {
          @Override
          public void onShowSearchResultsOnMap(ShowSearchResultsOnMapEvent event) {
            showSearchResultsMapScreen(event);
          }
        });

    eventBus.addHandler(ShowSearchResultsInListEvent.getType(),
        new ShowSearchResultsInListEventHandler() {
          @Override
          public void onShowSearchResultsInList(ShowSearchResultsInListEvent event) {
            showSearchResultsListScreen(event);
          }
        });

    eventBus.addHandler(ShowSearchItemDetailsEvent.getType(),
        new ShowSearchItemDetailsEventHandler() {
          @Override
          public void onShowSearchDetails(ShowSearchItemDetailsEvent event) {
            showSearchItemDetailsScreen(event);
          }
        });

       eventBus.addHandler(ShowTripItemCommentsEvent.getType(),
        new ShowTripItemCommentsEventHandler() {
          @Override
          public void onShowTripItemComments(ShowTripItemCommentsEvent event) {
            showTripItemCommentsScreen(event);
          }
        });
  }

  /**
   * Show home screen
   */
  private void showHomeScreen(ShowHomePageEvent event) {
    boolean firstDisplay = false;
    if (homeScreen == null) {
      final Provider<TripListFilterPresenter> filterProvider =
          new TripListFilterProvider(singletonComponents);
      final Provider<LogOutOptionsPresenter> tripListHeaderOptionsProvider =
        new LogOutOptionsProvider(singletonComponents, filterProvider);
      final Provider<HomeHeaderPresenter> tripListHeaderProvider =
          new HomeHeaderProvider(singletonComponents, tripListHeaderOptionsProvider);
      final HomeScreenPresenter.Display homeView = new HomeScreenView();
      final FooterPresenterProvider footerPresenterProvider = 
          new FooterPresenterProvider(singletonComponents);

      homeScreen =
          new HomeScreenPresenter(homeView, tripListHeaderProvider, new HomePanelProvider(
              singletonComponents), footerPresenterProvider);
      firstDisplay = true;
    }

    List<Trip> activeTrips = new ArrayList<Trip>();
    final Date currentDate = new Date();
    for (final Trip trip : tripService.getTrips()) {
      final Date tripStartDate = trip.getStartDate();
      final Date tripEndDate =
          singletonComponents.getUtils().addDaysToDate(tripStartDate, trip.getDuration());
      if (tripEndDate != null && tripEndDate.after(currentDate)) {
        activeTrips.add(trip);
      }
    }
    
    if (firstDisplay || !event.isHistoryEvent()) {
      homeScreen.release();
      homeScreen.setTrips(tripService.getTrips());
      homeScreen.bind();
    }

    RootPanel.get().clear();
    RootPanel.get().add(homeScreen.getDisplay().asWidget());

    Map<String, String[]> listParamMap = new HashMap<String, String[]>();
    listParamMap.put(Page.PAGE, new String[] {"" + Page.PAGE_HOME});
    History.newItem(createTokenHash(listParamMap), false);
  }

  /**
   * Show create trip screen
   */
  private void showCreateTripScreen(ShowCreateTripEvent event) {
    boolean firstDisplay = false;
    if (createTripScreenPresenter == null) {
      final CreateNewTripScreenPresenter.Display newTripView = new CreateNewTripScreenView();
      final HasGeocoder geocoder = new Geocoder();
      final Provider<HasGeocoderRequest> geocoderRequestProvider = new HasGeocoderRequestProvider();
      final Provider<TripListFilterPresenter> filterProvider =
          new TripListFilterProvider(singletonComponents);
      final Provider<BaseHeaderPresenter> tripListHeaderProvider =
          new BaseHeaderProvider(singletonComponents);
      final CreateNewTripProvider newTripProvider =
          new CreateNewTripProvider(singletonComponents, geocoder, geocoderRequestProvider);
      final Provider<LogOutOptionsPresenter> tripListHeaderOptionsProvider =
          new LogOutOptionsProvider(singletonComponents, filterProvider);

      createTripScreenPresenter =
              new CreateNewTripScreenPresenter(newTripView, tripListHeaderProvider,
                      newTripProvider, tripListHeaderOptionsProvider);
      firstDisplay = true;
    }
    
    if (firstDisplay || !event.isHistoryEvent()) {
      createTripScreenPresenter.release();
      createTripScreenPresenter.bind();
      createTripScreenPresenter.populateView();
    }

    Map<String, String[]> listParamMap = new HashMap<String, String[]>();
    listParamMap.put(Page.PAGE, new String[] {"" + Page.PAGE_CREATE_TRIP});
    History.newItem(createTokenHash(listParamMap), false);

    RootPanel.get().clear();
    RootPanel.get().add(createTripScreenPresenter.getDisplay().asWidget());
  }

  private void showTripListScreen(final List<Trip> trips, final String titleString, 
      final boolean isHistoryEvent) {
    boolean firstDisplay = false;
    if (triplistScreen == null) {
      final TripListScreenPresenter.Display homeView = new TripListScreenView();
      final Provider<TripListFilterPresenter> filterProvider =
          new TripListFilterProvider(singletonComponents);
      
      final Provider<BaseHeaderPresenter> tripListHeaderProvider =
          new BaseHeaderProvider(singletonComponents);
      final Provider<MenuItemPresenter> menuItemProvider = new MenuItemProvider();
      final Provider<MenuPresenter> menuProvider = new MenuProvider(menuItemProvider);
      
      final Provider<SearchBarPresenter> searchProvider =
        new SearchBarProvider(singletonComponents);
      final Provider<TripEditPopupPresenter> tripEditProvider =
          new TripEditPopupProvider(singletonComponents);
      final Provider<TripOptionsPresenter> tripHeaderOptionsProvider =
          new TripOptionsProvider(null, singletonComponents, searchProvider, tripEditProvider,
              menuProvider);
      
      final Provider<TripListItemPresenter> tripItemProvider =
        new TripListItemPresenterProvider(singletonComponents, tripHeaderOptionsProvider);
      final Provider<TripListOptionsPresenter> tripListHeaderOptionsProvider =
          new TripListOptionsProvider(singletonComponents, filterProvider, menuProvider);

      triplistScreen =
          new TripListScreenPresenter(homeView, tripListHeaderProvider,
              new TripListPresenterProvider(singletonComponents, tripItemProvider),
              tripListHeaderOptionsProvider);
      firstDisplay = true;
    }
    
    if (firstDisplay || !isHistoryEvent) {
      triplistScreen.release();
      triplistScreen.setTrips(trips, titleString);
      triplistScreen.bind();
    }

    RootPanel.get().clear();
    RootPanel.get().add(triplistScreen.getDisplay().asWidget());
  }

  /**
   * Show trip schedule screen
   */
  private void showTripScheduleScreen(ShowTripScheduleEvent event) {
    boolean firstDisplay = false;
    if (tripDetailsScreenPresenter == null) {
      final TripDetailsScreenPresenter.Display screenDisplay = new TripDetailsScreenView();
      final Provider<SearchBarPresenter> searchProvider =
          new SearchBarProvider(singletonComponents);
      
      final Provider<MenuItemPresenter> menuItemProvider = new MenuItemProvider();
      final Provider<MenuPresenter> menuProvider = new MenuProvider(menuItemProvider);
      final Provider<DayPopupPresenter> dayPopupProvider = new DayPopupProvider();
      final Provider<TripItemOptionsPresenter> tripItemOptionsPresenterProvider =
          new TripItemOptionsProvider(null, singletonComponents, searchProvider, menuProvider,
              menuItemProvider, dayPopupProvider);
          
      final Provider<TripItemPresenter> tripItemProvider = 
        new TripItemProvider(ViewType.LIST, singletonComponents, tripItemOptionsPresenterProvider);
      final Provider<DayListItemPresenter> dayListItemProvider = 
        new DayListItemProvider(singletonComponents, searchProvider);
      final Provider<DayListPresenter> dayListProvider = 
        new DayListProvider(singletonComponents, dayListItemProvider,
            tripItemOptionsPresenterProvider);
      
      final Provider<BaseHeaderPresenter> headerProvider =
          new BaseHeaderProvider(singletonComponents);
      final Provider<TripEditPopupPresenter> tripEditProvider =
          new TripEditPopupProvider(singletonComponents);
      final Provider<TripOptionsPresenter> tripHeaderOptionsProvider =
          new TripOptionsProvider(null, singletonComponents, searchProvider, tripEditProvider,
              menuProvider);
      tripDetailsScreenPresenter =
          new TripDetailsScreenPresenter(screenDisplay, headerProvider, dayListProvider,
              tripHeaderOptionsProvider);
      firstDisplay = true;
    }

    final Trip trip = singletonComponents.getTripService().getTrip(event.getTrip().getKey());
    if (firstDisplay || !event.isHistoryEvent()) {
      tripDetailsScreenPresenter.release();
      tripDetailsScreenPresenter.setTrip(trip);
      tripDetailsScreenPresenter.bind();
    }

    Map<String, String[]> listParamMap = new HashMap<String, String[]>();
    listParamMap.put(Page.PAGE, new String[] {"" + Page.PAGE_TRIP_DETAILS});
    listParamMap.put(Page.TRIP_ID, new String[] {"" + trip.getKey()});
    History.newItem(createTokenHash(listParamMap), false);

    RootPanel.get().clear();
    RootPanel.get().add(tripDetailsScreenPresenter.getDisplay().asWidget());
      
    // On itinerary view, (LP/not LP) always start search dialog if no trip
    // items.
    List<TripItem> allItems = singletonComponents.getTripItemService().getTripItems(trip.getKey());
    if (allItems.size() == 0) {
      SearchBarPresenter searchBarpresenter = new SearchBarProvider(singletonComponents).get();
      searchBarpresenter.setTrip(trip, Constants.NO_SELECTED_DAY, null);
      searchBarpresenter.release();
      searchBarpresenter.bind();
      searchBarpresenter.showPopup();
    }
//    // TODO: Remove delay related fix.
//    if (event.isNewTrip()) {
//      if (firstDisplay) {
//        final int delay = 300;
//        final Timer timer = new Timer() {
//          @Override
//          public void run() {
//            tripDetailsScreenPresenter.showEditPopUp(true);
//          }
//        };
//        timer.schedule(delay);
//        firstDisplay = false;
//      } else {
//        tripDetailsScreenPresenter.showEditPopUp(true);
//      }
//    }
    // Do not show edit trip after create trip.
    tripDetailsScreenPresenter.showEditPopUp(false);
  }

  /**
   * Show trip map screen
   */
  private void showTripOnMapScreen(ShowTripOnMapEvent event) {
    boolean firstDisplay = false;
    if (tripMapPresenter == null) {
      TripMapPresenter.Display tripMapView = new TripMapView();

      final Provider<SearchBarPresenter> searchProvider =
          new SearchBarProvider(singletonComponents);
      final Provider<BaseHeaderPresenter> headerProvider =
          new BaseHeaderProvider(tripMapView.getHeaderDisplay(), singletonComponents);
      final Provider<MapPresenter> mapProvider = new MapProvider(tripMapView.getMapDisplay());
      
      final Provider<MenuItemPresenter> menuItemProvider = new MenuItemProvider();
      final Provider<MenuPresenter> menuProvider = new MenuProvider(menuItemProvider);
      final Provider<DayPopupPresenter> dayPopupProvider = new DayPopupProvider();
      final Provider<TripItemOptionsPresenter> tripItemOptionsPresenterProvider =
          new TripItemOptionsProvider(null, singletonComponents, searchProvider, menuProvider,
              menuItemProvider, dayPopupProvider);

      final Provider<TripItemPresenter> tripItemProvider =
          new TripItemProvider(tripMapView.getInfoDisplay(), singletonComponents,
              tripItemOptionsPresenterProvider);
      final Provider<TripEditPopupPresenter> tripEditProvider =
          new TripEditPopupProvider(singletonComponents);
      final Provider<TripOptionsPresenter> tripHeaderOptionsProvider =
          new TripOptionsProvider(tripMapView.getHeaderOptionsDisplay(), singletonComponents,
              searchProvider, tripEditProvider, menuProvider);
      tripMapPresenter =
          new TripMapPresenter(tripMapView, singletonComponents, headerProvider, mapProvider,
              tripItemProvider, tripEditProvider, tripHeaderOptionsProvider);
      firstDisplay = true;
    }
    RootPanel.get().clear();
    RootPanel.get().add(tripMapPresenter.getDisplay().asWidget());

    final Trip trip = singletonComponents.getTripService().getTrip(event.getTrip().getKey());
    final int tripDay = event.getDay();
    if (firstDisplay || !event.isHistoryEvent()) {
      tripMapPresenter.release();
      tripMapPresenter.setTrip(trip, tripDay);
      tripMapPresenter.bind();
    }
    
    Map<String, String[]> listParamMap = new HashMap<String, String[]>();
    listParamMap.put(Page.PAGE, new String[] {"" + Page.PAGE_TRIP_MAP});
    listParamMap.put(Page.TRIP_DAY, new String[] {"" + tripDay});
    listParamMap.put(Page.TRIP_ID, new String[] {"" + trip.getKey()});
    
    String itemKey = event.getTripItemId();
    if (itemKey != null) {
      tripMapPresenter.setSelectedTripItem(itemKey);
      listParamMap.put(Page.TRIP_ITEM_ID, new String[] {"" + itemKey});
    }
    History.newItem(createTokenHash(listParamMap), false);
  }

  /**
   * Show trip item details screen
   */
  private void showTripItemDetailsScreen(final TripItem tripItem, final boolean isHistoryEvent) {
    boolean firstDisplay = false;
    if (tripItemDetailsScreen == null) {
      final TripItemDetailsScreenPresenter.Display itemDetailsView =
          new TripItemDetailsScreenView();
      final Provider<SearchBarPresenter> searchProvider =
          new SearchBarProvider(singletonComponents);
      final Provider<BaseHeaderPresenter> headerProvider =
          new BaseHeaderProvider(singletonComponents);

      final Provider<MenuItemPresenter> menuItemProvider = new MenuItemProvider();
      final Provider<MenuPresenter> menuProvider = new MenuProvider(menuItemProvider);
      final Provider<DayPopupPresenter> dayPopupProvider = new DayPopupProvider();
      final Provider<TripItemOptionsPresenter> headerOptionsProvider =
          new TripItemOptionsProvider(null, singletonComponents, searchProvider, menuProvider,
              menuItemProvider, dayPopupProvider);
      final Provider<TripEditPopupPresenter> tripEditProvider =
          new TripEditPopupProvider(singletonComponents);

      tripItemDetailsScreen =
              new TripItemDetailsScreenPresenter(itemDetailsView, headerProvider,
                      new TripItemProvider(TripItemProvider.ViewType.DETAILS, singletonComponents,
                          headerOptionsProvider),
                      tripEditProvider, headerOptionsProvider);
      firstDisplay = true;
    }
    
    if (firstDisplay || !isHistoryEvent) {
      tripItemDetailsScreen.release();
      tripItemDetailsScreen.setTripItem(tripService.getTrip(tripItem.getTripId()), tripItem);
      tripItemDetailsScreen.bind();
    }

    Map<String, String[]> listParamMap = new HashMap<String, String[]>();
    listParamMap.put(Page.PAGE, new String[] {"" + Page.PAGE_TRIP_ITEM_DETAILS});
    listParamMap.put(Page.TRIP_ID, new String[] {"" + tripItem.getTripId()});
    listParamMap.put(Page.TRIP_ITEM_ID, new String[] {"" + tripItem.getKey()});
    History.newItem(createTokenHash(listParamMap), false);

    RootPanel.get().clear();
    RootPanel.get().add(tripItemDetailsScreen.getDisplay().asWidget());
  }

  /**
   * Show search results map screen - using ShowSearchResultsOnMapEvent event
   */
  private void showSearchResultsMapScreen(final ShowSearchResultsOnMapEvent event) {
    final Trip trip = singletonComponents.getTripService().getTrip(event.getTrip().getKey());
    final int day = event.getTripDay();
    final String key = event.getSearchResultsKey();
    final SearchType type =
        (searchService.extractKeyProperty(key, Page.QUERY_TYPE)).equals(SearchType.GOOGLE
            .toString()) ? SearchType.GOOGLE : SearchType.LP;
    final String query = searchService.extractKeyProperty(key, Page.SEARCH_QUERY);
    final HasLatLngBounds searchBounds =
        searchService.stringToBounds(searchService.extractKeyProperty(key, Page.SEARCH_BOUNDS));

    toast.showLoading(messages.searching(type.equals(SearchType.GOOGLE) ? query
        : POIType.getDisplayString(query)));
    searchService.search(type, query, searchBounds, trip.getLocation(), new SearchResultsListener() {

      @Override
      public void onSuccess(List<SearchItem> results) {
        toast.hideLoading();
        showSearchResultsInMap(trip, day, results, key, event.isHistoryEvent());
      }

      @Override
      public void onFailure(Throwable caught) {
        toast.hideLoading();
        toast.showToast(messages.searchError());
        //showSearchResultsInMap(trip, day, new ArrayList<SearchItem>(), key);
      }
    });
  }

  private void showSearchResultsMapScreen(final SearchQueryEvent event) {
    final Trip trip = singletonComponents.getTripService().getTrip(event.getTrip().getKey());
    final int day = event.getTripDay();
    final String query = event.getQuery();
    final SearchType type = event.getSearchType();
    HasLatLngBounds searchBounds = event.getSearchBounds();

    // Always search in trip bounds only issue 2927318.
//    if (searchBounds == null) {
      searchBounds =
          new LatLngBounds(new LatLng(trip.getSouthLatitude(), trip.getWestLongitude()),
              new LatLng(trip.getNorthLatitude(), trip.getEastLongitude()));
//    }
    toast.showLoading(messages.searching(type.equals(SearchType.GOOGLE) ? query
        : POIType.getDisplayString(query)));
    final HasLatLngBounds finalBounds = searchBounds;
    searchService.search(type, query, finalBounds, trip.getLocation(), new SearchResultsListener() {
      @Override
      public void onSuccess(List<SearchItem> results) {
        toast.hideLoading();
        showSearchResultsInMap(trip, day, results, searchService.getResultsCacheKey(type, query,
            finalBounds), event.isHistoryEvent());
      }

      @Override
      public void onFailure(Throwable caught) {
        toast.hideLoading();
        toast.showToast(messages.searchError());
//        showSearchResultsInMap(trip, day, new ArrayList<SearchItem>(), searchService
//            .getResultsCacheKey(type, query, finalBounds));
      }
    });
  }

  /**
   * Show search results in map.
   */
  private void showSearchResultsInMap(final Trip trip, final int day,
      final List<SearchItem> results, final String key, final boolean isHistoryEvent) {
    boolean firstDisplay = false;
    if (searchResultsMapPresenter == null) {

      final SearchResultsMapPresenter.Display searchResultsMapView = new SearchResultsMapView();
      final Provider<SearchBarPresenter> searchProvider =
          new SearchBarProvider(singletonComponents);
      final Provider<BaseHeaderPresenter> headerProvider =
          new BaseHeaderProvider(searchResultsMapView.getHeaderDisplay(), singletonComponents);
      final Provider<MapPresenter> mapProvider =
          new MapProvider(searchResultsMapView.getMapDisplay());
      final Provider<SearchResultItemPresenter> searchResultsItemProvider =
              new SearchResultItemProvider(searchResultsMapView.getInfoDisplay(),
                      singletonComponents, new AddItemPopupProvider());

      final Provider<MenuItemPresenter> menuItemProvider = new MenuItemProvider();
      final Provider<MenuPresenter> menuProvider = new MenuProvider(menuItemProvider);
      final Provider<SearchResultsOptionsPresenter> headerOptionsProvider =
          new SearchResultsOptionsProvider(searchResultsMapView.getHeaderOptionsDisplay(),
              singletonComponents, searchProvider, menuProvider);
      final Provider<TripEditPopupPresenter> tripEditProvider =
          new TripEditPopupProvider(singletonComponents);

      searchResultsMapPresenter =
          new SearchResultsMapPresenter(searchResultsMapView, singletonComponents,
              headerProvider, mapProvider, searchResultsItemProvider, tripEditProvider,
              headerOptionsProvider);
      firstDisplay = true;
    }

    RootPanel.get().clear();
    RootPanel.get().add(searchResultsMapPresenter.getDisplay().asWidget());

    if (firstDisplay || !isHistoryEvent) {
      searchResultsMapPresenter.release();
      searchResultsMapPresenter.setResults(trip, day, results, key);
      searchResultsMapPresenter.bind();
    }

    Map<String, String[]> listParamMap = new HashMap<String, String[]>();
    listParamMap.put(Page.PAGE, new String[] {"" + Page.PAGE_SEARCH_RESULT_MAP});
    listParamMap.put(Page.TRIP_ID, new String[] {"" + trip.getKey()});
    listParamMap.put(Page.TRIP_DAY, new String[] {"" + day});
    listParamMap.put(Page.SEARCH_RESULTS_KEY, new String[] {"" + key});

    History.newItem(createTokenHash(listParamMap), false);


  }

  /**
   * Show search results in list
   */
  private void showSearchResultsListScreen(final ShowSearchResultsInListEvent event) {
    final Trip trip = singletonComponents.getTripService().getTrip(event.getTrip().getKey());
    final int day = event.getTripDay();
    final String key = event.getSearchResultsKey();
    final SearchType type =
        (searchService.extractKeyProperty(key, Page.QUERY_TYPE)).equals(SearchType.GOOGLE
            .toString()) ? SearchType.GOOGLE : SearchType.LP;
    final String query = searchService.extractKeyProperty(key, Page.SEARCH_QUERY);
    final HasLatLngBounds searchBounds =
        searchService.stringToBounds(searchService.extractKeyProperty(key, Page.SEARCH_BOUNDS));

    toast.showLoading(messages.searching(type.equals(SearchType.GOOGLE) ? query
        : POIType.getDisplayString(query)));
    searchService.search(type, query, searchBounds, trip.getLocation(), new SearchResultsListener() {
      @Override
      public void onSuccess(List<SearchItem> results) {
        toast.hideLoading();
        showSearchResultInList(trip, day, results, key, event.isHistoryEvent());
      }

      @Override
      public void onFailure(Throwable caught) {
        toast.hideLoading();
        toast.showToast(messages.searchError());
//        showSearchResultInList(trip, day, new ArrayList<SearchItem>(), key);
      }
    });
  }

  private void showSearchResultInList(final Trip trip, final int day,
      final List<SearchItem> results, final String key, final boolean isHistoryEvent) {
    boolean firstDisplay = false;
    if (searchResultsListScreenPresenter == null) {
      final SearchResultsListScreenPresenter.Display searchListScreenDisplay =
          new SearchResultsListScreenView();
      SearchResultsListPresenter.Display listView = new SearchResultsListView();
      Provider<SearchResultItemPresenter> searchResultsItemProvider =
              new SearchResultItemProvider(SearchResultItemProvider.ViewType.LIST,
                      singletonComponents, new AddItemPopupProvider());

      final Provider<SearchBarPresenter> searchProvider =
          new SearchBarProvider(singletonComponents);
      final Provider<BaseHeaderPresenter> headerProvider =
          new BaseHeaderProvider(singletonComponents);
      final Provider<MenuItemPresenter> menuItemProvider = new MenuItemProvider();
      final Provider<MenuPresenter> menuProvider = new MenuProvider(menuItemProvider);
      final Provider<SearchResultsOptionsPresenter> headerOptionsProvider =
              new SearchResultsOptionsProvider(null, singletonComponents, searchProvider,
                      menuProvider);
      Provider<SearchResultsListPresenter> searchResultsListProvider =
          new SearchResultsListProvider(singletonComponents, searchResultsItemProvider);
      final Provider<TripEditPopupPresenter> tripEditProvider =
          new TripEditPopupProvider(singletonComponents);

      searchResultsListScreenPresenter =
          new SearchResultsListScreenPresenter(searchListScreenDisplay, headerProvider,
              searchResultsListProvider, tripEditProvider, headerOptionsProvider);
      firstDisplay = true;
    }

    RootPanel.get().clear();
    RootPanel.get().add(searchResultsListScreenPresenter.getDisplay().asWidget());

    if (firstDisplay || !isHistoryEvent) {
      searchResultsListScreenPresenter.release();
      searchResultsListScreenPresenter.setResults(trip, day, results, key);
      searchResultsListScreenPresenter.bind();
    }

    Map<String, String[]> listParamMap = new HashMap<String, String[]>();
    listParamMap.put(Page.PAGE, new String[] {"" + Page.PAGE_SEARCH_RESULT_LIST});
    listParamMap.put(Page.TRIP_ID, new String[] {"" + trip.getKey()});
    listParamMap.put(Page.TRIP_DAY, new String[] {"" + day});
    listParamMap.put(Page.SEARCH_RESULTS_KEY, new String[] {"" + key});

    History.newItem(createTokenHash(listParamMap), false);


  }

  /**
   * Show search result item details
   */
  private void showSearchItemDetailsScreen(final ShowSearchItemDetailsEvent event) {
    toast.showToast(messages.loading());
    searchService.getSearchItem(event.getSearchItemId(), event.getTrip().getLocation(), new SearchItemListener() {

      @Override
      public void onSuccess(SearchItem searchItem) {
        toast.hideToast();
        showSearchItemDetails(searchItem, 
            singletonComponents.getTripService().getTrip(event.getTrip().getKey()), event.getDay(), 
            event.isHistoryEvent());
      }

      @Override
      public void onFailure(Throwable caught) {
        GWT.log(caught.getMessage());
        toast.showToast(messages.searchError());
      }
    });
  }

  private void showSearchItemDetails(final SearchItem item, final Trip trip, int day, 
      final boolean isHistoryEvent) {

    boolean firstDisplay = false;
    if (searchItemScreen == null) {
      SearchResultItemScreenPresenter.Display searchItemView = new SearchResultItemScreenView();
      final Provider<SearchResultItemPresenter> searchResultProvider =
          new SearchResultItemProvider(SearchResultItemProvider.ViewType.DETAILS,
              singletonComponents, new AddItemPopupProvider());
      final Provider<SearchBarPresenter> searchProvider =
          new SearchBarProvider(singletonComponents);
      final Provider<BaseHeaderPresenter> headerProvider =
          new BaseHeaderProvider(singletonComponents);

      final Provider<MenuItemPresenter> menuItemProvider = new MenuItemProvider();
      final Provider<MenuPresenter> menuProvider = new MenuProvider(menuItemProvider);
      final Provider<SearchResultsOptionsPresenter> headerOptionsProvider =
          new SearchResultsOptionsProvider(null, singletonComponents, searchProvider, 
              menuProvider);
      final Provider<TripEditPopupPresenter> tripEditProvider =
          new TripEditPopupProvider(singletonComponents);

      searchItemScreen =
              new SearchResultItemScreenPresenter(searchItemView, headerProvider,
                      searchResultProvider, tripEditProvider, headerOptionsProvider);
      firstDisplay = true;
    }
    
    if (firstDisplay || !isHistoryEvent) {
      searchItemScreen.release();
      searchItemScreen.setSearchItem(item, trip, day);
      searchItemScreen.bind();
    }

    Map<String, String[]> listParamMap = new HashMap<String, String[]>();
    listParamMap.put(Page.PAGE, new String[] {"" + Page.PAGE_SEARCH_ITEM_DETAILS});
    listParamMap.put(Page.SEARCH_ITEM_ID, new String[] {"" + item.getId()});
    listParamMap.put(Page.TRIP_ID, new String[] {"" + trip.getKey()});
    listParamMap.put(Page.TRIP_DAY, new String[] {"" + day});
    History.newItem(createTokenHash(listParamMap), false);

    RootPanel.get().clear();
    RootPanel.get().add(searchItemScreen.getDisplay().asWidget());
  }

  /**
   * Show comments for a trip item
   */
  private void showTripItemCommentsScreen(ShowTripItemCommentsEvent event) {

    boolean firstDisplay = false;
    if (commentsScreen == null) {
      final Provider<CommentAddPresenter> commentAddPresenterProvider =
          new CommentAddPresenterProvider(singletonComponents);
      final Provider<CommentListItemPresenter> itemPresenterProvider =
          new CommentListItemPresenterProvider(singletonComponents);
      final Provider<CommentsPresenter> listPresenterProvider =
          new CommentsPresenterProvider(singletonComponents, itemPresenterProvider);
      final Provider<SearchBarPresenter> searchProvider =
          new SearchBarProvider(singletonComponents);
      final Provider<BaseHeaderPresenter> headerProvider =
          new BaseHeaderProvider(singletonComponents);
      final TripItemCommentsScreenPresenter.Display itemCommentsView =
          new TripItemCommentsScreenView();

      final Provider<MenuItemPresenter> menuItemProvider = new MenuItemProvider();
      final Provider<MenuPresenter> menuProvider = new MenuProvider(menuItemProvider);
      final Provider<DayPopupPresenter> dayPopupProvider = new DayPopupProvider();
      final Provider<TripItemOptionsPresenter> headerOptionsProvider =
          new TripItemOptionsProvider(null, singletonComponents, searchProvider, menuProvider,
              menuItemProvider, dayPopupProvider);

      commentsScreen =
          new TripItemCommentsScreenPresenter(itemCommentsView, headerProvider,
              commentAddPresenterProvider, listPresenterProvider, 
              headerOptionsProvider);
      firstDisplay = true;
    }
    final TripItem tripItem = singletonComponents.getTripItemService().getTripItem(
        event.getTripItem().getKey());
    
    if (firstDisplay || !event.isHistoryEvent()) {
      commentsScreen.release();
      commentsScreen.setTripItem(tripService.getTrip(tripItem.getTripId()), tripItem);
      commentsScreen.bind();
    }

    Map<String, String[]> listParamMap = new HashMap<String, String[]>();
    listParamMap.put(Page.PAGE, new String[] {"" + Page.PAGE_TRIP_ITEM_COMMENTS});
    listParamMap.put(Page.TRIP_ID, new String[] {"" + tripItem.getTripId()});
    listParamMap.put(Page.TRIP_ITEM_ID, new String[] {"" + tripItem.getKey()});
    History.newItem(createTokenHash(listParamMap), false);

    RootPanel.get().clear();
    RootPanel.get().add(commentsScreen.getDisplay().asWidget());
  }

  /**
   * Show Trip share screen.
   * 
   */
  private void showTripShareScreen(ShareTripEvent event) {
    boolean firstDisplay = false;
    if (tripShareScreenPresenter == null) {
      final Provider<TripSharePresenter> tripShareProvider =
          new TripShareProvider(singletonComponents, remoteTripService);
      final TripShareScreenView display = new TripShareScreenView();
      final Provider<TripListFilterPresenter> filterProvider =
          new TripListFilterProvider(singletonComponents);
      final Provider<BaseHeaderPresenter> tripListHeaderProvider =
          new BaseHeaderProvider(singletonComponents);
      final Provider<LogOutOptionsPresenter> tripListHeaderOptionsProvider =
              new LogOutOptionsProvider(singletonComponents, filterProvider);
      final Provider<TripEditPopupPresenter> tripEditProvider =
          new TripEditPopupProvider(singletonComponents);

      tripShareScreenPresenter =
              new TripShareScreenPresenter(display, tripListHeaderProvider, tripShareProvider,
                  tripEditProvider, tripListHeaderOptionsProvider);
      firstDisplay = true;
    }
    final Trip trip = singletonComponents.getTripService().getTrip(event.getTrip().getKey());
    
    if (firstDisplay || !event.isHistoryEvent()) {
      tripShareScreenPresenter.release();
      tripShareScreenPresenter.setTrip(trip);
      tripShareScreenPresenter.bind();
    }

    Map<String, String[]> listParamMap = new HashMap<String, String[]>();
    listParamMap.put(Page.PAGE, new String[] {"" + Page.PAGE_TRIP_SHARE});
    listParamMap.put(Page.TRIP_ID, new String[] {"" + trip.getKey()});
    History.newItem(createTokenHash(listParamMap), false);

    RootPanel.get().clear();
    RootPanel.get().add(tripShareScreenPresenter.getDisplay().asWidget());
  }

  /**
   * Create history token
   * 
   */
  private static String createTokenHash(final Map<String, String[]> listParamMap) {
    final StringBuffer url = new StringBuffer();
    char prefix = '?';
    for (final Map.Entry<String, String[]> entry : listParamMap.entrySet()) {
      for (final String val : entry.getValue()) {
        url.append(prefix).append(entry.getKey()).append('=');
        if (val != null) {
          url.append(val);
        }
        prefix = '&';
      }
    }
    return url.toString();
  }
}
