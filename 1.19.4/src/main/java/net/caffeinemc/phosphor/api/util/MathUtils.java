package net.caffeinemc.phosphor.api.util;

import java.util.concurrent.ThreadLocalRandom;

public class MathUtils {
    public static int getRandomInt(int from, int to) {
        return ThreadLocalRandom.current().nextInt(from, to + 1);
    }

    public static double getRandomDouble(double from, double to) {
        return ThreadLocalRandom.current().nextDouble(from, to);
    }

    public static boolean getRandomBoolean() {
        return ThreadLocalRandom.current().nextBoolean();
    }
}
