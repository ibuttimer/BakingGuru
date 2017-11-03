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

package ie.ianbuttimer.bakingguru.bake;

/**
 * Convenience class to represent an ingredient list as a Step
 */
@SuppressWarnings("unused")
public class IngredientsStep extends Step {

    public static final int INGREDIENTS_STEP_ID = -100;

    Ingredient[] ingredients;

    /**
     * Default constructor
     */
    public IngredientsStep() {
        super();
        ingredients = new Ingredient[0];
    }

    /**
     * Constructor
     * @param id                Step id
     * @param shortDescription  Short description
     * @param description       Description
     * @param videoURL          Video url
     * @param thumbnailURL      Thumbnail url
     */
    public IngredientsStep(int id, String shortDescription, String description, String videoURL, String thumbnailURL) {
        super(id, shortDescription, description, videoURL, thumbnailURL);
        ingredients = new Ingredient[0];
    }

    /**
     * Constructor
     * @param id                Step id
     * @param shortDescription  Short description
     */
    public IngredientsStep(int id, String shortDescription) {
        super(id, shortDescription, "", "", "");
        ingredients = new Ingredient[0];
    }

    public Ingredient[] getIngredients() {
        return ingredients;
    }

    public void setIngredients(Ingredient[] ingredients) {
        this.ingredients = ingredients;
    }
}
