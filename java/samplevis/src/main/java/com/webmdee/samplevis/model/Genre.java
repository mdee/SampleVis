package com.webmdee.samplevis.model;

/**
 * Class that represents a genre with the relevant data points
 * Used to store parsed CSV data before it is entered in the DB
 * @author MDee
 */
public class Genre implements Comparable<Object>{

    private String name;
    private int id;
    
    public Genre(int id, String name) {
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
        Genre g = (Genre) o;
        return g.getName().equals(this.getName());
    }
    @Override
    public int compareTo(Object arg0) {
        Genre a = (Genre) arg0;
        if (this.getId() < a.getId()) {
            return -1;
        } else if (this.getId() == a.getId()) {
            return 0;
        } else {
            return 1;
        }
        
    }
}
