package org.poying.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.poying.e.TaskResultDetail;

import java.util.List;

public interface TaskResultDetailService extends IService<TaskResultDetail> {
    
    /**
     * 根据groupName和jobName查询TaskResultDetail
     * @param groupName 任务组名
     * @param jobName 任务名
     * @return TaskResultDetail对象
     */
    TaskResultDetail findOneByGroupAndJobName(String groupName, String jobName);
    
    /**
     * 根据groupName查询TaskResultDetail列表
     * @param groupName 任务组名
     * @return TaskResultDetail列表
     */
    List<TaskResultDetail> findByGroupName(String groupName);
    
    /**
     * 查询所有TaskResultDetail
     * @return TaskResultDetail列表
     */
    List<TaskResultDetail> findAll();
}