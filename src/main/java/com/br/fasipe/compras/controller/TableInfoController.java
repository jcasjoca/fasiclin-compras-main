package com.br.fasipe.compras.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/tables")
public class TableInfoController {
    
    @Autowired
    private DataSource dataSource;
    
    @GetMapping("/info")
    public Map<String, Object> getTablesInfo() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> tables = new ArrayList<>();
        
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getTables("fasiclin", null, "%", new String[]{"TABLE"});
            
            while (resultSet.next()) {
                Map<String, Object> tableInfo = new HashMap<>();
                tableInfo.put("tableName", resultSet.getString("TABLE_NAME"));
                tableInfo.put("tableType", resultSet.getString("TABLE_TYPE"));
                tables.add(tableInfo);
            }
            
            result.put("status", "SUCCESS");
            result.put("totalTables", tables.size());
            result.put("tables", tables);
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("error", e.getMessage());
        }
        
        return result;
    }
}
