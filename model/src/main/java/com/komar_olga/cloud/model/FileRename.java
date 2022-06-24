package com.komar_olga.cloud.model;

public class FileRename extends AbstractMessage{
    String sourcePath, destinationPath;


    public FileRename(String sourcePath, String destinationPath) {
        this.sourcePath = sourcePath;
        this.destinationPath = destinationPath;

    }

    public String getSourcePath() {
        return sourcePath;
    }

    public String getDestinationPath() {
        return destinationPath;
    }


}
