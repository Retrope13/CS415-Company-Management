package edu.colostate.cs415.model;

public enum ProjectSize {
    SMALL(1), MEDIUM(2), BIG(3);

    private final int value;

    private ProjectSize(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
