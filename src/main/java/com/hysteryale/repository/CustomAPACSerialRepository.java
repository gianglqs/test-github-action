package com.hysteryale.repository;


import com.hysteryale.model.APACSerial;
import com.hysteryale.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CustomAPACSerialRepository {

    public void saveOrUpdate(List<APACSerial> apacSerialList){
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        for(APACSerial apacSerial: apacSerialList){
            session.saveOrUpdate(apacSerial);
        }
    }
}
