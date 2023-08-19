package net.caffeinemc.phosphor.api.util;

import java.util.concurrent.ThreadLocalRandom;

public class MathUtils {
    public static int getRandomInt(int from, int to) {
        if (from >= to) return from;
        return ThreadLocalRandom.current().nextInt(from, to + 1);
    }

    public static double getRandomDouble(double from, double to) {
        if (from >= to) return from;
        return ThreadLocalRandom.current().nextDouble(from, to);
    }

    public static double getAverage(double int1, double int2) {
        return (int1 + int2) / 2;
    }

    public static boolean getRandomBoolean() {
        return ThreadLocalRandom.current().nextBoolean();
    }
}
