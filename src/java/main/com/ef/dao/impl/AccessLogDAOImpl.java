package com.ef.dao.impl;

import com.ef.dao.AccessLogDAO;
import com.ef.model.AccessLogEntity;
import org.hibernate.CacheMode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class AccessLogDAOImpl implements AccessLogDAO {

    @Inject
    private EntityManager em;

    @Override
    public void persist(AccessLogEntity accessLogEntity) {
        em.persist(accessLogEntity);
    }

    @Override
    // When high-performance is needed
    public void persistBulk(Collection<AccessLogEntity> accessLogs) {
        Session session = em.unwrap(Session.class);
        session.setCacheMode(CacheMode.IGNORE);
        Transaction tx = session.beginTransaction();
        NativeQuery sqlQuery = session.createSQLQuery("INSERT INTO access_log(id, access_datetime, access_ip_address, http_method, http_status_code, browser_description) VALUES (?, ?, ?, ?, ?, ?)");
        accessLogs.stream().filter(Objects::isNull).count();
        accessLogs.stream().filter(Objects::nonNull).forEach(item -> {
            sqlQuery.setParameter(1, UUID.randomUUID());
            sqlQuery.setParameter(2, item.getAccessDatetime());
            sqlQuery.setParameter(3, item.getAccessIpAddress());
            sqlQuery.setParameter(4, item.getHttpMethod());
            sqlQuery.setParameter(5, item.getHttpStatusCode());
            sqlQuery.setParameter(6, item.getBrowserDescription());
            sqlQuery.executeUpdate();
        });
        tx.commit();
        session.close();
    }

    @Override
    public List<AccessLogEntity> getAccessLogs(ZonedDateTime startDate, ZonedDateTime endDate) {
        TypedQuery<AccessLogEntity> namedQuery = em.createNamedQuery(AccessLogEntity.GET_ACCESS_LOGS_BY_DATETIME, AccessLogEntity.class);

        namedQuery.setParameter("startDate", startDate);
        namedQuery.setParameter("endDate", endDate);

        return namedQuery.getResultList();
    }
}
