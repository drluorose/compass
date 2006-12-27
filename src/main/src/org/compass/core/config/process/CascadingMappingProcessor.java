/*
 * Copyright 2004-2006 the original author or authors.
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

package org.compass.core.config.process;

import java.util.ArrayList;
import java.util.Iterator;

import org.compass.core.config.CompassSettings;
import org.compass.core.converter.ConverterLookup;
import org.compass.core.engine.naming.PropertyNamingStrategy;
import org.compass.core.mapping.AbstractResourceMapping;
import org.compass.core.mapping.CascadeMapping;
import org.compass.core.mapping.CompassMapping;
import org.compass.core.mapping.Mapping;
import org.compass.core.mapping.MappingException;
import org.compass.core.mapping.osem.AbstractCollectionMapping;

/**
 * Goes over all the {@link org.compass.core.mapping.ResourceMapping}s mappings and
 * finds all the {@link org.compass.core.mapping.CascadeMapping} in order to set them
 * at the resource mapping level.
 *
 * @author kimchy
 */
public class CascadingMappingProcessor implements MappingProcessor {

    public CompassMapping process(CompassMapping compassMapping, PropertyNamingStrategy namingStrategy,
                                  ConverterLookup converterLookup, CompassSettings settings) throws MappingException {

        for (Iterator compIt = compassMapping.mappingsIt(); compIt.hasNext();) {
            Object mapping = compIt.next();
            if (!(mapping instanceof AbstractResourceMapping)) {
                continue;
            }
            AbstractResourceMapping resourceMapping = (AbstractResourceMapping) mapping;
            ArrayList cascades = new ArrayList();
            for (Iterator it = resourceMapping.mappingsIt(); it.hasNext();) {
                Mapping m = (Mapping) it.next();
                if (m instanceof CascadeMapping) {
                    cascades.add(m);
                } else if (m instanceof AbstractCollectionMapping) {
                    // TODO maybe it is better for colleciton mapping to implement CascadeMapping? It might not apply to all its element mappings
                    AbstractCollectionMapping cm = (AbstractCollectionMapping) m;
                    if (cm.getElementMapping() instanceof CascadeMapping) {
                        cascades.add(cm.getElementMapping());
                    }
                }
            }
            if (cascades.size() > 0) {
                resourceMapping.setCascades((CascadeMapping[]) cascades.toArray(new CascadeMapping[cascades.size()]));
            } else {
                resourceMapping.setCascades(null);
            }
        }

        return compassMapping;
    }
}