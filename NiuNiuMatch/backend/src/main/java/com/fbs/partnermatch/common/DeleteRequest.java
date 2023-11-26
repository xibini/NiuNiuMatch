package com.fbs.partnermatch.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用的删除请求参数
 */
@Data
public class DeleteRequest implements Serializable {

    private static final long serialVersionUID = -419274570248847324L;
    private long id;
}
