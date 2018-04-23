package org.mahiti.convenemis.BeenClass;

/**
 * Created by mahiti on 2/12/16.
 */
public class LevelsListBean
{
    public int id;
    public String name;

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
    public LevelsListBean(int _id, String _name)
    {
        id = _id;
        name = _name;
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
}
