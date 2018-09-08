
package org.fwwb.convene.convenecode.BeenClass;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Levelid {

    @SerializedName("level2")
    @Expose
    private String level2;
    @SerializedName("level4")
    @Expose
    private String level4;
    @SerializedName("level6")
    @Expose
    private String level6;
    @SerializedName("level7")
    @Expose
    private String level7;
    @SerializedName("level3")
    @Expose
    private String level3;
    @SerializedName("level5")
    @Expose
    private String level5;

    public String getLevel2() {
        return level2;
    }

    public void setLevel2(String level2) {
        this.level2 = level2;
    }

    public String getLevel4() {
        return level4;
    }

    public void setLevel4(String level4) {
        this.level4 = level4;
    }

    public String getLevel6() {
        return level6;
    }

    public void setLevel6(String level6) {
        this.level6 = level6;
    }

    public String getLevel7() {
        return level7;
    }

    public void setLevel7(String level7) {
        this.level7 = level7;
    }

    public String getLevel3() {
        return level3;
    }

    public void setLevel3(String level3) {
        this.level3 = level3;
    }

    public String getLevel5() {
        return level5;
    }

    public void setLevel5(String level5) {
        this.level5 = level5;
    }

}
