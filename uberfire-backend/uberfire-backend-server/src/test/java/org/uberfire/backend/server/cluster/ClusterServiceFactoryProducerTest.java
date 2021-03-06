/*
 * Copyright 2015 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.backend.server.cluster;

import org.junit.Test;
import org.uberfire.commons.cluster.ClusterServiceFactory;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ClusterServiceFactoryProducerTest {

    @Test
    public void testClusterNotAvailable() {
        final ClusterServiceFactoryProducer factoryProducer = new ClusterServiceFactoryProducer() {
            ClusterServiceFactory buildFactory() {
                return null;
            }
        };

        assertNull( factoryProducer.clusterServiceFactory() );
    }

    @Test
    public void testClusterInitializedBeforeAnyUse() {
        final ClusterServiceFactory clusterServiceFactory = mock( ClusterServiceFactory.class );

        final ClusterServiceFactoryProducer factoryProducer = new ClusterServiceFactoryProducer() {
            ClusterServiceFactory buildFactory() {
                return clusterServiceFactory;
            }
        };

        final ClusterServiceFactory factory = factoryProducer.clusterServiceFactory();
        assertNotNull( factory );
        assertEquals( clusterServiceFactory, factory );

        verify( factory, times( 1 ) ).build( null );
    }

}
