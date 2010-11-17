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

package com.google.mobile.trippy.web.shared.models;

/**
 * Enum for POI types
 * 
 */

public enum POIType {
  EAT/* Restaurants */,
  SLEEP/* Hotels */,
  SEE /* Sights */,
  SHOP /* Shopping */,
  NIGHT /* Entertainment */,
  DO/* Activities */,
  GENERAL/* General */;

  public String getString() {
    switch (this) {
      case EAT:
        return "eat";
      case SLEEP:
        return "sleep";
      case SEE:
        return "see";
      case SHOP:
        return "shop";
      case NIGHT:
        return "night";
      case DO:
        return "do";
      case GENERAL:
        return "general";
    }
    return null;
  }
  
  

   public static String getDisplayString(String poiS) {
     
     if (poiS.hashCode() == "EAT".hashCode()) {
       return "Restaurants";
     } else if (poiS.hashCode() == "SLEEP".hashCode()) {
       return "Hotels";
     } else if (poiS.hashCode() == "SEE".hashCode()) {
       return "Sights";
     } else if (poiS.hashCode() == "SHOP".hashCode()) {
       return "Shopping";
     } else if (poiS.hashCode() == "NIGHT".hashCode()) {
       return "Entertainment";
     } else if (poiS.hashCode() == "DO".hashCode()) {
       return "Activities";
     } else if (poiS.hashCode() == "GENERAL".hashCode()) {
       return "General";
     }
     
//     switch(poiS.hashCode()) {
//       case "eat".hashCode():
//         return "Restaurants";
//       case "sleep".hashCode():
//         return "Hotels";
//       case "see".hashCode():
//         return "Sights";
//       case "shop".hashCode():
//         return "Shopping";
//       case "night".hashCode():
//         return "Entertainment";
//       case "do".hashCode():
//         return "Activities";
//       case "general".hashCode():
//         return "General";
//     }
     return null;
   }

   public static String getGoogleQueryString(final String poiS) {
     if (poiS.hashCode() == "EAT".hashCode()) {
       return "Restaurants";
     } else if (poiS.hashCode() == "SLEEP".hashCode()) {
       return "Hotels";
     } else if (poiS.hashCode() == "SEE".hashCode()) {
       return "Places";
     } else if (poiS.hashCode() == "SHOP".hashCode()) {
       return "Shopping";
     } else if (poiS.hashCode() == "NIGHT".hashCode()) {
       return "Entertainment";
     } else if (poiS.hashCode() == "DO".hashCode()) {
       return "Activities";
     } else if (poiS.hashCode() == "GENERAL".hashCode()) {
       return "Places";
     }
     return "Activities";
   }
}
