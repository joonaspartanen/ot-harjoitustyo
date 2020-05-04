package recipebook.ui;

import java.io.*;
import java.util.*;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import recipebook.dao.*;
import recipebook.dao.ingredientdao.*;
import recipebook.dao.recipedao.*;
import recipebook.dao.userdao.UserDao;
import recipebook.dao.userdao.UserNotFoundException;
import recipebook.domain.ingredient.Ingredient;
import recipebook.domain.ingredient.IngredientService;
import recipebook.domain.recipe.Recipe;
import recipebook.domain.recipe.RecipeService;
import recipebook.domain.user.BadUsernameException;
import recipebook.domain.user.User;
import recipebook.domain.user.UserService;

/**
 * The graphic UI for the application.
 */
public class GraphicUi extends Application {

    private RecipeService recipeService;
    private IngredientService ingredientService;
    private UserService userService;
    private DataStoreConnector connector;
    private Properties properties;

    private VBox mainContainer;
    private BorderPane titleWrapper;
    private BorderPane userControlView;
    private TabPane recipesTabPane;
    private ListView<Recipe> recipeListView;
    private ListView<Recipe> favoriteRecipesListView;
    private Label currentUserLabel;
    private Button logoutButton;
    private Alert alert;

    private Insets PADDINGBOTTOM10 = new Insets(0, 0, 10, 0);
    private Insets PADDING25 = new Insets(25, 25, 25, 25);

