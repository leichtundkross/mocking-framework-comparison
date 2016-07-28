package com.github.leichtundkross.mfc;

public class DemoEntity {

    private Long id;

    public DemoEntity() {
    }

    public DemoEntity(Long id) {
        this.id = id;
    }

    public void validate() throws IllegalArgumentException {
        // TODO Auto-generated method stub
    }

    public boolean isNew() {
        // TODO Auto-generated method stub
        return false;
    }

    public Long getId() {
        return id;
    }
}
