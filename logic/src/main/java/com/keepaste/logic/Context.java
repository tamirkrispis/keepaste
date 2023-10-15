package com.keepaste.logic;

import com.keepaste.gui.Gui;
import com.keepaste.logic.interceptors.WindowInterceptorRunner;
import com.keepaste.logic.managers.KeepExecutionManager;
import com.keepaste.logic.managers.KeepsManager;
import com.keepaste.logic.managers.window.WindowManager;
import com.keepaste.logic.models.ModelActiveWindow;
import com.keepaste.logic.models.ModelSettings;
import com.keepaste.logic.utils.OperatingSystemUtils;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.SystemUtils;

import java.util.Properties;

/**
 * This class holds the context of the application. The different instances used by it that defines its behaviour.
 */
@Log4j2
public class Context {

    @Getter
    private final Gui gui;
    private Properties properties;
    @Getter
    private final ModelSettings modelSettings;
    private Thread windowInterceptorThread;
    @Getter
    private final WindowManager windowManager;
    @Getter
    private final ModelActiveWindow modelActiveWindow;
    @Getter
    @Setter
    private boolean keepCurrentlyRunning;
    @Getter
    private final KeepsManager keepsManager;
    @Getter
    private final KeepExecutionManager keepExecutionManager;
    public static final int INTERCEPT_INTERVAL_IN_MS = 50;

    /**
     * Constructor.
     *
     * @param gui                   the {@link Gui}
     * @param windowManager         the relevant {@link WindowManager} based on the OS
     * @param modelSettings         the {@link ModelSettings} to be used
     * @param keepsManager          the {@link KeepsManager} to be used
     * @param keepExecutionManager  the {@link KeepExecutionManager} to be used
     * @param modelActiveWindow     the {@link ModelActiveWindow} to be used
     */
    public Context(@NonNull final Gui gui,
                   @NonNull final WindowManager windowManager,
                   @NonNull final ModelSettings modelSettings,
                   @NonNull final KeepsManager keepsManager,
                   @NonNull final KeepExecutionManager keepExecutionManager,
                   @NonNull final ModelActiveWindow modelActiveWindow) {
        this.gui = gui;
        this.modelSettings = modelSettings;
        this.windowManager = windowManager;
        this.modelActiveWindow = modelActiveWindow;
        this.keepsManager = keepsManager;
        this.keepExecutionManager = keepExecutionManager;

        initProperties();

        log.info("Application started");
        log.info("Operating system: ".concat(OperatingSystemUtils.getOperatingSystemType().name()));
        log.info("Operating system name: ".concat(SystemUtils.OS_NAME));
        log.info("Operating system version: ".concat(SystemUtils.OS_VERSION));
        log.info("Operating system architecture: ".concat(SystemUtils.OS_ARCH));
        log.info("Java version: ".concat(SystemUtils.JAVA_VERSION));
        log.info("Java runtime name: ".concat(SystemUtils.JAVA_RUNTIME_NAME));
        log.info("Java runtime version: ".concat(SystemUtils.JAVA_RUNTIME_VERSION));
        log.info("Timezone: ".concat(SystemUtils.USER_TIMEZONE));
    }

    /**
     * Will return the version of the Keepaste app.
     *
     * @return the version of the Keepaste app
     */
    public String getVersion() {
        return properties.getProperty("version");
    }


    /**
     * Will start intercepting windows constantly.
     */
    public void startWindowInterceptorRunner() {
        log.info("Starting window interceptor");

        if (modelSettings.isFocusOnWindowAndPaste()) {
            Runnable windowInterceptorRunner = new WindowInterceptorRunner(windowManager, modelActiveWindow, INTERCEPT_INTERVAL_IN_MS);
            windowInterceptorThread = new Thread(windowInterceptorRunner);
            windowInterceptorThread.setDaemon(true);
            windowInterceptorThread.start();
        }
    }

    /**
     * Will stop the window interceptor.
     */
    public void stopWindowInterceptorRunner() {
        log.info("Stopping window interceptor");
        if (windowInterceptorThread != null) {
            windowInterceptorThread.stop(); // interrupt doesn't stop the thread immediately, check that
        }
    }


    private void initProperties() {
        try {
            if (properties == null) {
                properties = new Properties();
                properties.load(Application.class.getResourceAsStream("/project.properties"));
            }
        } catch (Exception ex) {
            log.error("Failed to load project.properties file");
        }
    }
}
