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

import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * This class is a utility class for anything related to simulating keyboard clicks.
 */
@Log4j2
public final class KeyboardUtils {
    static final Robot ROBOT;

    @Setter
    static int delay = 50;

    private KeyboardUtils() {
        // private constructor for utils class
    }

    static {
        try {
            ROBOT = new Robot();
            ROBOT.setAutoWaitForIdle(true);
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Imitate pressing the 'Enter' key.
     */
    public static void enter() {
        log.debug("Robot pressing ENTER");
        ROBOT.keyPress(KeyEvent.VK_ENTER);
        ROBOT.keyRelease(KeyEvent.VK_ENTER);
    }

    /**
     * Imitate pressing a keyboard key.
     *
     * @param keyEventCode the key code of the keyboard button
     */
    public static void keyPress(final int keyEventCode) {
        ROBOT.keyPress(keyEventCode);
    }

    /**
     * Imitate releasing a keyboard key.
     *
     * @param keyEventCode the key code of the keyboard button
     */
    public static void keyRelease(final int keyEventCode) {
        ROBOT.keyRelease(keyEventCode);
    }

    /**
     * Perform a delay between key strokes.
     *
     * @param ms the time interval in ms to delay.
     */
    public static void delay(final int ms) {
        ROBOT.delay(ms);
    }

    public static void cmdV() {
        // VK_META is problematic on Mac, so this is a hack for pressing it for several times, then releasing it, so at least one will be simulated
        ROBOT.keyPress(KeyEvent.VK_META);
        ROBOT.keyPress(KeyEvent.VK_META);
        ROBOT.keyPress(KeyEvent.VK_META);
        ROBOT.keyPress(KeyEvent.VK_META);
        ROBOT.keyPress(KeyEvent.VK_META);
        ROBOT.delay(50);
        ROBOT.keyPress(KeyEvent.VK_V);
        ROBOT.delay(50);
        ROBOT.keyRelease(KeyEvent.VK_META);
        ROBOT.keyRelease(KeyEvent.VK_META);
        ROBOT.keyRelease(KeyEvent.VK_META);
        ROBOT.keyRelease(KeyEvent.VK_META);
        ROBOT.keyRelease(KeyEvent.VK_META);
        ROBOT.keyRelease(KeyEvent.VK_V);
    }
}
