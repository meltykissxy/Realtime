package utils

import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

object MykafkaSink {
    private val properties: Properties = PropertiesUtil.load("config.properties")
    val broker_list = properties.getProperty("kafka.broker.list")
    var kafkaProducer: KafkaProducer[String, String] = null

    def createKafkaProducer: KafkaProducer[String, String] = {
        val properties = new Properties
        properties.put("bootstrap.servers", broker_list)
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
        properties.put("enable.idompotence",(true: java.lang.Boolean))
        var producer: KafkaProducer[String, String] = null
        try
            producer = new KafkaProducer[String, String](properties)
        catch {
            case e: Exception =>
                e.printStackTrace()
        }
        producer
    }

    // 轮询分区 2.x  黏性分区3.x
    def send(topic: String, msg: String): Unit = {
        if (kafkaProducer == null) kafkaProducer = createKafkaProducer
        kafkaProducer.send(new ProducerRecord[String, String](topic, msg))
    }

    // 根据 分区键分区
    def send(topic: String,key:String, msg: String): Unit = {
        if (kafkaProducer == null) kafkaProducer = createKafkaProducer
        kafkaProducer.send(new ProducerRecord[String, String](topic,key, msg))
    }

    /**
     * 不关闭，会有数据丢失的问题
     * send线程是守护线程，意味着main线程不会在乎send线程是否还有未完成的工作，main线程一旦关闭，作为守护线程的send也会关闭
     * 这时，在RecordAccumulator中的数据就丢失了
     *
     * 调用close()主动关闭，会强制send线程去处理掉剩下的数据
     */
    def close(): Unit ={
        kafkaProducer.close()
    }
}
