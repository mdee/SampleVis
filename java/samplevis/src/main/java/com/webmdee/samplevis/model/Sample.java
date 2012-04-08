package com.webmdee.samplevis.model;

/**
 * Class that represents a sample with the relevant data points
 * Used to store parsed CSV data before it is entered in the DB
 * @author MDee
 */
public class Sample implements Comparable<Object>{
    
    private int id;
    private int sampledId;
    private int samplingId;
    private char sampleType;
    private char samplePart;
    private String whoSampledURL;
    
    public Sample(int id, int sampledId, int samplingId, char sampleType, char samplePart, String whoSampledURL) {
        this.id = id;
        this.sampledId = sampledId;
        this.samplingId = samplingId;
        this.sampleType = sampleType;
        this.samplePart = samplePart;
        this.whoSampledURL = whoSampledURL;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSampledId() {
        return sampledId;
    }

    public void setSampledId(int sampledId) {
        this.sampledId = sampledId;
    }

    public int getSamplingId() {
        return samplingId;
    }

    public void setSamplingId(int samplingId) {
        this.samplingId = samplingId;
    }

    public char getSampleType() {
        return sampleType;
    }

    public void setSampleType(char sampleType) {
        this.sampleType = sampleType;
    }

    public char getSamplePart() {
        return samplePart;
    }

    public void setSamplePart(char samplePart) {
        this.samplePart = samplePart;
    }

    public String getWhoSampledURL() {
        return whoSampledURL;
    }

    public void setWhoSampledURL(String whoSampledURL) {
        this.whoSampledURL = whoSampledURL;
    }
    
    @Override
    public int compareTo(Object arg0) {
        Sample s = (Sample) arg0;
        if (this.getId() < s.getId()) {
            return -1;
        } else if (this.getId() == s.getId()) {
            return 0;
        } else {
            return 1;
        }
        
    }
}
