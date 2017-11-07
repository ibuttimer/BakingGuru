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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ie.ianbuttimer.bakingguru.R;
import ie.ianbuttimer.bakingguru.bake.AbstractBakeObject;
import ie.ianbuttimer.bakingguru.bake.Step;
import ie.ianbuttimer.bakingguru.image.AbstractImageLoader;
import ie.ianbuttimer.bakingguru.image.ThumbnailImageLoader;

import static ie.ianbuttimer.bakingguru.bake.IngredientsStep.INGREDIENTS_STEP_ID;

/**
 * Adapter class for a RecyclerView of Step objects
 */
@SuppressWarnings("unused")
public class StepAdapter extends AbstractBakeRecycleViewAdapter<Step> {

    /**
     * Constructor
     * @param objects       The objects to represent in the list.
     * @param clickHandler  Click handler for the views in this adapter
     */
    public StepAdapter(@NonNull List<Step> objects, @Nullable IAdapterOnClickHandler<Step> clickHandler) {
        super(objects, clickHandler, R.layout.step_list_item);
    }

    /**
     * Constructor
     * @param objects       The objects to represent in the list.
     * @param clickHandler  Click handler for the views in this adapter
     */
    public StepAdapter(@NonNull Step[] objects, @Nullable IAdapterOnClickHandler<Step> clickHandler) {
        super(objects, clickHandler, R.layout.step_list_item);
    }


    @Override
    public AbstractBakeViewHolder<? extends AbstractBakeObject> getNewViewHolder(View view, IAdapterOnClickHandler<Step> clickHandler) {
        return new StepViewHolder(view, clickHandler);
    }

    @Override
    public void onViewRecycled(AbstractBakeViewHolder holder) {
        StepViewHolder viewHolder = (StepViewHolder) holder;
        viewHolder.onViewRecycled();

        super.onViewRecycled(holder);
    }

    /**
     * Get the step id display text
     * @param info  Step
     * @return  display id
     */
    public static String getIdText(Step info) {
        int id = info.getId() + 1;  // steps as 0-based
        String textId = "";
        if (id > 0) {
            textId = String.format(Locale.getDefault(), "%d", id);   // step id's are 0-based
        }
        return textId;
    }


    /**
     * A RecyclerView.ViewHolder for Step objects
     */

    class StepViewHolder extends AbstractBakeViewHolder<Step> {

        @BindView(R.id.tv_number_step_list_item) TextView mNumberTextView;
        @BindView(R.id.tv_description_step_list_item) TextView mDescriptionTextView;
        @BindView(R.id.iv_thumbnail_step_list_item) ImageView mImageImageView;
        @BindView(R.id.pb_thumbnail_step_list_item) ProgressBar mImageProgressBar;

        private BakeViewImageLoader<Step> mImageLoader;
        private Unbinder unbinder;

        /**
         * Constructor
         * @param view          View to hold
         * @param clickHandler  onClick handler for view
         */
        StepViewHolder(View view, IAdapterOnClickHandler<Step> clickHandler) {
            super(view, clickHandler);

            unbinder = ButterKnife.bind(this, view);

            mImageLoader = new BakeViewImageLoader<>(mImageImageView, new BakeViewImageLoader.IBakeViewImageLoader<Step>() {
                @Override
                public void setDefaultImage() {
                    setDefaultStepImage();
                }

                @Override
                public AbstractImageLoader<Step> getNewImageLoader() {
                    return new ThumbnailImageLoader(mImageImageView, mImageProgressBar);
                }
            });
        }


        @Override
        public void setViewInfo(Step info) {
            super.setViewInfo(info);
            mNumberTextView.setText(getIdText(info));
            mDescriptionTextView.setText(info.getShortDescription());

            if (info.getId() == INGREDIENTS_STEP_ID) {
                // no image for ingredient step
                mImageLoader.hideImage();
            } else {
                // set image
                mImageLoader.showImage();
                mImageLoader.setImage(info);
            }
        }

        /**
         * Set the default image
         */
        void setDefaultStepImage() {
            mImageImageView.setImageResource(R.drawable.ic_mixer);
        }

        /**
         * Cancel any in progresss ot pending requests
         */
        @Override
        public void onViewRecycled() {
            mImageLoader.cancel();
        }

    }
}
