package com.content.pdf.storage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.test.context.TestExecutionListeners.MergeMode;
import org.springframework.web.multipart.MultipartFile;

import com.content.pdf.merger.MergeFactory;


@Service
public class FileSystemStorageService implements StorageService  {
	
	private final Path rootLocation;
	
	private final StorageProperties properties;
	
	@Autowired
	private MergeFactory factory;

	@Autowired
    public FileSystemStorageService(StorageProperties properties) {
		this.properties=properties;
        this.rootLocation = Paths.get(properties.getUploadLocation());
    }

	@Override
	public Path store(MultipartFile file) {
		if (file==null) {
			 throw new StorageException("Failed to store empty file " );
		}
		Path subPath = createSubDirForContent();
		try {
            Path filePath=Paths.get(subPath+File.separator+file.getOriginalFilename());
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream,filePath ,StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException e) {
            throw new StorageException("Failed to store file " + subPath.toString(), e);
        }
        
        return subPath;
	}

	//Create unique folder each upload process
	private Path createSubDirForContent(){
		try {
			UUID uuid = UUID.randomUUID();
	        String randomUUIDString = uuid.toString();
	        Path path = Paths.get(properties.getUploadLocation()+File.separator+randomUUIDString);
			Files.createDirectories(path);
			return path;
		} catch (Exception e) {
			 throw new StorageException("Could not create sub dir storage", e);
		}
	}

	@Override
	public Path load(String filename) {
		return rootLocation.resolve(filename);
	}
	
	@Override
	public Resource loadAsResource(String filename) {
		try {
			Path file = load(filename);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				throw new StorageFileNotFoundException("Could not read file: " + filename);

			}
		} catch (java.net.MalformedURLException e) {
			throw new StorageFileNotFoundException("Could not read file: " + filename, e);
		}
	}
	
   

	@Override
	public void init() {
		try {
            Files.createDirectories(rootLocation);
//            UUID uuid = UUID.randomUUID();
//	        String randomUUIDString = uuid.toString();
//	        Path path = Paths.get(properties.getUploadLocation()+File.separator+randomUUIDString);
//			Files.createDirectories(path);
//			factory.merge(path, MergeFactory.AS.PDF);
        }
        catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
	}

}
