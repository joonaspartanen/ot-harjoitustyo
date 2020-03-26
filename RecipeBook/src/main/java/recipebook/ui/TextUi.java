package recipebook.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import recipebook.dao.ArrayListIngredientDao;
import recipebook.dao.ArrayListRecipeDao;
import recipebook.domain.Ingredient;
import recipebook.domain.IngredientService;
import recipebook.domain.Recipe;
import recipebook.domain.RecipeService;

public class TextUi {

    private Scanner scanner;
    private Map<String, String> commands;
    IngredientService ingService;
    RecipeService recipeService;

    public TextUi(Scanner scanner) {
        this.scanner = scanner;
        this.ingService = new IngredientService(new ArrayListIngredientDao());
        this.recipeService = new RecipeService(new ArrayListRecipeDao());

        commands = new TreeMap<>();

        commands.put("x", "x stop");
        commands.put("1", "1 add ingredient");
        commands.put("2", "2 list ingredients");
        commands.put("3", "3 add recipe");
        commands.put("4", "4 list recipes");
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
            }
        }
    }

    public void addIngredient() {
        System.out.println("Name: ");
        String name = scanner.nextLine();
        System.out.println("Unit: ");
        String unit = scanner.nextLine();
        ingService.addIngredient(name, unit);
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

            Ingredient ingredient = ingService.findByName(name);
            if (ingredient == null) {
                System.out.println("Unit: ");
                String unit = scanner.nextLine();
                ingredient = ingService.addIngredient(name, unit);
            }

            System.out.println("Amount (" + ingredient.getUnit() + "): ");
            int amount = Integer.parseInt(scanner.nextLine());
            ingredients.put(ingredient, amount);
        }
        
        System.out.println("Cooking time: ");
        int time = Integer.parseInt(scanner.nextLine());
        System.out.println("Instructions: ");
        String instructions = scanner.nextLine();

        recipeService.addRecipe(recipeName, ingredients, time, instructions);
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

}
