package hr.gamestop.repository;

import hr.gamestop.data.GameStopData;
import hr.gamestop.model.Console;
import hr.gamestop.model.Customer;
import hr.gamestop.model.Game;
import hr.gamestop.model.Order;

import java.io.IOException;
import java.util.ArrayList;

public class GameStopRepository {

    private final JsonRepository<Game> gameRepository =
            new JsonRepository<>(Game.class, "games.json");

    private final JsonRepository<Console> consoleRepository =
            new JsonRepository<>(Console.class, "consoles.json");

    private final JsonRepository<Customer> customerRepository =
            new JsonRepository<>(Customer.class, "customers.json");

    private final JsonRepository<Order> orderRepository =
            new JsonRepository<>(Order.class, "orders.json");


    public void saveAll(GameStopData data) throws IOException {

        gameRepository.save(data.getGames());
        consoleRepository.save(data.getConsoles());
        customerRepository.save(data.getCustomers());
        orderRepository.save(data.getOrders());
    }


    public ArrayList<Game> loadGames() throws IOException {
        return gameRepository.load();
    }

    public ArrayList<Console> loadConsoles() throws IOException {
        return consoleRepository.load();
    }

    public ArrayList<Customer> loadCustomers() throws IOException {
        return customerRepository.load();
    }

    public ArrayList<Order> loadOrders() throws IOException {
        return orderRepository.load();
    }


    public GameStopData loadAll() throws IOException {

        ArrayList<Game> games = loadGames();
        ArrayList<Console> consoles = loadConsoles();
        ArrayList<Customer> customers = loadCustomers();
        ArrayList<Order> orders = loadOrders();

        return new GameStopData(
                games,
                consoles,
                customers,
                orders
        );
    }
}