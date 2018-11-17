package services.impl;

import services.BusStopInterface;

public class BusStopImpl implements BusStopInterface {

    private String name;

    public BusStopImpl(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
