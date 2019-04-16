package de.tub.dima.scotty.datagenerator;

public class TimeUtil {

    public static long scaleTimeToMS(String timeScale, long timeValue) {
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

    public static long scaleTimeToS(String timeScale, long timeValue) {
        return scaleTimeToMS(timeScale, timeValue) / 1000;
    }
}
