package hr.gamestop.util;

import hr.gamestop.model.Console;
import hr.gamestop.model.Customer;
import hr.gamestop.model.Game;
import hr.gamestop.model.Order;
import hr.gamestop.model.Product;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RandomDataGenerator {

    private static final Random random = new Random();

    private static final String[] GAME_NAMES = {
            "God of War Ragnarok",
            "Marvel's Spider-Man 2",
            "GTA V",
            "Minecraft",
            "Cyberpunk 2077",
            "Red Dead Redemption 2",
            "Hogwarts Legacy",
            "The Witcher 3",
            "EA Sports FC 26",
            "Resident Evil 4"
    };

    private static final String[] PLATFORMS = {
            "PS5",
            "PS4",
            "PC",
            "Xbox Series X"
    };

    private static final String[] GENRES = {
            "Action",
            "Adventure",
            "RPG",
            "Sports",
            "Horror"
    };

    private static final String[] CONSOLE_NAMES = {
            "PlayStation 5",
            "PlayStation 5 Pro",
            "Xbox Series X",
            "Xbox Series S",
            "Nintendo Switch"
    };

    private static final String[] MANUFACTURERS = {
            "Sony",
            "Microsoft",
            "Nintendo"
    };

    private static final String[] FIRST_NAMES = {
            "Ivan",
            "Marko",
            "Petar",
            "Luka",
            "Ana",
            "Maja",
            "Ivana",
            "Sara"
    };

    private static final String[] LAST_NAMES = {
            "Horvat",
            "Novak",
            "Kovac",
            "Babic",
            "Maric",
            "Juric"
    };


    // IGRE - BEZ PONAVLJANJA
    public static ArrayList<Game> generateGames(int count) {

        ArrayList<Game> games = new ArrayList<>();

        ArrayList<String> availableNames =
                new ArrayList<>(Arrays.asList(GAME_NAMES));

        Collections.shuffle(availableNames);

        int numberOfGames = Math.min(count, availableNames.size());

        for (int i = 1; i <= numberOfGames; i++) {

            String name = availableNames.get(i - 1);

            String platform =
                    PLATFORMS[random.nextInt(PLATFORMS.length)];

            String genre =
                    GENRES[random.nextInt(GENRES.length)];

            double price = 20 + random.nextDouble() * 60;
            price = Math.round(price * 100.0) / 100.0;

            int stock = random.nextInt(30) + 1;

            int[] pegiValues = {3, 7, 12, 16, 18};

            int pegi =
                    pegiValues[random.nextInt(pegiValues.length)];

            Game game = new Game(
                    i,
                    name,
                    price,
                    stock,
                    platform,
                    genre,
                    pegi
            );

            games.add(game);
        }

        return games;
    }


    public static ArrayList<Console> generateConsoles(int count) {

        ArrayList<Console> consoles = new ArrayList<>();

        for (int i = 1; i <= count; i++) {

            String name =
                    CONSOLE_NAMES[random.nextInt(CONSOLE_NAMES.length)];

            String manufacturer =
                    MANUFACTURERS[random.nextInt(MANUFACTURERS.length)];

            double price = 250 + random.nextDouble() * 500;
            price = Math.round(price * 100.0) / 100.0;

            int stock = random.nextInt(15) + 1;

            int[] storageValues = {512, 825, 1000, 2000};

            int storage =
                    storageValues[random.nextInt(storageValues.length)];

            Console console = new Console(
                    1000 + i,
                    name,
                    price,
                    stock,
                    manufacturer,
                    storage
            );

            consoles.add(console);
        }

        return consoles;
    }


    public static ArrayList<Customer> generateCustomers(int count) {

        ArrayList<Customer> customers = new ArrayList<>();

        for (int i = 1; i <= count; i++) {

            String firstName =
                    FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];

            String lastName =
                    LAST_NAMES[random.nextInt(LAST_NAMES.length)];

            String email =
                    firstName.toLowerCase()
                            + "."
                            + lastName.toLowerCase()
                            + i
                            + "@gmail.com";

            Customer customer = new Customer(
                    i,
                    firstName,
                    lastName,
                    email
            );

            customers.add(customer);
        }

        return customers;
    }


    public static ArrayList<Order> generateOrders(
            int count,
            List<Customer> customers,
            List<Product> products) {

        ArrayList<Order> orders = new ArrayList<>();

        for (int i = 1; i <= count; i++) {

            Customer customer =
                    customers.get(random.nextInt(customers.size()));

            Product product =
                    products.get(random.nextInt(products.size()));

            int quantity = random.nextInt(3) + 1;

            double totalPrice =
                    product.getPrice() * quantity;

            totalPrice =
                    Math.round(totalPrice * 100.0) / 100.0;

            LocalDate date =
                    LocalDate.now().minusDays(random.nextInt(60));

            Order order = new Order(
                    i,
                    customer.getId(),
                    product.getId(),
                    quantity,
                    totalPrice,
                    date.toString()
            );

            orders.add(order);
        }

        return orders;
    }
}