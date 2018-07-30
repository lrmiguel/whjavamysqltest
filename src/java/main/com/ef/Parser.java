package com.ef;

import com.ef.model.DurationEnum;
import com.ef.service.ParserService;
import org.apache.commons.cli.*;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Parser {

    public static final String ACCESS_LOG = "accesslog";
    public static final String START_DATE = "startDate";
    public static final String DURATION = "duration";
    public static final String THRESHOLD = "threshold";

    private static Weld weld;

    public static void main(String[] args) {

        CommandLineParser gnuParser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = gnuParser.parse(getOptions(), args, false);
        } catch (ParseException e) {
            exitWithError();
        }

        if (cmd.getOptions().length < 1)
            exitWithError();

        String accessLog = cmd.getOptionValue(ACCESS_LOG);
        LocalDateTime startDate = LocalDateTime.parse(cmd.getOptionValue(START_DATE), DateTimeFormatter.ofPattern("yyyy-MM-dd.HH:mm:ss"));
        DurationEnum duration = DurationEnum.getEnumByDurationType(cmd.getOptionValue(DURATION));
        int threshold = Integer.valueOf(cmd.getOptionValue(THRESHOLD));

        ParserService parserService = getParserService();
        parserService.parseFile(accessLog);

        List<String> ipsFromAccessLog = parserService.getIPsFromAccessLog(startDate, duration, threshold);

        if (ipsFromAccessLog != null && !ipsFromAccessLog.isEmpty())
            ipsFromAccessLog.forEach(ipFromAccessLog -> System.out.println("Output: " + ipFromAccessLog));
        else
            System.out.println("No data found for the required parameters");


        weld.shutdown();
        System.exit(0);
    }

    private static ParserService getParserService() {
        weld = new Weld();
        WeldContainer container = weld.initialize();

        return container.select(ParserService.class).get();
    }

    private static void exitWithError() {
        System.err.println("Please, provide all arguments.");
        System.err.println("Usage example: --startDate=2017-01-01.13:00:00 --duration=[daily|hourly] --threshold=250");
        System.exit(-1);
    }

    private static Options getOptions() {
        final Options options = new Options();
        options.addOption("a", ACCESS_LOG, true, "Path to file containing access log");
        options.addOption("s", START_DATE, true, "Start date for the search");
        options.addOption("d", DURATION, true, "Duration of the threshold from the start date");
        options.addOption("t", THRESHOLD, true, "Filters data by the minimum requests threshold");
        return options;
    }

}
