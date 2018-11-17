package services.impl;

import services.BusInterface;
import services.BusLineInterface;
import services.BusStopInterface;
import services.PathFinderInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PathFinder implements PathFinderInterface {

    private List<Void> solutions;

    private Map<BusLineInterface, BusInterface> busLineGridMap;
    private Map<BusLineInterface, List<BusLineInterface>> intersectionsMap;

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
                            .add(second);
                    this.intersectionsMap
                            .computeIfAbsent(second, k -> new ArrayList<>())
                            .add(first);
                }
            }
        }
    }

    @Override
    public void find(BusStopInterface from, BusStopInterface to, int transfers) {

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
}
