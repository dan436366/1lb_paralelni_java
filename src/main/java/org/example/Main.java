package org.example;

import java.util.Scanner;
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter number of threads: ");
        int numThreads = scanner.nextInt();

        System.out.print("Enter step of threads: ");
        int step = scanner.nextInt();

        Worker[] workers = new Worker[numThreads];
        Thread[] threads = new Thread[numThreads];

        for (int i = 0; i < numThreads; i++) {
            workers[i] = new Worker(i + 1, step);
            threads[i] = new Thread(workers[i]);
            threads[i].start();
        }

        new Thread(() -> {
            for (int i = 0; i < numThreads; i++) {
                try {
                    Thread.sleep(1000);
                    workers[i].stop();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

class Worker implements Runnable {
    private final int id;
    private final int step;
    private boolean running = true;

    private long sum = 0;
    private int count = 0;

    public Worker(int id, int step) {
        this.id = id;
        this.step = step;
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        int current = 0;

        while (running) {
            sum += current;
            count++;
            current += step;

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        System.out.printf("Thread %d : sum = = %d, addends = %d\n", id, sum, count);
    }
}
