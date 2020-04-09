package recipebook.dao;

import java.util.ArrayList;
import java.util.List;

import recipebook.domain.Ingredient;
import recipebook.domain.Recipe;

// Preliminary implementation that stores ingredients in ArrayList
public class ArrayListIngredientDao implements IngredientDao {

    List<Ingredient> ingredients;

    public ArrayListIngredientDao() {
        ingredients = new ArrayList<>();
    }

    @Override
    public Ingredient create(Ingredient ingredient) {
        ingredient.setId(generateId());
        ingredients.add(ingredient);
        return ingredient;
    }

    @Override
    public List<Ingredient> getAll() {
        return ingredients;
    }

    @Override
    public Ingredient getByName(String name) {
        return ingredients.stream().filter(i -> i.getName().equals(name)).findFirst().orElse(null);
    }
        }
        return foundIngredients.get(0);
    }

}
