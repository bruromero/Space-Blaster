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
