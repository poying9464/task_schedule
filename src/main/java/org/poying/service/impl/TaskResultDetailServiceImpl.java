package org.poying.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.poying.e.TaskResultDetail;
import org.poying.mapper.TaskResultDetailMapper;
import org.poying.service.TaskResultDetailService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskResultDetailServiceImpl extends ServiceImpl<TaskResultDetailMapper, TaskResultDetail> implements TaskResultDetailService {

    /**
     * 根据groupName和jobName查询TaskResultDetail
     *
     * @param groupName 任务组名
     * @param jobName   任务名
     * @return TaskResultDetail对象
     */
    public TaskResultDetail findOneByGroupAndJobName(String groupName, String jobName) {
        QueryWrapper<TaskResultDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_name", groupName).eq("job_name", jobName);
        return getOne(queryWrapper);
    }

    /**
     * 根据groupName查询TaskResultDetail列表
     *
     * @param groupName 任务组名
     * @return TaskResultDetail列表
     */
    public List<TaskResultDetail> findByGroupName(String groupName) {
        QueryWrapper<TaskResultDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_name", groupName);
        return list(queryWrapper);
    }

    /**
     * 查询所有TaskResultDetail
     *
     * @return TaskResultDetail列表
     */
    public List<TaskResultDetail> findAll() {
        return list();
    }
}