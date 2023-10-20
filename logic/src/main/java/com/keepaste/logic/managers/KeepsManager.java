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

package com.keepaste.logic.managers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.keepaste.logic.models.Keep;
import com.keepaste.logic.models.KeepsGroup;
import com.keepaste.logic.models.KeepNode;
import com.keepaste.logic.models.KeepParameter;
import com.keepaste.logic.utils.FileSystemUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * This utility class holds methods for managing {@link Keep}s.
 */
@Log4j2
public final class KeepsManager {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Getter
    private KeepsGroup rootNode;

    /* ***************** PUBLIC METHODS ***************** */

    /**
     * Load Keeps into the tree.
     *
     * @return the root node of the tree
     * @throws IOException in case of failure
     */
    public KeepsGroup loadKeeps() throws IOException {
        return loadKeeps(getKeepsFile());
    }

    /**
     * Will load Keeps into the tree from a given specific file.
     *
     * @param keepsFile the Keeps filename
     * @return the root node of the tree
     */
    public KeepsGroup loadKeeps(File keepsFile) {
        try {
            rootNode = objectMapper.readValue(keepsFile, KeepsGroup.class);
            return rootNode;
        } catch (IOException e) {
            log.error("Failed to load Keeps", e);
        }
        return null;
    }

    /**
     * Will save the current tree structure into a file.
     *
     * @param rootNodeContext   the root node of the tree
     */
    public void saveKeeps(KeepsGroup rootNodeContext) {
        try {
            saveKeeps(rootNodeContext, getKeepsFile());
        } catch (Exception ex) {
            log.error("Failed to save Keeps", ex);
        }
    }

    /**
     * Will save the current tree structure into a file.
     *
     * @param rootNodeContext   the root node of the tree
     * @param toFile            the {@link File} where to persist the tree structure
     */
    public void saveKeeps(KeepsGroup rootNodeContext, File toFile) {
        try {
            if (rootNodeContext != null) {
                backupKeepsFile(toFile);
                objectMapper.writeValue(toFile, rootNodeContext);
            }
        } catch (IOException e) {
            log.error("Failed to save Keeps", e);
        }
    }

    /**
     * Will sort the tree alphabetically.
     */
    public void sort() {
        sort(rootNode);
    }

    /**
     * Will sort a given node's children alphabetically.
     *
     * @param node the root node to sort its children
     */
    public void sort(KeepNode node) {
        if (node instanceof KeepsGroup) {
            KeepsGroup group = (KeepsGroup) node;
            for (KeepNode childNode : group.getKeepsNodes()) {
                sort(childNode);
            }
            group.getKeepsNodes().sort((keepNode1, keepNode2) -> {
                // keep groups before keeps
                if (keepNode1 instanceof KeepsGroup && keepNode2 instanceof Keep) {
                    return -1;
                } else if (keepNode2 instanceof KeepsGroup && keepNode1 instanceof Keep) {
                    return 1;
                } else { // same types of nodes will be sorted by their title
                    return keepNode1.getTitle().compareTo(keepNode2.getTitle());
                }
            });
        }
    }

    /**
     * Will return all unique parameters from across the entire tree, this is used for adding an existing parameter on the
     * edit Keep dialog.
     *
     * @return a List of {@link KeepParameter}s
     */
    public List<KeepParameter> getAllUniqueParameters() {
        log.debug("Getting all unique parameters");
        List<KeepParameter> parametersList = new ArrayList<>();
        extractParamsFromNode(rootNode, parametersList);
        parametersList.sort((o1, o2) -> String.CASE_INSENSITIVE_ORDER.compare(o1.toString(), o2.toString()));
        return parametersList;
    }

    /**
     * Will return a json representation of a {@link Keep}.
     *
     * @param keep  the keep
     * @return      a json representation of the keep
     * @throws JsonProcessingException upon failure
     */
    public String getKeepJson(KeepNode keep) throws JsonProcessingException {
        log.debug("Getting a keep as json string");
        return objectMapper.writeValueAsString(keep);
    }

    /**
     * Will return a {@link KeepNode} out of a JSON {@link File}.
     *
     * @param jsonFile      the json file
     * @return a {@link KeepNode} out of a JSON {@link File}.
     * @throws IOException upon failure
     */
    public KeepNode getKeepFromJsonFile(File jsonFile) throws IOException {
        log.debug("Getting a keep form json file");
        return objectMapper.readValue(jsonFile, KeepNode.class);
    }

    /**
     * Gets the path to the keeps file.
     *
     * @return the path to the keeps file.
     */
    public String getKeepsFilePathString() {
        return FileSystemUtils.getKeepasteDirectory().concat("/keeps.json");
    }

    /* ***************** PRIVATE METHODS ***************** */

    private void setDefaultKeepsTreeFromFile() throws IOException {
        try (InputStream inputStream = KeepsManager.class.getResourceAsStream("/defaultkeeps/default_keeps.json")) {
            if (inputStream != null) {
                String fileContents = new String(inputStream.readAllBytes());
                String file = getKeepsFilePathString();
                try (FileWriter writer = new FileWriter(file)) {
                    log.debug(writer);
                    writer.write(fileContents);
                    log.info("Saved ".concat(file));
                }
            }
        }
    }

    private void backupKeepsFile(File sourceFile) throws IOException {
        File targetFile = new File(getKeepsBackupFilePathString());
        Files.copy(sourceFile.toPath(), targetFile.toPath(), REPLACE_EXISTING);
    }

    private void extractParamsFromNode(KeepNode node, List<KeepParameter> parameterList) {
        if (node instanceof Keep) {
            // keep, adding all parameters to the list
            Keep keep = (Keep) node;
            if (keep.getParameters() != null) {
                for (KeepParameter p : keep.getParameters()) {
                    if (!parameterList.contains(p)) {
                        parameterList.add(p);
                    }
                }
            }
        } else {
            // keep group
            for (KeepNode childNode : ((KeepsGroup) node).getKeepsNodes()) {
                extractParamsFromNode(childNode, parameterList);
            }
        }
    }


    private File getKeepsFile() throws IOException {
        log.debug("Getting keeps file");
        File keepsFile = new File(getKeepsFilePathString());
        if (!keepsFile.exists()) {
            log.info("Keeps file doesn't exist, saving and returning default keeps tree");
            setDefaultKeepsTreeFromFile();
        }
        log.debug("Returned keeps file [{}]", keepsFile);
        return keepsFile;
    }

    private String getKeepsBackupFilePathString() {
        return FileSystemUtils.getKeepasteDirectory().concat("/keeps_bck.json");
    }
}
