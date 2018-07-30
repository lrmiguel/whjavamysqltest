package com.ef.service.impl;

import com.ef.dao.AccessLogDAO;
import com.ef.model.AccessLogEntity;
import com.ef.model.DurationEnum;
import com.ef.service.ParserService;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ParserServiceImpl implements ParserService {

    @Inject
    private AccessLogDAO accessLogDAO;

    private List<AccessLogEntity> accessLogEntities;
    private static final Logger LOGGER = Logger.getLogger(ParserServiceImpl.class.getName());;

    @Override
    public void parseFile(String pathToAccessLogFile) {
        if (pathToAccessLogFile == null)
            return;

        LOGGER.info("Starting parsing process");
        extractAccessLogs(pathToAccessLogFile);
        LOGGER.info("Finishing parsing process");

        LOGGER.info("Inserting parsed objects into the database");
        accessLogDAO.persistBulk(accessLogEntities);
        LOGGER.info("Finished insertions");
    }

    private void extractAccessLogs(String pathToAccessLogFile) {
        accessLogEntities = new ArrayList<>();
        try (Stream<String> linesStream = Files.lines(Paths.get(pathToAccessLogFile))) {
            linesStream.forEach(s -> {
                String[] logElement = s.split("[|]");
                ZonedDateTime accessDatetime = LocalDateTime.parse(logElement[0], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")).atZone(ZoneId.of("GMT+00:00"));
                String ipAddress = logElement[1];
                String httpMethod = logElement[2];
                String httpStatusCode = logElement[3];
                String browserDescription = logElement[4];

                AccessLogEntity accessLogEntity = new AccessLogEntity();
                accessLogEntity.setAccessDatetime(accessDatetime);
                accessLogEntity.setAccessIpAddress(ipAddress);
                accessLogEntity.setHttpMethod(httpMethod.replace("\"", ""));
                accessLogEntity.setHttpStatusCode(httpStatusCode);
                accessLogEntity.setBrowserDescription(browserDescription.replace("\"", ""));

                accessLogEntities.add(accessLogEntity);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> getIPsFromAccessLog(LocalDateTime startDate, DurationEnum duration, int threshold) {
        LocalDateTime endDate = (duration.equals(DurationEnum.DAILY) ? startDate.plusDays(1L).minusNanos(1) : startDate.plusHours(1).minusNanos(1));
        List<AccessLogEntity> accesses = accessLogDAO.getAccessLogs(startDate.atZone(ZoneId.of("GMT+00:00")), endDate.atZone(ZoneId.of("GMT+00:00")));
        Map<String, Long> countsByIps = accesses.stream().collect(Collectors.groupingBy(AccessLogEntity::getAccessIpAddress, Collectors.counting()));

        return countsByIps.entrySet().stream().filter(o -> o.getValue() >= threshold).map(Map.Entry::getKey).collect(Collectors.toList());
    }
}
