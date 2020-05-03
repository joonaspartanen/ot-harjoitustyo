package recipebook.dao.ingredientdao;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import recipebook.domain.ingredient.Ingredient;

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
    public List<Ingredient> getByName(String name) {
        return ingredients.stream().filter(i -> i.getName().equals(name)).collect(Collectors.toList());
    }

    @Override
    public Ingredient getById(int id) {
        return ingredients.stream().filter(i -> i.getId() == id).findFirst().orElse(null);
    }

    private int generateId() {
        return ingredients.size() + 1;
    }

    @Override
    public Ingredient getByNameAndUnit(String name, String unit) {
        return ingredients.stream().filter(i -> i.getName().equals(name)).filter(i -> i.getUnit().equals(unit))
                .findFirst().orElse(null);
    }

}
