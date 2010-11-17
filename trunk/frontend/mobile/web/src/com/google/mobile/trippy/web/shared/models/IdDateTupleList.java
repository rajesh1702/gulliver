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

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * 
 */
public class IdDateTupleList implements Serializable {
  
  private static final long serialVersionUID = -5006033245794787879L;
  private  ArrayList<IdDateTuple> tuples = new ArrayList<IdDateTuple>();
  
  public IdDateTupleList() {
    this(new ArrayList<IdDateTuple>());
  }

  public IdDateTupleList(ArrayList<IdDateTuple> tuples) {
    super();
    this.tuples = tuples;
  }
  
  public ArrayList<IdDateTuple> getTuples() {
    return tuples;
  }
  
  public void setTuples(ArrayList<IdDateTuple> tuples) {
    this.tuples = tuples;
  }
  
}