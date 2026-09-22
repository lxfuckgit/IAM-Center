package com.iamcenter.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iamcenter.business.SecurityBusiness;
import com.iamcenter.constant.Constant;
import com.iamcenter.controller.dto.ChangeAppStatusDTO;
import com.iamcenter.domain.apps.AppInfo;
import com.iamcenter.domain.apps.AppUpdate;
import com.iamcenter.domain.security.SysLogin;
import com.iamcenter.domain.security.SysRole;
import com.iamcenter.repository.SysRoleRepository;
import com.iamcenter.repository.apps.AppInfoRepository;
import com.javapai.framework.action.PageResult;
import com.javapai.framework.action.ResultBuilder;
import com.javapai.framework.action.RstResult;
import com.javapai.framework.common.service.AbstractBizService;
import com.javapai.framework.enums.ChannelEnum;
import com.javapai.framework.enums.ErrorCode;
import com.javapai.framework.enums.StatusEnum;
import com.saasapi.contract.apps.AppsContract;
import com.saasapi.contract.apps.dto.AppListDTO;
import com.saasapi.contract.apps.enums.OSEnum;
import com.saasapi.contract.apps.vo.AppUpgrade;

@Service
public class AppInfoService extends AbstractBizService implements AppsContract {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	AppInfoRepository appInfoRepository;
	
	@Autowired
	SysRoleRepository sysRoleRepository;
	
	@Autowired
	SecurityBusiness securityBusiness;

	@Override
	public RstResult<com.saasapi.contract.apps.vo.AppInfo> getAppInfo(String appId) {
		if (StringUtils.isBlank(appId)) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_EMPTY);
		}
		List<AppInfo> list = jdbcTemplate.query("select * from app_info where app_id=?",
				new BeanPropertyRowMapper<AppInfo>(AppInfo.class), appId);
		if (null == list || list.isEmpty()) {
			return ResultBuilder.buildResult("40000002", "应用信息不存在!");
		}
		AppInfo entity = list.get(0);
		com.saasapi.contract.apps.vo.AppInfo vo = new com.saasapi.contract.apps.vo.AppInfo();
		BeanUtils.copyProperties(entity, vo);
		if (null != entity.getCreateTime()) {
			vo.setCreateTime(entity.getCreateTime().toLocalDateTime());
		}
		return ResultBuilder.normalResult(vo);
	}

	@Override
	public RstResult<AppUpgrade> checkIosVersion(String appId) {
		return checkAppsVersion(appId, OSEnum.OS_IOS);
	}

	@Override
	public RstResult<AppUpgrade> checkAndroidVersion(String appId) {
		return checkAppsVersion(appId, OSEnum.OS_ANDRIOD);
	}

	@Override
	public RstResult<AppUpgrade> checkAppsVersion(String appId, OSEnum osName) {
		return checkAppsVersion(appId, osName, ChannelEnum._NA_.name());
	}

	@Override
	public RstResult<AppUpgrade> checkAppsVersion(String appId, OSEnum osName, String channel) {
		String sql = "select * from app101 where app_id=? and app_sys=? and channel=? order by versNum desc limit 1 ";
		List<AppUpdate> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper<AppUpdate>(AppUpdate.class),
				new Object[] { appId, osName.getKey(), channel });
