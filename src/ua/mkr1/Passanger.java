package ua.mkr1;

import java.util.Random;

public class Passanger {
    private static Random random = new Random(System.currentTimeMillis());
    public final int destination = random.nextInt() % Airport.nDestinations;
}
