package com.janosgyerik.practice.oj.codejam.codejam2017.round1c.AmpleSyrup;

import com.janosgyerik.practice.oj.codejam.codejam2017.common.Answer;
import com.janosgyerik.practice.oj.codejam.codejam2017.common.Input;
import com.janosgyerik.practice.oj.codejam.codejam2017.common.Inputs;
import com.janosgyerik.practice.oj.codejam.codejam2017.common.Problem;
import com.janosgyerik.practice.oj.codejam.codejam2017.common.Runner;
import com.janosgyerik.practice.oj.codejam.codejam2017.common.Solver;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import lombok.Value;

import static java.util.Comparator.comparingDouble;

public class AmpleSyrup implements Problem {
    @Value
    static class Pancake {
        final int radius;
        final int height;

        Pancake(int radius, int height) {
            this.radius = radius;
            this.height = height;
        }

        double top() {
            return radius * radius * Math.PI;
        }

        double side() {
            return 2 * radius * Math.PI * height;
        }
    }

    @Override
    public Inputs inputs(Scanner scanner) {
        Inputs inputs = new Inputs();

        int t = scanner.nextInt();
        for (int i = 0; i < t; i++) {
            int n = scanner.nextInt();
            int k = scanner.nextInt();

            List<Pancake> pancakes = new ArrayList<>(n);

            for (int j = 0; j < n; j++) {
                int r = scanner.nextInt();
                int h = scanner.nextInt();
                pancakes.add(new Pancake(r, h));
            }
            inputs.add(new AmpleSyrupInput(pancakes, k));
        }
        return inputs;
    }

    static class AmpleSyrupInput implements Input {
        private final List<Pancake> pancakes;
        private final int k;

        AmpleSyrupInput(List<Pancake> pancakes, int k) {
            this.pancakes = pancakes;
            this.k = k;
        }
    }

    @Override
    public Solver solver(Inputs inputs) {
        return new AmpleSyrupSolver();
    }

    static class AmpleSyrupSolver implements Solver {
        @Override
        public Answer solve(Input input0) {
            AmpleSyrupInput input = (AmpleSyrupInput) input0;

            return new AmpleSyrupAnswer(findMaxSurface(input));
        }
    }

    private static double findMaxSurface(AmpleSyrupInput input) {
        input.pancakes.sort(Collections.reverseOrder(comparingDouble(Pancake::side)));
        return input.pancakes.stream().mapToDouble(p -> calculateValueForPivot(input, p)).max().getAsDouble();
    }

    private static double calculateValueForPivot(AmpleSyrupInput input, Pancake pivot) {
        List<Pancake> pancakes = input.pancakes.stream().filter(p -> p.radius <= pivot.radius).collect(Collectors.toList());
        if (pancakes.size() < input.k) {
            return 0;
        }
        pancakes.remove(pivot);
        return pivot.top() + pivot.side() + pancakes.stream().limit(input.k - 1).mapToDouble(Pancake::side).sum();
    }

    static class AmpleSyrupAnswer implements Answer {
        final String surface;
        final NumberFormat numberFormat = new DecimalFormat("#.#########");

        AmpleSyrupAnswer(double surface) {
            this.surface = numberFormat.format(surface);
        }

        @Override
        public String value() {
            return surface;
        }
    }

    public static void main(String[] args) throws IOException {
        Runner runner = Runner.create(new AmpleSyrup());
//        runner.run("dummy.in", true);
        // fails :(
        runner.run("A-small-practice.in", true);
    }
}