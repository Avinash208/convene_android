
package org.assistindia.convene.BeenClass.regionallanguage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class GetLanguageQuestions {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("LanguageQuestion")
    @Expose
    private List<LanguageQuestion> languageQuestion = new ArrayList<>();

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<LanguageQuestion> getLanguageQuestion() {
        return languageQuestion;
    }

    public void setLanguageQuestion(List<LanguageQuestion> languageQuestion) {
        this.languageQuestion = languageQuestion;
    }

}
