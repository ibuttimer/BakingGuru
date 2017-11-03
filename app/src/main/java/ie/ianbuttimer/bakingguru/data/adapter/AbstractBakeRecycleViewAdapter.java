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
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import ie.ianbuttimer.bakingguru.bake.AbstractBakeObject;
import ie.ianbuttimer.bakingguru.R;
import ie.ianbuttimer.bakingguru.utils.ITester;

/**
 * Adapter class for a RecyclerView of movies
 */
@SuppressWarnings("unused")
public abstract class AbstractBakeRecycleViewAdapter<T extends AbstractBakeObject> extends RecyclerView.Adapter<AbstractBakeViewHolder> {

    public static final int DEFAULT_TAG_KEY = R.id.bake_obj_tag;

    protected List<T> mList;         // list of objects represented by this adapter
    protected IAdapterOnClickHandler<T> mClickHandler;
    protected @LayoutRes int mLayoutId;
    protected Object mCommonTag;
    protected @IdRes int mTagKey;

    /**
     * Constructor for single view type
     * @param objects       The objects to represent in the list.
     * @param clickHandler  Click handler for the views in this adapter
     * @param layoutId      Id of layout to inflate
     */
    public AbstractBakeRecycleViewAdapter(@NonNull List<T> objects, @Nullable IAdapterOnClickHandler<T> clickHandler, @LayoutRes int layoutId) {
        init(objects, clickHandler, layoutId);
    }

    /**
     * Constructor for single view type
     * @param objects       The objects to represent in the list.
     * @param clickHandler  Click handler for the views in this adapter
     * @param layoutId      Id of layout to inflate
     */
    public AbstractBakeRecycleViewAdapter(@NonNull T[] objects, @Nullable IAdapterOnClickHandler<T> clickHandler, @LayoutRes int layoutId) {
        init(Arrays.asList(objects), clickHandler, layoutId);
    }

    /**
     * Constructor
     * @param objects       The objects to represent in the list.
     * @param layoutId      Id of layout to inflate
     */
    public AbstractBakeRecycleViewAdapter(@NonNull List<T> objects, @LayoutRes int layoutId) {
        this(objects, null, layoutId);
    }

    /**
     * Constructor
     * @param objects       The objects to represent in the list.
     * @param layoutId      Id of layout to inflate
     */
    public AbstractBakeRecycleViewAdapter(@NonNull T[] objects, @LayoutRes int layoutId) {
        this(objects, null, layoutId);
    }

