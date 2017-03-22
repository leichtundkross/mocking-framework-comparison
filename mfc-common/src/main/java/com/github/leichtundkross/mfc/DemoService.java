package com.github.leichtundkross.mfc;

import javax.inject.Inject;

public class DemoService {

    @Inject
    private DemoDAO dao;

    public DemoEntity create(Long id) {
        DemoEntity entity = new DemoEntity(id);
        dao.insert(entity);
        return entity;
    }

    public void save(DemoEntity entity) {
        entity.validate();
        if (entity.isNew()) {
            dao.insert(entity);
        } else {
            dao.update(entity);
        }
    }
}
