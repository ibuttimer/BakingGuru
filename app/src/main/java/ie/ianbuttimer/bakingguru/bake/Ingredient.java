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

import ie.ianbuttimer.bakingguru.data.ILoadable;

/**
 * Class representing a recipe ingredient
 */
@SuppressWarnings("unused")
@Parcel
public class Ingredient extends AbstractBakeObject {

    public static final String ID_QUANTITY = "quantity";
    public static final String ID_MEASURE = "measure";
    public static final String ID_INGREDIENT = "ingredient";

    double quantity;
    String measure;
    String ingredient;

    /**
     * Default constructor
     */
    public Ingredient() {
        quantity = 0.0d;
        measure = "";
        ingredient = "";
    }

    /**
     * Constructor
     * @param quantity      Quantity
     * @param measure       Measurement
     * @param ingredient    Name
     */
    public Ingredient(double quantity, String measure, String ingredient) {
        this.quantity = quantity;
        this.measure = measure;
        this.ingredient = ingredient;
    }

    /**
     * Read an Ingredient object from the specified reader
     * @param reader    Reader to read object from
     * @return  new Ingredient object
     * @throws IOException
     */
    public static Ingredient readIngredient(JsonReader reader) throws IOException {
        Ingredient ingredient = new Ingredient();

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals(ID_QUANTITY)) {
                ingredient.quantity = nextDouble(reader, 0);
            } else if (name.equals(ID_MEASURE)) {
                ingredient.measure = nextString(reader, "");
            } else if (name.equals(ID_INGREDIENT)) {
                ingredient.ingredient = nextString(reader, "");
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return ingredient;
    }

    /**
     * Read an Ingredient object list from the specified reader
     * @param reader    Reader to read object from
     * @return  new Step object
     * @throws IOException
     */
    public static List<Ingredient> readIngredientList(JsonReader reader) throws IOException {
        List<Ingredient> list = new ArrayList<>();

        reader.beginArray();
        while (reader.hasNext()) {
            list.add(readIngredient(reader));
        }
        reader.endArray();
        return list;
    }

    /**
     * Read an Ingredient object array from the specified reader
     * @param reader    Reader to read object from
     * @return  new Step object
     * @throws IOException
     */
    public static Ingredient[] readIngredientsArray(JsonReader reader) throws IOException {
        List<Ingredient> list = readIngredientList(reader);
        return list.toArray(new Ingredient[list.size()]);
    }


    @Override
    public void set(Object original) throws IllegalArgumentException {
        checkObject(original, getClass());

        Ingredient from = (Ingredient) original;

        this.quantity = from.quantity;
        this.measure = from.measure;
        this.ingredient = from.ingredient;
    }

    @Override
    public ILoadable<Ingredient> getLoader() {
        return new Loader();
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ingredient that = (Ingredient) o;

        if (Double.compare(that.quantity, quantity) != 0) return false;
        if (measure != null ? !measure.equals(that.measure) : that.measure != null) return false;
        return ingredient != null ? ingredient.equals(that.ingredient) : that.ingredient == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(quantity);
        result = (int) (temp ^ (temp >>> 32));
        result = 31 * result + (measure != null ? measure.hashCode() : 0);
        result = 31 * result + (ingredient != null ? ingredient.hashCode() : 0);
        return result;
    }

    /**
     * Loader class for Ingredients
     */
    public class Loader implements ILoadable<Ingredient> {

        @Override
        public Ingredient read(JsonReader reader) throws IOException {
            return readIngredient(reader);
        }

        @Override
        public List<Ingredient> readList(JsonReader reader) throws IOException {
            return readIngredientList(reader);
        }

        @Override
        public Ingredient[] readArray(JsonReader reader) throws IOException {
            return readIngredientsArray(reader);
        }

        @Override
        public Ingredient newInstance() {
            return new Ingredient();
        }
    }
}
