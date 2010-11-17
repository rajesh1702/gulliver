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

package com.google.mobile.trippy.web.client.i18n;

import com.google.gwt.i18n.client.Messages;

/**
 * A compile-time binding of messages supplied from various sources.
 * 
 */
public interface Message extends Messages {

  //Search related messages
  @DefaultMessage("Searching {0}...")
  String searching(String searchType);
  
  @DefaultMessage("No results found for \"{0}\" near {1}")
  String noSearchResults(String searchQuery, String location);
  
  @DefaultMessage("Trippy could not handle your request.")
  String searchError();
  
  //Data storage related messages
  @DefaultMessage("Writes to remote server failed")
  String remoteWriteFailed();
  
  @DefaultMessage("Trip created.")
  String tripCreated();
  
  @DefaultMessage("Trip creation failed.")
  String tripCreationFailed();
  
  @DefaultMessage("Inivitation not sent !")
  String errorInvitation();
  
  @DefaultMessage("Could not save trip. Please try again later.")
  String tripSaveFailed();

  @DefaultMessage("Trip updated.")
  String tripModifiedMsg();
  
  @DefaultMessage("Trip deleted,")
  String tripDeleted();

  @DefaultMessage("Could not delete trip. Please try again later.")
  String tripDeletionFailed();

  @DefaultMessage("Unscheduled trip item added.")
  String tripItemAddedUnschedule();

  @DefaultMessage("Trip item added to day {0}.")
  String tripItemAdded(int str);

  @DefaultMessage("Trip item updated.")
  String tripItemModified();
  
  @DefaultMessage("Could not save trip item. Please try again later.")
  String tripItemSaveFailed();
  
  @DefaultMessage("Trip item deleted.")
  String tripItemDeleted();

  @DefaultMessage("Could not delete trip item. Please try again later.")
  String tripItemDeletionFailed();

  @DefaultMessage("Comment added.")
  String commentAdded();

  @DefaultMessage("Could not add comment. Please try again later.")
  String commentAddFailed();
  
  @DefaultMessage("Comment deleted.")
  String commentDeleted();

  @DefaultMessage("Could not delete comment. Please try again later.")
  String commentDeletionFailed();

  @DefaultMessage("Could not sync data from remote DB")
  String syncFailed();
  
  @DefaultMessage("No locations found.")
  String noTripLocations();
  
  /**
   * Authorization related messages
   */
  @DefaultMessage("Not authorized to add {0}")
  String unauthorizedAdd(String content);

  @DefaultMessage("Not authorized to edit {0}")
  String unauthorizedEdit(String content);

  @DefaultMessage("Not authorized to delete {0}")
  String unauthorizedDelete(String content);

  @DefaultMessage("Not authorized to view {0}")
  String unauthorizedView(String content);
  
  @DefaultMessage("Not authorized to share trip")
  String unauthorizedShare();

  /**
   * User-help toast messages
   */
  @DefaultMessage("Go online to do the operation.")
  String userOffline();
  
  @DefaultMessage("Comment already exists")
  String commentExists();

  @DefaultMessage("Please enter trip name.")
  String tripToSearch();
  
  @DefaultMessage("Please enter trip location.")
  String tripLocationEmpty();
  
  @DefaultMessage("Please specify trip duration.")
  String tripDurationEmpty();

  @DefaultMessage("Invalid trip duration")
  String tripDurationInvalid();

  @DefaultMessage("Invalid start date.")
  String tripStartDateInvalid();
  
  @DefaultMessage("One or more of the email addresses are invalid.")
  String errorMsgEmailIdNotCorrect();

  @DefaultMessage("Please enter email address !")
  String errorMsgEmailBoxEmpty();

  @DefaultMessage("Could not find location !")
  String locationNotFoundForTrip();
  
  @DefaultMessage("Do you want to delete trip {0} ?")
  String confirmTripDelete(String trip);

  @DefaultMessage("Do you want to delete trip item {0} ?")
  String confirmTripItemDelete(String tripItem);

  @DefaultMessage("Do you want to delete this comment ?")
  String confirmCommentDelete();

  @DefaultMessage("We are back online.")
  String onLineStatusMsg();

  @DefaultMessage("Creating trip...")
  String toastMsgCreatingTrip();

