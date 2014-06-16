package org.jboss.as.quickstarts.datagrid.hotrod;

/**
 * @author Vitalii Chepeliuk
 */
public enum AnsiColors {

    HEADER("\u001B[95m"),
    RESET("\u001B[0m"),
    BLACK("\u001B[30m"),
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    BLUE("\u001B[34m"),
    PURPLE("\u001B[35m"),
    CYAN("\u001B[36m"),
    WHITE("\u001B[37m"),
    END("\u001B[0m");

    AnsiColors(String color) {
        this.color = color;
    }

    public String color() {
        return color;
    }

    private String color;
}
