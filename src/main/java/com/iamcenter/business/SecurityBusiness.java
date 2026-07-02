package com.iamcenter.business;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.iamcenter.constant.Constant;
import com.iamcenter.domain.security.SysLogin;
import com.iamcenter.domain.security.SysLoginRole;
import com.iamcenter.repository.SysLoginRepository;
import com.iamcenter.repository.SysLoginRoleDao;
import com.javapai.framework.action.ResultBuilder;
import com.javapai.framework.action.RstResult;
import com.javapai.framework.enums.ErrorCode;
import com.javapai.framework.enums.StatusEnum;
import com.saasapi.contract.security.vo.LoginVO;

import jakarta.annotation.Resource;

@Component
public class SecurityBusiness {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Resource
	protected JdbcTemplate jdbcTemplate;
	
	@Autowired
	private SysLoginRepository sysLoginRepository;
	
	@Autowired
	private SysLoginRoleDao sysLoginRoleDao;

	/**
	 * 检查当前登录名在某app产线上的可用性.<br>
	 *
	 * @param appId     应用标识。<br>
	 * 
	 * @param loginName 登录名称(登录标识)。<br>
	 * 
	 * @return 存在=true; 不存在=false;参数异常=false;
	 * 
	 */
	public boolean checkloginName(String appId, String loginName) {
		if (StringUtils.isBlank(appId) || StringUtils.isBlank(loginName)) {
			logger.warn("--->SecurityBusiness#checkloginNameckeck 参数({}/{})异常！", appId, loginName);
			return false;
		}
		if (sysLoginRepository.existsByAppIdAndLoginName(appId, loginName)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 检查当前appId下的外部登录标识的可用性.<br>
	 *
	 * @param appId
	 *            应用标识。<br>
	 * 
	 * @param externalLoginId
	 *            外部登录标识。<br>
	 * 
	 * @return 存在=true 不存在=false
	 * 
	 */
	public boolean checkExternalLoginId(String appId, String externalLoginId) {
		if (StringUtils.isBlank(appId) || StringUtils.isBlank(externalLoginId)) {
			return false;
		}

		String sql = "select count(loginId) as kk from sys_login where appId=? and ext_login_id=?";
		Map<String, Object> data = jdbcTemplate.queryForMap(sql, new Object[] { appId, externalLoginId });
		String result = String.valueOf(data.getOrDefault("kk", "0"));
		if (null != data && !result.equals("0")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 注册登录账号(随机密码)。<br>
	 * 
	 * @param appId
	 *            应用标识。<br>
	 * @param loginName
	 *            登录账号。<br>
	 * @param version
	 *            app版本号.<br>
	 * 
	 * @return {@link SecurityBusiness#doRegister(String, String, String, String)}。<br>
	 */
	public long doRegister(String appId, String loginName, String version) {
		return doRegister(appId, loginName, Constant.DEFAULT_PWD, version);
	}
	
	/**
	 * 微信账号三方联机注册登录。
	 * 
	 * @param appId       应用标识。<br>
	 * @param extLoginId  三方用户标识（例如：微信的openId开放标识)。<br>
	 * @param extLoginPwd 三方用户密码（例如：微信的session_key会话密钥）。<br>
	 * @param version     应用版本号。<br>
	 * @return
	 */
	public long doWxRegister(String appId, String extLoginId, String extLoginPwd, String version) {
		logger.info("----------->注册：三方账号({})登录信息!", extLoginId);
		SysLogin entity = new SysLogin();
//		entity.setLoginId(RandomUtils.nextLong(1, 100000));
		entity.setLoginName(extLoginId);
		entity.setLoginPwd(DigestUtils.md5Hex(Constant.DEFAULT_PWD));
		entity.setAppId(appId);
		entity.setVersion(version);
		entity.setExtLoginId(extLoginId);
		entity.setExtLoginPwd(extLoginPwd);
		sysLoginRepository.save(entity);
		logger.info("----------->完成：三方账号({})登录信息！", extLoginId);
		
		if (entity.getLoginId() > 0) {
			return entity.getLoginId();
		} else {
			return 0L;
		}
	}
	
	/**
	 * 注册登录账号(指定密码)。<br>
	 * 
	 * @param appId
	 *            应用标识。<br>
	 * @param loginName
	 *            登录账号。<br>
	 * @param loginPwd
	 *            登录密码（明文）。<br>
	 * @param version
	 *            app版本号.<br>
	 * @return 返回用户标识（当用户标识等于0时，代表注册失败）。<br>
	 * 
	 */
	public long doRegister(String appId, String loginName, String loginPwd, String version) {
		logger.info("----------->正在创建用户({})登录信息!", loginName);
		SysLogin entity = new SysLogin();
		entity.setAppId(appId);
//		entity.setLoginId(RandomUtils.nextLong(1, 100000));
		entity.setLoginName(loginName);
		entity.setLoginPwd(DigestUtils.md5Hex(loginPwd));
		entity.setVersion(version);
//		entity.setCreateTime(new java.sql.Timestamp(System.currentTimeMillis()));//我也不晓得为什么非要手工设置，先临时处理。
		sysLoginRepository.save(entity);
		logger.info("----------->登录账号({})已进行注册完成!", loginName);
		/* TODO:注册后续业务事件.如注册积分、注册送券...... */

		if (entity.getLoginId() > 0) {
			// trans.complete();
			// 登录埋点(还有很多信息没有存到登录表中，设计上考虑这些属性没有必要与业务数据表绑定，所有独立埋点存储)
			// EE.logEvent(dto.toString);
			// Map<String, Object> data = new HashMap<String, Object>();
			// data.put("downloadChannel", "app");
			// data.put("regChannel", bo.getAppChannel());
			// data.put("regProduct", "uzone");
			// data.put("regDeviceIdentify", bo.getDeviceId());
			// data.put("remoteIp", bo.getDeviceIp());
			// data.put("addChannel", bo.getAppChannel());
			// commonFields.put("addProduct", "uzone");

			return entity.getLoginId();
		} else {
			// EE.logEvent("Service", "userRegister");
			// trans.setStatus(new BizException(ErrorCode.REGISTER_ERROR));
			// trans.complete();
			// logger.error("---------->账号注册异常:{}"+ex.getLocalizedMessage());
			return 0l;
		}
	}
	
	/**
	 * 注册登录账号(指定密码)。<br>
	 * 
	 * @param loginInfo 注册信息。<br>
	 */
	public long doRegister(SysLogin loginInfo) {
		return doRegister(loginInfo, null);
	}
	
	/**
	 * 注册登录账号(指定密码)。<br>
	 * 
	 * @param appId
	 *            应用标识。<br>
	 * @param loginName
	 *            登录账号。<br>
	 * @param loginPwd
	 *            登录密码（明文）。<br>
	 * @param version
	 *            app版本号.<br>
	 * @return 返回用户标识（当用户标识等于0时，代表注册失败）。<br>
	 * 
	 */
	public long doRegister(SysLogin loginInfo, List<String> loginRole) {
		logger.info("--->正在创建用户(appId={} loginName={})登录信息!", loginInfo.getAppId(), loginInfo.getLoginName());
		// 新注册用户登录状态：INIT
		loginInfo.setLoginState(StatusEnum.INIT.name());
		sysLoginRepository.save(loginInfo);
		logger.info("--->当前登录账号(loginName={})已注册完成!", loginInfo.getLoginName());

		/* 1、注册结果检查 */
		if (loginInfo.getLoginId() <= 0) {
			// EE.logEvent("Service", "userRegister");
			// trans.setStatus(new BizException(ErrorCode.REGISTER_ERROR));
			// trans.complete();
			// logger.error("---------->账号注册异常:{}"+ex.getLocalizedMessage());
			return 0l;
		}

		/* 2、关联角色设置 */
		if (null != loginRole) {
			loginRole.forEach(roleId -> {
				sysLoginRoleDao.save(new SysLoginRole(loginInfo.getLoginId(), roleId));
			});
			logger.info("--->当前登录账户（{}）的角色分配完毕！", loginInfo.getLoginId());
		}
		
		/* 3、注册后续业务事件.如注册积分、注册送券...... */
		// trans.complete();
		// 登录埋点(还有很多信息没有存到登录表中，设计上考虑这些属性没有必要与业务数据表绑定，所有独立埋点存储)
		// EE.logEvent(dto.toString);
		// Map<String, Object> data = new HashMap<String, Object>();
		// data.put("downloadChannel", "app");
		// data.put("regChannel", bo.getAppChannel());
		// data.put("regProduct", "uzone");
		// data.put("regDeviceIdentify", bo.getDeviceId());
		// data.put("remoteIp", bo.getDeviceIp());
		// data.put("addChannel", bo.getAppChannel());
		// commonFields.put("addProduct", "uzone");
		return loginInfo.getLoginId();
	}
	
	public RstResult<LoginVO> doLogin(SysLogin login) {
		List<Long> idsList = listSysLoginIds(login.getAppId(), login.getLoginName());
		if (null == idsList || idsList.size() == 0) {
			logger.warn("----当前应用【{}】的登录用户【{}】有误!", login.getAppId(), login.getLoginName());
			return ResultBuilder.buildResult(ErrorCode.ERROR_LOGIN);
		}
		
		if (null != idsList && idsList.size() >= 2) {
			logger.warn("----当前应用【{}】的登录用户【{}】冲突!", login.getAppId(), login.getLoginName());
			return ResultBuilder.buildResult(ErrorCode.ERROR_LOGIN);
		}
		
		Optional<SysLogin> optional = sysLoginRepository.findById(idsList.get(0));
		if (optional.isEmpty() || !optional.get().getLoginPwd().equals(DigestUtils.md5Hex(login.getLoginPwd()))) {
			logger.warn("----当前应用【{}】的登录密码【{}】有误!", login.getAppId(), login.getLoginName());
			return ResultBuilder.buildResult(ErrorCode.ERROR_LOGIN);
		}

		/* 1、记录token状态 */
		SysLogin entity = optional.get();
		String token = getToken(entity.getAppId(), entity.getLoginName(), entity.getLoginPwd());
		entity.setLoginToken(token);
		entity.setLoginExpire(System.currentTimeMillis() + 36000L);
		sysLoginRepository.save(entity);
		
		/* 2:记录登录日志(异步) */
//		String token = getToken();
		// SysSession session = new SysSession();
		// session.setAppId(login.getAppId());
		// session.setToken(token);
		// session.setLoginType(loginType);
		
		LoginVO loginVO = new LoginVO(token);
		loginVO.setUserId(entity.getLoginId());
		loginVO.setNickName(entity.getNickName());
		loginVO.setLoginName(entity.getLoginName());
		loginVO.setStatus(entity.getLoginState());
		loginVO.setUserIcon(entity.getIconUrl());
		loginVO.setCreateTime(entity.getCreateTime().toString());
		return ResultBuilder.normalResult(loginVO);

		// Users user = null;
		// Long nowTime = UtilDataTime.getNowTime();

		// add login log(以后这里埋点)
		// UsersLog log = new UsersLog();
		// log.setCode("users");
		// log.setType("action");
		// log.setOperating("login");
		// log.setAddtime(String.valueOf(nowTime));
		//
		// try {
		// // 查询用户数据是否存在
		// user = this.getUser(username, password);
		// if (!StringUtils.isEmpty(user.getUsername())) {
		// System.out.println("user login sucess");
		// } else {
		// System.out.println("user login fail");
		// }
		// } catch (BizException e1) {
		//
		// }

	}

	/**
	 * 读取用户名关联登录信息.<br>
	 * 
	 * @param appId
	 *            应用标识.<br>
	 * @param loginName
	 *            登录名(例如:手机号). <br>
	 * @return
	 */
	public List<SysLogin> getSysLogin(String appId, String loginName) {
		String sql = "select loginId,loginName,partyId from sys_login where appId=? and loginName=?";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<SysLogin>(SysLogin.class), appId, loginName);
	}
	
	/**
	 * 读取用户名关联登录标识。<br>
	 * 
	 * @param appId
	 *            应用标识.<br>
	 * @param loginName
	 *            登录名(例如:手机号). <br>
	 * @return
	 */
	public List<Long> listSysLoginIds(String appId, String loginName) {
		String sql = "select loginId from sys_login where appId=? and loginName=?";
		return jdbcTemplate.queryForList(sql, Long.class, appId, loginName);
	}
	
	/**
	 * 登录.<br>
	 * 
	 * @param appId     app标识.<br>
	 * @param loginName 登录名.<br>
	 * @param password  登录密码.<br>
	 * @return token 令牌.<br>
	 * 
	 */
	public String userLogin(String appId, String loginName, String password) {
//		Subject subject = SecurityUtils.getSubject();
//		AuthenticationToken userAndPassworddToken = new UsernamePasswordToken();
//		subject.login(userAndPassworddToken);
//		logger.info("--->系统播报：用户【{}】登记成功！", loginName);
		return "xsss";
//		
//		SysLogin login = new SysLogin();
//		login.setAppId(appId);
//		login.setLoginName(loginName);
//		login.setLoginPwd(password);
//		return userLogin(login);
	}

	public Long getLoginIdByToken(String appId, String token) {
		String sql = "select loginId from sys_login where appId=? and login_token=?";
		try {
			return jdbcTemplate.queryForObject(sql, new Object[] { appId, token }, Long.class);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	/**
	 * 修改当前登录账号密码并使其现有token失效(需重新登录).<br>
	 * 
	 * @param loginId
	 *            登录标识.
	 * @param password
	 *            新密码.
	 * @return
	 */
	// public boolean updateLoginPassword(long loginId, String password) {
	// if (StringUtils.isEmpty(String.valueOf(loginId))) {
	// System.out.println(">>>>>>用户标识参数缺失!");
	// return false;
	// }
	//
	// //1修改用户密码
	// jdbcTemplate.update("");
	// //2密码修改日志.
	// jdbcTemplate.execute("");
	// SysPwdHistory entity = new SysPwdHistory();
	// //3登录token失效.
	// jdbcTemplate.execute("");
	// return true;
	// }
	
	/**
	 * 生成登录Token.<br>
	 * 
	 * @param appId     app标识.<br>
	 * @param loginName 登录名.<br>
	 * @param password  登录密码.<br>
	 * @return token 令牌.<br>
	 * 
	 */
	private String getToken(String appId, String loginName, String password) {
		StringBuffer sb = new StringBuffer();
		sb.append(System.currentTimeMillis());
		// sb.append(userId + productLine);
		sb.append(appId);
		sb.append(loginName);
		// sb.append(dto.getPassword());
		// sb.append(dto.getChannelNo());
		sb.append(System.currentTimeMillis());
		byte[] base64 = Base64.encodeBase64(sb.toString().getBytes());
		logger.info("--->系统播报：用户【{}】登记成功！", loginName);
		return DigestUtils.md5(base64).toString();
	}

}
