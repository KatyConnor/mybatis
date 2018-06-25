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
package org.apache.ibatis.exceptions;

/**
 * @Author mingliang
 * @Date 2018-06-01 10:20
 */
public class MapperSqlException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public MapperSqlException() {
        super();
    }

    public MapperSqlException(String message) {
        super(message);
    }

    public MapperSqlException(String message, Throwable cause) {
        super(message, cause);
    }

    public MapperSqlException(Throwable cause) {
        super(cause);
    }
}
