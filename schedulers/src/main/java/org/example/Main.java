package org.example;

import java.util.Random;

public class Main {
    public static void main(String[] args) {

        /**
         * Zanim zaczniesz pisać kod,
         * zajrzyj do kodu poniższej metody
         * i spróbuj zrozumieć o co w niej chodzi.
         * Jest tam przykład prostej implementacji
         * wzorca zwanego "metodą wytwórczą",
         * czasem zwaną "fabrykującą",
         * oraz prostej implementacji wzorca projektowego "budowniczego".
         *
         * zrozumienie implementacji tej metody
         * pomoże Tobie w rozwiązaniu następnych zadań.
         */
        checkThisOut();

//       System.out.println("[SEGMENT-1-OK]");
        
        /**
         * Utwórz nowy interfejs
         * z zadeklarowaną metodą "provideTime".
         * Jego implementacje później będą wykorzystywane
         * do określania, kiedy mają być wywoływane
         * konkretne operacje
         */
//        IProvideNextExecutionTime nextExecutionTimeProvider;
//       System.out.println("[SEGMENT-2-OK]");

        /**
         * dodaj adnotację @FunctionalInterface
         * nad nowoutworzonym interfejsem
         * aby zaznaczyć, że będzie on używany
         * z operatorem lambda.
         *
         * poniżej masz dwie przykładowe implementacje
         * tego interfejsu w postaci wyrażeń (metod) lambda
         */
//        nextExecutionTimeProvider = LocalDateTime::now;
//        nextExecutionTimeProvider = () -> LocalDateTime.now();
        
//       System.out.println("[SEGMENT-3-OK]");

        /**
         * Twoim pierwszym zadaniem jest napisanie
         * klasy o nazwie "Chron",
         * która jest przykładem prostej implementacji
         * wzorca projektowego "budowniczego".
         * Budowniczy w tym przykładzie zwraca metodę
         * wytwórczą, która ma być implementacją wcześniej utworzonego interfejsu.
         *
         * Chodzi o taką implemntację tych wzorców,
         * gdzie można ustawić:
         *      -> setStartTime: godzinę startu (domyślnie teraz)
         *      -> setEndTime: godzinę końca (domyślnie nigdy),
         *      -> setMaxExecutionTimes: ilość powtórzeń wykonania
         *          (czyli ile kolejnych godzin\czasów ma zwrócić - domyślnie ma wykonywać w nieskończoność),
         *      -> setIntervalDuration: odstęp czasowy między kolejnymi godzinami/ czasami (domyślnie 1 sekunda)
         *
         * Metoda buildNextTimeExecutionProvider ma zwracać lambdę,
         * która generuje kolejną godzinę według wcześniej podanych parametrów
         * UAWAGA !
         * Najlepiej zrobić tak, aby klasa Chron implementowała "buildera" (budowniczego) - w sensie nie robić buildera jako odrębnej klasy
         */
//        IProvideNextExecutionTime startsNowFor5SecondsMax5TimesWithDurationOf500Millis = Chron.builder()
//                .setStartTime(LocalDateTime.now())
//                .setEndDate(LocalDateTime.now().plusSeconds(5))
//                .setMaxExecutionTimes(5)
//                .setIntervalDuration(Duration.ofMillis(500))
//                .buildNextTimeExecutionProvider();

//        System.out.println(startsNowFor5SecondsMax5TimesWithDurationOf500Millis.provideTime()); // now
//        System.out.println(startsNowFor5SecondsMax5TimesWithDurationOf500Millis.provideTime()); // now+0.5s
//        System.out.println(startsNowFor5SecondsMax5TimesWithDurationOf500Millis.provideTime()); // now+1s
//        System.out.println(startsNowFor5SecondsMax5TimesWithDurationOf500Millis.provideTime()); // now+1.5s
//        System.out.println(startsNowFor5SecondsMax5TimesWithDurationOf500Millis.provideTime()); // now+2s
//        System.out.println(startsNowFor5SecondsMax5TimesWithDurationOf500Millis.provideTime()); // null
//       System.out.println("[SEGMENT-4-OK]");

        /**
         * Super udało się - jestem pod wielkim wrażeniem ;-]
         * W dalszej części programu bedziemy potrzebowali
         * kolejnego interfejsu, który będzie wykorzystywany
         * jako lambda, która może rzucić wyjątkiem (błędem)
         */
//        IRunNotSafeAction throwAnError = ()->{ throw new Exception(); };


//       System.out.println("[SEGMENT-5-OK]");        
//        try {
//            throwAnError.executeNotSafeAction();
//            System.out.println("tutaj powinien wystąpić błąd, a nie wystąpił :(");
//            return;
//            }catch (Exception ex){}

        
//       System.out.println("[SEGMENT-6-OK]");

        /**
         * wykorzystajmy metodę,
         * która co jakiś czas rzuca błedem
         * jako implementacja powyższego interfejsu
         */
//        IRunNotSafeAction randomlyThrowsAnError = () -> randomlyThrowException();
        /* albo inaczej: */
//        IRunNotSafeAction randomlyThrowsAnErrorMethodReference = Main::randomlyThrowException;

//       System.out.println("[SEGMENT-7-OK]");
        
        /**
         * Jeśli myslałeś, że poprzednie zadanie było łatwe,
         * to ciekawe co powiesz o tym...
         *
         * Teraz mamy zdefiniowene dwa interfejsy:
         * 1. IProvideNextExecutionTime - implementacja zwraca kolejną godzinę,
         *    według ustawień podanych w builderze
         * 2. IRunNotSafeAction - implementacje tego interfejsu będą definiować zadanie,
         *    które będzie wykonywane przez harmonogram
         *
         * Czas na zaimplementowanie klasy Scheduler,
         * która to właśnie będzie odpowiadać za wykonywanie zadań,
         * o konkretnych porach.
         *
         * Ta klasa będzie mixem 2 wzorców projektowych:
         * 1. singletonu - chcemy, aby był tylko jeden harmonogram,
         *   w którym będą przechowywane informacje o wszystkich zadaniach jakie mają być wykonane (Implementacje IRunNotSafeAction),
         *   oraz o metodzie lambda która zwraca kolejne czasy wykonania danego zadania (implementacja IProvideNextExecutionTime)
         * 2. budowniczego:
         *      -> forAction: metoda do definiowania zadania do wykonania
         *      -> onError: metoda która przyjmuje lambdę, która to z kolei jako parametr ma przyjąć wyjątek, i jakoś go obsłużyć
         *      -> onSingleActionCompleted: przyjmuje lambdę, która definiuje co ma się stać po wykonaniu pojednyczego zadania
         *      -> onCompleted: przyjmuje lambdę, która definiuje co ma się stać gdy wszystkie zadania danego typu się zakończą
         *      -> schedule: zapisuje wszystkie powyższe dane do pewnej kolekcji
         *
         *  UWAGA!
         *  W tym zadaniu pewnie będziesz musiał napisac kilka własnych klas/ inetrfejsów pomocniczych,
         *  których nie spotkasz w tym miejscu (tzn. w ciele funkcji main, w której aktualnie się znajdujemy)
         */
//        Scheduler scheduler = Scheduler.getInstance();
//         scheduler
//                .forAction(randomlyThrowsAnError)
//                .useExecutionTimeProvider(startsNowFor5SecondsMax5TimesWithDurationOf500Millis)
//                .onError(ex->handleException(ex))
//                .onSingleActionCompleted(()->System.out.println("wykonano akcje z powodzeniem"))
//                .onCompleted(()->System.out.println("Zakończyłem pracę"))
//                .Schedule();
        
//       System.out.println("[SEGMENT-8-OK]");

        /**
         * Jeżeli już tutaj się znalazłeś i samemu rozwiązałeeś powyższe zadania,
         * to wiedz, że drzemie w Tobie duży potencjał - tak trzymać !
         *
         * No to mamy już możliwość tworzenia harmonogramów zadań
         * teraz przyda się nam jakiś wątek, który będzie mógł uruchamiać te zadania,
         * w tle działania aplikacji
         * Utwórz klasę SchedulerThread,
         * która implementuje interfejs Runnable (ten interfejs jest utworzony w frameworku JAVY),
         * w taki sposób, że bierze zadania z singletonu Schedule'ra i odpala je o konkretnych porach.
         *
         * Np. co sekunde z kolekcji zadań sprawdza czas wykonania zadania
         *      i jeśli czas na te zadanie właśnie mija/minał,
         *      a zadanie się jeszcze nie wykonało,
         *      to je wykonuje.
         */
//        Runnable schedulerThread = new SchedulerThread();
//
//        new Thread(schedulerThread).start();
        
//       System.out.println("[SEGMENT-9-OK]");

        /**
         * na zakończenie sprawdźmy co się stanie,
         * jeśli 'zbuduję' jeszcze jedno zadanie
         * i dodam je do Schedulera
         */
//        scheduler.forAction(()->System.out.println("chyba zaczynam to rozumieć"))
//                .useExecutionTimeProvider(Chron.builder().setMaxExecutionTimes(1).buildNextTimeExecutionProvider())
//                .onCompleted(()->System.out.println("Nie wierzę... działa!"))
//                .Schedule();

//       System.out.println("[SEGMENT-10-OK]");
        /**
         * to jest tylko po to aby main sam nie kończyl się wykonywać.
         */
        runForever();

    }

