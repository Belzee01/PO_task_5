package services;

public interface BusLineInterface {
    /**
     * Metoda zwraca liczbÄ przystankĂłw, ktĂłre wchodzÄ w jej skĹad.
     *
     * @return liczba przystankĂłw danej lini
     */
    public int getNumberOfBusStops();

    /**
     * Metoda zwraca obiekt reprezentujÄcy przystanek o podanym numerze. PrawidĹowe
     * numery przystanĂłw mieszczÄ siÄ w przedziale od 0 do getNumberOfBusStops()-1.
     * Tylko podanie bĹednego numeru przystanku spowoduje zwrĂłcenie null.
     *
     * @param number numer przystanku
     * @return obiekt reprezentujÄcy przystanek o numerze number
     */
    public BusStopInterface getBusStop(int number);
}