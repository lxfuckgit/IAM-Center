package com.iamcenter.repository.apps;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iamcenter.domain.apps.AppInfo;

public interface AppInfoRepository extends JpaRepository<AppInfo, Long> {
	
	AppInfo findByAppId(Long appId);
	
	AppInfo findByAppCode(String appCode);

	/**
	 * 检查当前【应用编码】是否存在。
	 * 
	 * @param appCode
	 * @return
	 */
	//select count(1) from app_info where app_code=?
	boolean existsByAppCode(String appCode);
}
