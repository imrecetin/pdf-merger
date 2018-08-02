package com.content.pdf.controller;

import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.content.pdf.facade.PdfMergeFacade;
import com.content.pdf.storage.StorageFileNotFoundException;

@Controller("/pdf")
public class PdfMergeController {
	
	private final PdfMergeFacade pdfMergeFacade;

    @Autowired
    public PdfMergeController(PdfMergeFacade pdfMergeFacade) {
        this.pdfMergeFacade = pdfMergeFacade;
    }
    
    @GetMapping("/")
    public String index() {
        return "uploadForm";
    }
    
    @PostMapping("/")
    @ResponseBody
    public PdfMergeResource handleFileUpload(@RequestParam("file") MultipartFile uploadingFiles, RedirectAttributes redirectAttributes) {
    	Path mergedFilePath = pdfMergeFacade.merge(uploadingFiles);
        redirectAttributes.addFlashAttribute("message","You successfully uploaded !");
        PdfMergeResource resource=new PdfMergeResource();
        resource.setFilePath(mergedFilePath.toAbsolutePath().toString());
        return resource;
    }
   
    
    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = pdfMergeFacade.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
    
    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

}
