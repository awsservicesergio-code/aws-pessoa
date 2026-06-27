package com.pessoa.error.exceptions;

public class SqsErrorException extends RuntimeException{

    /**
     * Método responsável pela exception SqsErrorException.
     * @param message
     */
    public SqsErrorException(String message){
        super(message);
    }

    /**
     * Método responsável pela exception SqsErrorException.
     * @param message
     * @param value
     */
    public SqsErrorException(String message, Throwable value){
        super(message,value);
    }

    /**
     * Método responsável pela exception SqsErrorException.
     */
    public SqsErrorException(){
        super("Erro ao enviar payload ao sqs");
    }
}
