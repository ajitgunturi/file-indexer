package io.arcane.files.service;

import io.arcane.files.domain.FileSpec;
import io.arcane.files.domain.PageableResponse;
import io.arcane.files.repository.FileSpecRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
@Slf4j
public class FileService {

    private FileSpecRepository repository;
    public void listAllFiles(String path) throws IOException, UnknownHostException {

        File root = new File(path);
        File[] list = root.listFiles();
        String systemName = InetAddress.getLocalHost().getHostName();
        if (list != null) {  // In case of access error, list is null
            for (File f : list) {
                if (f.isDirectory()) {
                    listAllFiles(f.getAbsolutePath());
                    log.info("Indexing files under {}", f.getAbsolutePath());
                } else {
                    if (isValid(f)){
                        FileSpec fileSpec = FileSpec.builder()
                                .filePath(f.getAbsolutePath())
                                .fileName(f.getName())
                                .fileSize(sizeInMB(Files.size(Paths.get(f.getAbsolutePath()))))
                                .systemName(systemName)
                                .build();
                        try{
                            repository.save(fileSpec);
                        }catch (DataAccessException dae){
                            log.info("Unable to process - {}", fileSpec.getFileName());
                        }
                    }else{
                        log.info("Ignoring file - {}", f.getAbsolutePath());
                    }
                }
            }
        }
    }

    public void moveFiles() throws IOException {
        Path target = Files.createDirectory(Paths.get("FIA-"+System.currentTimeMillis()));
        int currentPage = 1;
        PageableResponse<FileSpec> fileList = repository.findAllFiles(currentPage,100);
        do {
            fileList.getRecords().forEach(fileSpec -> {
                moveFile(fileSpec, target);
                repository.updateProcessedStatus(fileSpec.getId());
            });
            log.info("{} of {} files processed", currentPage*fileList.getPageSize(), fileList.getTotalRecords());
            currentPage++;
            fileList = repository.findAllFiles(currentPage, 100);
        }   while (currentPage<=fileList.getTotalPages());
    }

    public void moveFile(FileSpec fileSpec, Path target){
        try {
            Files.move(Paths.get(fileSpec.getFilePath()), target.resolve(fileSpec.getFileName()), REPLACE_EXISTING);
        }catch (IOException e){

        }
    }

    private boolean isValid(File file){
        if (file.getName().startsWith("._")||file.getName().equalsIgnoreCase(".ds_store"))
            return false;
        else
            return !file.getName().endsWith(".pdf") && !file.getName().endsWith(".txt") && !file.getName().endsWith(".dat") && !file.getName().endsWith(".avi");
    }

    private float sizeInMB(long sizeInLong){
        return sizeInLong/(1024*1024);
    }

    @Autowired
    public void setRepository(FileSpecRepository repository) {
        this.repository = repository;
    }
}