  @DefaultMessage("Resolving name ...")
  String toastMsgResolvingName();

  @DefaultMessage("Search to add items")
  String toastMsgSearchToAddItem();
  
  @DefaultMessage("We are in offline View only mode.")
  String offLineStatusMsg();
  
  @DefaultMessage("Invitation sent.")
  String invitationSent();

  @DefaultMessage("Thanks for your Vote.")
  String submitVoteMsg();
  
  @DefaultMessage("Loading...")
  String loading();

  @DefaultMessage("Current location unavailable.")
  String currentLocUnavailable();

  @DefaultMessage("Please enter trip location.")
  String errorMsgLocationEmpty();
  
  @DefaultMessage("Please select an item.")
  String errorMsgNoItemSelected();
  
  @DefaultMessage("Adding {0}...")
  String adding(String content);
  
  @DefaultMessage("Updating {0}...")
  String updating(String content);
  
  @DefaultMessage("Deleting {0}...")
  String deleting(String content);
  /**
   * Screen labels
   */

  @DefaultMessage("Back")
  String backButtonName();

  @DefaultMessage("I am here")
  String buttonUnvisited();

  @DefaultMessage("Visited")
  String buttonVisited();

  @DefaultMessage("Edit ")
  String editPageLabel();

  @DefaultMessage("Trip")
  String editTripLabel();

  @DefaultMessage("Item")
  String editItemLabel();

  @DefaultMessage("Page")
  String editPageText();

  @DefaultMessage("Unscheduled")
  String unscheduled();

  @DefaultMessage("Day {0} ({1})")
  String dayLabel(int dayNumber, String displayDate);

  @DefaultMessage("Select time")
  String selectTime();

  @DefaultMessage(" Move to ")
  String moveToAnotherDay();

  @DefaultMessage("Search")
  String searchLabel();

  @DefaultMessage("Search Trip")
  String searchTripLabel();

  @DefaultMessage("Create a Trip")
  String createTripLabel();

  @DefaultMessage("Save")
  String saveButtonName();

  @DefaultMessage("Trip Location")
  String editTripLocationLabel();

  @DefaultMessage("Create Trip")
  String createNewTripTitle();

  @DefaultMessage("Edit Trip")
  String editTripTitle();

  @DefaultMessage("Please enter some location !")
  String msgErrLocationEmpty();

  @DefaultMessage("Confimation")
  String confirmationAlertTitle();

  @DefaultMessage("Enter email addresses separated by commas.")
  String initialEmailTextAreaContent();

  @DefaultMessage("Search Google Maps")
  String initialSearchTextAreaContent();

  @DefaultMessage("Delete")
  String deleteButtonName();

  @DefaultMessage("Trip Not Found")
  String tripNotFoundErrorMsg();

  @DefaultMessage("Trip Item Not Found")
  String tripItemNotFoundErrorMsg();

  @DefaultMessage("List View")
  String listView();

  @DefaultMessage("Map View")
  String mapView();

  @DefaultMessage("Comments")
  String commentsTitle();

  @DefaultMessage("In Progress")
  String inProgress();
  
  @DefaultMessage("Add")
  String addToTrip();

  @DefaultMessage("Add To Trip")
  String addItemToTrip();

  @DefaultMessage("Remove")
  String removeFromTrip();

  @DefaultMessage("Remove from trip")
  String removeItemFromTrip();

  @DefaultMessage("More Info..")
  String moreinfoTitle();
  
   @DefaultMessage("Show Detail")
  String showDetail();
  
  @DefaultMessage("Hide Detail")
  String hideDetail();

  
  @DefaultMessage("More info on Lonely Planet...")
  String moreInfoOnLP();

  @DefaultMessage("More info on Google...")
  String moreInfoOnGoogle();

  @DefaultMessage("shared trip item")
  String sharedTripItem();

  @DefaultMessage("Please enter numeric data as duration.")
  String errorMsgNoDuration();
 
  @DefaultMessage("Trip do not exists anymore")
  String tripNotFound();
  
  @DefaultMessage("Trip is not in sync with the server. please refresh")
  String tripNotSynced();
  
  @DefaultMessage("No Lonely Planet results found, showing Google results.")
  String noLpResults();
}
