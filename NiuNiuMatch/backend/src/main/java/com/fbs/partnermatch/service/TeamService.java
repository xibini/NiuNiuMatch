package com.fbs.partnermatch.service;

import com.fbs.partnermatch.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fbs.partnermatch.model.domain.User;
import com.fbs.partnermatch.model.dto.TeamQuery;
import com.fbs.partnermatch.model.request.TeamJoinRequest;
import com.fbs.partnermatch.model.request.TeamQuitRequest;
import com.fbs.partnermatch.model.request.TeamUpdateRequest;
import com.fbs.partnermatch.model.vo.TeamUserVO;

import java.util.List;

/**
* @author 123
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2023-11-14 14:38:31
*/
public interface TeamService extends IService<Team> {

    /**
     * 创建用户
     * @param team
     * @param loginUser
     * @return
     */
    long addTeam(Team team, User loginUser);

    /**
     * 搜索队伍
     *
     * @param teamQuery
     * @param isAdmin
     * @return
     */
    List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin);

    /**
     * 更新队伍
     * @param teamUpdateRequest
     * @return
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest,User loginUser);

    /**
     * 加入队伍
     * @param teamJoinRequest
     * @param loginUser
     * @return
     */
    boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);

    /**
     * 退出队伍
     * @param teamQuitRequest
     * @param loginUser
     * @return
     */
    boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser);

    /**
     * 解散队伍
     * @param id
     * @param loginUser
     * @return
     */
    boolean deleteTeam(long id,User loginUser);
}
