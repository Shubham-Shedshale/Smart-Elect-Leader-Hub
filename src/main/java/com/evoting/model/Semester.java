package com.evoting.model;

public enum Semester {
    SEMESTER_1("1st Semester"),
    SEMESTER_2("2nd Semester"),
    SEMESTER_3("3rd Semester"),
    SEMESTER_4("4th Semester"),
    SEMESTER_5("5th Semester"),
    SEMESTER_6("6th Semester"),
    SEMESTER_7("7th Semester"),
    SEMESTER_8("8th Semester");

    private final String displayName;

    Semester(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
