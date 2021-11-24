package com.revature.ormnl.util;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ClassObjectInspector {
    private Class clazz;
    private String clazzName;
    private String simpleClazzName;
    private Field[] clazzFields;
    private ArrayList<String> clazzFieldNames = new ArrayList<>();
    private ArrayList<Object> clazzFieldContents = new ArrayList<>();
//    private ArrayList<Object> clazzFieldContents;

    public ClassObjectInspector () {

    }

    public ClassObjectInspector (Class<?> clazz) {
        ClassInspector(clazz);
    }
    public ClassObjectInspector (Object o) {
        ClassInspector(o.getClass());
        ObjectInspector(o);
    }
    private void ClassInspector (Class<?> clazz) {
        this.clazz = clazz;
        this.clazzName = this.clazz.getName();
        this.simpleClazzName = this.clazz.getSimpleName();
        this.clazzFields = this.clazz.getDeclaredFields();
        this.clazzFieldNames.addAll(Arrays.stream(clazzFields).map(Object::toString).collect(Collectors.toList()));
//        this.clazzFieldNames = new ArrayList<String>(Arrays.asList((String[]) Arrays.stream(clazzFields).map(Object::toString).toArray()));
//        for (int i = 0; i < clazzFields.length; i++) {
//            Field field = clazzFields[i];
//            clazzFieldNames.add(i,field.getName());
//        }
    }

    private void ObjectInspector (Object o) {
        ObjectFieldInspector(o);
    }

    private void ObjectFieldInspector (Object o) {
        for (int i = 0; i < clazzFields.length; i++) {
            try {
                Field field = clazzFields[i];
                field.setAccessible(true);
                clazzFieldContents.add(i, field.get(o));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

//    Getters
    public String getClazzName() {return clazzName;}
    public String getSimpleClazzName() {return simpleClazzName;}
    public Field[] getClazzFields() {return clazzFields;}
    public ArrayList<String> getClazzFieldNames() {return clazzFieldNames;}
    public ArrayList<Object> getClazzFieldContents() {return clazzFieldContents;}
}
