# Belegverteidigung

## RMI
+ Vorteile:
	+ Einfachheit: Objekte, die weit entfernt sind, können als lokale Objekte behandelt werden.
	+ Flexibel
+ Nachteile:
	+ Latenz
	+ Potenzial für Teilausfälle

## Thread-Pool
+ Vorteile:
	+ Wiederverwendung von Threads und damit spart das Programm die Kosten für die Erstellung neuer Threads, wird die Leistung erhöht
	+ Threads müssen nicht manuell erstellt, verwaltet, geplant und beendet werden, die Thread-Pool-Klasse übernimmt alle
+ Nachteile:
	+ it nicht für lang laufende Operationen geeignet, da dies leicht zu einem Thread-Starvation führen kann
	+ keine Kontrolle über den Zustand und die Priorität des Threads
	+ Deadlock
	+ Ressource Thrashing, wenn der Thread-Pool sehr groß ist.
