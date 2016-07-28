package com.github.leichtundkross.mfc;

import javax.inject.Inject;

public class DemoService {

    @Inject
    private DemoDAO dao;

    public void save(DemoEntity entity) {
        if (entity.isNew()) {
            dao.insert(entity);
        } else {
            dao.update(entity);
        }
    }
}