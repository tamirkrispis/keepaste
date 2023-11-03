/**
 * Keepaste - The keep and paste program (http://www.keepaste.com)
 * Copyright (C) 2023 Tamir Krispis
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.keepaste.logic.utils;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

import javax.swing.*;
import java.io.File;

/**
 * This class is a utility class for anything related to the file system.
 */
@Log4j2
public final class FileSystemUtils {

    private static final String HOME_DIRECTORY = System.getProperty("user.home");

    private FileSystemUtils() {
        // private constructor for utils class
    }

    /**
     * Creates a directory if not exists already.
     *
     * @param path the path of the directory to be created
     */
    public static void createDirectoryIfNotExists(String path) {
        File theDir = new File(path);

        // if the directory does not exist, create it
        if (!theDir.exists()) {
            log.debug("Creating directory: ".concat(path));

            try {
                if (theDir.mkdir()) {
                    log.debug("Directory created: ".concat(path));
                }
            } catch (SecurityException se) {
                //handle it
                JOptionPane.showMessageDialog(null, "Infrastructure FileSystem failed: " + se);
            }
        }
    }

    public static String getKeepasteDirectory() {
        String path = HOME_DIRECTORY.concat("/.keepaste");
        createDirectoryIfNotExists(path);
        return path;
    }

    public static String getUserHomeDirectory() {
        return HOME_DIRECTORY;
    }

    public static boolean isHomeDirectoryExists() {
        return new File(HOME_DIRECTORY).exists();
    }

    public static boolean deleteFile(@NonNull final String filepath) {
        File file = new File(filepath);
        if (file.exists()) {
            return file.delete();
        }
        return true;
    }
}
