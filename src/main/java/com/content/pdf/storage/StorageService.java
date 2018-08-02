package com.content.pdf.storage;

import java.nio.file.Path;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
	
	void init();

	Path store(MultipartFile file);

    Path load(String filename);
    
    Resource loadAsResource(String filename) ;
    
}
