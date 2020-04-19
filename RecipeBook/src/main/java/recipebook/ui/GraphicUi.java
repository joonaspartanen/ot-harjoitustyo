package recipebook.ui;

import java.io.*;
import java.sql.Connection;
import java.util.*;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import recipebook.dao.DatabaseConnector;
import recipebook.dao.ingredientdao.*;
import recipebook.dao.recipedao.*;
import recipebook.domain.ingredient.Ingredient;
import recipebook.domain.ingredient.IngredientService;
import recipebook.domain.recipe.Recipe;
import recipebook.domain.recipe.RecipeService;

public class GraphicUi extends Application {

    private RecipeService recipeService;
    private IngredientService ingredientService;
    private IngredientDao ingredientDao;
    private RecipeDao recipeDao;
    Properties properties;
    private ListView<Recipe> recipeList;
    private Insets PADDINGBOTTOM10 = new Insets(0, 0, 10, 0);
    private Insets PADDING25 = new Insets(25, 25, 25, 25);
    private DatabaseConnector databaseConnector;

    @Override
    public void init() {

        properties = new Properties();
        try {
            properties.load(new FileInputStream("config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String dataStore = properties.getProperty("dataStore");

        if (dataStore.equals("database")) {
            useDatabaseStore();
        } else if (dataStore.equals("file")) {
            useFileStore();
        }

        ingredientService = new IngredientService(ingredientDao);
        recipeService = new RecipeService(recipeDao);
    }

    private void useDatabaseStore() {
        String databasePath = properties.getProperty("databasePath");
        databaseConnector = new DatabaseConnector("jdbc:sqlite:" + databasePath);
        Connection connection = databaseConnector.initializeDatabase();
        ingredientDao = new DatabaseIngredientDao(connection);
        recipeDao = new DatabaseRecipeDao(connection, ingredientDao);
    }

    private void useFileStore() {
        String ingredientsFile = properties.getProperty("ingredientsFile");
        String recipesFile = properties.getProperty("recipesFile");
        String recipesIngredientsFile = properties.getProperty("recipesIngredientsFile");
        ingredientDao = new FileIngredientDao(ingredientsFile);
        recipeDao = new FileRecipeDao(ingredientDao, recipesFile, recipesIngredientsFile);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Java Recipe Book");

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);

        Tab allRecipesTab = initiateAllRecipesTab();
        Tab addRecipeTab = initiateAddRecipeTab();
        Tab searchRecipeTab = initiateSearchRecipeTab();
        Tab recipeBookTab = new Tab("My Recipebook");

        tabPane.getTabs().add(allRecipesTab);
        tabPane.getTabs().add(addRecipeTab);
        tabPane.getTabs().add(searchRecipeTab);
        tabPane.getTabs().add(recipeBookTab);

        StackPane root = new StackPane();
        root.getChildren().add(tabPane);
        primaryStage.setScene(new Scene(root, 1280, 800));
        primaryStage.show();
    }

    private Tab initiateAllRecipesTab() {
        Tab allRecipesTab = new Tab("All Recipes");

        recipeList = new ListView<>();

        VBox allRecipesWrapper = new VBox();
        Label recipeLabel = new Label();
        Button updateRecipeListButton = new Button("Update view");
        Button showRecipeButton = generateShowRecipeButton(recipeList, recipeLabel);
        Button deleteRecipeButton = new Button("Delete recipe");

        refreshRecipes(recipeList);

        updateRecipeListButton.setOnAction(e -> {
            refreshRecipes(recipeList);
        });

        deleteRecipeButton.setOnAction(e -> {
            if (recipeList.getSelectionModel().getSelectedItem() == null) {
                return;
            }
            int recipeId = recipeList.getSelectionModel().getSelectedItem().getId();
            recipeService.deleteRecipeById(recipeId);
            refreshRecipes(recipeList);
            recipeLabel.setText("");
        });

        HBox buttonsWrapper = new HBox();
        buttonsWrapper.setPadding(new Insets(10, 0, 10, 0));
        buttonsWrapper.setSpacing(10);
        buttonsWrapper.getChildren().addAll(updateRecipeListButton, showRecipeButton, deleteRecipeButton);

        allRecipesWrapper.getChildren().add(recipeList);
        allRecipesWrapper.getChildren().add(buttonsWrapper);
        allRecipesWrapper.getChildren().add(recipeLabel);
        allRecipesWrapper.setPadding(padding25);
        allRecipesTab.setContent(allRecipesWrapper);

        return allRecipesTab;
    }

    private Tab initiateAddRecipeTab() {
        Tab addRecipeTab = new Tab("Add Recipe");

        TextField nameField = new TextField();
        TextField timeField = new TextField();
        TextArea instructionsArea = new TextArea();

        HBox nameFieldWrapper = new HBox(new Label("Recipe name:"), nameField);
        nameFieldWrapper.setAlignment(Pos.CENTER_LEFT);
        nameFieldWrapper.setSpacing(10);
        nameFieldWrapper.setPadding(bottomPadding10);

        HBox timeFieldWrapper = new HBox(new Label("Cooking time (minutes):"), timeField);
        timeFieldWrapper.setAlignment(Pos.CENTER_LEFT);
        timeFieldWrapper.setSpacing(10);
        timeFieldWrapper.setPadding(bottomPadding10);

        VBox instructionsAreaWrapper = new VBox(new Label("Instructions:"), instructionsArea);
        instructionsAreaWrapper.setPadding(bottomPadding10);

        VBox addRecipeWrapper = new VBox();

        VBox addIngredientWrapper = new VBox();
        HBox ingredientWrapper = generateIngredientWrapper(addIngredientWrapper);
        addIngredientWrapper.getChildren().add(ingredientWrapper);

        Button saveRecipeButton = new Button("Save recipe");

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.web("#E42800"));
        errorLabel.setPadding(new Insets(10, 0, 0, 0));

        addRecipeWrapper.getChildren().add(nameFieldWrapper);
        addRecipeWrapper.getChildren().add(timeFieldWrapper);
        addRecipeWrapper.getChildren().add(addIngredientWrapper);
        addRecipeWrapper.getChildren().add(instructionsAreaWrapper);
        addRecipeWrapper.getChildren().add(saveRecipeButton);
        addRecipeWrapper.getChildren().add(errorLabel);

        addRecipeWrapper.setPadding(padding25);

        addRecipeTab.setContent(addRecipeWrapper);

        saveRecipeButton.setOnAction(e -> {
            errorLabel.setText("");

            String name = nameField.getText();
            if (name.isBlank()) {
                errorLabel.setText("The recipe name can't be empty!");
                return;
            }

            int time = 0;
            try {
                time = Integer.parseInt(timeField.getText());
            } catch (NumberFormatException ex) {
                errorLabel.setText("The cooking time must be a number!");
            }

            String instructions = instructionsArea.getText();
            if (instructions.isBlank()) {
                errorLabel.setText("The recipe instructions are missing!");
                return;
            }

            Map<Ingredient, Integer> ingredients = new HashMap<>();

            addIngredientWrapper.getChildren().forEach(node -> {
                HBox singleIng = (HBox) node;
                TextField singleIngNameField = (TextField) singleIng.getChildren().get(1);
                String singleIngName = singleIngNameField.getText();
                if (singleIngName.isEmpty()) {
                    return;
                }
                TextField singleIngAmountField = (TextField) singleIng.getChildren().get(3);
                int singleIngAmount = Integer.parseInt(singleIngAmountField.getText());
                ChoiceBox<String> singleIngUnitChoiceBox = (ChoiceBox<String>) singleIng.getChildren().get(4);
                String singleIngUnit = singleIngUnitChoiceBox.getValue();
                Ingredient ingredient = ingredientService.addIngredient(singleIngName, singleIngUnit);
                ingredients.put(ingredient, singleIngAmount);
            });

            if (ingredients.isEmpty()) {
                errorLabel.setText("The recipe must have at least one ingredient!");
                return;
            }

            recipeService.addRecipe(name, ingredients, time, instructions);
            nameField.clear();
            timeField.clear();
            instructionsArea.clear();
            ObservableList<Node> ingredientFields = addIngredientWrapper.getChildren();
            while (ingredientFields.size() > 1) {
                ingredientFields.remove(ingredientFields.size() - 1);
            }
            TextField singleIngNameField = (TextField) ingredientWrapper.getChildren().get(1);
            singleIngNameField.clear();
            TextField singleIngAmountField = (TextField) ingredientWrapper.getChildren().get(3);
            singleIngAmountField.clear();
            Button addIngredientButton = (Button) ingredientWrapper.getChildren().get(5);
            addIngredientButton.setVisible(true);
            refreshRecipes(recipeList);
        });

        return addRecipeTab;
    }

    private Tab initiateSearchRecipeTab() {
        Tab searchRecipeTab = new Tab("Search recipes");

        TextField nameSearchField = new TextField();
        Label nameSearchFieldLabel = new Label("Search by name:");
        HBox nameSearchFieldWrapper = new HBox(nameSearchFieldLabel, nameSearchField);
        nameSearchFieldWrapper.setAlignment(Pos.CENTER_LEFT);
        nameSearchFieldWrapper.setSpacing(10);
        nameSearchFieldWrapper.setPadding(bottomPadding10);

        TextField ingredientSearchField = new TextField();
        Label ingredientSearchFieldLabel = new Label("Search by ingredient:");
        HBox ingredientSearchFieldWrapper = new HBox(ingredientSearchFieldLabel, ingredientSearchField);
        ingredientSearchFieldWrapper.setAlignment(Pos.CENTER_LEFT);
        ingredientSearchFieldWrapper.setSpacing(10);
        ingredientSearchFieldWrapper.setPadding(bottomPadding10);

        ListView<Recipe> foundRecipes = new ListView<>();

        Button searchButton = new Button("Search");
        searchButton.setOnAction(e -> {
            String name = ingredientSearchField.getText();
            List<Recipe> recipes = recipeService.findByIngredient(name);
            ObservableList<Recipe> observableRecipeList = FXCollections.observableList(recipes);
            foundRecipes.setItems(observableRecipeList);

            foundRecipes.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(Recipe item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null || item.getName() == null) {
                        setText(null);
                    } else {
                        setText(item.getName());
                    }
                }
            });
        });

        Label recipeLabel = new Label();
        Button showRecipeButton = generateShowRecipeButton(foundRecipes, recipeLabel);

        HBox buttonsWrapper = new HBox();
        buttonsWrapper.setPadding(new Insets(10, 0, 10, 0));
        buttonsWrapper.setSpacing(10);
        buttonsWrapper.getChildren().addAll(showRecipeButton);

        VBox searchRecipeWrapper = new VBox(ingredientSearchFieldWrapper, searchButton, foundRecipes, buttonsWrapper,
                recipeLabel);
        searchRecipeWrapper.setPadding(padding25);

        searchRecipeTab.setContent(searchRecipeWrapper);

        return searchRecipeTab;
    }

    private void refreshRecipes(ListView<Recipe> recipeList) {
        List<Recipe> recipes = recipeService.listAll();

        ObservableList<Recipe> observableRecipeList = FXCollections.observableList(recipes);
        recipeList.setItems(observableRecipeList);

        recipeList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Recipe item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null || item.getName() == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });
    }

    private HBox generateIngredientWrapper(VBox addIngredientWrapper) {
        TextField ingredientNameField = new TextField();
        TextField ingredientAmountField = new TextField();

        ChoiceBox<String> unitChoiceBox = new ChoiceBox<>();
        unitChoiceBox.getItems().add("g");
        unitChoiceBox.getItems().add("dl");
        unitChoiceBox.getItems().add("pcs");
        unitChoiceBox.getItems().add("tbs");
        unitChoiceBox.getItems().add("tsp");

        unitChoiceBox.getSelectionModel().selectFirst();

        Button addIngredientButton = new Button("+");

        HBox ingredientWrapper = new HBox(new Label("Ingredient:"), ingredientNameField, new Label("Amount:"),
                ingredientAmountField, unitChoiceBox, addIngredientButton);

        ingredientWrapper.setAlignment(Pos.CENTER_LEFT);
        ingredientWrapper.setSpacing(10);
        ingredientWrapper.setPadding(new Insets(0, 0, 5, 0));

        addIngredientButton.setOnAction(e -> {
            addIngredientWrapper.getChildren().add(generateIngredientWrapper(addIngredientWrapper));
            addIngredientButton.setVisible(false);
        });

        return ingredientWrapper;
    }

    private Button generateShowRecipeButton(ListView<Recipe> recipeList, Label recipeLabel) {
        Button showRecipeButton = new Button("Show recipe");
        showRecipeButton.setOnAction(e -> {
            if (recipeList.getSelectionModel().getSelectedItem() == null) {
                return;
            }
            recipeLabel.setText(recipeList.getSelectionModel().getSelectedItem().toString());
        });
        return showRecipeButton;
    }

    @Override
    public void stop() {
        databaseConnector.closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
