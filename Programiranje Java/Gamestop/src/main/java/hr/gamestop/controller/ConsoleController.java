package hr.gamestop.controller;

import hr.gamestop.Main;
import hr.gamestop.data.GameStopData;
import hr.gamestop.model.Console;
import hr.gamestop.repository.GameStopRepository;
import hr.gamestop.service.GameStopService;

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

public class ConsoleController {

    @FXML
    private TableView<Console> consoleTable;

    @FXML
    private TableColumn<Console, Integer> idColumn;

    @FXML
    private TableColumn<Console, String> nameColumn;

    @FXML
    private TableColumn<Console, String> manufacturerColumn;

    @FXML
    private TableColumn<Console, Integer> storageColumn;

    @FXML
    private TableColumn<Console, Double> priceColumn;

    @FXML
    private TableColumn<Console, Integer> stockColumn;

    @FXML
    private TextField searchField;


    private GameStopData data;

    private GameStopService service;

    private final GameStopRepository repository =
            new GameStopRepository();

    private final ObservableList<Console> consoleItems =
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

        manufacturerColumn.setCellValueFactory(
                new PropertyValueFactory<>("manufacturer")
        );

        storageColumn.setCellValueFactory(
                new PropertyValueFactory<>("storageGb")
        );

        priceColumn.setCellValueFactory(
                new PropertyValueFactory<>("price")
        );

        stockColumn.setCellValueFactory(
                new PropertyValueFactory<>("stock")
        );


        consoleTable.setItems(consoleItems);

        consoleTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );


        // PRETRAGA UŽIVO
        searchField.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    refreshConsoleTable();
                }
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

            refreshConsoleTable();
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

    private void refreshConsoleTable() {

        if (service == null) {
            return;
        }


        List<Console> consoles;

        String searchText =
                searchField.getText();


        if (searchText == null
                || searchText.isBlank()) {

            consoles =
                    service.getAllConsoles();

        } else {

            consoles =
                    service.searchConsolesByName(
                            searchText
                    );
        }


        consoleItems.setAll(consoles);

        consoleTable.refresh();
    }


    // =====================================================
    // CREATE - DODAJ KONZOLU
    // =====================================================

    @FXML
    private void onAddConsole() {

        if (service == null) {
            return;
        }


        Dialog<ButtonType> dialog =
                new Dialog<>();

        dialog.setTitle(
                "Dodaj konzolu"
        );

        dialog.setHeaderText(
                "Unesite podatke nove konzole"
        );


        TextField nameField =
                new TextField();

        TextField manufacturerField =
                new TextField();

        TextField storageField =
                new TextField();

        TextField priceField =
                new TextField();

        TextField stockField =
                new TextField();


        GridPane grid =
                createConsoleForm(
                        nameField,
                        manufacturerField,
                        storageField,
                        priceField,
                        stockField
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

                        String manufacturer =
                                manufacturerField
                                        .getText()
                                        .trim();

                        int storageGb =
                                Integer.parseInt(
                                        storageField.getText()
                                );

                        double price =
                                Double.parseDouble(
                                        priceField.getText()
                                );

                        int stock =
                                Integer.parseInt(
                                        stockField.getText()
                                );


                        if (name.isBlank()
                                || manufacturer.isBlank()) {

                            showError(
                                    "Sva polja moraju biti popunjena."
                            );

                            return;
                        }


                        Console console =
                                new Console(
                                        service.getNextConsoleId(),
                                        name,
                                        price,
                                        stock,
                                        manufacturer,
                                        storageGb
                                );


                        boolean added =
                                service.addConsole(console);


                        if (!added) {

                            showError(
                                    "Konzola s tim nazivom već postoji."
                            );

                            return;
                        }


                        saveAndRefresh();

                    } catch (NumberFormatException e) {

                        showError(
                                "Pohrana, cijena i zaliha moraju biti brojevi."
                        );
                    }
                });
    }


    // =====================================================
    // UPDATE - UREDI KONZOLU
    // =====================================================

    @FXML
    private void onEditConsole() {

        if (service == null) {
            return;
        }


        Console selectedConsole =
                consoleTable
                        .getSelectionModel()
                        .getSelectedItem();


        if (selectedConsole == null) {

            showError(
                    "Prvo odaberite konzolu koju želite urediti."
            );

            return;
        }


        Dialog<ButtonType> dialog =
                new Dialog<>();

        dialog.setTitle(
                "Uredi konzolu"
        );

        dialog.setHeaderText(
                "Uređivanje konzole: "
                        + selectedConsole.getName()
        );


        TextField nameField =
                new TextField(
                        selectedConsole.getName()
                );

        TextField manufacturerField =
                new TextField(
                        selectedConsole.getManufacturer()
                );

        TextField storageField =
                new TextField(
                        String.valueOf(
                                selectedConsole.getStorageGb()
                        )
                );

        TextField priceField =
                new TextField(
                        String.valueOf(
                                selectedConsole.getPrice()
                        )
                );

        TextField stockField =
                new TextField(
                        String.valueOf(
                                selectedConsole.getStock()
                        )
                );


        GridPane grid =
                createConsoleForm(
                        nameField,
                        manufacturerField,
                        storageField,
                        priceField,
                        stockField
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

                        String manufacturer =
                                manufacturerField
                                        .getText()
                                        .trim();

                        int storageGb =
                                Integer.parseInt(
                                        storageField.getText()
                                );

                        double price =
                                Double.parseDouble(
                                        priceField.getText()
                                );

                        int stock =
                                Integer.parseInt(
                                        stockField.getText()
                                );


                        if (name.isBlank()
                                || manufacturer.isBlank()) {

                            showError(
                                    "Sva polja moraju biti popunjena."
                            );

                            return;
                        }


                        boolean updated =
                                service.updateConsole(
                                        selectedConsole.getId(),
                                        name,
                                        price,
                                        stock,
                                        manufacturer,
                                        storageGb
                                );


                        if (!updated) {

                            showError(
                                    "Konzolu nije moguće urediti."
                            );

                            return;
                        }


                        saveAndRefresh();

                    } catch (NumberFormatException e) {

                        showError(
                                "Pohrana, cijena i zaliha moraju biti brojevi."
                        );
                    }
                });
    }


    // =====================================================
    // DELETE - OBRIŠI KONZOLU
    // =====================================================

    @FXML
    private void onDeleteConsole() {

        if (service == null) {
            return;
        }


        Console selectedConsole =
                consoleTable
                        .getSelectionModel()
                        .getSelectedItem();


        if (selectedConsole == null) {

            showError(
                    "Prvo odaberite konzolu koju želite obrisati."
            );

            return;
        }


        Alert confirmation =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        confirmation.setTitle(
                "Brisanje konzole"
        );

        confirmation.setHeaderText(
                "Želite li sigurno obrisati konzolu?"
        );

        confirmation.setContentText(
                selectedConsole.getName()
        );


        confirmation.showAndWait()
                .ifPresent(button -> {

                    if (button == ButtonType.OK) {

                        boolean deleted =
                                service.deleteConsole(
                                        selectedConsole.getId()
                                );


                        if (deleted) {

                            saveAndRefresh();

                        } else {

                            showError(
                                    "Konzolu nije moguće obrisati."
                            );
                        }
                    }
                });
    }


    // =====================================================
    // SPREMANJE + REFRESH
    // =====================================================

    private void saveAndRefresh() {

        try {

            repository.saveAll(data);

            refreshConsoleTable();

        } catch (IOException e) {

            showError(
                    "Greška pri spremanju podataka."
            );

            e.printStackTrace();
        }
    }


    // =====================================================
    // FORMA ZA KONZOLU
    // =====================================================

    private GridPane createConsoleForm(
            TextField nameField,
            TextField manufacturerField,
            TextField storageField,
            TextField priceField,
            TextField stockField) {


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
                new Label("Proizvođač:"),
                0,
                1
        );

        grid.add(
                manufacturerField,
                1,
                1
        );


        grid.add(
                new Label("Pohrana GB:"),
                0,
                2
        );

        grid.add(
                storageField,
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


        return grid;
    }


    // =====================================================
    // POVRATAK NA IGRE
    // =====================================================

    @FXML
    private void onOpenGames() {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            Main.class.getResource(
                                    "/hr/gamestop/game-view.fxml"
                            )
                    );


            Scene gameScene =
                    new Scene(
                            loader.load()
                    );


            Stage stage =
                    (Stage)
                            consoleTable
                                    .getScene()
                                    .getWindow();


            stage.setScene(
                    gameScene
            );

            stage.setTitle(
                    "GameStop Manager"
            );


        } catch (IOException e) {

            showError(
                    "Greška pri otvaranju igara."
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
                            consoleTable
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