    @Override
    public void init() {

        properties = new Properties();
        try {
            properties.load(new FileInputStream("config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String dataStoreType = properties.getProperty("dataStoreType");
        String dataStoreLocation = properties.getProperty("dataStoreLocation");

        if (dataStoreType.equals("database")) {
            connector = new DatabaseConnector(dataStoreLocation);
        } else if (dataStoreType.equals("file")) {
            connector = new FileConnector(dataStoreLocation);
        }

        try {
            connector.initializeDataStore();
        } catch (DataStoreException ex) {
            showAlert("Database error", "Error!", ex.getMessage(), AlertType.ERROR);
        } catch (UserNotFoundException ex) {
            showAlert("User not found", "Error!", ex.getMessage(), AlertType.ERROR);
        }

        IngredientDao ingredientDao = connector.getIngredientDao();
        RecipeDao recipeDao = connector.getRecipeDao();
        UserDao userDao = connector.getUserDao();

        userService = new UserService(userDao);
        ingredientService = new IngredientService(ingredientDao);
        recipeService = new RecipeService(recipeDao, userService);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Java Recipe Book");

        recipesTabPane = new TabPane();
        recipesTabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);

        Tab allRecipesTab = initiateAllRecipesTab();
        Tab addRecipeTab = initiateAddRecipeTab();
        Tab searchRecipeTab = initiateSearchRecipeTab();
        Tab favoriteRecipesTab = initiateFavoriteRecipesTab();

        recipesTabPane.getTabs().add(allRecipesTab);
        recipesTabPane.getTabs().add(addRecipeTab);
        recipesTabPane.getTabs().add(searchRecipeTab);
        recipesTabPane.getTabs().add(favoriteRecipesTab);

        StackPane tabs = new StackPane();
        tabs.getChildren().add(recipesTabPane);

        alert = new Alert(AlertType.NONE);

        mainContainer = new VBox();

        titleWrapper = new BorderPane();
        titleWrapper.setPadding(PADDING25);

        Label appTitle = new Label("Welcome to Java Recipe Book");
        appTitle.setFont(new Font(30));

        HBox currentUserWrapper = new HBox();

        currentUserLabel = new Label("No user logged in");
        logoutButton = new Button("Logout");
        logoutButton.setVisible(false);

        logoutButton.setOnAction(e -> {
            userService.logout();
            currentUserLabel.setText("");
            logoutButton.setVisible(false);
            showLogoutView();
        });

        currentUserWrapper.setSpacing(10);
        currentUserWrapper.getChildren().addAll(currentUserLabel, logoutButton);
        currentUserWrapper.setAlignment(Pos.CENTER_LEFT);

        titleWrapper.setLeft(appTitle);
        titleWrapper.setRight(currentUserWrapper);

        mainContainer.getChildren().add(titleWrapper);

        if (userService.userNotLoggedIn()) {
            showUserControlView();
        }

        primaryStage.setScene(new Scene(mainContainer, 1600, 1000));
        primaryStage.show();
    }

    private void showUserControlView() {
        userControlView = initiateUserControlView();
        mainContainer.getChildren().add(userControlView);
    }

    private void showLoggedInView() {
        mainContainer.getChildren().remove(userControlView);
        mainContainer.getChildren().add(recipesTabPane);
    }

    private void showLogoutView() {
        mainContainer.getChildren().remove(recipesTabPane);
        showUserControlView();
    }

    private void showAlert(String title, String header, String message, AlertType type) {
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.setAlertType(type);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.show();
    }

    private BorderPane initiateUserControlView() {
        BorderPane userControlView = new BorderPane();

        VBox userControlWrapper = new VBox();

        HBox loginWrapper = new HBox();
        Label loginInputLabel = new Label("Login: ");
        TextField loginInputField = new TextField();
        Button loginButton = new Button("Login");

        loginWrapper.getChildren().addAll(loginInputLabel, loginInputField, loginButton);
        loginWrapper.setSpacing(10);
        loginWrapper.setPadding(PADDINGBOTTOM10);
        loginWrapper.setAlignment(Pos.CENTER);

        loginButton.setOnAction(e -> {
            String username = loginInputField.getText();
            try {
                loginInputField.clear();
                User currentUser = userService.login(username);
                currentUserLabel.setText(currentUser.getUsername());
                logoutButton.setVisible(true);
                showLoggedInView();
                refreshFavoriteRecipesView(favoriteRecipesListView);
            } catch (UserNotFoundException ex) {
                showAlert("User Error", "Error!", ex.getMessage(), AlertType.ERROR);
            }

        });

        HBox newUserWrapper = new HBox();
        Label newUserLabel = new Label("New user?");
        TextField newUserInputField = new TextField();
        Button newUserButton = new Button("Create user account");
        newUserWrapper.getChildren().addAll(newUserLabel, newUserInputField, newUserButton);
        newUserWrapper.setSpacing(10);
        newUserWrapper.setPadding(PADDINGBOTTOM10);
        newUserWrapper.setAlignment(Pos.CENTER);

        Label userErrorLabel = new Label();

        newUserButton.setOnAction(e -> {
            String newUserName = newUserInputField.getText();
            try {
                newUserInputField.clear();
                User user = userService.createUser(newUserName);
                alert.setAlertType(AlertType.INFORMATION);
                alert.setTitle("New user created");
                alert.setHeaderText("Welcome!");
                alert.setContentText("New user " + user.getUsername() + " created.");
                alert.show();
            } catch (BadUsernameException ex) {
                showAlert("User error", "Bad username", ex.getMessage(), AlertType.ERROR);
            } catch (DataStoreException ex) {
                showAlert("Data store error", "Error!", ex.getMessage(), AlertType.ERROR);
            }
        });

        userControlWrapper.getChildren().addAll(loginWrapper, newUserWrapper, userErrorLabel);
        userControlWrapper.setPadding(PADDING25);
        userControlWrapper.setAlignment(Pos.CENTER);

        userControlView.setCenter(userControlWrapper);
        userControlView.setMinHeight(600);

        return userControlView;
    }

    private Tab initiateAllRecipesTab() {
        Tab allRecipesTab = new Tab("All recipes");

        recipeListView = new ListView<>();

        VBox allRecipesWrapper = new VBox();
        Label recipeLabel = new Label();
        recipeLabel.setWrapText(true);
        recipeLabel.setMinHeight(Region.USE_PREF_SIZE);
        Button showRecipeButton = generateShowRecipeButton(recipeListView, recipeLabel);

        Button addRecipeToFavoritesButton = new Button("Add to favorites");
        addRecipeToFavoritesButton.setOnAction(e -> {
            if (noRecipeSelected()) {
                return;
            }
            Recipe recipe = recipeListView.getSelectionModel().getSelectedItem();
            try {
                recipeService.addRecipeToFavorites(recipe);
            } catch (DataStoreException ex) {
                showAlert("Data store error", "Error!", ex.getMessage(), AlertType.ERROR);
            } catch (UserNotFoundException ex) {
                showAlert("User not found", "Error!", ex.getMessage(), AlertType.ERROR);
            }
            refreshFavoriteRecipesView(favoriteRecipesListView);
        });

        Button deleteRecipeButton = new Button("Delete recipe");

        deleteRecipeButton.setOnAction(e -> {
            if (noRecipeSelected()) {
                return;
            }
            int recipeId = recipeListView.getSelectionModel().getSelectedItem().getId();
            try {
                recipeService.deleteRecipeById(recipeId);
            } catch (DataStoreException ex) {
                showAlert("Data store error", "Error!", ex.getMessage(), AlertType.ERROR);
            } catch (UserNotFoundException ex) {
                showAlert("User not found", "Error!", ex.getMessage(), AlertType.ERROR);
            }
            refreshAllRecipesView(recipeListView);
            recipeLabel.setText("");
        });

        HBox buttonsWrapper = new HBox();
        buttonsWrapper.setPadding(new Insets(10, 0, 10, 0));
        buttonsWrapper.setSpacing(10);
        buttonsWrapper.getChildren().addAll(showRecipeButton, addRecipeToFavoritesButton, deleteRecipeButton);

        refreshAllRecipesView(recipeListView);

        allRecipesWrapper.getChildren().add(recipeListView);
        allRecipesWrapper.getChildren().add(buttonsWrapper);
        allRecipesWrapper.getChildren().add(recipeLabel);
        allRecipesWrapper.setPadding(PADDING25);
        allRecipesTab.setContent(allRecipesWrapper);

        return allRecipesTab;
    }

    private boolean noRecipeSelected() {
        return recipeListView.getSelectionModel().getSelectedItem() == null;
    }

    private Tab initiateAddRecipeTab() {
        Tab addRecipeTab = new Tab("Add Recipe");

        TextField nameField = new TextField();
        TextField timeField = new TextField();
        TextArea instructionsArea = new TextArea();

        HBox nameFieldWrapper = new HBox(new Label("Recipe name:"), nameField);
        nameFieldWrapper.setAlignment(Pos.CENTER_LEFT);
        nameFieldWrapper.setSpacing(10);
        nameFieldWrapper.setPadding(PADDINGBOTTOM10);

        HBox timeFieldWrapper = new HBox(new Label("Cooking time (minutes):"), timeField);
        timeFieldWrapper.setAlignment(Pos.CENTER_LEFT);
        timeFieldWrapper.setSpacing(10);
        timeFieldWrapper.setPadding(PADDINGBOTTOM10);

        VBox instructionsAreaWrapper = new VBox(new Label("Instructions:"), instructionsArea);
        instructionsAreaWrapper.setPadding(PADDINGBOTTOM10);

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

        addRecipeWrapper.setPadding(PADDING25);

        addRecipeTab.setContent(addRecipeWrapper);

        saveRecipeButton.setOnAction(e -> {
            errorLabel.setText("");

            String name = nameField.getText();
            if (name.isBlank()) {
                errorLabel.setText("The recipe name can't be empty!");
                return;
            }

            if (name.length() > 30) {
                errorLabel.setText("The recipe name can contain only 30 characters!");
                return;
            }

            int time = 0;
            try {
                time = Integer.parseInt(timeField.getText());
            } catch (NumberFormatException ex) {
                errorLabel.setText("The cooking time must be a number!");
                return;
            }

            String instructions = instructionsArea.getText();
            if (instructions.isBlank()) {
                errorLabel.setText("The recipe instructions are missing!");
                return;
            }

            if (instructions.length() > 1000) {
                errorLabel.setText("The instructions can contain only 1000 characters!");
            }

            Map<Ingredient, Integer> ingredients = new HashMap<>();

            addIngredientWrapper.getChildren().forEach(node -> {
                HBox singleIngredient = (HBox) node;
                TextField singleIngredientNameField = (TextField) singleIngredient.getChildren().get(1);
                String singleIngredientName = singleIngredientNameField.getText();
                if (singleIngredientName.isEmpty()) {
                    errorLabel.setText("The ingredient name must be specified!");
                    return;
                }

                if (singleIngredientName.length() > 30) {
                    errorLabel.setText("The ingredient name can contain only 30 characters!");
                    return;
                }

                TextField singleIngredientAmountField = (TextField) singleIngredient.getChildren().get(3);
                int singleIngredientAmount = 0;
                try {
                    singleIngredientAmount = Integer.parseInt(singleIngredientAmountField.getText());
                } catch (NumberFormatException ex) {
                    errorLabel.setText("The ingredient amount must be numeric!");
                    return;
                }

                @SuppressWarnings("unchecked")
                ChoiceBox<String> singleIngredientUnitChoiceBox = (ChoiceBox<String>) singleIngredient.getChildren()
                        .get(4);

                String singleIngredientUnit = singleIngredientUnitChoiceBox.getValue();

                try {
                    Ingredient ingredient = ingredientService.createIngredient(singleIngredientName,
                            singleIngredientUnit);
                    ingredients.put(ingredient, singleIngredientAmount);

                } catch (DataStoreException ex) {
                    showAlert("Data store error", "Error!", ex.getMessage(), AlertType.ERROR);
                }
            });

            if (ingredients.isEmpty()) {
                errorLabel.setText("The recipe must have at least one ingredient!");
                return;
            }

            try {
                recipeService.createRecipe(name, ingredients, time, instructions);
            } catch (DataStoreException ex) {
                showAlert("Data store error", "Error!", ex.getMessage(), AlertType.ERROR);
            }
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
            refreshAllRecipesView(recipeListView);
            refreshFavoriteRecipesView(favoriteRecipesListView);
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
        nameSearchFieldWrapper.setPadding(PADDINGBOTTOM10);

        TextField ingredientSearchField = new TextField();
        Label ingredientSearchFieldLabel = new Label("Search by ingredient:");
        HBox ingredientSearchFieldWrapper = new HBox(ingredientSearchFieldLabel, ingredientSearchField);
        ingredientSearchFieldWrapper.setAlignment(Pos.CENTER_LEFT);
        ingredientSearchFieldWrapper.setSpacing(10);
        ingredientSearchFieldWrapper.setPadding(PADDINGBOTTOM10);

        ListView<Recipe> foundRecipes = new ListView<>();

        Button searchButton = new Button("Search");
        searchButton.setOnAction(e -> {
            String name = ingredientSearchField.getText();
            List<Recipe> recipes;
            try {
                recipes = recipeService.findByIngredient(name);
            } catch (DataStoreException ex) {
                showAlert("Data store error", "Error!", ex.getMessage(), AlertType.ERROR);
                return;
            } catch (UserNotFoundException ex) {
                showAlert("User not found", "Error!", ex.getMessage(), AlertType.ERROR);
                return;
            }
            
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
        recipeLabel.setWrapText(true);
        recipeLabel.setMinHeight(Region.USE_PREF_SIZE);

        Button showRecipeButton = generateShowRecipeButton(foundRecipes, recipeLabel);

        HBox buttonsWrapper = new HBox();
        buttonsWrapper.setPadding(new Insets(10, 0, 10, 0));
        buttonsWrapper.setSpacing(10);
        buttonsWrapper.getChildren().addAll(showRecipeButton);

        VBox searchRecipeWrapper = new VBox(ingredientSearchFieldWrapper, searchButton, foundRecipes, buttonsWrapper,
                recipeLabel);
        searchRecipeWrapper.setPadding(PADDING25);

        searchRecipeTab.setContent(searchRecipeWrapper);

        return searchRecipeTab;
    }

    private Tab initiateFavoriteRecipesTab() {
        Tab favoriteRecipesTab = new Tab("My favorite recipes");
        favoriteRecipesListView = new ListView<>();

        VBox favoriteRecipesWrapper = new VBox();
        Label recipeLabel = new Label();
        recipeLabel.setWrapText(true);
        recipeLabel.setMinHeight(Region.USE_PREF_SIZE);
        Button showRecipeButton = generateShowRecipeButton(favoriteRecipesListView, recipeLabel);

        HBox buttonsWrapper = new HBox();
        buttonsWrapper.setPadding(new Insets(10, 0, 10, 0));
        buttonsWrapper.setSpacing(10);
        buttonsWrapper.getChildren().addAll(showRecipeButton);

        favoriteRecipesWrapper.getChildren().add(favoriteRecipesListView);
        favoriteRecipesWrapper.getChildren().add(buttonsWrapper);
        favoriteRecipesWrapper.getChildren().add(recipeLabel);
        favoriteRecipesWrapper.setPadding(PADDING25);
        favoriteRecipesTab.setContent(favoriteRecipesWrapper);

        return favoriteRecipesTab;
    }

    private void refreshFavoriteRecipesView(ListView<Recipe> favoriteRecipesListView) {
        List<Recipe> favoriteRecipes = new ArrayList<>();
        try {
            favoriteRecipes = recipeService.getFavoriteRecipes();
        } catch (DataStoreException ex) {
            showAlert("Data store error", "Error!", ex.getMessage(), AlertType.ERROR);
        } catch (UserNotFoundException ex) {
            showAlert("User not found", "Error!", ex.getMessage(), AlertType.ERROR);
        }

        ObservableList<Recipe> observableRecipeList = FXCollections.observableList(favoriteRecipes);
        favoriteRecipesListView.setItems(observableRecipeList);

        favoriteRecipesListView.setCellFactory(param -> new ListCell<>() {
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

    private void refreshAllRecipesView(ListView<Recipe> recipeList) {
        List<Recipe> recipes = new ArrayList<>();
        try {
            recipes = recipeService.listAll();
        } catch (DataStoreException ex) {
            showAlert("Data store error", "Error!", ex.getMessage(), AlertType.ERROR);
        } catch (UserNotFoundException ex) {
            showAlert("User not found", "Error!", ex.getMessage(), AlertType.ERROR);
        }

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
        try {
            connector.closeDataStore();
        } catch (DataStoreException e) {
            showAlert("Data store error", "Error!", e.getMessage(), AlertType.ERROR);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
