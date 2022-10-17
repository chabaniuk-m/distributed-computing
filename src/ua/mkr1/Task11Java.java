package ua.mkr1;

import java.util.Random;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.Semaphore;

public class Task11Java {
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

class Passanger {
    private static Random random = new Random(System.currentTimeMillis());
    public final int destination = random.nextInt() % Task11Java.nDestinations;
}

class Plane implements Runnable {
    private final long begin = System.currentTimeMillis();
    private final BlockingDeque<Passanger> deque;
    private final int waitTime;     // in milliseconds
    private final int destination;
    private int freePlaces;

    public Plane(BlockingDeque<Passanger> deque, int waitTime, int destination, int freePlaces) {
        this.deque = deque;
        this.waitTime = waitTime;
        this.destination = destination;
        this.freePlaces = freePlaces;
    }

    @Override
    public void run() {
        while (System.currentTimeMillis() - begin < waitTime) {
            if (!deque.isEmpty()) {
                var p = deque.pop();
                if (p.destination != destination) {
                    deque.addFirst(p);
                } else {
                    freePlaces--;
                    if (freePlaces == 0) {
                        break;
                    }
                }
            }
        }
        flyOut();
    }

    private void flyOut() {
        try {
            Task11Java.sem.acquire();
            Thread.sleep(5);
            Task11Java.sem.release();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}