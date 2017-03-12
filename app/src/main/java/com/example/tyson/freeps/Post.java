package com.example.tyson.freeps;

/**
 * Created by Davorin Doung on 3/11/2017.
 */
import com.google.firebase.database.IgnoreExtraProperties;

public class Post {

    String ClaimFlag;
    String Description;
    String ItemCategory;
    String LocationLon;
    String LocationLat;
//    public String Photo;
//    public String PostID;
    String TimeAndDate;
    String Title;
    String notThereFlag;

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

    public String getLocationLat(){
        return LocationLat;
    }

    public String getLocationLon(){
        return LocationLon;
    }

    public String getItemCategory(){
        return ItemCategory;
    }

    public String getTitle(){
        return Title;
    }

}