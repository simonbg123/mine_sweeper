## Mine Sweeper game for Desktop

### Usage / Characteristics

- 2 modes: single-game or multilevel
- 3 levels of difficulty
- 3 board sizes
- A Flag utton to enable placing/removing flags on the board
- A New Game button. This button is used to start a new game and apply configuration changes.
- A Continue button, enabled for multilevel play, when a level has been completed

When new settings are chosen, the New Game button will start a game with the new settings, but if a new board size has been selected a dialog will be displayed. The user must confirm the new configuration as it entails a game restart.

### Design

- Technology: Java 11, Java Swing for the GUI.
- Decoupling of the UI and the domain code. The domain logic is contained in Board, Game, and Manager classes, which implement interfaces defining their external use. A new GUI could be developed using a different technology, but keeping the same business logic implementation.


