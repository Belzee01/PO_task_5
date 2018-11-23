import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import services.BusInterface;
import services.BusLineInterface;
import services.BusStopInterface;
import services.PathFinderInterface;
import services.impl.PathFinder;

import java.text.DecimalFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.fail;

public class PMO_Test {
    //////////////////////////////////////////////////////////////////////////
    private static final Map<String, Double> tariff = new HashMap<>();

    static {
        tariff.put("oneSimpleSolution", 1.0);
        tariff.put("noSolutionNoTransfer", 1.0);
        tariff.put("noSolutionTooManyTransfers", 1.0);
        tariff.put("solutionWithTransfer", 1.0);
        tariff.put("noSolutionWithTransfers", 1.0);
        tariff.put("solutionWith2Transfers", 1.0);
        tariff.put("solutionWith2TransfersReverse", 1.0);
        tariff.put("testWith2Transfers2Solutions", 0.5);
        tariff.put("solutionWith3Transfers", 0.5);
    }

    public static double getTariff(String testName) {
        return tariff.get(testName);
    }

    //////////////////////////////////////////////////////////////////////////

    private static DecimalFormat myFormatter = new DecimalFormat("00.#");

    private PathFinderInterface pathFinder;
    private Map<Integer, List<BusStopInterface>> lines;
    private Map<Integer, List<BusAndBusStop>> solutionsMap;
    private Map<Integer, List<BusAndBusStop>> expectedSolutionsMap;
    private Map<String, BusStopInterface> allBusStops;

    private static void showException(Exception e, String txt) {
        e.printStackTrace();
        fail("W trakcie pracy metody " + txt + " doszĹo do wyjÄtku " + e.toString());
    }

    @BeforeEach
    public void create() {
        pathFinder = new PathFinder();
        lines = new TreeMap<>();
        solutionsMap = new TreeMap<>();
        expectedSolutionsMap = new TreeMap<>();
        allBusStops = new TreeMap<>();
    }

    private class BusStop implements BusStopInterface {
        private final String busName;

