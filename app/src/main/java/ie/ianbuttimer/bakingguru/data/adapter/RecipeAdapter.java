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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ie.ianbuttimer.bakingguru.bake.AbstractBakeObject;
import ie.ianbuttimer.bakingguru.bake.Recipe;
import ie.ianbuttimer.bakingguru.R;

/**
 * Adapter class for a RecyclerView of Recipe objects
 */

public class RecipeAdapter extends AbstractBakeRecycleViewAdapter<Recipe> {

    /**
     * Constructor
     * @param objects       The objects to represent in the list.
     * @param clickHandler  Click handler for the views in this adapter
     */
    public RecipeAdapter(@NonNull List<Recipe> objects, @Nullable IAdapterOnClickHandler<Recipe> clickHandler) {
        super(objects, clickHandler, R.layout.recipe_list_item);
    }


    @Override
    public AbstractBakeViewHolder<? extends AbstractBakeObject> getNewViewHolder(View view, IAdapterOnClickHandler<Recipe> clickHandler) {
        return new RecipeViewHolder(view, clickHandler);
    }

    /**
     * A RecyclerView.ViewHolder for Recipe objects
     */

    class RecipeViewHolder extends AbstractBakeViewHolder<Recipe> {

        @BindView(R.id.tv_name_recipe_list_item) TextView mNameTextView;
        @BindView(R.id.iv_poster_recipe_list_item) ImageView mImageImageView;

        private Unbinder unbinder;

        /**
         * Constructor
         * @param view          View to hold
         * @param clickHandler  onClick handler for view
         */
        RecipeViewHolder(View view, IAdapterOnClickHandler<Recipe> clickHandler) {
            super(view, clickHandler);

            unbinder = ButterKnife.bind(this, view);
        }


        @Override
        public void setViewInfo(Recipe info) {
            Context context = getContext();

            mNameTextView.setText(info.getName());
            mImageImageView.setImageResource(info.getType().getImage(context));
        }

    }
}
