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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ahamed.multiviewadapter.ItemViewHolder;

import butterknife.BindView;
import butterknife.ButterKnife;
import ie.ianbuttimer.bakingguru.R;
import ie.ianbuttimer.bakingguru.bake.Step;
import ie.ianbuttimer.bakingguru.image.AbstractImageLoader;
import ie.ianbuttimer.bakingguru.image.ThumbnailImageLoader;

import static ie.ianbuttimer.bakingguru.bake.IngredientsStep.INGREDIENTS_STEP_ID;
import static ie.ianbuttimer.bakingguru.data.adapter.StepAdapter.getIdText;

/**
 * Step binder class for the MultiViewAdapter library
 */
@SuppressWarnings("unused")
public class StepBinder extends AbstractBinder<Step, StepBinder.ViewHolder> {

    /**
     * Default constructor
     */
    public StepBinder() {
        super(R.layout.step_list_item);
    }

    /**
     * Constructor
     * @param clickListener     Click listener
     */
    public StepBinder(ItemViewHolder.OnItemClickListener<Step> clickListener) {
        super(R.layout.step_list_item, clickListener);
    }

    @Override
    public void bind(ViewHolder holder, Step item) {
        holder.setViewInfo(item);
    }

    @Override
    protected ViewHolder getViewHolder(View itemView) {
        return new ViewHolder(itemView);
    }

    @Override
    public boolean canBindData(Object item) {
        return (item instanceof Step);
    }




    /**
     * ViewHolder class
     */
    class ViewHolder extends ItemViewHolder<Step> implements IViewHolder {

        @BindView(R.id.tv_number_step_list_item) TextView mNumberTextView;
        @BindView(R.id.tv_description_step_list_item) TextView mDescriptionTextView;
        @BindView(R.id.iv_thumbnail_step_list_item) ImageView mImageImageView;
        @BindView(R.id.pb_thumbnail_step_list_item) ProgressBar mImageProgressBar;

        private BakeViewImageLoader<Step> mImageLoader;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
            setItemClickListener(getClickListener());

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

        /**
         * Set the details to display<br>
         * @param info   Information object to use
         */
        public void setViewInfo(Step info) {
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
