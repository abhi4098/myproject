
package com.a83idea.cobaltconnect.generated.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MyCloverProduct {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("tokenid")
    @Expose
    private String tokenid;



    public MyCloverProduct(String tokenid, String type)
    {
       this.tokenid = tokenid;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getTokenid() {
        return tokenid;
    }

    public void setTokenid(String tokenid) {
        this.tokenid = tokenid;
    }

}
