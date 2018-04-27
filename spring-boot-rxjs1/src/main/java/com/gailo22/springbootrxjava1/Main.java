package com.gailo22.springbootrxjava1;

import com.gailo22.springbootrxjava1.domain.Address;
import javafx.util.Pair;
import lombok.Builder;
import lombok.Data;
import org.springframework.util.StopWatch;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.schedulers.Schedulers;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {

//        CountDownLatch latch = new CountDownLatch(1);
//        Observable<List<Integer>> observableLocal = Observable.just(Arrays.asList(1,2,3,4,5));
//        Observable<List<String>> observableRemote = Observable.just(Arrays.asList("a","b","c","d","e"));
//        Observable.zip(observableLocal, observableRemote, (a, b) -> {
//            System.out.println(a);
//            System.out.println(b);
//            latch.countDown();
//            return String.format("%s, %s", a, b);
//        }).subscribe(System.out::println);

        List<Task> hardTasks = Arrays.asList(
                new Task("a", true),
                new Task("b1", true),
                new Task("b2", false),
                new Task("c", true),
                new Task("d", true)
        );
        List<Task> softTasks = Arrays.asList(
                new Task("e", true),
                new Task("f", true),
                new Task("g", false),
                new Task("h", true),
                new Task("i", true)
        );

        List<Task> applyHardTasks = hardTasks.stream()
                .filter(x -> x.isApply()).collect(Collectors.toList());
        List<Task> applySoftTasks = softTasks.stream()
                .filter(x -> x.isApply()).collect(Collectors.toList());

        List<String> steps = sequential(applyHardTasks);
        steps.addAll(parallel(applySoftTasks));

        System.out.println(steps);
    }

    private static List<String> parallel(List<Task> applySoftTasks) {
        List<String> steps = new ArrayList<>();
        StopWatch stopWatch2 = new StopWatch();
        stopWatch2.start();

        Map<String, Observable<String>> collect1 = applySoftTasks.parallelStream()
                .collect(Collectors.toMap(
                        x -> x.name,
                        x -> x.apply()));
        CountDownLatch latch1 = new CountDownLatch(collect1.size());
        for (Map.Entry<String, Observable<String>> o : collect1.entrySet()) {
            String key = o.getKey();
            o.getValue().subscribe(x -> {
                steps.add(String.format("%s: SUCCESS", key));
                latch1.countDown();
            }, t -> {
                steps.add(String.format("%s: ERROR", key));
                latch1.countDown();
            });
        }

        try {
            latch1.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        stopWatch2.stop();
        System.out.println("time2: " + stopWatch2.getTotalTimeMillis());

        return steps;
    }

    private static List<String> sequential(List<Task> applyHardTasks) {
        Map<String, Observable<String>> collect = applyHardTasks.stream()
                .collect(Collectors.toMap(x -> x.name,
                        x -> x.apply(),
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new));

        List<String> steps = new ArrayList<>();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Stack<Task> completedTasks = new Stack<>();
        for (Task task : applyHardTasks) {
            try {
                task.apply().toBlocking().subscribe();
                steps.add(String.format("%s: SUCCESS", task.name));
                completedTasks.push(task);
            } catch (Exception e) {
                steps.add(String.format("%s: ERROR", task.name));
                rollback(completedTasks);
                break;
            }
        }
//        for (Map.Entry<String, Observable<String>> o : collect.entrySet()) {
//            String key = o.getKey();
//            try {
//                o.getValue().toBlocking().subscribe();
//                steps.add(String.format("%s: SUCCESS", key));
//            } catch (Exception e) {
//                steps.add(String.format("%s: ERROR", key));
//                break;
//            }
//        }
        stopWatch.stop();
        System.out.println("time1: " + stopWatch.getTotalTimeMillis());
        return steps;
    }

    private static void rollback(Stack<Task> completedTasks) {
        while (!completedTasks.isEmpty()) {
            Task pop = completedTasks.pop();
            pop.rollback();
        }
    }

    static class Task {
        private boolean isApply;
        private String name;
        Task(String name, boolean isApply) {
            this.name = name;
            this.isApply = isApply;
        }
        public boolean isApply() {
            return isApply;
        }
        public void rollback() {
            System.out.println(name + ": rollback");
        }
        public Observable<String> apply() {
            Observable<String> observable = Observable.fromCallable(
                    () -> {
                        try {
                            TimeUnit.SECONDS.sleep(3);
                            System.out.println(name + ": " + Thread.currentThread().getName());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if ("c".equals(name) || "f".equals(name)) throw new RuntimeException("error: " + name);
                        return name;
                    }
            ).subscribeOn(Schedulers.io());
            return observable;
        }

    }
}
