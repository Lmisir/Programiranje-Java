package hr.gamestop.data;

import hr.gamestop.model.Console;
import hr.gamestop.model.Customer;
import hr.gamestop.model.Game;
import hr.gamestop.model.Order;
import hr.gamestop.model.Product;
import hr.gamestop.util.RandomDataGenerator;

import java.util.ArrayList;

public class GameStopData {

    private ArrayList<Game> games;
    private ArrayList<Console> consoles;
    private ArrayList<Customer> customers;
    private ArrayList<Order> orders;


    // Konstruktor koji generira nasumicne pocetne podatke
    public GameStopData() {

        games = RandomDataGenerator.generateGames(10);
        consoles = RandomDataGenerator.generateConsoles(5);
        customers = RandomDataGenerator.generateCustomers(10);

        ArrayList<Product> products = new ArrayList<>();

        products.addAll(games);
        products.addAll(consoles);

        orders = RandomDataGenerator.generateOrders(
                10,
                customers,
                products
        );
    }


    // Konstruktor za podatke ucitane iz JSON datoteka
    public GameStopData(
            ArrayList<Game> games,
            ArrayList<Console> consoles,
            ArrayList<Customer> customers,
            ArrayList<Order> orders) {

        this.games = games;
        this.consoles = consoles;
        this.customers = customers;
        this.orders = orders;
    }


    public ArrayList<Game> getGames() {
        return games;
    }

    public ArrayList<Console> getConsoles() {
        return consoles;
    }

    public ArrayList<Customer> getCustomers() {
        return customers;
    }

    public ArrayList<Order> getOrders() {
        return orders;
    }
}