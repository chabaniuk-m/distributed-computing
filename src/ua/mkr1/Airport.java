package ua.mkr1;

import java.util.Random;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.Semaphore;

// VARIANT - 11

public class Airport {
    public static final int nDestinations = 6;
    public static final int nRunways = 3;
    public static final Semaphore sem = new Semaphore(nRunways);
    private static final Random random = new Random(System.currentTimeMillis());

    public static void main(String[] args) {
        BlockingDeque<Passanger> deque = new LinkedBlockingDeque<>();
        while (true) {
            for (int i = 0; i < random.nextInt(600); i++) {
                if (i % 100 != 0) {
                    deque.add(new Passanger());
                }
            }
            Thread plane = new Thread(new Plane(
                    deque,
                    random.nextInt(5) + 6,
                    random.nextInt(nDestinations),
                    random.nextInt(200) + 301
            ));
            plane.setDaemon(true);
            plane.start();
        }
    }
}