    /**
     * Initialise object
     * @param objects       The objects to represent in the list.
     * @param clickHandler  Click handler for the views in this adapter
     * @param layoutId      Id of layout to inflate
     */
    protected void init(@NonNull List<T> objects, @Nullable IAdapterOnClickHandler<T> clickHandler, @LayoutRes int layoutId) {
        mList = objects;
        mClickHandler = clickHandler;
        mLayoutId = layoutId;
        mCommonTag = null;              // no common tag by default
        mTagKey = DEFAULT_TAG_KEY;
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
     *                  can use this viewType integer to provide a different layout. See
     *                  {@link RecyclerView.Adapter#getItemViewType(int)}
     *                  for more details.
     * @return A new AbstractTMDbViewHolder that holds the View for each list item
     */
    @Override
    public AbstractBakeViewHolder<? extends AbstractBakeObject> onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // inflate but don't attach
        View view = inflater.inflate(mLayoutId, viewGroup, false);

        return getNewViewHolder(view, mClickHandler);
    }

    /**
     * Create a new view holder
     * @param view          View to insert into view holder
     * @param clickHandler  Click handler
     * @return  View holder
     */
    public abstract AbstractBakeViewHolder<? extends AbstractBakeObject> getNewViewHolder(View view, IAdapterOnClickHandler<T> clickHandler);

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the weather
     * details for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param viewHolder The ViewHolder which should be updated to represent the
     *                   contents of the item at the given position in the data set.
     * @param position   The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(AbstractBakeViewHolder viewHolder, int position) {
        T info = getItem(position);
        if (mCommonTag != null) {
            // set the common tag
            viewHolder.itemView.setTag(mTagKey, mCommonTag);
        } else {
            // set the data set item as the tag
            if (info != null) {
                // set the info object as the view tag for easy retrieval later
                viewHolder.itemView.setTag(mTagKey, info);
            }
        }
        viewHolder.setViewInfo(info);   // set the view's elements to the movie's info
    }

    /**
     * Set the item click handler
     * @param clickHandler  Click handler for the views in this adapter
     */
    public void setClickHandler(IAdapterOnClickHandler<T> clickHandler) {
        mClickHandler = clickHandler;
    }

    /**
     * Set a common tag for item views
     * @param tag   Tag
     */
    public void setCommonTag(Object tag) {
        setCommonTag(mTagKey, tag);
    }

    /**
     * Set a common tag for item views
     * @param key   Tag key
     * @param tag   Tag
     */
    public void setCommonTag(int key, Object tag) {
        mTagKey = key;
        mCommonTag = tag;
    }


    /**
     * Get the data item associated with the specified position in the data set.
     * @param position   Position of the item whose data we want within the adapter's data set.
     * @return The data at the specified position. This value may be null.
     */
    public T getItem(int position) {
        T item = null;
        if (mList != null) {
            item = mList.get(position);
        }
        return item;
    }

    /**
     * Find the data item in the data set matching the test criteria
     * @param tester        Tester to use to find required data
     * @param fromIndex	    Index of the first element, inclusive
     * @param toIndex	    Index of the last element, exclusive
     * @return A Pair with the data at the specified position and its index. If a data item isn't found the Pair will contain null & -1;
     * @throws IllegalArgumentException	if fromIndex > toIndex
     * @throws ArrayIndexOutOfBoundsException	if fromIndex < 0 or toIndex > a.length
     */
    public Pair<T, Integer> findItemAndIndex(ITester<T> tester, int fromIndex, int toIndex) {
        Pair<T, Integer> result = Pair.create(null, -1);
        if (fromIndex > toIndex) {
            throw new IllegalArgumentException("From index greater than to index");
        }
        if (fromIndex < 0) {
            throw new ArrayIndexOutOfBoundsException("From index before start");
        }
        if (mList != null) {
            int length = mList.size();
            if (toIndex > length) {
                throw new ArrayIndexOutOfBoundsException("To index after end");
            }
            for (int i = fromIndex, ll = Math.min(length, toIndex); i < ll; i++) {
                T toTest = mList.get(i);
                if (tester.test(toTest)) {
                    result = Pair.create(toTest, i);
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Find the data item in the data set matching the test criteria
     * @param tester    Tester to use to find required data
     * @param fromIndex	Index of the first element, inclusive
     * @param toIndex	Index of the last element, exclusive
     * @return The data matching the test criteria, or <code>null</code> if not found
     * @throws IllegalArgumentException	if fromIndex > toIndex
     * @throws ArrayIndexOutOfBoundsException	if fromIndex < 0 or toIndex > a.length
     */
    public T findItem(ITester<T> tester, int fromIndex, int toIndex) {
        Pair<T, Integer> result = findItemAndIndex(tester, fromIndex, toIndex);
        return result.first;
    }

    /**
     * Find the data item in the data set matching the test criteria
     * @param tester    Tester to use to find required data
     * @param fromIndex	Index of the first element, inclusive
     * @return The data matching the test criteria, or <code>null</code> if not found
     * @throws ArrayIndexOutOfBoundsException	if fromIndex < 0 or toIndex > a.length
     */
    public T findItem(ITester<T> tester, int fromIndex) {
        T item = null;
        if (mList != null) {
            item = findItem(tester, fromIndex, mList.size());
        }
        return item;
    }

    /**
     * Find the data item in the data set matching the test criteria
     * @param tester    Tester to use to find required data
     * @return The data matching the test criteria, or <code>null</code> if not found
     */
    public T findItem(ITester<T> tester) {
        return findItem(tester, 0);
    }

    /**
     * Find the index of a data item in the data set matching the test criteria
     * @param tester    Tester to use to find required data
     * @param fromIndex	Index of the first element, inclusive
     * @param toIndex	Index of the last element, exclusive
     * @return The index of the data, or -1 if nothing is found
     * @throws IllegalArgumentException	if fromIndex > toIndex
     * @throws ArrayIndexOutOfBoundsException	if fromIndex < 0 or toIndex > a.length
     */
    public int findItemIndex(ITester<T> tester, int fromIndex, int toIndex) {
        Pair<T, Integer> result = findItemAndIndex(tester, fromIndex, toIndex);
        return result.second;
    }

    /**
     * Find the index of a data item in the data set matching the test criteria
     * @param tester    Tester to use to find required data
     * @param fromIndex	Index of the first element, inclusive
     * @return The index of the data, or -1 if nothing is found
     * @throws ArrayIndexOutOfBoundsException	if fromIndex < 0 or toIndex > a.length
     */
    public int findItemIndex(ITester<T> tester, int fromIndex) {
        int index = -1;
        if (mList != null) {
            index = findItemIndex(tester, fromIndex, mList.size());
        }
        return index;
    }

    /**
     * Find the index of a data item in the data set matching the test criteria
     * @param tester    Tester to use to find required data
     * @return The index of the data, or -1 if nothing is found
     */
    public int findItemIndex(ITester<T> tester) {
        return findItemIndex(tester, 0);
    }

    /**
     * This method simply returns the number of items to display
     * @return The number of items available in the dataset.
     */
    @Override
    public int getItemCount() {
        int length = 0;
        if (mList != null) {
            length = mList.size();
        }
        return length;
    }

    /**
     * Adds the specified object at the end of the list.
     * Note: Remember to call notifyDataSetChanged() when finished adding items to the list
     * @param item  The object to add at the end of the list.
     */
    public void add(T item) {
        mList.add(item);
    }

    /**
     * Adds the specified items at the end of the list.
     * Note: Remember to call notifyDataSetChanged() when finished adding items to the list
     * @param items The items to add at the end of the list.
     */
    public void addAll(T... items) {
        addAll(Arrays.asList(items));
    }

    /**
     * Adds the specified items at the end of the list.
     * Note: Remember to call notifyDataSetChanged() when finished adding items to the list
     * @param collection The Collection to add at the end of the list.
     */
    public void addAll(Collection<? extends T> collection) {
        mList.addAll(collection);
    }

    /**
     * Removes the element at the specified position in this list.
     * @param position   The index of the element to be removed.
     * @return The data at the specified position. This value may be null.
     * @throws UnsupportedOperationException    if the remove operation is not supported by this list
     * @throws IndexOutOfBoundsException    if the index is out of range (index < 0 || index >= size())
     */
    public T remove(int position) {
        T item = null;
        if (mList != null) {
            item = mList.remove(position);
        }
        return item;
    }

    /**
     * Get a list iterator
     * @return  Iterator
     */
    public Iterator<T> iterator () {
        Iterator<T> iterator = null;
        if (mList != null) {
            iterator = mList.iterator();
        }
        return iterator;
    }

    /**
     * Remove all elements from the list.
     * Note: Remember to call notifyDataSetChanged() when finished adding items to the list
     */
    public void clear() {
        mList.clear();
    }

    /**
     * Sets the list to the specified items.
     * Note: Remember to call notifyDataSetChanged() when finished adding items to the list
     * @param items The items to set the list.
     */
    public void setList(T... items) {
        setList(Arrays.asList(items));
    }

    /**
     * Sets the list to the specified items.
     * Note: Remember to call notifyDataSetChanged() when finished adding items to the list
     * @param collection The Collection to set the list.
     */
    public void setList(Collection<? extends T> collection) {
        mList.clear();
        mList.addAll(collection);
    }

    /**
     * Sort the data set
     * @param comparator    Comparator to sort data set
     */
    public void sortList(Comparator<Object> comparator) {
        // based on https://developer.android.com/reference/java/util/List.html#sort(java.util.Comparator<? super E>)
        Object[] elements = mList.toArray();
        Arrays.sort(elements, comparator);
        ListIterator<T> iterator = mList.listIterator();
        for (Object element : elements) {
            iterator.next();
            iterator.set((T) element);
        }
    }

}
