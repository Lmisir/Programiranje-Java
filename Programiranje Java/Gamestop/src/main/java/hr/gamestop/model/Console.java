package hr.gamestop.model;

public class Console extends Product {

    private String manufacturer;
    private int storageGb;

    public Console(int id, String name, double price, int stock,
                   String manufacturer, int storageGb) {

        super(id, name, price, stock);

        this.manufacturer = manufacturer;
        this.storageGb = storageGb;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public int getStorageGb() {
        return storageGb;
    }

    public void setStorageGb(int storageGb) {
        this.storageGb = storageGb;
    }

    @Override
    public String getType() {
        return "Console";
    }
}