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
package org.apache.ibatis.velocity;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author mingliang
 * @Date 2018-01-30 17:04
 */
public class VelocityFactory{

    private static VelocityContext context = new VelocityContext();
    private static VelocityEngine velocityEngine = new VelocityEngine();

    static {
        initVelocity();
    }

    private static void initVelocity(){
        // 初始化velocity
        velocityEngine.init();
        // 添加函数实体
        context.put("v", VelocityUtils.class);

    }


    /**
     * 模板渲染解析，返回解析之后的值
     * @param temp
     * @param values
     * @return
     * @throws Exception
     */
    public static String evaluate(String temp, Map<String,Object> values){
        convertVelocityContext(values);
        StringWriter sw = new StringWriter();
        velocityEngine.evaluate( context, sw,"",temp );
        return sw.toString();
    }


    /**
     * <pre>
     *   把Map转换成Context，将所有的值存放在 context中
     * </pre>
     * @param map 参数值
     */
    private static void convertVelocityContext(Map<String, Object> map) {
        if (map == null) {
            return;
        }
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            context.put(entry.getKey(), entry.getValue());
        }
    }

    public static void main(String[] args) {
        String temp = "this is a velocity test , $!{name} is ok ";
        Map<String,Object> valueMap = new HashMap<>();
        valueMap.put("name","testName");
        String result = evaluate(temp,valueMap);
        System.out.println(result);
    }

}
