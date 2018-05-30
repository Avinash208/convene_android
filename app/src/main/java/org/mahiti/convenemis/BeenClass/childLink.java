
package org.mahiti.convenemis.BeenClass;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class childLink implements Parcelable {

    String child_form_id;
    int child_form_type;

    public childLink() {
    }

    protected childLink(Parcel in) {
        child_form_id = in.readString();
        child_form_type = in.readInt();
    }

    public static final Creator<childLink> CREATOR = new Creator<childLink>() {
        @Override
        public childLink createFromParcel(Parcel in) {
            return new childLink(in);
        }

        @Override
        public childLink[] newArray(int size) {
            return new childLink[size];
        }
    };

    public String getChild_form_id() {
        return child_form_id;
    }

    public void setChild_form_id(String child_form_id) {
        this.child_form_id = child_form_id;
    }

    public int getChild_form_type() {
        return child_form_type;
    }

    public void setChild_form_type(int child_form_type) {
        this.child_form_type = child_form_type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(child_form_id);
        parcel.writeInt(child_form_type);
    }
}
