package services;

public interface PathFinderInterface {
    /**
     * Metoda dodaje liniÄ autobusowÄ do serwisu. Ten sam autobus
     * obsĹuguje liniÄ w obu kierunkach.
     * @param line linia autobusowa
     * @param bus autobus, ktĂłry jÄ obsĹuguje
     */
    public void addLine( BusLineInterface line, BusInterface bus );

    /**
     * Metoda zleca znalezienie poĹÄczenia autobusowego
     * prowadzÄcego od przystanku from do przystanku to
     * z uwzglÄdnieniem podanej liczby przesiadek.
     * Liczba przesiadek rĂłwna zero oznacza, Ĺźe poszukiwane jest
     * poĹÄczenie bezpoĹrednie.
     * @param from przystanek poczÄtkowy
     * @param to przystanek koĹcowy
     * @param transfers liczba przesiadek
     */
    public void find( BusStopInterface from, BusStopInterface to, int transfers );

    /**
     * Liczba odnalezionych rozwiÄzaĹ.
     * @return liczba rozwiÄzaĹ. Przed wykonaniem metody find
     * metoda zwraca zawsze 0.
     */
    public int getNumerOfSolutions();

    /**
     * Liczba przystankĂłw autobusowych naleĹźÄcych do rozwiÄzania
     * o podanym numerze. Przystanek o numerze 0 to przystanek, od
     * ktĂłrego rozpoczynana jest podrĂłĹź (from). Przystanek o numerze
     * getNumerOfSolutions()-1 to przystanek koĹcowy (to).
     * @param solution numer rozwiÄzania
     * @return liczba przystankĂłw.
     */
    public int getBusStops( int solution );

    /**
     * Metoda zwraca przystanek o numerze busStop w rozwiÄzaniu
     * o numerze solution.
     * @param solution numer rozwiÄzania
     * @param busStop numer przystanku w obrÄbie danego rozwiÄzania
     * @return przystanek o podanych numerach identyfikacyjnych
     */
    public BusStopInterface getBusStop( int solution, int busStop );

    /**
     * Dla wszystkich przystankĂłw poza ostatnim, metoda zwraca autobus, ktĂłry
     * obsĹuguje poĹÄczenie z przystanku o numerze busStop do nastÄpnego.
     * Dla przystanku ostatniego, autobus, ktĂłry obsĹugiwaĹ przejazd
     * z przystanku busStop-2 do busStop-1 (czyli ostatniego).
     * @param solution numer rozwiÄzania
     * @param busStop numer przystanku w obrÄbie danego rozwiÄzania
     * @return autobus, ktĂłrym pasaĹźer odjeĹźdĹźa z danego przystanku lub
     * w przypadku przystanku docelowego, autobus, z ktĂłrego pasaĹźer
     * na tym przystanku wysiadĹ.
     */
    public BusInterface getBus( int solution, int busStop );
}