package com.github.leichtundkross.mfc;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
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

    @Test
    public void save_validateCalledBeforeUpdate() {
        DemoEntity entity = Mockito.mock(DemoEntity.class);

        service.save(entity);

        InOrder executionOrder = Mockito.inOrder(entity, dao);
        executionOrder.verify(entity).validate();
        executionOrder.verify(dao).update(entity);
        executionOrder.verifyNoMoreInteractions();
    }

    @Test
    public void save_verifyCorrectId() {
        ArgumentCaptor<DemoEntity> entityCaptor = ArgumentCaptor.forClass(DemoEntity.class);
        Mockito.doNothing().when(dao).update(entityCaptor.capture());

        final Long entityId = Long.valueOf(4711L);
        DemoEntity entity = new DemoEntity(entityId);

        service.save(entity);

        assertEquals(entityId, entityCaptor.getValue().getId());
    }
}
