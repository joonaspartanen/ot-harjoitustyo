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
        return ingDao.create(new Ingredient(name));
    }
    
    public Ingredient addIngredient(String name, String unit) {
        return ingDao.create(new Ingredient(name, unit));
    }
    
    public Ingredient findByName(String name) {
        return ingDao.getByName(name);
    }

}
