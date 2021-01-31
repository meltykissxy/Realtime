package utils

import redis.clients.jedis.{Jedis, JedisPool, JedisPoolConfig}

object RedisUtil {
    var jedisPool: JedisPool = null

    def getJedisClient: Jedis = {
        if (jedisPool == null) {
            val (host, port) = PropertiesUtil.initRedis()

            val jedisPoolConfig = new JedisPoolConfig()
            jedisPoolConfig.setMaxTotal(10) //最大连接数
            jedisPoolConfig.setMaxIdle(4) //最大空闲
            jedisPoolConfig.setMinIdle(4) //最小空闲
            jedisPoolConfig.setBlockWhenExhausted(true) //忙碌时是否等待
            jedisPoolConfig.setMaxWaitMillis(5000) //忙碌时等待时长 毫秒
            jedisPoolConfig.setTestOnBorrow(true) //每次获得连接的进行测试

            jedisPool = new JedisPool(jedisPoolConfig, host, port.toInt)
        }
        jedisPool.getResource
    }

    /**
     * 使用
     * val jedisClient = getJedisClient
     * println(jedisClient.ping())
     * jedisClient.close()
     */
}
