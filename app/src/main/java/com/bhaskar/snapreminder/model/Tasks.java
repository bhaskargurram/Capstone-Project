package com.bhaskar.snapreminder.model;

/**
 * Created by bhaskar on 24/5/16.
 */
public class Tasks {
    String taskname;
    boolean checked;
    int tasknumber;

    public Tasks(String taskname, boolean checked, int tasknumber) {
        this.taskname = taskname;
        this.checked = checked;
        this.tasknumber = tasknumber;
    }

    public void setTasknumber(int tasknumber) {
        this.tasknumber = tasknumber;
    }

    public int getTasknumber() {
        return tasknumber;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getTaskname() {
        return taskname;
    }

    public void setTaskname(String taskname) {
        this.taskname = taskname;
    }

    public boolean getChecked() {
        return checked;
    }

}
