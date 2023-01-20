# lovecraft_letter

[![Coverage Status](https://coveralls.io/repos/github/TobiasReyEye/lovecraft_letter/badge.svg?branch=main)](https://coveralls.io/github/TobiasReyEye/lovecraft_letter?branch=main)
![Scala CI](https://github.com/TobiasReyEye/lovecraft_letter/actions/workflows/scala.yml/badge.svg)
![RepoSize](https://img.shields.io/github/repo-size/TobiasReyEye/lovecraft_letter)
![Lines of Code](https://tokei.rs/b1/github/TobiasReyEye/lovecraft_letter)
[![License](https://img.shields.io/github/license/TobiasReyEye/lovecraft_letter?color=green)](https://cdn130.picsart.com/272563229032201.jpg?r1024x1024)

## Usage

You can run this Project with sbt.

[install sbt](https://www.scala-sbt.org/1.x/docs/Setup.html)

Compile code with:
```
sbt compile
```
run it with:
```
sbt run
```
and
```
sbt console
```
will start a Scala 3 REPL.

---

## Spielablauf

Der Spieler, der am Zug ist, zieht zunächst die oberste Karte des
Nachziehstapels. Da er zuvor
bereits 1 Karte auf der Hand hatte, besitzt er nun 2 Handkarten.
Er wählt 1 dieser 2 Handkarten und spielt sie offen vor sich aus. Die
andere Karte behält er weiterhin verdeckt auf der Hand.
Hat die ausgespielte Karte eine Funktion, muss der Spieler sie jetzt
ausführen, auch wenn es zu seinem Nachteil ist. Abschließend legt er
sie offen auf seinen persönlichen Ablagestapel, den er vor sich bildet.
Damit ist sein Zug beendet, und der Spieler links von ihm ist am Zug.

### Karten ablegen
Die Spieler müssen aufgrund verschiedener Kartenfunktionen oftmals
Karten ablegen. Dies bedeutet immer, dass der Spieler diese Karten offen
auf seinen Ablagestapel legt, die Funktion dieser Karte aber nicht ausführt.

### Wahnsinn und Wahnsinnskontrolle
Liegt eine Wahnsinnskarte offen auf dem Ablagestapel eines Spielers,
unabhängig davon, wie sie dort gelandet ist, ist dieser Spieler in Kontakt
mit verbotenem Wissen gekommen und wird sofort für den Rest der
Runde wahnsinnig. Hat der Spieler die Karte gespielt, führt er erst noch
deren normale Funktion aus!
Spielt ein wahnsinniger Spieler in einem folgenden Spielzug eine
Wahnsinnskarte nutzt er die Wahnsinnsfunktion der Karte.
Ein wahnsinniger Spieler riskiert aber, jederzeit mental zusammenzubrechen.
Zu Beginn jedes seiner folgenden Spielzüge, bevor er die
neue Handkarte zieht, muss er die Wahnsinnskontrolle durchführen.
Dazu deckt er so viele Karten eine nach der anderen vom Nachziehstapel
auf, wie er bereits an Wahnsinnskarten auf seinem Ablagestapel liegen
hat. Dies passiert automatisch.
Deckt er dabei eine Wahnsinnskarte auf, scheidet er sofort aus.
Der Spieler legt die aufgedeckten Karten anschließend offen
auf seinen Ablagestapel, ohne deren Funktionen auszuführen.

---

## Contributors
| [Tobias Stöhr](https://github.com/TobiasReyEye)  |  [Jona Böcker](https://github.com/jbcker) | 
|---|---|
| ![image](https://github-readme-streak-stats.herokuapp.com/?user=TobiasReyEye) | ![image](https://github-readme-streak-stats.herokuapp.com/?user=jbcker)  |
