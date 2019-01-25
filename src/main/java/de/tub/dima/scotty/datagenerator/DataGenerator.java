package de.tub.dima.scotty.datagenerator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.tub.dima.scotty.datagenerator.configModel.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

/**
 * A basic generator for out ouf order data sets.
 * It generates files according to a configuration file.
 * Please pass the path to the config file.
 */
public class DataGenerator {

	private static String configPath;
	public static void main(String... args) throws Exception {

		if(args.length==0){
			throw new Exception("Please pass path to config file");
		}
		configPath = args[0];

		final DataGeneratorConfig config = loadConfig();

		List<ExperimentFileGenerator> experimentGenerators = config.generatorConfigurations.stream().map(dataGeneratorExperimentConfig -> new ExperimentFileGenerator(dataGeneratorExperimentConfig, config)).collect(Collectors.toList());

		Stream<String> rawFileStream = getFileStream(config.rawFilePath);
		// Map the lines to the simple DataTuple item
		Stream<DataTuple> dataTupleStram = rawFileStream.map(line -> {
			final String[] split = line.split(config.seperator);
			if (split.length <= 3)
				return null;
			return new DataTuple(
				split[config.keyIndex].trim(),
				split,
				Long.valueOf(split[config.timeIndex].trim())
			);
		});

		final Iterator<DataTuple> iterator = dataTupleStram.iterator();
		while (iterator.hasNext()) {
			final DataTuple dataTuple = iterator.next();

			if(dataTuple == null)
				break;
			if (dataTuple.eventTime > config.endTime)
				break;

			if (dataTuple.key.equals(config.keySelect) && dataTuple.eventTime > config.startTime) {
				dataTuple.eventTime = scaleTime(config.srcTimeScale, dataTuple.eventTime);
				dataTuple.processTime = dataTuple.eventTime;
				experimentGenerators.forEach(item ->
					item.addDataTuple(dataTuple));
			}
		}


		experimentGenerators.forEach(experimentGenerator -> {
			System.out.println("sort file");
			experimentGenerator.sort();
		});


	}

	private static DataGeneratorConfig loadConfig() throws Exception {
		try (java.io.Reader reader = new InputStreamReader(new FileInputStream(configPath), "UTF-8")) {
			Gson gson = new GsonBuilder().create();
			return gson.fromJson(reader, DataGeneratorConfig.class);
		}
	}

	private static Long scaleTime(String timeScale, long timeValue) {
		//System.out.println(timeValue);
		switch (timeScale) {
			case "ps":
				return timeValue / 1000000000;
			case "ns":
				return timeValue / 1000000;
			case "ms":
				return timeValue;
			case "s":
				return timeValue + 1000;
		}
		return timeValue;
	}

	private static Stream<String> getFileStream(String filePath) throws Exception {
		final GZIPInputStream gzipStream = new GZIPInputStream(new FileInputStream(filePath));
		BufferedReader reader = new BufferedReader(new InputStreamReader(gzipStream));
		return reader.lines();
	}

}
