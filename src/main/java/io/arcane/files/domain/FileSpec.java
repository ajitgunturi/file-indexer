package io.arcane.files.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileSpec {

    private long id;
    private String fileName;
    private String filePath;
    private float fileSize;
    private String systemName;

}