    private static void handleException(Throwable ex) {
        System.out.println("Wystąpił błąd :(");
    }


    static void randomlyThrowException() throws Exception {
        int nextInt = new Random().nextInt(10);
        if(nextInt<2){
            System.out.println("O nie... coś się popsuło");
            throw new Exception(":(");
        }
        System.out.println("Działam sobie normalnie :)");
        Thread.sleep(nextInt*100);
    }

    static void runForever(){

        new Thread(()->{
            var counter = 0;
            while (true){
                try {
                    Thread.sleep(1000);
                    counter++;
                    if(counter==30){
                        System.exit(0);
                    }
                }catch (Exception ignored){}
            }
        }).start();
    }

    static void checkThisOut(){

        IProvide<Person> janKowalskiProvider = Person.builder()
                .setName("Jan")
                .setSurname("Kowalski")
                .buildPersonProvider();

        Person janKowalski = janKowalskiProvider.provide();

    }

}

class Person{
    private String name;
    private String surname;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public static PersonBuilder builder(){
        return new PersonBuilder();
    }
}

interface IProvide<T>{
    T provide();
}

class PersonBuilder{
    String name, surname;

    public PersonBuilder setName(String name){
        this.name = name;
        return this;
    }

    public PersonBuilder setSurname(String surname){
        this.surname = surname;
        return this;
    }

    public IProvide<Person> buildPersonProvider()
    {
        return ()->{
            Person p = new Person();
            p.setName(name);
            p.setSurname(surname);
            return p;
        };
    }

}
