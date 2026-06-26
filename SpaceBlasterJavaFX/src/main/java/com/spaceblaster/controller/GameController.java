package com.spaceblaster.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import com.spaceblaster.model.Asteroid;
import com.spaceblaster.model.Boss;
import com.spaceblaster.model.Bullet;
import com.spaceblaster.model.Enemy;
import com.spaceblaster.model.GameState;
import com.spaceblaster.model.Player;
import com.spaceblaster.model.PowerUp;
import com.spaceblaster.util.ScoreManager;

import javafx.animation.AnimationTimer;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

/**
 * Main game controller that manages all game logic, rendering, and game state.
 * This class handles the game loop, entity spawning, collision detection,
 * level progression, and user input.
 * 
 * @author Space Blaster Team
 * @version 1.0
 */
public class GameController {

    /** The primary stage (window) where the game is displayed. */
    private Stage primaryStage;

    /** The width of the game area in pixels. */
    private int width;

    /** The height of the game area in pixels. */
    private int height;

    /** The current state of the game containing all entities and data. */
    private GameState gameState;

    /** The main game loop that runs at 60 FPS. */
    private AnimationTimer gameLoop;

    /** The set of keys currently being pressed by the player. */
    private Set<KeyCode> activeKeys;

    /** Timestamp of the last shot fired by the player. */
    private long lastShotTime;

    /** Timestamp of the last enemy spawn. */
    private long lastEnemySpawnTime;

    /** Timestamp of the last asteroid spawn. */
    private long lastAsteroidSpawnTime;

    /** Random number generator for spawn positions and probabilities. */
    private Random random;

    /** The canvas where all game graphics are rendered. */
    private Canvas canvas;

    /** The graphics context used for drawing on the canvas. */
    private GraphicsContext gc;

    /** Background image for the game. */
    private Image background;

    /** Player ship image. */
    private Image playerImage;

    /** Asteroid image. */
    private Image asteroidImage;

    /** Enemy ship image. */
    private Image enemyImage;

    /** Boss ship image. */
    private Image bossImage;

    /** Bullet image. */
    private Image bulletImage;

    /** Heart image for lives display. */
    private Image heartImage;

    /** Map of power-up type to corresponding images. */
    private Map<PowerUp.PowerUpType, Image> powerUpImages;

    /**
     * Constructs a new GameController with the specified parameters.
     * 
     * @param primaryStage The primary stage (window) for the game
     * @param width        The width of the game area
     * @param height       The height of the game area
     */
    public GameController(Stage primaryStage, int width, int height) {
        this.primaryStage = primaryStage;
        this.width = width;
        this.height = height;
        this.activeKeys = new HashSet<>();
        this.random = new Random();

        loadImages();
    }

    /**
     * Loads all game images from the file system.
     * Images are loaded from the resources/images directory.
     * Falls back to colored rectangles if images fail to load.
     */
    private void loadImages() {
        try {
            // Obtém o diretório atual do projeto
            String userDir = System.getProperty("user.dir");
            String basePath = userDir + "/src/main/resourses/images/";

            background = new Image("file:" + basePath + "darkPurple.png");
            playerImage = new Image("file:" + basePath + "playerShip3_green.png");
            asteroidImage = new Image("file:" + basePath + "meteorBrown_big4.png");
            enemyImage = new Image("file:" + basePath + "enemyBlue4.png");
            bossImage = new Image("file:" + basePath + "ufoYellow.png");
            bulletImage = new Image("file:" + basePath + "laserBlue01.png");
            heartImage = new Image("file:" + basePath + "heart.png");

            powerUpImages = new HashMap<>();
            powerUpImages.put(PowerUp.PowerUpType.RAPID_FIRE,
                    new Image("file:" + basePath + "powerup_rapid.png"));

            int loadedCount = 0;
            if (background != null)
                loadedCount++;
            if (playerImage != null)
                loadedCount++;
            if (asteroidImage != null)
                loadedCount++;
            if (enemyImage != null)
                loadedCount++;
            if (bossImage != null)
                loadedCount++;
            if (bulletImage != null)
                loadedCount++;
            if (heartImage != null)
                loadedCount++;

            System.out.println("Loaded " + loadedCount + "/7 images successfully!");

        } catch (Exception e) {
            System.err.println("Error loading images: " + e.getMessage());
        }

        com.spaceblaster.util.SoundManager.loadSound("shoot", "shoot.wav");
        com.spaceblaster.util.SoundManager.loadSound("explosion", "explosion.wav");
    }
    
