package recipebook.domain.ingredient;

import java.util.List;

import recipebook.dao.ingredientdao.IngredientDao;

public class IngredientService {

    private IngredientDao ingDao;

    public IngredientService(IngredientDao ingDao) {
        this.ingDao = ingDao;
    }

    public List<Ingredient> listAll() {
        return ingDao.getAll();
    }

    public Ingredient createIngredient(String name, String unit) {
        Ingredient foundIngredient = ingDao.getByNameAndUnit(formatName(name), unit);
        if (foundIngredient == null) {
            return ingDao.create(new Ingredient(formatName(name), unit));
        }
        return foundIngredient;
    }

    public Ingredient addIngredient(String name) {
        return createIngredient(name, "g");
    }

    public List<Ingredient> findByName(String name) {
        return ingDao.getByName(formatName(name));
    }

    public Ingredient findByNameAndUnit(String name, String unit) {
        return ingDao.getByNameAndUnit(formatName(name), unit);
    }

    public String formatName(String name) {
        return name.toLowerCase().trim();
    }

}
