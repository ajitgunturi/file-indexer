package io.arcane.files.repository;

import io.arcane.files.domain.FileSpec;
import io.arcane.files.domain.PageableResponse;
import io.arcane.files.repository.rowmapper.FileSpecRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class FileSpecRepository {

    private NamedParameterJdbcTemplate jdbcTemplate;

    public void save(FileSpec fileSpec){
        Map<String, Object> paramMap = new HashMap<>();

        paramMap.put("file_name", fileSpec.getFileName());
        paramMap.put("file_path", fileSpec.getFilePath());
        paramMap.put("file_size", fileSpec.getFileSize());
        paramMap.put("system_name", fileSpec.getSystemName());

        String query = "insert into file_spec " +
                "(file_name, file_path, file_size, system_name)" +
                "values (:file_name, :file_path, :file_size, :system_name)";

        jdbcTemplate.update(query,paramMap);
    }

    public void updateProcessedStatus(long fileId){
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", fileId);
        jdbcTemplate.update("update file_spec set processed=true where id=:id", paramMap);
    }

    public PageableResponse findAllFiles(int pageNo, int pageSize) {
        Map<String, Object> paramMap = new HashMap<>();

        int limit;
        limit = pageSize;
        /*
         * Changed the offset formulae to include
         * the first record as well.
         */
        int offSet = pageSize * (pageNo - 1);
        /*
         * Check if offset is less than or equal to
         * 0. Set offset to 0,if it is -ve or 0.
         * Page 1 and Page 0 will return the same records
         *
         */
        offSet = Math.max(offSet, 0);


        paramMap.put("limit", limit); // limit=pagesize
        paramMap.put("offSet", offSet); // offset=pageNo
        int totalRecords = jdbcTemplate.queryForObject("select count(distinct file_name) from file_spec where file_name not like '%(%' and processed=false", paramMap, Integer.class);
        float pageTotal = (float) totalRecords / (float) pageSize;
        int totalPages = (int) Math.ceil(pageTotal);

        List<FileSpec> fileSpecList = jdbcTemplate.query("select id, file_name, file_size, file_path, system_name from file_spec where file_name not like '%(%' and file_name in (select distinct file_name from file_spec) and processed=false ", paramMap,
                new FileSpecRowMapper());
        return PageableResponse.builder().records(fileSpecList).pageNumber(pageNo)
                .pageSize(pageSize).totalPages(totalPages).totalRecords(totalRecords).build();

    }



    @Autowired
    public void setJdbcTemplate(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
