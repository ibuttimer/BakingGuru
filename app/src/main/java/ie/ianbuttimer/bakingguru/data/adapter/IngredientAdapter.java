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


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ie.ianbuttimer.bakingguru.R;
import ie.ianbuttimer.bakingguru.bake.AbstractBakeObject;
import ie.ianbuttimer.bakingguru.bake.Ingredient;

/**
 * Adapter class for a RecyclerView of Ingredient objects
 */
@SuppressWarnings("unused")
public class IngredientAdapter extends AbstractBakeRecycleViewAdapter<Ingredient> {

    /**
     * Constructor
     * @param objects       The objects to represent in the list.
     * @param clickHandler  Click handler for the views in this adapter
     */
    public IngredientAdapter(@NonNull List<Ingredient> objects, @Nullable IAdapterOnClickHandler<Ingredient> clickHandler) {
        super(objects, clickHandler, R.layout.ingredient_list_item);
    }

    /**
     * Constructor
     * @param objects       The objects to represent in the list.
     * @param clickHandler  Click handler for the views in this adapter
     */
    public IngredientAdapter(@NonNull Ingredient[] objects, @Nullable IAdapterOnClickHandler<Ingredient> clickHandler) {
        super(objects, clickHandler, R.layout.ingredient_list_item);
    }

    @Override
    public AbstractBakeViewHolder<? extends AbstractBakeObject> getNewViewHolder(View view, IAdapterOnClickHandler<Ingredient> clickHandler) {
        return new IngredientViewHolder(view, clickHandler);
    }


    /**
     * A RecyclerView.ViewHolder for Ingredient objects
     */

    class IngredientViewHolder extends AbstractBakeViewHolder<Ingredient> {

        @BindView(R.id.tv_quantity_ingredient_list_item) TextView mQuantityTextView;
        @BindView(R.id.tv_measure_ingredient_list_item) TextView mMeasureTextView;
        @BindView(R.id.tv_ingredient_ingredient_list_item) TextView mIngredientTextView;

        private Unbinder unbinder;

        /**
         * Constructor
         * @param view          View to hold
         * @param clickHandler  onClick handler for view
         */
        IngredientViewHolder(View view, IAdapterOnClickHandler<Ingredient> clickHandler) {
            super(view, clickHandler);

            unbinder = ButterKnife.bind(this, view);
        }


        @Override
        public void setViewInfo(Ingredient info) {
            super.setViewInfo(info);
            mQuantityTextView.setText(IngredientsAdapterViewBinder.formatQuantity(info.getQuantity()));
            mMeasureTextView.setText(info.getMeasure());
            mIngredientTextView.setText(info.getIngredient());
        }

    }
}
