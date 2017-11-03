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

import android.util.JsonReader;

import org.parceler.Parcel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ie.ianbuttimer.bakingguru.BakingGuruApp;
import ie.ianbuttimer.bakingguru.data.ILoadable;

/**
 * Class representing a recipe
 */
@SuppressWarnings("unused")
@Parcel
public class Recipe extends AbstractBakeObject {

    public static final String ID_KEY = "id";
    public static final String ID_NAME = "name";
    public static final String ID_INGREDIENTS = "ingredients";
    public static final String ID_STEPS = "steps";
    public static final String ID_SERVINGS = "servings";
    public static final String ID_IMAGE = "image";

    int id;
    String name;
    Ingredient[] ingredients;
    Step[] steps;
    int servings;
    String image;
    RecipeType type;

    /**
     * Default constructor
     */
    public Recipe() {
        id = 0;
        name = "";
        ingredients = new Ingredient[0];
        steps = new Step[0];
        servings = 0;
        image = "";
        type = RecipeType.FOOD;
    }

    /**
     * Read a Recipe object from the specified reader
     * @param reader    Reader to read object from
     * @return  new Recipe object
     * @throws IOException
     */
    public static Recipe readRecipe(JsonReader reader) throws IOException {
        Recipe recipe = new Recipe();

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals(ID_KEY)) {
                recipe.id = nextInt(reader, 0);
            } else if (name.equals(ID_NAME)) {
                recipe.setName(nextString(reader, ""));
            } else if (name.equals(ID_INGREDIENTS)) {
                recipe.ingredients = Ingredient.readIngredientsArray(reader);
            } else if (name.equals(ID_STEPS)) {
                recipe.steps = Step.readStepsArray(reader);
            } else if (name.equals(ID_SERVINGS)) {
                recipe.servings = nextInt(reader, 0);
            } else if (name.equals(ID_IMAGE)) {
                recipe.image = nextString(reader, "");
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return recipe;
    }

    /**
     * Read a Recipe object list from the specified reader
     * @param reader    Reader to read object from
     * @return  new Step object
     * @throws IOException
     */
    public static List<Recipe> readRecipeList(JsonReader reader) throws IOException {
        List<Recipe> list = new ArrayList<>();

        reader.beginArray();
        while (reader.hasNext()) {
            list.add(readRecipe(reader));
        }
        reader.endArray();
        return list;
    }

    /**
     * Read an Recipe object array from the specified reader
     * @param reader    Reader to read object from
     * @return  new Step object
     * @throws IOException
     */
    public static Recipe[] readRecipesArray(JsonReader reader) throws IOException {
        List<Recipe> list = readRecipeList(reader);
        return list.toArray(new Recipe[list.size()]);
    }

    @Override
    public void set(Object original) throws IllegalArgumentException {
        checkObject(original, getClass());

        Recipe from = (Recipe) original;

        this.id = from.id;
        setName(from.name);
        this.ingredients = from.ingredients;
        this.steps = from.steps;
        this.servings = from.servings;
        this.image = from.image;
    }

    @Override
    public ILoadable<Recipe> getLoader() {
        return new Loader();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.type = RecipeType.getType(BakingGuruApp.getWeakApplicationContext().get(), name);
    }

    public Ingredient[] getIngredients() {
        return ingredients;
    }

    public void setIngredients(Ingredient[] ingredients) {
        this.ingredients = ingredients;
    }

    public Step[] getSteps() {
        return steps;
    }

    /**
     * Return the step from the specified index
     * @param index Index of step to return
     * @return  Step object or <code>null</code> if index out of range
     */
    public Step getStep(int index) {
        Step step = null;
        if ((steps != null) && (index >= 0) && (index < steps.length)) {
            step = steps[index];
        }
        return step;
    }

    public void setSteps(Step[] steps) {
        this.steps = steps;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public RecipeType getType() {
        return type;
    }

    public void setType(RecipeType type) {
        this.type = type;
    }

    /**
     * Return the number od recipe steps
     * @return  Number of steps
     */
    public int getStepCount() {
        int count = 0;
        if (steps != null) {
            count = steps.length;
        }
        return count;
    }

    /**
     * Return the number od recipe ingredients
     * @return  Number of ingredients
     */
    public int getIngredientCount() {
        int count = 0;
        if (ingredients != null) {
            count = ingredients.length;
        }
        return count;
    }

    /**
     * Loader class for Recipes
     */
    public static class Loader implements ILoadable<Recipe> {

        @Override
        public Recipe read(JsonReader reader) throws IOException {
            return readRecipe(reader);
        }

        @Override
        public List<Recipe> readList(JsonReader reader) throws IOException {
            return readRecipeList(reader);
        }

        @Override
        public Recipe[] readArray(JsonReader reader) throws IOException {
            return readRecipesArray(reader);
        }

        @Override
        public Recipe newInstance() {
            return new Recipe();
        }
    }
}
