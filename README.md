# Senioren Quiz App

Eine barrierefreie, seniorengerechte Quiz-App für Android, entwickelt, um das Gedächtnis zu trainieren und Freude zu bereiten.

## Features

### Benutzerfreundlichkeit (Accessibility)
*   **Optimiert für Senioren**: Extra große Schriften, Buttons und Icons für optimale Lesbarkeit auf Tablets und Smartphones.
*   **Stressfreie Bedienung**: Kein Zeitlimit für die Beantwortung der Fragen.
*   **Screen-On Feature**: Der Bildschirm bleibt während des gesamten Quiz aktiv – kein nerviges Entsperren nötig.
*   **Einfache Navigation**: Klare Struktur mit Bottom-Navigation zum Wechseln zwischen den Bereichen "Quiz", "Märchen", "Bilder" und "Audio".

### Interaktives Feedback
*   **Akustisch**: Unterschiedliche Töne für richtige (hoch) und falsche (tief) Antworten.
*   **Visuell**: Der gesamte Bildschirm leuchtet kurz Grün (richtig) oder Rot (falsch) auf.
*   **Haptisch**: Sanfte Vibration bei Tastendruck.
*   **Lerneffekt**: Bei falscher Antwort wird die korrekte Lösung direkt angezeigt.

### Inhalte & Spielmodi

#### 1. Klassik Quiz (Text)
*   **9 Kategorien**: Natur & Tiere, Sprichwörter, Geschichte, Geografie, Musik & Lieder, Filme & Fernsehen, Weltall & Technik, Essen & Trinken, Sport & Hobby.
*   **Bunte Mischung**: Ein Mix aus allen Bereichen.
*   **Umfang**: Hunderte Fragen von leicht bis knifflig, optimiert durch Deduplizierung.

#### 2. Märchen Modus
*   Spezielle Fragen zu den beliebtesten Grimms Märchen.
*   Kategorien: Hänsel & Gretel, Rotkäppchen, Schneewittchen, Froschkönig, Aschenputtel, Dornröschen, Frau Holle, Rumpelstilzchen, Wolf & 7 Geißlein.

#### 3. Bilder-Quiz (NEU in v1.3)
*   Fragen mit visueller Unterstützung.
*   Erkennen von Tieren, Objekten oder Sehenswürdigkeiten anhand von Bildern.
*   **NEU in v1.3.1**: Antippen der Bilder öffnet eine Vollbildansicht für bessere Erkennbarkeit.

#### 4. Audio-Quiz (NEU in v1.3)
*   Fragen mit hörbaren Inhalten.
*   Erkennen von Instrumenten, Stimmen berühmter Persönlichkeiten oder Geräuschen.

### Optimierungen (v1.3.2)
*   **Zufällige Antworten**: Die Position der richtigen Antwort wechselt nun zufällig (nicht mehr immer Antwort A).
*   **Verbesserte UI**: Übersichtlichere Vollbildansicht mit großem Schließen-Button am unteren Rand.

## Tech Stack

*   **Sprache**: Kotlin
*   **Architektur**: MVVM (Model-View-ViewModel) mit Single Activity und Fragmenten.
*   **UI**:
    *   Android XML Layouts, Material Design 3.
    *   Responsive Layouts für verschiedene Displaygrößen.
    *   Custom Full-Screen Overlay für Bilder.
*   **Medien**: `MediaPlayer` für Audios, `ImageView` für Bilder, `ToneGenerator` für Feedback.
*   **Daten**:
    *   Trennung der Datenquellen in `questions.json` (Text), `images.json` (Bilder) und `audio.json` (Audio).
    *   Dynamisches Laden der Fragen.

## Update-System

Die App verfügt über einen integrierten Updater:
1.  Prüft beim Start `version.json` auf GitHub.
2.  Bei neuerer Version (höherer `versionCode`) wird ein **vollständiger In-App-Download** der APK angeboten.
3.  Zeigt einen Fortschrittsbalken während des Downloads.
4.  Installiert die APK automatisch (erfordert Berechtigung für unbekannte Quellen ab Android 8+).

## Fragen-Update

Textfragen können unabhängig von App-Updates aktualisiert werden:
*   Die App lädt `questions.json` beim Start im Hintergrund.
*   Neue Fragen sind beim nächsten Start sofort verfügbar.

## Installation

1.  Lade die aktuelle `Seniorenquiz.apk` (v1.3.2) herunter.
2.  Installiere sie auf dem Android-Gerät (Dateimanager -> APK antippen).
3.  Viel Spaß beim Rätseln!
