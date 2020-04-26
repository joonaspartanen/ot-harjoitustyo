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

public class GraphicUi extends Application {

    private RecipeService recipeService;
    private IngredientService ingredientService;
    private UserService userService;
    DataStoreConnector connector;
    Properties properties;

    VBox mainContainer;
    BorderPane titleWrapper;
    BorderPane userControlView;
    TabPane tabPane;
    private ListView<Recipe> recipeList;
    Label currentUserLabel;
    Button logoutButton;
    Alert alert;

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
        } catch (DatabaseException e) {
            showAlert("Database error", "Error!", e.getMessage(), AlertType.ERROR);
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

        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);

        Tab allRecipesTab = initiateAllRecipesTab();
        Tab addRecipeTab = initiateAddRecipeTab();
        Tab searchRecipeTab = initiateSearchRecipeTab();
        Tab recipeBookTab = new Tab("My Recipebook");

        tabPane.getTabs().add(allRecipesTab);
        tabPane.getTabs().add(addRecipeTab);
        tabPane.getTabs().add(searchRecipeTab);
        tabPane.getTabs().add(recipeBookTab);

        StackPane tabs = new StackPane();
        tabs.getChildren().add(tabPane);

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
        mainContainer.getChildren().add(tabPane);
    }

    private void showLogoutView() {
        mainContainer.getChildren().remove(tabPane);
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
        Tab allRecipesTab = new Tab("All Recipes");

        recipeList = new ListView<>();

        VBox allRecipesWrapper = new VBox();
        Label recipeLabel = new Label();
        recipeLabel.setWrapText(true);
        recipeLabel.setMinHeight(Region.USE_PREF_SIZE);
        Button showRecipeButton = generateShowRecipeButton(recipeList, recipeLabel);
        Button deleteRecipeButton = new Button("Delete recipe");

        refreshRecipes(recipeList);

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
        buttonsWrapper.getChildren().addAll(showRecipeButton, deleteRecipeButton);

        allRecipesWrapper.getChildren().add(recipeList);
        allRecipesWrapper.getChildren().add(buttonsWrapper);
        allRecipesWrapper.getChildren().add(recipeLabel);
        allRecipesWrapper.setPadding(PADDING25);
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

            Map<Ingredient, Integer> ingredients = new HashMap<>();

            addIngredientWrapper.getChildren().forEach(node -> {
                HBox singleIngredient = (HBox) node;
                TextField singleIngredientNameField = (TextField) singleIngredient.getChildren().get(1);
                String singleIngredientName = singleIngredientNameField.getText();
                if (singleIngredientName.isEmpty()) {
                    errorLabel.setText("The ingredient name must be specified!");
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

                ChoiceBox<String> singleIngredientUnitChoiceBox = (ChoiceBox<String>) singleIngredient.getChildren()
                        .get(4);
                String singleIngredientUnit = singleIngredientUnitChoiceBox.getValue();
                Ingredient ingredient = ingredientService.createIngredient(singleIngredientName, singleIngredientUnit);
                ingredients.put(ingredient, singleIngredientAmount);
            });

            if (ingredients.isEmpty()) {
                errorLabel.setText("The recipe must have at least one ingredient!");
                return;
            }

            recipeService.createRecipe(name, ingredients, time, instructions);
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
        try {
            connector.closeDataStore();
        } catch (DatabaseException e) {
            showAlert("Database error", "Error!", e.getMessage(), AlertType.ERROR);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
