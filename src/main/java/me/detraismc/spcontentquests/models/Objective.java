package me.detraismc.spcontentquests.models;

import java.util.List;

public class Objective {
    private final String type;
    private final int amount;
    private final List<String> required;
    private final String display;
    private final List<String> desc;
    private final List<String> command;

    public Objective(String type, int amount, List<String> required, String display, List<String> desc, List<String> command) {
        this.type = type;
        this.amount = amount;
        this.required = required;
        this.display = display;
        this.desc = desc;
        this.command = command;
    }

    public String getType() {
        return type;
    }

    public int getAmount() {
        return amount;
    }

    public List<String> getRequired() {
        return required;
    }

    public String getDisplay() {
        return display;
    }

    public List<String> getDesc() {
        return desc;
    }

    public List<String> getCommand() {
        return command;
    }
}
