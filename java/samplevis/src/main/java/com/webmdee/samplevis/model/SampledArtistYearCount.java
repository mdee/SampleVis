package com.webmdee.samplevis.model;

/**
 * Used to wrap data about sampled artists for D3
 * Objects of this type are written to JSON and used for the linegraph visualization
 * A sampled artist ID will be mapped to several of these objects
 * which detail the years and how many times that year an artist was sampled
 * @author MDee
 */
public class SampledArtistYearCount {

    private int year;
    private int count;
    
    public SampledArtistYearCount() {}
    
    public SampledArtistYearCount(int year, int count) {
        this.year = year;
        this.count = count;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int x) {
        this.year = x;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int y) {
        this.count = y;
    }
    
}
