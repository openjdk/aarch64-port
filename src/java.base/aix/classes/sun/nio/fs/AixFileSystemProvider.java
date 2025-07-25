/*
 * Copyright (c) 2008, 2025, Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2013, 2025 SAP SE. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package sun.nio.fs;

import java.nio.file.*;
import java.nio.file.attribute.*;
import java.io.IOException;

/**
 * AIX implementation of FileSystemProvider
 */

class AixFileSystemProvider extends UnixFileSystemProvider {
    public AixFileSystemProvider() {
        super();
    }

    @Override
    AixFileSystem newFileSystem(String dir) {
        return new AixFileSystem(this, dir);
    }

    /**
     * @see sun.nio.fs.UnixFileSystemProvider#getFileStore(sun.nio.fs.UnixPath)
     */
    @Override
    AixFileStore getFileStore(UnixPath path) throws IOException {
        return new AixFileStore(path);
    }

    private static boolean supportsUserDefinedFileAttributeView(UnixPath file) {
        try {
            FileStore store = new AixFileStore(file);
            return store.supportsFileAttributeView(UserDefinedFileAttributeView.class);
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V extends FileAttributeView> V getFileAttributeView(Path obj,
                                                                Class<V> type,
                                                                LinkOption... options)
    {
        if (type == UserDefinedFileAttributeView.class) {
            UnixPath file = UnixPath.toUnixPath(obj);
            return supportsUserDefinedFileAttributeView(file) ?
                (V) new AixUserDefinedFileAttributeView(file, Util.followLinks(options))
                : null;
        }
        return super.getFileAttributeView(obj, type, options);
    }

    @Override
    public DynamicFileAttributeView getFileAttributeView(Path obj,
                                                         String name,
                                                         LinkOption... options)
    {
        if (name.equals("user")) {
            UnixPath file = UnixPath.toUnixPath(obj);
            return supportsUserDefinedFileAttributeView(file) ?
                new AixUserDefinedFileAttributeView(file, Util.followLinks(options))
                : null;
        }
        return super.getFileAttributeView(obj, name, options);
    }
}
