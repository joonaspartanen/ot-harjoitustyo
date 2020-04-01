package recipebook.domain;

import java.util.Map;
import java.util.stream.Collectors;

public class Recipe {

    private int id;
    private String name;
    private Map<Ingredient, Integer> ingredients;
    private int time;
    private String instructions;

    public Recipe(int id, String name, Map<Ingredient, Integer> ingredients, int time, String instructions) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.time = time;
        this.instructions = instructions;
    }

    public Recipe(String name, Map<Ingredient, Integer> ingredients, int time, String instructions) {
        this.name = name;
        this.ingredients = ingredients;
        this.time = time;
        this.instructions = instructions;
    }

    public String stringifyIngredients() {
        String result = ingredients.entrySet().stream().sorted(Map.Entry.comparingByKey())
                .map(i -> i.getValue() + " " + i.getKey().getUnit() + " " + i.getKey() + "\n").collect(Collectors.joining());
        return result;
    }

    @Override
    public String toString() {
        return name + "\n\n" + "Ingredients:" + "\n" + stringifyIngredients() + "\n" + "Cooking time: " + time + " min"
                + "\n\n" + "Instructions:" + "\n" + instructions + "\n";
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
