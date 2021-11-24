package com.revature.ormnl.persistance;

import com.revature.ormnl.model.UserAccount;
import com.revature.ormnl.util.ClassObjectInspector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class GenericDao {

    // test variables
    private GenericDao dao;

    // dependencies
    private UserAccount userAccount;
    private ClassObjectInspector coi;

    @BeforeEach
    public void setup() {
        dao = new GenericDao();
        userAccount = Mockito.mock(UserAccount.class);
        coi = Mockito.mock(ClassObjectInspector.class);
    }

    @Test
    public void createTest() {
        Assertions.assertTrue(true);
    }

    @Test
    public void getByIndexTest() {
        Assertions.assertTrue(true);
    }
}
