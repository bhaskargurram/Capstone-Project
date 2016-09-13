package com.bhaskar.snapreminder.model;

/**
 * Created by bhaskar on 25/5/16.
 */
public class ReminderCards {
    String image_path, description, date;
    long rem_id;

    public ReminderCards(String image_path, String description, String date, long rem_id) {
        this.image_path = image_path;
        this.description = description;
        this.date = date;
        this.rem_id = rem_id;
    }

    public void setRem_id(long rem_id) {
        this.rem_id = rem_id;
    }

    public long getRem_id() {
        return rem_id;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getImage_path() {
        return image_path;
    }
}
