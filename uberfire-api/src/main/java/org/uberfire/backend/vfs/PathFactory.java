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

package org.uberfire.backend.vfs;

import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.Portable;

import static org.uberfire.commons.validation.PortablePreconditions.*;

public final class PathFactory {

    public static final String LOCK_FILE_EXTENSION = ".ulock";
    public static String VERSION_PROPERTY = "hasVersionSupport";

    private PathFactory() {
    }

    public static Path newPath( final String fileName,
                                final String uri ) {
        return new PathImpl( checkNotEmpty( "fileName", fileName ), checkNotEmpty( "uri", uri ) );
    }

    public static Path newPathBasedOn( final String fileName,
                                       final String uri,
                                       final Path path ) {
        return new PathImpl( checkNotEmpty( "fileName", fileName ), checkNotEmpty( "uri", uri ), checkNotNull( "path", path ) );
    }

    public static Path newPath( final String fileName,
                                final String uri,
                                final Map<String, Object> attrs ) {
        return new PathImpl( checkNotEmpty( "fileName", fileName ), checkNotEmpty( "uri", uri ), attrs );
    }
    
    public static Path newLock( final Path path ) {
        Path lockPath = newLockPath(path);
        return PathFactory.newPath( path.getFileName() + LOCK_FILE_EXTENSION,
                                    lockPath.toURI() + LOCK_FILE_EXTENSION );
    }

    /**
     * Returns a path of a lock for the provided file. 
     * 
     * Examples:
     * 
     * <pre>
     * default://master@repo/some/path/to/file.txt =>
     * default://locks@system/repo/master/some/path/to/file.txt.ulock
     * 
     * file:\\master@repo\some\path\to\file.txt =>
     * file:\\locks@system\repo\master\some\path\to\file.txt.ulock
     * </pre>
     * 
     * @param path
     *            the path of a file for which a lock should be created, must not be null.
     * @return the lock path
     */
    public static Path newLockPath( final Path path) {
        checkNotNull( "path", path );

        final String systemUri = path.toURI().replaceFirst( "(/|\\\\)([^/&^\\\\]*)@([^/&^\\\\]*)",
                                                            "$1locks@system$1$3$1$2" );

        return PathFactory.newPath( "/", 
                                    systemUri);
    }
    
    /**
     * Returns the path of the locked file for the provided lock.
     * 
     * Examples:
     * 
     * <pre>
     * default://locks@system/repo/master/some/path/to/file.txt.ulock =>
     * default://master@repo/some/path/to/file.txt
     * 
     * file:\\locks@system\repo\master\some\path\to\file.txt.ulock =>
     * file:\\master@repo\some\path\to\file.txt
     * </pre>
     * 
     * @param lockPath
     *            the path of a lock, must not be null.
     * @return the locked path.
     */
    public static Path fromLock( final Path lockPath ) {
        checkNotNull( "path", lockPath );

        final String uri = lockPath.toURI().replaceFirst( "locks@system(/|\\\\)([^/&^\\\\]*)(/|\\\\)([^/&^\\\\]*)",
                                                          "$4@$2" );

        return PathFactory.newPath( lockPath.getFileName().replace( LOCK_FILE_EXTENSION, "" ),
                                    uri.replace( LOCK_FILE_EXTENSION, "" ) );
    }        

    @Portable
    public static class PathImpl implements Path,
                                            IsVersioned {

        private String uri = null;
        private String fileName = null;
        private HashMap<String, Object> attributes = null;
        private boolean hasVersionSupport = false;

        public PathImpl() {
        }

        private PathImpl( final String fileName,
                          final String uri ) {
            this( fileName, uri, (Map<String, Object>) null );
        }

        private PathImpl( final String fileName,
                          final String uri,
                          final Map<String, Object> attrs ) {
            this.fileName = fileName;
            this.uri = uri;
            if ( attrs == null ) {
                this.attributes = new HashMap<String, Object>();
            } else {
                if ( attrs.containsKey( VERSION_PROPERTY ) ) {
                    hasVersionSupport = (Boolean) attrs.remove( VERSION_PROPERTY );
                }
                if ( attrs.size() > 0 ) {
                    this.attributes = new HashMap<String, Object>( attrs );
                } else {
                    this.attributes = new HashMap<String, Object>();
                }
            }
        }

        private PathImpl( final String fileName,
                          final String uri,
                          final Path path ) {
            this.fileName = fileName;
            this.uri = uri;
            if ( path instanceof PathImpl ) {
                this.hasVersionSupport = ( (PathImpl) path ).hasVersionSupport;
            }
        }

        @Override
        public String getFileName() {
            return fileName;
        }

        @Override
        public String toURI() {
            return uri;
        }

        public Map<String, Object> getAttributes() {
            return attributes;
        }

        @Override
        public int compareTo( final Path another ) {
            return this.uri.compareTo( another.toURI() );
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) {
                return true;
            }
            if ( !( o instanceof Path ) ) {
                return false;
            }

            final Path path = (Path) o;

            return uri.equals( path.toURI() );
        }

        @Override
        public boolean hasVersionSupport() {
            return hasVersionSupport;
        }

        @Override
        public int hashCode() {
            return uri.hashCode();
        }

        @Override
        public String toString() {
            return "PathImpl{" +
                    "uri='" + uri + '\'' +
                    ", fileName='" + fileName + '\'' +
                    ", attrs=" + attributes +
                    '}';
        }
    }
}
