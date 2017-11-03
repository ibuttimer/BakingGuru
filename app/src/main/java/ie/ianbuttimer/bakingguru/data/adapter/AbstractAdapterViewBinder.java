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

import android.support.annotation.IdRes;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Utility class for use with ingredient_list_item.xml layout
 */

public abstract class AbstractAdapterViewBinder<T> {

    protected Unbinder unbinder;

    /**
     * Constructor
     * @param view  View to bind
     */
    public AbstractAdapterViewBinder(View view) {
        unbinder = ButterKnife.bind(this, view);
    }

    /**
     * Set the view information
     * @param info  Object to populate the view from
     */
    public abstract void setViewInfo(T info);

    /**
     * Set the view information
     * @param id    Id of view to set
     * @param info  Object to populate the view from
     */
    public abstract void setViewInfo(@IdRes int id, T info);

    public void unbind() {
        unbinder.unbind();
    }

}
