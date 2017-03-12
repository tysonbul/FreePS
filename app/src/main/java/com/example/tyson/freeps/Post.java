package com.example.tyson.freeps;

/**
 * Created by Davorin Doung on 3/11/2017.
 */
import com.google.firebase.database.IgnoreExtraProperties;

public class Post {

    public String ClaimFlag;
    public String Description;
    public String ItemCategory;
    public String LocationLon;
    public String LocationLat;
//    public String Photo;
//    public String PostID;
    public String TimeAndDate;
    public String Title;
    public String notThereFlag;

    public Post() {

    }
//public Post(String ClaimFlag, String TimeAndDate, String Description, String ItemCategory, String LocationLon, String LocationLat, String Photo, String PostID, String Title, String notThereFlag)
    public Post(String Title, String Description, String LocationLat, String LocationLon, String TimeAndDate, String ItemCategory, String ClaimFlag, String notThereFlag) {
//        this.PostID = PostID;
        this.TimeAndDate = TimeAndDate;
        this.Description = Description;
        this.ItemCategory = ItemCategory;
        this.LocationLon = LocationLon;
        this.LocationLat = LocationLat;
//        this.Photo = Photo;
        this.Title = Title;
        this.ClaimFlag = ClaimFlag;
        this.notThereFlag = notThereFlag;
    }
}