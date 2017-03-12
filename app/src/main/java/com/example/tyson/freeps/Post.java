package com.example.tyson.freeps;

/**
 * Created by Davorin Doung on 3/11/2017.
 */
import com.google.firebase.database.IgnoreExtraProperties;

public class Post {

//    public String ClaimFlag;
//    public String Date;
    public String Description;
//    public String ItemCategory;
//    public String LocationLon;
//    public String LocationLat;
//    public String Photo;
//    public String PostID;
//    public String Time;
    public String Title;
//    public String notThereFlag;

    public Post() {

    }
//public Post(String ClaimFlag, String Date, String Description, String ItemCategory, String LocationLon, String LocationLat, String Photo, String PostID, String Time, String Title, String notThereFlag)
    public Post(String Title, String Description) {
//        this.PostID = PostID;
//        this.Date = Date;
        this.Description = Description;
//        this.ItemCategory = ItemCategory;
//        this.LocationLon = LocationLon;
//        this.LocationLat = LocationLat;
//        this.Photo = Photo;
//        this.Time = Time;
        this.Title = Title;
//        this.notThereFlag = notThereFlag;
    }
}