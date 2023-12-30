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

import com.keepaste.logic.Application;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * This class is a utility class for anything related to images and icons.
 */
@Log4j2
public final class ImagesUtils {
    static final Random RANDOM = new Random();
    private static final String KEEPASTE = "keepaste";
    public static final int DEFAULT_COLOR_VAL = 128;
    public static final int DEFAULT_ICON_WIDTH_HEIGHT = 15;
    public static final int COLOR_PART_BOUND = 256;
    public static final int COLOR_PART_LIGHTER_VAL = 50;
    public static final int KEEP_ICON_FONT_SIZE = 12;
    public static final Color AWS_COLOR = new Color(243, 140, 0);
    public static final Color MVN_COLOR = new Color(194, 3, 59);
    public static final Color TERRAFORM_COLOR = new Color(94, 55, 214);
    public static final Color GIT_COLOR = new Color(225, 74, 50);
    public static final Color KUBECTL_COLOR = new Color(45, 104, 216);
    public static final Color DOCKER_COLOR = new Color(33, 140, 223);

    private ImagesUtils() { }

    /**
     * Will return a generated or known icon to be used on the tree for a Keep based on the command text.
     *
     * @param keepExecutable the Keep's first word on its phrase
     * @return a generated or known icon to be used on the tree for a Keep based on the command text.
     */
    public static ImageIcon getImageIconAndGenerateIfNotPresent(String keepExecutable) {
        Color color = getCommandColor(keepExecutable);
        if (color != null) {
            return getImageIcon(keepExecutable, color);
        } else {
            return getDefaultKeepNodeIcon();
        }
    }

    /**
     * Will return the default icon to be used for a Keep on the tree.
     *
     * @return the default icon to be used for a Keep on the tree.
     */
    public static ImageIcon getDefaultKeepNodeIcon() {
        return getImageIcon(KEEPASTE, new Color(DEFAULT_COLOR_VAL, DEFAULT_COLOR_VAL, DEFAULT_COLOR_VAL));
    }

    /**
     * Will return an {@code ImageIcon} from a file path.
     *
     * @param imagePath the image file path
     * @return an {@code ImageIcon} from a filepath.
     */
    public static ImageIcon getImageIconFromFilePath(String imagePath) {
        Image image = getImage(imagePath);
        if (image != null) {
            return new ImageIcon(image);
        } else {
            return new ImageIcon();
        }
    }

    /**
     * Will return an {@code Image} from a file path.
     *
     * @param imagePath the image file path
     * @return an {@code Image} from a file path.
     */
    public static Image getImage(String imagePath) {
        try {
            URL url = ImagesUtils.class.getResource(imagePath);
            if (url != null) {
                return ImageIO.read(url);
            }
        } catch (IOException ex) {
            log.error(String.format("Failed to load image with path [%s]", imagePath), ex);
        }

        return null;
    }

    private static ImageIcon getImageIcon(String keepExecutable, Color color) {
        String commandWithThemeName = keepExecutable.concat("-").concat(Application.getContext().getModelSettings().getTheme().getName());
        // this is a known keep, setting specific icon for it
        File file = new File(getCommandsIconsPath(commandWithThemeName));
        if (!file.exists()) {
            generateIcon(commandWithThemeName, color);
        }
        return new ImageIcon(file.getAbsolutePath());
    }

    /**
     * Generates a Keep's icon.
     *
     * @param commandExecutable The executable to create icon for
     * @param color             the color of the icon
     */
    public static void generateIcon(@NonNull final String commandExecutable, Color color) {
        int width = DEFAULT_ICON_WIDTH_HEIGHT;
        int height = DEFAULT_ICON_WIDTH_HEIGHT;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        // Enable antialiasing for smoother edges
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Set transparent background
        g2d.setColor(new Color(0, 0, 0, 0));
        g2d.fillRect(0, 0, width, height);

        if (color == null) {
            // Generate random color
            color = new Color(RANDOM.nextInt(COLOR_PART_BOUND), RANDOM.nextInt(COLOR_PART_BOUND), RANDOM.nextInt(COLOR_PART_BOUND));
        }

        Color letterColor = Color.WHITE;
        if (Application.getContext().getModelSettings().getTheme().getName().equals("FlatLaf macOS Dark")) {
            letterColor = Color.BLACK;
        }

        // Calculate a lighter color based on the random color
        final var gradientPaint = getGradientPaint(color, width, height);

        // Apply gradient paint to the circle
        g2d.setPaint(gradientPaint);
        g2d.fillOval(0, 0, width - 1, height - 1);

        // Get the letter to be painted in the middle of the circle
        String letter = commandExecutable.substring(0, 1).toLowerCase();

        // Set font properties
        Font font = new Font("Arial", Font.PLAIN, KEEP_ICON_FONT_SIZE);
        g2d.setFont(font);
        g2d.setColor(letterColor);

        // Calculate the position to center the letter
        int letterWidth = g2d.getFontMetrics().stringWidth(letter);
        int letterHeight = g2d.getFontMetrics().getAscent();
        int x = (width - letterWidth) / 2;
        int y = (height - letterHeight) / 2 + letterHeight - 2;

        // Draw the letter in the middle of the circle
        g2d.drawString(letter, x, y);

        g2d.dispose();

        // Save image as PNG
        String imagePath = getCommandsIconsPath(commandExecutable);
        try {
            ImageIO.write(image, "png", new File(imagePath));
            log.info("Image for image path [{}] was created", imagePath);
        } catch (IOException e) {
            log.error(String.format("Failed to write to image path [%s]", imagePath), e);
        }
    }

    private static GradientPaint getGradientPaint(Color color, int width, int height) {
        Color lighterColor = new Color(color.getRed() > COLOR_PART_LIGHTER_VAL ? color.getRed() - COLOR_PART_LIGHTER_VAL : color.getRed(),
                color.getGreen() > COLOR_PART_LIGHTER_VAL ? color.getGreen() - COLOR_PART_LIGHTER_VAL :  color.getGreen(),
                color.getBlue() > COLOR_PART_LIGHTER_VAL ? color.getBlue() - COLOR_PART_LIGHTER_VAL : color.getBlue());

        // Create gradient paint for the circle
        return new GradientPaint(0, 0, lighterColor, width, height, color, true);
    }

    private static String getCommandsIconsPath(String commandExecutable) {
        FileSystemUtils.createDirectoryIfNotExists(FileSystemUtils.getKeepasteDirectory().concat("/commands_icons"));
        return FileSystemUtils.getKeepasteDirectory().concat("/commands_icons/".concat(commandExecutable).concat(".png"));
    }

    private static Color getCommandColor(String command) {
        Map<String, Color> knownCommandsColors = new HashMap<>();
        knownCommandsColors.put("aws", AWS_COLOR);
        knownCommandsColors.put("mvn", MVN_COLOR);
        knownCommandsColors.put("terraform", TERRAFORM_COLOR);
        knownCommandsColors.put("git", GIT_COLOR);
        knownCommandsColors.put("kubectl", KUBECTL_COLOR);
        knownCommandsColors.put("docker", DOCKER_COLOR);
        return knownCommandsColors.get(command.toLowerCase());
    }

}
