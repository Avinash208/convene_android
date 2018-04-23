package org.mahiti.convenemis.BeenClass.regionallanguage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LanguageBlock {

@SerializedName("language_id")
@Expose
private Integer languageId;
@SerializedName("extra_column1")
@Expose
private String extraColumn1;
@SerializedName("extra_column2")
@Expose
private Integer extraColumn2;
@SerializedName("updated_time")
@Expose
private String updatedTime;
@SerializedName("block_name")
@Expose
private String blockName;
@SerializedName("block_pid")
@Expose
private Integer blockPid;
@SerializedName("id")
@Expose
private Integer id;

public Integer getLanguageId() {
return languageId;
}

public void setLanguageId(Integer languageId) {
this.languageId = languageId;
}

public String getExtraColumn1() {
return extraColumn1;
}

public void setExtraColumn1(String extraColumn1) {
this.extraColumn1 = extraColumn1;
}

public Integer getExtraColumn2() {
return extraColumn2;
}

public void setExtraColumn2(Integer extraColumn2) {
this.extraColumn2 = extraColumn2;
}

public String getUpdatedTime() {
return updatedTime;
}

public void setUpdatedTime(String updatedTime) {
this.updatedTime = updatedTime;
}

public String getBlockName() {
return blockName;
}

public void setBlockName(String blockName) {
this.blockName = blockName;
}

public Integer getBlockPid() {
return blockPid;
}

public void setBlockPid(Integer blockPid) {
this.blockPid = blockPid;
}

public Integer getId() {
return id;
}

public void setId(Integer id) {
this.id = id;
}

}