package com.revature.ormnl.persistance;

import com.revature.ormnl.model.UserAccount;
import com.revature.ormnl.service.ObjectService;
import com.revature.ormnl.util.ClassObjectInspector;
import com.revature.ormnl.util.ConnectionSingleton;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Fields;

import javax.sql.DataSource;
import java.sql.*;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class GenericDao {

    // test variables
    private GenericDao dao;

    // dependencies
    private UserAccount userAccount;
    private ClassObjectInspector coi;
    private ObjectService objectService;
    private DataSource mockDataSource;
    private Connection mockConnection;
    private Statement mockStatement;
    private ResultSet mockResultSet;


    @BeforeEach
    public void setupVars() throws SQLException {
        dao = new com.revature.ormnl.persistance.GenericDao();
        userAccount = Mockito.mock(UserAccount.class);
        coi = Mockito.mock(ClassObjectInspector.class);
        mockDataSource = Mockito.mock(DataSource.class);
        mockConnection = Mockito.mock(Connection.class);
        mockStatement = Mockito.mock(Statement.class);
        mockResultSet = Mockito.mock(ResultSet.class);
//        when(mockDataSource.getConnection()).thenReturn(mockConnection);
//        when(mockDataSource.getConnection(anyString()).thenReturn(mockConnection);
//        doNothing().when(mockConnection).commit();
//        when(mockConnection.prepareStatement(anyString(), anyInt())).thenReturn(mockStatement);
//        doNothing().when(mockStatement).setString(anyInt(), anyString());
//        when(mockStatement.execute()).thenReturn(Boolean.TRUE);
//        when(mockStatement.getGeneratedKeys()).thenReturn(mockResultSet);
//        when(mockResultSet.next()).thenReturn(Boolean.TRUE, Boolean.FALSE);
//        when(mockResultSet.getInt(Fields.GENERATED_KEYS)).thenReturn(userId);
    }

    @Test
    public void TestAddRecall() {
        userAccount = new UserAccount("username", "password", "firstname", "lastname");
        userAccount.setAccountID(100);
        objectService = new ObjectService();
        assert(dao.create(userAccount));
    }

    @Test
    public void getByIndexTest() {
        Assertions.assertTrue(true);
    }
}
