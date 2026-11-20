package hr.gamestop.model;

public class Game extends Product {

    private String platform;
    private String genre;
    private int pegi;

    public Game(int id, String name, double price, int stock,
                String platform, String genre, int pegi) {

        super(id, name, price, stock);

        this.platform = platform;
        this.genre = genre;
        this.pegi = pegi;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getPegi() {
        return pegi;
    }

    public void setPegi(int pegi) {
        this.pegi = pegi;
    }

    @Override
    public String getType() {
        return "Game";
    }
}