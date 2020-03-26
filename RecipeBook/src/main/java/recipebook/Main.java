package recipebook;

import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import recipebook.dao.ArrayListIngredientDao;
import recipebook.dao.ArrayListRecipeDao;
import recipebook.dao.IngredientDao;
import recipebook.dao.RecipeDao;
import recipebook.domain.Ingredient;
import recipebook.domain.Recipe;
import recipebook.ui.TextUi;

public class Main {

    public static void main(String[] args) {
        /* System.out.println("Starting app");
        System.out.println("");
        
        RecipeDao recipeDao = new ArrayListRecipeDao();
        IngredientDao ingDao = new ArrayListIngredientDao();
        
        ingDao.create(new Ingredient("Chicken"));
        ingDao.create(new Ingredient("Butter"));
        ingDao.create(new Ingredient("Onion"));
        ingDao.create(new Ingredient("Garlic"));
        
        HashMap<Ingredient, Integer> ingredients = new HashMap<>();
        ingredients.put(ingDao.getByName("Chicken"), 400);
        ingredients.put(ingDao.getByName("Butter"), 50);
        ingredients.put(ingDao.getByName("Garlic"), 5);
        ingredients.put(ingDao.getByName("Onion"), 50);
        
        Recipe testRecipe = recipeDao.create(new Recipe(1, "Butter Chicken", ingredients, 40, "Cook until ready"));
        
        List<Ingredient> availableIngredients = ingDao.getAll();
        System.out.println(testRecipe);
        System.out.println("");
        System.out.println(availableIngredients);*/
        
        Scanner reader = new Scanner(System.in);
        TextUi tui = new TextUi(reader);
        tui.start();
    }
}
