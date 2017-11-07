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
import android.widget.TextView;

import com.ahamed.multiviewadapter.ItemViewHolder;

import butterknife.BindView;
import butterknife.ButterKnife;
import ie.ianbuttimer.bakingguru.R;
import ie.ianbuttimer.bakingguru.bake.Ingredient;

/**
 * Ingredient binder class for the MultiViewAdapter library
 */
@SuppressWarnings("unused")
public class IngredientBinder extends AbstractBinder<Ingredient, IngredientBinder.ViewHolder> {

    /**
     * Default constructor
     */
    public IngredientBinder() {
        super(R.layout.ingredient_list_item);
    }

    /**
     * Constructor
     * @param clickListener     Click listener
     */
    public IngredientBinder(ItemViewHolder.OnItemClickListener<Ingredient> clickListener) {
        super(R.layout.ingredient_list_item, clickListener);
    }

    @Override
    public void bind(ViewHolder holder, Ingredient item) {
        holder.setViewInfo(item);
    }

    @Override
    protected ViewHolder getViewHolder(View itemView) {
        return new ViewHolder(itemView);
    }

    @Override
    public boolean canBindData(Object item) {
        return (item instanceof Ingredient);
    }

    /**
     * ViewHolder class
     */
    class ViewHolder extends ItemViewHolder<Ingredient> implements IViewHolder {

        @BindView(R.id.tv_quantity_ingredient_list_item) TextView mQuantityTextView;
        @BindView(R.id.tv_measure_ingredient_list_item) TextView mMeasureTextView;
        @BindView(R.id.tv_ingredient_ingredient_list_item) TextView mIngredientTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            setItemClickListener(getClickListener());
        }

        public void setViewInfo(Ingredient info) {
            mQuantityTextView.setText(IngredientsAdapterViewBinder.formatQuantity(info.getQuantity()));
            mMeasureTextView.setText(info.getMeasure());
            mIngredientTextView.setText(info.getIngredient());
        }

        @Override
        public void onViewRecycled() {
            // no op
        }
    }
}
