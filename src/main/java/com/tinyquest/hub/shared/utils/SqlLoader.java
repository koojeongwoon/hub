package com.tinyquest.hub.shared.utils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SqlLoader {

    // resources/sql 디렉토리 존재해야 함.
    // 사용하는 쪽에서는 String sql = SqlLoader.fromClasspath("promotion_item_upsert.sql");
    public static String fromClasspath(String path) {
        try {
            URL resource = Thread.currentThread().getContextClassLoader().getResource("sql/".concat(path));
            if (resource == null) {
                throw new IllegalArgumentException("SQL 파일을 찾을 수 없습니다: " + path);
            }
            return Files.readString(Paths.get(resource.toURI()), StandardCharsets.UTF_8);
        } catch (IOException | URISyntaxException e) {
            throw new UncheckedIOException("SQL 파일 읽기 실패: " + path, new IOException(e));
        }
    }

}
