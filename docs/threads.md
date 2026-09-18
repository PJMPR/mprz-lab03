# 🧵 Wątki w Javie

## 🎯 Cel

Krótko i praktycznie: jak stworzyć i uruchomić **wątek poboczny** w tym samym procesie Javy oraz jak napisać pętlę harmonogramu w stylu `SchedulerThread`.

---

## 1) Dwa podstawowe sposoby utworzenia wątku

### ✅ Implementacja `Runnable`

```java
class Worker implements Runnable {
    @Override
    public void run() {
        System.out.println("Start wątku: " + Thread.currentThread().getName());
        // ...kod pracy wątku...
    }
}

Runnable worker = new Worker();
new Thread(worker, "worker-1").start();
```

### ✅ Klasa anonimowa lub lambda

```java
new Thread(() -> {
    // ...kod pracy w tle...
}, "anon-1").start();
```

> 💡 Rekomendowane: **`Runnable`**** + ****`new Thread(...)`** albo egzekutory (poniżej).

---

## 2) Cykliczna praca wątku: `sleep` i pętla

```java
class Heartbeat implements Runnable {
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            System.out.println("tick");
            try {
                Thread.sleep(500); // nie rób "busy loop"
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // przywróć znacznik przerwania
            }
        }
    }
}
```

---

## 3) Eleganckie zatrzymywanie wątku

### 🔸 Flaga `volatile`

```java
class Stoppable implements Runnable {
    private volatile boolean running = true;
    public void stop() { running = false; }

    @Override public void run() {
        while (running) {
            // ...
            try { Thread.sleep(200); } catch (InterruptedException e) { running = false; }
        }
    }
}
```

### 🔸 Użycie `interrupt()`

```java
Thread t = new Thread(new Heartbeat());
t.start();
// ...
t.interrupt(); // sygnał przerwania dla pętli sprawdzającej isInterrupted()
```

---

## 4) Wątki **daemon** vs **user**

* `setDaemon(true)` → wątek **nie blokuje** zamknięcia JVM.
* Dobre dla usług tła, które nie muszą dobiec do końca.

```java
Thread t = new Thread(new Heartbeat(), "scheduler-thread");
t.setDaemon(true);
t.start();
```

---

## 5) Obsługa wyjątków w wątkach

* Złap wyjątki **w środku ****`run()`** aby nie ubić wątku.
* Opcjonalnie: **`UncaughtExceptionHandler`** do logów.

```java
Thread t = new Thread(() -> {
    try {
        // ...krytyczna praca...
    } catch (Throwable ex) {
        System.err.println("Błąd w wątku: " + ex.getMessage());
    }
});

t.setUncaughtExceptionHandler((th, ex) -> {
    System.err.println("Nieprzechwycony wyjątek w " + th.getName());
});
```

---

## 6) `ExecutorService` – wygodna alternatywa

```java
ExecutorService exec = Executors.newSingleThreadExecutor(r -> {
    Thread t = new Thread(r, "scheduler-exec");
    t.setDaemon(true);
    return t;
});

exec.submit(() -> { /* ...kod w tle... */ });
// po zakończeniu pracy
exec.shutdown();
```

---

## 7) Mini–szablon `SchedulerThread`

Pętla sprawdzająca zadania i odpalająca je „o czasie”.

```java
public class SchedulerThread implements Runnable {
    private final Scheduler scheduler = Scheduler.getInstance();
    private final long tickMillis = 200; // częstotliwość sprawdzania

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                // 1) pobierz niezakończone joby (zapewnij bezpieczny dostęp do kolekcji)
                for (ScheduledJob job : scheduler.getPendingJobs()) {
                    if (job.isFinished()) continue;

                    LocalDateTime now = LocalDateTime.now();
                    if (now.isAfter(job.getNextRunAt()) || now.isEqual(job.getNextRunAt())) {
                        try {
                            job.getAction().executeNotSafeAction();
                            job.runOnSingleCompletedHook(); // jeśli ustawiony
                        } catch (Exception ex) {
                            job.runOnErrorHook(ex); // jeśli ustawiony
                        }

                        LocalDateTime next = job.provideNextExecutionTime();
                        if (next == null) {
                            job.markFinished();
                            job.runOnCompletedHook();
                        } else {
                            job.setNextRunAt(next);
                        }
                    }
                }
            } catch (Throwable loopEx) {
                // loguj i kontynuuj – wątek schedulera powinien być odporny
            }

            try { Thread.sleep(tickMillis); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
        }
    }
}
```

> ℹ️ **Ważne:** `scheduler.getPendingJobs()` powinno być bezpieczne wątkowo (np. `CopyOnWriteArrayList`, synchronizacja lub własna sekcja krytyczna).

---

## 8) Dobre praktyki (TL;DR)

* 🔁 Unikaj „busy loop” → zawsze `sleep` lub `wait`.
* 🧱 Chroń współdzielone struktury (synchronizacja / kolekcje concurrent).
* 🧯 Złap wyjątki w `run()` – wątek schedulera ma **nie padać**.
* 🏷️ Nazywaj wątki (`new Thread(r, "scheduler-1")`) – logi będą czytelniejsze.
* 🧪 Logika pętli ma być **deterministyczna** i krótka.

---

## 9) Minimalny przykład do skopiowania (hello thread)

```java
public class HelloThread {
    public static void main(String[] args) throws Exception {
        Thread t = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                System.out.println("Hello from " + Thread.currentThread().getName());
                try { Thread.sleep(300); } catch (InterruptedException e) { return; }
            }
        }, "hello-worker");
        t.start();
        t.join(); // czekamy aż skończy
        System.out.println("Done.");
    }
}
```

---

**Gotowe!** Masz pod ręką esencję tworzenia i uruchamiania wątków oraz szkielet `SchedulerThread`. Dopasuj nazwy metod do tych, które zdefiniujesz w swoim projekcie.
