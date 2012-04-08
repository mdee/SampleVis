package com.webmdee.samplevis.model;

/**
 * Class that represents an album with the relevant data points
 * Used to store parsed CSV data before it is entered in the DB
 * @author MDee
 */
public class Album implements Comparable<Object>{

    private String title;
    private int id;
    private int artistId;
    private int year;
    
    public Album(int id, String title, int artistId, int year) {
        this.title = title;
        this.id = id;
        this.artistId = artistId;
        this.year = year;
    }
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public int getArtistId() {
        return artistId;
    }

    public void setArtistId(int artistId) {
        this.artistId = artistId;
    }

    @Override
    public boolean equals(Object o) {
        Album a = (Album) o;
        return (a.getTitle().equals(this.getTitle()) && a.getArtistId() == this.getArtistId() && a.year == this.year);
    }
    
    @Override
    public int compareTo(Object arg0) {
        Album a = (Album) arg0;
        if (this.getId() < a.getId()) {
            return -1;
        } else if (this.getId() == a.getId()) {
            return 0;
        } else {
            return 1;
        }
        
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
    
}
