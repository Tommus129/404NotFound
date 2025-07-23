# 404NotFound by Tommaso Prandini (Tommus129)
# 🎮 Java Playground - Esercizi interattivi JavaFX

**Playground** è un'applicazione desktop sviluppata in JavaFX, progettata per aiutare studenti e appassionati a migliorare la loro comprensione del linguaggio Java attraverso esercizi interattivi e gamificati.  
L'app include diverse tipologie di esercizi, tracciamento dei progressi, classifica.🏆


## 🧠 Funzionalità principali

- 🔐 **Login & Registrazione utenti**
- 🧩 **4 tipologie di esercizi**:
  - Output del codice
  - Completa il codice
  - Trova il codice corretto
  - Debugger di codice
- 🥇 **Classifica globale**
- 📈 **Barre di progresso per ogni categoria**
- ⏱️ **Timer per ogni esercizio**

---

## ⚙️ Caratteristiche Tecniche

### 🛠 Linguaggi e tecnologie utilizzate:

- **Java 17**
- **JavaFX** per l'interfaccia grafica
- **FXML** per la definizione delle view
- **CSS** per lo stile grafico (JavaFX)
- **RichTextFX** per evidenziazione del codice (syntax highlighting)
- **Gson** per la gestione di dati JSON
- **Maven** per la gestione delle dipendenze
- **FXML Loader** per il caricamento dinamico delle schermate

---

### 🧩 Architettura del progetto

- **Model-View-Controller (MVC)**:
  - `model/` → Classi dati (`User`, `Attempt`, `Exercise`)
  - `view/` → File `.fxml` per le schermate dell'app
  - `controller/` → Logica dell'interfaccia e gestione eventi
  - `service/` → Caricamento ed elaborazione dati
  - `utils/` → Utility per serializzazione e file management

---

### 📁 Struttura risorse

- `resources/view/` → File `.fxml` (UI)
- `resources/data/` → File `.json` contenenti esercizi, utenti, tentativi
- `resources/styles/` → File `.css` per personalizzazione grafica
---

### 🔒 Sicurezza

- Le password sono salvate in chiaro nei file `.json`  
  ⚠️ Questo approccio è accettabile solo per scopi accademici/didattici.



### 🧪 Test compatibilità

- Testato su:
  - macOS Ventura
  - Windows 11
  - Java 17 e 21
- Compatibile con:
  - IntelliJ IDEA (raccomandato)
  - Eclipse (con plugin JavaFX)



## 🚀 Installazione ed Esecuzione 

### 1.Clona la repository
```bash
   git clone https://github.com/tuo-username/Codelympics.git
```
## Per MacOs/Linux

### 1.Apri il terminale e spostati nella cartella:
Esempio:
```bash
cd ~/Scrivania/Play 
```
### 2.Dai i permessi allo script di avvio(solo la prima votla) 
```bash
chmod +x start.sh
```
### 3.Avviare l'applcazione
```bash
./start.sh
```
## Per Windows

### Fare doppio clic sul file start.bat
----
## 📦 Utilizzo

Una volta avviata l'applicazione, segui questi semplici passaggi:

1. **Seleziona il tipo di esercizio**:
   - 📘 *Scegli l'Output*
   - 🧩 *Completa il Codice*
   - 🐞 *Correggi l'Errore*
   - ❓ *Trova Quello Corretto*

2. **Scegli il livello di difficoltà**:
   - Facile – per chi è alle prime armi
   - Medio – per testare le basi
   - Difficile – per mettersi alla prova 🔥

3. **Clicca su "🚀 Inizia"** per cominciare.

Durante l'esercizio:
- Leggi attentamente il codice proposto
- Scegli una delle 4 opzioni
- Ricevi un feedback immediato (✔️ o ❌)
- Avanza al prossimo esercizio o torna alla Dashboard

Al termine:
- Controlla i tuoi progressi 📈
- Visualizza la tua posizione in classifica 🏆


 ## 📝Licenza

Questo progetto è rilasciato sotto la licenza [MIT](https://choosealicense.com/licenses/mit/).

## Autore

Team 404NotFound
- Tommus129
