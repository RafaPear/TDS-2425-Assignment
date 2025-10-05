# Module Reversi

This module implements the Reversi game with a focus on modularity, clarity, and extensibility. The architecture is divided into distinct packages to separate responsibilities, facilitating maintenance, testing, and project evolution.

The module includes:
- Core game logic, including rules, turn alternation, and victory conditions.
- Efficient board representation, with support for different sizes and variants.
- Intuitive command-line interface, allowing direct interaction with the game.
- Extensible command system to facilitate new functionalities.
- Structure designed for future integration with graphical or web interfaces.

This project can be used as a foundation for studying object-oriented programming, modular design, and game development in Kotlin. The documentation details each component, facilitating understanding and collaboration.

#Package pt.isel.reversi.core.board
# Board

This package is responsible for the internal representation of the Reversi board, including structures for cells and pieces, and algorithms for move validation and board state updates. It allows adaptation for different sizes and rule variants, with optimized methods for board manipulation.

#Package pt.isel.reversi.core.game
# Game

Manages the Reversi game lifecycle, turn alternation, verification of victory or draw conditions, and coordination between board and players. Provides abstractions for different player types and facilitates integration with external interfaces.

#Package pt.isel.reversi.cli
# CLI

Implements the command-line interface, managing board presentation, reading and validating user commands, and displaying messages. Supports customization of the game experience and different interaction modes.

#Package pt.isel.reversi.cli.commands
# CLI Commands

Organizes and defines the commands available in the command-line interface, such as starting a game, making a move, showing help, and ending a match. Facilitates extension for new commands and ensures validation of user actions.

#Package pt.isel.reversi
# Main

Entry point of the Reversi application, responsible for initializing the main components, environment configuration, and launching the command-line interface. Integrates all packages, ensuring correct game flow and enabling future integrations.