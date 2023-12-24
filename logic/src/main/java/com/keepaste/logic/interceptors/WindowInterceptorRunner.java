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

package com.keepaste.logic.interceptors;

import com.keepaste.logic.common.BaseSubject;
import com.keepaste.logic.managers.window.WindowManager;
import com.keepaste.logic.models.ModelActiveWindow;
import com.keepaste.logic.models.WindowInformation;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.TimeUnit;

/**
 * This class is a {@code Runnable} that runs in parallel thread and samples the currently active window.
 * The active window is where Keeps will be pasted upon execution of a Keep.
 */
@Log4j2
public class WindowInterceptorRunner extends BaseSubject implements Runnable {
    private final WindowManager windowManager;
    private final ModelActiveWindow modelActiveWindow;
    private final int interceptIntervalInMs;
    private WindowInformation lastInterceptedWindow = null;

    public WindowInterceptorRunner(@NonNull final WindowManager windowManager,
                                   @NonNull final ModelActiveWindow modelActiveWindow,
                                   final int interceptIntervalInMs) {
        this.windowManager = windowManager;
        this.modelActiveWindow = modelActiveWindow;
        this.interceptIntervalInMs = interceptIntervalInMs;
    }

    @Override
    public void run() {
        log.info("Starting monitoring active windows");
        try {
            do {
                WindowInformation interceptedWindow = windowManager.getActiveWindow();

                // we do not want the window interceptor to intercept our keepaste app window as an active window
                if (interceptedWindow != null
                        && !interceptedWindow.getApp().toLowerCase().contains("java")
                        && !interceptedWindow.getApp().toLowerCase().endsWith("keepaste")
                        && !interceptedWindow.equals(lastInterceptedWindow)) {
                    log.debug("Intercepted window = [{}]", interceptedWindow);
                    modelActiveWindow.setInterceptedWindow(interceptedWindow);
                    lastInterceptedWindow = interceptedWindow;
                }
                TimeUnit.MILLISECONDS.sleep(interceptIntervalInMs);
            } while (!Thread.currentThread().isInterrupted());
        } catch (InterruptedException ex) {
            log.debug("WindowInterceptorRunner interrupted");
            Thread.currentThread().interrupt();
        } catch (Exception ex) {
            log.error("Window interceptor stopped due to exception", ex);
        } finally {
            log.debug("WindowInterceptorRunner stopped");
        }
    }
}
