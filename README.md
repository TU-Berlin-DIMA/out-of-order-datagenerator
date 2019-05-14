# Generating Reproducible Out-of-Order Data Streams

Evaluating modern stream processing systems in a reproduciblemanner requires data streams with different data distributions,data rates, and real-world characteristics such as delayed and out-of-order tuples. In this paper, we present an open source streamgenerator which generates reproducible and deterministic out-of-order streams, simulating arbitrary fractions of out-of-order tuplesand their respective delays.


### Poster:

The out of order data generator was accepted at the [DEBS 2019](http://debs2019.org/).
A pre print will be released soon.

For this, we provide two executables, the data generator, and the verifier.

### Data Generator:

The data generator is customized by a config file: 
Example in:
config/debs_generate_out_of_order.json

```json 
{
  "outputFilePath":"data/", // output dir
  "rawFilePath": "/home/philipp/debs2013.data.tar.gz", // Path to the debs 2013 data set as tar.gz
  "keyIndex": 0, // index of the key field
  "keySelect": 4, // filters the dataset for a specific key. -1 to take all
  "srcTimeScale": "ps", // time unit in the original data set
  "timeIndex": 1, // time index 
  "seperator": ",", // seperator in the dataset
  "startTime": 11043295594424116, 
  "endTime": 11053295594424116,
  "generatorConfigurations": [
    {
      "outOfOrder": 50, // percentage of out of order events
      "maxDelay": 2000, // maximal delay for out of order events
      "minDelay": 0 // minimal delay
    }
  ]
}

```

The data generator adds two time columns in ms, 1. processing time, 2. event time.
The out of order delay is normally distributed between minDelay and maxDelay.



### Verifier:
The verifier analyzes the data set and counts the number of out of order records.


```
events : 20490
outOfOrderEvents : 10330
outOfOrder % : 50.4148365056125
maxDelay: 1998
```

### Build & Start

1. Clone the repository.
2. execute  ```mvn package -DskipTests ```
3. Execute data generator  `` java -cp target/datagenerator-1.0-SNAPSHOT-jar-with-dependencies.jar de.tub.dima.scotty.datagenerator.DataGenerator config/debs_generate_out_of_order.json 
``
4. Execute verifier: ```java -cp target/datagenerator-1.0-SNAPSHOT-jar-with-dependencies.jar de.tub.dima.scotty.datagenerator.Verifier $Path to generated file$ $event time column$```
