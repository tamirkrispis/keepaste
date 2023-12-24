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

package com.keepaste.logic.common;

import com.keepaste.logic.models.Model;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is a base for all subjects that can have observers registered for their events in order to be updated upon state changes.
 */
public abstract class BaseSubject implements Subject {

    private final List<Observer> observers = new ArrayList<>();

    /**
     * Adds an observer to the subject.
     *
     * @param observer the {@code Observer}
     */
    @Override
    public void registerObserver(@NonNull final Observer observer) {
        observers.add(observer);
    }

    /**
     * Removes an observer from the subject.
     *
     * @param observer the {@code Observer}
     */
    @Override
    public void removeObserver(@NonNull final Observer observer) {
        observers.remove(observer);
    }

    /**
     * Will updated all the registered observers with the certain model in context.
     *
     * @param model the model in context
     */
    @Override
    public void updateAllObservers(Model model) {
        observers.parallelStream().forEach(observer -> observer.updateObserver(model));
    }
}
