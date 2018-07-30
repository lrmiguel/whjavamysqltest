package com.ef.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "access_log", schema = "parser_db", indexes = {@Index(name = "ACCESS_DATETIME", columnList = "access_datetime")})
@NamedQuery(name = AccessLogEntity.GET_ACCESS_LOGS_BY_DATETIME, query = "select a from AccessLogEntity a where a.accessDatetime between :startDate and :endDate")
public class AccessLogEntity {

    public static final String GET_ACCESS_LOGS_BY_DATETIME = "AccessLogEntity.getAccessLogsByDatetime";

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", nullable = false)
    private UUID id; // Not generating id by the database increases performance, since ORM tool won't fetch sequence from the db before each insert.

    @Column(name = "access_datetime", nullable = false)
    private ZonedDateTime accessDatetime;//e

    @Column(name = "access_ip_address", nullable = false, length = 15)
    private String accessIpAddress;

    @Column(name = "http_method", nullable = false, length = 45)
    private String httpMethod;

    @Column(name = "http_status_code", nullable = false, length = 3)
    private String httpStatusCode;

    @Column(name = "browser_description", nullable = false, length = 255)
    private String browserDescription;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public ZonedDateTime getAccessDatetime() {
        return accessDatetime;
    }

    public void setAccessDatetime(ZonedDateTime accessDatetime) {
        this.accessDatetime = accessDatetime;
    }

    public String getAccessIpAddress() {
        return accessIpAddress;
    }

    public void setAccessIpAddress(String accessIpAddress) {
        this.accessIpAddress = accessIpAddress;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(String httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public String getBrowserDescription() {
        return browserDescription;
    }

    public void setBrowserDescription(String browserDescription) {
        this.browserDescription = browserDescription;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccessLogEntity that = (AccessLogEntity) o;
        return id == that.id &&
                Objects.equals(accessDatetime, that.accessDatetime) &&
                Objects.equals(accessIpAddress, that.accessIpAddress) &&
                Objects.equals(httpMethod, that.httpMethod) &&
                Objects.equals(httpStatusCode, that.httpStatusCode) &&
                Objects.equals(browserDescription, that.browserDescription);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, accessDatetime, accessIpAddress, httpMethod, httpStatusCode, browserDescription);
    }
}
