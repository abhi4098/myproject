
package com.a83idea.cobaltconnect.generated.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MyCloverProductResponse {

    @SerializedName("type")
    @Expose
    private Integer type;
    @SerializedName("lastFetch")
    @Expose
    private String lastFetch;
    @SerializedName("inventory")
    @Expose
    private List<Inventory> inventory = null;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public List<Inventory> getInventory() {
        return inventory;
    }

    public void setInventory(List<Inventory> inventory) {
        this.inventory = inventory;
    }

    public String getLastFetch() {
        return lastFetch;
    }

    public void setLastFetch(String lastFetch) {
        this.lastFetch = lastFetch;
    }

}
