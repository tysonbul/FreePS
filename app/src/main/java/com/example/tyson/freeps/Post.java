package com.example.tyson.freeps;

/**
 * Created by Davorin Doung on 3/11/2017.
 */
import com.google.firebase.database.IgnoreExtraProperties;

import java.sql.Time;

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
    String Image;

    public Post() {

    }
//public Post(String ClaimFlag, String TimeAndDate, String Description, String ItemCategory, String LocationLon, String LocationLat, String Photo, String PostID, String Title, String notThereFlag)
    public Post(String Image, String Title, String Description, String LocationLat, String LocationLon, String TimeAndDate, String ItemCategory, String ClaimFlag, String notThereFlag) {
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
        this.Image = Image;
    }

    public String geLocationLat(){
        return LocationLat;
    }

    public String geLocationLon(){
        return LocationLon;
    }

    public String geItemCategory(){
        return ItemCategory;
    }

    public String geTitle(){
        return Title;
    }

    public String geTimeAndDate(){
        return TimeAndDate;
    }

    public String geDescription(){
        return Description;
    }

    public String geClaimFlag(){
        return ClaimFlag;
    }

    public String geNotThereFlag(){
        return notThereFlag;
    }

    public String geImage(){
        return Image;
    }

}