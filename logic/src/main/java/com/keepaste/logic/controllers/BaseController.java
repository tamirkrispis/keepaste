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

package com.keepaste.logic.controllers;

import com.keepaste.logic.common.BaseSubject;
import com.keepaste.logic.common.Observer;
import com.keepaste.logic.views.View;
import org.apache.maven.model.Model;

/**
 * This class is a base controller (MVC), all controllers are both subjects and observers as they observe changes in {@link Model}
 * and notifies the relevant {@link View}.
 */
public abstract class BaseController extends BaseSubject implements Observer {
    // doesn't hold any specific implementation
}
