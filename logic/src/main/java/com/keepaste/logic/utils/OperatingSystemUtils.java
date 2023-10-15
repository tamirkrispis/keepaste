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

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.SystemUtils;

/**
 * This class is a utility class for anything related to the operating system.
 */
@Log4j2
public final class OperatingSystemUtils {

    public static final String WINDOWS = "WINDOWS";
    public static final String MAC = "MAC";
    public static final String LINUX = "LINUX";
    public static final String OTHER = "OTHER";

    private OperatingSystemUtils() {

    }

    /**
     * Will return the current {@link OperatingSystemType}.
     *
     * @return return the current {@link OperatingSystemType}.
     */
    public static OperatingSystemType getOperatingSystemType() {
        OperatingSystemType operatingSystemType;
        if (SystemUtils.IS_OS_WINDOWS) {
            operatingSystemType = OperatingSystemType.WINDOWS;
        } else if (SystemUtils.IS_OS_MAC) {
            operatingSystemType = OperatingSystemType.MAC;
        } else if (SystemUtils.IS_OS_LINUX) {
            operatingSystemType = OperatingSystemType.LINUX;
        } else {
            operatingSystemType = OperatingSystemType.OTHER;
        }
        return operatingSystemType;
    }

    public enum OperatingSystemType {
        WINDOWS,
        MAC,
        LINUX,
        OTHER
    }
}
