package ro.teamnet.zth.api.em;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import ro.teamnet.zth.api.annotations.Id;
import ro.teamnet.zth.api.database.DBManager;
import ro.teamnet.zth.api.database.DBProperties;

public class EntityManagerImpl implements EntityManager {

    @Override
    public <T> T insert(T entity) {
        try (Connection conn = DBManager.getConnection();
             Statement stmt = conn.createStatement()) {

            QueryBuilder query = new QueryBuilder();
            //get table name
            String tableName = EntityUtils.getTableName(entity.getClass());
            //get columns
            List<ColumnInfo> columns = EntityUtils.getColumns(entity.getClass());

            Integer lastId;
            //set values for columns
            for(ColumnInfo column : columns) {
                if(column.isId()) {
                    if(DBProperties.IS_ORACLE) {
                        //used for Oracle DB
                        lastId = getSeqNextValue().intValue();
                        column.setValue(lastId);
                    }
                } else {
                    Field field = entity.getClass().getDeclaredField(column.getColumnName());
                    field.setAccessible(true);
                    Object value = field.get(entity);
                    column.setValue(EntityUtils.getSqlValue(value));
                }
            }
            //set details in QueryBuilder
            query.setTableName(tableName).setQueryType(QueryType.INSERT).addQueryColumns(columns);
            //create sql statement
            String sql = query.createQuery();
            //execute sql
            stmt.executeUpdate(sql);

            if(DBProperties.IS_MYSQL) {
                //get last id (used for MySQL)
                sql = "select LAST_INSERT_ID()";
                ResultSet rs = stmt.executeQuery(sql);
                rs.next();
                lastId = rs.getInt(1);
                rs.close();
            }

            return (T) findById(entity.getClass(), lastId);
        } catch(SQLException | ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public <T> T update(T entity) {
        try (Connection conn = DBManager.getConnection();
             Statement stmt = conn.createStatement()) {
            QueryBuilder query = new QueryBuilder();
            //get table name
            String tableName = EntityUtils.getTableName(entity.getClass());
            //get columns
            List<ColumnInfo> columns = EntityUtils.getColumns(entity.getClass());
            //set values for columns
            for(ColumnInfo column : columns) {
                Field field = entity.getClass().getDeclaredField(column.getColumnName());
                field.setAccessible(true);
                Object value = field.get(entity);
                column.setValue(EntityUtils.getSqlValue(value));
            }
            Condition condition = new Condition(columns.get(0).getDbName(), columns.get(0).getValue());
            query = query.setTableName(tableName).setQueryType(QueryType.UPDATE).addQueryColumns(columns)
                .addCondition(condition);
            String sql = query.createQuery();
            stmt.executeUpdate(sql);
            return entity;
        } catch(SQLException | ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void delete(Object entity) {
        try( Connection conn = DBManager.getConnection();
             Statement stmt = conn.createStatement()){
            QueryBuilder query = new QueryBuilder();
            String tableName = EntityUtils.getTableName(entity.getClass());
            List<ColumnInfo> columns = EntityUtils.getColumns(entity.getClass());

            for(ColumnInfo column : columns) {
                Field field = entity.getClass().getDeclaredField(column.getColumnName());
                field.setAccessible(true);
                Object value = field.get(entity);
                column.setValue(EntityUtils.getSqlValue(value));
            }
            Condition condition = new Condition(columns.get(0).getDbName(), columns.get(0).getValue());
            query.setTableName(tableName).setQueryType(QueryType.DELETE).addCondition(condition);
            String sql = query.createQuery();
            stmt.executeUpdate(sql);
        } catch (SQLException | ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private Long getSeqNextValue() throws SQLException, ClassNotFoundException {
        ResultSet rs;
        try(Connection conn = DBManager.getConnection();
            Statement stmt = conn.createStatement()){
            String sql = "select ZTH_SEQ.nextval from dual";
            rs = stmt.executeQuery(sql);
            rs.next();
            return rs.getLong(1);
        }
    }

    @Override
    public <T> T findById(Class<T> entityClass, Integer id) {
        try (Connection conn = DBManager.getConnection();
             Statement stmt = conn.createStatement()) {

            QueryBuilder query = new QueryBuilder();
            String tableName = EntityUtils.getTableName(entityClass);
            List<ColumnInfo> columns = EntityUtils.getColumns(entityClass);
            List<Field> fieldsByAnnotations = EntityUtils.getFieldsByAnnotations(entityClass, Id.class);

            Condition condition = new Condition(fieldsByAnnotations.get(0).getAnnotation(Id.class).name(), id);
            query.setTableName(tableName).addQueryColumns(columns).setQueryType(QueryType.SELECT).addCondition(
                condition);
            String sql = query.createQuery();
            ResultSet rs = stmt.executeQuery(sql);

            T instance = null;
            if(rs.next()) {
                instance = entityClass.newInstance();
                for(ColumnInfo column : columns) {
                    column.setValue(rs.getObject(column.getDbName()));
                    Field field = instance.getClass().getDeclaredField(column.getColumnName());
                    field.setAccessible(true);
                    field.set(instance, EntityUtils.castFromSqlType(column.getValue(), column.getColumnType()));
                }
            }
            return instance;
        } catch(Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public <T> List<T> findAll(Class<T> entityClass) {
        try (Connection conn = DBManager.getConnection();
             Statement stmt = conn.createStatement()){

            List<T> rows = new ArrayList<>();
            QueryBuilder query = new QueryBuilder();
            String tableName = EntityUtils.getTableName(entityClass);
            List<ColumnInfo> columns = EntityUtils.getColumns(entityClass);
            query.setTableName(tableName).addQueryColumns(columns).setQueryType(QueryType.SELECT);
            String sql = query.createQuery();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                T instance = entityClass.newInstance();
                for (ColumnInfo column : columns) {
                    column.setValue(rs.getObject(column.getDbName()));
                    Field field = instance.getClass().getDeclaredField(column.getColumnName());
                    field.setAccessible(true);
                    field.set(instance, EntityUtils.castFromSqlType(column.getValue(), column.getColumnType()));
                }
                rows.add(instance);
            }
            return rows;
        } catch (SQLException | ClassNotFoundException | NoSuchFieldException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public <T> List<T> findByParams(Class<T> entityClass, Map<String, Object> params) {
        return null;
    }
}
