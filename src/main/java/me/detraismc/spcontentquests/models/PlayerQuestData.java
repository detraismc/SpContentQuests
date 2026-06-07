package me.detraismc.spcontentquests.models;

import java.util.HashMap;
import java.util.Map;

public class PlayerQuestData {
    private final String questId;
    private final Map<Integer, Integer> objectivesProgress = new HashMap<>();
    private boolean completed;
    private boolean claimed;
    private boolean modified = false;

    public PlayerQuestData(String questId) {
        this.questId = questId;
    }

    public PlayerQuestData(String questId, int points, boolean completed, boolean claimed) {
        this.questId = questId;
        this.objectivesProgress.put(0, points);
        this.completed = completed;
        this.claimed = claimed;
    }

    public PlayerQuestData(String questId, Map<Integer, Integer> objectivesProgress, boolean completed, boolean claimed) {
        this.questId = questId;
        this.objectivesProgress.putAll(objectivesProgress);
        this.completed = completed;
        this.claimed = claimed;
    }

    public String getQuestId() {
        return questId;
    }

    public int getPoints() {
        return objectivesProgress.values().stream().mapToInt(Integer::intValue).sum();
    }

    public void addPoints(int amount) {
        int current = objectivesProgress.getOrDefault(0, 0);
        objectivesProgress.put(0, current + amount);
        this.modified = true;
    }

    public void setPoints(int points) {
        for (int i = 0; i < Math.max(1, objectivesProgress.size()); i++) {
            objectivesProgress.put(i, points);
        }
        this.modified = true;
    }

    public int getObjectiveProgress(int index) {
        return objectivesProgress.getOrDefault(index, 0);
    }

    public void setObjectiveProgress(int index, int value) {
        objectivesProgress.put(index, value);
        this.modified = true;
    }

    public void addObjectiveProgress(int index, int amount) {
        int current = objectivesProgress.getOrDefault(index, 0);
        objectivesProgress.put(index, current + amount);
        this.modified = true;
    }

    public Map<Integer, Integer> getObjectivesProgress() {
        return new HashMap<>(objectivesProgress);
    }

    public boolean isCompleted() {
        return completed;
    }

    public boolean isClaimed() {
        return claimed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
        this.modified = true;
    }

    public void setClaimed(boolean claimed) {
        this.claimed = claimed;
        this.modified = true;
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public static String serializeProgress(Map<Integer, Integer> progress) {
        if (progress == null || progress.isEmpty()) return "";
        int maxIndex = progress.keySet().stream().mapToInt(Integer::intValue).max().orElse(0);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <= maxIndex; i++) {
            if (i > 0) sb.append(',');
            sb.append(progress.getOrDefault(i, 0));
        }
        return sb.toString();
    }

    public static Map<Integer, Integer> deserializeProgress(String data) {
        Map<Integer, Integer> result = new HashMap<>();
        if (data == null || data.isEmpty()) return result;
        String[] parts = data.split(",");
        for (int i = 0; i < parts.length; i++) {
            try {
                result.put(i, Integer.parseInt(parts[i].trim()));
            } catch (NumberFormatException ignored) {
                result.put(i, 0);
            }
        }
        return result;
    }
}
