
package org.mahiti.convenemis.BeenClass.facilityServiceList;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum implements Parcelable
{

    @SerializedName("service_subtype_id")
    @Expose
    private Integer serviceSubtypeId;
    @SerializedName("beneficiary_id")
    @Expose
    private Object beneficiaryId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("thematic_area_id")
    @Expose
    private Integer thematicAreaId;
    @SerializedName("service_type_id")
    @Expose
    private Integer serviceTypeId;
    @SerializedName("modified")
    @Expose
    private String modified;
    @SerializedName("parent_id")
    @Expose
    private Object parentId;
    @SerializedName("active")
    @Expose
    private Integer active;
    @SerializedName("partner_id")
    @Expose
    private Object partnerId;
    @SerializedName("id")
    @Expose
    private Integer id;
    public final static Creator<Datum> CREATOR = new Creator<Datum>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Datum createFromParcel(Parcel in) {
            Datum instance = new Datum();
            instance.serviceSubtypeId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.beneficiaryId = in.readValue((Object.class.getClassLoader()));
            instance.name = ((String) in.readValue((String.class.getClassLoader())));
            instance.created = ((String) in.readValue((String.class.getClassLoader())));
            instance.thematicAreaId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.serviceTypeId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.modified = ((String) in.readValue((String.class.getClassLoader())));
            instance.parentId = in.readValue((Object.class.getClassLoader()));
            instance.active = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.partnerId = in.readValue((Object.class.getClassLoader()));
            instance.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
            return instance;
        }

        public Datum[] newArray(int size) {
            return (new Datum[size]);
        }

    }
    ;

    public String getName() {
        return name;
    }

    public Integer getActive() {
        return active;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(serviceSubtypeId);
        dest.writeValue(beneficiaryId);
        dest.writeValue(name);
        dest.writeValue(created);
        dest.writeValue(thematicAreaId);
        dest.writeValue(serviceTypeId);
        dest.writeValue(modified);
        dest.writeValue(parentId);
        dest.writeValue(active);
        dest.writeValue(partnerId);
        dest.writeValue(id);
    }

    public int describeContents() {
        return  0;
    }

}
