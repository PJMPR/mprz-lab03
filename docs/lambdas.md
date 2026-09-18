# ➤ Lambdy w Javie – praktycznie (pod zadanie *Schedulers*)

## 🎯 Po co nam lambdy w tym projekcie?

W zadaniu korzystasz z **interfejsów funkcyjnych** jako „gniazdek” na zachowania:

* `IProvideNextExecutionTime` → lambda zwracająca **kolejny czas wykonania** (generator terminów).
* `IRunNotSafeAction` → lambda reprezentująca **zadanie do uruchomienia** (może rzucać wyjątek).
* Callbacki w `Scheduler`: `onError(throwable)`, `onSingleActionCompleted()`, `onCompleted()`.

---

## 1) Interfejs funkcyjny – definicja i adnotacja

```java
@FunctionalInterface
public interface IProvideNextExecutionTime {
    LocalDateTime provideTime();
}

@FunctionalInterface
public interface IRunNotSafeAction {
    void executeNotSafeAction() throws Exception;
}
```

> `@FunctionalInterface` gwarantuje dokładnie **jedną** metodę abstrakcyjną – wymóg dla lambd i referencji do metod.

---

## 2) Najprostsze lambdy i referencje do metod

```java
IProvideNextExecutionTime nowProvider = LocalDateTime::now; // referencja do metody statycznej
IProvideNextExecutionTime nowLambda   = () -> LocalDateTime.now();

IRunNotSafeAction boom = () -> { throw new Exception("ups"); };
IRunNotSafeAction methodRef = Main::randomlyThrowException; // referencja do metody statycznej z Main
```

---

## 3) Lambdy zależne od stanu (zamykanie/„closure”)

Lambdy **zamykają** (capturują) wartości zmiennych spoza swojego ciała – muszą być *efektywnie finalne*.

```java
Duration step = Duration.ofMillis(500); // efektywnie finalne
LocalDateTime[] next = { LocalDateTime.now() }; // mutowalny pojemnik

IProvideNextExecutionTime seq = () -> {
    LocalDateTime current = next[0];
    next[0] = current.plus(step);
    return current;
};
```

> Wzorzec z tablicą 1‑elementową pozwala utrzymać **stan** wewnątrz lambdy (przydaje się w `Chron`).

---

## 4) Budowniczy, który zwraca lambdę (metoda wytwórcza)

Tak jak w `Person.builder().buildPersonProvider()`, w `Chron` metoda `buildNextTimeExecutionProvider()` powinna **zbudować i zwrócić** lambdę `IProvideNextExecutionTime` z *własnym stanem*.

Schemat:

```java
public class Chron {
    private LocalDateTime start = LocalDateTime.now();
    private LocalDateTime end = null; // brak limitu
    private Duration interval = Duration.ofSeconds(1);
    private Integer maxTimes = null; // brak limitu

    public static Chron builder() { return new Chron(); }
    // ...settery łańcuchowe...

    public IProvideNextExecutionTime buildNextTimeExecutionProvider() {
        final LocalDateTime start0 = this.start;
        final LocalDateTime end0 = this.end;
        final Duration step0 = this.interval;
        final Integer max0 = this.maxTimes;

        final LocalDateTime[] cursor = { start0 };
        final int[] count = { 0 };

        return () -> {
            //algorytm do napisania dla Ciebie :)
        };
    }
}
```

> To **tylko schemat** – dopasuj do swojego API. Kluczowa myśl: **builder tworzy lambdę** z zamkniętym stanem.

---

## 5) Lambdy rzucające wyjątki i ich obsługa

Ponieważ `IRunNotSafeAction.executeNotSafeAction()` deklaruje `throws Exception`, możesz:

```java
IRunNotSafeAction risky = () -> {
    if (ThreadLocalRandom.current().nextInt(10) < 2) throw new Exception("fail");
    System.out.println("OK");
};
```

W `SchedulerThread` wywołujesz to bez „owijania”, ale **w bloku ********`try/catch`**:

```java
try {
    job.getAction().executeNotSafeAction();
    onSingleCompleted.run();
} catch (Exception ex) {
    onError.accept(ex);
}
```

---

## 6) Kompozycja lambd (proste dekoratory)

Chcesz dodać logowanie do akcji? Owiń lambdę w inną lambdę.

```java
static IRunNotSafeAction withLogging(IRunNotSafeAction action, String name) {
    return () -> {
        System.out.println("[START] " + name + " " + LocalDateTime.now());
        try { action.executeNotSafeAction(); }
        finally { System.out.println("[END]   " + name + " " + LocalDateTime.now()); }
    };
}

IRunNotSafeAction task = withLogging(Main::randomlyThrowException, "job#1");
```

