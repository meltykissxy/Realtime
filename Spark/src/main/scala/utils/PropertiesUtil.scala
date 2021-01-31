package utils

import java.io.InputStreamReader
import java.util.Properties

object PropertiesUtil {
    def load(propertieName: String = "config.properties"): Properties = {
        val prop=new Properties();
        prop.load(new InputStreamReader(Thread.currentThread().getContextClassLoader.
            getResourceAsStream(propertieName) , "UTF-8"))
        prop
    }

    def initRedis(propertieName: String = "config.properties") = {
        (load().getProperty("redis.host"), load().getProperty("redis.port"))
    }

    def initHBase(propertieName: String = "config.properties") = {
        (load().getProperty("hbase.server"), load().getProperty("hbase.default.family"), load().getProperty("hbase.namespace"))
    }

    /**
     * 用法
     * val properties: Properties =  PropertiesUtil.load("config.properties")
     * println(properties.getProperty("kafka.broker.list"))
     */
}