
package org.yale.convene.BeenClass.parentChild;

import android.os.Parcel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Level2 extends Level1
{

    @SerializedName("level2_id")
    @Expose
    private int level2Id;



    /**
     * 
     * @return
     *     The level2Id
     */
    public int getLevel2Id() {
        return level2Id;
    }

    /**
     * 
     * @param level2Id
     *     The level2_id
     */
    public void setLevel2Id(int level2Id) {
        this.level2Id = level2Id;
    }



    public static final Creator<Level2> CREATOR = new Creator<Level2>()
    {
        public Level2 createFromParcel(Parcel in) {
            return new Level2(in);
        }

        public Level2[] newArray(int size) {
            return new Level2[size];
        }
    };
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeInt(level2Id);

    }

    public Level2(Parcel in) {
        super(in);
        level2Id = in.readInt();

    }


}
