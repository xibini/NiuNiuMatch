package com.fbs.partnermatch.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fbs.partnermatch.contant.UserConstant;
import com.fbs.partnermatch.model.request.TeamJoinRequest;
import com.fbs.partnermatch.model.request.TeamQuitRequest;
import com.fbs.partnermatch.model.request.TeamUpdateRequest;
import org.apache.commons.collections4.CollectionUtils;
import com.fbs.partnermatch.common.ErrorCode;
import com.fbs.partnermatch.exception.BusinessException;
import com.fbs.partnermatch.model.domain.Team;
import com.fbs.partnermatch.model.domain.User;
import com.fbs.partnermatch.mapper.TeamMapper;
import com.fbs.partnermatch.model.domain.UserTeam;
import com.fbs.partnermatch.model.dto.TeamQuery;
import com.fbs.partnermatch.model.enums.TeamStatusEnum;
import com.fbs.partnermatch.model.vo.TeamUserVO;
import com.fbs.partnermatch.model.vo.UserVO;
import com.fbs.partnermatch.service.TeamService;
import com.fbs.partnermatch.service.UserService;
import com.fbs.partnermatch.service.UserTeamService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
* @author 123
* @description 针对表【team(队伍)】的数据库操作Service实现
* @createDate 2023-11-14 14:38:31
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService {

    @Resource
    private UserService userService;

    @Resource
    private UserTeamService userTeamService;

    @Override
    @Transactional(rollbackFor = Exception.class) //开启全局事务
    public long addTeam(Team team, User loginUser) {
        //1、请求参数是否为空
        if(team==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数为空");
        }
        //2、是否登录，未登录不允许创建
        if(loginUser==null){
            throw new BusinessException(ErrorCode.NOT_LOGIN,"未登录");
        }
        //3、校验信息
        //  1、队伍人数>1且<=20
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);//如果为空则置为0
        if(maxNum<1 || maxNum>20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍人数不满足要求");
        }
        //  2、队伍标题<=20
        String name = team.getName();
        if(StringUtils.isBlank(name)||name.length()>20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍标题不满足要求");
        }
        //  3、描述<=512
        String description = team.getDescription();
        if(StringUtils.isNotBlank(description) && description.length()>512){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍描述过长");
        }
        //  4、status是否公开
        int status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        if(statusEnum==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍状态不满足要求");
        }
        //  5、如果status是加密状态，一定要有密码，且密码<=32
        String password = team.getPassword();
        if(TeamStatusEnum.SECRET.equals(statusEnum)){
            if((StringUtils.isBlank(password)||password.length()>32)){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码格式不正确");
            }
        }
        //  6、超时时间>当前时间
        Date expireTime = team.getExpireTime();
        if(new Date().after(expireTime)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍已超时");
        }
        //  7、校验用户最多创建5个队伍
        // todo 可能同时创建多个队伍
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        final long userId = loginUser.getId();
        queryWrapper.eq("userId",userId);
        long hasTeamNum = this.count(queryWrapper);
        if(hasTeamNum>=5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户最多创建5个队伍");
        }

        //4、插入队伍信息到队伍表
        team.setId(null);
        team.setUserId(userId);
        boolean result = this.save(team);
        Long teamId = team.getId();
        if(!result || teamId==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"创建队伍失败");
        }

        //5、插入用户 => 队伍关系到关系表
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        result = userTeamService.save(userTeam);
        if(!result){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"创建队伍失败");
        }
        return teamId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin) {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        //组合查询条件
        if(teamQuery!=null){
            Long id = teamQuery.getId();
            if(id!=null && id>0){
                queryWrapper.eq("id",id);
            }
            List<Long> idList = teamQuery.getIdList();
            if(CollectionUtils.isNotEmpty(idList)){
                queryWrapper.in("id",idList);
            }
            String searchText = teamQuery.getSearchText();
            if(StringUtils.isNotBlank(searchText)){
                queryWrapper.and(qw->qw.like("name",searchText).or().like("description",searchText));
            }
            String name = teamQuery.getName();
            if(StringUtils.isNotBlank(name)){
                queryWrapper.like("name",name);
            }
            String description = teamQuery.getDescription();
            if(StringUtils.isNotBlank(description)){
                queryWrapper.like("description",description);
            }
            //查询最大人数相等的
            Integer maxNum = teamQuery.getMaxNum();
            if(maxNum!=null && maxNum>0){
                queryWrapper.eq("maxNum",maxNum);
            }
            //根据创建人查询
            Long userId = teamQuery.getUserId();
            if(userId!=null && userId>0){
                queryWrapper.eq("userId",userId);
            }
            //根据状态查询
            Integer status = teamQuery.getStatus();
            TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
            if(statusEnum==null){
                statusEnum = TeamStatusEnum.PUBLIC;
            }
            if(!isAdmin && statusEnum.equals(TeamStatusEnum.PRIVATE)){
                throw new BusinessException(ErrorCode.NO_AUTH);
            }
            queryWrapper.eq("status",statusEnum.getValue());
        }
        //不展示已过期队伍
        //过期时间为空 or 过期时间大于当前时间
        //
        queryWrapper.and(qw->qw.gt("expireTime", new Date()).or().isNull("expireTime"));

        //查询
        List<Team> teamList = this.list(queryWrapper);

        //查询到的队伍为空
        if(CollectionUtils.isEmpty(teamList)){
            return new ArrayList<>();
        }

        List<TeamUserVO> teamUserVOList = new ArrayList<>();
        //关联查询创建人信息
        for (Team team : teamList) {
            Long userId = team.getUserId();
            if(userId==null){
                continue;
            }
            User user = userService.getById(userId);
            User safetyUser = userService.getSafetyUser(user);
            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtils.copyProperties(team,teamUserVO);
            //对用户信息脱敏
            UserVO userVO = new UserVO();
            if(user!=null){
                BeanUtils.copyProperties(safetyUser,userVO);
                teamUserVO.setCreateUser(userVO);
                teamUserVOList.add(teamUserVO);
            }
        }
        return teamUserVOList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest,User loginUser){
        if(teamUpdateRequest==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = teamUpdateRequest.getId();
        if(id==null || id<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team oldTeam = this.getById(id);
        if(oldTeam==null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"队伍不存在");
        }
        //只有管理员和队伍创建者可以修改
        if(oldTeam.getUserId()!=loginUser.getId() && !userService.isAdmin(loginUser)){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        TeamStatusEnum stateEnum = TeamStatusEnum.getEnumByValue(teamUpdateRequest.getStatus());
        if(stateEnum.equals(TeamStatusEnum.SECRET)){
            if(StringUtils.isBlank(teamUpdateRequest.getPassword())){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"加密房间必须设置密码");
            }
        }
        //更新
        Team updateTeam = new Team();
        BeanUtils.copyProperties(teamUpdateRequest,updateTeam);
        return this.updateById(updateTeam);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser) {
        if(teamJoinRequest==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //校验传入的队伍id
        Long teamId = teamJoinRequest.getTeamId();
        Team team = this.getTeamById(teamId);
        //校验过期时间
        Date expireTime = team.getExpireTime();
        if(expireTime !=null && expireTime.before(new Date())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍已过期");
        }
        //校验队伍状态
        Integer status = team.getStatus();
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValue(status);
        if(TeamStatusEnum.PRIVATE.equals(teamStatusEnum)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"禁止加入私有队伍");
        }
        //如果是加密队伍校验密码
        String password = teamJoinRequest.getPassword();
        if(TeamStatusEnum.SECRET.equals(teamStatusEnum)){
            if(StringUtils.isBlank(password) || !password.equals(team.getPassword())){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码错误");
            }
        }

        //加锁
        //todo : 分布式锁、不同用户/不同队伍的锁分开
        synchronized (this) {
            //查询改用户加入了多少队伍
            long userId = loginUser.getId();
            QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userId",userId);
            long hasJoinNum = userTeamService.count(queryWrapper);
            if(hasJoinNum>=5){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"最多加入5个队伍");
            }
            //查询已加入该队伍的人数
            long teamHasJoinNum = this.countTeamUserByTeamId(teamId);
            if(teamHasJoinNum>=team.getMaxNum()){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍已满");
            }
            //不能重复加入已加入的队伍
            queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userId",userId);
            queryWrapper.eq("teamId",teamId);
            long hasUserJoinTeam = userTeamService.count(queryWrapper);
            if(hasUserJoinTeam>0){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"已加入该队伍");
            }

            //修改队伍信息
            UserTeam userTeam = new UserTeam();
            userTeam.setUserId(userId);
            userTeam.setTeamId(teamId);
            userTeam.setJoinTime(new Date());
            return userTeamService.save(userTeam);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser) {
        if(teamQuitRequest==null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        Long teamId = teamQuitRequest.getTeamId();
        Team team = getTeamById(teamId);
        long userId = loginUser.getId();
        UserTeam queryUserTeam = new UserTeam();
        queryUserTeam.setTeamId(teamId);
        queryUserTeam.setUserId(userId);
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>(queryUserTeam);
        long count = userTeamService.count(queryWrapper);
        if(count==0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"未加入该队伍");
        }
        long teamHasJoinNum = this.countTeamUserByTeamId(teamId);
        //队伍只剩一人
        if(teamHasJoinNum==1){
            //删除队伍
            this.removeById(teamId);
        }else{
            //队伍还剩至少两人
            // 是否为队长
            if(team.getUserId()==userId){
                //把队伍转移给最早加入的用户
                //1、查询已加入队伍的所有用户和加入时间
                QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
                userTeamQueryWrapper.eq("teamId",teamId);
                userTeamQueryWrapper.last("order by id asc limit 2");
                List<UserTeam> userTeamlist = userTeamService.list(userTeamQueryWrapper);
                if(CollectionUtils.isEmpty(userTeamlist) || userTeamlist.size()<=1){
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                }
                UserTeam nextuserTeam = userTeamlist.get(1);
                Long nextTeamLeaderId = nextuserTeam.getUserId();
                Team updateTeam = new Team();
                updateTeam.setId(teamId);
                updateTeam.setUserId(nextTeamLeaderId);
                boolean result = this.updateById(updateTeam);
                if(!result){
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR,"更新队长失败");
                }
            }
        }
        //移除用户-队伍关系
        return userTeamService.remove(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTeam(long id,User loginUser) {
        //校验队伍是否存在
        Team team = this.getTeamById(id);
        long teamId = team.getId();
        //校验是否为队长
        if(team.getUserId()!=loginUser.getId() && loginUser.getUserRole()!= UserConstant.ADMIN_ROLE){
            throw new BusinessException(ErrorCode.NO_AUTH,"无访问权限");
        }
        //移除队伍用户关联信息
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("teamId", teamId);
        boolean result = userTeamService.remove(userTeamQueryWrapper);
        if(!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"删除队伍关联信息失败");
        }
        //删除队伍
        return this.removeById(teamId);
    }


    /*
    获取某队伍当前人数
     */
    private long countTeamUserByTeamId(long teamId){
        //查询已加入该队伍的人数
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId",teamId);
        return userTeamService.count(queryWrapper);
    }

    /**
     * 根据id获取队伍信息
     * @param teamId
     * @return
     */
    private Team getTeamById(Long teamId) {
        if(teamId ==null || teamId <=0){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        Team team = this.getById(teamId);
        if(team==null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"队伍不存在");
        }
        return team;
    }
}




