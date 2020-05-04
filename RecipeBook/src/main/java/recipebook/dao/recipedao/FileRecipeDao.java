package recipebook.dao.recipedao;

import recipebook.dao.ingredientdao.IngredientDao;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import recipebook.dao.DataStoreException;
import recipebook.dao.IdExtractor;
import recipebook.dao.userdao.UserDao;
import recipebook.dao.userdao.UserNotFoundException;

import recipebook.domain.ingredient.Ingredient;
import recipebook.domain.recipe.Recipe;
import recipebook.domain.user.User;

/**
 * RecipeDao implementation for storing recipe-related data into a text file
 * data store.
 *
 */
public class FileRecipeDao implements RecipeDao {

    private List<Recipe> recipes;
    private Map<Integer, List<Recipe>> favoriteRecipes;
    private String recipeFile;
    private String recipeIngredientsFile;
    private String favoriteRecipesFile;

    private Set<String> recipeIngredientsSet;
    private IngredientDao ingredientDao;
    private UserDao userDao;
    private IdExtractor idExtractor;

    /**
     *
     * @param ingredientDao         An ingredientDao implementation needed to handle
     *                              the ingredients related to recipes.
     * @param userDao               An userDao implementation needed to handle the
     *                              user data related to recipes.
     * @param recipeFile            Path to the text file to store recipe data.
     * @param recipeIngredientsFile Path to the text file to store the recipe
     *                              ingredients cross-references.
     * @param favoriteRecipesFile   Path to the text file to store the favorite
     *                              recipes data.
     */
    public FileRecipeDao(IngredientDao ingredientDao, UserDao userDao, String recipeFile, String recipeIngredientsFile,
            String favoriteRecipesFile) throws DataStoreException, UserNotFoundException {
        recipes = new ArrayList<>();
        recipeIngredientsSet = new HashSet<>();
        favoriteRecipes = new HashMap<>();
        this.ingredientDao = ingredientDao;
        this.userDao = userDao;
        this.recipeFile = recipeFile;
        this.recipeIngredientsFile = recipeIngredientsFile;
        this.favoriteRecipesFile = favoriteRecipesFile;
        idExtractor = new IdExtractor();
        readRecipesFromFile();
        readFavoriteRecipesFromFile();
    }

    /**
     *
     * @param recipe Recipe to be stored.
     * @return The stored recipe with corresponding id.
     */
    @Override
    public Recipe create(Recipe recipe) throws DataStoreException {
        recipe.setId(idExtractor.getCreatedItemIdFromList(recipes));
        recipes.add(recipe);
        int authorId = recipe.getAuthorId();
        favoriteRecipes.putIfAbsent(authorId, new ArrayList<>());
        favoriteRecipes.get(authorId).add(recipe);
        writeRecipesToFile();
        return recipe;
    }

    /**
     * Fetches all the recipes store in the data store.
     *
     * @return List of recipes or an empty list if no recipes are found.
     */
    @Override
    public List<Recipe> getAll() {
        return recipes;
    }

    /**
     * Fetches all the recipes whose name contains the search term passed in as a
     * parameter.
     *
     * @param name The recipe name used as a search term.
     * @return List of recipes or an empty list if no recipes are found.
     */
    @Override
    public List<Recipe> getByName(String name) {
        List<Recipe> foundRecipes = recipes.stream().filter(r -> r.getName().contains(name))
                .collect(Collectors.toList());
        return foundRecipes;
    }

    /**
     * Fetches the recipe whose id matches the search term passed in as a parameter.
     * Single result expected.
     *
     * @param id The recipe id used as a search term.
     * @return The matching recipe.
     * @throws DataStoreException if no results are found.
     */
    @Override
    public Recipe getById(int id) throws DataStoreException {
        return recipes.stream().filter(r -> r.getId() == id).findFirst()
                .orElseThrow(() -> new DataStoreException("Recipe with id " + id + " was not found."));
    }

    /**
     *
     * @param ingredientName
     * @return
     */
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

    /**
     *
     * @param recipe
     */
    @Override
    public void delete(Recipe recipe) throws DataStoreException {
        recipes.remove(recipe);
        writeRecipesToFile();
    }

    /**
     *
     * @param userId
     * @param recipe
     */
    @Override
    public void saveRecipeToFavorites(int userId, Recipe recipe) throws DataStoreException {
        favoriteRecipes.putIfAbsent(userId, new ArrayList<>());
        favoriteRecipes.get(userId).add(recipe);
        writeRecipesToFile();
    }

    /**
     *
     * @param userId
     * @return
     */
    @Override
    public List<Recipe> getFavoriteRecipes(int userId) {
        favoriteRecipes.putIfAbsent(userId, new ArrayList<>());
        return favoriteRecipes.get(userId);
    }

