package de.tub.dima.scotty.datagenerator;

import org.apache.commons.cli.*;
import org.apache.commons.math3.ml.neuralnet.twod.util.*;

import java.io.*;
import java.util.*;

/**
 * /$$$$$$             /$$$$$$        /$$    /$$                    /$$  /$$$$$$  /$$
 * /$$__  $$           /$$__  $$      | $$   | $$                   |__/ /$$__  $$|__/
 * | $$  \ $$  /$$$$$$ | $$  \ $$      | $$   | $$ /$$$$$$   /$$$$$$  /$$| $$  \__/ /$$  /$$$$$$   /$$$$$$
 * | $$  | $$ /$$__  $$| $$  | $$      |  $$ / $$//$$__  $$ /$$__  $$| $$| $$$$    | $$ /$$__  $$ /$$__  $$
 * | $$  | $$| $$  \ $$| $$  | $$       \  $$ $$/| $$$$$$$$| $$  \__/| $$| $$_/    | $$| $$$$$$$$| $$  \__/
 * | $$  | $$| $$  | $$| $$  | $$        \  $$$/ | $$_____/| $$      | $$| $$      | $$| $$_____/| $$
 * |  $$$$$$/|  $$$$$$/|  $$$$$$/         \  $/  |  $$$$$$$| $$      | $$| $$      | $$|  $$$$$$$| $$
 * \______/  \______/  \______/           \_/    \_______/|__/      |__/|__/      |__/ \_______/|__/
 */
public class Verifier {

    public static void main(String[] args) throws Exception {

        Options options = new Options();
        options.addOption("path","filePath", true,"filepath");
        Option timeindex = Option.builder("ti")
                .required(true)
                .hasArg()
                .build();
        options = options.addOption(timeindex);
        options.addOption("td","timedomain", true,"Time domain for event time (ps, ns, mic, mi, s");


        CommandLineParser parser = new DefaultParser();
        CommandLine cli = parser.parse(options, args);

        String path = cli.getOptionValue("path");
        int timeIndex = Integer.valueOf(cli.getOptionValue("ti"));
        String timeDomain = cli.getOptionValue("td");

        long numberOfEvents = 0;
        long numberOfEventsInSec = 0;
        long numberOfOutOfOrderEvents = 0;
        long maxTS = 0;
        LongSummaryStatistics delays = new LongSummaryStatistics();
        LongSummaryStatistics frequency = new LongSummaryStatistics();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
        String[] tuple = readNextTuple(reader);

        long lastSec = 0;
        while (tuple != null) {
            numberOfEvents++;
            long ts = Long.valueOf(tuple[timeIndex]);
            maxTS = Math.max(maxTS, ts);
            if (ts < maxTS) {
                //out Of order
                numberOfOutOfOrderEvents++;
                long eventDelay = maxTS - ts;
                delays.accept(eventDelay);
            }
            tuple = readNextTuple(reader);
        }


        System.out.println("events : " + numberOfEvents);
        System.out.println("outOfOrderEvents : " + numberOfOutOfOrderEvents);
        System.out.println("outOfOrder % : " + ((100.0 / numberOfEvents) * numberOfOutOfOrderEvents));
        System.out.println("maxDelay: " + delays.getMax());
        System.out.println("minDelay: " + delays.getMin());
        System.out.println("averageDelay: " + delays.getAverage());
    }


    private static String[] readNextTuple(BufferedReader reader) throws Exception {
        final String line = reader.readLine();
        if (line == null)
            return null;
        return line.split(",");
    }

}
