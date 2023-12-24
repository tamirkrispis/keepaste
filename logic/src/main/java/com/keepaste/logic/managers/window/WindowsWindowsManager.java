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

package com.keepaste.logic.managers.window;

import com.keepaste.logic.Application;
import com.keepaste.logic.models.WindowInformation;
import com.keepaste.logic.utils.KeyboardUtils;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.PsapiUtil;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.ptr.IntByReference;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

import java.awt.event.KeyEvent;

/**
 * This class is Windows's {@code WindowManager}, it holds relevant methods related to windows management in the Windows OS.
 */
@Log4j2
public final class WindowsWindowsManager implements WindowManager {
    public static final int BUFFER_LENGTH_MEGA = 1024;
    private WindowInformation lastTopMostWindow;

    @Override
    public WindowInformation getActiveWindow() {

        // TODO: we need to find out when the locking window was closed, then release the locking from it.
        // maybe check from active windows/processes, if the name exists or not

        try {
            // active window handle
            WinDef.HWND hwnd = User32.INSTANCE.GetForegroundWindow();

            // getting the process id by the active window handle
            IntByReference intByReference = new IntByReference();
            User32.INSTANCE.GetWindowThreadProcessId(hwnd, intByReference);
            int processId = intByReference.getValue();

            // getting extended thread info if needed
//        WinUser.GUITHREADINFO guithreadinfo = new WinUser.GUITHREADINFO();
//        User32.INSTANCE.GetGUIThreadInfo(processId, guithreadinfo);

            // getting the active window text (title)
            char[] windowTextBuf = new char[BUFFER_LENGTH_MEGA];
            User32.INSTANCE.GetWindowText(hwnd, windowTextBuf, BUFFER_LENGTH_MEGA);
            String windowText = Native.toString(windowTextBuf);

            WinUser.WINDOWINFO windowinfo = new WinUser.WINDOWINFO();
            User32.INSTANCE.GetWindowInfo(hwnd, windowinfo);

            // getting the executable file name based on the extracted process id
            WinNT.HANDLE process = Kernel32.INSTANCE.OpenProcess(BUFFER_LENGTH_MEGA | 0x0010, false, processId);
            if (process != null) {
                String processImageFileName = PsapiUtil.GetProcessImageFileName(process);


                // another option and format for getting the executable file name of the active window
//        char[] buffer = new char[1024];
//        Psapi.INSTANCE.GetModuleFileNameExW(process, null, buffer, 1024);
//        String moduleFilenameExW = Native.toString(buffer);

                // closing the handle once done with it
                // Kernel32Util.closeHandle(process);

                var topMostWindow = WindowInformation.builder()
                        .text(windowText)
                        .processId(processId)
                        .app(processImageFileName)
                        .hwnd(hwnd)
                        .left(windowinfo.rcWindow.left)
                        .right(windowinfo.rcWindow.right)
                        .top(windowinfo.rcWindow.top)
                        .bottom(windowinfo.rcWindow.bottom)
                        .build();

                if (!topMostWindow.equals(lastTopMostWindow)) {
                    log.debug("top most window = [{}]", topMostWindow);
                }
                lastTopMostWindow = topMostWindow;
                return topMostWindow;
            }
            return null;
        } catch (Exception ex) {
            log.debug("Failed to get active window", ex);
            return null;
        }
    }

    @Override
    public void paste() {
        log.debug("Pasting using SHIFT+INSERT");
        KeyboardUtils.keyPress(KeyEvent.VK_SHIFT);
        KeyboardUtils.delay(100);
        KeyboardUtils.keyPress(KeyEvent.VK_INSERT);
        KeyboardUtils.keyRelease(KeyEvent.VK_INSERT);
        KeyboardUtils.delay(50);
        KeyboardUtils.keyRelease(KeyEvent.VK_SHIFT);
    }

    @Override
    public boolean focusOnActiveWindow(@NonNull final WindowInformation windowContext) {
        try {
            WindowInformation activeWindow = Application.getContext().getModelActiveWindow().getActiveWindow();
            if (activeWindow != null) {
                User32DLL.SetFocus(activeWindow.getHwnd());
                User32DLL.SetForegroundWindow(activeWindow.getHwnd());
                return true;
            } else {
                log.error(String.format("Failed to focus on window as active window is null [%s]", windowContext));
                return false;
            }
        } catch (Exception ex) {
            log.error(String.format("Failed to focus on window as active window is null [%s]", windowContext), ex);
            return false;
        }
    }

    static class User32DLL {

        private User32DLL() {
            // do nothing
        }

        static {
            Native.register("user32");
        }

//        public static native int GetWindowThreadProcessId(HWND hWnd, PointerByReference pref);
//        public static native HWND GetForegroundWindow();
//        public static native int GetWindowTextW(HWND hWnd, char[] lpString, int nMaxCount);
        public static native WinDef.HWND SetFocus(WinDef.HWND hWnd);
        public static native boolean ShowWindow(WinDef.HWND hWnd, int nCmdShow);
        public static native boolean SetForegroundWindow(WinDef.HWND hWnd);
//        public static native boolean SetWindowPos(WinDef.HWND hWnd,
//                                                  WinDef.HWND hWndInsertAfter,
//                                                  int X,
//                                                  int Y,
//                                                  int cx,
//                                                  int cy,
//                                                  int uFlags);
    }

//    static class Psapi {
//        static { Native.register("psapi"); }
//        public static native int GetModuleBaseNameW(Pointer hProcess, Pointer hmodule, char[] lpBaseName, int size);
//    }
//
//    static class Kernel32 {
//        static { Native.register("kernel32"); }
//        public static int PROCESS_QUERY_INFORMATION = 0x0400;
//        public static int PROCESS_VM_READ = 0x0010;
//        public static native int GetLastError();
//        public static native Pointer OpenProcess(int dwDesiredAccess, boolean bInheritHandle, Pointer pointer);
//    }
//
//    static class User32DLL {
//        static { Native.register("user32"); }
//        public static native int GetWindowThreadProcessId(HWND hWnd, PointerByReference pref);
//        public static native HWND GetForegroundWindow();
//        public static native int GetWindowTextW(HWND hWnd, char[] lpString, int nMaxCount);
//    }
}
