package com.content.pdf.storage;

public class StorageException extends RuntimeException {

	private static final long serialVersionUID = 2028185127683313715L;

	public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
