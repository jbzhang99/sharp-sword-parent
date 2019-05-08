package com.dili.ss.activiti.service.impl;

import com.dili.ss.activiti.dao.ActFormMapper;
import com.dili.ss.activiti.domain.ActForm;
import com.dili.ss.activiti.service.ActFormService;
import com.dili.ss.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2019-03-21 16:02:46.
 */
@Service
public class ActFormServiceImpl extends BaseServiceImpl<ActForm, Long> implements ActFormService {

    public ActFormMapper getActualDao() {
        return (ActFormMapper)getDao();
    }
}