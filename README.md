# MancalaGame-AI
# **Mancala Game - Documentation**

## **Table of Contents**

1. [Introduction](#introduction)  
2. [Features](#features)  
3. [Requirements](#requirements)  
4. [Installation](#installation)  
5. [Usage](#usage)  
   5.1. Running the Game  
   5.2. Game Modes  
   5.3. Hints System  
6. [Project Structure](#project-structure)  
7. [How to Save and Load a Game](#how-to-save-and-load-a-game)  
8. [Strategies and Difficulty Levels](#strategies-and-difficulty-levels)  
9. [Future Enhancements](#future-enhancements)  
10. [License](#license)

---

## **Introduction**

The Mancala Game is a Java implementation of the classic board game, featuring two main modes: **Player vs Player** and **Player vs AI**. This project integrates advanced AI strategies like **Alpha-Beta pruning** and includes features like saving/loading games, hints for players, and choosing AI difficulty levels.

---

## **Features**

- **Multiplayer Mode**: Play against another player on the same system.  
- **AI Mode**: Play against the computer with three difficulty levels (Amateur, Intermediate, Expert).  
- **Hints System**: Request hints to suggest optimal moves (limited number per game).  
- **Save/Load Functionality**: Save your game at any point and resume it later.  
- **AI Strategies**: The computer uses advanced strategies powered by Alpha-Beta pruning.  
- **Dynamic Menu**: Access options to save, load, request hints, or quit mid-game.

---

## **Requirements**

- **Java Development Kit (JDK)**: Version 8 or higher.  
- **Java IDE (optional)**: IntelliJ IDEA, Eclipse, or similar.  

---

## **Installation**

1. Clone the repository:  
   ```bash
   git clone https://github.com/mouad4949/MancalaGame-AI.git
   cd repository
   ```

2. Compile the project:  
   ```bash
   javac -d build src/**/*.java
   ```

3. Run the game:  
   ```bash
   java -cp build Main
   ```

---

## **Usage**

### **5.1. Running the Game**

To start the game, execute the following command in the terminal:  
```bash
java -cp build Main
```

You will be prompted to choose a game mode:  
1. **Multiplayer Mode**: Both players take turns.  
2. **Single-player Mode**: Play against the computer.  

### **5.2. Game Modes**

1. **Multiplayer Mode**:  
   - Each player selects pits to make moves alternately.  
   - Access the menu anytime by entering `13` to save, load, or request a hint.  

2. **Single-player Mode**:  
   - Choose the AI difficulty level before starting.  
   - Decide whether the player or AI starts first.  

### **5.3. Hints System**

Players can request hints during their turn to get advice on the best possible move. The number of hints is **limited** per game (set by the game configuration).  
- To request a hint, enter `13` during your turn.

---

## **Project Structure**

The project is organized as follows:

- **src/**: Contains the Java source files.  
- **build/**: Compiled files and class files.  
- **mancala_save.dat**: Save data for the game.  

Key classes:  
- **MancalaGame**: Main game logic and rules.  
- **GameSave**: Handles game save/load functionality.  
- **GameSearch**: Implements the AI strategy using Alpha-Beta pruning.  
- **MancalaPosition**: Represents the state of the game board.

---

## **How to Save and Load a Game**

You can save and load the game at any time during gameplay:
- To save the game, press `13` and select the save option in the menu.
- To load a previously saved game, select the load option from the menu.

This allows you to resume your game at a later time.

---

## **Strategies and Difficulty Levels**

The AI uses **Alpha-Beta pruning** for optimal decision-making. The difficulty level determines how many levels of look-ahead the AI will use:

1. **Amateur**: Low-level AI with basic moves.
2. **Intermediate**: Uses moderate depth of search for better decisions.
3. **Expert**: Deep search with advanced tactics for challenging gameplay.

---

## **Future Enhancements**

- **Enhanced AI**: Implement more sophisticated strategies for the Expert level.
- **Multiplayer Online Mode**: Allow players to compete remotely.
- **Graphical User Interface (GUI)**: Create a visual representation of the game board.

---

## **License**

This project is licensed under the MIT License.

---



