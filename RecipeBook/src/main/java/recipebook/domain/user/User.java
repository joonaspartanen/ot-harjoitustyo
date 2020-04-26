package recipebook.domain.user;

import java.util.ArrayList;
import java.util.List;

import recipebook.domain.recipe.Recipe;

public class User {

    private int id;
    private String username;
    private List<Recipe> favoriteRecipes = new ArrayList<>();

    public User(String username) {
        this.username = username;
    }

    public User(int id, String username) {
        this.id = id;
        this.username = username;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return username;
    }

    public int getId() {
        return id;
    }

    public List<Recipe> getFavoriteRecipes() {
        return favoriteRecipes;
    }
}