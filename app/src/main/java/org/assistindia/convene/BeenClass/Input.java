
package org.assistindia.convene.BeenClass;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Input {

    @SerializedName("type")
    @Expose
    private Integer type;
    @SerializedName("placeholder")
    @Expose
    private String placeholder;
    @SerializedName("Option")
    @Expose
    private List<Option> option = null;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public List<Option> getOption() {
        return option;
    }

    public void setOption(List<Option> option) {
        this.option = option;
    }

}
