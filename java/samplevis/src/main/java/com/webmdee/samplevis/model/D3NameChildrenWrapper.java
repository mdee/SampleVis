package com.webmdee.samplevis.model;

import java.util.List;

/**
 * Many of the D3 visualizations expect data to be hierarchically organized, with each parent
 * node containing a value mapped to the "name" key and a collection mapped to the "children" key.
 * In the interest of time so that the project can be turned in, this wrapper class is used to quickly
 * map the appropriate data (like a sampled track to its sampling tracks) and create a hierarchy for D3.
 * @author MDee
 */
public class D3NameChildrenWrapper {
    
    private String name;
    private List children;
    
    public D3NameChildrenWrapper(){}
    
    public D3NameChildrenWrapper(String name, List children) {
        this.name = name;
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List getChildren() {
        return children;
    }

    public void setChildren(List children) {
        this.children = children;
    }
    
    

}
