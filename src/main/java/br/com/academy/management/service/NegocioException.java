package br.com.academy.management.service;

public class NegocioException extends RuntimeException {

    public NegocioException(String message) {
        super(message);
    }
}
