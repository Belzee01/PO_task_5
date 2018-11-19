package services.impl;

import services.BusInterface;
import services.BusLineInterface;
import services.BusStopInterface;
import services.PathFinderInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;

public class PathFinder implements PathFinderInterface {

    private List<Solution> solutions;

    private Map<BusLineInterface, BusInterface> busLineGridMap;
    private Map<BusLineInterface, Set<BusLineInterface>> intersectionsMap;

    private Stack<BusLineInterface> stack;

    public PathFinder() {
        this.solutions = new ArrayList<>();
        this.busLineGridMap = new HashMap<>();
        this.intersectionsMap = new HashMap<>();
        this.stack = new Stack<>();
    }

    @Override
    public void addLine(BusLineInterface line, BusInterface bus) {
        this.busLineGridMap.forEach((key, value) -> findIntersection(key, line));
        this.busLineGridMap.put(line, bus);
    }

    private void findIntersection(BusLineInterface first, BusLineInterface second) {
        for (int i = 0; i < first.getNumberOfBusStops(); i++) {
            for (int j = 0; j < second.getNumberOfBusStops(); j++) {
                if (first.getBusStop(i).getName().equals(second.getBusStop(j).getName())) {
                    this.intersectionsMap
                            .computeIfAbsent(first, k -> new HashSet<>())
                            .add(second);
                    this.intersectionsMap
                            .computeIfAbsent(second, k -> new HashSet<>())
                            .add(first);
                }
            }
        }
    }

    @Override
    public void find(BusStopInterface from, BusStopInterface to, int transfers) {

        this.solutions.clear();

        List<BusLineInterface> startingBusLine = findBusLineByBusStop(from);
        List<BusLineInterface> destinationBusLine = findBusLineByBusStop(to);

        if (transfers < 1) {
            Optional<BusLineInterface> directConnectionSolution = startingBusLine.stream()
                    .filter(destinationBusLine::contains)
                    .findFirst();

            // przystanki na tej samej lini bez przesiadek
            directConnectionSolution.ifPresent(busLineInterface -> this.solutions.add(new Solution().add(busLineInterface)));
        } else {
            startingBusLine.forEach(sbl -> {
                destinationBusLine.forEach(dbl -> {
                    bruteForceToDestination(sbl, dbl, transfers, 0);
                });

            });

        }
    }

    private boolean bruteForceToDestination(BusLineInterface start, BusLineInterface destination, int transfers, final int currentTransferCount) {
        if (start == null)
            return false;

        this.stack.add(start);

        if (currentTransferCount > transfers) {
            this.stack.pop();

            return false;
        }

        if (start.equals(destination) && currentTransferCount == transfers) {
            return true;
        }

        final int ctc = currentTransferCount + 1;

        this.intersectionsMap.get(start).stream()
                .filter(p -> !stack.contains(p))
                .forEach(p -> {
                    boolean isFound = bruteForceToDestination(p, destination, transfers, ctc);
                    if (isFound)
                        this.solutions.add(new Solution(this.stack));
                    this.stack.pop();
                });
        this.stack.pop();
        return false;
    }

    private List<BusLineInterface> findBusLineByBusStop(BusStopInterface busStop) {
        List<BusLineInterface> busLines = new ArrayList<>();
        this.busLineGridMap.forEach((key, value) -> {
            for (int i = 0; i < key.getNumberOfBusStops(); i++) {
                if (key.getBusStop(i).equals(busStop))
                    busLines.add(key);
            }
        });

        return busLines;
    }

    @Override
    public int getNumerOfSolutions() {
        return this.solutions.size();
    }

    @Override
    public int getBusStops(int solution) {
        return this.solutions.get(solution).getBusStops();
    }

    @Override
    public BusStopInterface getBusStop(int solution, int busStop) {
        return null;
    }

    @Override
    public BusInterface getBus(int solution, int busStop) {
        return null;
    }

    public List<Solution> getSolutions() {
        return solutions;
    }

    public static class Solution {

        private Stack<BusLineInterface> busStops;

        public Solution() {
            this.busStops = new Stack<>();
        }

        public Solution(Stack<BusLineInterface> busStops) {
            this.busStops = new Stack<>();
            this.busStops.addAll(busStops);
        }

        public Solution add(BusLineInterface busStop) {
            this.busStops.add(busStop);
            return this;
        }

        public Solution addAll(Stack<BusLineInterface> busStops) {
            this.busStops.addAll(busStops);
            return this;
        }

        public int getBusStops() {
            return 0;
        }

        @Override
        public String toString() {
            return this.busStops.toString();
        }
    }
}
