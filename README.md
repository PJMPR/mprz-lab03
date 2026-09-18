# 🚀 JAZ – Lab 03: **Schedulers**

> 🎯 **Cel:** Twoim zadaniem jest stworzenie zestawu klas, które sprawią, że wszystkie zakomentowane fragmenty w `Main.java` zaczną działać poprawnie po ich odkomentowaniu.
>

---
📘 Uwaga dla studentów: zanim przystąpisz do implementacji poszczególnych segmentów w Main.java, zapoznaj się z poniższymi materiałami pomocniczymi w kolejności:

1️⃣ Lambdy w Javie – praktycznie (pod zadanie Schedulers) – zanim zaczniesz pisać interfejsy IProvideNextExecutionTime i IRunNotSafeAction.

2️⃣ Wzorce projektowe – Budowniczy i Singleton – zanim rozpoczniesz implementację klas Chron i Scheduler.

3️⃣ Wątki w Javie – przed napisaniem klasy SchedulerThread.

4️⃣ Diagram klas – rozwiązanie – jako wizualna mapa zależności po zrozumieniu powyższych dokumentów.

---
## 🧩 0️⃣ Zanim zaczniesz – zrozum przykład `checkThisOut()`

📘 Metoda `checkThisOut()` pokazuje:

* 🧱 **Wzorzec budowniczego (builder)** – `Person.builder()...buildPersonProvider()`
* ⚙️ **Metodę wytwórczą (factory method)** – `IProvide<Person>` zwracającą gotowy obiekt.

Zrozumienie tego przykładu ułatwi Ci implementację klas `Chron` i `Scheduler`.

---

## 🕒 1️⃣ Stwórz interfejs `IProvideNextExecutionTime`

🔹 Oznacz go adnotacją `@FunctionalInterface`
🔹 Zdefiniuj metodę:

```java
LocalDateTime provideTime();
```

🔹 Gdy nie ma już więcej terminów – metoda może zwracać `null`.

💡 Po tym kroku możesz odkomentować:

```java
nextExecutionTimeProvider = LocalDateTime::now;
```
 i

```java
nextExecutionTimeProvider = () -> LocalDateTime.now();
```

---

## 🧠 2️⃣ Zaimplementuj klasę–builder `Chron`

🎯 `Chron` ma budować funkcję (lambdę), która zwraca kolejne czasy wykonania.

### 🔧 API (dopasuj nazwy do `Main.java`):

* `static Chron builder()`
* `setStartTime(LocalDateTime)` 🕐 – domyślnie `LocalDateTime.now()`
* `setEndDate(LocalDateTime)` ⏰ – domyślnie brak końca
* `setMaxExecutionTimes(int)` 🔁 – domyślnie nieskończoność
* `setIntervalDuration(Duration)` ⏳ – domyślnie 1 sekunda
* `buildNextTimeExecutionProvider()` → zwraca `IProvideNextExecutionTime`

📈 Zachowanie:

* Pierwsze `provideTime()` → `startTime`
* Kolejne → poprzedni + `interval`
* Zatrzymaj, jeśli:

  * osiągnięto `maxExecutionTimes`, lub
  * przekroczono `endDate`
* Po zakończeniu zwróć `null`.

✅ Po tym kroku odkomentuj blok:

```java
Chron.builder()
  .setStartTime(...)
  .setEndDate(...)
  .setMaxExecutionTimes(...)
  .setIntervalDuration(...)
  .buildNextTimeExecutionProvider();
```

---

## ⚡ 3️⃣ Utwórz interfejs `IRunNotSafeAction`

🧨 Ten interfejs reprezentuje akcję, która **może rzucać wyjątek**.

```java
@FunctionalInterface
public interface IRunNotSafeAction {
    void executeNotSafeAction() throws Exception;
}
```

✅ Po tym kroku powinny działać linie z:

```java
IRunNotSafeAction throwAnError = () -> { throw new Exception(); };
IRunNotSafeAction randomlyThrowsAnError = () -> randomlyThrowException();
IRunNotSafeAction randomlyThrowsAnErrorMethodReference = Main::randomlyThrowException;
```

---

## 🧮 4️⃣ Zaimplementuj klasę `Scheduler`

🏗️ Łączy dwa wzorce: **Singleton** + **Builder dla zadań**.

### 🧩 Kluczowe elementy:

1. **Singleton:**

   ```java
   public static Scheduler getInstance() { ... }
   ```
2. **Builder API:**

   * `forAction(IRunNotSafeAction action)`
   * `useExecutionTimeProvider(IProvideNextExecutionTime provider)`
   * `onError(Consumer<Throwable> handler)`
   * `onSingleActionCompleted(Runnable hook)`
   * `onCompleted(Runnable hook)`
   * `Schedule()` – rejestruje zadanie

🧾 Scheduler powinien przechowywać listę obiektów np. `ScheduledJob`, z polami:

* `IRunNotSafeAction action`
* `IProvideNextExecutionTime timeProvider`
* `LocalDateTime nextRunAt`
* Callbacki: `onError`, `onCompleted`, `onSingleActionCompleted`

💡 Walidacja: jeśli brakuje wymaganych pól → rzuć wyjątek przy `Schedule()`.

---

## 🔁 5️⃣ Utwórz klasę `SchedulerThread`

🧵 Ma uruchamiać zadania w tle.

### 🧠 Logika pętli:

1. Co np. 500 ms → przejrzyj listę jobów.
2. Dla każdego:

   * jeśli `now >= nextRunAt` → uruchom `action`:

     * `try { action.executeNotSafeAction(); } catch (Exception e) { onError.accept(e); }`
     * po sukcesie → `onSingleActionCompleted.run()`
   * pobierz nowy czas z providera:

     * jeśli `null` → wywołaj `onCompleted.run()` i oznacz job jako zakończony
     * w przeciwnym razie ustaw nowy `nextRunAt`

💤 Dodaj `Thread.sleep(...)` w pętli, aby nie obciążać CPU.

✅ Po tym kroku odkomentuj:

```java
Runnable schedulerThread = new SchedulerThread();
new Thread(schedulerThread).start();
```

---

## 🧩 6️⃣ Dodaj drugie zadanie testowe

Odkomentuj:

```java
scheduler.forAction(() -> System.out.println("chyba zaczynam to rozumieć"))
    .useExecutionTimeProvider(Chron.builder().setMaxExecutionTimes(1).buildNextTimeExecutionProvider())
    .onCompleted(() -> System.out.println("Nie wierzę... działa!"))
    .Schedule();
```

✅ Powinien wydrukować komunikat **raz**, po czym wywołać `onCompleted`.

---

## ⚙️ 7️⃣ Dodatkowe zasady

* 🔄 `SchedulerThread` działa w tle – nie blokuje programu.
* ❗ `randomlyThrowException()` symuluje błędy.
* 🧱 `runForever()` utrzymuje aplikację przy życiu – **nie zmieniaj tego!**

---

## 🧭 8️⃣ Kolejność odkomentowywania

1️⃣ Lambdy `IProvideNextExecutionTime`

2️⃣ `Chron.builder()`

3️⃣ `IRunNotSafeAction`

4️⃣ `Scheduler`

5️⃣ `SchedulerThread`

6️⃣ Drugie zadanie testowe

Po każdym kroku uruchom i obserwuj logi.

---

## 🧪 9️⃣ Kryteria zaliczenia

✅ Program kompiluje się i działa po odkomentowaniu wszystkiego lub konkretnych segmentów. 

✅ Zadania wykonują się w odpowiednich momentach.

✅ Kod jest czytelny i uporządkowany.

---

