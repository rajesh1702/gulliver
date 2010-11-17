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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.mobile.trippy.web.client.base.Constants;
import com.google.mobile.trippy.web.client.base.SingletonComponents;
import com.google.mobile.trippy.web.client.view.View;
import com.google.mobile.trippy.web.client.widget.Toast;
import com.google.mobile.trippy.web.shared.exception.AuthorizationException;
import com.google.mobile.trippy.web.shared.models.Comment;

import java.util.ArrayList;
import java.util.List;

/**
 * Presenter for a single comment in comment list.
 * 
 * This presenter is responsible for filling a comment view. It will fill
 * comment text as entered by user, the date on which comment posted, User who 
 * posted the comment, and will show the delete button (if the viewer is same
 *  as owner of the comment). 
 * 
 */
public class CommentListItemPresenter implements
    EventHandlerPresenter<CommentListItemPresenter.Display> {

  /**
   * View interface for this presenter
   */
  public interface Display extends View {
    public HasClickHandlers getDeleteButton();
    public void setCommentText(final String text);
    public void setDate(final String dateString);
    public void setUser(final String userString);
    public void setDeleteButtonVisible(final boolean visible);
  }

  /** Object to contain the reference of associated view. */
  private final Display display;

  /** Common container for all the singleton components of this application. */
  private final SingletonComponents provider;

  private final List<HandlerRegistration> handlers;

  /** Boolean to check whether current viewer is owner or not. */
  private boolean isOwner;
  private Comment comment;

  public CommentListItemPresenter(final Display display, final SingletonComponents provider) {
    this.display = display;
    this.provider = provider;
    this.handlers = new ArrayList<HandlerRegistration>();
  }

  /**
   * Method takes the responsibility for handling all the events and firing
   * the events.
   * 
   * Click Handlers:
   * 1) It will have a click handler on Click event of Delete button, which will
   *    send delete request for this comment to comment service.
   */
  @Override
  public void bind() { 
    if (isOwner) {
      handlers.add(display.getDeleteButton().addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          final Toast toast = provider.getToast();
          try {
            if (Window.confirm(provider.getMessage().confirmCommentDelete())) {
              toast.showLoading(provider.getMessage().deleting(Constants.COMMENT_LOWER_CASE_STR));
              provider.getCommentService().deleteComment(comment, new AsyncCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                  toast.hideLoading();
                  toast.showToast(provider.getMessage().commentDeleted());
                }

                @Override
                public void onFailure(Throwable caught) {
                  toast.hideLoading();
                  if (caught instanceof AuthorizationException) {
                    toast.showToast(provider.getMessage().
                        unauthorizedDelete(Constants.COMMENT_LOWER_CASE_STR));
                  } else {
                    toast.showToast(provider.getMessage().commentDeletionFailed());
                  }
                }
              });
            }
          } catch (AuthorizationException e) {
            toast.hideLoading();
            toast.showToast(provider.getMessage().unauthorizedDelete(Constants.COMMENT_LOWER_CASE_STR));
          }
        }
      }));
    }
  }

  @Override
  public HandlerManager getEventBus() {
    return provider.getEventBus();
  }

  @Override
  public void release() {
    for (HandlerRegistration handler : handlers) {
      if (handler != null) {
        handler.removeHandler();
      }
    }
    handlers.clear();
  }

  @Override
  public Display getDisplay() {
    return display;
  }

  /**
   * Method is used to populate the Comment Item View. 
   * 
   * It will set comment text, user name and date into display.
   * It also hide delete button, if user is not the owner of comment. 
   */
  @VisibleForTesting
  void populateView(final Comment commentToAdd) {
    display.setCommentText(commentToAdd.getComment());
    display.setDate(provider.getUtils().getDateTimeString(commentToAdd.getAddedOn()));
    display.setUser(commentToAdd.getUserId());
    display.setDeleteButtonVisible(isOwner);
  }

  /** 
   * Method will be used to set comment by the {@link CommentsPresenter}. 
   */
  void setComment(final Comment comment, final boolean owner) {
    this.comment = comment;  
    setOwner(owner);
    populateView(comment);
  }

  /**
   *  Method will be used by test cases only. 
   */
  @VisibleForTesting
  void setOwner(final boolean owner) {
    this.isOwner = owner; 
  }
}
