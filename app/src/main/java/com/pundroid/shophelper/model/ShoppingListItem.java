package com.pundroid.shophelper.model;

/**
 * Created by pumba30 on 02.06.2016.
 */
public class ShoppingListItem {
    private String owner;
    private String name;

    public ShoppingListItem() {
    }


    public ShoppingListItem(String name) {
        this.name = name;
        this.owner = "Vadion";
    }


    public String getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }
}
