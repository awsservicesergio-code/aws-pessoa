package com.pessoa.error.exceptions;

public class SqsErrorException extends RuntimeException{

    public SqsErrorException(String message){
        super(message);
    }

    public SqsErrorException(String message, Throwable value){
        super(message,value);
    }

    public SqsErrorException(){
        super("Erro ao enviar payload ao sqs");
    }
}
