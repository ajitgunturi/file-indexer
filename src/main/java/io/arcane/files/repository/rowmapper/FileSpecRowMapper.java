package io.arcane.files.repository.rowmapper;

import io.arcane.files.domain.FileSpec;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FileSpecRowMapper implements RowMapper<FileSpec> {
    @Override
    public FileSpec mapRow(ResultSet rs, int rowNum) throws SQLException {

        return FileSpec.builder()
                .id(rs.getLong("id"))
                .fileName(rs.getString("file_name"))
                .fileSize(rs.getFloat("file_size"))
                .filePath(rs.getString("file_path"))
                .systemName(rs.getString("system_name"))
                .build();
    }
}
