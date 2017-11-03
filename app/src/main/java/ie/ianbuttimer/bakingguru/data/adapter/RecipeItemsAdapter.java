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

import com.ahamed.multiviewadapter.DataListManager;
import com.ahamed.multiviewadapter.ItemViewHolder;
import com.ahamed.multiviewadapter.RecyclerAdapter;

import java.util.Arrays;

import ie.ianbuttimer.bakingguru.bake.AbstractBakeObject;
import ie.ianbuttimer.bakingguru.bake.Ingredient;
import ie.ianbuttimer.bakingguru.bake.Step;

/**
 * A RecyclerView Adapter to handle recipe ingredients & steps
 */
@SuppressWarnings("unused")
public class RecipeItemsAdapter extends RecyclerAdapter {

    private DataListManager<Ingredient> ingredientDataManager;
    private DataListManager<Step> stepDataManager;
    private IngredientBinder ingredientBinder;
    private StepBinder stepBinder;

    /**
     * Default constructor
     */
    public RecipeItemsAdapter() {
        ingredientDataManager = new DataListManager<>(this);
        stepDataManager = new DataListManager<>(this);

        addDataManager(ingredientDataManager);
        addDataManager(stepDataManager);

        ingredientBinder = new IngredientBinder();
        stepBinder = new StepBinder();

        registerBinder(ingredientBinder);
        registerBinder(stepBinder);
    }

    /**
     * Get an Ingredient control object
     * @return  new Ingredient control object
     */
    public RecyclerAdapterControl<Ingredient> getIngredientCtrl() {
        return new RecyclerAdapterControl<>(ingredientDataManager, ingredientBinder);
    }

    /**
     * Get an Step control object
     * @return  new Step control object
     */
    public RecyclerAdapterControl<Step> getStepCtrl() {
        return new RecyclerAdapterControl<>(stepDataManager, stepBinder);
    }

    /**
     * Class providing access to the RecyclerAdapter control elements
     * @param <T>   Type of the object controlled
     */
    public class RecyclerAdapterControl<T extends AbstractBakeObject> {
        private DataListManager<T> dataManager;
        private AbstractBinder<T, ? extends ItemViewHolder<T>> itemBinder;

        public RecyclerAdapterControl(DataListManager<T> dataManager, AbstractBinder<T, ? extends ItemViewHolder<T>> itemBinder) {
            this.dataManager = dataManager;
            this.itemBinder = itemBinder;
        }

        /**
         * The the onClick listener for Ingredient items
         * @param clickListener click listener
         */
        public void setClickListener(ItemViewHolder.OnItemClickListener<T> clickListener) {
            itemBinder.setClickListener(clickListener);
        }

        /**
         * Adds the specified object at the end of the list.
         * Note: notifyDataSetChanged() is automatically called
         * @param item  The object to add at the end of the list.
         */
        public void add(T item) {
            dataManager.add(item);
        }

        /**
         * Adds the specified items at the end of the list.
         * Note: notifyDataSetChanged() is automatically called
         * @param items The items to add at the end of the list.
         */
        public void addAll(T... items) {
            dataManager.addAll(Arrays.asList(items));
        }

        /**
         * Set a tag for item views
         * @param key   Tag key
         * @param tag   Tag
         */
        public void setTag(int key, Object tag) {
            itemBinder.setTag(key, tag);
        }

    }

}
