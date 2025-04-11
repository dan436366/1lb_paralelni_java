package org.example;

import java.util.Scanner;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        System.out.print("Enter number of threads: ");
        int threadCount = scanner.nextInt();

        int[] steps = new int[threadCount];
        int[] delays = new int[threadCount];

        for (int i = 0; i < threadCount; i++) {
            steps[i] = random.nextInt(5) + 1;
            delays[i] = (random.nextInt(5) + 1) * 1000;
        }

        SumThread[] workers = new SumThread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            workers[i] = new SumThread(i + 1, steps[i]);
            workers[i].start();
        }

        ControllerThread controller = new ControllerThread(workers, delays);
        controller.start();

        scanner.close();
    }
}

class ControllerThread extends Thread {
    private final SumThread[] threads;
    private final int[] delays;

    public ControllerThread(SumThread[] threads, int[] delays) {
        this.threads = threads;
        this.delays = delays;
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        boolean[] stopped = new boolean[threads.length];

        while (true) {
            long currentTime = System.currentTimeMillis();
            boolean allStopped = true;

            for (int i = 0; i < threads.length; i++) {
                if (!stopped[i]) {
                    allStopped = false;
                    if (currentTime - startTime >= delays[i]) {
                        threads[i].stopRunning();
                        stopped[i] = true;
                    }
                }
            }

            if (allStopped) {
                break;
            }
        }

        for (SumThread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                System.err.println("Cant wait to close thread");
            }
        }
    }
}

class SumThread extends Thread {
    private final int id;
    private final int step;
    volatile private boolean running = true;

    private long sum = 0;
    private long count = 0;

    public SumThread(int id, int step) {
        this.id = id;
        this.step = step;
    }

    @Override
    public void run() {
        int current = 0;
        while (running) {
            sum += current;
            current += step;
            count++;
        }
        System.out.printf("Thread #%d sum = %d, addends = %d%n", id, sum, count);
    }

    public void stopRunning() {
        running = false;
    }
}

