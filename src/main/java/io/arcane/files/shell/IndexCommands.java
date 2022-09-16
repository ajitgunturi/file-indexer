package io.arcane.files.shell;

import io.arcane.files.domain.IndexStatus;
import io.arcane.files.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.io.IOException;

@ShellComponent
public class IndexCommands {
    private FileService service;

    @ShellMethod(value = "Method to index files and store in connected db", key="index")
    public void indexFiles(String root) throws IOException {
        service.listAllFiles(root);
    }
    @ShellMethod("Moves files already indexed in database")
    public void move() throws IOException{
        service.moveFiles();
    }

    @Autowired
    public void setService(FileService service) {
        this.service = service;
    }
}
