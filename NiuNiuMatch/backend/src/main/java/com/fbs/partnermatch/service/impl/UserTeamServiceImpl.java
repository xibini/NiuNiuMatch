package com.fbs.partnermatch.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fbs.partnermatch.model.domain.UserTeam;
import com.fbs.partnermatch.service.UserTeamService;
import com.fbs.partnermatch.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

/**
* @author 123
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2023-11-14 14:39:29
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




