package services.impl;

import services.BusLineInterface;
import services.BusStopInterface;

import java.util.LinkedList;

public class BusLineImpl implements BusLineInterface {

    private LinkedList<BusStopInterface> busStops;

    public BusLineImpl(LinkedList<BusStopInterface> busStops) {
        this.busStops = busStops;
    }

    @Override
    public int getNumberOfBusStops() {
        return this.busStops.size();
    }

    @Override
    public BusStopInterface getBusStop(int number) {
        return this.busStops.get(number);
    }
}
