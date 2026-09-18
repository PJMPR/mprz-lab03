# 📘 Instrukcja rozpoczęcia i oddawania zadań – JAZ

Dokument ten opisuje **pełny proces pracy z zadaniami projektowymi** realizowanymi w ramach przedmiotu **Język Aplikacji Złożonych (JAZ)** na PJATK. Postępuj zgodnie z poniższymi krokami, aby poprawnie rozpocząć, realizować i oddać swoje zadanie.

---

## 🚀 Rozpoczęcie pracy

### 1️⃣ Wejście do zadania

* Przejdź do **linku udostępnionego na grupie w Teams** dla danego zadania.
* Na stronie GitHub Classroom znajdź **swoje imię i nazwisko** na liście uczestników.
* Kliknij, aby **zaakceptować zadanie**.

### 2️⃣ Utworzenie repozytorium

Po chwili pojawi się strona informująca, że Twoje repozytorium jest tworzone.
Jeśli nic się nie dzieje — **odśwież stronę** po kilku sekundach 🔄

GitHub automatycznie utworzy dla Ciebie **indywidualne repozytorium**, w którym będziesz pracować.

### 3️⃣ Otworzenie repozytorium

* Po utworzeniu repozytorium kliknij wygenerowany link.
* Skopiuj adres URL repozytorium — będzie potrzebny do klonowania projektu 🧩

---

## 💻 Klonowanie projektu na komputer

### 4️⃣ Utworzenie folderu roboczego

Zalecane miejsce na komputerach uczelnianych:
📁 `Z:\git`
Możesz też utworzyć inny folder w dowolnej, wygodnej lokalizacji.

### 5️⃣ Otworzenie terminala w folderze

Aby szybko otworzyć wiersz poleceń w wybranym folderze:

* kliknij w pasek adresu, wpisz `cmd` i naciśnij **Enter**.

### 6️⃣ Klonowanie repozytorium

W terminalu wpisz polecenie:

```bash
git clone <adres_repozytorium>
```

Przykład:

```bash
git clone https://github.com/PJMPR/JAZ2025_lab01_JanKowalski.git
```

Po chwili w folderze pojawi się sklonowany projekt 📂

---

## 🧠 Praca nad projektem

### 7️⃣ Otwórz projekt w IntelliJ IDEA

* Uruchom IntelliJ IDEA 🧩
* Wybierz **File → Open...** i wskaż folder do pojektu.

Od tej chwili możesz pisać kod, uruchamiać testy i realizować kolejne segmenty zadania.

> 💡 **Wskazówka:** Po każdej zakończonej części warto zrobić *commit* — zapis zmian lokalnie.

---

## ☁️ Wysyłanie rozwiązania (oddanie zadania)

### 8️⃣ Przygotowanie do wysyłki

Upewnij się, że wszystkie pliki są zapisane, a testy przechodzą poprawnie ✅

### 9️⃣ Wysłanie zmian na GitHub

W terminalu wpisz kolejno:

```bash
git add .
git commit -m "Moje rozwiązanie zadania JAZ"
git push origin main
```

Twoje zmiany zostaną wysłane do zdalnego repozytorium 🌍

---

## 🎓 Gratulacje!

Twoje zadanie zostało poprawnie przesłane.
Możesz spokojnie zamknąć projekt lub rozpocząć pracę nad kolejnym 🧑‍💻

---

### 🧭 Szybkie przypomnienie

| Etap | Co zrobić             | Narzędzie             |
| ---- | --------------------- | --------------------- |
| 1    | Wejdź w link z Teams  | Przeglądarka 🌐       |
| 2    | Zaakceptuj zadanie    | GitHub Classroom 🧩   |
| 3    | Skopiuj link repo     | GitHub 🔗             |
| 4    | Utwórz folder lokalny | Eksplorator plików 📁 |
| 5    | Otwórz `cmd`          | Terminal 💻           |
| 6    | `git clone`           | Git 🧠                |
| 7    | Otwórz w IntelliJ     | IDE 🧰                |
| 8    | `git add/commit/push` | Terminal ⬆️           |
| 9    | Sprawdź na GitHub     | Przeglądarka 👀       |

---

> ✨ **Pamiętaj:**
>
> * Nie wysyłaj plików spoza projektu (np. `.idea`, `out/`, `target/`).
> * Sprawdzaj status komendą `git status`.
> * Często zapisuj i commituj zmiany – unikniesz utraty postępu.

---

📅 *Polsko-Japońska Akademia Technik Komputerowych – Przedmiot JAZ, 2025*
