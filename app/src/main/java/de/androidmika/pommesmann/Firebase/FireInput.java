package de.androidmika.pommesmann.Firebase;

public class FireInput {

    // analyze input

    static String analyzeString(String text) {
        if (text.length() > 15) {
            text = text.substring(0,14);
        }
        text = text.replaceAll("delete", "");
        text = text.replaceAll("collection", "");
        text = text.replaceAll("document", "");
        text = text.replaceAll(";", ".");
        text = text.replaceAll("Mika", "nope");
        text = text.replaceAll("mika", "nope");
        return text;
    }
}
