
package org.assistindia.convene.BeenClass.parentChild;

import android.os.Parcel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Level5 extends Level4
{

    @SerializedName("level5_id")
    @Expose
    private int level5Id;


    /**
     * 
     * @return
     *     The level5Id
     */
    public int getLevel5Id() {
        return level5Id;
    }

    /**
     * 
     * @param level5Id
     *     The level5_id
     */
    public void setLevel5Id(int level5Id) {
        this.level5Id = level5Id;
    }

    /**
     * 
     * @return
     *     The level2Id
     */
    public static final Creator<Level5> CREATOR = new Creator<Level5>()
    {
        public Level5 createFromParcel(Parcel in) {
            return new Level5(in);
        }

        public Level5[] newArray(int size) {
            return new Level5[size];
        }
    };
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeInt(level5Id);
    }

    public Level5(Parcel in) {
        super(in);
        level5Id = in.readInt();
    }
}
