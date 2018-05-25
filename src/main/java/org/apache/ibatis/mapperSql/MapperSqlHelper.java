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

import org.apache.ibatis.builder.BuilderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author mingliang
 * @Date 2018-04-11 17:11
 */
public class MapperSqlHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapperSqlHelper.class);
    private static String classPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();

    /**
     * <p>
     *      如果是多个文件一次读取，以namespace作为文件结束和开始的标识，每一mapperSql文件解析为一个MapperSqlEntity对象
     *  </p>
     * @param mapperUrl
     * @return
     */
    public static List<MapperSqlEntity> readMapper(String mapperUrl){
        List<MapperSqlEntity> mapperSqlEntityList = null;
        if (null == mapperUrl){
            new RuntimeException(String.format("*.mapper文件不存在!, url = %s",mapperUrl));
        }
        if (mapperUrl.indexOf("*") != -1){
            File file = FileReadStream.getResources(classPath+mapperUrl.split("\\*")[0]);
            File[] fileSources = file.listFiles();
            for (int i = 0; i < fileSources.length; i++) {
                if (fileSources[i].isFile() && fileSources[i].getName().indexOf(".sql") != -1) {
                    //读取某个文件夹下的所有文件
                    MapperSqlEntity mapperSqlEntity = readMapperFile(fileSources[i]);
                    if (null != mapperSqlEntity){
                        mapperSqlEntityList.add(mapperSqlEntity);
                    }
                }
            }
        }else {
             MapperSqlEntity mapperSqlEntity = readMapperFile(new File(classPath+mapperUrl));
             if (null != mapperSqlEntity){
                 mapperSqlEntityList.add(mapperSqlEntity);
             }
        }
        return mapperSqlEntityList;
    }

    private static MapperSqlEntity readMapperFile(File file){
        //读取某个文件夹下的所有文件
        try {
            InputStream inputStream = FileReadStream.readMapper(file);
            byte[] stream = new byte[1024];
            inputStream.read(stream);
            // 解析读取的字符串
            return analysisStringReader(new String(stream));
        } catch (FileNotFoundException e) {
            LOGGER.error("file not found ",e);
        } catch (IOException e) {
            LOGGER.error("read file exception ",e);
        }
        return null;
    }

    /**
     * 文件内容分四部分
     *
     *  namespacec 接口映射，dao或者mapper
     *
     *  import 改接口需要使用的变量类
     *
     *  method 操作数据库的dao方法
     *
     *  sql 具体sql
     *
     *  <p>
     *      把每个文件解析成一个 MapperSqlEntity 对象
     *  </p>
     *
     * @param reader
     */
    private static MapperSqlEntity analysisStringReader(String reader){
        String[] readers = reader.split(";");
        if (readers.length == 0){
            throw new RuntimeException("文件语法错误，文件中每一行语句必须使用 ; 分割");
        }
        MapperSqlEntity mapperSqlEntity = null;
        // 解析文件头，文件头必须是import引入相关变量，若果没有import就解析dao接口，然后再解析接口的调用方法，然后在解析对应的sql
        int lineCount = 0;
        Map<String,String> importParamMap = new HashMap<>();
        Map<String,String> sqlMap = new HashMap<>();
        for (String str : readers){
            // 文件头
            if (lineCount == 0 && str.indexOf("namespace") == -1) {
                throw new BuilderException("Mapper's import cannot be empty");
            }

            if (str.indexOf("namespace") != -1){
                mapperSqlEntity = new MapperSqlEntity();
                mapperSqlEntity.setNamespace(str.trim().split(" ")[1].trim());
            }

            // 解析import部分
            if (str.indexOf("import") != -1){
                String[] importStrs = str.trim().split(" ");
                if (importStrs.length < 3){
                    throw new BuilderException("this import Incomplete, Please complete the "+ str);
                }
                importParamMap.put(importStrs[1],importStrs[2]);
            }

            // 解析方法名称
            if (str.indexOf("method") != -1){
                String[] methodStr = str.trim().split(" ");
                if (methodStr.length < 3){
                    throw new BuilderException("this method Incomplete, Please complete the "+ str);
                }
                sqlMap.put(methodStr[1],methodStr[2]);
            }
        }
        if (null != mapperSqlEntity){
            mapperSqlEntity.setImportParamMap(importParamMap);
            mapperSqlEntity.setSqlMap(sqlMap);
        }
        return mapperSqlEntity;
    }
}
