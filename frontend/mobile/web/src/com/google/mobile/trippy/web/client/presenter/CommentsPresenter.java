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
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.mobile.trippy.web.client.base.Provider;
import com.google.mobile.trippy.web.client.base.SingletonComponents;
import com.google.mobile.trippy.web.client.event.CommentAddedEvent;
import com.google.mobile.trippy.web.client.event.CommentAddedEventHandler;
import com.google.mobile.trippy.web.client.event.CommentDeletedEvent;
import com.google.mobile.trippy.web.client.event.CommentDeletedEventHandler;
import com.google.mobile.trippy.web.client.view.View;
import com.google.mobile.trippy.web.shared.models.Comment;
import com.google.mobile.trippy.web.shared.models.Trip;
import com.google.mobile.trippy.web.shared.models.TripItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Presenter for comments screen.
 * 
 * This presenter is responsible for filling a comment List view. It will fill
 * comment list by {@link CommentListItemPresenter}.  
 * 
 */
public class CommentsPresenter implements EventHandlerPresenter<CommentsPresenter.Display> {

  /**
   * View interface for comment List.
   */
  public interface Display extends View {
    public CommentListItemPresenter.Display newCommentListItemView();
    public void addCommentListItem(CommentListItemPresenter.Display listItemDisplay);
    public void removeCommentListItem(int index);
    public void clear();
  }
  
  /** Object to contain the reference of associated view. */
  private final Display display;
  
  /** Common container for all the singleton components of this application. */
  private final SingletonComponents provider;
  
  private final List<HandlerRegistration> handlers;
  private TripItem tripItem;
  private Trip trip;
  private ArrayList<Comment> displayCommentList = new ArrayList<Comment>();
  private Provider<CommentListItemPresenter> commentListItemPresenterProvider;
  
  public CommentsPresenter(Display display, SingletonComponents provider, 
      Provider<CommentListItemPresenter> commentListItemPresenterProvider) {
    this.display = display;
    this.provider = provider;
    this.commentListItemPresenterProvider = commentListItemPresenterProvider;
    this.handlers = new ArrayList<HandlerRegistration>();
  }
  
  /**
   * Method takes the responsibility for handling all the events and firing
   * the events. 
   * 
   * Event To Listen:
   * 1) {@link CommentAddedEvent}: It will listen comment add event from
   *  service, and updates the view accordingly. To update the view, It will 
   *  fetch Comment object from event and will call a method 
   *  addCommentToDisplay(Comment comment) with the retrieved object.
   *  
   * 2) {@link CommentDeletedEvent}: It will listen comment delete event from 
   * service, and updates the view accordingly. To update the view, it will 
   * fetch Comment Object from event and will call a method 
   * removeCommentFromDisplay(Comment comment) with the retrieved object.
   */
  @Override
  public void bind() {
    final HandlerManager eventBus = provider.getEventBus();
    // add Listeners.
    handlers.add(eventBus.addHandler(CommentAddedEvent.getType(), 
        new CommentAddedEventHandler() {
          @Override
          public void onCommentAdded(CommentAddedEvent event) {
            addCommentToDisplay(event.getComment());
          }
    }));
    
    handlers.add(eventBus.addHandler(CommentDeletedEvent.getType(), 
        new CommentDeletedEventHandler() {
          @Override
          public void onCommentDeleted(CommentDeletedEvent event) {
            removeCommentFromDisplay(event.getComment());
          }
    }));
  }

  @Override
  public HandlerManager getEventBus() {
    return provider.getEventBus();
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
   * Method will extract List of Comments from tripItem and will call
   *  populateView(list) with this list as parameter.
   */
  public void setTripItem(TripItem item) {
    tripItem = item;
    trip = provider.getTripService().getTrip(tripItem.getTripId());
    final ArrayList<Comment> comments =
        provider.getCommentService().getComments(tripItem.getTripId(), tripItem.getKey());
    populateView(comments);
  }
  
  /**
   * Method will extract List of Comments from trip and will call
   *  populateView(list) with this list as parameter.
   */
  public void setTrip(Trip tripobject) {
    trip = tripobject;
    final ArrayList<Comment> comments = 
        provider.getCommentService().getComments(trip.getKey(), "");
    populateView(comments);
  }
  
  /**
   * Method is used to populate the Comment Screen View.
   * 
   * It will iterate over comment list and add will call a method
   * addCommentToDisplay(Comment comment) for adding them to display list.
   */
  @VisibleForTesting
  void populateView(List<Comment> comments) {
    display.clear();
    displayCommentList.clear();
    for (final Comment comment : comments) {
      addCommentToDisplay(comment);
    }
  }

  /**
   * Method will add Comment item to display.
   * 
   * It will create a CommentListItemPresenter and its associated view from 
   * comment Object and will add that view to the display's List.
   * It also shows a toast message to the user. 
   */
  @VisibleForTesting
  void addCommentToDisplay(Comment comment) {
    final boolean authorized =
      provider.getUserUtils().isOwner(trip, comment, provider.getUtils().getUserEmail());
    final CommentListItemPresenter itemPresenter = commentListItemPresenterProvider.get();
    itemPresenter.setComment(comment, authorized);
    itemPresenter.bind();
    
    // The following two operation should be atomic.
    displayCommentList.add(comment);
    display.addCommentListItem(itemPresenter.getDisplay());
    
    provider.getToast().showToast(provider.getMessage().commentAdded());
  }
  
  /**
   * Method will remove Comment item from display.
   * 
   * It will find the index of the item from the display's comment list, whose 
   * comment Id matches to the passed comment object, and then will pass index 
   * to the display.removeCommentListItem(index) for removing it from display.
   * It also shows a toast message to the user. 
   * 
   * @return an index of the comment item, which matches with comment Id.
   */
  @VisibleForTesting
  int removeCommentFromDisplay(Comment comment) {
    int indexToRemove = -1;
    for (int i = 0; i < displayCommentList.size(); i++) {
      if (displayCommentList.get(i).getKey().equals(comment.getKey())) {
        indexToRemove = i;
        break;
      }
    }
    // The following two operation should be atomic.
    displayCommentList.remove(indexToRemove);
    display.removeCommentListItem(indexToRemove);
    
    provider.getToast().showToast(provider.getMessage().commentDeleted());
    return indexToRemove;
  }
  
  /**
   * Method will set displayCommentList.
   * 
   * This will be called only by Test Methods.
   * 
   * @param commentList Dummy List to set displayCommentList by Test method.
   */
  @VisibleForTesting
  void setDisplayCommentlist(ArrayList<Comment> commentList) {
    displayCommentList = commentList;
  }
}
