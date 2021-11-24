package com.revature.ormnl.persistance;

import com.revature.ormnl.util.ClassObjectInspector;
import com.revature.ormnl.util.ConnectionSingleton;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static com.revature.ormnl.util.PropertiesSingleton.loadProperties;

public class GenericDao {

    private static Properties properties;
//    private static final String propertiesPath = "src/main/resources/application.properties"; // for testing, remove in package
//    private static final String propertiesPath = "application.properties"; // for package

//    private static void loadProperties(){
//        properties = new Properties();
////        try (InputStream stream = new FileInputStream(new File(propertiesPath).getAbsolutePath())) {
//        try (InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(propertiesPath)) {
//            properties.load(stream);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public GenericDao() {
        properties = loadProperties();
    }

    public ArrayList<String> getAllTableNames() {
        String sqlGetAllTables = "select table_name from information_schema.tables where table_schema='"+
                properties.getProperty("schemaName")+"' and table_type='BASE TABLE'";
        ArrayList<String> tableNames = new ArrayList<>();
        try {
            Connection conn = ConnectionSingleton.getInstance();
            PreparedStatement stmtGetAllTables = conn.prepareStatement(sqlGetAllTables);
            ResultSet rs = stmtGetAllTables.executeQuery();
            while (rs.next()) {
                tableNames.add(rs.getString("table_name"));
            }
            rs.close(); rs.close(); stmtGetAllTables.close(); conn.close();
            return tableNames;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean create(Object obj){
        ClassObjectInspector o = new ClassObjectInspector(obj);
        String clazzSimpleName = o.getClass().getSimpleName();
        Field[] fields = o.getClazzFields();
        HashMap<String, String> objectMap = new HashMap<>();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                objectMap.put(field.getName(),field.get(o).toString());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return create(objectMap,clazzSimpleName);
    }

    public boolean create (HashMap<String,String> objectMap, String clazzSimpleName) {
        if (!doesClassExistInDatabase(clazzSimpleName)) {
            createTable(objectMap, clazzSimpleName);
        }
        StringBuilder sqlInsertFront = new StringBuilder("insert into " + clazzSimpleName + " (");
        StringBuilder sqlInsertBack = new StringBuilder(") values (");
        for (Map.Entry<String,String> entry : objectMap.entrySet()){
            sqlInsertFront.append(entry.getKey()); sqlInsertFront.append(",");
            sqlInsertBack.append("'"); sqlInsertBack.append(entry.getValue()); sqlInsertBack.append("',");
        }
        sqlInsertFront.deleteCharAt(sqlInsertFront.lastIndexOf(","));
        sqlInsertBack.deleteCharAt(sqlInsertBack.lastIndexOf(","));
        sqlInsertFront.append(sqlInsertBack);
        sqlInsertFront.append(")");
        try (Connection conn = ConnectionSingleton.getInstance()) {
            PreparedStatement stmt = conn.prepareStatement(sqlInsertFront.toString());
            stmt.executeUpdate();
            stmt.close(); conn.close();
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public HashMap<String,String> getByIndex(String clazzSimpleName, int pid) {
        String sqlGetInfoSchema = "select * from information_schema.columns WHERE table_schema = '"
                + properties.getProperty("schemaName") + "' AND table_name = '" + clazzSimpleName.toLowerCase() + "'";
        String sqlGetAll = "select * from " + clazzSimpleName.toLowerCase() + " where pid=" + pid;
        try (Connection conn = ConnectionSingleton.getInstance()) {
            PreparedStatement stmtGetInfoSchema = conn.prepareStatement(sqlGetInfoSchema);
            PreparedStatement stmtGetAll = conn.prepareStatement(sqlGetAll);
            ResultSet rsInfo = stmtGetInfoSchema.executeQuery();
            ResultSet rs = stmtGetAll.executeQuery();
            ArrayList<HashMap<String,String>> objectArray = fillObjectListFromResultSet(rsInfo, rs);
            HashMap<String,String> object = objectArray.get(0);
            rsInfo.close(); rs.close(); stmtGetInfoSchema.close(); stmtGetAll.close(); conn.close();
            return object;
        } catch (SQLException | NoSuchFieldException | IllegalAccessException | InstantiationException e) { // need to handle more gracefully
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<HashMap<String,String>> getAllByClass(String clazzSimpleName){
        String sqlGetInfoSchema = "select * from information_schema.columns WHERE table_schema = '"
                + properties.getProperty("schemaName") + "' AND table_name = '" + clazzSimpleName.toLowerCase() + "'";
        String sqlGetAll = "select * from " + clazzSimpleName.toLowerCase();
        try (Connection conn = ConnectionSingleton.getInstance()) {
            PreparedStatement stmtGetInfoSchema = conn.prepareStatement(sqlGetInfoSchema);
            PreparedStatement stmtGetAll = conn.prepareStatement(sqlGetAll);
            ResultSet rsInfo = stmtGetInfoSchema.executeQuery();
            ResultSet rs = stmtGetAll.executeQuery();
            ArrayList<HashMap<String,String>> objectArray = fillObjectListFromResultSet(rsInfo, rs);
            rsInfo.close(); rs.close(); stmtGetInfoSchema.close(); stmtGetAll.close(); conn.close();
            return objectArray;
        } catch (SQLException | NoSuchFieldException | IllegalAccessException | InstantiationException e) { // need to handle more gracefully
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateById (HashMap<String,String> objectMap, String clazzSimpleName, int pid) {
        StringBuilder sqlUpdateById = new StringBuilder("update " + clazzSimpleName + " set ");
        for (Map.Entry<String,String> entry : objectMap.entrySet()) {
            sqlUpdateById.append(entry.getKey());
            sqlUpdateById.append(" = ");
            sqlUpdateById.append(entry.getValue());
            sqlUpdateById.append(", ");
        }
        sqlUpdateById.deleteCharAt(sqlUpdateById.lastIndexOf(","));
        sqlUpdateById.append("where pid = "); sqlUpdateById.append(pid);
        try (Connection conn = ConnectionSingleton.getInstance()) {
            PreparedStatement stmtUpdateById = conn.prepareStatement(sqlUpdateById.toString());
            int i = stmtUpdateById.executeUpdate();
            stmtUpdateById.close();
            conn.close();
            return i == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteById (String clazzSimpleName, int pid) {
        String sqlDelete = "delete from " + clazzSimpleName + " where pid=" + pid;
        try (Connection conn = ConnectionSingleton.getInstance()) {
            PreparedStatement stmtDelete = conn.prepareStatement(sqlDelete);
            int i = stmtDelete.executeUpdate();
            return (i > 0);
        } catch (SQLException e) { // need to handle more gracefully
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteAllFromTable (String clazzSimpleName) {
        String sqlDelete = "truncate table " + clazzSimpleName;
        try (Connection conn = ConnectionSingleton.getInstance()) {
            PreparedStatement stmtDelete = conn.prepareStatement(sqlDelete);
            int i = stmtDelete.executeUpdate();
            return (i > 0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteTable (String clazzSimpleName) {
        String sqlDelete = "drop table " + clazzSimpleName;
        try (Connection conn = ConnectionSingleton.getInstance()) {
            PreparedStatement stmtDelete = conn.prepareStatement(sqlDelete);
            int i = stmtDelete.executeUpdate();
            return (i > 0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private ArrayList<HashMap<String,String>> fillObjectListFromResultSet(ResultSet rsInfo, ResultSet rs)
            throws SQLException, IllegalAccessException, InstantiationException, NoSuchFieldException {
        ArrayList<String> fieldNames = new ArrayList<>();
        while (rsInfo.next()) {
            fieldNames.add(rsInfo.getString("column_name"));
        }
        ArrayList<HashMap<String,String>> objectArray = new ArrayList<>(fieldNames.size());
        while (rs.next()) {
            HashMap<String, String> fieldContents = new HashMap<>();
            for (String field : fieldNames) {
                fieldContents.put(field, rs.getString(field));
            }
            objectArray.add(Integer.parseInt(fieldContents.get("pid"))-1,fieldContents);
        }
        return objectArray;

    }

    private void createTable (HashMap<String,String> objectMap, String clazzName) {
        try (Connection conn = ConnectionSingleton.getInstance()) {
            PreparedStatement stmtTable = conn.prepareStatement("create table if not exists " + clazzName + " (pid serial primary key);");
            conn.setAutoCommit(false);
            stmtTable.executeUpdate();
            stmtTable.close();
            for (String fieldName : objectMap.keySet()) {
                PreparedStatement stmtColumn = conn.prepareStatement("alter table " + clazzName + " add column if not exists " + fieldName + " varchar;");
                stmtColumn.executeUpdate();
                stmtColumn.close();
            }
            conn.commit();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void createTable (Class<?> clazz) {
        ClassObjectInspector o = new ClassObjectInspector(clazz);
        String clazzName = o.getSimpleClazzName();
        ArrayList<String> fieldNames = o.getClazzFieldNames();
        try (Connection conn = ConnectionSingleton.getInstance()) {
            PreparedStatement stmtTable = conn.prepareStatement("create table if not exists " + clazzName + " (pid serial primary key);");
            conn.setAutoCommit(false);
            stmtTable.executeUpdate();
            stmtTable.close();
            for (String fieldName : fieldNames) {
                PreparedStatement stmtColumn = conn.prepareStatement("alter table " + clazzName + " add column if not exists " + fieldName + " varchar;");
                stmtColumn.executeUpdate();
                stmtColumn.close();
            }
            conn.commit();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean doesClassExistInDatabase(String clazzSimpleName) {
        try (Connection connection = ConnectionSingleton.getInstance()) {
            PreparedStatement stmt = connection.prepareStatement("select exists (select from pg_tables " +
                    "where schemaname = ? and tablename = ?)");
            stmt.setString(1, properties.getProperty("schemaName"));
            stmt.setString(2,clazzSimpleName.toLowerCase());
            ResultSet rs = stmt.executeQuery();
            rs.next();
            boolean exists = rs.getBoolean(1);
            rs.close(); stmt.close(); connection.close();
            return exists;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }


}
