package de.marenthyu.memedit.bunny.exceptions;

public class UserInputError extends RuntimeException {
    public UserInputError(NumberFormatException ex) {
        super(ex);
    }
}
