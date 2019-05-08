package com.dili.ss.uid.service;

import com.dili.ss.uid.domain.BizNumberRule;

/**
 * 业务号服务
 */
public interface BizNumberService {

	/**
	 * 根据业务类型获取编号
	 * @param bizNumberType 业务类型
	 * @return
	 */
	String getBizNumberByType(BizNumberRule bizNumberType);

}