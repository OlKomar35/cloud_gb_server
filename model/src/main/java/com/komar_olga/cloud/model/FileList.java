package com.komar_olga.cloud.model;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FileList extends AbstractMessage {
    private List<String> fileName = new ArrayList<>();
    private List<String> fileType = new ArrayList<>();
    private List<Long> fileSize = new ArrayList<>();
    private String directory;


    public String getDirectory() {
        return directory;
    }

    public List<String> getFileName() {
        return fileName;
    }

    public List<String> getFileType() {
        return fileType;
    }

    public List<Long> getFileSize() {
        return fileSize;
    }


    public FileList(String directory) throws IOException {
        this.directory = directory;
        System.out.println(directory);
        List<String> filesNameArr = Files.list(Paths.get(directory))
                .filter(p -> !Files.isDirectory(p))
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toList());
        filesNameArr.forEach(System.out::println);
        System.out.println("------------------------------------------");

        for (int i = 0; i < filesNameArr.size(); i++) {
            FileChannel imageFileChannel = FileChannel.open(Path.of(directory + filesNameArr.get(i)));
            long size = imageFileChannel.size();
            String[] parts = filesNameArr.get(i).split("\\.");
            this.fileName.add(parts[0]);
            this.fileType.add(parts[1]);
            this.fileSize.add(size);

        }
        System.out.println(fileName.size()+" "+filesNameArr.size());
    }
}
