package com.webmdee.samplevis.model;

/**
 * Used to wrap data about a sampled track for D3
 * Objects of this type are written to JSON and used for the sunburst and dendrogram visualizations
 * An object of this type will be mapped to a collection of all of the tracks that sample this track
 * @author MDee
 */
public class SampledTrack {
    
    private int id;
    private String title;
    private int year;
    private int genreId;
    private String genre;
    private int albumId;
    private String albumTitle;
    
    public SampledTrack(int id, String title, int year, int genreId, String genre, int albumId, String albumTitle) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.genreId = genreId;
        this.genre = genre;
        this.albumId = albumId;
        this.albumTitle = albumTitle;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getGenreId() {
        return genreId;
    }

    public void setGenreId(int genreId) {
        this.genreId = genreId;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }
    
    // Super hacky way to get all of the information about this track into a String so that it can be used as a key in the json array
    public String toString() {
        return this.id + "_" + this.title + "_" + this.year + "_" + this.genreId + "_" + this.genre + "_" + this.albumId + "_" + this.albumTitle;
    
    }
}
