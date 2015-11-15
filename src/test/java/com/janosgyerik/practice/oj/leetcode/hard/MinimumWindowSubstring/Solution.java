package com.janosgyerik.practice.oj.leetcode.hard.MinimumWindowSubstring;

import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public class Solution {
    public String minWindow(String s, String t) {
        String min = "";

        Tracker tracker = new Tracker(s, t);

        for (char c : s.toCharArray()) {
            tracker.add(c);
            if (tracker.isWindowComplete()) {
                String window = tracker.getWindow();
                if (min.isEmpty() || window.length() < min.length()) {
                    min = window;
                }
                tracker.advance();
            }
        }
        return min;
    }

    private static class Tracker {
        private final String source;

        private final Map<Integer, Long> targetCounts;
        private final Map<Integer, Long> runningCounts = new HashMap<>();
        private final PriorityQueue<CharPos> heap = new PriorityQueue<>((p1, p2) -> Integer.compare(p1.pos, p2.pos));
        private int pos = -1;

        private static class CharPos {
            private final char c;
            private final int pos;

            private CharPos(char c, int pos) {
                this.c = c;
                this.pos = pos;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) {
                    return true;
                }
                if (o == null || getClass() != o.getClass()) {
                    return false;
                }

                CharPos charPos = (CharPos) o;

                if (c != charPos.c) {
                    return false;
                }
                if (pos != charPos.pos) {
                    return false;
                }

                return true;
            }

            @Override
            public int hashCode() {
                int result = (int) c;
                result = 31 * result + pos;
                return result;
            }

            @Override
            public String toString() {
                return "(" + c + ", " + pos + ")";
            }
        }

        public Tracker(String source, String target) {
            this.source = source;
            targetCounts = target.chars()
                    .boxed()
                    .collect(groupingBy(Function.identity(), counting()));
        }

        void add(char c) {
            ++pos;
            Long count = targetCounts.get((int) c);
            if (count == null) {
                return;
            }
            if (count.equals(runningCounts.get((int) c))) {
                List<CharPos> copy = new ArrayList<>(heap.size() - 1);
                while (!heap.isEmpty()) {
                    CharPos item = heap.poll();
                    if (item.c == c) {
                        break;
                    }
                    copy.add(item);
                }
                copy.addAll(heap);
                heap.clear();
                heap.addAll(copy);
            } else {
                Long runningCount = runningCounts.get((int) c);
                if (runningCount == null) {
                    runningCounts.put((int) c, 1L);
                } else {
                    runningCounts.put((int) c, runningCount + 1);
                }
            }
            heap.add(new CharPos(c, pos));
        }

        boolean isWindowComplete() {
            return runningCounts.equals(targetCounts);
        }

        public String getWindow() {
            int start = heap.peek().pos;
            int end = pos + 1;
            return source.substring(start, end);
        }

        public void advance() {
            CharPos first = heap.poll();
            runningCounts.put((int) first.c, runningCounts.get((int) first.c) - 1);
        }
    }
}