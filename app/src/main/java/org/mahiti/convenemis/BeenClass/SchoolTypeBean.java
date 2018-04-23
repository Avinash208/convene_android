package org.mahiti.convenemis.BeenClass;

/**
 * Created by mahiti on 27/5/17.
 */

public class SchoolTypeBean {
    String modifiedDate;
    int id;
    String name;
    int active;
    public SchoolTypeBean(){

    }
    public SchoolTypeBean(String defaultstring){
        this.name=defaultstring;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
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

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public String toString(){
        return name;
    }
}
