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

/**
 * Interface for common functionality between ie.ianbuttimer.bakingguru.data.adapter.AbstractBakeViewHolder
 */

public interface IViewHolder {

    /**
     * Called when a view created by an adapter has been recycled.<br>
     * If an item view has large or expensive data bound to it such as large bitmaps, this may be a good place to release those resources.
     * @see android.support.v7.widget.RecyclerView.Adapter#onViewRecycled(android.support.v7.widget.RecyclerView.ViewHolder)
     */
    void onViewRecycled();


}
