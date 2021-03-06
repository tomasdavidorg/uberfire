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

package org.uberfire.backend.vfs.impl;

import org.jboss.errai.bus.client.api.interceptor.RpcInterceptor;
import org.jboss.errai.common.client.api.interceptor.RemoteCallContext;
import org.uberfire.backend.vfs.PathFactory;

public class VFSCacheInterceptor implements RpcInterceptor {

    @Override
    public void aroundInvoke( final RemoteCallContext context ) {
        final Object o = context.getParameters()[ 0 ];
        if ( o instanceof PathFactory.PathImpl && ( (PathFactory.PathImpl) o ).getAttributes().size() > 0 ) {
            context.setResult( ( (PathFactory.PathImpl) o ).getAttributes() );
            return;
        }

        context.proceed();
    }
}
