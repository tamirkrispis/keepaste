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

package com.keepaste.logic.models;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.formdev.flatlaf.util.StringUtils;
import com.keepaste.logic.managers.KeepExecutionManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.apache.commons.text.StringEscapeUtils;
import java.util.List;

/**
 * This class represents a Keep which is a data unit for storing a phrase, description and its parameters.
 */
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeName("Keep")
public class Keep extends KeepNode {
    // what we want to keep
    private String phrase;
    // a description about what this phrase is used for
    private String description;
    // any parameters to be executed along with the Keep
    private List<KeepParameter> parameters;
    // if to never press 'Enter' after pasting, even if this option in general is on
    private boolean neverPressEnter;

    /**
     * Constructor.
     *
     * @param title     the title of the Keep
     * @param phrase    the phrase to keep
     */
    public Keep(final String title, final String phrase) {
        super(title, null);
        this.phrase = phrase;
    }

    @Override
    public String toString() {
        return title;
    }

    /**
     * Expanded representation of the Keep.
     *
     * @return an expanded representation of the Keep.
     */
    public String toStringAll() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Title: [%s]", title));
        sb.append(String.format( ", Phrase: [%s]", phrase));
        sb.append(", Parameters: {");
        if (parameters != null) {
            for (KeepParameter parameter : parameters) {
                sb.append(parameter);
            }
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * HTML representation of the Keep, as used on Tooltips over the tree nodes.
     *
     * @return HTML representation of the Keep
     */
    public String toStringHTML() {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append(String.format("<b>%s</b><br/>", getTitle()));
        if (getDescription() != null && !getDescription().isEmpty() && !getDescription().isBlank()) {
            sb.append(String.format("%s<br/><br/>", this.getDescription().replace(System.lineSeparator(), "<br/>").replace("\n", "<br/>")));
        }
        sb.append(String.format("<blockquote><i>\"%s\"</i></blockquote><br/><hr>", StringEscapeUtils.escapeHtml4(this.getPhrase())));

        if (parameters != null) {
            for (KeepParameter parameter : parameters) {
                String color = "#b75300";
                String currentValue = "";
                if (parameter.isGlobal()) {
                    color = "#58a6ff";
                }
                if (KeepExecutionManager.getGlobalParameterValuesMap().containsKey(parameter.getName())) {
                    currentValue = ", <b>\"".concat(KeepExecutionManager.getGlobalParameterValuesMap().get(parameter.getName())).concat("\"</b>");
                }
                String type;
                if (StringUtils.isEmpty(parameter.getPhrase())) {
                    type = "free-text";
                } else if (parameter.getPhrase().startsWith("[")) {
                    type = "values";
                } else {
                    type = "command";
                }
                String command = !StringUtils.isEmpty(parameter.getPhrase()) ? "\"".concat(parameter.getPhrase()).concat("\"") : "";
                sb.append(String.format("<b style=\"color: %s\">%s</b> (%s%s)<blockquote><i>%s</i></blockquote>", color, parameter.getName(), type, currentValue, StringEscapeUtils.escapeHtml4(command)));
                sb.append("<br>");
            }
        }
        sb.append("</html>");
        return sb.toString();
    }
}
