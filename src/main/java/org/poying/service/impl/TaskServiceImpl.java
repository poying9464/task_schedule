package org.poying.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.poying.entity.Task;
import org.poying.mapper.TaskMapper;
import org.poying.service.TaskService;
import org.springframework.stereotype.Service;

@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {
    
}