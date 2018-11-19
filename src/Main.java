import services.BusLineInterface;
import services.BusStopInterface;
import services.impl.BusImpl;
import services.impl.BusLineImpl;
import services.impl.BusStopImpl;
import services.impl.PathFinder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        PathFinder finder = new PathFinder();

        List<BusStopInterface> busStops = new ArrayList<>(
                Arrays.asList(
                        new BusStopImpl("A"), //0
                        new BusStopImpl("B"), //1
                        new BusStopImpl("C"), //2
                        new BusStopImpl("D"), //3

                        new BusStopImpl("E"), //4
                        new BusStopImpl("F"), //5
                        new BusStopImpl("G"), //6
                        new BusStopImpl("H"), //7

                        new BusStopImpl("I"), //8
                        new BusStopImpl("J"), //9
                        new BusStopImpl("K"), //10
                        new BusStopImpl("L") //11
                )
        );

        BusLineInterface a1 = new BusLineImpl(new LinkedList<BusStopInterface>(
                Arrays.asList(
                        busStops.get(0), //A
                        busStops.get(1), //B
                        busStops.get(2), //C
                        busStops.get(3) //D
                )
        ), "a1");
        BusLineInterface b2 = new BusLineImpl(new LinkedList<>(
                Arrays.asList(
                        busStops.get(4), //E
                        busStops.get(5), //F
                        busStops.get(2), //C
                        busStops.get(6) //G
                )
        ), "b2");
        BusLineInterface c3 = new BusLineImpl(new LinkedList<>(
                Arrays.asList(
                        busStops.get(7), //H
                        busStops.get(8), //I
                        busStops.get(6), //G
                        busStops.get(9) //J
                )
        ), "c3");

        BusLineInterface d4 = new BusLineImpl(new LinkedList<>(
                Arrays.asList(
                        busStops.get(10), //K
                        busStops.get(5), //F
                        busStops.get(11), //L
                        busStops.get(3), //D
                        busStops.get(9) //J
                )
        ), "d4");


        finder.addLine(a1, new BusImpl(1));
        finder.addLine(b2, new BusImpl(2));
        finder.addLine(c3, new BusImpl(3));
        finder.addLine(d4, new BusImpl(4));


        finder.find(busStops.get(0), busStops.get(3), 3);
        System.out.println(finder.getNumerOfSolutions());
        finder.getSolutions().forEach(s -> System.out.println(s.toString()));

        finder.find(busStops.get(0), busStops.get(4), 0);
        System.out.println(finder.getNumerOfSolutions());
    }
}
