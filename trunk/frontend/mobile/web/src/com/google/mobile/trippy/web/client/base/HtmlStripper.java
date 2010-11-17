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

package com.google.mobile.trippy.web.client.base;

/**
 * Removes all HTML tags from the input string and replaces BR and P tags with
 * newlines. The returned string is meant to be used as Text, not HTML.
 * 
 */
public class HtmlStripper {
  public static String stripHtml(String input) {
    // Try to convert simple formatting tags to line breaks.
    input = input.replaceAll("</?p/?>", "\n\n")
      .replaceAll("<br/?>", "\n")
      .replaceAll("&#x2019;", "'")
      .replaceAll("&#x2013;", "-");
    
    // Remove all ugly tags. Regex is not reliable - so don't depend on it to 
    // prevent XSS. Use the returned string as Text, not HTML.
    input = input.replaceAll("<.*?>", "");
    
    return input;
  }  
}
