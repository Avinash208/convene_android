
package org.assistindia.convene.BeenClass.parentChild;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Level1 implements Parcelable
{

    @SerializedName("level1_id")
    @Expose
    private int level1Id;

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("modified_date")
    @Expose
    private String modifiedDate;
    @SerializedName("location_type")
    @Expose
    private String locationType;
    @SerializedName("active")
    @Expose
    private int active;
    @SerializedName("name")
    @Expose
    private String name;
    public Level1(Parcel in) {
        level1Id = in.readInt();
        id = in.readInt();
        modifiedDate = in.readString();
        active = in.readInt();
        name = in.readString();
        locationType=in.readString();

    }
    public Level1(int level1Ids, int id, String mdDate, int act, String name) {
        this.level1Id = level1Ids;
        this.id = id;
        this.modifiedDate = mdDate;
        this.active = act;
        this.name =name;


    }



    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    /**
     * 
     * @return
     *     The level1Id
     */
    public int getLevel1Id() {
        return level1Id;
    }

    /**
     * 
     * @param level1Id
     *     The level1_id
     */
    public void setLevel1Id(int level1Id) {
        this.level1Id = level1Id;
    }

    /**
     * 
     * @return
     *     The modifiedDate
     */
    public String getModifiedDate() {
        return modifiedDate;
    }

    /**
     * 
     * @param modifiedDate
     *     The modified_date
     */
    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    /**
     * 
     * @return
     *     The active
     */
    public int getActive() {
        return active;
    }

    /**
     * 
     * @param active
     *     The active
     */
    public void setActive(int active) {
        this.active = active;
    }

    /**
     * 
     * @return
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }
    public static final Creator<Level1> CREATOR = new Creator<Level1>() {
        public Level1 createFromParcel(Parcel in) {
            return new Level1(in);
        }

        public Level1[] newArray(int size) {
            return new Level1[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(level1Id);
        dest.writeInt(id);
        dest.writeString(modifiedDate);
        dest.writeInt(active);
        dest.writeString(name);
        dest.writeString(locationType);


    }
    public String toString(){
        return name;
    }
}
