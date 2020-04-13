package recipebook.dao.ingredientdao;

import recipebook.dao.ingredientdao.IngredientDao;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import recipebook.domain.ingredient.Ingredient;

public class FileIngredientDao implements IngredientDao {

    private List<Ingredient> ingredients;
    private String ingredientsFile;

    public FileIngredientDao(String ingredientsFile) {
        ingredients = new ArrayList<>();
        this.ingredientsFile = ingredientsFile;
        load();
    }

    private void load() {
        try {
            Scanner reader = new Scanner(new File(ingredientsFile));
            while (reader.hasNextLine()) {
                String[] parts = reader.nextLine().split(";");
                Ingredient ingredient = new Ingredient(Integer.parseInt(parts[0]), parts[1], parts[2]);
                ingredients.add(ingredient);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File " + ingredientsFile + " will be created.");
        }
    }

    private void save() {
        try {
            FileWriter writer = new FileWriter(new File(ingredientsFile));
            for (Ingredient i : ingredients) {
                writer.write(i.getId() + ";" + i.getName() + ";" + i.getUnit() + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Ingredient create(Ingredient ingredient) {
        ingredient.setId(generateId());
        ingredients.add(ingredient);
        save();
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
