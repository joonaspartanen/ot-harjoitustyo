package recipebook.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import recipebook.dao.ingredientdao.ArrayListIngredientDao;
import recipebook.dao.recipedao.ArrayListRecipeDao;
import recipebook.dao.ingredientdao.IngredientDao;
import recipebook.domain.ingredient.Ingredient;
import recipebook.domain.ingredient.IngredientService;
import recipebook.domain.recipe.Recipe;
import recipebook.domain.recipe.RecipeService;

public class TextUi {

    private Scanner scanner;
    private Map<String, String> commands;
    IngredientService ingService;
    RecipeService recipeService;

    public TextUi(Scanner scanner) {
        this.scanner = scanner;
        IngredientDao ingDao = new ArrayListIngredientDao();
        this.ingService = new IngredientService(ingDao);
        this.recipeService = new RecipeService(new ArrayListRecipeDao(ingDao));

        commands = new TreeMap<>();

        commands.put("x", "x stop");
        commands.put("1", "1 add ingredient");
        commands.put("2", "2 list ingredients");
        commands.put("3", "3 add recipe");
        commands.put("4", "4 list recipes");
        commands.put("5", "5 search recipes");
    }

    public void start() {
        System.out.println("Welcome to Java Recipe Book");
        printCommands();
        while (true) {
            System.out.println();
            System.out.println("Command:");
            String command = scanner.nextLine();

            if (command.equals("x")) {
                break;
            } else if (command.equals("1")) {
                addIngredient();
            } else if (command.equals("2")) {
                printIngredients();
            } else if (command.equals("3")) {
                addRecipe();
            } else if (command.equals("4")) {
                printRecipes();
            } else if (command.equals("5")) {
                searchRecipes();
            }

        }
    }

    public void addIngredient() {
        System.out.println("Name: ");
        String name = scanner.nextLine();
        System.out.println("Unit: ");
        String unit = scanner.nextLine();
        ingService.createIngredient(name, unit);
    }

    public void printIngredients() {
        System.out.println(ingService.listAll());
    }

    public void addRecipe() {
        Map<Ingredient, Integer> ingredients = new HashMap<>();
        System.out.println("Recipe name: ");
        String recipeName = scanner.nextLine();
        while (true) {
            System.out.println("Ingredient: (type 'x' when finished)");
            String name = scanner.nextLine();

            if (name.equals("x")) {
                break;
            }

            System.out.println("Unit: ");
            String unit = scanner.nextLine();

            Ingredient ingredient = ingService.createIngredient(name, unit);

            System.out.println("Amount (" + ingredient.getUnit() + "): ");
            try {
                int amount = Integer.parseInt(scanner.nextLine());
                ingredients.put(ingredient, amount);
            } catch (NumberFormatException e) {
                System.out.println("The amount must be numeric.");
            }
        }

        System.out.println("Cooking time: ");
        int time;
        try {
            time = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("The time must be numeric.");
            time = 0;
        }

        System.out.println("Instructions: ");
        String instructions = scanner.nextLine();

        recipeService.createRecipe(recipeName, ingredients, time, instructions);
    }

    public void printRecipes() {
        for (Recipe recipe : recipeService.listAll()) {
            System.out.println(recipe);
            System.out.println();
        }
    }

    public void printCommands() {
        for (String command : commands.keySet()) {
            System.out.println(commands.get(command));
        }
    }

    public void searchRecipes() {
        System.out.println("Ingredient?");
        String name = scanner.nextLine();
        List<Recipe> foundRecipes = recipeService.findByIngredient(name);
        for (Recipe recipe : foundRecipes) {
            System.out.println(recipe);
        }
    }

}
