package com.example.musicbottom;

public class Songs {
    private String title;
    private String path;
    private String duration;
    private String id;

    public Songs(){

    }

    public Songs(String name , String path , String duration, String id){
        this.title = name;
        this.path = path;
        this.duration = duration;
        this.id = id;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setName(String name) {
        this.title = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
