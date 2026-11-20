package hr.gamestop.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class JsonRepository<T> {

    private final Gson gson;
    private final Class<T> type;
    private final Path filePath;

    public JsonRepository(Class<T> type, String fileName) {

        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        this.type = type;
        this.filePath = Path.of("data", fileName);
    }

    public void save(List<T> data) throws IOException {

        Files.createDirectories(filePath.getParent());

        try (Writer writer = Files.newBufferedWriter(filePath)) {
            gson.toJson(data, writer);
        }
    }

    public ArrayList<T> load() throws IOException {

        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }

        Type listType = TypeToken
                .getParameterized(List.class, type)
                .getType();

        try (Reader reader = Files.newBufferedReader(filePath)) {

            List<T> data = gson.fromJson(reader, listType);

            if (data == null) {
                return new ArrayList<>();
            }

            return new ArrayList<>(data);
        }
    }
}