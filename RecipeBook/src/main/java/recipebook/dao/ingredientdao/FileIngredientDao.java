package recipebook.dao.ingredientdao;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import recipebook.dao.DataStoreException;
import recipebook.dao.IdExtractor;

import recipebook.domain.ingredient.Ingredient;

/**
 * IngredientDao implementation to store ingredient-related data into a text
 * file.
 *
 */
public class FileIngredientDao implements IngredientDao {

    private List<Ingredient> ingredients;
    private String ingredientsFile;
    private IdExtractor idExtractor;

    /**
     * Constructor.
     *
     * @param ingredientsFile Name of the text file used as a file store.
     */
    public FileIngredientDao(String ingredientsFile) {
        ingredients = new ArrayList<>();
        this.ingredientsFile = ingredientsFile;
        idExtractor = new IdExtractor();
        readIngredientsFromFile();
    }

    private void readIngredientsFromFile() {
        try {
            Scanner reader = new Scanner(new File(ingredientsFile));
            while (reader.hasNextLine()) {
                readSingleIngredient(reader);
            }
        } catch (FileNotFoundException ex) {
            System.out.println("File " + ingredientsFile + " will be created.");
        }
    }

    private void readSingleIngredient(Scanner reader) {
        String[] parts = reader.nextLine().split(";");
        int id = Integer.parseInt(parts[0]);
        String name = parts[1];
        String unit = parts[2];
        Ingredient ingredient = new Ingredient(id, name, unit);
        ingredients.add(ingredient);
    }

    private void writeIngredientsToFile() throws DataStoreException {
        try (FileWriter writer = new FileWriter(new File(ingredientsFile))) {
            for (Ingredient ingredient : ingredients) {
                writer.write(ingredient.getId() + ";" + ingredient.getName().replace(";", "") + ";"
                        + ingredient.getUnit() + "\n");
            }
        } catch (IOException ex) {
            throw new DataStoreException("Writing ingredients to file failed.", ex);
        }
    }

    /**
     * Stores a new ingredient and sets its id.
     *
     * @param ingredient The ingredient to be stored.
     * @return The stored with corresponding id.
     */
    @Override
    public Ingredient create(Ingredient ingredient) throws DataStoreException {
        ingredient.setId(idExtractor.getCreatedItemIdFromList(ingredients));
        ingredients.add(ingredient);
        writeIngredientsToFile();
        return ingredient;
    }

    /**
     * Fetches all ingredients.
     *
     * @return List of ingredients or an empty list if no results found.
     */
    @Override
    public List<Ingredient> getAll() {
        return ingredients;
    }

    /**
     * Fetches all ingredients whose name matches the search term passed in as a
     * parameter.
     *
     * @param name The ingredient name used as a search term.
     * @return List of matching ingredients or an empty list if no results found.
     */
    @Override
    public List<Ingredient> getByName(String name) {
        return ingredients.stream().filter(i -> i.getName().equals(name)).collect(Collectors.toList());
    }

    /**
     * Fetches the ingredient whose id matches the id passed in as a parameter.
     * Single result expected.
     *
     * @param id The ingredient id used as a search term.
     * @return The matching ingredient or null if no results found.
     * @throws DataStoreException
     */
    public Ingredient getById(int id) throws DataStoreException {
        return ingredients.stream().filter(i -> i.getId() == id).findFirst()
                .orElseThrow(() -> new DataStoreException("Ingredient with id " + id + " was not found."));
    }

    /**
     * Fetches the ingredient whose name and unit matches the search terms passed in
     * as parameters. Single result expected.
     *
     * @param name The ingredient name used as a search term.
     * @param unit The ingredient unit used as a search term.
     * @return The matching ingredient or null if no results found.
     */
    @Override
    public Ingredient getByNameAndUnit(String name, String unit) {
        return ingredients.stream().filter(i -> i.getName().equals(name)).filter(i -> i.getUnit().equals(unit))
                .findFirst().orElse(null);
    }
}
