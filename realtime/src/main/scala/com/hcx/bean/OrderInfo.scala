package com.hcx.bean

/**
 * @Author HCX
 * @Description //TODO  orderInfo样例类
 * @Date 17:32 2019-11-25
 *
 * @return
 * @exception
 **/

case class OrderInfo(area: String,
                     consignee: String,
                     orderComment: String,
                     var consigneeTel: String,
                     operateTime: String,
                     orderStatus: String,
                     paymentWay: String,
                     userId: String,
                     imgUrl: String,
                     totalAmount: Double,
                     expireTime: String,
                     deliveryAddress: String,
                     createTime: String,
                     trackingNo: String,
                     parentOrderId: String,
                     outTradeNo: String,
                     id: String,
                     tradeBody: String,
                     var createDate: String,
                     var createHour: String,
                     var createHourMinute: String)
