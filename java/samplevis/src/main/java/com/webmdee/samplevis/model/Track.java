package com.webmdee.samplevis.model;

/**
 * Class that represents a track with the relevant data points
 * Used to store parsed CSV data before it is entered in the DB
 * @author MDee
 */
public class Track implements Comparable<Object>{

    private String title;
    private int id;
    private int artistId;
    private int albumId;
    private int genreId;
    private int year;
    
    public Track(int id, String title, int artistId, int albumId, int year) {
        this.artistId = artistId;
        this.title = title;
        this.id = id;
        this.albumId = albumId;
        this.setYear(year);
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

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public int getGenreId() {
        return genreId;
    }

    public void setGenreId(int genreId) {
        this.genreId = genreId;
    }
    
    @Override
    public boolean equals(Object o) {
        Track t = (Track) o;
        return (t.getTitle().equals(this.getTitle()) && t.getArtistId() == this.getArtistId() && t.getAlbumId() == this.getAlbumId());
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getYear() {
        return year;
    }
    
    @Override
    public int compareTo(Object arg0) {
        Track a = (Track) arg0;
        if (this.getId() < a.getId()) {
            return -1;
        } else if (this.getId() == a.getId()) {
            return 0;
        } else {
            return 1;
        }
        
    }
    
}
