package recipebook.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import recipebook.domain.Ingredient;

// Preliminary implementation that stores ingredients in ArrayList
public class ArrayListIngredientDao implements IngredientDao {

    List<Ingredient> ingredients;

    public ArrayListIngredientDao() {
        ingredients = new ArrayList<>();
    }

    @Override
    public Ingredient create(Ingredient ingredient) {
        if (!ingredients.contains(ingredient)) {
            ingredients.add(ingredient);
        }
        return ingredient;
    }

    @Override
    public List<Ingredient> getAll() {
        return ingredients;
    }

    @Override
    public Ingredient getByName(String name) {
        List<Ingredient> foundIngredients = ingredients.stream().filter(i -> i.getName().equals(name)).collect(Collectors.toList());
        if (foundIngredients.isEmpty()) {
            return null;
        }
        return foundIngredients.get(0);
    }

}
