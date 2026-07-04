package com.iamcenter.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;

import com.iamcenter.business.SecurityBusiness;
import com.iamcenter.constant.Constant;
import com.iamcenter.domain.security.SysLogin;
import com.iamcenter.domain.security.SysPwdHistory;
import com.iamcenter.repository.PwdHistoryRepository;
import com.iamcenter.repository.SysLoginRepository;
import com.javapai.framework.action.ResultBuilder;
import com.javapai.framework.action.RstResult;
import com.javapai.framework.enums.ErrorCode;
import com.saasapi.contract.security.AuthContract;
import com.saasapi.contract.security.dto.ChangePwdDTO;
import com.saasapi.contract.security.dto.ConnectorPLoginDTO;
import com.saasapi.contract.security.dto.LogoutDTO;
import com.saasapi.contract.security.dto.PwdLoginDTO;
import com.saasapi.contract.security.dto.RegPwdDTO;
import com.saasapi.contract.security.dto.RegSmsDTO;
import com.saasapi.contract.security.dto.ResetPwdDTO;
import com.saasapi.contract.security.dto.SmsLoginDTO;
import com.saasapi.contract.security.dto.UpdateLoginProfileDTO;
import com.saasapi.contract.security.dto.ValidateTokenDTO;
import com.saasapi.contract.security.enums.SecurityECode;
import com.saasapi.contract.security.vo.LoginVO;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@DubboService
public final class AuthService implements AuthContract {
	/**/
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	// @Autowired
	// @org.apache.dubbo.config.annotation.Reference(lazy = true, check = false)
	// private MessageContract messageService;

	@Autowired
	private SecurityBusiness securityBusiness;

//	@Autowired
//	private SysSessionBusiness sysSessionBusiness;

	@Autowired
	private SysLoginRepository sysLoginRepository;
//	@Autowired
//	private SessionRepository sessionRepository;
	@Autowired
	private PwdHistoryRepository pwdHistoryRepository;

	@Value("${is.sms.fake:1}")
	private String isSmsFake = "1";// 考虑用loginNameFake和LoginPwdFake代替.

//	@Value("${is.loginName.fake:18888888888}")
//	private String loingNameFake;
//
//	@Value("${is.loginPwd.fake:password")
//	private String loginPwdFake;

	@Override
	public RstResult<String> register(RegPwdDTO dto) {
		if (StringUtils.isEmpty(dto.getAppId())) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_APPID);
		}
		if (StringUtils.isBlank(dto.getUsername()) || StringUtils.isBlank(dto.getPassword())) {
			return ResultBuilder.buildResult(ErrorCode.ERROR_LOGIN);
		}
		if (StringUtils.isEmpty(dto.getVersion())) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_VERISON);
		}
		if (securityBusiness.checkloginName(dto.getAppId(), dto.getUsername())) {
			logger.error("----------->当前用户注册名{}已存在，请更换注册用户名!", dto.getUsername());
			return ResultBuilder.buildResult(ErrorCode.EXIST_USER);
		}
		if (StringUtils.isNotBlank(dto.getPassword2())) {
			if (!dto.getPassword().equals(dto.getPassword2())) {
				logger.error("----------->两次密码设置不一致，请检查!", dto.getUsername());
				return ResultBuilder.buildResult(ErrorCode.PASSWORD_PATTERN);
			}
		}
		SysLogin entity = new SysLogin();
		entity.setAppId(dto.getAppId());
		entity.setLoginName(dto.getUsername());
		entity.setLoginPwd(dto.getPassword());
		entity.setVersion(dto.getVersion());
		entity.setNickName(dto.getNickName());
		Long loginId = securityBusiness.doRegister(entity, dto.getRoleList());
		if (loginId > 0) {
			return ResultBuilder.normalResult(String.valueOf(entity.getLoginId()));
		} else {
			return ResultBuilder.buildResult(ErrorCode.ERROR_REGISTER);
		}
	}

	@Override
	public RstResult<String> register(RegSmsDTO dto) {
		// 验证手机号(用户名)
		if (StringUtils.isEmpty(dto.getPhone())) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_PHONE);
		}
		// 验证验证码
		if (StringUtils.isEmpty(dto.getValidCode())) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_CAPTCHA);
		}
		// 根据应用标识
		if (StringUtils.isEmpty(dto.getAppId())) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_APPID);
		}
		// 验证版本号
		if (StringUtils.isEmpty(dto.getVersion())) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_VERISON);
		}

		/* 验证码确认 */
