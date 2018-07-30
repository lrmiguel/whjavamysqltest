package com.ef.service;

import com.ef.model.DurationEnum;

import java.time.LocalDateTime;
import java.util.List;

public interface ParserService {
    void parseFile(String pathToAccessLogFile);

    List<String> getIPsFromAccessLog(LocalDateTime startDate, DurationEnum duration, int threshold);
}
