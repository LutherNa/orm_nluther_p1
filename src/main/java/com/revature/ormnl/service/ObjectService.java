package com.revature.ormnl.service;

import com.revature.ormnl.persistance.GenericDao;
import com.revature.ormnl.util.ClassObjectInspector;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

public class ObjectService {

    private final GenericDao dao = new GenericDao();

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
                System.out.println("result set contents: " + objectMap.get(field.toString()));
                try {
                    if (field.getType().isArray()) {
                        values = objectMap.get(field.toString()).substring(1, field.toString().length() - 1).split(", ");
                        for (int k = 0; k < values.length; k++) {
                            values[k] = toPrimitiveIfPrimitive(field.getType(), values[k].toString());
                        }
                        field.set(o, values);
                    } else {
                        value = toPrimitiveIfPrimitive(field.getType(), objectMap.get(field.toString()));
                        field.set(o, value);
                    }
                    System.out.println("set field contents: " + field.get(o));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                field.setAccessible(isFieldAccessible);
            }
        }
            return o;
    }

    private static Object toPrimitiveIfPrimitive( Class<?> fieldClazz, String value ) {
        System.out.println("Field Class " + fieldClazz + " Value: " + value);
        if( Boolean.class == fieldClazz ) return Boolean.parseBoolean( value );
        if( Byte.class == fieldClazz ) return Byte.parseByte( value );
        if( Short.class == fieldClazz ) return Short.parseShort( value );
        if( Integer.class == fieldClazz ) return Integer.parseInt( value );
        if( Long.class == fieldClazz ) return Long.parseLong( value );
        if( Float.class == fieldClazz ) return Float.parseFloat( value );
        if( Double.class == fieldClazz ) return Double.parseDouble( value );
        if( Character.class == fieldClazz ) return value.toString().charAt(0);
        return value;
    }

}
