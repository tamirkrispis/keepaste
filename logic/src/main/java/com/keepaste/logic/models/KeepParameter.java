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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This class represents a single Keep parameter.
 */
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class KeepParameter {
    public KeepParameter(final String name, final String phrase) {
        this.name = name;
        this.phrase = phrase;
        this.isGlobal = false;
    }

    private String name;
    private String phrase;
    private boolean isGlobal;

    @Override
    public String toString() {
        return String.format("%s (%s)", name, phrase == null ? "<free text>" : phrase);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof KeepParameter) {
            KeepParameter keepParameter = (KeepParameter) obj;
            return this.name.equals(keepParameter.getName()) && this.phrase.equals(keepParameter.getPhrase());
        }
        return false;
    }
}
