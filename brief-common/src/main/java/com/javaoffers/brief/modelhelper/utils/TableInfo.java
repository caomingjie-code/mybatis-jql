package com.javaoffers.brief.modelhelper.utils;

import com.javaoffers.brief.modelhelper.anno.derive.flag.DeriveFlag;
import com.javaoffers.brief.modelhelper.anno.derive.flag.DeriveInfo;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: table description
 * @Auther: create by cmj on 2022/5/4 00:00
 */
public class TableInfo {

    /**
     * modelClazz
     */
    private Class<?> modelClazz;

    /**
     * database type
     */
    private DBType dbType;

    /**
     * table name
     */
    private String tableName;

    /**
     * K: table field (col Name)
     * V: Corresponding table field information, obtained by database query
     */
    private Map<String, ColumnInfo> primaryColNames = new LinkedHashMap<>();

    /**
     * This property is typically used for inserts and updates
     * K: fields of the table (Col Name)
     * V: Corresponding table field information, obtained by database query
     */
    private Map<String, ColumnInfo> colNames = new LinkedHashMap<>();

    /**
     * Store field information
     */
    private Set<ColumnInfo> columnInfos = new LinkedHashSet<>();

    /**
     * This property is typically used for inserts and updates
     * K: col name of table
     * V: the corresponding table field, generated by the class attribute
     */
    private Map<String, List<Field>> originalColNameOfModelField = new LinkedHashMap<>();

    /**
     * Record the field information of the model class.
     * fieldName as alias name
     */
    private Map<String, Field> fieldNameAndFields = new HashMap<>();

    /**
     * This property is usually used when querying
     * K: attribute name (also alias)
     * V: the corresponding table field, generated by the class attribute, or sql-function field
     * for Sql-Fun example : key: sex ,  value: IF(sex = 1,'boy','girl').
     * lazy loading storage
     */
    private Map<String, String> fieldNameMappingcolNameOfModel = new LinkedHashMap<>();

    /**
     * This property is usually used when querying
     * K: colName
     * V: Fields of model class
     * lazy loading storage
     */
    private Map<String, List<Field>> colNameMappingModelFields = new LinkedHashMap<>();

    /**
     * The lamda information will be filled during the running process.
     * This colNameOfGetter is only provided to Table Helper
     * K: The method name obtained by lamda
     * V: The corresponding table fields are generated by lamda (colName)
     * lazy loading storage
     */
    private Map<String, String> colNameOfGetter = new ConcurrentHashMap<>();

    /**
     * col name mapping anno info
     */
    private Map<String, ColNameAnnoInfo> colNameAnnoInfos = new HashMap<>();

    /**
     * fieldName mapping anno info
     */
    private Map<String, FieldNameAnnoInfo> fieldNameAnnoInfos = new HashMap<>();

    /**
     * derive info.
     * string: colName info
     */
    private Map<DeriveFlag, DeriveInfo> deriveColName = new HashMap<>();



    /***************************************************************/
    public DBType getDbType() {
        return dbType;
    }

    public String getTableName() {
        return tableName;
    }

    public Map<String, ColumnInfo> getColNames() {
        return colNames;
    }

    public void putColNames(String colName, ColumnInfo columnInfo) {
        this.colNames.put(colName, columnInfo);
    }

    public Set<ColumnInfo> getColumnInfos() {
        return columnInfos;
    }

    public TableInfo(String tableName) {
        this.tableName = tableName;
    }

    public void setDbType(DBType dbType){
        this.dbType = dbType;
    }

    public TableInfo setModelClass(Class clazz){
        this.modelClazz = clazz;
        return this;
    }

    public Class<?> getModelClass(){
        return this.modelClazz;
    }

    public Map<String, String> getFieldNameColNameOfModel() {
        return fieldNameMappingcolNameOfModel;
    }

    void putFieldNameColNameOfModel(String fieldName, String colName) {
        this.fieldNameMappingcolNameOfModel.put(fieldName, colName);
    }

    Map<String, String> getMethodNameMappingFieldNameOfGetter() {
        return colNameOfGetter;
    }

    public Map<String, List<Field>> getColNameAndFieldOfModel() {
        return colNameMappingModelFields;
    }


    private ColumnInfo getColumnInfo(String colName) {
        ColumnInfo columnInfo = this.getColNames().get(colName);
        Assert.isTrue(columnInfo != null, "the " + colName + "col name is empty");
        return columnInfo;
    }

    public Map<String, ColumnInfo> getPrimaryColNames() {
        return primaryColNames;
    }

    public Map<String, List<Field>> getOriginalColNameOfModelField() {
        return originalColNameOfModelField;
    }

    public boolean isSqlFun(String colName) {
        ColNameAnnoInfo colNameAnnoInfo = getColNameAnnoInfoNotCreate(colName);
        return colNameAnnoInfo.isSqlFun();
    }

