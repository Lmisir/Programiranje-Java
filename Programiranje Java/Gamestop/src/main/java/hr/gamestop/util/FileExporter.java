package hr.gamestop.util;

import hr.gamestop.data.GameStopData;
import hr.gamestop.model.Game;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileExporter {

    public static void exportGames(GameStopData data, String fileName)
            throws IOException {

        Path path = Path.of("export", fileName);

        Files.createDirectories(path.getParent());

        try (BufferedWriter writer = Files.newBufferedWriter(path)) {

            writer.write("GAMESTOP - POPIS IGARA");
            writer.newLine();
            writer.write("======================");
            writer.newLine();

            for (Game game : data.getGames()) {

                writer.write(
                        game.getId()
                                + " | "
                                + game.getName()
                                + " | "
                                + game.getPlatform()
                                + " | "
                                + game.getPrice()
                                + " EUR | Stock: "
                                + game.getStock()
                );

                writer.newLine();
            }
        }
    }
}