//		if (!"1".equals(isSmsFake)
//				|| !(loingNameFake.equals(dto.getPhone()) && loginPwdFake.equals(dto.getValidCode()))) {
			// RstResult<String> result = messageService.validSmsCaptcha(new
			// ValidSmsCaptchaDTO(dto.getPhone(), dto.getValidCode()));
			// if(!result.getCode().equals("00000000")) {
			// return null;//RstResultBuilder.buildErrorResponse(MessageECode.SMS_INVALID);
			// }
//		}

		/* 验证用户名(手机号)是否存在 */
		boolean exist = securityBusiness.checkloginName(dto.getAppId(), dto.getPhone());
		if (exist) {
			// EE.logEvent("Service", "userRegister");
			// trans.complete();
			logger.info("----------->验证当前手机号{}结果:[{}]", dto.getPhone(), exist);
			return ResultBuilder.buildResult(ErrorCode.EXIST_PHONE);
		} else {
			return doRegister(dto.getAppId(), dto.getPhone(), Constant.DEFAULT_PWD, dto.getVersion());
		}
	}

	@Override
	public RstResult<LoginVO> registerAndLogin(RegSmsDTO dto) {
		// 利用注册服务注册(dto类型一样，先借用其业务功能)。
		RstResult<String> temp = register(dto);
		String rstCode = temp.getCode();
		if (rstCode.equals(ResultBuilder.RESPONSE_OK) || rstCode.equals(ErrorCode.EXIST_PHONE.getKey())) {
			logger.info("--------->开始登录!");
			/* 自动登录账号 */
			SysLogin login = new SysLogin();
			login.setAppId(dto.getAppId());
			login.setLoginName(dto.getPhone());
			login.setLoginPwd(Constant.DEFAULT_PWD);
			return securityBusiness.doLogin(login);
		} else {
			return ResultBuilder.buildResult(temp.getCode(), temp.getMessage());
		}

//		if(canAutoLogin){
//			//其实，这里也要可以不用报错，找到当前已存在的用户，可跳过注册并完成登录(如果用户换手机号后，历史数据问题，这个问题要考虑的太多了)。
//			List<SysLogin> list = securityBusiness.getSysLogin(dto.getAppId(), dto.getPhone());
//		} else {
//			return RstResultBuilder.buildErrorResponse(ErrorCode.ERROR_EXIST_PHONE);
//		}

		/* 注册登录账号 */
//		List<SysLogin> list = securityBusiness.getSysLogin(dto.getAppId(), dto.getPhone());
//		if (CollectionUtils.isEmpty(list)) {
//			SysLogin entity = new SysLogin();
//			entity.setLoginName(dto.getPhone());
//			entity.setLoginPwd("123456");//DigestUtils.md5Hex(Constant.USER_DEFAULT_PWD)
//			entity.setAppId(dto.getAppId());
//			entity.setAppName(dto.getAppName());
//			entity.setVersion(dto.getVersion());
//			entity.setFromChannel(dto.getFromChannel());
//			entity.setDeviceIp(dto.getDeviceIp());
//			entity.setDeviceId(dto.getDeviceId());
//			entity.setDeviceMac(dto.getDeviceMac());
//			entity.setDeviceIdfa(dto.getDeviceIdfa());
//			entity.setDeviceOs//操作系统及型号
//			.channel(Constant.channel)
//            .equipNo(param.getDeviceId())
//            .imei(param.getImei())
//            .loginLat(param.getLat())
//            .loginLon(param.getLon())
//            .isEscape(param.getIsEscape())
//            .isRoot(param.getIsRoot())
//            .idfa(param.getDeviceIdfa())
//            .idfv(param.getDeviceIdfv())
//            .loginMaketingChannel(param.getChannel())
//            .dataChannel(param.getDataChannel())
//            .equipSys(param.getEquipSys())
//            .loginPwd(Constant.USER_DEFAULT_PWD)
//            .mobile(param.getPhone())
//            .userNo(Constant.channel + "_" + userId)
//		}

	}

	/**
	 * 用户登录认证服务.<br>
	 * 用户通过账号和登录密码登录.<br>
	 *
	 * @param dto
	 * @return 返回登录成功后的授权码code.
	 */
	@Override
	public RstResult<LoginVO> userLogin(PwdLoginDTO dto) {
		if (StringUtils.isEmpty(dto.getAppId())) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_APPID);
		}
		if (StringUtils.isEmpty(dto.getUsername())) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_USERNAME);
		}
		if (StringUtils.isEmpty(dto.getPassword())) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_USERNAME);
		}

		SysLogin login = new SysLogin();
		login.setAppId(dto.getAppId());
		login.setLoginName(dto.getUsername());
		login.setLoginPwd(dto.getPassword());
		return securityBusiness.doLogin(login);
	}

	/**
	 * 用户登录认证服务.<br>
	 * 用户通过账号和登录验证码登录.<br>
	 *
	 * @param dto
	 * @return
	 */
	@Override
	public RstResult<LoginVO> userLogin(SmsLoginDTO dto) {
		// TODO: 读取配置中心，有无密码规则。或是要验证密码规则(交由api层)
		// ResponseEntity<String> result =
		// validatePasswordRule(dto.getPassword(), true);
		/*
		 * if(!ResponseResultBuilder.isSuccess(result)) { return
		 * ResponseResultBuilder.buildErrorResponse(ErrorCode.PASSWORD_PATTERN); }
		 */

		/* 验证必要参数 */
		if (StringUtils.isEmpty(dto.getUsername())) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_CHANNEL);
		}
		if (StringUtils.isEmpty(dto.getSmsCode())) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_CHANNEL);
		}
		if (StringUtils.isEmpty(dto.getUsername())) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_CHANNEL);
		}

		/* 读取相关配置 */
		String is_skip = "1";// 读取配置中心配置
		if (!"1".equals(is_skip)) {
		}

		/* auto login */
