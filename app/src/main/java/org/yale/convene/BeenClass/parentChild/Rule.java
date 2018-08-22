package org.yale.convene.BeenClass.parentChild;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Rule implements Parcelable{

@SerializedName("type")
@Expose
private String type;
@SerializedName("data_type")
@Expose
private String dataType;
@SerializedName("Reference_type")
@Expose
private String referenceType;
@SerializedName("Operation")
@Expose
private String operation;
@SerializedName("value")
@Expose
private String value;

    public Rule() {
    }

    protected Rule(Parcel in) {
        type = in.readString();
        dataType = in.readString();
        referenceType = in.readString();
        operation = in.readString();
        value = in.readString();
    }

    public static final Creator<Rule> CREATOR = new Creator<Rule>() {
        @Override
        public Rule createFromParcel(Parcel in) {
            return new Rule(in);
        }

        @Override
        public Rule[] newArray(int size) {
            return new Rule[size];
        }
    };

    public String getType() {
return type;
}

public void setType(String type) {
this.type = type;
}

public String getDataType() {
return dataType;
}

public void setDataType(String dataType) {
this.dataType = dataType;
}

public String getReferenceType() {
return referenceType;
}

public void setReferenceType(String referenceType) {
this.referenceType = referenceType;
}

public String getOperation() {
return operation;
}

public void setOperation(String operation) {
this.operation = operation;
}

public String getValue() {
return value;
}

public void setValue(String value) {
this.value = value;
}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(type);
        parcel.writeString(dataType);
        parcel.writeString(referenceType);
        parcel.writeString(operation);
        parcel.writeString(value);
    }
}
