package com.github.leichtundkross.mfc;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;

import mockit.Delegate;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import mockit.VerificationsInOrder;

public class DemoServiceTest {

    @Tested
    DemoService service;

    @Injectable
    DemoDAO dao;

    @Test
    public void save() {
        DemoEntity entity = new DemoEntity();

        service.save(entity);

        new Verifications() {
            {
                dao.insert(entity); times = 0;
                dao.update(entity);
            }
        };
    }

    @Test
    public void save_newEntity(@Mocked DemoEntity entity) {
        new Expectations() {
            {
                entity.isNew(); result = true;
            }
        };

        service.save(entity);

        new Verifications() {
            {
                dao.insert(entity);
            }
        };
    }

    @Test
    public void save_entitySavedServeralTime(@Mocked DemoEntity entity) {
        new Expectations() {
            {
                AtomicBoolean isNew = new AtomicBoolean(true);

                entity.isNew();
                result = new Delegate<Boolean>() {

                    @SuppressWarnings("unused")
                    boolean delegate() {
                        if (isNew.get()) {
                            isNew.set(false);
                            return true;
                        }

                        return false;
                    }
                };
            }
        };

        service.save(entity);
        service.save(entity);
        service.save(entity);

        new Verifications() {
            {
                dao.insert(entity); times = 1;
                dao.update(entity); times = 2;
            }
        };
    }

    @Test
    public void save_neverCallsLoad() {
        DemoEntity entity = new DemoEntity();

        service.save(entity);

        new Verifications() {
            {
                dao.load(withInstanceOf(DemoEntity.class)); times = 0;
            }
        };
    }

    @Test
    public void save_validateCalledBeforeUpdate(@Mocked DemoEntity entity) {
        service.save(entity);

        new VerificationsInOrder() {
            {
                entity.validate();
                dao.update(entity);
            }
        };
    }

    @Test
    public void save_verifyCorrectId() {
        final Long entityId = Long.valueOf(4711L);
        DemoEntity entity = new DemoEntity(entityId);

        service.save(entity);

        new Verifications() {
            {
                DemoEntity captured;
                dao.update(captured = withCapture());
                assertEquals(entityId, captured.getId());
            }
        };
    }

    @Test
    public void create(@Mocked DemoEntity e) {
        service.create(10L);
        service.create(15L);

        new Verifications() {
            {
                List<DemoEntity> entitiesInstantiated = withCapture(new DemoEntity(anyLong));

                List<DemoEntity> entitesInserted = new ArrayList<>();
                dao.insert(withCapture(entitesInserted));

                assertEquals(entitiesInstantiated, entitesInserted);
            }
        };
    }
}
