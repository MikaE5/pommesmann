package de.androidnewcomer.pommesmann.ShopDatabase;

public class Item {
    private String name;
    private int level;


    public Item () {};

    public Item(String name, int level) {
        this.name = name;
        this.level = level;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() { return name; }

    public void setLevel(int level) {
        this.level = level;
    }
    public int getLevel() { return level; }

}
