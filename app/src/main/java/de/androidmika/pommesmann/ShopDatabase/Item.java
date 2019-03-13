package de.androidmika.pommesmann.ShopDatabase;

public class Item {
    private String name;
    private String description;
    private int level;
    private boolean active;
    private int restriction;
    private int price;
    private int resource;


    public Item() {
        this.name = null;
    }

    public Item(String name, String description, int level, int restriction, int price, int resource) {
        this.name = name;
        this.description = description;
        this.level = level;
        this.active = true;
        this.restriction = restriction;
        this.price = price;
        this.resource = resource;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() { return name; }

    public void setDescription(String description) {this.description = description; }
    public String getDescription() { return description;}

    public void setLevel(int level) {
        this.level = level;
    }
    public int getLevel() { return level; }

    public void setActive(boolean isActive) { this.active = isActive; }
    public boolean getActive() { return active; }

    public void setRestriction(int restriction) { this.restriction = restriction; }
    public int getRestriction() { return restriction; }

    public void setPrice(int price) { this.price = price; }
    public int getPrice() { return price; }

    public void setResource(int resource) { this.resource = resource; }
    public int getResource() { return resource; }
}
