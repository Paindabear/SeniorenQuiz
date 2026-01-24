# Senioren Quiz app

Eine barrierefreie, seniorengerechte Quiz-App für Android, entwickelt, um das Gedächtnis zu trainieren und Freude zu bereiten.

## Features

### Benutzerfreundlichkeit (Accessibility)
*   **Optimiert für Senioren**: Extra große Schriften, Buttons und Icons für optimale Lesbarkeit auf Tablets und Smartphones.
*   **Stressfreie Bedienung**: Kein Zeitlimit für die Beantwortung der Fragen.
*   **Screen-On Feature**: Der Bildschirm bleibt während des gesamten Quiz aktiv – kein nerviges Entsperren nötig.
*   **Einfache Navigation**: Klare Struktur mit Bottom-Navigation zum Wechseln zwischen den Bereichen.

### Interaktives Feedback
*   **Akustisch**: Unterschiedliche Töne für richtige (hoch) und falsche (tief) Antworten.
*   **Visuell**: Der gesamte Bildschirm leuchtet kurz Grün (richtig) oder Rot (falsch) auf.
*   **Haptisch**: Sanfte Vibration bei Tastendruck.
*   **Lerneffekt**: Bei falscher Antwort wird die korrekte Lösung direkt angezeigt.

### Inhalte & Spielmodi
*   **Klassik Quiz**:
    *   9 Kategorien: Natur & Tiere, Sprichwörter, Geschichte, Geografie, Musik & Lieder, Filme & Fernsehen, Weltall & Technik, Essen & Trinken, Sport & Hobby.
    *   "Bunte Mischung" für einen Mix aus allen Bereichen.
    *   Über **450 Fragen**, von leichten Erinnerungsfragen bis zu kniffligeren Wissensfragen.
*   **Märchen Modus**:
    *   Spezielle Fragen zu den beliebtesten Grimms Märchen.
    *   Kategorien: Hänsel & Gretel, Rotkäppchen, Schneewittchen, Froschkönig, Aschenputtel, Dornröschen, Frau Holle, Rumpelstilzchen, Wolf & 7 Geißlein.

## Tech Stack

*   **Sprache**: Kotlin
*   **Architektur**: Single Activity mit Fragments (`MainActivity` hostet `CategorySelectionFragment` & `FairyTaleFragment`).
*   **UI**:
    *   Android XML Layouts
    *   **Material Design 3**: `MaterialButton`, `BottomNavigationView`
    *   Vector Drawables für skalierbare Icons.
*   **Media & Animation**:
    *   `ToneGenerator` für ressourcensparendes Audio-Feedback.
    *   `ObjectAnimator` (`ArgbEvaluator`) für flüssige Farbübergänge.
    *   `AlphaAnimation` für visuelle Hinweise (blinkender Weiter-Button).
*   **Build System**: Gradle mit Kotlin DSL (`build.gradle.kts`).

## Installation

1.  APK (`app-debug.apk`) auf das Android-Gerät übertragen.
2.  Dateimanager öffnen und APK auswählen.
3.  Ggf. "Installation aus unbekannten Quellen" zulassen.
4.  App starten und losrätseln!
