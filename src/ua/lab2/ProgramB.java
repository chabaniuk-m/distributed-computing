package ua.lab2;

import java.util.Random;
import java.util.concurrent.*;

public class ProgramB {
    private Random random = new Random(System.currentTimeMillis());
    // consumer-producer paradigm
    private BlockingDeque<Integer> takenOutOfWarehouse = new LinkedBlockingDeque<>();
    private BlockingDeque<Integer> loadedOnTheTruck = new LinkedBlockingDeque<>();
    private int price = 0;
    private int nItems;

    public ProgramB(int nItems) {
        this.nItems = nItems;
    }

    private final Runnable ivanov = () -> {
        for (int i = 0; i < nItems; i++) {
            try {
                int item;
                synchronized (random) {
                    item = random.nextInt(100) + 1;
                }
                System.out.println("Іванов виніс зі складу річ " + item + "-го виду");
                takenOutOfWarehouse.put(item);
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    private final Runnable petrov = () -> {
        for (int i = 0; i < nItems; i++) {
            try {
                Integer loaded = takenOutOfWarehouse.take();
                Thread.sleep(1000);
                System.out.println("Петров завантажив на вантажівку річ " + loaded + "-го виду");
                loadedOnTheTruck.put(loaded);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    private final Runnable nechiporchuk = () -> {
        for (int i = 0; i < nItems; i++) {
            try {
                int item = loadedOnTheTruck.take();
                Thread.sleep(1500);
                price += item * 100;
                System.out.println("Нечипорчук порахував вартість речі " + item + "-го виду");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Загальна вартість викрадених речей - " + price + " грн");
    };

    private void begin() {
        ExecutorService executor = Executors.newCachedThreadPool();

        executor.submit(ivanov);
        executor.submit(petrov);
        executor.submit(nechiporchuk);

        executor.shutdown();
    }

    public static void main(String[] args) {
        new ProgramB(5).begin();
    }
}