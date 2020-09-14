package com.svi.accountchecker.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Filewalker {
	private List<File> fileList = new ArrayList<>();
	
	public void setFileList(String path) {
        File root = new File(path);
        File[] list = root.listFiles();

        if (list == null) {
        	return;
        }

        for (File f : list) {
        	if (f.isDirectory()) {
        		setFileList(f.getAbsolutePath());
//        		System.out.println("Dir:" + f.getAbsoluteFile());
        	} else {
//        		System.out.println("File:" + f.getAbsoluteFile());
        		fileList.add(f.getAbsoluteFile());
        	}
        }
    }

	public List<File> getFileList() {
		return fileList;
	}
	
	public static File searchFile(File file, String search) {
	    if (file.isDirectory()) {
	        File[] arr = file.listFiles();
	        for (File f : arr) {
	            File found = searchFile(f, search);
	            if (found != null)
	                return found;
	        }
	    } else {
	        if (file.getName().equals(search)) {
	            return file;
	        }
	    }
	    return null;
	}
}
