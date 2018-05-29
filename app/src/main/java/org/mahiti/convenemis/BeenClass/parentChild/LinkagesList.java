package org.mahiti.convenemis.BeenClass.parentChild;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mahiti on 29/05/18.
 */

public class LinkagesList implements Parcelable {
    int relation_id;
    String uuid;
    String name;
    int form_type_id;

    public LinkagesList() {
    }

    protected LinkagesList(Parcel in) {
        relation_id = in.readInt();
        uuid = in.readString();
        name = in.readString();
        form_type_id = in.readInt();
    }

    public static final Creator<LinkagesList> CREATOR = new Creator<LinkagesList>() {
        @Override
        public LinkagesList createFromParcel(Parcel in) {
            return new LinkagesList(in);
        }

        @Override
        public LinkagesList[] newArray(int size) {
            return new LinkagesList[size];
        }
    };

    public int getRelation_id() {
        return relation_id;
    }

    public void setRelation_id(int relation_id) {
        this.relation_id = relation_id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getForm_type_id() {
        return form_type_id;
    }

    public void setForm_type_id(int form_type_id) {
        this.form_type_id = form_type_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(relation_id);
        parcel.writeString(uuid);
        parcel.writeString(name);
        parcel.writeInt(form_type_id);
    }
}
