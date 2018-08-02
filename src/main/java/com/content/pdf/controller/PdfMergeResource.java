package com.content.pdf.controller;

import org.springframework.hateoas.ResourceSupport;

public class PdfMergeResource extends ResourceSupport {
	
	private String filePath;

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
}
