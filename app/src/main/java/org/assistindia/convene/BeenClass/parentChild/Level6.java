
package org.assistindia.convene.BeenClass.parentChild;

import android.os.Parcel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Level6 extends Level5 {


    @SerializedName("level6_id")
    @Expose
    private int level6Id;

    /**
     * 
     * @return
     *     The level6Id
     */
    public int getLevel6Id() {
        return level6Id;
    }

    /**
     * 
     * @param level6Id
     *     The level6_id
     */
    public void setLevel6Id(int level6Id) {
        this.level6Id = level6Id;
    }
    public static final Creator<Level6> CREATOR = new Creator<Level6>()
    {
        public Level6 createFromParcel(Parcel in) {
            return new Level6(in);
        }

        public Level6[] newArray(int size) {
            return new Level6[size];
        }
    };
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeInt(level6Id);
    }

    public Level6(Parcel in) {
        super(in);
        level6Id = in.readInt();
    }

}
