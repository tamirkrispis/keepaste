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

package com.keepaste.logic.common;

import com.keepaste.logic.models.Model;

/**
 * This interface is for implementing Observers to register to a subject in order to get notifications upon Subject's state changes.
 */
public interface Observer {

    /**
     * The method that the Subject will call once it needs to update the object with any change to the model in context.
     *
     * @param model the {@link Model} in context
     */
    void updateObserver(Model model);
}
