
package org.fwwb.convene.convenecode.BeenClass.parentChild;

import android.os.Parcel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Level7 extends Level6 {


    @SerializedName("level7_id")
    @Expose
    private int level7Id;



    /**
     * 
     * @return
     *     The level7Id
     */
    public int getLevel7Id() {
        return level7Id;
    }

    /**
     * 
     * @param level7Id
     *     The level7_id
     */
    public void setLevel7Id(int level7Id) {
        this.level7Id = level7Id;
    }

    public static final Creator<Level7> CREATOR = new Creator<Level7>()
    {
        public Level7 createFromParcel(Parcel in) {
            return new Level7(in);
        }

        public Level7[] newArray(int size) {
            return new Level7[size];
        }
    };
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeInt(level7Id);
    }

    public Level7(Parcel in) {
        super(in);
        level7Id = in.readInt();
    }
}
