package com.hcx.bean
/**
 * @Author HCX
 * @Description //TODO startuplog样例类
 * @Date 17:33 2019-11-25
 *
 * @return
 * @exception
 **/

case class Startuplog(mid:String,
                      uid:String,
                      appid:String,
                      area:String,
                      os:String,
                      ch:String,
                      logType:String,
                      vs:String,
                      var logDate:String,
                      var logHour:String,
                      var logHourMinute:String,
                      var ts:Long)
