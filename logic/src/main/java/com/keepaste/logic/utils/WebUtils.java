/**
 * Keepaste - The keep and paste program (http://www.keepaste.com)
 * Copyright (C) 2023 Tamir Krispis
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.keepaste.logic.utils;

import lombok.extern.log4j.Log4j2;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

/**
 * This class is a utility class for anything related to the web.
 */
@Log4j2
public final class WebUtils {

    private WebUtils() {
        // private constructor as this is a utility class
    }

    /**
     * Will return true if internet connection is available.
     *
     * @return true if internet connection is available.
     */
    public static boolean isInternetAvailable() {
        try {
            URL url = new URL("https://www.keepaste.com");
            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            connection.connect();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Will open the default browser with a specific url.
     *
     * @param url the url to browse to
     */
    public static void browseTo(String url) {
        try {
            URI uri = new URI(url);
            Desktop.getDesktop().browse(uri);
        } catch (Exception e) {
            log.error(String.format("Failed to browse to [%s]", url), e);
        }
    }
}
