package org.mahiti.convenemis.BeenClass.regionallanguage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetLanguageBlock {

@SerializedName("status")
@Expose
private Integer status;
@SerializedName("LanguageBlock")
@Expose
private List<LanguageBlock> languageBlock = null;
@SerializedName("message")
@Expose
private String message;

public Integer getStatus() {
return status;
}

public void setStatus(Integer status) {
this.status = status;
}

public List<LanguageBlock> getLanguageBlock() {
return languageBlock;
}

public void setLanguageBlock(List<LanguageBlock> languageBlock) {
this.languageBlock = languageBlock;
}

public String getMessage() {
return message;
}

public void setMessage(String message) {
this.message = message;
}

}