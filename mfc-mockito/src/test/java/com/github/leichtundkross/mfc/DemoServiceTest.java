package com.github.leichtundkross.mfc;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

@RunWith(MockitoJUnitRunner.class)
public class DemoServiceTest {

    @InjectMocks
    DemoService service;

    @Mock
    DemoDAO dao;

    @Test
    public void save() {
        DemoEntity entity = new DemoEntity();

        service.save(entity);

        Mockito.verify(dao, Mockito.times(0)).insert(entity);
        Mockito.verify(dao).update(entity);
    }

    @Test
    public void save_newEntity() {
        DemoEntity entity = Mockito.mock(DemoEntity.class);
        Mockito.when(entity.isNew()).thenReturn(true);

        service.save(entity);

        Mockito.verify(dao).insert(entity);
    }

    @Test
    public void save_entitySavedServeralTime() {
        AtomicBoolean isNew = new AtomicBoolean(true);
        DemoEntity entity = Mockito.mock(DemoEntity.class);
        Mockito.when(entity.isNew()).thenAnswer(new Answer<Boolean>() {

            @Override
            public Boolean answer(InvocationOnMock invocation) {
                if (isNew.get()) {
                    isNew.set(false);
                    return true;
                }

                return false;
            }
        });

        service.save(entity);
        service.save(entity);
        service.save(entity);

        Mockito.verify(dao, Mockito.times(1)).insert(entity);
        Mockito.verify(dao, Mockito.times(2)).update(entity);
    }

    @Test
    public void save_isNewIsCalled() {
        DemoEntity entity = Mockito.spy(new DemoEntity());

        service.save(entity);

        Mockito.verify(entity).isNew();
    }

    @Test
    public void save_neverCallsLoad() {
        DemoEntity entity = new DemoEntity();

        service.save(entity);

        Mockito.verify(dao, Mockito.never()).load(ArgumentMatchers.any(DemoEntity.class));
    }
}
