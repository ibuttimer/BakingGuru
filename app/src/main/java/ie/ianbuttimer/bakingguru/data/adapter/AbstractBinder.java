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

import android.support.annotation.LayoutRes;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ahamed.multiviewadapter.ItemBinder;
import com.ahamed.multiviewadapter.ItemViewHolder;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import ie.ianbuttimer.bakingguru.R;
import ie.ianbuttimer.bakingguru.bake.Ingredient;

/**
 * Ingredient binder class for the MultiViewAdapter library
 */
@SuppressWarnings("unused")
public abstract class AbstractBinder<T, V extends ItemViewHolder<T>> extends ItemBinder<T, V> {

    @LayoutRes private int mLayoutId;
    private ItemViewHolder.OnItemClickListener<T> mClickListener;
    private SparseArray<Object> mTags;

    /**
     * Default constructor
     */
    public AbstractBinder(@LayoutRes int layoutId) {
        super();
        mLayoutId = layoutId;
        mTags = new SparseArray<>();
    }

    /**
     * Constructor
     * @param clickListener     Click listener
     */
    public AbstractBinder(@LayoutRes int layoutId, ItemViewHolder.OnItemClickListener<T> clickListener) {
        this(layoutId);
        mClickListener = clickListener;
    }

    @Override
    public V create(LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(mLayoutId, parent, false);
        for (int i = 0, ll = mTags.size(); i < ll; i++) {
            int key = mTags.keyAt(i);
            view.setTag(key, mTags.get(key));
        }
        return getViewHolder(view);
    }

    protected abstract V getViewHolder(View itemView);

    /**
     * set the click listener for view holders
     * @param clickListener     Click listener
     */
    public void setClickListener(ItemViewHolder.OnItemClickListener<T> clickListener) {
        this.mClickListener = clickListener;
    }

    /**
     * Get the click listener for view holders
     * @return clickListener     Click listener
     */
    public ItemViewHolder.OnItemClickListener<T> getClickListener() {
        return mClickListener;
    }

    /**
     * Set a tag for item views
     * @param key   Tag key
     * @param tag   Tag
     */
    public void setTag(int key, Object tag) {
        mTags.append(key, tag);
    }

}
