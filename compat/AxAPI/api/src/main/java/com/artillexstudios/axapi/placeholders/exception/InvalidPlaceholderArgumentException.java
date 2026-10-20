package com.artillexstudios.axapi.placeholders.exception;

public class InvalidPlaceholderArgumentException extends PlaceholderException {

    public InvalidPlaceholderArgumentException(String placeholder) {
        super(String.format("Invalid placeholder argument, %s!", placeholder));
    }
}
