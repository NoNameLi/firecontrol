/*
 *  Copyright (C) 2018  Wanghaobin<463540703@qq.com>

 *  AG-Enterprise 企业版源码
 *  郑重声明:
 *  如果你从其他途径获取到，请告知老A传播人，奖励1000。
 *  老A将追究授予人和传播人的法律责任!

 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.

 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.

 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package cn.turing.firecontrol.common.msg;

/**
 * Created by ace on 2017/8/23.
 */
public class BaseResponse {
    private int status = 200;
    private String message;

    //构造函数
    public BaseResponse() {
    }

    //构造函数
    public BaseResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    /**
     * 请求成功
     * @return
     */
    public static BaseResponse  success(){
        return  new BaseResponse(200,"success");
    }
    /**
     * 请求失败
     * @return
     */
    public static BaseResponse  failure(){
        return  new BaseResponse(300,"failure");
    }
    /**
     * 请求失败
     * @return
     */
    public static BaseResponse  failure(String msg){
        return  new BaseResponse(300,msg);
    }



    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


}
