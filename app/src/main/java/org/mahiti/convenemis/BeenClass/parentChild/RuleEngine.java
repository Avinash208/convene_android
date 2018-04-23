package org.mahiti.convenemis.BeenClass.parentChild;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class RuleEngine implements Parcelable {

@SerializedName("rules")
@Expose
private List<Rule> rules = new ArrayList<>();

    @SerializedName("operation")
    @Expose
    private String operation;

    public RuleEngine() {
    }

    protected RuleEngine(Parcel in) {
        rules = in.createTypedArrayList(Rule.CREATOR);
        operation = in.readString();
    }

    public static final Creator<RuleEngine> CREATOR = new Creator<RuleEngine>() {
        @Override
        public RuleEngine createFromParcel(Parcel in) {
            return new RuleEngine(in);
        }

        @Override
        public RuleEngine[] newArray(int size) {
            return new RuleEngine[size];
        }
    };

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public List<Rule> getRules() {
return rules;
}

public void setRules(List<Rule> rules) {
this.rules = rules;
}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(rules);
        parcel.writeString(operation);
    }
}
