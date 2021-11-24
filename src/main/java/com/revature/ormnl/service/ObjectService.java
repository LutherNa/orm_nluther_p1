package com.revature.ormnl.service;

import com.revature.ormnl.persistance.GenericDao;
import com.revature.ormnl.util.ClassObjectInspector;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

public class ObjectService {

    private final GenericDao dao = new GenericDao();
    private boolean DeleteNotTruncate = false;

    public boolean add(Object o) {
        return dao.create(o);
    }

    public boolean add(HashMap<String,String> objectMap, String clazzSimpleName) {
        return dao.create(objectMap, clazzSimpleName);
    }

    public Object getObject(String clazzName, int index) throws ClassNotFoundException {
        Class<?> clazz = Class.forName(clazzName);
        HashMap<String,String> objectMap = get(clazz.getSimpleName(),index);
        return ObjectBuilder(objectMap,clazz);
    }

    public ArrayList<Object> getObject(String clazzName) throws ClassNotFoundException {
        Class<?> clazz = Class.forName(clazzName);
        ArrayList<HashMap<String,String>> objectMaps = get(clazz.getSimpleName());
        ArrayList<Object> objects = new ArrayList<>();
        for (HashMap<String,String> objectMap : objectMaps) {
            objects.add(ObjectBuilder(objectMap, clazz));
        }
        return objects;
    }

    public HashMap<String,String> get(String clazzSimpleName, int index) throws ClassNotFoundException {
//        Class<?> clazz = setClass(clazzName);
        if (!dao.doesClassExistInDatabase(clazzSimpleName)) throw new ClassNotFoundException();
        return dao.getByIndex(clazzSimpleName, index);
    }

    public ArrayList<HashMap<String,String>> get(String clazzSimpleName) throws ClassNotFoundException {
//        Class<?> clazz = setClass(clazzName);
        if (!dao.doesClassExistInDatabase(clazzSimpleName)) throw new ClassNotFoundException();
        return dao.getAllByClass(clazzSimpleName);
    }

    public boolean update(HashMap<String,String> objectMap, String clazzSimpleName, int pid) {
        return dao.updateById(objectMap, clazzSimpleName, pid);
    }
    public boolean update(HashMap<String,String> objectMap, Class<?> clazz, int pid) {
        return dao.updateById(objectMap, clazz.getSimpleName(), pid);
    }

    public boolean delete(String clazzSimpleName, int pid) {
        return dao.deleteById(clazzSimpleName, pid);
    }
    public boolean delete(String clazzSimpleName) {
        if (DeleteNotTruncate) return dao.deleteTable(clazzSimpleName); else return dao.deleteAllFromTable(clazzSimpleName);
    }

    public boolean toggleDeleteNotTruncate() {
        if (DeleteNotTruncate) {
            this.DeleteNotTruncate = false;
            return false;
        } else {
            this.DeleteNotTruncate = true;
            return true;
        }
    }

    private Class<?> setClass(String clazzName) {
        try {
            return Class.forName(clazzName);
        } catch (ClassNotFoundException e) {
            System.out.println("Class Not Found in Program Context");
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<String> getAllTableNames() {
        return dao.getAllTableNames();
    }

    private Object ObjectBuilder(HashMap<String,String> objectMap, Class<?> clazz) {
            Object o = null;
        try {
            o = clazz.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        assert o != null;
        Field[] fields = new ClassObjectInspector(o).getClazzFields();
        for (Field field : fields) {
            if (field.getType().equals(String.class) || field.getType().isPrimitive()) {
                boolean isFieldAccessible = field.isAccessible();
                Object value;
                Object[] values;
                field.setAccessible(true);
                try {
                    if (field.getType().isArray()) {
                        values = objectMap.get(field.getName().toLowerCase()).substring(1, field.getName().length() - 1).split(", ");
                        for (int k = 0; k < values.length; k++) {
                            values[k] = toPrimitiveIfPrimitive(field.getType(), values[k].toString());
                        }
                        field.set(o, values);
                    } else {
                        value = toPrimitiveIfPrimitive(field.getType(), objectMap.get(field.getName().toLowerCase()));
                        field.set(o, value);
                    }
//                    System.out.println("set field contents: " + field.get(o));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                field.setAccessible(isFieldAccessible);
            }
        }
            return o;
    }

    private static Object toPrimitiveIfPrimitive(Class<?> fieldClazz, String value ) {
//        System.out.println("Field Class " + fieldClazz + " Value: " + value);
        if(fieldClazz.isPrimitive()) {
            if (fieldClazz.getTypeName().equals("bool")) return Boolean.parseBoolean(value);
            if (fieldClazz.getTypeName().equals("byte")) return Byte.parseByte(value);
            if (fieldClazz.getTypeName().equals("short")) return Short.parseShort(value);
            if (fieldClazz.getTypeName().equals("int")) return Integer.parseInt(value);
            if (fieldClazz.getTypeName().equals("long")) return Long.parseLong(value);
            if (fieldClazz.getTypeName().equals("float")) return Float.parseFloat(value);
            if (fieldClazz.getTypeName().equals("double")) return Double.parseDouble(value);
            if (fieldClazz.getTypeName().equals("char")) return value.charAt(0);
        }
        return value;
    }

}
