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
import android.widget.TextView;

import java.text.DecimalFormat;


import butterknife.BindView;
import ie.ianbuttimer.bakingguru.R;
import ie.ianbuttimer.bakingguru.bake.Ingredient;

/**
 * Utility class for use with ingredient_list_item.xml layout
 */

public class IngredientsAdapterViewBinder extends AbstractAdapterViewBinder<Ingredient>{

    @BindView(R.id.tv_quantity_ingredient_list_item) TextView mQuantityTextView;
    @BindView(R.id.tv_measure_ingredient_list_item) TextView mMeasureTextView;
    @BindView(R.id.tv_ingredient_ingredient_list_item) TextView mIngredientTextView;

    protected static final int[] textViewIds = new int[] {
            R.id.tv_quantity_ingredient_list_item,
            R.id.tv_measure_ingredient_list_item,
            R.id.tv_ingredient_ingredient_list_item
    };

    /**
     * Constructor
     * @param view  View to bind
     */
    public IngredientsAdapterViewBinder(View view) {
        super(view);
    }


    /**
     * Format the quantity text to display for an ingredient
     * @param quantity  Ingredient quantity
     * @return  Text to display
     */
    public static String formatQuantity(Double quantity) {
        String format;
        Double quantity100 = quantity * 100;
        if (quantity100.intValue() < quantity100) {
            format = "0.00";    // only 2 decimal places
        } else if (quantity.intValue() < quantity) {
            format = "0.0";     // 1 decimal place
        } else {
            format = "0";       // integer
        }
        DecimalFormat formatter = new DecimalFormat(format);
        return formatter.format(quantity);
    }

    @Override
    public void setViewInfo(@IdRes int id, Ingredient info) {
        switch (id) {
            case R.id.tv_quantity_ingredient_list_item:
                mQuantityTextView.setText(formatQuantity(info.getQuantity()));
                break;
            case R.id.tv_measure_ingredient_list_item:
                mMeasureTextView.setText(info.getMeasure());
                break;
            case R.id.tv_ingredient_ingredient_list_item:
                mIngredientTextView.setText(info.getIngredient());
                break;
        }
    }

    @Override
    public void setViewInfo(Ingredient info) {
        for (int id : textViewIds) {
            setViewInfo(id, info);
        }
    }

}
