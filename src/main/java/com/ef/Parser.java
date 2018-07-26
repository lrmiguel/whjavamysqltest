package com.ef;

import com.ef.model.DurationEnum;
import com.ef.service.ParserService;
import org.apache.commons.cli.*;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Parser {

    public static final String START_DATE = "startDate";
    public static final String DURATION = "duration";
    public static final String THRESHOLD = "threshold";

    @Inject
    private ParserService service;

    public static void main(String[] args) {

        CommandLineParser gnuParser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = gnuParser.parse(getOptions(), args, true);
        } catch (ParseException e) {
            exitWithError();
        }

        if (cmd.getOptions().length < 1)
            exitWithError();

        LocalDateTime startDate = LocalDateTime.parse(cmd.getOptionValue(START_DATE), DateTimeFormatter.ofPattern("yyyy-MM-dd.HH:mm:ss"));
        DurationEnum duration = DurationEnum.getEnumByDurationType(cmd.getOptionValue(DURATION));
        int threshold = Integer.valueOf(cmd.getOptionValue(THRESHOLD));


    }

    private static void exitWithError() {
        System.err.println("Please, provide all arguments.");
        System.err.println("Usage example: --startDate=2017-01-01.13:00:00 --duration=[daily|hourly] --threshold=250");
        System.exit(-1);
    }

    private static Options getOptions() {
        final Options options = new Options();
        options.addRequiredOption("s", START_DATE,true,"Start date for the search");
        options.addRequiredOption("d", DURATION, true, "Duration of the threshold from the start date");
        options.addRequiredOption("t", THRESHOLD, true, "Filters data by the minimum requests threshold");
        return options;
    }

}
