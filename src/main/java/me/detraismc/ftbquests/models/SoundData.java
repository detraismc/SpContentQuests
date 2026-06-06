package me.detraismc.ftbquests.models;

import org.bukkit.configuration.ConfigurationSection;

public class SoundData {
    private final String id;
    private final float pitch;
    private final float volume;

    public SoundData(String id, float pitch, float volume) {
        this.id = id;
        this.pitch = pitch;
        this.volume = volume;
    }

    public String getId() {
        return id;
    }

    public float getPitch() {
        return pitch;
    }

    public float getVolume() {
        return volume;
    }

    public static SoundData fromConfig(ConfigurationSection section) {
        if (section == null) return null;
        String id = section.getString("id");
        if (id == null || id.isEmpty()) return null;
        float pitch = (float) section.getDouble("pitch", 1.0);
        float volume = (float) section.getDouble("volume", 1.0);
        return new SoundData(id, pitch, volume);
    }
}
