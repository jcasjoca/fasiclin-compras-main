package com.br.fasipe.compras.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/database")
public class DatabaseAdminController {
    
    @Autowired
    private DataSource dataSource;
    
    @GetMapping("/processes")
    public Map<String, Object> listProcesses() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> processes = new ArrayList<>();
        
        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT ID, USER, HOST, DB, COMMAND, TIME, STATE, INFO FROM information_schema.processlist WHERE ID <> CONNECTION_ID()";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> process = new HashMap<>();
                process.put("id", rs.getLong("ID"));
                process.put("user", rs.getString("USER"));
                process.put("host", rs.getString("HOST"));
                process.put("database", rs.getString("DB"));
                process.put("command", rs.getString("COMMAND"));
                process.put("time", rs.getInt("TIME"));
                process.put("state", rs.getString("STATE"));
                process.put("info", rs.getString("INFO"));
                processes.add(process);
            }
            
            result.put("status", "SUCCESS");
            result.put("totalProcesses", processes.size());
            result.put("processes", processes);
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("error", e.getMessage());
        }
        
        return result;
    }
    
    @GetMapping("/kill-commands")
    public Map<String, Object> generateKillCommands() {
        Map<String, Object> result = new HashMap<>();
        List<String> killCommands = new ArrayList<>();
        
        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT CONCAT('KILL ', ID, ';') as kill_command FROM information_schema.processlist WHERE ID <> CONNECTION_ID()";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            
            while (rs.next()) {
                killCommands.add(rs.getString("kill_command"));
            }
            
            result.put("status", "SUCCESS");
            result.put("totalCommands", killCommands.size());
            result.put("killCommands", killCommands);
            result.put("warning", "ATENÇÃO: Estes comandos irão terminar todas as outras conexões!");
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("error", e.getMessage());
        }
        
        return result;
    }
    
    @PostMapping("/kill-other-connections")
    public Map<String, Object> killOtherConnections() {
        Map<String, Object> result = new HashMap<>();
        List<String> executedCommands = new ArrayList<>();
        int successCount = 0;
        int errorCount = 0;
        
        try (Connection connection = dataSource.getConnection()) {
            // Primeiro, obter os IDs das conexões para matar
            String selectSql = "SELECT ID FROM information_schema.processlist WHERE ID <> CONNECTION_ID()";
            PreparedStatement selectStatement = connection.prepareStatement(selectSql);
            ResultSet rs = selectStatement.executeQuery();
            
            List<Long> processIds = new ArrayList<>();
            while (rs.next()) {
                processIds.add(rs.getLong("ID"));
            }
            
            // Executar KILL para cada processo
            for (Long processId : processIds) {
                try {
                    String killSql = "KILL " + processId;
                    PreparedStatement killStatement = connection.prepareStatement(killSql);
                    killStatement.executeUpdate();
                    executedCommands.add("KILL " + processId + "; -- SUCCESS");
                    successCount++;
                } catch (Exception e) {
                    executedCommands.add("KILL " + processId + "; -- ERROR: " + e.getMessage());
                    errorCount++;
                }
            }
            
            result.put("status", "COMPLETED");
            result.put("totalProcesses", processIds.size());
            result.put("successCount", successCount);
            result.put("errorCount", errorCount);
            result.put("executedCommands", executedCommands);
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("error", e.getMessage());
        }
        
        return result;
    }
    
    @GetMapping("/connection-info")
    public Map<String, Object> getConnectionInfo() {
        Map<String, Object> result = new HashMap<>();
        
        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT CONNECTION_ID() as current_connection_id";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            
            if (rs.next()) {
                result.put("currentConnectionId", rs.getLong("current_connection_id"));
            }
            
            result.put("status", "SUCCESS");
            result.put("info", "Esta é sua conexão atual que será preservada");
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("error", e.getMessage());
        }
        
        return result;
    }
}