//		com.javapai.security.domain.SysSession session = new SysSession();
//		session.setToken(getToken());

		Set<String> selectField = new HashSet<String>();
		selectField.add("userId");

		securityBusiness.getSysLogin("", dto.getUsername());
//		session.setLoginId("");
//		session.setLoginType("SMS");
//		boolean result = sysSessionBusiness.cLoginSession(session);

		// try {
		// List<Map<String, String>> loginvo =
		// profileBusiness.getUserInfo(dto.getUsername(), selectField);
		// String token = userAuthBusiness.createUserSession(123456L,
		// dto.getProductLine());
		//
		// Map<String,String> map = new HashMap<>();
		// map.put("token", token);
		return ResultBuilder.normalResult();
		// } catch (BizException e) {
		// e.printStackTrace();
		// return
		// ResponseResultBuilder.buildErrorResponse(ErrorCode.ERROR_USER_LOGIN);
		// }
	}

	/**
	 * 用户退出认证服务.<br>
	 *
	 * @param dto
	 * @return
	 */
	@Override
	public RstResult<String> logout(@Validated LogoutDTO dto) {
		if (StringUtils.isBlank(dto.getToken())) {
			return ResultBuilder.buildResult(ErrorCode.INVALID_TOKEN);
		}
		if (dto.getToken().startsWith("Bearer ")) {
			dto.setToken(dto.getToken().substring(7));
		}
//		int result = userLoginBusiness.deleteUserSessionByCode(dto.getToken());
//		if (result > 0) {
//			return RstResultBuilder.buildResult();
//		} else {
//			return RstResultBuilder.buildErrorResponse(ErrorCode.ERROR_LOGOUT);
//		}

		/* 1.validate token */
		// if redis exsit, select userLogin,and update token is null
		int r1 = sysLoginRepository.deleteByLoginToken(dto.getToken());
		logger.info("--->[{}]delete token:{} ", r1, dto.getToken());

		/* 2.clear session */
//		sessionRepository.delete(dto.getToken());
		SecurityContextHolder.clearContext();
		logger.info("--->delete session:{} ", dto.getToken());

		/* 3.clear redis */

		/* 4.return result */
		return ResultBuilder.normalResult();
	}

	/**
	 * 用户重置密码服务.<br>
	 *
	 * @param dto
	 * @return
	 */
	@Override
	public RstResult<String> resetPassword(ResetPwdDTO dto) {
		// 验证电话是否存在
		if (StringUtils.isEmpty(String.valueOf(dto.getPhone()))) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_PHONE);
		}
		Long userId = null;

		/* 1:验证密码规则 */
		RstResult<String> rule = validatePasswordRule(dto.getPassword(), true);
		if (!ResultBuilder.RESPONSE_OK.equals(rule.getMessage())) {
			return ResultBuilder.buildResult(ErrorCode.PASSWORD_PATTERN);
		}

		// try {
		// //* 2：提取用户信息 *//*
		// Set<String> field = new HashSet<>();
		// field.add("phone");
		// //更具手机号查询用户数据
		// List<Map<String, String>> userList =
		// profileBusiness.getUserInfo(dto.getPhone(), field);
		// if (userList == null) {
		// EE.logEvent("Service", "userRegister");
		// return
		// ResponseResultBuilder.buildErrorResponse(CenterConstants.ERROR_CODE_DEFAULT,"用户标识无效,请核对!");
		// } else {
		// if (userList.size() > 1) {
		// EE.logEvent("Service", "userRegister");
		// return
		// ResponseResultBuilder.buildErrorResponse(CenterConstants.ERROR_CODE_DEFAULT,"用户("+dto.getPhone()+")存在多条记录!");
		// } else {
		// Map<String, String> map = userList.get(0);
		// userId = Long.valueOf(map.get("userId"));
		// }
		// }
		// } catch (BizException e) {
		// e.printStackTrace();
		// }

		/* 3:修改用户密码 */
		Map<String, Object> commonFields = new HashMap<>();
		commonFields.put("addChannel", "app");
		commonFields.put("addProduct", "uzone");
		commonFields.put("remoteIp", dto.getDeviceIp());
		// commonFields.put("addVersion", dto.getAppVersion());
		commonFields.put("addUser", "system");
		boolean result = false;
		// try {
		// result = userAuthBusiness.updateUserPassword(userId,
		// dto.getPassword(),commonFields);
		// } catch (Exception e) {
		// EE.logError("userBusiness#updateUserPassword", e);
		// e.printStackTrace();
		// }
		if (result) {
			return ResultBuilder.normalResult();
		} else {
			return ResultBuilder.buildResult("420000025", "找回密码服务异常，无法完成密码修改!");
		}
	}

	/**
	 * 用户更改密码服务.<br>
	 *
	 * @param dto
	 * @return
	 */
	@Override
	public RstResult<String> changePassword(ChangePwdDTO dto) {
		if (StringUtils.isEmpty(String.valueOf(dto.getLoginId()))) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_USERID);
		}
		if (StringUtils.isEmpty(dto.getNewPasswd()) || StringUtils.isEmpty(dto.getNewPasswdAgain())) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_NEWPASSWORD);
		}
		if (!dto.getNewPasswd().equals(dto.getNewPasswdAgain())) {
			return ResultBuilder.buildResult("420000025", "两次密码输次不一致!");
		}

		/* 1:验证密码规则 */
		RstResult<String> rule = validatePasswordRule(dto.getNewPasswd(), true);
		if (!ResultBuilder.RESPONSE_OK.equals(rule.getCode())) {
			return ResultBuilder.buildResult(rule.getCode(), rule.getMessage());
		}

		/* 2：提取用户信息 */
		Optional<SysLogin> login = sysLoginRepository.findById(dto.getLoginId());
		if (login == null || !login.isPresent()) {
			// EE.logEvent("Service", "userRegister");
			return ResultBuilder.buildResult(SecurityECode.LOGINID_INVALID);
		}

		/* 3:修改用户密码 */
		login.get().setLoginPwd(dto.getNewPasswd());
		sysLoginRepository.save(login.get());

		/* 4：日志记录 */
		SysPwdHistory his = new SysPwdHistory();
		his.setLogId(dto.getLoginId());
		his.setPassword(dto.getNewPasswd());
		his.setAppId(dto.getDeviceIp());
