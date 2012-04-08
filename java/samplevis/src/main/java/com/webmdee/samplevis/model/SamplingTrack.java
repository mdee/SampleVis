package com.webmdee.samplevis.model;

/**
 * Used to wrap data about a sampling track for D3
 * Objects of this type are written in JSON and mapped to the track that they sampled
 * and are used for the sunburst and dendrogram visualizations
 * @author MDee
 */
public class SamplingTrack extends SampledTrack {

    private char sampleType;
    public char getSampleType() {
        return sampleType;
    }

    public void setSampleType(char sampleType) {
        this.sampleType = sampleType;
    }

    public char getPartSampled() {
        return partSampled;
    }

    public void setPartSampled(char partSampled) {
        this.partSampled = partSampled;
    }

    private char partSampled;
    
    private String url;
    
    public SamplingTrack(int id, String title, int year, int genreId,
            String genre, int albumId, String albumTitle, char sampleType, char partSampled, String url, int artistId, String artistName) {
        super(id, title, year, genreId, genre, albumId, albumTitle);
        this.sampleType = sampleType;
        this.partSampled = partSampled;
        this.url = url;
        this.artistId = artistId;
        this.artistName = artistName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    int artistId;
    String artistName;
    public int getArtistId() {
        return artistId;
    }

    public void setArtistId(int artistId) {
        this.artistId = artistId;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

}