    /**
     * Starts the game by initializing the game state, setting up the level,
     * creating the game scene, and starting the game loop.
     */
    public void startGame() {
        if (gameState == null) {
            gameState = new GameState();
        }
        gameState.setPointsInCurrentLevel(0);
        setupLevel();

        canvas = new Canvas(width, height);
        gc = canvas.getGraphicsContext2D();

        Scene gameScene = new Scene(new StackPane(canvas), width, height);

        gameScene.setOnKeyPressed(e -> {
            activeKeys.add(e.getCode());
            if (e.getCode() == KeyCode.SPACE) {
                shoot();
            }
        });

        gameScene.setOnKeyReleased(e -> activeKeys.remove(e.getCode()));

        primaryStage.setScene(gameScene);

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                render(gc);
            }
        };

        gameLoop.start();
    }

    /**
     * Sets up the current level by configuring enemy spawn counts and boss
     * presence.
     * Level 1: Only asteroids
     * Level 2: Asteroids + Enemies
     * Level 3: Asteroids + Enemies (Enemies shoot)
     * Level 4: Asteroids + Enemies + Boss
     */
    private void setupLevel() {
        int level = gameState.getLevel();

        switch (level) {
            case 1:
                gameState.setEnemiesToSpawn(0);
                break;
            case 2:
                gameState.setEnemiesToSpawn(10);
                break;
            case 3:
                gameState.setEnemiesToSpawn(16);
                break;
            case 4:
                gameState.setEnemiesToSpawn(30);
                Boss boss = new Boss(width / 2 - 40, 50);
                gameState.setBoss(boss);
                break;
            default:
                // No configuration needed
                break;
        }
    }

    /**
     * Updates all game logic for the current frame.
     * Handles player movement, entity spawning, AI behavior, and level transitions.
     */
    private void update() {
        if (!gameState.isGameRunning()) {
            gameOver();
            return;
        }

        if (!gameState.isLevelComplete() &&
                gameState.shouldAdvanceLevel() &&
                gameState.getLevel() < 4) {
            gameState.setLevelComplete(true);
            return;
        }

        if (gameState.getLevel() == 4 && gameState.isBossDefeated() && !gameState.isLevelComplete()) {
            gameState.setLevelComplete(true);
            return;
        }

        if (gameState.isLevelComplete()) {
            nextLevel();
            return;
        }

        Player player = gameState.getPlayer();

        if (activeKeys.contains(KeyCode.LEFT)) {
            player.moveLeft();
        }
        if (activeKeys.contains(KeyCode.RIGHT)) {
            player.moveRight(width);
        }
        if (activeKeys.contains(KeyCode.UP)) {
            player.moveUp();
        }
        if (activeKeys.contains(KeyCode.DOWN)) {
            player.moveDown(height);
        }

        long now = System.currentTimeMillis();
        int spawnDelay = Math.max(600, 2000 - gameState.getLevel() * 150);
        if (now - lastAsteroidSpawnTime > spawnDelay) {
            spawnAsteroid();
            lastAsteroidSpawnTime = now;
        }

        if (gameState.getLevel() >= 2 &&
                gameState.getEnemies().size() < 3 &&
                gameState.getEnemiesSpawned() < gameState.getEnemiesToSpawn()) {
            int enemySpawnDelay = Math.max(3000, 5000 - gameState.getLevel() * 500);
            if (now - lastEnemySpawnTime > enemySpawnDelay) {
                spawnEnemy();
                lastEnemySpawnTime = now;
            }
        }

        // Enemies shoot only from Level 3 onwards
        if (gameState.getLevel() >= 3) {
            for (Enemy enemy : gameState.getEnemies()) {
                if (enemy.canShoot()) {
                    int bulletSpeed = 3 + (gameState.getLevel() / 2);
                    Bullet bullet = new Bullet(enemy.getBulletX(), enemy.getBulletY(), bulletSpeed, false);
                    gameState.addBullet(bullet);
                }
            }
        }

        if (gameState.getLevel() == 4) {
            Boss boss = gameState.getBoss();
            if (boss != null && !gameState.isBossDefeated() && boss.canShoot()) {
                Bullet bullet = new Bullet(boss.getBulletX(), boss.getBulletY(), 5, false);
                gameState.addBullet(bullet);
            }
        }

        gameState.update();
        checkCollisions();
    }

    /**
     * Spawns a new asteroid at a random horizontal position at the top of the
     * screen.
     */
    private void spawnAsteroid() {
        double x = random.nextDouble() * (width - 50) + 25;
        double speed = 1 + random.nextDouble() * 2 + (gameState.getLevel() * 0.3);
        Asteroid asteroid = new Asteroid(x, -30, speed);
        gameState.addAsteroid(asteroid);
    }

    /**
     * Spawns a new enemy at a random horizontal position at the top of the screen.
     */
    private void spawnEnemy() {
        double x = random.nextDouble() * (width - 50) + 25;
        double speedX = (random.nextDouble() - 0.5) * 0.5;
        double speedY = 0.5 + random.nextDouble() * 0.8;
        Enemy enemy = new Enemy(x, -35, speedX, speedY);
        gameState.addEnemy(enemy);
    }

    /**
     * Fires a bullet from the player's ship if the cooldown has elapsed.
     * Rapid fire power-up reduces the cooldown from 200ms to 50ms.
     */
    private void shoot() {
        long now = System.currentTimeMillis();
        long shootDelay = gameState.getPlayer().hasRapidFire() ? 50 : 200;
        if (now - lastShotTime > shootDelay) {
            Player player = gameState.getPlayer();
            Bullet bullet = new Bullet(player.getX() + player.getWidth() / 2 - 2,
                    player.getY() - 10, true);
            gameState.addBullet(bullet);

            com.spaceblaster.util.SoundManager.play("shoot");
            
            lastShotTime = now;
        }
    }

    /**
     * Checks and handles all collisions between game entities.
     * Includes bullet-asteroid, bullet-enemy, bullet-boss, player-asteroid,
     * player-bullet, and player-powerup collisions.
     */
    private void checkCollisions() {
        Player player = gameState.getPlayer();

        // Player bullets vs Asteroids
        for (Iterator<Bullet> bulletIt = gameState.getBullets().iterator(); bulletIt.hasNext();) {
            Bullet bullet = bulletIt.next();
            if (bullet.isFromPlayer()) {
                for (Iterator<Asteroid> astIt = gameState.getAsteroids().iterator(); astIt.hasNext();) {
                    Asteroid asteroid = astIt.next();
                    if (bullet.getBounds().intersects(asteroid.getBounds().getBoundsInParent())) {
                        bulletIt.remove();
                        astIt.remove();
                        gameState.addScore(asteroid.getPoints());
                        maybeSpawnPowerUp(asteroid.getX(), asteroid.getY());

                        com.spaceblaster.util.SoundManager.play("explosion");
                        break;
                    }
                }

                // Player bullets vs Enemies
                for (Iterator<Enemy> enemyIt = gameState.getEnemies().iterator(); enemyIt.hasNext();) {
                    Enemy enemy = enemyIt.next();
                    if (bullet.getBounds().intersects(enemy.getBounds().getBoundsInParent())) {
                        bulletIt.remove();
                        enemyIt.remove();
                        gameState.addScore(enemy.getPoints());
                        maybeSpawnPowerUp(enemy.getX(), enemy.getY());

                        com.spaceblaster.util.SoundManager.play("explosion");
                        break;
                    }
                }

                // Player bullets vs Boss
                Boss boss = gameState.getBoss();
                if (boss != null && !gameState.isBossDefeated()
                        && bullet.getBounds().intersects(boss.getBounds().getBoundsInParent())) {
                    bulletIt.remove();
                    boss.hit();
                    gameState.addScore(50);

                    com.spaceblaster.util.SoundManager.play("explosion");
                    
                    if (boss.isDefeated()) {
                        gameState.addScore(1000);
                        gameState.setBossDefeated(true);
                    }
                }
            }
        }

        // Player vs Asteroids
        for (Iterator<Asteroid> it = gameState.getAsteroids().iterator(); it.hasNext();) {
            Asteroid asteroid = it.next();
            if (player.getBounds().intersects(asteroid.getBounds().getBoundsInParent())) {
                player.hit();
                it.remove();

                com.spaceblaster.util.SoundManager.play("explosion");
                
                if (player.getLives() <= 0) {
                    gameState.setGameRunning(false);
                }
                break;
            }
        }

        // Player vs Enemy Bullets
        for (Iterator<Bullet> it = gameState.getBullets().iterator(); it.hasNext();) {
            Bullet bullet = it.next();
            if (!bullet.isFromPlayer()
                    && player.getBounds().intersects(bullet.getBounds().getBoundsInParent())) {
                player.hit();
                it.remove();

                com.spaceblaster.util.SoundManager.play("explosion");
                
                if (player.getLives() <= 0) {
                    gameState.setGameRunning(false);
                }
                break;
            }
        }

        // Player vs Power-ups
        for (Iterator<PowerUp> it = gameState.getPowerUps().iterator(); it.hasNext();) {
            PowerUp powerUp = it.next();
            if (player.getBounds().intersects(powerUp.getBounds().getBoundsInParent())) {
                applyPowerUp(powerUp);
                it.remove();
            }
        }
    }

    /**
     * Randomly spawns a power-up with 10% probability at the given position.
     * 
     * @param x The x-coordinate where the power-up spawns
     * @param y The y-coordinate where the power-up spawns
     */
    private void maybeSpawnPowerUp(double x, double y) {
        if (random.nextDouble() < 0.1) {
            PowerUp.PowerUpType[] types = PowerUp.PowerUpType.values();
            PowerUp.PowerUpType type = types[random.nextInt(types.length)];
            PowerUp powerUp = new PowerUp(x, y, type);
            gameState.addPowerUp(powerUp);
        }
    }

    /**
     * Applies the effect of a collected power-up to the player.
     * 
     * @param powerUp The power-up to apply
     */
    private void applyPowerUp(PowerUp powerUp) {
        Player player = gameState.getPlayer();
        switch (powerUp.getType()) {
            case RAPID_FIRE:
                player.activateRapidFire(5000);
                break;
            case SHIELD:
                player.activateShield(5000);
                break;
            case SCORE_MULTIPLIER:
                gameState.addScore(500);
                break;
            default:
                // No action needed
                break;
        }
    }

    /**
     * Transitions to the next level by stopping the game loop and showing
     * the level complete screen.
     */
    private void nextLevel() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
        showLevelCompleteScreen();
    }

    /**
     * Displays the level complete screen with progress information.
     * Shows different messages for levels 1-3 and the final level 4.
     */
    private void showLevelCompleteScreen() {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: black;");

        int completedLevel = gameState.getLevel();
        String message;

        if (completedLevel == 4) {
            message = "CONGRATULATIONS!\nYou completed the game!\nFinal Score: " + gameState.getScore()
                    + "\n\nPress ENTER to return to menu";
        } else {
            int currentPoints = gameState.getPointsInCurrentLevel();
            int nextLevelPoints = 400;
            int extraPoints = Math.max(0, currentPoints - nextLevelPoints);

            message = "LEVEL " + completedLevel + " COMPLETE!\n"
                    + "Total Score: " + gameState.getScore()
                    + "\nPoints in this level: " + Math.min(currentPoints, nextLevelPoints) + "/" + nextLevelPoints
                    + "\nExtra points carried over: " + extraPoints
                    + "\n\nPress ENTER to continue";
        }

        javafx.scene.control.Label levelLabel = new javafx.scene.control.Label(message);
        levelLabel.setFont(Font.font("Monospace", 24));
        levelLabel.setTextFill(Color.WHITE);
        levelLabel.setTextAlignment(TextAlignment.CENTER);
        levelLabel.setWrapText(true);

        root.getChildren().add(levelLabel);

        Scene scene = new Scene(root, width, height);
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                if (completedLevel == 4) {
                    MenuController menuController = new MenuController(primaryStage, width, height);
                    menuController.showMainMenu();
                } else {
                    gameState.nextLevel();
                    gameState.resetForNextLevel();
                    setupLevel();
                    startGame();
                }
            }
        });

        primaryStage.setScene(scene);
    }

    /**
     * Displays the game over screen with player statistics and options.
     * Provides buttons to restart, return to menu (with score saving), or exit.
     */
    private void gameOver() {
        if (gameLoop != null) {
            gameLoop.stop();
        }

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: black;");

        VBox vbox = new VBox(30);
        vbox.setAlignment(Pos.CENTER);

        Text gameOverTitle = new Text("GAME OVER");
        gameOverTitle.setFont(Font.font("Monospace", 60));
        gameOverTitle.setFill(Color.RED);
        gameOverTitle.setStyle("-fx-font-weight: bold;");

        int currentLevel = gameState.getLevel();
        int pointsInLevel = gameState.getPointsInCurrentLevel();
        int pointsNeeded = 400;
        int missingPoints = Math.max(0, pointsNeeded - pointsInLevel);

        Text infoText = new Text("You reached Level " + currentLevel
                + "\nYour total score: " + gameState.getScore()
                + "\nPoints in this level: " + pointsInLevel + "/" + pointsNeeded
                + "\nYou needed " + missingPoints + " more points to advance"
                + (currentLevel < 4 ? "\n\nTip: Destroy asteroids for 100 points each!" : ""));
        infoText.setFont(Font.font("Monospace", 18));
        infoText.setFill(Color.WHITE);
        infoText.setTextAlignment(TextAlignment.CENTER);

        Button restartButton = createGameOverButton("RESTART GAME", "green");
        restartButton.setOnAction(e -> restartGame());

        Button menuButton = createGameOverButton("MAIN MENU", "cyan");
        menuButton.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Save Score");
            dialog.setHeaderText("Your score: " + gameState.getScore());
            dialog.setContentText("Enter your name:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(name -> {
                if (name.trim().isEmpty()) {
                    name = "PLAYER";
                }
                ScoreManager.saveHighScore(name, gameState.getScore());
            });

            MenuController menuController = new MenuController(primaryStage, width, height);
            menuController.showMainMenu();
        });

        Button exitButton = createGameOverButton("EXIT", "red");
        exitButton.setOnAction(e -> System.exit(0));

        HBox buttonBox = new HBox(20, restartButton, menuButton, exitButton);
        buttonBox.setAlignment(Pos.CENTER);

        vbox.getChildren().addAll(gameOverTitle, infoText, buttonBox);
        root.getChildren().add(vbox);

        Scene scene = new Scene(root, width, height);
        primaryStage.setScene(scene);
    }

    /**
     * Creates a styled button for the game over screen.
     * 
     * @param text  The button text
     * @param color The border color for the button
     * @return A styled Button instance
     */
    private Button createGameOverButton(String text, String color) {
        Button button = new Button(text);
        String baseStyle = "-fx-font-size: 20px; -fx-padding: 10px 30px; "
                + "-fx-background-color: transparent; -fx-text-fill: white; "
                + "-fx-border-color: " + color + "; -fx-border-width: 2px;";
        String hoverStyle = "-fx-font-size: 20px; -fx-padding: 10px 30px; "
                + "-fx-background-color: rgba(0," +
                (color.equals("green") ? "255,0" : color.equals("cyan") ? "255,255" : "255,0") + ",0.2); "
                + "-fx-text-fill: " + color + "; "
                + "-fx-border-color: " + color + "; -fx-border-width: 2px;";

        button.setStyle(baseStyle);
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(baseStyle));

        return button;
    }

    /**
     * Restarts the game by resetting all game state and starting fresh.
     */
    private void restartGame() {
        gameState.resetGame();
        lastShotTime = 0;
        lastEnemySpawnTime = 0;
        lastAsteroidSpawnTime = 0;
        startGame();
    }

    /**
     * Renders all game entities and HUD elements on the canvas.
     * Draws background, player, asteroids, enemies, boss, bullets, power-ups,
     * and HUD with score, level, progress bar, lives, and power-up status.
     * 
     * @param gc The GraphicsContext to draw on
     */
    private void render(GraphicsContext gc) {
        // Background
        if (background != null && !background.isError()) {
            gc.drawImage(background, 0, 0, width, height);
        } else {
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, width, height);
        }

        Player player = gameState.getPlayer();

        // Player
        if (playerImage != null && !playerImage.isError()) {
            if (player.isInvincible() && (System.currentTimeMillis() / 100) % 2 == 0) {
                gc.setGlobalAlpha(0.5);
            }
            gc.drawImage(playerImage, player.getX(), player.getY(), player.getWidth(), player.getHeight());
            gc.setGlobalAlpha(1.0);
        } else {
            gc.setFill(Color.BLUE);
            gc.fillRect(player.getX(), player.getY(), player.getWidth(), player.getHeight());
        }

        // Asteroids
        if (asteroidImage != null && !asteroidImage.isError()) {
            for (Asteroid a : gameState.getAsteroids()) {
                gc.drawImage(asteroidImage, a.getX(), a.getY(), a.getWidth(), a.getHeight());
            }
        } else {
            gc.setFill(Color.BROWN);
            for (Asteroid a : gameState.getAsteroids()) {
                gc.fillOval(a.getX(), a.getY(), a.getWidth(), a.getHeight());
            }
        }

        // Enemies
        if (enemyImage != null && !enemyImage.isError()) {
            for (Enemy e : gameState.getEnemies()) {
                gc.drawImage(enemyImage, e.getX(), e.getY(), e.getWidth(), e.getHeight());
            }
        } else {
            gc.setFill(Color.RED);
            for (Enemy e : gameState.getEnemies()) {
                gc.fillRect(e.getX(), e.getY(), e.getWidth(), e.getHeight());
            }
        }

        // Boss
        if (gameState.getBoss() != null && !gameState.isBossDefeated()) {
            Boss boss = gameState.getBoss();
            if (bossImage != null && !bossImage.isError()) {
                gc.drawImage(bossImage, boss.getX(), boss.getY(), boss.getWidth(), boss.getHeight());
            } else {
                gc.setFill(Color.PURPLE);
                gc.fillRect(boss.getX(), boss.getY(), boss.getWidth(), boss.getHeight());
            }
            gc.setFill(Color.RED);
            gc.fillRect(boss.getX(), boss.getY() - 20, boss.getWidth(), 10);
            gc.setFill(Color.GREEN);
            double healthPercent = (double) boss.getHitPoints() / boss.getMaxHitPoints();
            gc.fillRect(boss.getX(), boss.getY() - 20, boss.getWidth() * healthPercent, 10);
        }

        // Bullets
        if (bulletImage != null && !bulletImage.isError()) {
            for (Bullet b : gameState.getBullets()) {
                gc.drawImage(bulletImage, b.getX(), b.getY(), b.getWidth(), b.getHeight());
            }
        } else {
            gc.setFill(Color.YELLOW);
            for (Bullet b : gameState.getBullets()) {
                gc.fillRect(b.getX(), b.getY(), b.getWidth(), b.getHeight());
            }
        }

        // Power-ups
        if (powerUpImages != null) {
            for (PowerUp p : gameState.getPowerUps()) {
                Image powerImg = powerUpImages.get(p.getType());
                if (powerImg != null && !powerImg.isError()) {
                    gc.drawImage(powerImg, p.getX(), p.getY(), p.getWidth(), p.getHeight());
                }
            }
        }

        // HUD
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("monospaced", FontWeight.BOLD, 24));
        gc.fillText("SCORE: " + gameState.getScore(), 20, 40);
        gc.fillText("LEVEL: " + gameState.getLevel() + "/4", 20, 80);

        // Progress bar
        int currentLevel = gameState.getLevel();
        if (currentLevel < 4) {
            int pointsInLevel = gameState.getPointsInCurrentLevel();
            int pointsNeeded = 400;
            double progress = Math.min(1.0, (double) Math.max(0, pointsInLevel) / pointsNeeded);

            gc.setFill(Color.GRAY);
            gc.fillRect(width - 220, 20, 200, 20);
            gc.setFill(Color.GREEN);
            gc.fillRect(width - 220, 20, 200 * progress, 20);
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("monospaced", FontWeight.NORMAL, 12));
            int displayPoints = Math.min(pointsInLevel, pointsNeeded);
            gc.fillText(displayPoints + "/" + pointsNeeded, width - 130, 35);
            gc.fillText("NEXT LEVEL", width - 210, 18);
        }

        // Lives
        if (heartImage != null && !heartImage.isError()) {
            for (int i = 0; i < gameState.getPlayer().getLives(); i++) {
                gc.drawImage(heartImage, 20 + (i * 35), 110, 30, 30);
            }
        } else {
            gc.fillText("LIVES: " + gameState.getPlayer().getLives(), 20, 120);
        }

        // Power-up status
        gc.setFont(Font.font("monospaced", FontWeight.BOLD, 18));
        if (gameState.getPlayer().hasShield()) {
            gc.setFill(Color.CYAN);
            gc.fillText("SHIELD", width - 120, 40);
        }
        if (gameState.getPlayer().hasRapidFire()) {
            gc.setFill(Color.ORANGE);
            gc.fillText("RAPID", width - 120, 70);
        }
    }
}
