package com.hcx.util

import java.io.InputStreamReader
import java.util.Properties

/**
 * @Author HCX
 * @Description //TODO properties工具类，读取配置文件
 * @Date 17:33 2019-11-25
 *
 * @return
 * @exception
 **/

object PropertiesUtil {

  def load(propertiesName:String):Properties ={
    val prop = new Properties()
    prop.load(new InputStreamReader(Thread.currentThread().getContextClassLoader.getResourceAsStream(propertiesName),"UTF-8"))
    prop
  }

}
