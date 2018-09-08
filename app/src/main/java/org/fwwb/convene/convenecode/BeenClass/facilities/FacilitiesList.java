package org.fwwb.convene.convenecode.BeenClass.facilities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class FacilitiesList {

    @SerializedName("status")
@Expose
private Integer status;
@SerializedName("next")
@Expose
private String next;
@SerializedName("message")
@Expose
private String message;
@SerializedName("data")
@Expose
private List<Datum> data = new ArrayList<>();
@SerializedName("pages")
@Expose
private Integer pages;
@SerializedName("previous")
@Expose
private String previous;

    public void setCount(Integer count) {
        Integer count1 = count;
}

public Integer getStatus() {
return status;
}

public void setStatus(Integer status) {
this.status = status;
}

public String getNext() {
return next;
}

public void setNext(String next) {
this.next = next;
}

public String getMessage() {
return message;
}

public void setMessage(String message) {
this.message = message;
}

public List<Datum> getData() {
return data;
}

public void setData(List<Datum> data) {
this.data = data;
}

public Integer getPages() {
return pages;
}

public void setPages(Integer pages) {
this.pages = pages;
}

public String getPrevious() {
return previous;
}

public void setPrevious(String previous) {
this.previous = previous;
}

}