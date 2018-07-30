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

    private static final String ACCESS_LOG = "accesslog";
    private static final String START_DATE = "startDate";
    private static final String DURATION = "duration";
    private static final String THRESHOLD = "threshold";

    private static Weld weld;
    private static String accessLog;
    private static LocalDateTime startDate;
    private static DurationEnum duration;
    private static int threshold;

    public static void main(String[] args) {
        CommandLineParser helperParser = new DefaultParser();
        CommandLine cmd = null;

        try {
            cmd = helperParser.parse(new Options().addOption("h", "help", false, "Gets information about usage"), args, false);
        } catch (ParseException e) {
            // Does nothing
        }

        if (cmd != null && cmd.getOptions().length == 1 && cmd.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("help", getOptions());
            System.exit(0);
        } else {
            getInputsFromConsole(args);

            ParserService parserService = getParserService();// CDI - Weld SE

            parserService.parseFile(accessLog);

            getIPsBasedOnParams(parserService);

            weld.shutdown();
            System.exit(0);
        }
    }

    private static void getIPsBasedOnParams(ParserService parserService) {
        List<String> ipsFromAccessLog = parserService.getIPsFromAccessLog(startDate, duration, threshold);

        if (ipsFromAccessLog != null && !ipsFromAccessLog.isEmpty()) {
            System.out.println("Retrieved IPs:");
            ipsFromAccessLog.forEach(System.out::println);
        } else
            System.out.println("No data found for the given parameters");
    }

    private static void getInputsFromConsole(String[] args) {
        CommandLine cmd;CommandLineParser gnuParser = new DefaultParser();
        cmd = null;
        try {
            cmd = gnuParser.parse(getOptions(), args, false);
        } catch (ParseException e) {
            exitWithError();
        }

        if (cmd.getOptions().length < 1)
            exitWithError();

        accessLog = cmd.getOptionValue(ACCESS_LOG);
        startDate = LocalDateTime.parse(cmd.getOptionValue(START_DATE), DateTimeFormatter.ofPattern("yyyy-MM-dd.HH:mm:ss"));
        duration = DurationEnum.getEnumByDurationType(cmd.getOptionValue(DURATION));
        threshold = Integer.valueOf(cmd.getOptionValue(THRESHOLD));
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
        options.addRequiredOption("s", START_DATE, true, "Start date for the search");
        options.addRequiredOption("d", DURATION, true, "Duration of the threshold from the start date");
        options.addRequiredOption("t", THRESHOLD, true, "Filters data by the minimum requests threshold");

        return options;
    }

}
