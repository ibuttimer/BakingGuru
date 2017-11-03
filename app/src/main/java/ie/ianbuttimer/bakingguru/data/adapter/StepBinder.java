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
import ie.ianbuttimer.bakingguru.bake.Step;

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
    class ViewHolder extends ItemViewHolder<Step> {

        @BindView(R.id.tv_number_step_list_item) TextView mNumberTextView;
        @BindView(R.id.tv_description_step_list_item) TextView mDescriptionTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            setItemClickListener(getClickListener());
        }

        public void setViewInfo(Step info) {
            mNumberTextView.setText(getIdText(info));
            mDescriptionTextView.setText(info.getShortDescription());
        }


    }
}
