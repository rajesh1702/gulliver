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

package com.google.mobile.trippy.web.rebind;

import com.google.gwt.core.ext.Linker;
import com.google.gwt.core.ext.LinkerContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.ArtifactSet;
import com.google.gwt.core.ext.linker.EmittedArtifact;
import com.google.gwt.core.ext.linker.LinkerOrder;
import com.google.gwt.core.ext.linker.LinkerOrder.Order;

/**
 * @Linker.
 * 
 */
@LinkerOrder(Order.POST)
public class RecursiveArtifactLinker extends Linker {

  private static final String TRIPPY_PREFIX = "trippy/";
  private static final String WEB_INF_PREFIX = "WEB-INF/";

    @Override
    public String getDescription() {
      return "Recursive Type Remover";
    }

    @Override
    public ArtifactSet link(TreeLogger logger, LinkerContext context,
        ArtifactSet artifacts) throws UnableToCompleteException {
      ArtifactSet toReturn = new ArtifactSet(artifacts);
      ArtifactSet toReturnAfterRemove = new ArtifactSet(artifacts);
      for (EmittedArtifact artifact : toReturn.find(EmittedArtifact.class)) {
        String partialPath = artifact.getPartialPath();
        if (partialPath.startsWith(TRIPPY_PREFIX) || partialPath.startsWith(WEB_INF_PREFIX)) {
          toReturnAfterRemove.remove(artifact);
        }
      }
      return toReturnAfterRemove;
   }

}
