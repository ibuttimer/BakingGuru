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

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import ie.ianbuttimer.bakingguru.bake.AbstractBakeObject;


/**
 * A base RecyclerView.ViewHolder for AbstractBakeObject objects
 */
@SuppressWarnings("unused")
abstract class AbstractBakeViewHolder<T extends AbstractBakeObject> extends RecyclerView.ViewHolder implements View.OnClickListener, IViewHolder {

    private final View mView;
    private IAdapterOnClickHandler<T> mClickHandler;
    private T mItem;

    /**
     * Constructor
     * @param view          View to hold
     * @param clickHandler  onClick handler for view
     */
    AbstractBakeViewHolder(View view, IAdapterOnClickHandler<T> clickHandler) {
        super(view);

        mView = view;
        mClickHandler = clickHandler;

        view.setOnClickListener(this);
    }

    /**
     * Get a context reference
     * @return  context reference
     */
    public Context getContext() {
        return mView.getContext();
    }

    /**
     * Get the view
     * @return  view reference
     */
    public View getView() {
        return mView;
    }

    /**
     * Get the object associated with this view
     * @return  object
     */
    public T getItem() {
        return mItem;
    }

    /**
     * Set the details to display<br>
     * This method should be overridden in sub classes, ensuring to call super.setViewInfo()
     * @param info   Information object to use
     */
    public void setViewInfo(T info) {
        mItem = info;
    }

    @Override
    public void onClick(View v) {
        // pass the click onto the click handler
        if (mClickHandler != null) {
            mClickHandler.onItemClick(v, mItem);
        }
    }

    /**
     * Set the click handler
     * @param clickHandler  onClick handler for view
     */
    public void setClickHandler(IAdapterOnClickHandler<T> clickHandler) {
        this.mClickHandler = clickHandler;
    }
}