        private BusStop(String busName) {
            this.busName = busName;
            allBusStops.put(busName, this);
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
            BusStop busStop = (BusStop) o;
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

    private class Bus implements BusInterface {
        private final int number;

        private Bus(int number) {
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
            Bus bus = (Bus) o;
            return number == bus.number;
        }

        @Override
        public int hashCode() {
            return Objects.hash(number);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }

    private class BusLine implements BusLineInterface {

        private final List<BusStopInterface> line;

        private BusLine(List<BusStopInterface> line) {
            this.line = line;
        }

        @Override
        public int getNumberOfBusStops() {
            return line.size();
        }

        @Override
        public BusStopInterface getBusStop(int number) {
            if ((number < 0) || (number >= line.size())) {
                fail("WywoĹano getBusStop z bĹÄdnym numerem przystanku");
            }
            return line.get(number);
        }

        @Override
        public String toString() {
            return "BL{" + line + '}';
        }
    }

    private class BusAndBusStop {
        final int bus;
        final String busStop;

        BusAndBusStop(BusInterface bus, BusStopInterface busStop) {
            this(bus.getBusNumber(), busStop);
        }

        BusAndBusStop(int bus, BusStopInterface busStop) {
            this.bus = bus;
            assertNotNull(busStop, "Zamiast przystanku jest null");
            this.busStop = busStop.getName();
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

    private String stringPlusNumber(String txt, int number) {
        return txt + "_" + myFormatter.format(number);
    }

    private List<BusStopInterface> createLine(String commonName, int stops) {
        return IntStream.range(0, stops).mapToObj(i -> new BusStop(stringPlusNumber(commonName, i)))
                .collect(Collectors.toList());
    }

    private void connect2lines(int busID1, int busID2, int line1idx, int line2idx, String name) {
        BusStopInterface commonBusStop = new BusStop(name);
        lines.get(busID1).add(line1idx, commonBusStop);
        lines.get(busID2).add(line2idx, commonBusStop);
    }

    private List<BusStopInterface> getSublineExclusive(int lineNumber, BusStopInterface first, BusStopInterface last) {
        List<BusStopInterface> line = lines.get(lineNumber);
        return getSublineExclusive(lineNumber, line.indexOf(first), line.indexOf(last));
    }

    private List<BusStopInterface> getSublineExclusive(int lineNumber, int firstIdx, int lastIdx) {
        List<BusStopInterface> line = lines.get(lineNumber);
        return line.subList(firstIdx + 1, lastIdx);
    }

    private void execute(Runnable code2execute, String txt) {
        try {
            assertTimeoutPreemptively(Duration.ofMillis(500), code2execute::run);
        } catch (Exception e) {
            showException(e, txt);
        }
    }

    private void executeAddLine(BusLineInterface line, BusInterface bus) {
        execute(() -> {
            pathFinder.addLine(line, bus);
        }, "executeAddLine(" + bus + " -> " + line + ")");
    }

    private void executeFind(BusStopInterface from, BusStopInterface to, int transfers) {
        execute(() -> {
            pathFinder.find(from, to, transfers);
        }, "executeFind(" + from + "- " + transfers + " ->" + to);
    }

    private void addLines() {
        lines.forEach((b, l) -> {
            executeAddLine(new BusLine(l), new Bus(b));
        });
    }

    private BusStopInterface get(int line, int busStop) {
        return lines.get(line).get(busStop);
    }

    private BusStopInterface get(String name) {
        return allBusStops.get(name);
    }

    private List<BusAndBusStop> downloadSolution(int solution) {
        List<BusAndBusStop> result = null;
        try {
            int stops = pathFinder.getBusStops(solution);

            result = IntStream.range(0, stops)
                    .mapToObj(
                            i -> new BusAndBusStop(pathFinder.getBus(solution, i), pathFinder.getBusStop(solution, i)))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            showException(e, "W trakcie pobierania rozwiÄzania " + solution + " doszĹo do bĹÄdu");
        }
        return result;
    }

    private void downloadSolutions() {
        try {
            int solutions = pathFinder.getNumerOfSolutions();
            IntStream.range(0, solutions).forEach(solution -> solutionsMap.put(solution, downloadSolution(solution)));
        } catch (Exception e) {
            showException(e, "W trakcie pobierania rozwiÄzaĹ");
        }
    }

    private void checkIfExists(int solution) {
        if (!expectedSolutionsMap.containsKey(solution)) {
            expectedSolutionsMap.put(solution, new ArrayList<>());
        }
    }

    private void expectedSolutionsMapBuild(int solution, int bus, BusStopInterface busStop) {
        checkIfExists(solution);
        expectedSolutionsMap.get(solution).add(new BusAndBusStop(bus, busStop));
    }

    private void expectedSolutionsMapBuild(int solution, int bus, List<BusStopInterface> busStops) {
        checkIfExists(solution);

        busStops.forEach(busStop -> expectedSolutionsMap.get(solution).add(new BusAndBusStop(bus, busStop)));
    }

    private void testNumberOfSolutions(int expected) {
        assertEquals(expected, solutionsMap.size(), "Oczekiwana byĹa inna liczba rozwiÄzaĹ");
    }

    private boolean testAny(List<BusAndBusStop> expectedSolution) {
        return solutionsMap.entrySet().stream().anyMatch(s -> s.getValue().equals(expectedSolution));
    }

    private void testSolutions() {
        boolean solutionsOK = expectedSolutionsMap.entrySet().stream().allMatch(s -> testAny(s.getValue()));

        if (!solutionsOK) {
            fail("Nie odnaleziono wszystkich oczekiwanych rozwiÄzaĹ.\n" + "Oczekiwano: " + expectedSolutionsMap + "\n"
                    + "Jest:" + solutionsMap);
        }
    }

    private void createLine100and200() {
        lines.put(100, createLine("LiniaA", 10));
        lines.put(200, createLine("LiniaB", 10));
        addLines();
    }

    private void create3lines() {
        lines.put(100, createLine("LiniaA", 10));
        lines.put(200, createLine("LiniaB", 10));
        lines.put(300, createLine("LiniaC", 10));

        connect2lines(100, 300, 3, 3, "A-C");
        connect2lines(200, 300, 3, 7, "B-C");

        addLines();
    }

    private void create4lines() {
        lines.put(100, createLine("LiniaA", 12));
        lines.put(200, createLine("LiniaB", 12));
        lines.put(300, createLine("LiniaC", 12));
        lines.put(400, createLine("LiniaD", 12));

        connect2lines(100, 300, 3, 3, "A-C");
        connect2lines(100, 400, 9, 2, "A-D");
        connect2lines(200, 300, 3, 8, "B-C");
        connect2lines(200, 400, 7, 8, "B-D");

        addLines();
    }

    /////////////// 2 linie

    @Test
    @DisplayName("Test dla 2 linii i istniejÄcych rozwiÄzaniach")
    public void oneSimpleSolution() {
        createLine100and200();

        BusStopInterface p1 = get(100, 2);
        BusStopInterface p2 = get(100, 8);

        expectedSolutionsMapBuild(0, 100, p1);
        expectedSolutionsMapBuild(0, 100, getSublineExclusive(100, 2, 8));
        expectedSolutionsMapBuild(0, 100, p2);

        executeFind(p1, p2, 0);
        downloadSolutions();
        testNumberOfSolutions(1);
        testSolutions();
    }

    @Test
    @DisplayName("Test dla 2 linii i braku rozwiÄzania - linie nie sÄ poĹÄczone")
    public void noSolutionNoTransfer() {
        createLine100and200();

        BusStopInterface p1 = get(100, 2);
        BusStopInterface p2 = get(200, 2);

        executeFind(p1, p2, 0);
        downloadSolutions();
        testNumberOfSolutions(0);
    }

    @Test
    @DisplayName("Test dla 2 linii i braku rozwiÄzania - ĹźÄdanie zbyt wielu przesiadek")
    public void noSolutionTooManyTransfers() {
        createLine100and200();

        BusStopInterface p1 = get(100, 2);
        BusStopInterface p2 = get(100, 8);

        executeFind(p1, p2, 1);
        downloadSolutions();
        testNumberOfSolutions(0);
    }

    /////////////////////////// 3 linie

    @Test
    @DisplayName("Test dla 3 linii i jednej przesiadki")
    public void solutionWithTransfer() {
        create3lines();

        BusStopInterface p1 = get(100, 1);
        BusStopInterface p2 = get(300, 9);
        BusStopInterface p3 = get("A-C");

        expectedSolutionsMapBuild(0, 100, p1);
        expectedSolutionsMapBuild(0, 100, getSublineExclusive(100, p1, p3));
        expectedSolutionsMapBuild(0, 300, p3);
        expectedSolutionsMapBuild(0, 300, getSublineExclusive(300, p3, p2));
        expectedSolutionsMapBuild(0, 300, p2);

        executeFind(p1, p2, 1);
        downloadSolutions();
        testNumberOfSolutions(1);
        testSolutions();
    }

    @Test
    @DisplayName("Test dla 3 linii i 2 przesiadek")
    public void solutionWith2Transfers() {
        create3lines();

        BusStopInterface p1 = get(100, 1);
        BusStopInterface p2 = get(200, 9);
        BusStopInterface p3 = get("A-C");
        BusStopInterface p4 = get("B-C");

        expectedSolutionsMapBuild(0, 100, p1);
        expectedSolutionsMapBuild(0, 100, getSublineExclusive(100, p1, p3));
        expectedSolutionsMapBuild(0, 300, p3);
        expectedSolutionsMapBuild(0, 300, getSublineExclusive(300, p3, p4));
        expectedSolutionsMapBuild(0, 200, p4);
        expectedSolutionsMapBuild(0, 200, getSublineExclusive(200, p4, p2));
        expectedSolutionsMapBuild(0, 200, p2);

        executeFind(p1, p2, 2);
        downloadSolutions();
        testNumberOfSolutions(1);
        testSolutions();
    }

    @Test
    @DisplayName("Test dla 3 linii i 2 przesiadek")
    public void solutionWith2TransfersReverse() {
        create3lines();

        BusStopInterface p1 = get(100, 1);
        BusStopInterface p2 = get(200, 9);
        BusStopInterface p3 = get("A-C");
        BusStopInterface p4 = get("B-C");

        expectedSolutionsMapBuild(0, 100, p1);
        expectedSolutionsMapBuild(0, 100, getSublineExclusive(100, p1, p3));
        expectedSolutionsMapBuild(0, 100, p3);
        expectedSolutionsMapBuild(0, 300, getSublineExclusive(300, p3, p4));
        expectedSolutionsMapBuild(0, 300, p4);
        expectedSolutionsMapBuild(0, 200, getSublineExclusive(200, p4, p2));
        expectedSolutionsMapBuild(0, 200, p2);

        Collections.reverse(expectedSolutionsMap.get(0));

        executeFind(p2, p1, 2);
        downloadSolutions();
        testNumberOfSolutions(1);
        testSolutions();
    }


    @Test
    @DisplayName("Test dla 3 linii i 0, 3, 4 przesiadkami")
    public void noSolutionWithTransfers() {
        create3lines();

        BusStopInterface p1 = get(100, 1);
        BusStopInterface p2 = get(300, 9);

        executeFind(p1, p2, 0);
        downloadSolutions();
        testNumberOfSolutions(0);
        executeFind(p1, p2, 3);
        downloadSolutions();
        testNumberOfSolutions(0);
        executeFind(p1, p2, 4);
        downloadSolutions();
        testNumberOfSolutions(0);
    }


    @Test
    @DisplayName("Test dla 4 linii i 2 przesiadek i 2 rozwiÄzaĹ")
    public void testWith2Transfers2Solutions() {
        create4lines();

        BusStopInterface p1 = get(100, 1);
        BusStopInterface p2 = get(200, 12);
        BusStopInterface pAC = get("A-C");
        BusStopInterface pAD = get("A-D");
        BusStopInterface pBC = get("B-C");
        BusStopInterface pBD = get("B-D");

        expectedSolutionsMapBuild(0, 100, p1);
        expectedSolutionsMapBuild(0, 100, getSublineExclusive(100, p1, pAC));
        expectedSolutionsMapBuild(0, 300, pAC);
        expectedSolutionsMapBuild(0, 300, getSublineExclusive(300, pAC, pBC));
        expectedSolutionsMapBuild(0, 200, pBC);
        expectedSolutionsMapBuild(0, 200, getSublineExclusive(200, pBC, p2));
        expectedSolutionsMapBuild(0, 200, p2);

        expectedSolutionsMapBuild(1, 100, p1);
        expectedSolutionsMapBuild(1, 100, getSublineExclusive(100, p1, pAD));
        expectedSolutionsMapBuild(1, 400, pAD);
        expectedSolutionsMapBuild(1, 400, getSublineExclusive(400, pAD, pBD));
        expectedSolutionsMapBuild(1, 200, pBD);
        expectedSolutionsMapBuild(1, 200, getSublineExclusive(200, pBD, p2));
        expectedSolutionsMapBuild(1, 200, p2);

        executeFind(p1, p2, 2);
        downloadSolutions();
        testNumberOfSolutions(2);
        testSolutions();
    }

    @Test
    @DisplayName("Test dla 4 linii i 3 przesiadki")
    public void solutionWith3Transfers() {
        create4lines();

        BusStopInterface p1 = get(300, 1);
        BusStopInterface p2 = get(200, 12);
        BusStopInterface pAC = get("A-C");
        BusStopInterface pAD = get("A-D");
        BusStopInterface pBD = get("B-D");

        expectedSolutionsMapBuild(0, 300, p1);
        expectedSolutionsMapBuild(0, 300, getSublineExclusive(300, p1, pAC));
        expectedSolutionsMapBuild(0, 100, pAC);
        expectedSolutionsMapBuild(0, 100, getSublineExclusive(100, pAC, pAD));
        expectedSolutionsMapBuild(0, 400, pAD);
        expectedSolutionsMapBuild(0, 400, getSublineExclusive(400, pAD, pBD));
        expectedSolutionsMapBuild(0, 200, pBD);
        expectedSolutionsMapBuild(0, 200, getSublineExclusive(200, pBD, p2));
        expectedSolutionsMapBuild(0, 200, p2);

        executeFind(p1, p2, 3);
        downloadSolutions();
        testNumberOfSolutions(1);
        testSolutions();
    }

    /////////////////////////////////////////////////////////////////////////////////////////////

}