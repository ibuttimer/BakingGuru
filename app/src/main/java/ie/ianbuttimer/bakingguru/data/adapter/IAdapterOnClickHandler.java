/*
 * Copyright (c) 2017 Ian Buttimer.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ie.ianbuttimer.bakingguru.data.adapter;

import android.view.View;

import ie.ianbuttimer.bakingguru.bake.AbstractBakeObject;

/**
 * Interface to handle events for adapters
 */

public interface IAdapterOnClickHandler<T extends AbstractBakeObject> {

    /**
     * Process the click event
     * @param view  View that was clicked
     */
    void onItemClick(View view);

    /**
     * Process the click event
     * @param view  View that was clicked
     * @param item  Item represented by view
     */
    void onItemClick(View view, T item);
}
