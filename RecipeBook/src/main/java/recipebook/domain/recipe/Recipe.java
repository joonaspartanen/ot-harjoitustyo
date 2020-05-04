package recipebook.domain.recipe;

import recipebook.domain.ingredient.Ingredient;
import recipebook.domain.user.User;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class that represents a recipe.
 */
public class Recipe {

    private int id;
    private String name;
    private Map<Ingredient, Integer> ingredients;
    private int time;
    private String instructions;
    private User author;

    public Recipe(String name, Map<Ingredient, Integer> ingredients, int time, String instructions) {
        this.name = name;
        this.ingredients = ingredients;
        this.time = time;
        this.instructions = instructions;
    }

    public Recipe(int id, String name, Map<Ingredient, Integer> ingredients, int time, String instructions) {
        this(name, ingredients, time, instructions);
        this.id = id;
    }

    public Recipe(String name, Map<Ingredient, Integer> ingredients, int time, String instructions, User author) {
        this(name, ingredients, time, instructions);
        this.author = author;
    }

    public Recipe(int id, String name, Map<Ingredient, Integer> ingredients, int time, String instructions,
            User author) {
        this(id, name, ingredients, time, instructions);
        this.author = author;
    }

    private String stringifyIngredients() {
        String result = ingredients.entrySet().stream().sorted(Map.Entry.comparingByKey())
                .map(i -> i.getValue() + " " + i.getKey().getUnit() + " " + i.getKey() + "\n")
                .collect(Collectors.joining());
        return result;
    }

    @Override
    public String toString() {
        return name + "\n\n" + "Ingredients:" + "\n" + stringifyIngredients() + "\n" + "Cooking time: " + time + " min"
                + "\n\n" + "Instructions:" + "\n" + instructions + "\n\n" + "Recipe created by: " + author + "\n";
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

    public Map<Ingredient, Integer> getIngredients() {
        return ingredients;
    }

    public void setIngredients(Map<Ingredient, Integer> ingredients) {
        this.ingredients = ingredients;
    }

    public int getTime() {
        return time;
    }

    public String getInstructions() {
        return instructions;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }

        if (!(object instanceof Recipe)) {
            return false;
        }

        Recipe other = (Recipe) object;

        return this.id == other.getId() && this.name.equals(other.getName())
                && this.ingredients.equals(other.getIngredients()) && this.time == other.getTime()
                && this.instructions.equals(other.getInstructions());
    }

    public int getAuthorId() {
        return author.getId();
    }
}
