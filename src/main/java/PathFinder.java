import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Stack;

public class PathFinder implements PathFinderInterface {

    private List<Solution> solutions;

    private Map<BusLineInterface, BusInterface> busLineGridMap;
    private Map<BusLineInterface, List<Pair>> intersectionsMap;

    private Stack<Pair> stack;

    public PathFinder() {
        this.solutions = new ArrayList<>();
        this.busLineGridMap = new HashMap<>();
        this.intersectionsMap = new HashMap<>();
        this.stack = new Stack<>();
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
                            .add(new Pair(second, first.getBusStop(i))); //revert to version with bus line -> intersection bus stop
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

        if (transfers < 1) {
            Optional<BusLineInterface> directConnectionSolution = startingBusLine.stream()
                    .filter(destinationBusLine::contains)
                    .findFirst();

            // przystanki na tej samej lini bez przesiadek
            directConnectionSolution.ifPresent(busLineInterface -> this.solutions
                    .add(
                            new Solution()
                                    .add(new Pair(busLineInterface, from))
                                    .add(new Pair(busLineInterface, to))
                    )
            );
        } else {
            startingBusLine.forEach(sbl -> {
                destinationBusLine.forEach(dbl -> {
                    bruteForceToDestination(new Pair(sbl, from), new Pair(dbl, to), transfers, 0);
                    this.stack.pop();
                });
            });

        }

        this.solutions.forEach(Solution::normalizeToBusStops);
    }

    private boolean bruteForceToDestination(Pair start, Pair destination, int transfers, final int currentTransferCount) {
        if (start == null)
            return false;

        this.stack.add(start);

        if (currentTransferCount > transfers) {
            return false;
        }

        if (start.getBusLine().equals(destination.getBusLine()) && currentTransferCount == transfers) {
            return true;
        }

        final int ctc = currentTransferCount + 1;

        List<Pair> pairs = this.intersectionsMap.get(start.getBusLine());

        if (pairs == null || pairs.isEmpty())
            return false;

        pairs.stream()
                .filter(p -> !stack.contains(p))
                .forEach(p -> {
                    boolean isFound = bruteForceToDestination(p, destination, transfers, ctc);
                    if (isFound) {
                        this.solutions.add(new Solution(this.stack).add(destination));
                    }
                    this.stack.pop();
                });
        return false;
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
        return this.solutions.get(solution).getSolutions().get(busStop).getBusStop();
    }

    @Override
    public BusInterface getBus(int solution, int busStop) {
        return this.solutions.get(solution).getSolutions().get(busStop).getBus();
    }

    public List<Solution> getSolutions() {
        return solutions;
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

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            Pair p = (Pair) o;
            return busLine == p.busLine || busStop == p.busStop;
        }

        @Override
        public int hashCode() {
            return Objects.hash(busLine);
        }

        @Override
        public String toString() {
            return this.busStop.toString() + " : " + this.busLine.toString();
        }
    }

    public static class BusAndBusStop {
        private BusInterface bus;
        private BusStopInterface busStop;

        public BusAndBusStop(BusInterface bus, BusStopInterface busStop) {
            this.bus = bus;
            this.busStop = busStop;
        }

        public BusInterface getBus() {
            return bus;
        }

        public BusStopInterface getBusStop() {
            return busStop;
        }

        @Override
        public String toString() {
            return "{" + bus + ", " + busStop + '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            BusAndBusStop that = (BusAndBusStop) o;
            return bus == that.bus && Objects.equals(busStop, that.busStop);
        }

        @Override
        public int hashCode() {
            return Objects.hash(bus, busStop);
        }
    }

    public class Solution {

        private Stack<Pair> busStops;
        private LinkedList<BusAndBusStop> solutions;

        public Solution() {
            this.busStops = new Stack<>();
        }

        public Solution(Stack<Pair> busStops) {
            this.busStops = new Stack<>();
            this.busStops.addAll(busStops);
        }

        public Solution add(Pair busStop) {
            this.busStops.add(busStop);
            return this;
        }

        public int indexOf(BusLineInterface line, BusStopInterface stop) {
            int index = -1;
            for (int i = 0; i < line.getNumberOfBusStops(); i++) {
                if (line.getBusStop(i).equals(stop)) {
                    index = i;
                    break;
                }
            }
            return index;
        }

        public void normalizeToBusStops() {
            LinkedList<BusAndBusStop> buses = new LinkedList<>();

            for (int i = 1; i < this.busStops.size(); i++) {
                BusStopInterface prevStop = busStops.get(i - 1).getBusStop();
                BusLineInterface prevLine = busStops.get(i - 1).getBusLine();
                BusStopInterface nextStop = busStops.get(i).getBusStop();

                int prevStopIndex = indexOf(prevLine, prevStop);
                int nextStopIndex = indexOf(prevLine, nextStop);

                int direction = prevStopIndex - nextStopIndex;
                if (direction < 0) {
                    for (int j = prevStopIndex; j <= nextStopIndex; j++) {
                        buses.add(new BusAndBusStop(busLineGridMap.get(prevLine), prevLine.getBusStop(j)));
                    }
                } else {
                    for (int j = prevStopIndex; j >= nextStopIndex; j--) {
                        buses.add(new BusAndBusStop(busLineGridMap.get(prevLine), prevLine.getBusStop(j)));
                    }
                }
            }

            if (!buses.isEmpty()) {
                for (int i = 1; i < buses.size(); i++) {
                    if (buses.get(i).getBusStop().equals(buses.get(i - 1).getBusStop()))
                        buses.remove(i - 1);
                }
            }

            this.solutions = buses;
        }

        // get index of the busStop
        // check direction by substracting the indexes e.g. BS:  A -> BS:C ; indexes : 0 -> 2
        // 2 - 0 = 2, it is positive so it goes in order
        // from C -> A; 2 -> 0
        // 0 - 2 = -2 negative, so reverse order
        // by on the direction run findBysStopsInBetween(line, from, to)
        // add each busStop, omit already added busStops
        private LinkedList<BusAndBusStop> findBusStopInBetween(BusLineInterface line, BusStopInterface from, BusStopInterface to) {
            LinkedList<BusAndBusStop> stops = new LinkedList<>();
            boolean passed = false;
            for (int i = 0; i < line.getNumberOfBusStops(); i++) {
                if (line.getBusStop(i) == from || line.getBusStop(i) == to)
                    passed = !passed;

                if (passed)
                    stops.add(new BusAndBusStop(busLineGridMap.get(line), line.getBusStop(i)));
            }

            passed = false;
            for (int i = line.getNumberOfBusStops() - 1; i >= 0; i--) {
                if (line.getBusStop(i) == from || line.getBusStop(i) == to)
                    passed = !passed;

                if (passed)
                    stops.add(new BusAndBusStop(busLineGridMap.get(line), line.getBusStop(i)));
            }
            return stops;
        }

        public int getBusStops() {
            return this.solutions.size();
        }

        public LinkedList<BusAndBusStop> getSolutions() {
            return solutions;
        }

        @Override
        public String toString() {
            return this.busStops.toString();
        }
    }
}
