package com.pundroid.shophelper.model;

/**
 * Created by pumba30 on 02.06.2016.
 */
public class ShoppingListItem {
    private String owner;
    private String name;
    private String boughtBy;
    private boolean hasBought;

    public ShoppingListItem() {
    }


    public ShoppingListItem(String name, String userOwner) {
        this.name = name;
        this.owner = userOwner;
        this.boughtBy = null;
        this.hasBought = false;
    }


    public String getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public boolean isHasBought() {
        return hasBought;
    }

    public String getBoughtBy() {
        return boughtBy;
    }
}
