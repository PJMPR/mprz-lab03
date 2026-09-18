# 🧱 Wzorce projektowe: **Budowniczy** i **Singleton**

## 🎯 Cel dokumentu

Ten materiał ma pomóc Ci zrozumieć dwa klasyczne wzorce projektowe wykorzystywane w programowaniu obiektowym:

* **Budowniczy (Builder Pattern)** – służy do tworzenia złożonych obiektów krok po kroku.
* **Singleton** – zapewnia, że w systemie istnieje tylko **jedna instancja** danej klasy.

Oba wzorce występują w zadaniu „Schedulers” w różnych formach, dlatego warto dobrze zrozumieć ich idee i zastosowania.

---

## 🧩 1️⃣ Wzorzec **Budowniczy (Builder)**

### 📘 Problem

Czasem obiekt posiada wiele pól opcjonalnych lub konfiguracji, a jego konstruktor staje się nieczytelny:

```java
Person p = new Person("Jan", "Kowalski", 30, "Warszawa", true, "developer", 5000);
```

Taki konstruktor trudno czytać i utrzymywać.

### 💡 Idea

Zamiast przekazywać wszystko w konstruktorze, **budowniczy** pozwala tworzyć obiekt krok po kroku, poprzez czytelne metody konfigurujące.

### 🏗️ Struktura

* **Builder** – klasa, która posiada pola odpowiadające właściwościom budowanego obiektu.
* **Metody ****`set...()`** – ustalają poszczególne wartości.
* **Metoda ****`build()`** – tworzy ostateczny obiekt.

### ✨ Przykład

```java
public class Person {
    private String name;
    private String surname;

    public static PersonBuilder builder() {
        return new PersonBuilder();
    }

    public static class PersonBuilder {
        private String name;
        private String surname;

        public PersonBuilder setName(String name) {
            this.name = name;
            return this; // umożliwia łańcuchowanie
        }

        public PersonBuilder setSurname(String surname) {
            this.surname = surname;
            return this;
        }

        public Person build() {
            Person p = new Person();
            p.name = this.name;
            p.surname = this.surname;
            return p;
        }
    }
}
```

✅ Użycie:

```java
Person jan = Person.builder()
    .setName("Jan")
    .setSurname("Kowalski")
    .build();
```

### 📦 Wersja z metodą wytwórczą

W zadaniu „Schedulers” builder nie zwraca gotowego obiektu, tylko **funkcję wytwórczą (factory)**:

```java
IProvide<Person> janKowalskiProvider = Person.builder()
    .setName("Jan")
    .setSurname("Kowalski")
    .buildPersonProvider();
```

To rozszerzenie wzorca: zamiast gotowego obiektu, dostajesz **dostawcę**, który może tworzyć wiele kopii.

### 🔧 Przykład implementacji metody wytwórczej

W tym wariancie metoda buildera nie tworzy od razu obiektu, lecz zwraca funkcję (lambdę), która potrafi go stworzyć później:

```java
@FunctionalInterface
interface IProvide<T> {
    T provide();
}

public class Person {
    private String name;
    private String surname;

    public static PersonBuilder builder() {
        return new PersonBuilder();
    }

    public static class PersonBuilder {
        private String name;
        private String surname;

        public PersonBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public PersonBuilder setSurname(String surname) {
            this.surname = surname;
            return this;
        }

        // zamiast tworzyć obiekt bezpośrednio, zwracamy funkcję wytwórczą
        public IProvide<Person> buildPersonProvider() {
            return () -> {
                Person p = new Person();
                p.name = this.name;
                p.surname = this.surname;
                return p;
            };
        }
    }
}
```

✅ Użycie:

```java
IProvide<Person> provider = Person.builder()
    .setName("Jan")
    .setSurname("Kowalski")
    .buildPersonProvider();

Person person1 = provider.provide();
Person person2 = provider.provide(); // nowa kopia, z tymi samymi wartościami
```

Dzięki temu możemy łatwo tworzyć **wiele instancji** obiektu na podstawie jednej konfiguracji buildera – np. w testach, generatorach danych lub harmonogramach.

---

## 🧭 2️⃣ Wzorzec **Singleton**

### 📘 Problem

Potrzebujesz tylko jednej instancji danej klasy w całym systemie — np.:

* rejestru zadań (`Scheduler`),
* menedżera połączeń z bazą danych,
* globalnego loggera.

Tworzenie wielu instancji mogłoby prowadzić do chaosu i błędów.

### 💡 Idea

Singleton kontroluje tworzenie instancji – sam ją przechowuje i udostępnia przez metodę statyczną.

### 🏗️ Struktura

* Prywatny konstruktor (blokuje tworzenie nowych obiektów spoza klasy).
* Statyczne pole przechowujące instancję.
* Statyczna metoda dostępu (`getInstance()`).

### ✨ Przykład

```java
public class Scheduler {
    private static Scheduler instance;

    private Scheduler() {
        // prywatny konstruktor – nikt z zewnątrz nie może utworzyć nowego obiektu
    }

    public static Scheduler getInstance() {
        if (instance == null) {
            instance = new Scheduler();
        }
        return instance;
    }
}
```

✅ Użycie:

```java
Scheduler scheduler = Scheduler.getInstance();
```

Każde wywołanie `getInstance()` zwróci **ten sam obiekt**, co zapewnia spójność systemu.

### 🧠 Wersja wielowątkowa

Jeśli aplikacja jest wielowątkowa, dodaj synchronizację:

```java
public static synchronized Scheduler getInstance() {
    if (instance == null) {
        instance = new Scheduler();
    }
    return instance;
}
```

Lub zastosuj tzw. **lazy initialization holder idiom**:

```java
public class Scheduler {
    private Scheduler() {}

    private static class Holder {
        private static final Scheduler INSTANCE = new Scheduler();
    }

    public static Scheduler getInstance() {
        return Holder.INSTANCE;
    }
}
```

---

## 🔍 Porównanie obu wzorców

| Cecha               | Budowniczy                                   | Singleton                               |
| ------------------- | -------------------------------------------- | --------------------------------------- |
| Cel                 | Tworzenie złożonych obiektów krok po kroku   | Zapewnienie jednej instancji klasy      |
| Typ                 | Wzorzec kreacyjny                            | Wzorzec kreacyjny                       |
| Główna zaleta       | Czytelność i elastyczność tworzenia obiektów | Globalny, kontrolowany dostęp do zasobu |
| Przykład z projektu | `Chron` i `PersonBuilder`                    | `Scheduler`                             |

---

## 📚 Podsumowanie

* 🧱 **Budowniczy** ułatwia budowanie obiektów poprzez czytelne metody konfiguracyjne.
* 🔁 **Singleton** gwarantuje, że istnieje tylko jedna instancja klasy w aplikacji.
* Oba wzorce często współpracują — np. `Scheduler` może być singletonem, a jego konfiguracje (zadania) tworzone builderem.

---
