package utils

import java.util
import java.util.Properties

import com.alibaba.fastjson.JSONObject
import org.apache.commons.lang3.StringUtils
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.{Cell, CellUtil, HBaseConfiguration, TableName}
import org.apache.hadoop.hbase.client.{Connection, ConnectionFactory, Get, Put, Result, ResultScanner, Scan, Table}
import org.apache.hadoop.hbase.util.Bytes

import scala.collection.mutable

object HbaseUtil {
    var connection: Connection = null
    //加载配置参数
//    private val properties: Properties = PropertiesUtil.load()
//    val HBASE_SERVER = properties.getProperty("hbase.server")
//    val DEFAULT_FAMILY = properties.getProperty("hbase.default.family")
//    val NAMESPACE = properties.getProperty("hbase.namespace")

    val (_HBASE_SERVER, _DEFAULT_FAMILY, _NAMESPACE) = PropertiesUtil.initHBase()

    def put(tableName: String, rowKey: String, columnValueMap: java.util.Map[String, AnyRef]): Unit = {
        if(connection == null) init()  //连接
        //连接 -->表
        val table: Table = connection.getTable(TableName.valueOf(_NAMESPACE + ":" + tableName) )
        //表--> put
        //把columnValueMap制作成一个 Put列表   一起交给table提交执行
        val puts: util.List[Put] = new java.util.ArrayList[Put]()
        import collection.JavaConverters._
        for (colValue <- columnValueMap.asScala ) {
            val col: String = colValue._1
            val value: AnyRef = colValue._2
            if(value != null && value.toString.length > 0){
                //一个字段一个put
                val put = new Put(Bytes.toBytes(rowKey)).addColumn(Bytes.toBytes(_DEFAULT_FAMILY),Bytes.toBytes(col),Bytes.toBytes(value.toString))
                puts.add(put)
            }
        }
        table.put(puts )
    }

    def get(tableName:String,rowKey:String): JSONObject ={
        if(connection == null) init()  //连接
        //连接 -->表
        val table: Table = connection.getTable(TableName.valueOf(_NAMESPACE + ":" + tableName) )
        //创建查询动作
        val get = new Get(Bytes.toBytes(rowKey))
        //执行查询
        val result: Result = table.get(get)
        //转换查询结果
        convertToJSONObj(result)
    }

    def convertToJSONObj(result: Result ): JSONObject ={
        val cells: Array[Cell] = result.rawCells()
        val jsonObj = new JSONObject()
        for (cell <- cells ) {
            jsonObj.put(Bytes.toString(CellUtil.cloneQualifier(cell)),Bytes.toString(CellUtil.cloneValue(cell) ))
        }
        jsonObj
    }

    def scanTable(tableName:String): mutable.Map[String, JSONObject] ={
        if(connection == null) init()  //连接
        //连接 -->表
        val table: Table = connection.getTable(TableName.valueOf(_NAMESPACE + ":" + tableName) )
        val scan = new Scan()
        val resultScanner: ResultScanner = table.getScanner(scan)
        // 把整表结果存入Map中
        val resultMap: mutable.Map[String, JSONObject] = mutable.Map[String,JSONObject]()
        import collection.JavaConverters._
        for (result <- resultScanner.iterator().asScala ) {
            val rowkey: String = Bytes.toString(result.getRow )
            val jsonObj: JSONObject = convertToJSONObj(result)
            resultMap.put(rowkey,jsonObj)
        }
        resultMap
    }

    def getDimRowkey(id: String): String = {
        // 数据存入HBase时就是这么设计的rowKey，所以读取时也要同样的处理
        // 左边补齐到10位数，然后反转
        StringUtils.leftPad(id, 10, "0").reverse
    }
    // 初始化连接
    def init() = {
        val configuration: Configuration = HBaseConfiguration.create()
        configuration.set("hbase.zookeeper.quorum", _HBASE_SERVER)
        connection=ConnectionFactory.createConnection(configuration)
    }
}
