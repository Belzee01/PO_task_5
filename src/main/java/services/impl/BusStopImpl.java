package services.impl;

import services.BusStopInterface;

import java.util.Objects;

public class BusStopImpl implements BusStopInterface {
    private final String busName;

    public BusStopImpl(String busName) {
        this.busName = busName;
    }

    @Override
    public String getName() {
        return busName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        BusStopImpl busStop = (BusStopImpl) o;
        return Objects.equals(busName, busStop.busName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(busName);
    }

    @Override
    public String toString() {
        return "BS{" + busName + '}';
    }
}
