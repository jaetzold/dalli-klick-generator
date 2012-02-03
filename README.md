Ein Dalli-Klick Generator
=========================

Mit diesem Tool kann man sehr einfach aus einem Bild mehrere Bilder machen die sich fuer ein Dalli-Klick Spiel eignen.

Benutzung
---------

1. Einfach das fertige .jar laden: [dalli-klick-generator-1.0.jar](https://github.com/downloads/jaetzold/dalli-klick-generator/dalli-klick-generator-1.0.jar)
2. In ein Verzeichnis legen in dem die Bilder fuer das Spiel liegen (.png/.jpg Format)
3. Per Doppelklick auf das .jar den Generator starten. Achtung, es werden **alle Bilder** aus dem Verzeichnis genommen! Ausser welchen die vom Tool selbst produziert wurden.
4. Die generierten Dateien nehmen und nacheinander wie in einer normalen Diashow zeigen - egal mit welchem Tool. Z.B. Quicklook unter MacOSX oder der "Windows Bild und Faxanzeige" unter Windows.

Das Bild wird per default in 13 Bereiche eingeteilt: Einen Kreis in der Mitte und 12 "Tortenstuecke" aussen herum.
Diese werden in der Voreinstellung im Uhrzeigersinn aufgedeckt, mit dem mittleren Kreis als letztes. Als Punktzahl wird jeweils angezeigt wieviele Teile noch verdeckt sind.

Parameter
---------

Die Voreinstellungen kann man veraendern, indem man Parameter in den Dateinamen einbettet, und zwar direkt vor der Dateiendung mit einem Punkt vom eigentlichen Dateinamen davor getrennt.
Man kann die Anzahl und Reihenfolge der Kreissegmente, ob ein mittlerer Kreis verdeckt ist und ob eine Punktzahl angezeigt wird veraendern.

Beispiele:

* `bild.12-6-8-2-10-4-1-5-9-11-3-7.jpg` - produziert 12 Tortensegmente und einen Kreis in der Mitte, mit Punkteanzeige und deckt die Tortensegmente in der angegebenen, recht durcheinandergewuerfelten Reihenfolge auf.
* `bild.-6-5-4-3-2-1.jpg` - produziert 6 Tortensegmente _ohne_ Kreis in der Mitte und ohne Punkteanzeige.
* `bild.k-1-3-5-7-2-4-6-8.jpg` - produziert 8 Tortensegmente und einen Kreis in der Mitte, keine Punkteanzeige und deckt die Tortensegmente in der angegebenen Reihenfolge (immer eins wird uebersprungen) auf.
* `bild.p.jpg` - produziert 12 Tortensegmente (default) _ohne_ Kreis in der Mitte aber mit Punkteanzeige.
* `bild..jpg` - produziert 12 Tortensegmente (default) ohne Kreis in der Mitte _und ohne_ Punkteanzeige.

Alles klar? ;-)
