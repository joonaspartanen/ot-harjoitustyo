package recipebook.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import recipebook.domain.Ingredient;
import recipebook.domain.Recipe;

public class FileRecipeDao implements RecipeDao {

    private List<Recipe> recipes;
    private String recipeFile;
    private String recipeIngFile;
    private Set<String> recipeIngsSet;
    IngredientDao ingDao;

    public FileRecipeDao(IngredientDao ingDao, String recipeFile, String recipeIngFile) {
        recipes = new ArrayList<>();
        recipeIngsSet = new HashSet<>();
        this.ingDao = ingDao;
        this.recipeFile = recipeFile;
        this.recipeIngFile = recipeIngFile;
        loadRecipes();
    }

    private void loadRecipes() {
        try {
            Scanner reader = new Scanner(new File(recipeFile));
            while (reader.hasNextLine()) {
                loadSingleRecipe(reader);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File " + recipeFile + " will be created.");
        }
    }

    private void loadSingleRecipe(Scanner reader) {
        String[] parts = reader.nextLine().split(";");
        int recipeId = Integer.parseInt(parts[0]);
        Map<Ingredient, Integer> ingredients = readIngredients(recipeId);
        Recipe recipe = new Recipe(recipeId, parts[1], ingredients, Integer.parseInt(parts[2]), parts[3]);
        recipes.add(recipe);
    }

    private void saveRecipes() {
        try {
            FileWriter recipeWriter = new FileWriter(new File(recipeFile));
            for (Recipe r : recipes) {
                recipeWriter
                        .write(r.getId() + ";" + r.getName() + ";" + r.getTime() + ";" + r.getInstructions() + "\n");
                addRecipeIngredientsToSet(r);
            }
            recipeWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        saveRecipeIngredients();
    }

    private void addRecipeIngredientsToSet(Recipe recipe) {
        for (Ingredient i : recipe.getIngredients().keySet()) {
            recipeIngsSet.add(recipe.getId() + ";" + i.getId() + ";" + recipe.getIngredients().get(i) + "\n");
        }
    }

    private void saveRecipeIngredients() {
        try {
            FileWriter recipeIngWriter = new FileWriter(new File(recipeIngFile));
            for (String line : recipeIngsSet) {
                recipeIngWriter.write(line);
            }
            recipeIngWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<Ingredient, Integer> readIngredients(int recipeId) {
        Map<Ingredient, Integer> ingredients = new HashMap<>();
        try {
            Scanner reader = new Scanner(new File(recipeIngFile));
            while (reader.hasNextLine()) {
                readSingleIngredient(reader, recipeId, ingredients);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ingredients;
    }

    private void readSingleIngredient(Scanner reader, int recipeId, Map<Ingredient, Integer> ingredients) {
        String[] parts = reader.nextLine().split(";");
        int id = Integer.parseInt(parts[0]);
        if (id == recipeId) {
            Ingredient ingredient = ingDao.getById(Integer.parseInt(parts[1]));
            ingredients.put(ingredient, Integer.parseInt(parts[2]));
        }
    }

    @Override
    public Recipe create(Recipe recipe) {
        recipe.setId(generateId());
        recipes.add(recipe);
        ingDao.createNewIngredients(recipe);
        saveRecipes();
        return recipe;
    }

    @Override
    public List<Recipe> getAll() {
        return recipes;
    }

    @Override
    public List<Recipe> getByName(String name) {
        List<Recipe> foundRecipes = recipes.stream().filter(r -> r.getName().contains(name))
                .collect(Collectors.toList());
        return foundRecipes;
    }

    @Override
    public Recipe getById(int id) {
        return recipes.stream().filter(r -> r.getId() == id).findFirst().orElse(null);
    }

    @Override
    public List<Recipe> getByIngredient(String ingredientName) {
        List<Recipe> foundRecipes = new ArrayList<>();
        for (Recipe recipe : recipes) {
            if (recipeContainsIngredient(recipe, ingredientName)) {
                foundRecipes.add(recipe);
            }
        }
        return foundRecipes;
    }

    private boolean recipeContainsIngredient(Recipe recipe, String ingredientName) {
        List<String> ingredientNames = recipe.getIngredients().keySet().stream().map(i -> i.getName())
                .collect(Collectors.toList());
        return ingredientNames.contains(ingredientName.toLowerCase());
    }

    @Override
    public void delete(Recipe recipe) {
        recipes.remove(recipe);
        saveRecipes();
    }

    @Override
    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }

    private int generateId() {
        return recipes.size() + 1;
    }

}
