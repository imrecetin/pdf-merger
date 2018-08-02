package com.content.pdf.merger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;

import com.content.pdf.storage.StorageException;

@Service
public class PDFMerge implements IDocumentMerge{

	@Override
	public Path merge(Path filesPath) {
		Path subPath = createMergedFilesDir(filesPath);
		try {
			List<Path> files = Files.list(filesPath).sorted().collect(Collectors.toList());
			convertImagesToPDF(files);
			files = Files.list(filesPath).sorted().collect(Collectors.toList());
			mergeDocuments(subPath, files);
		}catch (IOException e) {
			e.printStackTrace();
		}
		return Paths.get(subPath.getFileName().toString()+File.separator+"mergedPDF.pdf");
	}

	private void mergeDocuments(Path subPath, List<Path> files) throws IOException {
		PDFMergerUtility mergePDFUtility = new PDFMergerUtility();
		mergePDFUtility.setDestinationFileName(subPath.toAbsolutePath().toString()+File.separator+"mergedPDF.pdf");
		for (Path p : files) {
			File file = new File(p.toAbsolutePath().toString());
			if (file.exists() && file.isFile()) {
				if (file.getName().endsWith(".pdf")) {
					try {
						mergePDFUtility.addSource(file);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		}
		mergePDFUtility.mergeDocuments();
	}
	
	private final List<String> imageExtensions= new ArrayList<String>(Arrays.asList("png","jpg","jpeg","tiff","bmp"));
	
	private void convertImagesToPDF(List<Path> files) {
		
		for (Path p : files) {
			File file = new File(p.toAbsolutePath().toString());
			if (file.exists() && file.isFile()) {
				if (imageExtensions.contains(FilenameUtils.getExtension(file.getName().toLowerCase()))) {
					try {
						combineImagesIntoPDF(changeExtension(file,".pdf").getAbsolutePath().toString(),file.getAbsolutePath().toString());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
	}
	

	private void combineImagesIntoPDF(String pdfPath, String... inputDirsAndFiles) throws IOException {
        try (PDDocument doc = new PDDocument()) {
            for (String input : inputDirsAndFiles) {
                Files.find(Paths.get(input),Integer.MAX_VALUE,
                           (path, basicFileAttributes) -> Files.isRegularFile(path))
                     .forEachOrdered(path -> convertImageAsNewPage(doc, path.toString()));
            }
            doc.save(pdfPath);
        }
    }

	
	private void convertImageAsNewPage(PDDocument doc, String imagePath) {
        try {
            PDImageXObject image          = PDImageXObject.createFromFile(imagePath, doc);
            PDRectangle    pageSize       = PDRectangle.A4;

            int            originalWidth  = image.getWidth();
            int            originalHeight = image.getHeight();
            float          pageWidth      = pageSize.getWidth();
            float          pageHeight     = pageSize.getHeight();
            float          ratio          = Math.min(pageWidth / originalWidth, pageHeight / originalHeight);
            float          scaledWidth    = originalWidth  * ratio;
            float          scaledHeight   = originalHeight * ratio;
            float          x              = (pageWidth  - scaledWidth ) / 2;
            float          y              = (pageHeight - scaledHeight) / 2;

            PDPage         page           = new PDPage(pageSize);
            doc.addPage(page);
            
            try (PDPageContentStream contents = new PDPageContentStream(doc, page)) {
                contents.drawImage(image, x, y, scaledWidth, scaledHeight);
            }
            System.out.println("Added: " + imagePath);
        } catch (IOException e) {
            System.err.println("Failed to process: " + imagePath);
            e.printStackTrace(System.err);
        }
    }
	
	private Path createMergedFilesDir(Path filesPath){
		try {
	        Path path = Paths.get(filesPath.toAbsolutePath().toString()+File.separator+"mergedFiles");
			Files.createDirectories(path);
			return path;
		} catch (Exception e) {
			 throw new StorageException("Could not create sub dir storage", e);
		}
	}
	
	private  File changeExtension(File f, String newExtension) {
		  int i = f.getName().lastIndexOf('.');
		  String name = f.getName().substring(0,i);
		  return new File(f.getParent() + File.separator + name + newExtension);
	}

}
