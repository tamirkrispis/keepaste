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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is a utility class for anything related to the file system.
 */
@Log4j2
public class FileSystemUtils {

    private static final String HOME_DIRECTORY = System.getProperty("user.home").concat("/.keepaste");

    private FileSystemUtils() {
        // private constructor for utils class
    }

    public static List<File> getFilesByFolder(String path, String extension){
        List<File> files = new ArrayList<>();

        //Getting all sub folders from the applications folder
        File file = new File(path);
        String[] names = file.list();

        if (extension == null)
        {
            extension = "";
        }

        if (names != null) {
            for (String name : names) {
                if (name.toLowerCase().endsWith(extension.toLowerCase())) {
                    if (!name.startsWith(".")) {
                        if (!path.endsWith("/")) {
                            path = path.concat("/");
                        }
                        File afile = new File(path + name);
                        files.add(afile);
                    }
                }
            }
        }
        Collections.sort(files);
        return files;
    }

    public static boolean createDirectoryIfNotExists(String path){
        File theDir = new File(path);

        // if the directory does not exist, create it
        if (!theDir.exists()) {
            log.debug("Creating directory: ".concat(path));
            boolean result = false;

            try{
                result = theDir.mkdir();
            } catch(SecurityException se){
                //handle it
                JOptionPane.showMessageDialog(null, "Infrastructure FileSystem failed: " + se);
            }
            if (result) {
                log.debug("Directory created: ".concat(path));
            }
            return result;
        }
        return false;
    }

    public static String getHomeDirectory() {
        String path = HOME_DIRECTORY;
        createDirectoryIfNotExists(path);
        return path;
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
