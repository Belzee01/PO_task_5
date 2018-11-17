package services.impl;

import services.BusInterface;

public class BusImpl implements BusInterface {

    private int busNumber;

    public BusImpl(int busNumber) {
        this.busNumber = busNumber;
    }

    @Override
    public int getBusNumber() {
        return busNumber;
    }
}
