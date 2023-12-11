package com.example.myapplication;

import android.graphics.Bitmap;

public class ItemModel {
    private String name;
    private String type; // Thêm trường type để đánh dấu là folder, file, hoặc nhạc
    private String album;
    private Bitmap imageMusicPath;


    public ItemModel(String name, String type) {
        this.name = name;
        this.type = type;

    }
    public ItemModel(String name,  String type, String album, Bitmap imageMusicPath) {
        this.name = name;
        this.type = type;
        this.album = album;
        this.imageMusicPath = imageMusicPath;

    }
    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getAlbum() {
        return album;
    }


    public Bitmap getImageMusicPath() {
        return imageMusicPath;
    }

}