    private void readRecipesFromFile() throws DataStoreException, UserNotFoundException {
        try (Scanner reader = new Scanner(new File(recipeFile))) {
            while (reader.hasNextLine()) {
                readSingleRecipe(reader);
            }
        } catch (FileNotFoundException ex) {
            System.out.println("File " + recipeFile + " will be created.");
        }
    }

    private void readSingleRecipe(Scanner reader) throws DataStoreException, UserNotFoundException {
        String[] parts = reader.nextLine().split(";");

        int recipeId = Integer.parseInt(parts[0]);
        String recipeName = parts[1];
        int time = Integer.parseInt(parts[2]);
        String instructions = parts[3];
        int authorId = Integer.parseInt(parts[4]);
        User author = userDao.getById(authorId);

        Map<Ingredient, Integer> ingredients = readRecipeIngredientsFromFile(recipeId);

        Recipe recipe = new Recipe(recipeId, recipeName, ingredients, time, instructions, author);
        recipes.add(recipe);
    }

    private void readFavoriteRecipesFromFile() throws DataStoreException {
        try (Scanner reader = new Scanner(new File(favoriteRecipesFile))) {
            while (reader.hasNextLine()) {
                String[] parts = reader.nextLine().split(";");
                int userId = Integer.parseInt(parts[0]);
                int recipeId = Integer.parseInt(parts[1]);
                Recipe recipe = getById(recipeId);
                favoriteRecipes.putIfAbsent(userId, new ArrayList<>());
                favoriteRecipes.get(userId).add(recipe);
            }
        } catch (FileNotFoundException ex) {
            System.out.println("File " + favoriteRecipesFile + " will be created.");
        }
    }

    private void writeRecipesToFile() throws DataStoreException {
        try (FileWriter recipeWriter = new FileWriter(new File(recipeFile))) {
            for (Recipe r : recipes) {
                recipeWriter.write(r.getId() + ";" + r.getName().replace(";", "") + ";" + r.getTime() + ";"
                        + r.getInstructions().replace(";", "") + ";" + r.getAuthorId() + "\n");
                addRecipeIngredientsToSet(r);
            }
        } catch (IOException ex) {
            throw new DataStoreException("Writing recipes to file failed.", ex);
        }

        writeRecipeIngredientsToFile();
        writeFavoriteRecipesToFile();
    }

    private void addRecipeIngredientsToSet(Recipe recipe) {
        for (Ingredient i : recipe.getIngredients().keySet()) {
            recipeIngredientsSet.add(recipe.getId() + ";" + i.getId() + ";" + recipe.getIngredients().get(i) + "\n");
        }
    }

    private void writeRecipeIngredientsToFile() throws DataStoreException {
        try (FileWriter recipeIngredientWriter = new FileWriter(new File(recipeIngredientsFile))) {
            for (String recipeIngredientsLine : recipeIngredientsSet) {
                recipeIngredientWriter.write(recipeIngredientsLine);
            }
        } catch (IOException ex) {
            throw new DataStoreException("Writing recipe ingredients to file failed.", ex);
        }
    }

    private Map<Ingredient, Integer> readRecipeIngredientsFromFile(int recipeId) throws DataStoreException {
        Map<Ingredient, Integer> ingredients = new HashMap<>();
        try (Scanner reader = new Scanner(new File(recipeIngredientsFile))) {
            while (reader.hasNextLine()) {
                readSingleIngredient(reader, recipeId, ingredients);
            }
        } catch (Exception ex) {
            throw new DataStoreException("Reading recipe ingredients from file failed.", ex);
        }
        return ingredients;
    }

    private void readSingleIngredient(Scanner reader, int recipeId, Map<Ingredient, Integer> ingredients)
            throws NumberFormatException, DataStoreException {
        String[] parts = reader.nextLine().split(";");
        int id = Integer.parseInt(parts[0]);
        if (id == recipeId) {
            Ingredient ingredient = ingredientDao.getById(Integer.parseInt(parts[1]));
            ingredients.put(ingredient, Integer.parseInt(parts[2]));
        }
    }

    private void writeFavoriteRecipesToFile() throws DataStoreException {
        try (FileWriter favoriteRecipesWriter = new FileWriter(new File(favoriteRecipesFile))) {
            for (Integer userId : favoriteRecipes.keySet()) {
                writeSingleUserFavoriteRecipes(favoriteRecipesWriter, userId);
            }
        } catch (IOException ex) {
            throw new DataStoreException("Writing favorite recipes to file failed.", ex);
        }
    }

    private void writeSingleUserFavoriteRecipes(FileWriter writer, int userId) throws IOException {
        for (Recipe recipe : favoriteRecipes.get(userId)) {
            writer.write(userId + ";" + recipe.getId() + "\n");
        }
    }
}
