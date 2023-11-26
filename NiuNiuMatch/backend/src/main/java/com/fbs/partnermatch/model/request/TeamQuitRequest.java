package com.fbs.partnermatch.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户退出队伍
 */
@Data
public class TeamQuitRequest implements Serializable {

    private static final long serialVersionUID = -1516191111838277476L;

    /**
     * id
     */
    private Long teamId;

}
