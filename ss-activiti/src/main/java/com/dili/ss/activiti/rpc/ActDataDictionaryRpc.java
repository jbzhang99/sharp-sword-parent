package com.dili.ss.activiti.rpc;

import com.dili.ss.activiti.domain.DataDictionaryValue;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.retrofitful.annotation.POST;
import com.dili.ss.retrofitful.annotation.Restful;
import com.dili.ss.retrofitful.annotation.VOBody;

import java.util.List;

/**
 * 数据字典(值)接口
 * Created by asiam on 2018/7/10 0010.
 */
@Restful("${uap.contextPath}")
public interface ActDataDictionaryRpc {

    @POST("/dataDictionaryApi/list.api")
    BaseOutput<List<DataDictionaryValue>> list(@VOBody DataDictionaryValue dataDictionaryValue);

}
