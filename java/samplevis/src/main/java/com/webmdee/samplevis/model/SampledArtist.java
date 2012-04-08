package com.webmdee.samplevis.model;

/**
 * Used to wrap data about sampled artists for D3
 * Objects of this type are written to JSON for the treemap visualization
 * @author MDee
 */
public class SampledArtist {
    private int id;
    private String name;
    private int size;
    private int index;
    
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public SampledArtist() {
    }
    
    public SampledArtist(int id, String name, int size, int index) {
        this.id = id;
        this.name = name;
        this.index = index;
        this.size = size;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int sampleCount) {
        this.size = sampleCount;
    }
}
