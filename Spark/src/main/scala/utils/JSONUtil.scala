package utils

import com.alibaba.fastjson.{JSON, JSONObject}

import java.util

object JSONUtil {
    def main(args: Array[String]): Unit = {
        val json = "{\"name\": \"ly\", \"age\":18}"
        val value = JSON.parse(json).asInstanceOf[JSONObject]
        val map = value.getInnerMap
        println(map.get("name"))
        println(value.get("name"))
        println(value.get("age"))

        val strings = new util.ArrayList[String]()

        val _null = null
        _null.toString
    }
}
