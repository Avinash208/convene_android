package org.mahiti.convenemis.BeenClass.parentChild;

/**
 * Created by mahiti on 8/12/16.
 */
public class LevelBeen
{
    private int id;
    private String name;
    private String uuid;
    private int locationLevel;
    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public LevelBeen() {


    }


    public int getLocationLevel() {
        return locationLevel;
    }

    public void setLocationLevel(int locationLevel) {
        this.locationLevel = locationLevel;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    // A simple constructor for populating our member variables for this tutorial.
    public LevelBeen( int _id, String _name)
    {
        id = _id;
        name = _name;
    }

    public LevelBeen( int _id, String _name,int _locationLevel)
    {
        id = _id;
        name = _name;
        locationLevel=_locationLevel;
    }
    // The toString method is extremely important to making this class work with a Spinner
    // (or ListView) object because this is the method called when it is trying to represent
    // this object within the control.  If you do not have a toString() method, you WILL
    // get an exception.
    @Override
    public String toString()
    {
        return name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
