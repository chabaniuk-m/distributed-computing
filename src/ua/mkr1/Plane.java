package ua.mkr1;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class Plane implements Runnable {
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
            Airport.sem.acquire();
            Thread.sleep(5);
            Airport.sem.release();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
