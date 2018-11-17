package services.impl;

import services.BusInterface;
import services.BusLineInterface;
import services.BusStopInterface;
import services.PathFinderInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PathFinder implements PathFinderInterface {

    private List<Solution> solutions;

    private Map<BusLineInterface, BusInterface> busLineGridMap;
    private Map<BusLineInterface, List<Pair>> intersectionsMap;

    public PathFinder() {
        this.solutions = new ArrayList<>();
        this.busLineGridMap = new HashMap<>();
        this.intersectionsMap = new HashMap<>();
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
                            .computeIfAbsent(first, k -> new ArrayList<>())
                            .add(new Pair(second, first.getBusStop(i)));
                    this.intersectionsMap
                            .computeIfAbsent(second, k -> new ArrayList<>())
                            .add(new Pair(first, first.getBusStop(i)));
                }
            }
        }
    }

    @Override
    public void find(BusStopInterface from, BusStopInterface to, int transfers) {

        this.solutions.clear();

        List<BusLineInterface> startingBusLine = findBusLineByBusStop(from);
        List<BusLineInterface> destinationBusLine = findBusLineByBusStop(to);

        Optional<BusLineInterface> directConnectionSolution = startingBusLine.stream()
                .filter(destinationBusLine::contains)
                .findFirst();

        if (directConnectionSolution.isPresent())
            this.solutions.add(new Solution());

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

    public static class Pair {
        private BusLineInterface busLine;
        private BusStopInterface busStop;

        public Pair(BusLineInterface busLine, BusStopInterface busStop) {
            this.busLine = busLine;
            this.busStop = busStop;
        }

        public BusLineInterface getBusLine() {
            return busLine;
        }

        public BusStopInterface getBusStop() {
            return busStop;
        }
    }

    public static class Solution {

        public int getBusStops() {
            return 0;
        }
    }
}
