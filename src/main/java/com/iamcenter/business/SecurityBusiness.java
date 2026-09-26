package com.iamcenter.business;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;

import com.iamcenter.config.jwt.JwtUtil;
import com.iamcenter.constant.Constant;
import com.iamcenter.domain.security.SysLogin;
import com.iamcenter.domain.security.SysLoginRole;
import com.iamcenter.domain.security.SysRole;
import com.iamcenter.repository.SysLoginRepository;
import com.iamcenter.repository.SysLoginRoleDao;
import com.iamcenter.repository.SysRoleRepository;
import com.iamcenter.strategy.EncoderStrategy;
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
	SysRoleRepository sysRoleDao;
	
	@Autowired
	private SysLoginRoleDao sysLoginRoleDao;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private EncoderStrategy encoderStrategy;
	
	@Autowired
	private JwtUtil jwtUtil; 
	
	@Value("${jwt.expiration:3600000}")
	private Long expiration;

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
	 * 三方账号(例如微信登录）注册登录。
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
	 * 注册登录账号(默认密码)。<br>
	 * 
	 * @param appId     应用标识。<br>
	 * @param loginName 登录账号。<br>
	 * @param version   应用版本号。<br>
	 * 
	 * @return {@link SecurityBusiness#doRegister(String, String, String, String)}。<br>
	 */
	public RstResult<String> doRegister(String appId, String loginName, String version) {
		return doRegister(appId, loginName, Constant.DEFAULT_PWD, version);
	}

	/**
	 * 注册登录账号(指定密码)。<br>
	 * 
	 * @param appId     应用标识。<br>
	 * @param loginName 登录账号。<br>
	 * @param loginPwd  登录密码（明文）。<br>
	 * @param version   应用版本号。<br>
	 * @return 返回用户标识（当用户标识等于0时，代表注册失败）。<br>
	 * 
	 */
	public RstResult<String> doRegister(String appId, String loginName, String loginPwd, String version) {
		SysLogin entity = new SysLogin();
		entity.setAppId(appId);
		entity.setLoginName(loginName);
		entity.setLoginPwd(DigestUtils.md5Hex(loginPwd));
		entity.setVersion(version);
//		entity.setCreateTime(new java.sql.Timestamp(System.currentTimeMillis()));//我也不晓得为什么非要手工设置，先临时处理。
		return doRegister(entity, null);
		
		
//		logger.info("----------->正在创建用户({})登录信息!", loginName);
//		
//		sysLoginRepository.save(entity);
//		logger.info("----------->登录账号({})已进行注册完成!", loginName);
//		/* TODO:注册后续业务事件.如注册积分、注册送券...... */
//
//		if (entity.getLoginId() > 0) {
//			// trans.complete();
//			// 登录埋点(还有很多信息没有存到登录表中，设计上考虑这些属性没有必要与业务数据表绑定，所有独立埋点存储)
//			// EE.logEvent(dto.toString);
//			// Map<String, Object> data = new HashMap<String, Object>();
//			// data.put("downloadChannel", "app");
//			// data.put("regChannel", bo.getAppChannel());
//			// data.put("regProduct", "uzone");
//			// data.put("regDeviceIdentify", bo.getDeviceId());
//			// data.put("remoteIp", bo.getDeviceIp());
//			// data.put("addChannel", bo.getAppChannel());
//			// commonFields.put("addProduct", "uzone");
//
//			return entity.getLoginId();
//		} else {
//			// EE.logEvent("Service", "userRegister");
//			// trans.setStatus(new BizException(ErrorCode.REGISTER_ERROR));
//			// trans.complete();
//			// logger.error("---------->账号注册异常:{}"+ex.getLocalizedMessage());
//			return 0l;
//		}
	}
	
	/**
	 * 注册登录账号(指定密码)。<br>
	 * 
	 * @param loginInfo 注册信息。<br>
	 */
	public RstResult<String> doRegister(SysLogin loginInfo) {
		return doRegister(loginInfo, null);
	}
	
	/**
	 * 
	 * @param loginInfo  登录信息。<br>
	 * @param loginRoles 角色信息。<br>
	 * @return 返回用户标识（当用户标识等于0时，代表注册失败）。<br>
	 */
	public RstResult<String> doRegister(SysLogin loginInfo, List<String> loginRoles) {
		logger.info("--->正在创建用户(appId={} loginName={})登录信息!", loginInfo.getAppId(), loginInfo.getLoginName());

		/* 1、验证角色有效性 */
		if (null != loginRoles) {
			for (String roleId : loginRoles) {
				Optional<SysRole> optional = sysRoleDao.findById(Long.valueOf(roleId));
				if (!optional.isPresent() || !optional.get().getAppId().equals(loginInfo.getAppId())) {
					logger.warn("--->当前应用[{}]关联角色[{}]有误！", loginInfo.getAppId(), roleId);
					return ResultBuilder.buildResult(ErrorCode.ERROR_REGISTER);
				}
			}
		}
		
		/* 2、保存注册信息 */
		// 注册用户密码加密
		loginInfo.setLoginPwd(encoderStrategy.encoderPassword(EncoderStrategy.BCrypt, loginInfo.getLoginPwd()));
		// 注册用户登录状态：INIT
		loginInfo.setLoginState(StatusEnum.ENABLE.name());
		sysLoginRepository.save(loginInfo);
		logger.info("--->当前登录账号(loginName={})已注册完成!", loginInfo.getLoginName());
		if (loginInfo.getLoginId() <= 0) {
			// EE.logEvent("Service", "userRegister");
			// trans.setStatus(new BizException(ErrorCode.REGISTER_ERROR));
			// trans.complete();
			logger.error("--->{}账号注册异常！" + loginInfo.getLoginName());
			return ResultBuilder.buildResult(ErrorCode.ERROR_REGISTER);
		}

		/* 3、关联角色设置 */
		if (null != loginRoles) {
			loginRoles.forEach(roleId -> {
				sysLoginRoleDao.save(new SysLoginRole(loginInfo.getLoginId(), Long.valueOf(roleId)));
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
		return ResultBuilder.normalResult(String.valueOf(loginInfo.getLoginId()));
	}
	
	public RstResult<LoginVO> doLogin(SysLogin login) {
		// 当前AppId是否可用
		SysLogin entity = sysLoginRepository.findByAppIdAndLoginName(login.getAppId(), login.getLoginName());
		if (null == entity) {
			logger.warn("----当前应用【{}】的登录用户【{}】有误!", login.getAppId(), login.getLoginName());
			return ResultBuilder.buildResult(ErrorCode.ERROR_LOGIN);
		}
//		if (!entity.getLoginPwd().equals(DigestUtils.md5Hex(login.getLoginPwd()))) {
//			logger.warn("----当前应用【{}】的登录密码【{}】有误!", login.getAppId(), login.getLoginName());
//			return ResultBuilder.buildResult(ErrorCode.ERROR_LOGIN);
//		}

		/* 0、解决认证问题（手动编码进行认证 或 借助security框架认证 */
		/*-----采用Security框架认证(开始）-------*/
		String fullUserUserName = login.getAppId() + "#" + login.getLoginName();
		UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(fullUserUserName, login.getLoginPwd());
		try {
			Authentication authentication = authenticationManager.authenticate(upat);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		} catch (BadCredentialsException bce) {
			logger.warn("----当前应用【{}】的登录密码【{}】有误!", login.getAppId(), login.getLoginName());
			return ResultBuilder.buildResult(ErrorCode.ERROR_LOGIN);
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
		}
		/*-----采用Security框架认证(结束）-------*/
		
		/* 1、更新记录token状态 */
//		String token = getToken(entity.getAppId(), entity.getLoginName(), entity.getLoginPwd());
		String token = getJWTToken(entity.getAppId(), String.valueOf(entity.getLoginId()), entity.getLoginName());
		entity.setLoginToken(token);
		entity.setLoginExpire(System.currentTimeMillis() + expiration);
		sysLoginRepository.save(entity);
		
		/* 2:记录登录日志(异步) */
//		String token = getToken();
		// SysSession session = new SysSession();
		// session.setAppId(login.getAppId());
		// session.setToken(token);
		// session.setLoginType(loginType);
		
		/* 3、响应登录成功的报文 */
		LoginVO loginVO = new LoginVO();
		loginVO.setAccessToken(token);
		loginVO.setUserId(entity.getLoginId());
		loginVO.setNickName(entity.getNickName());
		loginVO.setLoginName(entity.getLoginName());
		loginVO.setStatus(entity.getLoginState());
		loginVO.setUserIcon(entity.getIconUrl());
		loginVO.setCreateTime(entity.getCreateTime().toString());
		// 查询登录用户关联角色
		loginVO.setRoleList(listRoleCodeByLoginId(entity.getLoginId()));
		return ResultBuilder.normalResult(loginVO);
	}
	
	@Transactional
	public boolean doLogout(String token) {
		/* 1.select userLogin,and update token is null */
		SysLogin login = sysLoginRepository.findByLoginToken(token);
		if (null != login) {
			login.setLoginExpire(0L);
			logger.info("--->[{}]set token expire!", login.getLoginName());
		}

		/* 2.clear session */
//		sessionRepository.delete(dto.getToken());
		SecurityContextHolder.clearContext();
		logger.info("--->[{}]delete session over！", login.getLoginName());

		/* 3.clear redis(if redis exsit) */

		/* 4.return result */
		return true;
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
			return jdbcTemplate.queryForObject(sql, Long.class, new Object[] { appId, token });
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	public List<String> listRoleCodeByLoginId(Long loginId) {
		// 优先读缓存再读本地库
		return sysLoginRoleDao.listRoleCodeByLoginId(loginId);
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
	 * 生成JWT登录Token.<br>
	 * 
	 * @param appId     app标识.<br>
	 * @param loginId   登录密码.<br>
	 * @param loginName 登录名.<br>
	 * 
	 * @return token 令牌.<br>
	 */
	private String getJWTToken(String appId, String loginId, String loginName) {
		// 构造 JWT Claims（把应用权限和用户权限合并）
//		Map<String, Object> claims = new HashMap<String, Object>();
//		// 应用信息
//		claims.put("appId", appId);
//		// 用户信息
//		claims.put("username", loginName);
//		// 用户角色列表（来自 UserDetails）
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		claims.put("roles", auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
		// 应用权限范围（来自 oauth_app 表）
//		claims.put("scopes", Arrays.asList(app.getAllowedScopes().split(",")));
//		return jwtUtil.generateToken(loginId, claims);
		/* 重点提示：因为claims内容越多生成的token越长（jwt机制原因），所以claims暂时只放必要字段。 */
		return jwtUtil.generateToken(loginId, Map.of("appId", appId, "username", loginName));
	}
	
//	@Transactional
//	public String refreshToken(String oldToken) {
//		try {
//			String loginId = jwtUtil.extractSubject(oldToken);
//			String username = jwtUtil.parseToken(oldToken).get("username", String.class);
//			Optional<SysLogin> optional = sysLoginRepository.findById(Long.valueOf(loginId));
//			if (!optional.isPresent()) {
//				return null;
//			}
//			SysLogin entity = optional.get();
//			if (!entity.getLoginToken().equals(oldToken)) {
//				return null;
//			}
////			String appId = jwtUtil.extractAppId(oldToken);
//			String newToken = getJWTToken(optional.get().getAppId(), loginId, username);
//			entity.setLoginToken(newToken);
//			entity.setLoginExpire(System.currentTimeMillis() + expiration);
//			sysLoginRepository.save(entity);
//			logger.info("--->用户[{}]token已自动刷新!", loginId);
//			return newToken;
//		} catch (Exception e) {
//			logger.error("--->token刷新异常:{}", e.getMessage());
//			return null;
//		}
//	}
	
}
