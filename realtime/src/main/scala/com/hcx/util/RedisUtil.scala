package com.hcx.util

import redis.clients.jedis.{Jedis, JedisPool, JedisPoolConfig}
/**
 * @Author HCX
 * @Description //TODO  获取redis连接
 * @Date 9:55 2019-11-12
 *
 * @return
 * @exception
 **/

object RedisUtil {

  var jedisPool:JedisPool = null

  def getJedisClient:Jedis = {
    if (jedisPool == null){
      val config = PropertiesUtil.load("config.properties")
      val host = config.getProperty("redis.host")
      val port = config.getProperty("redis.port")

      val jedisPoolConfig = new JedisPoolConfig()
      jedisPoolConfig.setMaxTotal(100)  //最大连接数
      jedisPoolConfig.setMaxIdle(20)    //最大空闲
      jedisPoolConfig.setMinIdle(20)    //最小空闲
      jedisPoolConfig.setBlockWhenExhausted(true)   //设置忙碌是是否等待
      jedisPoolConfig.setMaxWaitMillis(500)   //忙碌时等待时长
      jedisPoolConfig.setTestOnBorrow(true)   //每次获得连接的时候进行测试


      jedisPool = new JedisPool(jedisPoolConfig,host,port.toInt)
    }

    jedisPool.getResource
  }

  def main(args: Array[String]): Unit = {
    val client: Jedis = getJedisClient
    client.set("hcx","123")
    val str: String = client.get("hcx")
    println(str)

  }

}