//		list.stream()
//				.filter(appUpdate -> appUpdate.getUpdateChannel() == channel)
//				.max(Comparator.comparing(AppUpdate::getCreateTime));

		if (null != list && list.size() > 0) {
			AppUpgrade result = new AppUpgrade();
			result.setVersion(list.get(0).getVersion());
			result.setVersNum(list.get(0).getVersNum());
			return ResultBuilder.normalResult();
		} else {
			return ResultBuilder.normalResult();
		}
	}

	/**
	 * 应用列表查询。<br>
	 */
	public PageResult<AppInfo> listAppInfo(AppListDTO dto) {
		List<Object> params = new ArrayList<Object>();
		StringBuilder sb = new StringBuilder("select * from app_info where 1=1");
		if (StringUtils.isNotBlank(dto.getAppId())) {
			sb.append(" and app_id=?");
			params.add(dto.getAppId());
		}
		if (StringUtils.isNotBlank(dto.getAppCode())) {
			sb.append(" and app_code=?");
			params.add(dto.getAppCode());
		}
		if (StringUtils.isNotBlank(dto.getAppName())) {
			sb.append(" and app_name like ?");
			params.add("%" + dto.getAppName() + "%");
		}
		if (StringUtils.isNotBlank(dto.getAppStatus())) {
			sb.append(" and app_status=?");
			params.add(dto.getAppStatus());
		}
		return getPage(sb.toString(), params, dto.getPageIndex(), dto.getPageSize(), AppInfo.class);
	}

	/**
	 * 应用注册。<br>
	 */
	@Transactional
	public RstResult<String> addAppInfo(AppInfo dto) {
		if (StringUtils.isBlank(dto.getAppCode()) || StringUtils.isBlank(dto.getAppName())) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_EMPTY);
		}
		// 校验应用编号唯一
		if (appInfoRepository.existsByAppCode(dto.getAppCode())) {
			return ResultBuilder.buildResult("40000002", "应用编号已存在!");
		}
		// 应用标识为空时自动生成
		String appId = String.valueOf(System.currentTimeMillis());
		String appStatus = StatusEnum.ENABLE.name();
		String updateSQL = "insert into app_info (app_id, app_code, app_name, app_status, app_provider, app_contact) values (?, ?, ?, ?, ?, ?)";
		int r1 = jdbcTemplate.update(updateSQL, appId, dto.getAppCode(), dto.getAppName(), appStatus, dto.getAppProvider(), dto.getAppContact());
		logger.info("--->应用（{})创建结果：{}", dto.getAppName(), r1);
		// 生成应用的管理员角色
		SysRole userRole = new SysRole();
		userRole.setAppId(appId);
		userRole.setCode(dto.getAppCode());
		userRole.setName("管理员");
		userRole.setStatusId(StatusEnum.ENABLE.getValue());
		userRole.setCreatorId(SecurityContextHolder.getContext().getAuthentication().getName());
		sysRoleRepository.save(userRole);
		// 生成应用的管理员账号（提示：电话当登录账号可能会重复）
		SysLogin userLogin = new SysLogin();
		userLogin.setAppId(appId);
		userLogin.setLoginName(dto.getAppCode());
		userLogin.setLoginPwd(Constant.DEFAULT_PWD);
		userLogin.setVersion(Constant.DEFAULT_VERSION);
		RstResult<String> r2 = securityBusiness.doRegister(userLogin, List.of(String.valueOf(userRole.getId())));
		logger.info("--->应用管理员创建结果：{}", dto.getAppName(), r2.getCode());
		return ResultBuilder.normalResult();
	}

	/**
	 * 应用修改。<br>
	 */
	public RstResult<String> updateAppInfo(AppInfo dto) {
		if (null == dto.getAppId()) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_EMPTY);
		}
		Integer count = jdbcTemplate.queryForObject("select count(1) from app_info where app_id=?", Integer.class,
				dto.getAppId());
		if (null == count || count == 0) {
			return ResultBuilder.buildResult("40000002", "应用信息不存在!");
		}
		// app_code 不允许修改（怕有引用），app_status 由启停接口维护
		StringBuilder sql = new StringBuilder("update app_info set ");
		List<Object> params = new ArrayList<Object>();
		if (StringUtils.isNotBlank(dto.getAppName())) {
			sql.append("app_name=?,");
			params.add(dto.getAppName());
		}
		if (StringUtils.isNotBlank(dto.getAppProvider())) {
			sql.append("app_provider=?,");
			params.add(dto.getAppProvider());
		}
		if (StringUtils.isNotBlank(dto.getAppContact())) {
			sql.append("app_contact=?,");
			params.add(dto.getAppContact());
		}
		if (params.isEmpty()) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_EMPTY);
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(" where app_id=?");
		params.add(dto.getAppId());
		jdbcTemplate.update(sql.toString(), params.toArray());
		return ResultBuilder.normalResult();
	}

	/**
	 * 应用启动/停用。<br>
	 */
	public RstResult<String> changeAppStatus(ChangeAppStatusDTO dto) {
		if (StringUtils.isBlank(dto.getAppId()) || StringUtils.isBlank(dto.getAppStatus())) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_EMPTY);
		}
		int rows = jdbcTemplate.update("update app_info set app_status=? where app_id=?", dto.getAppStatus(),
				dto.getAppId());
		if (rows == 0) {
			return ResultBuilder.buildResult("40000002", "应用信息不存在!");
		}
		return ResultBuilder.normalResult();
	}

}
