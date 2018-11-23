package services.impl;

import services.BusLineInterface;
import services.BusStopInterface;

import java.util.List;

public class BusLineImpl implements BusLineInterface {

    private final List<BusStopInterface> line;

    private String busLineName;

    public BusLineImpl(List<BusStopInterface> line, String name) {
        this.line = line;
        this.busLineName = name;
    }

    @Override
    public int getNumberOfBusStops() {
        return line.size();
    }

    @Override
    public BusStopInterface getBusStop(int number) {
        if ((number < 0) || (number >= line.size())) {
            return null;
        }
        return line.get(number);
    }

    @Override
    public String toString() {
        return this.busLineName + " : BL{" + line + '}';
    }
}