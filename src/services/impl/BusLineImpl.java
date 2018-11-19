package services.impl;

import services.BusLineInterface;
import services.BusStopInterface;

import java.util.LinkedList;

public class BusLineImpl implements BusLineInterface {

    private LinkedList<BusStopInterface> busStops;
    private String name;

    public BusLineImpl(LinkedList<BusStopInterface> busStops) {
        this.busStops = busStops;
    }

    public BusLineImpl(LinkedList<BusStopInterface> busStops, String name) {
        this.busStops = busStops;
        this.name = name;
    }

    @Override
    public int getNumberOfBusStops() {
        return this.busStops.size();
    }

    @Override
    public BusStopInterface getBusStop(int number) {
        return this.busStops.get(number);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
