package io.arcane.files.domain;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PageableResponse<T> {
    private List<FileSpec> records;
    private int pageSize;
    private int pageNumber;
    private int totalPages;
    private int totalRecords;
}
