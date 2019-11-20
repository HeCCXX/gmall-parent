package com.hcx

import java.util
import java.util.Objects

import io.searchbox.client.config.HttpClientConfig
import io.searchbox.client.{JestClient, JestClientFactory}
import io.searchbox.core.{Bulk, BulkResult, Index}

object MyEsUtil {

  private val ES_HOST = "http://hadoop1"
  private val ES_HTTP_PORT = 9200
  private var factory:JestClientFactory = null
  /**
 * @Author HCX
 * @Description //TODO 获取客户端
 * @Date 9:23 2019-11-14
 *
 * @return _root_.io.searchbox.client.JestClient
 * @exception
 **/

  def getClient:JestClient = {
    if (factory == null) build()
    factory.getObject
  }
  /**
 * @Author HCX
 * @Description //TODO 关闭客户端
 * @Date 9:23 2019-11-14
 * [client]
 * @return Unit
 * @exception
 **/

  def close(client: JestClient): Unit ={
    if (!Objects.isNull(client)) try
      client.shutdownClient()
    catch {
      case e: Exception =>
        e.printStackTrace()
    }
  }

  /**
 * @Author HCX
 * @Description //TODO 建立连接
 * @Date 9:27 2019-11-14
 *
 * @return Unit
 * @exception
 **/

  private def build(): Unit ={
    factory = new JestClientFactory
    factory.setHttpClientConfig(new HttpClientConfig.Builder(ES_HOST+":"+ES_HTTP_PORT).multiThreaded(true).maxTotalConnection(20)
    .connTimeout(10000).readTimeout(10000).build())
  }

  /**
 * @Author HCX
 * @Description //TODO 批量插入es
 * @Date 9:37 2019-11-14
 * [indexname, list]
 * @return Unit
 * @exception
 **/

  def indexBulk(indexname:String,list: List[Any]): Unit ={
    val jest:JestClient = getClient
    val bulkBuilder = new Bulk.Builder().defaultIndex(indexname).defaultType("_doc")
    for (doc <- list){
      val index: Index = new Index.Builder(doc).build()
      bulkBuilder.addAction(index)
    }
    val items: util.List[BulkResult#BulkResultItem] = jest.execute(bulkBuilder.build()).getItems
    println(s"保存 = ${items.size()}")
    close(jest)
  }

  def main(args: Array[String]): Unit = {
    val jest = getClient
    val source = "{\n  \"name\":\"hcx222\",\n  \"age\":132222\n}"
    val index: Index = new Index.Builder(source).index("gmall").`type`("_doc").build()
    jest.execute(index)
    close(jest)
  }

}
