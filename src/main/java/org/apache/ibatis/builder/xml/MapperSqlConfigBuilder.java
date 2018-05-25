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

import org.apache.ibatis.builder.*;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapperSql.MapperSqlEntity;
import org.apache.ibatis.mapperSql.MapperSqlHelper;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.session.Configuration;
import java.util.*;

/**
 *
 * @Author mingliang
 * @Date 2018-04-11 16:37
 */
public class MapperSqlConfigBuilder extends BaseBuilder {

    private final String resource;
    private List<MapperSqlEntity> mapperSqlEntities;

    public MapperSqlConfigBuilder(Configuration configuration, String resource) {
        super(configuration);
        this.resource = resource;
    }

    public void parse() {
        // 没有加载的情况下先加载
        if (!configuration.isResourceLoaded(resource)) {
            configurationElement(resource);
            configuration.addLoadedResource(resource);
            bindMapper();
        }
        parsePendingStatements();
    }

    private void configurationElement(String resource) {
        try {
            // 读取文件，
            this.mapperSqlEntities = MapperSqlHelper.readMapper(resource);
            buildStatementFromContext(mapperSqlEntities);
        } catch (Exception e) {
            throw new BuilderException("Error parsing Mapper XML. The XML location is '" + resource + "'. Cause: " + e, e);
        }
    }

    private void parsePendingStatements() {
        Collection<MapperSqlStatementBuilder> incompleteStatements = configuration.getIncompleteMapperSqlStatements();
        synchronized (incompleteStatements) {
            Iterator<MapperSqlStatementBuilder> iter = incompleteStatements.iterator();
            while (iter.hasNext()) {
                try {
                    iter.next().parseStatementNode();
                    iter.remove();
                } catch (IncompleteElementException e) {
                    // Statement is still missing a resource...
                }
            }
        }
    }

    private void bindMapper() {
        String namespace = null;
        if (mapperSqlEntities.size() <= 0){
            namespace = mapperSqlEntities.get(0).getNamespace();
            addMapper(namespace);
        }else {
            for (MapperSqlEntity mapperSqlEntity : mapperSqlEntities){
                namespace = mapperSqlEntity.getNamespace();
                addMapper(namespace);
            }
        }
    }

    private void addMapper(String namespace){
        if (null != namespace) {
            Class<?> boundType = null;
            try {
                boundType = Resources.classForName(namespace);
            } catch (ClassNotFoundException e) {
                //ignore, bound type is not required
            }
            if (boundType != null) {
                if (!configuration.hasMapper(boundType)) {
                    configuration.addLoadedResource("namespace:" + namespace);
                    configuration.addMapper(boundType);
                }
            }
        }
    }

    // 构建MappedStatement
    private void buildStatementFromContext(List<MapperSqlEntity> mapperSqlEntities) {
        if (configuration.getDatabaseId() != null) {
            buildStatementFromContext(mapperSqlEntities, configuration.getDatabaseId());
        }
        buildStatementFromContext(mapperSqlEntities, null);
    }

    private void buildStatementFromContext(List<MapperSqlEntity> mapperSqlEntities, String requiredDatabaseId) {
        for (MapperSqlEntity mapperSqlEntity : mapperSqlEntities) {
            final MapperSqlStatementBuilder statementBuilder = new MapperSqlStatementBuilder(configuration,"",mapperSqlEntity,requiredDatabaseId);
            try {
                statementBuilder.parseStatementNode();
            } catch (IncompleteElementException e) {
                // ingor this exception
            }
        }
    }
}
