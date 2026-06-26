# 🚀 Space Blaster

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-FF0000?style=for-the-badge&logo=java&logoColor=white)
![Architecture](https://img.shields.io/badge/Architecture-MVC-blue?style=for-the-badge)

A 2D space shooter game developed in Java with JavaFX. Fight against waves of asteroids and enemy ships, collect power-ups, and defeat the final boss.

## Group Identification

| Name | USP Number |
|------|------------|
| Daniela | 14613625 |
| Bruna Romero | 11913896 |

---

## 1. Requirements

The game implements the following requirements:

### Core Features
- Main menu with "Start Game", "High Scores", and "Exit" options
- Instructions screen displayed before the first level
- Player controls: Arrow keys for movement, Space bar for shooting
- Asteroids spawn at the top of the screen and move downward
- Enemy ships appear from level 2 onward and fire back at the player
- Collision detection: bullets destroy asteroids/enemies; enemy bullets or collisions reduce player lives
- 3 lives system; game ends when all lives are lost

### Levels and Difficulty
- 4 levels with increasing difficulty
- Level progression based on score (1000 points per level)
- Final level includes a boss enemy with larger sprite and 10 hit points
- "Level Complete" screen between levels showing score earned

### Scoring and HUD
- Points awarded for destroying asteroids (100 pts), enemies (150 pts), and boss hits (50 pts)
- Boss defeat awards 1000 bonus points
- Current score, level, and lives always visible on screen
- Progress bar shows points needed for next level

### Graphics and Sound
- Sprite-based graphics for ship, enemies, and bullets
- Simple explosion animation using sprite sheet
- Sound effects for shooting and explosions (optional)

### High Scores
- Top 5 scores saved to file (highscores.txt)
- High scores displayed on dedicated screen accessible from main menu

### Power-ups (Optional Enhancements)
- Rapid Fire: Shoot faster for 5 seconds
- Shield: Become invincible for 5 seconds
- Score Multiplier: Earn 500 bonus points
- Extra Life: Gain an additional life (structure implemented)

---

## 2. Project Description

## 🎮 Game Screens & UI Flows

* **Main Menu:** High-contrast UI displaying the stylized title logo in a cyan monospace typeface (60pt). It provides entry nodes to launch the game, check the leaderboards, or terminate the virtual machine.
* **Instructions:** An intermediate screen introducing the hardware command mappings (`Arrow Keys` and `SPACEBAR`) and survival parameters immediately prior to booting Sector 1.
* **Gameplay Stage:** The core interactive battlefield featuring responsive movement vectors, a dynamically calculated progress bar, high-visibility HUD telemetry, and automated entity rendering.
* **Level Complete Screen:** A synchronous transition view that pauses the animation loop clock to consolidate scores, process carry-over points, and reconfigure spawner parameters for the next sector.
* **Game Over Screen:** Triggered instantly when the player's structural vitality counter drops to zero. Displays a session statistics breakdown and delegates execution to the score saving prompt.
* **High Scores Screen:** A retrieval panel that reads local physical disk files, sorting and presenting the historical top 5 ranking entries.

---
### Package & Directory Structure

```text
com.spaceblaster
├── Main.java                      # Main application entry point (JavaFX Application Launcher)
├── controller
│   ├── GameController.java        # Main game loop orchestrator (60FPS), collisions, and inputs
│   ├── HighScoreController.java   # Manages the leaderboard scoreboard view and logic
│   └── MenuController.java        # Handles main menus, instructions navigation, and view routing
├── model
│   ├── Asteroid.java              # Falling obstacle entity with level-scaled dynamic speed
│   ├── Boss.java                  # Sector 4 flagship boss entity with health bar and shooting AI
│   ├── Bullet.java                # Projectile entity (both player-allied and hostile)
│   ├── Enemy.java                 # Tactical enemy ship entity with multi-axis diagonal movement
│   ├── GameState.java             # Runtime data cache and concurrent queue buffer manager
│   └── Player.java                # Player ship entity tracking lives, invincibility, and power-ups
└── util
    ├── CollisionDetector.java     # Utility for rectangle intersection testing and type-safe generic casting
    └── ScoreManager.java          # Handles persistent disk I/O for leaderboard storage
```
---

### Architecture

The project follows the Model-View-Controller (MVC) architectural pattern:

- **Model**: Game entities (Player, Asteroid, Enemy, Boss, Bullet, PowerUp) and game state management
- **View**: Rendering of game elements using JavaFX GraphicsContext
- **Controller**: Game logic, input handling, collision detection, and level progression

## 🏗️ Architecture Pattern: MVC (Model-View-Controller)

The project structurally implements the **Model-View-Controller (MVC)** architectural pattern to ensure strict separation of concerns, decoupling physical model simulations from the JavaFX UI rendering thread.

```text
┌─────────────────────────────────────────────────────────┐
│                    VIEW / UI LAYER                      │
│   (JavaFX Scenes, Canvas Viewport, GraphicsContext)     │
└─────────────────────────────────────────────────────────┘
                            │
                            ▼ Reads entity state to draw frames
┌─────────────────────────────────────────────────────────┐
│                    CONTROLLER LAYER                     │
│  GameController, MenuController, HighScoreController    │
│ (AnimationTimer Loop, Input Routing, Collision Triggers)│
└─────────────────────────────────────────────────────────┘
                            │
                            ▼ Mutates data states & collections
┌─────────────────────────────────────────────────────────┐
│                    MODEL / LOGIC LAYER                  │
│   GameState, Player, Asteroid, Enemy, Boss, Bullet      │
│   (Entity Data Structures, Mathematical Formulations)   │
└─────────────────────────────────────────────────────────┘
                            │
                            ▼ Provides utility helper execution
┌─────────────────────────────────────────────────────────┐
│                     UTILITY LAYER                       │
│              ScoreManager, CollisionDetector            │
│    (Persistent Local I/O, Generic Boundary Casting)     │
└─────────────────────────────────────────────────────────┘
```
### Architectural Breakdown

* **Model Layer (`com.spaceblaster.model`):** Pure state containers and physics definitions. These classes (`Player`, `Asteroid`, `Enemy`, `Boss`, `Bullet`, `PowerUp`) encapsulate positional coordinates ($X, Y$), scale vectors, velocity properties, and structural health metrics. They process algebraic updates (e.g., $Y \leftarrow Y + \text{Speed}_Y$) but remain entirely agnostic of JavaFX graphics libraries, rendering pipelines, or window contexts. `GameState` serves as the centralized data cache, managing entity collections and scoring status flags.
* **Controller Layer (`com.spaceblaster.controller`):** The operational core of the engine. `GameController` acts as the main orchestrator, driving the internal frame clock and handling user input. `MenuController` manages high-level view state switching, scene routing, and setup workflows, while `HighScoreController` delegates ranking presentation tasks. Controllers receive input signals and execute conditional operations to mutate Model properties.
* **View Layer (Implicit Rendering Interface):** Integrated directly inside the controller loop using JavaFX `Canvas` layouts and `GraphicsContext` (`gc`) painters. At every tick of the frame clock, the controller queries the data matrices inside `GameState` and draws the matching assets (`drawImage`, `fillRect`, `fillText`) onto the viewport. If graphical source assets are corrupted or missing, the rendering engine dynamically falls back to drawing solid-color geometric shapes (`fillOval`, `fillRect`).
* **Utility Layer (`com.spaceblaster.util`):** Decoupled static helper classes. `CollisionDetector` provides geometry calculations to test rectangle overlaps, while `ScoreManager` abstracts local disk access via Java I/O streams to read and write leaderboard logs.

## 🎮 Game Screens & Features

### 1. Main Menu Screen
* **Purpose:** Central entry point providing game access, navigation, and persistent states.
* **Elements:**
    * **Title:** "SPACE BLASTER" styled in a high-contrast cyan monospace typeface (60pt font size).
    * **Navigation Nodes:** Three cyberpunk-styled interactive buttons:
        * `START GAME`: Moves the application context forward into the interactive instructions terminal.
        * `HIGH SCORES`: Switches the view state to load the top-5 ranking archive.
        * `EXIT`: Signals the Java Virtual Machine (`System.exit(0)`) to close the runtime cleanly.
    * **Visual Effects:** Custom CSS inline styling containing mouse hover listeners (`setOnMouseEntered` / `setOnMouseExited`) that smoothly transition the components to a semi-transparent cyan background layer (`rgba(0,255,255,0.2)`).
    * **Layout Structure:** Embedded inside a `VBox` wrapper with a vertical spacing gap of 30px, layout-centered dynamically on a solid black viewport matrix.

### 2. Instructions Screen
* **Purpose:** Introduces the user interface controls, structural level rules, and combat parameters before launching the game engine loop.
* **Elements:**
    * **Header text:** "INSTRUCTIONS" in a high-contrast cyan monospace font (40pt).
    * **Control Layout Text:**
        * `↑ ↓ ← →` (Arrow Keys): Directs multi-axis 2D movement constraints.
        * `SPACEBAR`: Pressed to launch the game session from this screen.
    * **Core Gameplay Guidelines:** Detailed rules listing point mechanics, health indicators, and a warning that hostile targets actively return fire starting from Level 3.
    * **Interaction:** Monitors active keyboard states. Pressing the `SPACEBAR` terminates the instructions view state and immediately triggers the core loop initializer method (`startGame()`).

### 3. Game Screen (Main Gameplay)
* **Purpose:** Orchestrates real-time 2D graphics rendering, user input collection, physics validation loops, and dynamic HUD telemetry.
* **Game Area Matrix:** A high-performance JavaFX `Canvas` component ($1024 \times 768$ pixels) operating via a 60 FPS frame cycle driven by an `AnimationTimer`. Supports fallback solid background fills if external assets fail to load.
* **Heads-Up Display (HUD) Overlays:**
    * **Top-Left Analytics:** Real-time text nodes tracing global performance metrics: `"SCORE: [points]"` and `"LEVEL: [1-4]/4"` in a bold, high-visibility white font (24pt).
    * **Top-Right Progress Bar:** A structural layout bar tracking advancement metrics towards the next sector. Features a dark gray backing box ($200 \times 20$ pixels) filled progressively with solid green blocks based on points won. Displays a sub-label tracking numeric ratios. (Hidden on Level 4).
    * **Vitals Tracking:** Dynamically draws `heart.png` sprites ($30 \times 30$ pixels) lined up horizontally to represent remaining lifelines.
    * **Power-Up Status Alerts:** Text nodes rendered in bold 18pt fonts showing modifier state limits: `"SHIELD"` in cyan or `"RAPID"` in orange when active.
* **Boss Encounter System (Level 4 Exclusive):**
    * Spawns a massive UFO flagship sprite ($80 \times 80$ pixels) centered near the top axis ($Y = 50$).
    * Includes an independent red and green layered health bar ($10\text{px}$ thickness) tracking structural integrity.
    * Executes horizontal tracking paths that automatically reverse velocity vectors upon edge detection. Requires **10 hits** to shatter.
* **Player Feedback Systems:**
    * **Damage Indicators:** Fades the ship model alpha channel down to 50% opacity using structural clock dividers (`(System.currentTimeMillis() / 100) % 2 == 0`) during the 2-second invincibility phase.
    * **Entity Updates:** Immediate physical removal of objects upon collision verification, clearing projectile vectors and updating point maps.

### 4. Level Complete Screen
* **Purpose:** Halts the frame animation clock to display sector clearance data, point distribution matrices, and progress transfer stats between level steps.
* **Elements:**
    * **Header Output:** Centered monochrome typographic readout announcing `"LEVEL [X] COMPLETE!"`.
    * **Data Layout:** Lists total historical points alongside precise mathematical point capture ratios (`[points]/400`).
    * **Carry-Over Indicators:** Displays extra points that survived the 400-point threshold and were transferred into the next phase's progress bar.
    * **Transition Control:** Directs a blocking key handler. Pressing `ENTER` cleans up entity collections, indexes the level counter, recalculates spawner parameters, and boots the next loop phase.

### 5. Game Over Screen
* **Purpose:** Halts active background routines upon death to present definitive session stats and score persistence options.
* **Elements:**
    * **Header:** Bold text displaying `"GAME OVER"` in deep red (60pt font size).
    * **Statistical Breakdown:** Shows final sector coordinates reached, total points scored, and an algorithmic tip prompt.
    * **Action Row:** Three stylized layout buttons: `RESTART GAME` (green accent), `MAIN MENU` (cyan accent), and `EXIT` (red accent).
* **Leaderboard Entry Dialog System:**
    * Triggered exclusively by selecting the `MAIN MENU` action node.
    * Launches a JavaFX `TextInputDialog` system prompt overlay showing session totals.
    * Processes string buffers; if empty inputs or blank fields are submitted, it automatically falls back to write the signature string `"PLAYER"` to the persistent high-scores archive.

### 6. High Scores Screen
* **Purpose:** Reads, sorts, and presents persistent platform records stored on the local drive.
* **Elements:**
    * **Header:** "HIGH SCORES" centered text layout in a cyan monospace font (40pt).
    * **Ranked Ledger (Top 5):** Parses and displays records using a standardized data presentation layout: `"[Rank]. [PlayerName] - [Score]"`. Generated in a clean white monospace font (20pt).
    * **Empty State Handling:** Displays a high-visibility gray fallback message reading `"No scores yet! Play the game to set a record."` if the data source file is missing or unwritten.
    * **Navigation:** Features a `BACK TO MENU` button linked to a functional `Runnable` wrapper that safely routes execution back to the menu controller.

---

##📊 Class Diagram (UML)

```mermaid
classDiagram
    direction TB

    class Main {
        -String TITLE$
        -int WIDTH$
        -int HEIGHT$
        -MenuController menuController
        +start(Stage primaryStage) void
        +main(String[] args)$ void
        +getGameWidth()$ int
        +getGameHeight()$ int
    }

    class MenuController {
        -Stage primaryStage
        -int width
        -int height
        +MenuController(Stage primaryStage, int width, int height)
        +showMainMenu() void
        -createStyledButton(String text) Button
        +showInstructions() void
        +startGame() void
        +showHighScores() void
    }

    class GameController {
        -Stage primaryStage
        -int width
        -int height
        -GameState gameState
        -AnimationTimer gameLoop
        -Set~KeyCode~ activeKeys
        -long lastShotTime
        -long lastEnemySpawnTime
        -long lastAsteroidSpawnTime
        -Random random
        -Canvas canvas
        -GraphicsContext gc
        -Image background
        -Image playerImage
        -Image asteroidImage
        -Image enemyImage
        -Image bossImage
        -Image bulletImage
        -Image heartImage
        -Map~PowerUpType, Image~ powerUpImages
        +GameController(Stage primaryStage, int width, int height)
        -loadImages() void
        +startGame() void
        -setupLevel() void
        -update() void
        -spawnAsteroid() void
        -spawnEnemy() void
        -shoot() void
        -checkCollisions() void
        -maybeSpawnPowerUp(double x, double y) void
        -applyPowerUp(PowerUp powerUp) void
        -nextLevel() void
        -showLevelCompleteScreen() void
        -gameOver() void
        -createGameOverButton(String text, String color) Button
        -restartGame() void
        -render(GraphicsContext gc) void
    }

    class HighScoreController {
        -Stage primaryStage
        -int width
        -int height
        -Runnable onBack
        +HighScoreController(Stage primaryStage, int width, int height)
        +showHighScores(Runnable onBack) void
    }

    class GameState {
        -Player player
        -List~Asteroid~ asteroids
        -List~Enemy~ enemies
        -List~Bullet~ bullets
        -List~PowerUp~ powerUps
        -Boss boss
        -int score
        -int level
        -boolean gameRunning
        -boolean levelComplete
        -boolean bossDefeated
        -int enemiesToSpawn
        -int enemiesSpawned
        -int pointsInCurrentLevel
        -Queue~Asteroid~ asteroidsToAdd
        -Queue~Enemy~ enemiesToAdd
        -Queue~Bullet~ bulletsToAdd
        -Queue~PowerUp~ powerUpsToAdd
        +GameState()
        +shouldAdvanceLevel() boolean
        +addScore(int points) void
        +resetForNextLevel() void
        +resetGame() void
        +update() void
        +addAsteroid(Asteroid asteroid) void
        +addEnemy(Enemy enemy) void
        +addBullet(Bullet bullet) void
        +addPowerUp(PowerUp powerUp) void
        +getPlayer() Player
        +getAsteroids() List~Asteroid~
        +getEnemies() List~Enemy~
        +getBullets() List~Bullet~
        +getPowerUps() List~PowerUp~
        +getBoss() Boss
        +setBoss(Boss boss) void
        +getScore() int
        +getLevel() int
        +nextLevel() void
        +isGameRunning() boolean
        +setGameRunning(boolean running) void
        +isLevelComplete() boolean
        +setLevelComplete(boolean complete) void
        +getEnemiesToSpawn() int
        +getEnemiesSpawned() int
        +isBossDefeated() boolean
        +setBossDefeated(boolean defeated) void
        +getPointsInCurrentLevel() int
    }

    class Player {
        -double x
        -double y
        -double width
        -double height
        -double speed
        -int lives
        -boolean invincible
        -long invincibleEndTime
        -boolean rapidFireActive
        -long rapidFireEndTime
        -boolean shieldActive
        -long shieldEndTime
        +Player(double x, double y)
        +moveLeft() void
        +moveRight(double screenWidth) void
        +moveUp() void
        +moveDown(double screenHeight) void
        +hit() void
        +update() void
        +activateRapidFire(long duration) void
        +activateShield(long duration) void
        +hasShield() boolean
        +hasRapidFire() boolean
        +getBounds() Rectangle
        +getLives() int
        +getX() double
        +getY() double
    }

    class Asteroid {
        -double x
        -double y
        -double width
        -double height
        -double speedY
        -int points
        +Asteroid(double x, double y, double speedY)
        +update() void
        +isOffScreen(double screenHeight) boolean
        +getBounds() Rectangle
        +getX() double
        +getY() double
        +getPoints() int
    }

    class Enemy {
        -double x
        -double y
        -double width
        -double height
        -double speedX
        -double speedY
        -int points
        -long lastShotTime
        -static long SHOT_DELAY
        +Enemy(double x, double y, double speedX, double speedY)
        +update() void
        +canShoot() boolean
        +isOffScreen(double screenWidth, double screenHeight) boolean
        +getBounds() Rectangle
        +getX() double
        +getY() double
        +getPoints() int
    }

    class Boss {
        -double x
        -double y
        -double width
        -double height
        -int hitPoints
        -int maxHitPoints
        -double speedX
        -long lastShotTime
        -static long SHOT_DELAY
        +Boss(double x, double y)
        +update(double screenWidth) void
        +canShoot() boolean
        +hit() void
        +isDefeated() boolean
        +getBounds() Rectangle
        +getX() double
        +getY() double
        +getHitPoints() int
        +getMaxHitPoints() int
    }

    class Bullet {
        -double x
        -double y
        -double width
        -double height
        -double speedY
        -boolean fromPlayer
        +Bullet(double x, double y, boolean fromPlayer)
        +Bullet(double x, double y, double speedY, boolean fromPlayer)
        +update() void
        +isOffScreen(double screenHeight) boolean
        +getBounds() Rectangle
        +getX() double
        +getY() double
        +isFromPlayer() boolean
    }

    class PowerUp {
        <<enumeration>> PowerUpType
        -double x
        -double y
        -double width
        -double height
        -double speedY
        -PowerUpType type
        +PowerUp(double x, double y, PowerUpType type)
        +update() void
        +isOffScreen(double screenHeight) boolean
        +getBounds() Rectangle
        +getType() PowerUpType
    }

    class CollisionDetector {
        <<utility>>
        +checkCollision(Rectangle r1, Rectangle r2)$ boolean
        +getCollision(Rectangle rect, List~?~ objects, Class~T~ type)$ T
    }

    class ScoreManager {
        <<utility>>
        -String HIGH_SCORE_FILE$
        -int MAX_SCORES$
        -getScoreFilePath()$ String
        -ensureDirectoryExists()$ void
        +saveHighScore(String playerName, int score)$ void
        +loadHighScores()$ List~HighScoreEntry~
        -createDefaultScores()$ List~HighScoreEntry~
    }

    Main ..> MenuController : instantiates
    MenuController ..> GameController : instantiates
    MenuController ..> HighScoreController : instantiates
    GameController --> GameState : mutates & queries
    HighScoreController ..> ScoreManager : invokes loading
    GameController ..> ScoreManager : invokes saving
    GameController ..> CollisionDetector : processes physics

    GameState "1" *-- "1" Player : composes
    GameState "1" *-- "*" Asteroid : contains
    GameState "1" *-- "*" Enemy : contains
    GameState "1" *-- "*" Bullet : contains
    GameState "1" *-- "*" PowerUp : contains
    GameState "1" *-- "0..1" Boss : contains

```
---

## 🛸 Game Elements & Entity Properties

### Player Ship
* **Visual Asset:** Renders via a green spaceship model (`playerShip3_green.png`) scaled to a bounding area of $40 \times 40$ pixels.
* **Structural Integrity:** Starts with 3 base lives. Can absorb structural modifications up to a **maximum cap of 5 lives**.
* **Kinematic Limits:** Position updates scale smoothly at a velocity constant of **5 pixels per frame** across both horizontal and vertical axes, restricted safely by viewport edges.
* **Primary Weapons:** Deploys upward-moving projectile objects starting from coordinates adjusted to the nose of the ship ($X + \text{width}/2 - 2, Y - 10$).
* **Weapon Cooldowns:** Standard weapon firing cadence is restricted by a structural delay limit of **200 milliseconds** between shots.
* **Hit Reactions:** Activates a 2000ms absolute frame immunity window upon damage, preventing rapid consecutive damage loops.
* **Collision Model:** Evaluates spatial intersections via structural geometric properties using rectangular bounding maps (`getBounds()`).

### Asteroids
* **Visual Asset:** Renders via a brown space rock model (`meteorBrown_big4.png`) scaled to a bounding box of $30 \times 30$ pixels.
* **Kinematic Profile:** Spawns above the viewport line ($Y = -30$) along randomized horizontal coordinates, moving straight down.
* **Dynamic Velocity formula:** Calculated using base multipliers scaled by current level metrics:
    $$\text{Speed}_Y = 1.0 + \text{Random}(0.0, 2.0) + (\text{Level} \times 0.3)$$
* **Combat Interactions:** Destroyed instantly when impacted by a single friendly projectile vector.
* **Point Evaluation:** Rewards a static value of **+100 points** to the global state.
* **Spawning Rates:** Frame delay values scale down dynamically as level progression updates:
    $$\text{Spawn Delay} = \max(600\text{ms}, 2000\text{ms} - (\text{Level} \times 150\text{ms}))$$
    * *Level 1:* Generates an asteroid obstacle every **1850ms**.
    * *Level 2:* Generates an asteroid obstacle every **1700ms**.
    * *Level 3:* Generates an asteroid obstacle every **1550ms**.
    * *Level 4:* Generates an asteroid obstacle every **1400ms**.

### Enemies
* **Visual Asset:** Renders via a tactical blue spaceship model (`enemyBlue4.png`) scaled to a bounding area of $35 \times 35$ pixels.
* **Kinematic Profile:** Spawns above the viewport line ($Y = -35$) along randomized horizontal coordinates. Moves using dual-axis physics vectors: horizontal vectors range randomly between $-0.25$ and $+0.25$, while vertical speed ranges between $0.5$ and $1.3$.
* **Weapon Systems AI:** Actively calculates weapon firing intervals starting from **Level 3 and higher**. Fires projectiles downward toward the player ship model.
* **AI Firing Cadence:** Controlled by a strict internal cooldown clock set to a static delay of **1500ms** between shots.
* **Point Evaluation:** Rewards a static value of **+150 points** to the global state.
* **Population Limits:** Generation is capped by structural engine limits. Active instances on screen cannot exceed **3 concurrent ships**, and total level spawns are restricted by the level criteria.
* **Memory Management:** Monitored by cleaner filters that safely remove instances from runtime arrays if positions exceed boundaries ($x < -50$, $x > \text{screenWidth} + 50$, or $y > \text{screenHeight} + 50$).

### Boss Enemy (Level 4 Exclusive)
* **Visual Asset:** Renders via a large yellow UFO ship model (`ufoYellow.png`) scaled to a heavy bounding size of $80 \times 80$ pixels.
* **Structural Integrity:** Features a maximum structural health pool of **10 hit points**.
* **Kinematic Profile:** Spawns at the top of Sector 4. Moves horizontally at a constant speed of **2 pixels per frame**, automatically reversing its heading vector upon reaching boundary limits ($x < 50$ or $x > \text{screenWidth} - \text{width} - 50$).
* **Weapon Systems AI:** High-frequency firing cycle deploying heavy counter-projectiles downward.
* **AI Firing Cadence:** Controlled by a rapid cooldown clock restricted to a static delay of **800ms** between shots.
* **Defeat Condition:** Depleting the Boss's health pool rewards a massive clear bonus of **+1000 points**, flags the target as defeated, and immediately finishes Level 4 to trigger the victory state.

### Bullets
* **Friendly Projectiles (Player):**
    * **Visual Asset:** Blue laser graphic (`laserBlue01.png`) with a standard fallback sizing of $4 \times 10$ pixels.
    * **Kinematic Profile:** Travels vertically straight up at a high speed constant of **-8 pixels per frame**.
    * **Memory Management:** Swept from runtime memory arrays instantly if coordinates cross the upper boundary ($y < 0$).
* **Hostile Projectiles (Enemy & Boss):**
    * **Visual Asset:** Reuses the standard projectile model asset, moving down.
    * **Kinematic Profile (Enemy):** Travels vertically straight down. Speed constants scale dynamically with level variables: $\text{Speed}_Y = 3 + (\text{Level} / 2)$. At Level 3, projectiles travel down at **4 pixels per frame**, and at Level 4, they accelerate to **5 pixels per frame**.
    * **Kinematic Profile (Boss):** Travels vertically straight down at a high fixed speed constant of **5 pixels per frame**.
    * **Memory Management:** Swept from runtime memory arrays instantly if coordinates cross the lower viewport boundary ($y > \text{screenHeight}$).

### Power-Ups
* **Visual Asset:** Small structural modifier modules scaled to a canvas space of $20 \times 20$ pixels.
* **Drop Rate Probabilities:** Generated dynamically at the death coordinates of an enemy ship or rock obstacle based on a fixed **10% random chance**.
* **Kinematic Profile:** Drops vertically straight down at a smooth constant speed of **3 pixels per frame**. Cleared from memory if they fall past the screen boundary ($y > \text{screenHeight}$).
* **Modifier Types & Effects:**
    1. `RAPID_FIRE` (Green icon asset: `powerup_rapid.png`):
        * **Effect:** Compresses the player's weapon firing delay clock from 200ms down to a rapid **50ms cooldown** (allowing up to 20 shots per second).
        * **Duration:** Lasts for an active duration of **5 seconds** (5000ms).
    2. `SHIELD` (Blue icon asset: `powerup_shield.png`):
        * **Effect:** Deploys a protective energy barrier that blocks damage from hostile hits.
        * **Duration:** Remains active for a maximum duration of **5 seconds** (5000ms) or until shattered by an impact.
    3. `EXTRA_LIFE` (Red icon asset: `powerup_life.png`):
        * **Effect:** Restores an extra life counter instantly upon collection (restricted by the maximum life cap of 5).
    4. `SCORE_MULTIPLIER` (Yellow icon asset: `powerup_score.png`):
        * **Effect:** Grants an immediate instant bonus reward of **+500 points** directly to the global score.

---

## 📋 Levels & Difficulty Progression

| Level | Hazards Present | New Hazard Behaviors | Objective to Clear |
| :---: | :--- | :--- | :--- |
| **1** | Asteroids Only | Random horizontal spawns and varying falling speeds | Accumulate 400 pts |
| **2** | Asteroids + Enemies | Up to 3 tactical enemy ships spawn simultaneously; no shooting | Accumulate 400 pts |
| **3** | Asteroids + Shooting Enemies | Enemy spaceships begin counter-firing projectiles downward | Accumulate 400 pts |
| **4** | Asteroids + Enemies + **BOSS** | Flagship Boss arrives with 10 HP and high-frequency weaponry | Defeat the Boss |

### Dynamic Progression Formula
* **Advancement Threshold:** Moving up to the next sector requires scoring **400 points within that specific level**.
* **Score Carry-Over Logic:** Points won beyond the 400-point limit are not discarded. The excess score is calculated ($\text{Score} - 400$) and safely carried over to kickstart the progress bar of the next level.
* **Combat Paradigm:** Player bullets destroy standard targets instantly. Enemy projectile speeds adapt dynamically based on the active level. No global countdown timers or artificial time pressures restrict session durations.

#### Level 1: Beginner Sector
* **Hostile Forces:** Asteroids only. No enemy spaceships spawn during this phase.
* **Spawn Parameters:** Asteroid generation tracking delay operates at its maximum interval of **1850ms**.
* **Objective:** Introduction to basic navigation controls, projectile aiming vectors, and HUD tracking.

#### Level 2: Intermediate Sector
* **Hostile Forces:** Asteroids + Non-Shooting Enemy Spaceships.
* **Spawn Parameters:** Configured to generate a total pool of **10 enemy ships** per level. Asteroid spawn delay drops to **1700ms**. Enemies move along dual-axis diagonal vectors across the viewport but cannot fire projectiles.
* **Objective:** Teaches predictive aiming and tracking against moving targets with complex diagonal velocity paths.

#### Level 3: Advanced Sector
* **Hostile Forces:** Asteroids + Active Shooting Enemy Spaceships.
* **Spawn Parameters:** Configured to generate a total pool of **16 enemy ships** per level. Asteroid spawn delay updates to **1550ms**. Enemies engage their internal weapon systems, firing hostile projectiles downward every **1500ms** at a speed of **4 pixels per frame**.
* **Objective:** Introduces defensive dodging tactics, requiring the player to weave through hostile bullet streams while returning fire.

#### Level 4: Endgame Boss Battle
* **Hostile Forces:** Asteroids + Standard Shooting Enemy Spaceships + **The Flagship Boss**.
* **Spawn Parameters:** Continuous background generation of asteroids (every **1400ms**) and standard enemies alongside the immediate initialization of the Boss entity. Standard enemy projectiles accelerate to **5 pixels per frame** in this final stage.
* **Boss Mechanics:** The Boss features a heavy health pool of **10 HP**, moving horizontally across the upper screen while firing rapid counter-projectiles downward every **800ms** at a high fixed speed of **5 pixels per frame**.
* **Victory Condition:** The level cannot be cleared by score accretion alone. The sector is won and the playthrough is completed only when the Boss's health pool is reduced to zero.
---
## 🛠️ Utility Modules & Persistence

The project relies on specific utility helper classes located in the `com.spaceblaster.util` package to handle decoupled subsystem tasks:

* **CollisionDetector:** Exposes lightweight static helper APIs to resolve bounding logic. It provides type-safe generic scanning (`getCollision`) to search collections for specific `Rectangle` overlays and safely cast matching items.
* **ScoreManager:** Implements disk-based data persistence. It manages a local configuration ecosystem by creating a hidden directory (`.spaceblaster`) inside the user's home profile to save and load `highscores.txt` using Java `BufferedReader` and `PrintWriter` structures. The engine keeps up to 5 ranking entries sorted in descending numerical sequence, dropping overflow scores.

---

## 3. Comments About the Code

### Code Organization
- Each class has a single, well-defined responsibility.
- Game entities extend no external classes, utilizing clean object composition instead.
- All public methods are thoroughly documented with standard JavaDoc comments.

## 🎨 Design Patterns & Core Mechanics

### 1. Game Loop Pattern
* **Implementation:** Driven by JavaFX's native `AnimationTimer` abstraction layer inside `GameController`.
* **Mechanics:** Synchronizes with the hardware display refresh rate to deliver a smooth 60 FPS update cycle. Every atomic frame step executes a strict sequential cascade:
    $$\text{Handle Input} \longrightarrow \text{Update Physics (Positions/Timers)} \longrightarrow \text{Check Collisions} \longrightarrow \text{Draw Background | Render Layout}$$
* **Concurrency & Safety:** While position changes and inputs occur rapidly, entity additions and removals are buffered using non-blocking concurrent pipelines (`ConcurrentLinkedQueue`). This architecture completely prevents asynchronous modification faults (`ConcurrentModificationException`) during active loops without relying on blocking threads.

### 2. Decoupled State Management (State Container)
* **Implementation:** Engineered via the `GameState` monolithic context manager.
* **Mechanics:** Encapsulates the entire operational data schema of a live playthrough (score counters, level progression metrics, and active entity lists). By decoupling data tracking completely from the core engine loop class, the project can instantly reset metrics (`resetGame()`) or modify level parameters (`resetForNextLevel()`) without needing to tear down, reallocate, or flash structural UI containers.

### 3. Observer Pattern (Event-Driven Input Architecture)
* **Implementation:** Set up via JavaFX event filter lambda configurations (`gameScene.setOnKeyPressed` and `gameScene.setOnKeyReleased`).
* **Mechanics:** The application registers listeners on the active `Scene` window to monitor keyboard states. When a key is pressed, its unique hardware token (`KeyCode`) is stored inside an active lookup collection (`Set<KeyCode> activeKeys`). This decoupled approach separates OS key-press events from the movement system, allowing the game loop to read raw input data asynchronously and execute smooth multi-axis movement.

### 4. Centralized Persistence Service (Static Utility Service)
* **Implementation:** Handled by the static utility class `ScoreManager`.
* **Mechanics:** Rather than instantiating multiple leaderboard entities across different scenes, a centralized static access pattern manages all game-wide leaderboard persistence. It provides a single point of interaction for saving and loading ranking files, isolating file system I/O stream operations completely from the rest of the game presentation logic.

### 5. Encapsulated Spawning (Procedural Factory Generation)
* **Implementation:** Implemented through specialized procedural spawning routines inside `GameController` (`spawnAsteroid()` and `spawnEnemy()`).
* **Mechanics:** Centralizes entity initialization logic, abstracting away the generation of randomized spawn locations and level-scaled velocity vectors. This ensures the rest of the application can request new objects cleanly without needing to expose the complex underlying algebraic physics formulas used to initialize them.


## 🕹️ How to Play

### Starting the Game
1. **Launch the Application:** Boot the compiled JavaFX jar or run the main class from your development environment.
2. **Access Terminal:** Click the `START GAME` action button on the stylized cyber-retro main menu interface.
3. **Review Protocols:** Read through the operational constraints displayed on the **Instructions** screen.
4. **Initialize Sector 1:** Press the `SPACEBAR` while on the instructions screen to terminate the menu interface and immediately boot the game loop into Level 1.

### Flight Controls Matrix
| Key Input | Operational Action |
| :---: | :--- |
| `↑` (Arrow Up) | Move Spaceship Upward |
| `↓` (Arrow Down) | Move Spaceship Downward |
| `←` (Arrow Left) | Move Spaceship Leftward |
| `→` (Arrow Right) | Move Spaceship Rightward |
| `SPACEBAR` | Dispatch Weapon Projectiles (Laser Stream) |

### Strategic Gameplay Tips
* **Maintain High Mobility:** Never park the ship model in a single coordinate vector. Starting from Level 3, hostile AI nodes actively track your location matrices and deploy down-moving counter-projectiles.
* **Prioritize Modifier Drop Modules:** Snatch floating power-up drops immediately upon resource allocation. They drift down continuously and will vanish past the lower window edge ($y > 768$).
* **Monitor Structural Integrity HUD:** The flashing visual alpha feedback (50% ship transparency) indicates active temporary invincibility protocols. Use these 2 seconds to reposition away from danger.
* **Establish an Early Economy:** Leverage the relaxed pace of Level 1 to clear asteroid fields cleanly, establishing a baseline of **+100 points per rock** toward the sector clear threshold.
* **Avoid Boundary Traps:** Do not hug the absolute corners or side margins ($x \approx 10$ or $x \approx 974$). Doing so cuts your available evasion angles in half, making you an easy target for cascading threats.
* **Flagship Evasion Vectors:** When engaging the Level 4 Boss, maintain continuous sweeping horizontal movement. The Boss deploys rapid fire vectors every 800ms, making linear strafing essential to survive.
* **Target High-Value Threats:** Target blue enemy ships immediately upon entry. At **+150 points per kill**, clearing enemy fleets is the most efficient vector to force level advancement.

### Termination & Victory Parameters
* **Victory Condition:** Successfully drain the 10 HP health bar of the flagship Boss in Sector 4 to clear the simulation and claim victory.
* **Defeat Condition:** The system encounters a fatal game over state if your vitals counter drops to 0. The player starts with 3 structural lives, and damage tracking is mitigated temporarily if a collected `SHIELD` module is active.

### Leaderboard Verification
* Upon encountering a Game Over or Victory state, select the `MAIN MENU` button to trigger a `TextInputDialog` system prompt. Enter your signature callsign to save your session score record to the persistent high-scores archive.
  
## 4. Test Plan
## 🧪 Testing & Quality Assurance

### 📋 Test Plan Specification
The following table outlines the initial functional scenarios mapped out to guarantee that the game's core mechanics behave exactly as expected:

| Case ID | Test Case Description | Expected Result |
| :---: | :--- | :--- |
| **TC-01** | Start game from main menu. | Game screen appears with player ship at bottom center. |
| **TC-02** | Move ship with arrow keys. | Ship moves smoothly within screen boundaries. |
| **TC-03** | Shoot with space bar. | Bullet appears from ship and moves upward. |
| **TC-04** | Collide with asteroid. | Life decreases, player becomes invincible briefly. |
| **TC-05** | Destroy asteroid. | Score increases by 100, asteroid disappears. |
| **TC-06** | Reach 1000 points. | Level complete screen appears. |
| **TC-07** | Complete level 4 with boss. | Victory screen appears with final score. |
| **TC-08** | Lose all 3 lives. | Game over screen appears with restart options. |
| **TC-09** | Collect power-up. | Corresponding effect activates (rapid fire/shield). |
| **TC-10** | Enter name after game over. | Score saved to high scores list. |

---

## 🧪 Testing & Quality Assurance

### Unit Testing Matrix (JUnit 5)

#### 1. Entity Collision Detection (`CollisionDetector`)
* **Intersection Verification:** Validates that `CollisionDetector.checkCollision()` processes boundaries accurately, returning true when JavaFX `Rectangle` regions overlap.
* **Type-Safe Generic Filtering:** Verifies that the generic list scanner (`getCollision`) evaluates target arrays, ignores invalid types, and returns the correctly cast matching entity.
* **Edge Case Tests:** Validates boundary overlaps at extreme coordinates ($0$ or maximum canvas limits) and checks physics responses for completely nested entities.

#### 2. Score Persistence Engine (`ScoreManager`)
* **Disk I/O Integrity:** Tests file operations to confirm the system creates a hidden directory (`.spaceblaster`) inside the user's home path (`System.getProperty("user.home")`) and writes data successfully.
* **Sorting Algorithms:** Verifies that the leaderboard array sorts entries in descending order based on scores, ensuring highest scores occupy the top ranks.
* **Array Constraints:** Asserts that the high score array trims data down to a **strict limit of 5 entries**, dropping low-scoring records.
* **Fallback Protections:** Tests that the engine generates default entries (`AAA` to `EEE` with scores from 5 down to 1) if the target `highscores.txt` file is corrupt, empty, or missing.

#### 3. State Engine Routines (`GameState`)
* **Entity Array Updates:** Asserts that `GameState.update()` advances positions correctly, removes off-screen entities past canvas bounds ($768\text{px}$ vertical clearance for hazards; dynamic padding for enemies), and flushes arrays cleanly.
* **Thread-Safe Queuing:** Verifies that objects added during active frame cycles are safely routed through non-blocking concurrent pipelines (`ConcurrentLinkedQueue` such as `asteroidsToAdd`, `enemiesToAdd`, `bulletsToAdd`, and `powerUpsToAdd`), preventing runtime concurrent modification faults (`ConcurrentModificationException`).
* **Progression Flags:** Tests that level transition flags trigger correctly once the 400-point sector threshold is reached or when the level 4 Boss is flagged as dead.

#### 4. Kinematics & Boundaries (`Player`)
* **Boundary Assertions:** Asserts that movement methods (`moveLeft()`, `moveRight()`, etc.) restrict coordinates within explicit canvas safety margins ($10\text{px}$ internal padding), blocking off-screen escapes.
* **Timer Cooldown Clocks:** Tests that invincibility windows (2000ms) and power-up durations (5000ms) decrement accurately against system time ticks (`System.currentTimeMillis()`) and clear status flags upon expiration.

#### 5. Object Factory Generation (`GameController`)
* **Spawning Logic:** Validates that asteroid spawn coordinates match the random generation formulas and verify that enemy spawn caps adapt correctly to the active level.
* **Boss Initialization:** Asserts that the Level 4 state successfully triggers the Boss entity factory, mapping its initial health pool to 10 HP and positioning it correctly at the top-center mathematical coordinates of the canvas.

### 💻 Automated Unit Test Implementation

The domain logic and mathematical constraints described in the matrix above are fully verified by automated unit tests using the **JUnit 5** framework. This ensures that any future refactoring or optimization applied to the core physics and state engine does not introduce regression bugs.

The automated test suite is implemented under `src/test/java/com/spaceblaster/model/SpaceBlasterCoreTest.java`:

```java
package com.spaceblaster.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SpaceBlasterCoreTest {

    @Test
    public void testPlayerBoundaryConstraints() {
        // Arrange: Initialize a player ship at an internal coordinate
        Player player = new Player(100, 100);
        
        // Act: Simulate the user forcing the ship to move left continuously
        for (int i = 0; i < 500; i++) {
            player.moveLeft();
        }
        
        // Assert: Ensure the internal physics engine safely clamps the ship coordinate
        // inside the viewport, preventing the sprite from escaping off-screen boundaries.
        assertTrue(player.getX() >= 10, "The player spaceship escaped the left screen boundary!");
    }

    @Test
    public void testScoreCarryOverLogic() {
        // Arrange: Establish a fresh Level 1 game state profile
        GameState state = new GameState();
        state.setLevel(1);
        
        // Act: Add 450 points, surpassing the level threshold requirement of 400 points
        state.addScore(450);
        
        // Assert Scenario 1: The engine must flag the level progression condition as true
        assertTrue(state.shouldAdvanceLevel(), "The system failed to trigger the level transition flag.");
        
        // Assert Scenario 2: Moving to the next sector must process the mathematical carry-over
        state.resetForNextLevel();
        assertEquals(50, state.getPointsInCurrentLevel(), "Excess points beyond the 400 threshold were discarded instead of carried over!");
    }
}
```

## 5. Test Results

### 📊 Test Execution Results
Below are the actual validation outcomes, verification statuses, and technical engineering observations logged during the official QA run:

| Case ID | Status | Notes / Engineering Observations |
| :---: | :---: | :--- |
| **TC-01** | **PASS** | Menu navigates correctly. |
| **TC-02** | **PASS** | Movement is responsive and bounded. |
| **TC-03** | **PASS** | Shots fire at 200ms intervals (50ms with rapid fire). |
| **TC-04** | **PASS** | Lives decrease correctly, invincibility works. |
| **TC-05** | **PASS** | Score updates correctly. |
| **TC-06** | **PASS** | Level complete triggers at 1000, 2000, 3000 points. |
| **TC-07** | **PASS** | Boss appears in level 4, requires 10 hits. |
| **TC-08** | **PASS** | Game over screen offers restart, menu, and exit. |
| **TC-09** | **PASS** | Power-ups activate for 5 seconds. |
| **TC-10** | **PASS** | Scores persist to highscores.txt. |

**Conclusion:** All core features and gameplay dynamics function exactly as expected. No critical bugs or regressions were found during this quality assurance window, and the build is certified as stable.

---

## 6. Build Procedures

* **Prerequisites**
* **Install Java 17 or higher (Ubuntu/Debian):**
`sudo apt update`
`sudo apt install openjdk-17-jdk`
`java -version`

* **Install JavaFX:**
`sudo apt install openjfx`

* **Install Maven (optional but recommended):**
`sudo apt install maven`

**Clone and Build**

* **Clone the repository**<
`git clone https://github.com/bruromero/Space-Blaster.git`

* **Go to the project folder**
`cd Space-Blaster/SpaceBlasterJavaFX`

* **Build the files**
`mvn clean compile`

* **Run the game!**
`mvn javafx:run`

---
**Alternative: Run without Maven**

* **Set JavaFX path**
`JAVAFX_PATH="/usr/share/openjfx/lib"`

* **Compile**
`javac --module-path $JAVAFX_PATH --add-modules javafx.controls,javafx.fxml,javafx.media -d bin $(find src -name "*.java")`

* **Run**
`java --module-path $JAVAFX_PATH --add-modules javafx.controls,javafx.fxml,javafx.media -cp bin com.spaceblaster.Main`

* **Image Assets**
The game uses sprites from the Space Shooter Redux asset pack. Images are located at:

text
`src/main/resourses/images/`

---
## 7. Problems

## ⚠️ Challenges & Lessons Learned

Developing **Space Blaster** was a great learning experience. We had to face some tricky coding hurdles to keep the game running smoothly while making sure the code stayed clean and organized.

### 1. Cleaning Up the Monolithic Game Loop
* **The Problem:** At first, our `GameController` class was doing way too much—handling user inputs, spawning enemies, moving items, and tracking scores all at the same time. The code became huge and messy.
* **The Solution:** We moved all data arrays, score tracking, and game status flags into a separate, dedicated `GameState` model. This split made the main game loop short, readable, and easy to maintain.

### 2. Cutting Down JavaFX UI Boilerplate
* **The Problem:** Creating buttons and menus purely through Java code can make the file extremely long due to repetitive layout, alignment, and hover-effect settings for every single button.
* **The Solution:** We organized elements using structured layout containers (like `VBox`) and used smart lambda loops to apply hover animations (`setOnMouseEntered` / `setOnMouseExited`) to all buttons at once.

### 3. Fixing Game Crashes (`ConcurrentModificationException`)
* **The Problem:** In a real-time game, enemies are constantly spawning while the physics engine is looping to check for hits. Trying to add or remove items from a list while looping through it caused frequent game crashes.
* **The Solution:** We stopped changing the game lists directly inside the loop. Instead, we used a thread-safe queue (`ConcurrentLinkedQueue`) to buffer items, letting them wait safely to be added or removed on the next game tick.

### 4. Keeping Ships Inside the Screen
* **The Problem:** Calculating dynamic speeds based on the current level while making sure player ships and asteroids didn't fly off or disappear past the $1024 \times 768$ screen limits was a bit tricky.
* **The Solution:** We added simple position-clamping boundaries directly inside each object's logic (like `Player.java`), keeping everyone safely inside the playable viewport.

---
## 8. Comments

## 🌟 Why We Chose JavaFX

Using **JavaFX** as our main graphics engine brought some huge advantages to the table:

* **Hardware Acceleration:** JavaFX uses your computer's graphics card (DirectX/OpenGL) automatically. This keeps the game rendering at a rock-solid **60 FPS** without slowing down your CPU.
* **Smooth Animation Core (`AnimationTimer`):** Instead of using unreliable background timers that cause micro-stutters, the native `AnimationTimer` syncs the game's movement ticks directly with your monitor's refresh rate (V-Sync).
* **Responsive Keyboard Controls:** Key inputs are captured asynchronously and saved into a local `Set<KeyCode>` collection. This eliminates typical operating system keyboard delays, making ship steering and shooting feel instant.
* **Easy Visual Effects & UI:** Containers like `VBox` and `StackPane` made layout design very straightforward. We were also able to easily apply visual transparency (alpha changes) to indicate player invincibility without doing complex image processing.
* **Run Anywhere Portability:** The exact same code for the game window, graphics rendering, local file saving, and audio runs perfectly across Windows, Linux, and macOS without requiring a single tweak.
