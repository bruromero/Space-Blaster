package com.spaceblaster.util;

import javafx.scene.media.AudioClip;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class that manages the concurrent loading and playback of audio assets.
 * It caches audio clips in memory to prevent real-time performance stuttering
 * within the 60 FPS active gameplay loop.
 */
public class SoundManager {
    
    // In-memory lookup map acting as a data cache for loaded audio elements
    private static final Map<String, AudioClip> soundCache = new HashMap<>();

    /**
     * Synchronously loads an audio asset from the resource directory into the memory cache.
     * * @param soundName The unique identifier key used to invoke the playback later (e.g., "shoot").
     * @param fileName The actual file name located inside the 'src/main/resources/sound/' directory (e.g., "shoot.wav").
     */
    public static void loadSound(String soundName, String fileName) {
        try {
            URL soundURL = SoundManager.class.getResource("/sound/" + fileName);
            if (soundURL != null) {
                AudioClip clip = new AudioClip(soundURL.toExternalForm());
                soundCache.put(soundName, clip);
            } else {
                System.err.println("[SoundManager] Warning: Sound asset file path not found: /sound/" + fileName);
            }
        } catch (Exception e) {
            System.err.println("[SoundManager] Error initializing audio asset " + fileName + ": " + e.getMessage());
        }
    }

    /**
     * Dispatches and plays a cached audio clip asynchronously without blocking 
     * the primary JavaFX application thread.
     * * @param soundName The unique identifier key assigned to the asset during the loading stage.
     */
    public static void play(String soundName) {
        AudioClip clip = soundCache.get(soundName);
        if (clip != null) {
            clip.play(); // Spawns an internal detached thread for immediate audio performance
        }
    }
}
