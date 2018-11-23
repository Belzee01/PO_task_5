package services.impl;

import services.BusInterface;

import java.util.Objects;

public class BusImpl implements BusInterface {
    private final int number;

    public BusImpl(int number) {
        this.number = number;
    }

    @Override
    public int getBusNumber() {
        return number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        BusImpl bus = (BusImpl) o;
        return number == bus.number;
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }

    @Override
    public String toString() {
        return String.valueOf(this.number);
    }
}
