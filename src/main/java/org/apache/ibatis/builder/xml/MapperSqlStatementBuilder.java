/**
 *    Copyright 2009-2018 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.builder.xml;

import org.apache.ibatis.builder.BaseBuilder;
import org.apache.ibatis.builder.annotation.ProviderSqlSource;
import org.apache.ibatis.mapperSql.MapperSqlEntity;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.session.Configuration;

import java.util.Map;

/**
 * @Author mingliang
 * @Date 2018-05-22 15:50
 */
public class MapperSqlStatementBuilder extends BaseBuilder {

    private final MapperSqlEntity mapperSqlEntity;
    private final String resource;
    private final String requiredDatabaseId;

    public MapperSqlStatementBuilder(Configuration configuration,String resource, MapperSqlEntity mapperSqlEntity) {
        this(configuration, resource, mapperSqlEntity, null);
    }

    public MapperSqlStatementBuilder(Configuration configuration,String resource, MapperSqlEntity mapperSqlEntity,String databaseId) {
        super(configuration);
        this.resource = resource;
        this.mapperSqlEntity = mapperSqlEntity;
        this.requiredDatabaseId = databaseId;
    }

    // 解析node,将dao接口方法放入，MappedStatement 对象
    public void parseStatementNode() {
        String nameSpace = this.mapperSqlEntity.getNamespace();
        Map<String,MapperSqlEntity.SqlEntity> methodMap = this.mapperSqlEntity.getSqlMap();
        StringBuffer id = new StringBuffer();
        for (Map.Entry entry : methodMap.entrySet()){
            SqlSource sqlSource = new ProviderSqlSource(this.configuration,null,null,null);
            SqlCommandType sqlCommandType = getSqlCommandType(((MapperSqlEntity.SqlEntity)entry.getValue()).getSqlTemplate().toString());
            id.append(nameSpace).append(".").append(entry.getKey());
            MappedStatement.Builder statementBuilder = new MappedStatement.Builder(this.configuration, id.toString(), sqlSource, sqlCommandType)
                    .resource(this.resource).mapperSqlEntity(this.mapperSqlEntity)
                    .databaseId(this.requiredDatabaseId);
            MappedStatement statement = statementBuilder.build();
            this.configuration.addMappedStatement(statement);
            id.delete(0,id.length());
        }
    }

    private SqlCommandType getSqlCommandType(String sql){
       if (sql.startsWith("SELECT") || sql.startsWith("select")){
           return SqlCommandType.SELECT;
       }else if (sql.startsWith("INSERT") || sql.startsWith("insert")){
           return SqlCommandType.INSERT;
       }else if (sql.startsWith("UPDATE") || sql.startsWith("update")){
           return SqlCommandType.UPDATE;
       }else if (sql.startsWith("DELETE") || sql.startsWith("delete")){
           return SqlCommandType.DELETE;
       }else {
           return SqlCommandType.UNKNOWN;
       }
    }
}
