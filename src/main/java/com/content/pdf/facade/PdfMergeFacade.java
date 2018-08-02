package com.content.pdf.facade;

import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.content.pdf.merger.MergeFactory;
import com.content.pdf.storage.FileSystemStorageService;
import com.content.pdf.storage.StorageService;

@Service
public class PdfMergeFacade {
	
	private final StorageService storageService;
	private final MergeFactory mergeFactory;
	
	@Autowired
    public PdfMergeFacade(FileSystemStorageService storageService,MergeFactory mergeFactory) {
		this.storageService=storageService;
		this.mergeFactory=mergeFactory;
    }
	
	public Path merge(MultipartFile file) {
		Path uploadedFilesPath = storageService.store(file);
		Path mergedFilesPath = mergeFactory.merge(uploadedFilesPath,MergeFactory.AS.PDF);
		return mergedFilesPath;
	}
	
    public Resource loadAsResource(String filename) {
        return storageService.loadAsResource(filename);
    }

}
