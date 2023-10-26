package com.hysteryale.repository;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Slf4j
@Repository
public class CustomRepository<T> {

    @PersistenceContext
    private EntityManager entityManager;


    public void merge(T entity) {
        Transaction transaction = null;
        Session session = entityManager.unwrap(Session.class);
        try {
            // start a transaction
            transaction = session.beginTransaction();
            // save  object
            session.merge(entity);
            // commit transaction
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
}
