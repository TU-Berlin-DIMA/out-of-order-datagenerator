package de.tub.dima.scotty.datagenerator;

import de.tub.dima.scotty.datagenerator.configModel.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * Created by philipp on 5/25/17.
 */
public class ExperimentFileGenerator {

    private final RandomGenerator outOfOrderGenerator;
    private final String path;
    private DataGeneratorExperimentConfig config;
    private BufferedWriter writer;
    private int tuples = 0;
    private final RandomGenerator delayGenerator;

    OpenOption[] options = {StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING};

    public ExperimentFileGenerator(final DataGeneratorExperimentConfig config, DataGeneratorConfig dataGeneratorConfig) {
        this.config = config;

        this.path = dataGeneratorConfig.outputFilePath + "_OutOfOrder" + config.outOfOrder + "_maxdelay" + config.maxDelay + "_mindelay" + config.minDelay + ".data";

        try {

            writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get(path), options)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.delayGenerator = new RandomGenerator(100);
        this.outOfOrderGenerator = new RandomGenerator(100);
    }

    public void addDataTuple(DataTuple tuple) {

        // add out of order delay
        if (outOfOrderGenerator.getBoolean(config.outOfOrder)) {
            long delay = config.minDelay + delayGenerator.getInt(config.maxDelay - config.minDelay);
            // copy tuple
            tuple = new DataTuple(tuple.key, tuple.values, tuple.eventTime);
            tuple.processTime += delay;
        }

        String result = tupleToString(tuple);
        try {
            writer.write(result);
            writer.newLine();
            tuples++;
            if (tuples % 100000 == 0) {
                writer.flush();
                System.out.println("added " + tuples);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String tupleToString(DataTuple tuple) {
        StringBuilder sb = new StringBuilder();
        sb.append(tuple.processTime);
        sb.append(",");
        sb.append(tuple.eventTime);
        sb.append(",");
        for (String value : tuple.values) {
            sb.append(value);
            sb.append(",");
        }

        return sb.toString();
    }

    public void sort() {
        System.out.println("sort " + tuples);

        try {
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(this.path)));
            final Stream<CharSequence> sortedStream = reader
                    .lines()
                    .sorted(new Comparator<String>() {
                        @Override
                        public int compare(final String o1, final String o2) {
                            return Long.compare(getTimeFromStringValue(o1), getTimeFromStringValue(o2));
                        }
                    }).map(Function.identity());

            Path sortedFile = Paths.get(this.path);

            Files.write(sortedFile, sortedStream::iterator, StandardOpenOption.CREATE);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private long getTimeFromStringValue(String string) {
        final String[] split = string.split(",");
        assert split.length < 1;
        return Long.valueOf(string.split(",")[0]);
    }

}
