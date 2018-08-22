
package org.yale.convene.BeenClass.parentChild;

import android.os.Parcel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Level4 extends Level3
{

    @SerializedName("level4_id")
    @Expose
    private int level4Id;




    /**
     * 
     * @return
     *     The level4Id
     */
    public int getLevel4Id() {
        return level4Id;
    }

    /**
     * 
     * @param level4Id
     *     The level4_id
     */
    public void setLevel4Id(int level4Id) {
        this.level4Id = level4Id;
    }


    public static final Creator<Level4> CREATOR = new Creator<Level4>()
    {
        public Level4 createFromParcel(Parcel in) {
            return new Level4(in);
        }

        public Level4[] newArray(int size) {
            return new Level4[size];
        }
    };
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeInt(level4Id);
    }

    public Level4(Parcel in) {
        super(in);
        level4Id = in.readInt();
    }
}
