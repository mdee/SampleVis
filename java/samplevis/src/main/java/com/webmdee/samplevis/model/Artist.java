package com.webmdee.samplevis.model;

/**
 * Class that represents an artist with the relevant data points
 * Used to store parsed CSV data before it is entered in the DB
 * @author MDee
 */
public class Artist implements Comparable<Object>{
    
    private String name;
    private int id;
    
    public Artist(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    @Override
    public boolean equals(Object o) {
        Artist a = (Artist) o;
        return (a.getName().equals(this.getName()));
    }

    @Override
    public int compareTo(Object arg0) {
        Artist a = (Artist) arg0;
        if (this.getId() < a.getId()) {
            return -1;
        } else if (this.getId() == a.getId()) {
            return 0;
        } else {
            return 1;
        }
        
    }

}
