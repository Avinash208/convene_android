
package org.mahiti.convenemis.BeenClass.parentChild;

import android.os.Parcel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Level3 extends Level2 {


    @SerializedName("level3_id")
    @Expose
    private int level3Id;




    public static final Creator<Level3> CREATOR = new Creator<Level3>()
    {
        public Level3 createFromParcel(Parcel in) {
            return new Level3(in);
        }

        public Level3[] newArray(int size) {
            return new Level3[size];
        }
    };
    public int describeContents() {
        return 0;
    }




    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeInt(level3Id);
    }

    public Level3(Parcel in) {
        super(in);
        level3Id = in.readInt();

    }


    /**
     * 
     * @return
     *     The level3Id
     */
    public int getLevel3Id() {
        return level3Id;
    }

    /**
     * 
     * @param level3Id
     *     The level3_id
     */
    public void setLevel3Id(int level3Id) {
        this.level3Id = level3Id;
    }

    /*

      @return
     *     The level1Id
     */

}
