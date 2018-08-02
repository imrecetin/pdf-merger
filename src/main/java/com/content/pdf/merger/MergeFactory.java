package com.content.pdf.merger;


import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class MergeFactory {

	@Autowired
	private ApplicationContext context;
	
	public enum AS{
		PDF
	}
	
	public Path merge(Path filesPath,MergeFactory.AS mergeType) {
		IDocumentMerge documentMerge=findDocumentMergeBean(mergeType);
		return documentMerge.merge(filesPath);
	}

	private IDocumentMerge findDocumentMergeBean(MergeFactory.AS mergeType) {
		IDocumentMerge documentMerge;
		switch (mergeType) {
			case PDF:
				documentMerge=(PDFMerge)context.getBean(PDFMerge.class);
				break;
			default:
				documentMerge=(PDFMerge)context.getBean(PDFMerge.class);
				break;
		}
		return documentMerge;
	}

}