    public boolean colNameIsExcludeColAll(String colName) {
        ColNameAnnoInfo colNameAnnoInfoNotCreate = getColNameAnnoInfoNotCreate(colName);
        return colNameAnnoInfoNotCreate.isExcludeColAll();
    }

    public Map<String, Field> getFieldNameAndField() {
        return this.fieldNameAndFields;
    }

    public boolean fieldNameIsExcludeColAll(String fieldName){
        FieldNameAnnoInfo fieldNameAnnoInfo = getFieldNameAnnoInfoNotCreate(fieldName);
        return fieldNameAnnoInfo.isExcludeColAll();

    }

    private FieldNameAnnoInfo getFieldNameAnnoInfoNotCreate(String fieldName) {
        return fieldNameAnnoInfos.getOrDefault(fieldName, new FieldNameAnnoInfo());
    }

    void putFieldNameExcludeColAll(String fieldName, boolean isExcludeColAll) {
        FieldNameAnnoInfo fieldNameAnnoInfo = getFieldNameAnnoInfoOrCreate(fieldName);
        fieldNameAnnoInfo.setExcludeColAll(isExcludeColAll);
    }

    FieldNameAnnoInfo getFieldNameAnnoInfoOrCreate(String fieldName) {
        FieldNameAnnoInfo fieldNameAnnoInfo = fieldNameAnnoInfos.get(fieldName);
        if(fieldNameAnnoInfo == null){
            fieldNameAnnoInfo = new FieldNameAnnoInfo();
            fieldNameAnnoInfo.setFieldName(fieldName);
        }
        fieldNameAnnoInfos.put(fieldName, fieldNameAnnoInfo);
        return fieldNameAnnoInfo;
    }

    Map<String, List<Field>> putColNameAndFieldOfModel(String colName, Field field) {
        List<Field> fields = colNameMappingModelFields.get(colName);
        if (fields == null) {
            fields = new LinkedList<>();
            colNameMappingModelFields.put(colName, fields);
        }
        fields.add(field);
        return colNameMappingModelFields;
    }

    ColNameAnnoInfo getColNameAnnoInfoOrCreate(String colName) {
        ColNameAnnoInfo colNameAnnoInfo = colNameAnnoInfos.get(colName);
        if (colNameAnnoInfo == null) {
            colNameAnnoInfo = new ColNameAnnoInfo();
            colNameAnnoInfo.setColName(colName);
        }
        colNameAnnoInfos.put(colName, colNameAnnoInfo);
        return colNameAnnoInfo;
    }

    ColNameAnnoInfo getColNameAnnoInfoNotCreate(String colName) {
        return colNameAnnoInfos.getOrDefault(colName, new ColNameAnnoInfo());
    }

    public void putPrimaryColNames(String primaryCol, ColumnInfo columnInfo) {
        primaryColNames.put(primaryCol, columnInfo);
    }

    void putOriginalColNameAndFieldOfModelField(String colName, Field field) {
        List<Field> fields = this.originalColNameOfModelField.get(colName);
        if (fields == null) {
            fields = new ArrayList<>();
            this.originalColNameOfModelField.put(colName, fields);
        }
        fields.add(field);
    }

    void putSqlFun(String colName, boolean isFunFun) {
        ColNameAnnoInfo colNameAnnoInfo = getColNameAnnoInfoOrCreate(colName);
        colNameAnnoInfo.setSqlFun(isFunFun);
    }

    void putFieldNameAndField(String fieldName, Field field) {
        this.fieldNameAndFields.put(fieldName, field);
    }

    void putColNameExcludeColAll(String colName, boolean isFunGroup) {
        ColNameAnnoInfo colNameAnnoInfo = getColNameAnnoInfoOrCreate(colName);
        colNameAnnoInfo.setExcludeColAll(isFunGroup);
    }

    void putDeriveColName(DeriveFlag deriveFlag, DeriveInfo colName){
        this.deriveColName.put(deriveFlag, colName);
    }

    public DeriveInfo getDeriveColName(DeriveFlag deriveFlag){
        return this.deriveColName.get(deriveFlag);
    }

    public void unmodifiable() {
        this.colNames = Collections.unmodifiableMap(colNames);
        this.fieldNameMappingcolNameOfModel = Collections.unmodifiableMap(fieldNameMappingcolNameOfModel);
        this.colNameMappingModelFields = Collections.unmodifiableMap(colNameMappingModelFields);
        this.primaryColNames = Collections.unmodifiableMap(primaryColNames);
        this.originalColNameOfModelField = Collections.unmodifiableMap(originalColNameOfModelField);
        this.fieldNameAndFields = Collections.unmodifiableMap(this.fieldNameAndFields);
        this.deriveColName = Collections.unmodifiableMap(this.deriveColName);
    }

}
