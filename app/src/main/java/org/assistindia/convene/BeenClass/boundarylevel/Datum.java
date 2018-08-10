package org.assistindia.convene.BeenClass.boundarylevel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Datum {

@SerializedName("parent_id")
@Expose
private Integer parentId;
@SerializedName("child")
@Expose
private List<Object> child = null;
@SerializedName("id")
@Expose
private Integer id;
@SerializedName("name")
@Expose
private String name;

public Integer getParentId() {
return parentId;
}

public void setParentId(Integer parentId) {
this.parentId = parentId;
}

public List<Object> getChild() {
return child;
}

public void setChild(List<Object> child) {
this.child = child;
}

public Integer getId() {
return id;
}

public void setId(Integer id) {
this.id = id;
}

public String getName() {
return name;
}

public void setName(String name) {
this.name = name;
}

}