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
import com.google.mobile.trippy.web.client.base.Constants;
import com.google.mobile.trippy.web.client.base.Provider;
import com.google.mobile.trippy.web.client.event.ShowTripItemDetailsEvent;
import com.google.mobile.trippy.web.client.presenter.BaseHeaderPresenter;
import com.google.mobile.trippy.web.client.presenter.CommentAddPresenter;
import com.google.mobile.trippy.web.client.presenter.CommentsPresenter;
import com.google.mobile.trippy.web.client.presenter.EventHandlerPresenter;
import com.google.mobile.trippy.web.client.presenter.header.TripItemOptionsPresenter;
import com.google.mobile.trippy.web.client.view.View;
import com.google.mobile.trippy.web.shared.models.Trip;
import com.google.mobile.trippy.web.shared.models.TripItem;

/**
 * This class is responsible to show trip item comments.
 * 
 * 
 */
public class TripItemCommentsScreenPresenter implements
    EventHandlerPresenter<TripItemCommentsScreenPresenter.Display> {

  /**
   * Interface to view the create trip screen.
   */
  public static interface Display extends View {
    void setHeader(final BaseHeaderPresenter.Display display);

    void setBody(final CommentAddPresenter.Display addDisplay, 
        final CommentsPresenter.Display commentList);
  }

  private final Display display;
  private final BaseHeaderPresenter headerPresenter;
  private final CommentAddPresenter commentAddPresenter;
  private final CommentsPresenter commentListPresenter;
  private final TripItemOptionsPresenter headerOptions;

  public TripItemCommentsScreenPresenter(final Display display,
      final Provider<BaseHeaderPresenter> headerProvider,
      final Provider<CommentAddPresenter> commentAddPresenterProvider,
      final Provider<CommentsPresenter> commentListPresenterProvider,
      final Provider<TripItemOptionsPresenter> headerOptionsProvider) {
    this.display = display;
    this.headerPresenter = headerProvider.get();
    this.commentAddPresenter = commentAddPresenterProvider.get();
    this.commentListPresenter = commentListPresenterProvider.get();
    this.headerOptions = headerOptionsProvider.get();
  }

  /**
   * Method should be preceded with the setTripItem(TripItem item) method call.
   */
  @Override
  public void bind() {
    headerPresenter.bind();
    commentAddPresenter.bind();
    commentListPresenter.bind();
    headerOptions.bind();
  }

  @Override
  public HandlerManager getEventBus() {
    return commentAddPresenter.getEventBus();
  }

  @Override
  public void release() {
    headerPresenter.release();
    commentAddPresenter.release();
    commentListPresenter.release();
    headerOptions.release();
  }

  @Override
  public Display getDisplay() {
    return display;
  }

  public void setTripItem(final Trip trip, final TripItem item) {
    headerPresenter.setTitleString(item.getName(), null);
    headerPresenter.setNavigation(Constants.DETAILS_STR, new Runnable() {
      @Override
      public void run() {
        getEventBus().fireEvent(new ShowTripItemDetailsEvent(item));
      }
    });
    headerPresenter.setOptions(headerOptions, true);
    headerPresenter.setSubNavigationVisible(false);
    commentAddPresenter.setTripItem(item);
    commentListPresenter.setTripItem(item);
    headerOptions.setTripItem(item);
    populateView();
  }

  private void populateView() {
    display.setHeader(headerPresenter.getDisplay());
    display.setBody(commentAddPresenter.getDisplay(), commentListPresenter.getDisplay());
  }
}
