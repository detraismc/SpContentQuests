package me.detraismc.spcontentquests.models;

public class PlayerQuestData {
    private final String questId;
    private int points;
    private boolean completed;
    private boolean claimed;
    private boolean modified = false;

    public PlayerQuestData(String questId, int points, boolean completed, boolean claimed) {
        this.questId = questId;
        this.points = points;
        this.completed = completed;
        this.claimed = claimed;
    }

    public String getQuestId() {
        return questId;
    }

    public int getPoints() {
        return points;
    }

    public boolean isCompleted() {
        return completed;
    }

    public boolean isClaimed() {
        return claimed;
    }

    public void addPoints(int amount) {
        this.points += amount;
        this.modified = true;
    }

    public void setPoints(int points) {
        this.points = points;
        this.modified = true;
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
}
