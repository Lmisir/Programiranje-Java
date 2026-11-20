package hr.gamestop.service;

import hr.gamestop.data.GameStopData;
import hr.gamestop.model.Console;
import hr.gamestop.model.Game;

import java.util.List;

public class GameStopService {

    private final GameStopData data;


    public GameStopService(GameStopData data) {
        this.data = data;
    }


    // =====================================================
    // GAME
    // =====================================================


    // READ - dohvati sve igre
    public List<Game> getAllGames() {
        return data.getGames();
    }


    // READ - pronađi igru po ID-u
    public Game getGameById(int id) {

        return data.getGames()
                .stream()
                .filter(game -> game.getId() == id)
                .findFirst()
                .orElse(null);
    }


    // CREATE
    public boolean addGame(Game game) {

        boolean alreadyExists = data.getGames()
                .stream()
                .anyMatch(existingGame ->
                        existingGame.getName()
                                .equalsIgnoreCase(game.getName()));

        if (alreadyExists) {
            return false;
        }

        data.getGames().add(game);

        return true;
    }


    // UPDATE
    public boolean updateGame(
            int id,
            String name,
            double price,
            int stock,
            String platform,
            String genre,
            int pegi) {

        Game game = getGameById(id);

        if (game == null) {
            return false;
        }


        boolean duplicateName = data.getGames()
                .stream()
                .anyMatch(existingGame ->
                        existingGame.getId() != id
                                && existingGame.getName()
                                .equalsIgnoreCase(name));

        if (duplicateName) {
            return false;
        }


        game.setName(name);
        game.setPrice(price);
        game.setStock(stock);
        game.setPlatform(platform);
        game.setGenre(genre);
        game.setPegi(pegi);

        return true;
    }


    // DELETE
    public boolean deleteGame(int id) {

        return data.getGames()
                .removeIf(game -> game.getId() == id);
    }


    // Generira sljedeći slobodan ID
    public int getNextGameId() {

        return data.getGames()
                .stream()
                .mapToInt(Game::getId)
                .max()
                .orElse(0) + 1;
    }


    // FILTER - igre jeftinije od zadane cijene
    public List<Game> getGamesCheaperThan(double maxPrice) {

        return data.getGames()
                .stream()
                .filter(game ->
                        game.getPrice() <= maxPrice)
                .toList();
    }


    // FILTER - igre prema platformi
    public List<Game> getGamesByPlatform(String platform) {

        return data.getGames()
                .stream()
                .filter(game ->
                        game.getPlatform()
                                .equalsIgnoreCase(platform))
                .toList();
    }


    // FILTER - pretraga prema nazivu
    public List<Game> searchGamesByName(String text) {

        return data.getGames()
                .stream()
                .filter(game ->
                        game.getName()
                                .toLowerCase()
                                .contains(text.toLowerCase()))
                .toList();
    }


    // =====================================================
    // CONSOLE
    // =====================================================


    // READ - dohvati sve konzole
    public List<Console> getAllConsoles() {

        return data.getConsoles();
    }


    // READ - pronađi konzolu po ID-u
    public Console getConsoleById(int id) {

        return data.getConsoles()
                .stream()
                .filter(console ->
                        console.getId() == id)
                .findFirst()
                .orElse(null);
    }


    // CREATE
    public boolean addConsole(Console console) {

        boolean alreadyExists = data.getConsoles()
                .stream()
                .anyMatch(existingConsole ->
                        existingConsole.getName()
                                .equalsIgnoreCase(
                                        console.getName()
                                )
                );


        if (alreadyExists) {
            return false;
        }


        data.getConsoles().add(console);

        return true;
    }


    // UPDATE
    public boolean updateConsole(
            int id,
            String name,
            double price,
            int stock,
            String manufacturer,
            int storageGb) {


        Console console =
                getConsoleById(id);


        if (console == null) {
            return false;
        }


        boolean duplicateName =
                data.getConsoles()
                        .stream()
                        .anyMatch(existingConsole ->

                                existingConsole.getId() != id

                                        && existingConsole
                                        .getName()
                                        .equalsIgnoreCase(name)
                        );


        if (duplicateName) {
            return false;
        }


        console.setName(name);

        console.setPrice(price);

        console.setStock(stock);

        console.setManufacturer(
                manufacturer
        );

        console.setStorageGb(
                storageGb
        );


        return true;
    }


    // DELETE
    public boolean deleteConsole(int id) {

        return data.getConsoles()
                .removeIf(console ->
                        console.getId() == id);
    }


    // Generira sljedeći slobodan ID konzole
    public int getNextConsoleId() {

        return data.getConsoles()
                .stream()
                .mapToInt(Console::getId)
                .max()
                .orElse(1000) + 1;
    }


    // FILTER - pretraga konzola prema nazivu
    public List<Console> searchConsolesByName(
            String text) {

        return data.getConsoles()
                .stream()
                .filter(console ->
                        console.getName()
                                .toLowerCase()
                                .contains(
                                        text.toLowerCase()
                                )
                )
                .toList();
    }
}