package de.androidmika.pommesmann.ShopDatabase;

public class Item {
    private String name;
    private int level;
    private int price;


    public Item() {
        this.name = null;
    }

    public Item(String name, int level) {
        this.name = name;
        this.level = level;
    }

    public Item(String name, int level, int price) {
        this.name = name;
        this.level = level;
        this.price = price;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() { return name; }

    public void setLevel(int level) {
        this.level = level;
    }
    public int getLevel() { return level; }

    public void setPrice(int price) { this.price = price; }
    public int getPrice() { return price; }
}
