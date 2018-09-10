package org.fwwb.convene.convenecode.BeenClass.beneficiaryTypes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

@SerializedName("parent_id")
@Expose
private Integer parentId;
@SerializedName("name")
@Expose
private String name;
@SerializedName("parent")
@Expose
private String parent;
@SerializedName("created")
@Expose
private String created;
@SerializedName("active")
@Expose
private Integer active;
@SerializedName("id")
@Expose
private Integer id;
@SerializedName("is_main")
@Expose
private Boolean isMain;
@SerializedName("modified")
@Expose
private String modified;
@SerializedName("order")
@Expose
private Integer order;

public Integer getParentId() {
return parentId;
}

public void setParentId(Integer parentId) {
this.parentId = parentId;
}

public String getName() {
return name;
}

public void setName(String name) {
this.name = name;
}

public String getParent() {
return parent;
}

public void setParent(String parent) {
this.parent = parent;
}

public String getCreated() {
return created;
}

public void setCreated(String created) {
this.created = created;
}

public Integer getActive() {
return active;
}

public void setActive(Integer active) {
this.active = active;
}

public Integer getId() {
return id;
}

public void setId(Integer id) {
this.id = id;
}

public Boolean getIsMain() {
return isMain;
}

public void setIsMain(Boolean isMain) {
this.isMain = isMain;
}

public String getModified() {
return modified;
}

public void setModified(String modified) {
this.modified = modified;
}

public Integer getOrder() {
return order;
}

public void setOrder(Integer order) {
this.order = order;
}

}