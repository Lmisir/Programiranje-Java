package hr.gamestop.controller;

import hr.gamestop.Main;
import hr.gamestop.data.GameStopData;
import hr.gamestop.model.Game;
import hr.gamestop.repository.GameStopRepository;
import hr.gamestop.service.GameStopService;
import hr.gamestop.util.FileExporter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class GameController {

    @FXML
    private TableView<Game> gameTable;

    @FXML
    private TableColumn<Game, Integer> idColumn;

    @FXML
    private TableColumn<Game, String> nameColumn;

    @FXML
    private TableColumn<Game, String> platformColumn;

    @FXML
    private TableColumn<Game, String> genreColumn;

    @FXML
    private TableColumn<Game, Double> priceColumn;

    @FXML
    private TableColumn<Game, Integer> stockColumn;

    @FXML
    private TableColumn<Game, Integer> pegiColumn;

    @FXML
    private TextField searchField;


    private GameStopData data;

    private GameStopService service;

    private final GameStopRepository repository =
            new GameStopRepository();

    private final ObservableList<Game> gameItems =
            FXCollections.observableArrayList();


    // =====================================================
    // INITIALIZE
    // =====================================================

    @FXML
    private void initialize() {

        idColumn.setCellValueFactory(
                new PropertyValueFactory<>("id")
        );

        nameColumn.setCellValueFactory(
                new PropertyValueFactory<>("name")
        );

        platformColumn.setCellValueFactory(
                new PropertyValueFactory<>("platform")
        );

        genreColumn.setCellValueFactory(
                new PropertyValueFactory<>("genre")
        );

        priceColumn.setCellValueFactory(
                new PropertyValueFactory<>("price")
        );

        stockColumn.setCellValueFactory(
                new PropertyValueFactory<>("stock")
        );

        pegiColumn.setCellValueFactory(
                new PropertyValueFactory<>("pegi")
        );

        gameTable.setItems(gameItems);

        gameTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );


        // PRETRAGA UŽIVO
        searchField.textProperty().addListener(
                (observable, oldValue, newValue) ->
                        refreshGameTable()
        );


        loadData();
    }


    // =====================================================
    // UČITAVANJE PODATAKA
    // =====================================================

    private void loadData() {

        Task<GameStopData> loadTask =
                new Task<>() {

                    @Override
                    protected GameStopData call()
                            throws Exception {

                        return repository.loadAll();
                    }
                };


        loadTask.setOnSucceeded(event -> {

            data = loadTask.getValue();

            service =
                    new GameStopService(data);

            refreshGameTable();
        });


        loadTask.setOnFailed(event -> {

            showError(
                    "Greška pri učitavanju podataka."
            );

            if (loadTask.getException() != null) {

                loadTask
                        .getException()
                        .printStackTrace();
            }
        });


        Thread thread =
                new Thread(loadTask);

        thread.setDaemon(true);

        thread.start();
    }


    // =====================================================
    // REFRESH + PRETRAGA
    // =====================================================

    private void refreshGameTable() {

        if (service == null) {
            return;
        }


        List<Game> games;

        String searchText =
                searchField.getText();


        if (searchText == null
                || searchText.isBlank()) {

            games =
                    service.getAllGames();

        } else {

            games =
                    service.searchGamesByName(
                            searchText
                    );
        }


        gameItems.setAll(games);

        gameTable.refresh();
    }


    // =====================================================
    // CREATE - DODAJ IGRU
    // =====================================================

    @FXML
    private void onAddGame() {

        if (service == null) {
            return;
        }


        Dialog<ButtonType> dialog =
                new Dialog<>();

        dialog.setTitle(
                "Dodaj igru"
        );

        dialog.setHeaderText(
                "Unesite podatke nove igre"
        );


        TextField nameField =
                new TextField();

        TextField platformField =
                new TextField();

        TextField genreField =
                new TextField();

        TextField priceField =
                new TextField();

        TextField stockField =
                new TextField();

        TextField pegiField =
                new TextField();


        GridPane grid =
                createGameForm(
                        nameField,
                        platformField,
                        genreField,
                        priceField,
                        stockField,
                        pegiField
                );


        dialog.getDialogPane()
                .setContent(grid);


        dialog.getDialogPane()
                .getButtonTypes()
                .addAll(
                        ButtonType.OK,
                        ButtonType.CANCEL
                );


        dialog.showAndWait()
                .ifPresent(button -> {

                    if (button != ButtonType.OK) {
                        return;
                    }


                    try {

                        String name =
                                nameField
                                        .getText()
                                        .trim();

                        String platform =
                                platformField
                                        .getText()
                                        .trim();

                        String genre =
                                genreField
                                        .getText()
                                        .trim();


                        double price =
                                Double.parseDouble(
                                        priceField.getText()
                                );

                        int stock =
                                Integer.parseInt(
                                        stockField.getText()
                                );

                        int pegi =
                                Integer.parseInt(
                                        pegiField.getText()
                                );


                        if (name.isBlank()
                                || platform.isBlank()
                                || genre.isBlank()) {

                            showError(
                                    "Sva polja moraju biti popunjena."
                            );

                            return;
                        }


                        Game game =
                                new Game(
                                        service.getNextGameId(),
                                        name,
                                        price,
                                        stock,
                                        platform,
                                        genre,
                                        pegi
                                );


                        boolean added =
                                service.addGame(game);


                        if (!added) {

                            showError(
                                    "Igra s tim nazivom već postoji."
                            );

                            return;
                        }


                        saveAndRefresh();

                    } catch (NumberFormatException e) {

                        showError(
                                "Cijena, zaliha i PEGI moraju biti brojevi."
                        );
                    }
                });
    }


    // =====================================================
    // UPDATE - UREDI IGRU
    // =====================================================

    @FXML
    private void onEditGame() {

        if (service == null) {
            return;
        }


        Game selectedGame =
                gameTable
                        .getSelectionModel()
                        .getSelectedItem();


        if (selectedGame == null) {

            showError(
                    "Prvo odaberite igru koju želite urediti."
            );

            return;
        }


        Dialog<ButtonType> dialog =
                new Dialog<>();

        dialog.setTitle(
                "Uredi igru"
        );

        dialog.setHeaderText(
                "Uređivanje igre: "
                        + selectedGame.getName()
        );


        TextField nameField =
                new TextField(
                        selectedGame.getName()
                );

        TextField platformField =
                new TextField(
                        selectedGame.getPlatform()
                );

        TextField genreField =
                new TextField(
                        selectedGame.getGenre()
                );

        TextField priceField =
                new TextField(
                        String.valueOf(
                                selectedGame.getPrice()
                        )
                );

        TextField stockField =
                new TextField(
                        String.valueOf(
                                selectedGame.getStock()
                        )
                );

        TextField pegiField =
                new TextField(
                        String.valueOf(
                                selectedGame.getPegi()
                        )
                );


        GridPane grid =
                createGameForm(
                        nameField,
                        platformField,
                        genreField,
                        priceField,
                        stockField,
                        pegiField
                );


        dialog.getDialogPane()
                .setContent(grid);


        dialog.getDialogPane()
                .getButtonTypes()
                .addAll(
                        ButtonType.OK,
                        ButtonType.CANCEL
                );


        dialog.showAndWait()
                .ifPresent(button -> {

                    if (button != ButtonType.OK) {
                        return;
                    }


                    try {

                        String name =
                                nameField
                                        .getText()
                                        .trim();

                        String platform =
                                platformField
                                        .getText()
                                        .trim();

                        String genre =
                                genreField
                                        .getText()
                                        .trim();


                        double price =
                                Double.parseDouble(
                                        priceField.getText()
                                );

                        int stock =
                                Integer.parseInt(
                                        stockField.getText()
                                );

                        int pegi =
                                Integer.parseInt(
                                        pegiField.getText()
                                );


                        if (name.isBlank()
                                || platform.isBlank()
                                || genre.isBlank()) {

                            showError(
                                    "Sva polja moraju biti popunjena."
                            );

                            return;
                        }


                        boolean updated =
                                service.updateGame(
                                        selectedGame.getId(),
                                        name,
                                        price,
                                        stock,
                                        platform,
                                        genre,
                                        pegi
                                );


                        if (!updated) {

                            showError(
                                    "Igru nije moguće urediti."
                            );

                            return;
                        }


                        saveAndRefresh();

                    } catch (NumberFormatException e) {

                        showError(
                                "Cijena, zaliha i PEGI moraju biti brojevi."
                        );
                    }
                });
    }


    // =====================================================
    // DELETE - OBRIŠI IGRU
    // =====================================================

    @FXML
    private void onDeleteGame() {

        if (service == null) {
            return;
        }


        Game selectedGame =
                gameTable
                        .getSelectionModel()
                        .getSelectedItem();


        if (selectedGame == null) {

            showError(
                    "Prvo odaberite igru koju želite obrisati."
            );

            return;
        }


        Alert confirmation =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        confirmation.setTitle(
                "Brisanje igre"
        );

        confirmation.setHeaderText(
                "Želite li sigurno obrisati igru?"
        );

        confirmation.setContentText(
                selectedGame.getName()
        );


        confirmation.showAndWait()
                .ifPresent(button -> {

                    if (button == ButtonType.OK) {

                        boolean deleted =
                                service.deleteGame(
                                        selectedGame.getId()
                                );


                        if (deleted) {

                            saveAndRefresh();

                        } else {

                            showError(
                                    "Igru nije moguće obrisati."
                            );
                        }
                    }
                });
    }


    // =====================================================
    // SPREMANJE + TXT EXPORT + REFRESH
    // =====================================================

    private void saveAndRefresh() {

        try {

            // Spremanje u JSON
            repository.saveAll(data);


            // Export igara u TXT
            FileExporter.exportGames(
                    data,
                    "games.txt"
            );


            // Osvježavanje TableViewa
            refreshGameTable();


        } catch (IOException e) {

            showError(
                    "Greška pri spremanju podataka."
            );

            e.printStackTrace();
        }
    }


    // =====================================================
    // FORMA ZA DODAVANJE / UREĐIVANJE
    // =====================================================

    private GridPane createGameForm(
            TextField nameField,
            TextField platformField,
            TextField genreField,
            TextField priceField,
            TextField stockField,
            TextField pegiField) {


        GridPane grid =
                new GridPane();

        grid.setHgap(10);

        grid.setVgap(10);

        grid.setPadding(
                new Insets(20)
        );


        grid.add(
                new Label("Naziv:"),
                0,
                0
        );

        grid.add(
                nameField,
                1,
                0
        );


        grid.add(
                new Label("Platforma:"),
                0,
                1
        );

        grid.add(
                platformField,
                1,
                1
        );


        grid.add(
                new Label("Žanr:"),
                0,
                2
        );

        grid.add(
                genreField,
                1,
                2
        );


        grid.add(
                new Label("Cijena EUR:"),
                0,
                3
        );

        grid.add(
                priceField,
                1,
                3
        );


        grid.add(
                new Label("Zaliha:"),
                0,
                4
        );

        grid.add(
                stockField,
                1,
                4
        );


        grid.add(
                new Label("PEGI:"),
                0,
                5
        );

        grid.add(
                pegiField,
                1,
                5
        );


        return grid;
    }


    // =====================================================
    // OTVORI KONZOLE
    // =====================================================

    @FXML
    private void onOpenConsole() {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            Main.class.getResource(
                                    "/hr/gamestop/console-view.fxml"
                            )
                    );


            Scene consoleScene =
                    new Scene(
                            loader.load()
                    );


            Stage stage =
                    (Stage)
                            gameTable
                                    .getScene()
                                    .getWindow();


            stage.setScene(
                    consoleScene
            );

            stage.setTitle(
                    "GameStop Manager - Konzole"
            );


        } catch (IOException e) {

            showError(
                    "Greška pri otvaranju konzola."
            );

            e.printStackTrace();
        }
    }


    // =====================================================
    // ODJAVA
    // =====================================================

    @FXML
    private void onLogout() {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            Main.class.getResource(
                                    "/hr/gamestop/login-view.fxml"
                            )
                    );


            Scene loginScene =
                    new Scene(
                            loader.load()
                    );


            Stage stage =
                    (Stage)
                            gameTable
                                    .getScene()
                                    .getWindow();


            stage.setScene(
                    loginScene
            );

            stage.setTitle(
                    "GameStop Manager - Login"
            );


        } catch (IOException e) {

            showError(
                    "Greška prilikom odjave."
            );

            e.printStackTrace();
        }
    }


    // =====================================================
    // ERROR
    // =====================================================

    private void showError(String message) {

        Alert alert =
                new Alert(
                        Alert.AlertType.ERROR
                );

        alert.setTitle(
                "Greška"
        );

        alert.setHeaderText(
                null
        );

        alert.setContentText(
                message
        );

        alert.showAndWait();
    }
}