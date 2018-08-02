package com.content.pdf.storage;

public class StorageFileNotFoundException extends StorageException {

	private static final long serialVersionUID = 6495284782425108033L;

	public StorageFileNotFoundException(String message) {
        super(message);
    }

    public StorageFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
