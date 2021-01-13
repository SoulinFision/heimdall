/*
 *    Copyright 2020-2021 Luter.me
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.luter.heimdall.sample.restful;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.net.InetAddress;

/**
 * 启动类
 *
 * @author Luter
 */
@Slf4j
@SpringBootApplication
public class RestfulRedisDemoApp {
    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        try {
            ConfigurableApplicationContext application = SpringApplication.run(RestfulRedisDemoApp.class, args);
            Environment env = application.getEnvironment();
            log.info("\n----------------------------------------------------------\n\t" +
                            "应用: '{}' 启动完毕!\n\t" +
                            "地址: \t\thttp://{}:{}\n" +
                            "----------------------------------------------------------",
                    env.getProperty("spring.application.name"),
                    InetAddress.getLocalHost().getHostAddress(),
                    env.getProperty("local.server.port"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
