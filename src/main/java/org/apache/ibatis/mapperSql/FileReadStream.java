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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * 文件读取
 * @Author mingliang
 * @Date 2018-03-12 10:19
 */
public class FileReadStream {

    /**
     * 读取 .mapper 文件 先读取用户配置指定路径，如果用户没有配置，那么就读取resource下的.mapper 结尾的文件
     * @return
     * @throws FileNotFoundException
     */
    public static InputStream readMapper(File file) throws FileNotFoundException {
        if (null == file){
            new RuntimeException(String.format("文件不存在!, url = %s",file));
        }
        return  new FileInputStream(file);
    }

    public static File getResources(String path){
        if (null == path){
            new RuntimeException(String.format("没有指定文件路径！, path = %s",path));
        }
        return new File(path);
    }

    /**
     * 语法验证
     * @return
     */
    public static boolean syntaxVerification(){
        return false;
    }

}
