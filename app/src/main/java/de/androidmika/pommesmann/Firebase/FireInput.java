package de.androidmika.pommesmann.Firebase;

public class FireInput {

    static final String dummyName = "";

    // analyze input

    public static String analyzeString(String text) {
        if (text.length() > 15) {
            text = text.substring(0,14);
        }
        text = text.replaceAll("delete", "");
        text = text.replaceAll("collection", "");
        text = text.replaceAll("document", "");
        text = text.replaceAll(";", ".");

        if (text.toLowerCase().equals("mika")) {
            text = "nope";
        }

        if (text.toLowerCase().equals("pommesmann")) {
            text = "nope";
        }

        return text;
    }
}
