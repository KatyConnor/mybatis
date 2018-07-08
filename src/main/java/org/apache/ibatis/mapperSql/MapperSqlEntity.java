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
package org.apache.ibatis.mapperSql;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.asm.ArgNameMethod;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.exceptions.MapperSqlException;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 解析mapper sql 文件核心类
 * @Author mingliang
 * @Date 2018-04-11 17:11
 */
public class MapperSqlEntity {

    /** 接口映射的接口名称 */
    private String namespace;
    /** 引入的变量 key --> variable name, value --> import class*/
    private Map<String,String> importParamMap;
    /** 方法和sql key --> method, value -- > sql*/
    private Map<String,SqlEntity> sqlMap;
    /** 基础列 key --> name, value --> column*/
    private Map<String,String> baseColumnMap;

    // 初始化参数
    public MapperSqlEntity(String namespace) {
        this.namespace = namespace;
        this.sqlMap = new HashMap<>();
        this.baseColumnMap = new HashMap<>();
        this.importParamMap = new HashMap<>();
    }

    public String getNamespace() {
        return namespace;
    }

    public Map<String, SqlEntity> getSqlMap() {
        return sqlMap;
    }

    public Map<String, String> getImportParamMap() {
        return importParamMap;
    }

    public void setSqlMap(Map<String, SqlEntity> sqlMap) {
        this.sqlMap = sqlMap;
    }

    public Map<String, String> getBaseColumnMap() {
        return baseColumnMap;
    }

    public void setBaseColumnMap(Map<String, String> baseColumnMap) {
        this.baseColumnMap = baseColumnMap;
    }

    public void build(String read){
        // 解析import部分,数据库表映射实体
        if (read.indexOf("import") != -1){
            String[] importStrs = read.trim().split(" ");
            if (importStrs.length < 3){
                throw new MapperSqlException("this import Incomplete, Please complete the "+ read);
            }
            importParamMap.put(importStrs[2],importStrs[1]);
        }

        // base column
        if (read.indexOf("column") != -1){
            String[] columns = read.trim().split(" ");
            if (columns.length < 3){
                throw new MapperSqlException("this column Incomplete, Please complete the "+ read);
            }
            baseColumnMap.put(columns[1],columns[2].replace("(","").replace(")",""));
        }

        // 解析方法名称
        if (read.indexOf("method") != -1){
            String[] methodStr = read.trim().split(":");
            if (methodStr.length != 2){
                throw new MapperSqlException("this method Incomplete, Please complete the "+ read);
            }

            if (methodStr[0].split(" ").length != 2){
                throw new BuilderException("this method sql grammar error, Please complete the [ "+ read+" ]");
            }
            String methodName = methodStr[0].split(" ")[1];
            SqlEntity sqlEntity = new SqlEntity(methodName);
            sqlEntity.build(methodStr[1]);
            // key: 方法名, value: sql
            sqlMap.put(methodName,sqlEntity);
        }
    }

    public class SqlEntity{

        /** 方法名 */
        private String methodName;
        /** sql 模板 */
        private StringBuilder sqlTemplate;
        /** 方法返回类型 */
        private Class<?> returnType;
        /** 数据库表映射的实体 */
        Map<String,Class<?>> tableDTO;
        /** 方法参数 */
        private Map<String,Class<?>>paramMap;

        public SqlEntity(String methodName) {
            this.methodName = methodName;
            this.tableDTO = new HashMap<>();
        }

        public StringBuilder getSqlTemplate() {
            return sqlTemplate;
        }

        public Class<?> getReturnType() {
            return returnType;
        }

        public String getMethodName() {
            return methodName;
        }

        public Map<String, Class<?>> getParamMap() {
            return paramMap;
        }

        public Map<String, Class<?>> getTableDTO() {
            return tableDTO;
        }

        public void build(String sql){
            this.sqlTemplate = new StringBuilder(sql);
            paramMap.forEach((k,v) ->{
                int index = sqlTemplate.indexOf(" "+k+" ");
                if (index != -1){
                    sqlTemplate.replace(index+1,index+k.length()+1,String.format("${%s}",k));
                }
            });

            paramAndReturnType();
            analysisTableDTO();
            setBaseColumns();
        }

        private void paramAndReturnType(){
            try {
                Class<?> classzz = Class.forName(namespace);
                Method[] methods = classzz.getDeclaredMethods();
                Method method = null;
                for (Method m : methods){
                    if (this.methodName.equals(m.getName())){
                        method = m;
                        break;
                    }
                }
                this.returnType = method.getReturnType();
                Parameter[] parameter = method.getParameters();
                parameter[0].getName();
                this.paramMap = new HashMap<>();
                // 采用asm api 获取方法原生的参数名
                List<String> params = ArgNameMethod.methodArgNames(method);
                for (int i = 0; i < parameter.length; i++) {
                    Param param = parameter[i].getAnnotation(Param.class);
                    if (null != param){
                        this.paramMap.put(param.value(),parameter[i].getType());
                    }else {
                        // 没有使用注解时
                        this.paramMap.put(params.get(i),parameter[i].getType());
                    }
                }
            } catch (ClassNotFoundException e) {
                throw new MapperSqlException(String.format("mapper class not found, class = [%s], please check it right!,Exception = ",namespace),e);
            }
        }

        /**
         *  设置当前sql 查询的表
         */
        private void analysisTableDTO(){
            importParamMap.forEach((k,v) ->{
                if (this.sqlTemplate.indexOf(k) != -1){
                    try {
                        tableDTO.put(k,Class.forName(v));
                    } catch (ClassNotFoundException e) {
                        throw new MapperSqlException(String.format("mapper DTO class not found, class = [%s], please check it right!,Exception = ",v),e);
                    }
                }
            });
        }

        /**
         * 设置baseColumns
         */
        private void setBaseColumns(){
            StringBuilder baseColums = new StringBuilder();
            baseColumnMap.forEach((k,v) ->{
                baseColums.append(" ").append(k).append(" ");
                int index = this.sqlTemplate.indexOf(k);
                if ( index != -1){
                    this.sqlTemplate.replace(index+1,index+baseColums.length()-1,v);
                }
            });
        }
    }
}
