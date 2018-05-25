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
import org.apache.ibatis.builder.IncompleteElementException;
import org.apache.ibatis.mapperSql.MapperSqlEntity;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.session.Configuration;

import java.util.ArrayList;
import java.util.List;

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

    // 解析node
    public void parseStatementNode() {
        String id = mapperSqlEntity.getNamespace();
        SqlSource sqlSource = null;
        SqlCommandType sqlCommandType = null;
        MappedStatement.Builder statementBuilder = new MappedStatement.Builder(configuration, id, sqlSource, sqlCommandType)
                .resource(resource)
                .databaseId("");
        MappedStatement statement = statementBuilder.build();
        configuration.addMappedStatement(statement);
    }

}