//		his.setVersion();
		his.setDeviceIp(dto.getDeviceIp());
		pwdHistoryRepository.save(his);

		/* 5：token失效 */
		Map<String, Object> commonFields = new HashMap<>();
		commonFields.put("remoteIp", "192.168.1.1");
		// commonFields.put("addChannel", dto.getRstSource());
		commonFields.put("addProduct", "uzone");
		// commonFields.put("addVersion", dto.getAppVersion());
		commonFields.put("addUser", "system");
		boolean result = true;
		if (result) {
			return ResultBuilder.normalResult();
		} else {
			return ResultBuilder.buildResult("420000025", "密码服务异常，无法完成密码修改!");
		}
	}

	@Override
	public RstResult<Long> validateToken(ValidateTokenDTO dto) {
		// if(StringUtils.isEmpty(dto.getToken())) {
		// return
		// ResponseResultBuilder.buildErrorResponse(ErrorCode.PARAMS_AUTH_CODE);
		// }
		// if(StringUtils.isEmpty(dto.getChannel())) {
		// return
		// ResponseResultBuilder.buildErrorResponse(ErrorCode.PARAMS_CHANNEL);
		// }
		//
		// String sessionExpirySec =
		// AppSettings.getAppSetting("session_expiry_sec", "3600");
		// String key = RedisConstants.PREFIX_REDIS_CODE + dto.getToken();
		//
		// List<UserSession> list =
		// userAuthBusiness.getUserSessionBy(dto.getToken(),dto.getChannel());
		// if (null == list || list.size() <= 0) {
		// return
		// ResponseResultBuilder.buildErrorResponse(ErrorCode.ERROR_TOKEN_INVALID);
		// }
		//
		// UserSession session = list.get(0);
		// Long nowTime = UtilDataTime.getNowTime();
		// Long interval = Long.valueOf(sessionExpirySec);
		// if (nowTime - session.getUpdateTime() < interval) {
		// userAuthBusiness.updateUserSessionTime(session.getId(), nowTime);
		// } else {
		// userAuthBusiness.deleteUserSessionByCode(dto.getToken());
		// logger.debug(">>>>>>用户授权码过期!请重新登录!");
		return ResultBuilder.buildResult(ErrorCode.ERROR_TOKEN_EXPIRE);
		// }
		//
		// return
		// ResponseResultBuilder.buildNormalResponse(session.getUserId());
	}

	/**
	 * 验证登录密码规则.<br>
	 *
	 * @param password
	 * @param encrypt
	 * @return
	 */
	private RstResult<String> validatePasswordRule(String password, boolean encrypt) {
		if (StringUtils.isEmpty(password)) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_EMPTY);
		}

		if (encrypt) {
			if (password.length() != 32) {
				return ResultBuilder.buildResult(ErrorCode.PASSWORD_PATTERN);
			}
		} else {
			if (password.length() < 6) {
				return ResultBuilder.buildResult(ErrorCode.PASSWORD_LENGH);
			}
			Pattern pattern = Pattern.compile("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,10}$");
			Matcher matcher = pattern.matcher(password);
			if (!matcher.matches()) {
				return ResultBuilder.buildResult(ErrorCode.PASSWORD_PATTERN);
			}
		}
		return ResultBuilder.normalResult();
	}

	@Override
	public RstResult<String> sendRegSms(String phone) {
		return null;

		// TODO Auto-generated method stub
//		if("1".equalsIgnoreCase(isSmsFake)){
//			SendSmsDTO sms = new SendSmsDTO();
//			sms.setPhone(phone);
//			sms.setTemplateId("reg_sms_tempalte");
//			return messageService.sendSmsCaptcha(sms);
//		} else {
//			return RstResultBuilder.buildResult();
//		}
	}

	@Override
	public RstResult<String> sendLoginSms(String phone) {
		if ("1".equalsIgnoreCase(isSmsFake)) {
			return null;
		} else {
			return ResultBuilder.normalResult();
		}
	}

	@Override
	public RstResult<LoginVO> getLoginInfo(Long loginId) {
		LoginVO vo = new LoginVO();
		Optional<SysLogin> optional = sysLoginRepository.findById(loginId);
		if (optional.isPresent()) {
			vo.setNickName(optional.get().getNickName());
			vo.setUserIcon(optional.get().getIconUrl());
		}
		return ResultBuilder.normalResult(vo);
	}

	@Override
	public RstResult<Map<String, String>> getLoginProfile(String arg0) {
		return null;
	}

	@Override
	public RstResult<String> updateLoginProfile(UpdateLoginProfileDTO arg0) {
		return null;
	}

	@Override
	public RstResult<String> connectorPartyLogin(ConnectorPLoginDTO dto) {
		if (StringUtils.isBlank(dto.getLoginId()) || StringUtils.isBlank(dto.getPartyId())) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_EMPTY);
		}
		Optional<SysLogin> optional = sysLoginRepository.findById(Long.valueOf(dto.getLoginId()));
		if (!optional.isPresent()) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_ILLEGE);
		}
		optional.get().setPartyId(dto.getPartyId());
		sysLoginRepository.save(optional.get());
		return ResultBuilder.normalResult();
	}

	/**
	 * 注册登录账号(随机密码)。<br>
	 * 
	 * @param appId     应用标识。<br>
	 * @param loginName 登录账号。<br>
	 * @param version   app版本号.<br>
	 * 
	 * @return {@link SecurityBusiness#doRegister(String, String, String, String)}。<br>
	 */
	private RstResult<String> doRegister(String appId, String loginName, String version) {
		return doRegister(appId, loginName, Constant.DEFAULT_PWD, version);
	}

	/**
	 * 注册登录账号(指定密码)。<br>
	 * 
	 * @param appId     应用标识。<br>
	 * @param loginName 登录账号。<br>
	 * @param loginPwd  登录密码（明文）。<br>
	 * @param version   app版本号.<br>
	 * @return 返回用户标识（当用户标识等于0时，代表注册失败）。<br>
	 * 
	 */
	private RstResult<String> doRegister(String appId, String loginName, String loginPwd, String version) {
		logger.info("----------->正在创建用户({})登录信息!", loginName);
		SysLogin entity = new SysLogin();
//		entity.setLoginId(RandomUtils.nextLong(1, 100000));
		entity.setLoginName(loginName);
		entity.setLoginPwd(DigestUtils.md5Hex(loginPwd));
		entity.setAppId(appId);
		entity.setVersion(version);
		entity.setCreateTime(new Timestamp(System.currentTimeMillis()));// 我也不晓得为什么非要手工设置，先临时处理。
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

			return ResultBuilder.normalResult(String.valueOf(entity.getLoginId()));
		} else {
			// EE.logEvent("Service", "userRegister");
			// trans.setStatus(new BizException(ErrorCode.REGISTER_ERROR));
			// trans.complete();
			// logger.error("---------->账号注册异常:{}"+ex.getLocalizedMessage());
			return ResultBuilder.buildResult(ErrorCode.ERROR_REGISTER);
		}
	}

	@Override
	public Long getLoginIdByToken(String appId, String token) {
		return securityBusiness.getLoginIdByToken(appId, token);
	}

}
