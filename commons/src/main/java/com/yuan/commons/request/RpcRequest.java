package com.yuan.commons.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author by yuanlai
 * @Date 2020/7/16 3:27 下午
 * @Description: TODO
 * @Version 1.0
 */

@Data
public class RpcRequest implements Serializable {
    private static final long serialVersionUID = 2944790584022635557L;
    private String name;
    private String method;
}