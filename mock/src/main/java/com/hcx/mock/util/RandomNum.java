package com.hcx.mock.util;

import java.util.Random;

/**
 * @ClassName RandomNum
 * @Description TODO
 * @Author 贺楚翔
 * @Date 2019-11-06 19:22
 * @Version 1.0
 **/
public class RandomNum {
    public static final  int getRandInt(int fromNum,int toNum){
        return   fromNum+ new Random().nextInt(toNum-fromNum+1);
    }
}
