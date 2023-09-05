package com.avensys.rts.accountservice.exception;

/**
 * author: Koh He Xiang
 * This is the exception class for when a document is not found
 */
public class RequiredDocumentMissingException extends RuntimeException{
    public RequiredDocumentMissingException(String message) {
        super(message);
    }
}
