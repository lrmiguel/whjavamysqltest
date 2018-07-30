package com.ef.dao;

import com.ef.model.AccessLogEntity;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;

public interface AccessLogDAO {
    void persist(AccessLogEntity accessLogEntity);

    void persistBulk(Collection<AccessLogEntity> accessLogs);

    List<AccessLogEntity> getAccessLogs(ZonedDateTime startDate, ZonedDateTime endDate);
}
