package recipebook.domain;

import java.util.List;

import recipebook.dao.IngredientDao;

public class IngredientService {

    private IngredientDao ingDao;

    public IngredientService(IngredientDao ingDao) {
        this.ingDao = ingDao;
    }

    public List<Ingredient> listAll() {
        return ingDao.getAll();
    }

    public Ingredient addIngredient(String name) {
        Ingredient existingIngredient = ingDao.getByName(name);
        if (existingIngredient == null) {
            return ingDao.create(new Ingredient(formatName(name)));
        }
        return existingIngredient;
    }

    public Ingredient addIngredient(String name, String unit) {
        return ingDao.create(new Ingredient(formatName(name), unit));
    }

    public Ingredient findByName(String name) {
        return ingDao.getByName(formatName(name));
    }

    public String formatName(String name) {
        return name.toLowerCase().trim();
    }

}
