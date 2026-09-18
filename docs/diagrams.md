# 🧭 Diagram klas – rozwiązanie zadania **Schedulers**

Poniżej znajdziesz propozycję **diagramu klas** dla implementacji spełniającej wymagania z `Main.java`. Diagram jest w **Mermaid**.

---

## 📐 Class Diagram

```mermaid
classDiagram
    direction LR

    class IProvideNextExecutionTime {
      <<interface>>
      +LocalDateTime provideTime()
    }

    class IRunNotSafeAction {
      <<interface>>
      +void executeNotSafeAction() throws Exception
    }

    class Chron {
      -LocalDateTime start
      -LocalDateTime end
      -Duration interval
      -Integer maxTimes
      +static Chron builder()
      +Chron setStartTime(LocalDateTime)
      +Chron setEndDate(LocalDateTime)
      +Chron setIntervalDuration(Duration)
      +Chron setMaxExecutionTimes(int)
      +IProvideNextExecutionTime buildNextTimeExecutionProvider()
    }

    class Scheduler {
      -static Scheduler instance
      -List~ScheduledJob~ jobs
      -Scheduler()
      +static Scheduler getInstance()
      +Builder forAction(IRunNotSafeAction)
      +List~ScheduledJob~ getPendingJobs()
    }

    class Builder {
      -IRunNotSafeAction action
      -IProvideNextExecutionTime timeProvider
      -Consumer~Throwable~ onError
      -Runnable onSingleActionCompleted
      -Runnable onCompleted
      +Builder useExecutionTimeProvider(IProvideNextExecutionTime)
      +Builder onError(Consumer~Throwable~)
      +Builder onSingleActionCompleted(Runnable)
      +Builder onCompleted(Runnable)
      +void Schedule()
    }

    class ScheduledJob {
      -IRunNotSafeAction action
      -IProvideNextExecutionTime timeProvider
      -Consumer~Throwable~ onError
      -Runnable onSingleActionCompleted
      -Runnable onCompleted
      -LocalDateTime nextRunAt
      -boolean finished
      +IRunNotSafeAction getAction()
      +LocalDateTime getNextRunAt()
      +void setNextRunAt(LocalDateTime)
      +LocalDateTime provideNextExecutionTime()
      +void markFinished()
      +boolean isFinished()
      +void runOnErrorHook(Throwable)
      +void runOnSingleCompletedHook()
      +void runOnCompletedHook()
    }

    class SchedulerThread {
      +void run()
      -long tickMillis
    }

    Scheduler o-- "*" ScheduledJob : zawiera
    Scheduler ..> Builder : tworzy
    Builder --> ScheduledJob : Schedule()
    ScheduledJob --> IRunNotSafeAction : używa
    ScheduledJob --> IProvideNextExecutionTime : używa
    SchedulerThread ..> Scheduler : odczyt jobów
    Chron ..> IProvideNextExecutionTime : buduje
```