To prosty przykład **dekoratora** na lambda‑zadaniu.

---

## 7) Referencje do metod – typy

* `ClassName::staticMethod` – np. `LocalDateTime::now`, `Main::randomlyThrowException`.
* `instance::method` – gdy masz obiekt, np. `logger::info`.
* `ClassName::new` – referencja do konstruktora, np. `ArrayList::new`.

Dopasowanie działa przez **typ docelowy** (interfejs funkcyjny). Kompilator sprawdza, czy sygnatura pasuje.

---

## 8) Lambdy a wątki – praktyka ze `SchedulerThread`

Upewnij się, że lambdy używane w zadaniach:

* nie trzymają **ciężkich referencji** (np. dużych kolekcji),
* są **bezpieczne wątkowo** (jeśli modyfikują wspólny stan, użyj synchronizacji lub ogranicz modyfikacje do wnętrza joba),
* nie blokują długo pętli schedulera (ew. przenieś wykonanie do oddzielnego `Thread`/`Executor`).

---

## 9) Minimalne przykłady „pod ręką”

**Provider czasu co 1s, 3 razy od teraz:**

```java
IProvideNextExecutionTime threeTicks = Chron.builder()
    .setStartTime(LocalDateTime.now())
    .setIntervalDuration(Duration.ofSeconds(1))
    .setMaxExecutionTimes(3)
    .buildNextTimeExecutionProvider();
```

**Zadanie, które czasem wybucha, z referencją do metody:**

```java
IRunNotSafeAction unstable = Main::randomlyThrowException;
```

**Obsługa błędów w Schedulerze (callback lambda):**

```java
scheduler
  .forAction(unstable)
  .useExecutionTimeProvider(threeTicks)
  .onError(ex -> System.out.println("BŁĄD: " + ex.getMessage()))
  .onSingleActionCompleted(() -> System.out.println("✓ ok"))
  .onCompleted(() -> System.out.println("koniec serii"))
  .Schedule();
```

---

## 10) Najczęstsze pułapki

* **Zmienna w lambdzie musi być efektywnie finalna** – nie modyfikuj jej po zdefiniowaniu lambdy.
* **Stan wewnętrzny** lambdy → trzymaj w **mutowalnym pojemniku** (tablica 1‑elem., `Atomic*`) lub zamknij w obiekcie.
* **Wyjątki** w lambdach bez `throws` trzeba „owijać” (tworząc własny interfejs funkcyjny **z ****`throws`** – jak `IRunNotSafeAction`).

---

## 📚 11) Najczęściej używane interfejsy funkcyjne w Javie

| Interfejs                 | Pakiet                 | Sygnatura metody            | Zwracany typ | Przykład użycia                                                           |
| ------------------------- | ---------------------- | --------------------------- | ------------ | ------------------------------------------------------------------------- |
| **`Supplier<T>`**         | `java.util.function`   | `T get()`                   | wartość      | `Supplier<LocalDateTime> now = LocalDateTime::now;`                       |
| **`Consumer<T>`**         | `java.util.function`   | `void accept(T t)`          | brak         | `Consumer<String> printer = s -> System.out.println(s);`                  |
| **`Function<T, R>`**      | `java.util.function`   | `R apply(T t)`              | wynik        | `Function<Integer, String> toText = n -> "Liczba: " + n;`                 |
| **`Predicate<T>`**        | `java.util.function`   | `boolean test(T t)`         | prawda/fałsz | `Predicate<Integer> isEven = n -> n % 2 == 0;`                            |
| **`BiConsumer<T, U>`**    | `java.util.function`   | `void accept(T t, U u)`     | brak         | `BiConsumer<String, Integer> info = (s, i) -> System.out.println(s + i);` |
| **`BiFunction<T, U, R>`** | `java.util.function`   | `R apply(T t, U u)`         | wynik        | `BiFunction<Integer, Integer, Integer> sum = (a, b) -> a + b;`            |
| **`Runnable`**            | `java.lang`            | `void run()`                | brak         | `Runnable r = () -> System.out.println("Hello");`                         |
| **`Callable<V>`**         | `java.util.concurrent` | `V call() throws Exception` | wynik        | `Callable<Double> random = Math::random;`                                 |

> 🧠 W Twoim zadaniu `IProvideNextExecutionTime` jest odpowiednikiem `Supplier<LocalDateTime>`, a `IRunNotSafeAction` – uproszczonym wariantem `Callable<Void>` z możliwością rzucania wyjątków.

---

**📘 Tip:** Możesz swobodnie wykorzystywać standardowe interfejsy funkcyjne Javy w swoim projekcie – działają identycznie jak Twoje, o ile ich sygnatura pasuje do oczekiwanego kontekstu.
