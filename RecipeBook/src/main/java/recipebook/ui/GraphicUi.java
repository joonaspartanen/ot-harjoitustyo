package recipebook.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import recipebook.dao.ArrayListIngredientDao;
import recipebook.dao.ArrayListRecipeDao;
import recipebook.dao.IngredientDao;
import recipebook.dao.RecipeDao;
import recipebook.domain.Ingredient;
import recipebook.domain.IngredientService;
import recipebook.domain.Recipe;
import recipebook.domain.RecipeService;

public class GraphicUi extends Application {

  private RecipeService recipeService;
  private IngredientService ingService;

  @Override
  public void init() {
    IngredientDao ingDao = new ArrayListIngredientDao();
    RecipeDao recipeDao = new ArrayListRecipeDao(ingDao);
    recipeService = new RecipeService(recipeDao);
    ingService = new IngredientService(ingDao);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    primaryStage.setTitle("Java Recipe Book");

    TabPane tabPane = new TabPane();
    tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);

    Tab allRecipesTab = new Tab("All Recipes");
    Tab addRecipeTab = new Tab("Add Recipe");
    Tab searchRecipeTab = new Tab("Search recipes");
    Tab recipeBookTab = new Tab("My Recipebook");

    tabPane.getTabs().add(allRecipesTab);
    tabPane.getTabs().add(addRecipeTab);
    tabPane.getTabs().add(searchRecipeTab);
    tabPane.getTabs().add(recipeBookTab);

    // Add recipe tab

    TextField nameField = new TextField();
    TextField timeField = new TextField();
    TextArea instructionsArea = new TextArea();

    Insets defaultInsets = new Insets(0, 0, 10, 0);

    HBox nameFieldWrapper = new HBox(new Label("Recipe name:"), nameField);
    nameFieldWrapper.setAlignment(Pos.CENTER_LEFT);
    nameFieldWrapper.setSpacing(10);
    nameFieldWrapper.setPadding(defaultInsets);

    HBox timeFieldWrapper = new HBox(new Label("Cooking time:"), timeField);
    timeFieldWrapper.setAlignment(Pos.CENTER_LEFT);
    timeFieldWrapper.setSpacing(10);
    timeFieldWrapper.setPadding(defaultInsets);

    VBox instructionsAreaWrapper = new VBox(new Label("Instructions:"), instructionsArea);
    instructionsAreaWrapper.setPadding(defaultInsets);

    VBox addRecipeWrapper = new VBox();

    VBox addIngredientWrapper = new VBox();
    HBox ingredientWrapper = generateIngredientWrapper(addIngredientWrapper);
    addIngredientWrapper.getChildren().add(ingredientWrapper);

    Button saveRecipeButton = new Button("Save recipe");

    addRecipeWrapper.getChildren().add(nameFieldWrapper);
    addRecipeWrapper.getChildren().add(timeFieldWrapper);
    addRecipeWrapper.getChildren().add(addIngredientWrapper);
    addRecipeWrapper.getChildren().add(instructionsAreaWrapper);
    addRecipeWrapper.getChildren().add(saveRecipeButton);

    addRecipeWrapper.setPadding(new Insets(25, 25, 25, 25));

    addRecipeTab.setContent(addRecipeWrapper);

    saveRecipeButton.setOnAction(e -> {
      String name = nameField.getText();
      int time = Integer.parseInt(timeField.getText());
      String instructions = instructionsArea.getText();

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
        ChoiceBox<String> singleIngUnitChoiceBox = (ChoiceBox) singleIng.getChildren().get(4);
        String singleIngUnit = singleIngUnitChoiceBox.getValue();
        Ingredient ingredient = ingService.addIngredient(singleIngName, singleIngUnit);
        ingredients.put(ingredient, singleIngAmount);
      });

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
    });

    // All recipes tab
    ListView<Recipe> recipeList = new ListView<>();

    VBox allRecipesWrapper = new VBox();
    Button updateRecipeListButton = new Button("Update view");
    Button showRecipeButton = new Button("Show recipe");
    Label recipeLabel = new Label();

    updateRecipeListButton.setOnAction(e -> {
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

    });

    showRecipeButton.setOnAction(e -> {
      recipeLabel.setText(recipeList.getSelectionModel().getSelectedItem().toString());
    });

    HBox buttonsWrapper = new HBox();
    buttonsWrapper.setPadding(new Insets(10, 0, 10, 0));
    buttonsWrapper.setSpacing(10);
    buttonsWrapper.getChildren().addAll(updateRecipeListButton, showRecipeButton);

    allRecipesWrapper.getChildren().add(recipeList);
    allRecipesWrapper.getChildren().add(buttonsWrapper);
    allRecipesWrapper.getChildren().add(recipeLabel);
    allRecipesWrapper.setPadding(new Insets(25, 25, 25, 25));
    allRecipesTab.setContent(allRecipesWrapper);

    // Search recipe tab

    TextField nameSearchField = new TextField();
    Label nameSearchFieldLabel = new Label("Search by name:");
    HBox nameSearchFieldWrapper = new HBox(nameSearchFieldLabel, nameSearchField);
    nameSearchFieldWrapper.setAlignment(Pos.CENTER_LEFT);
    nameSearchFieldWrapper.setSpacing(10);
    nameSearchFieldWrapper.setPadding(defaultInsets);

    TextField ingredientSearchField = new TextField();
    Label ingredientSearchFieldLabel = new Label("Search by ingredient:");
    HBox ingredientSearchFieldWrapper = new HBox(ingredientSearchFieldLabel, ingredientSearchField);
    ingredientSearchFieldWrapper.setAlignment(Pos.CENTER_LEFT);
    ingredientSearchFieldWrapper.setSpacing(10);
    ingredientSearchFieldWrapper.setPadding(defaultInsets);

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

    VBox searchRecipeWrapper = new VBox(ingredientSearchFieldWrapper, searchButton, foundRecipes);
    searchRecipeWrapper.setPadding(new Insets(25, 25, 25, 25));

    searchRecipeTab.setContent(searchRecipeWrapper);

    StackPane root = new StackPane();
    root.getChildren().add(tabPane);
    primaryStage.setScene(new Scene(root, 1280, 800));
    primaryStage.show();

  }

  private HBox generateIngredientWrapper(VBox addIngredientWrapper) {
    TextField ingredientNameField = new TextField();
    TextField ingredientAmountField = new TextField();

    ChoiceBox<String> unitChoiceBox = new ChoiceBox<>();
    unitChoiceBox.getItems().add("g");
    unitChoiceBox.getItems().add("l");
    unitChoiceBox.getItems().add("pcs");
    unitChoiceBox.getItems().add("tbs");
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

  public static void main(String[] args) {
    launch(args);
  }
}