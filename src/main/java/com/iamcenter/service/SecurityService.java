package com.iamcenter.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import com.iamcenter.business.RBACBusiness;
import com.iamcenter.config.jwt.JwtUtil;
import com.iamcenter.domain.security.SysLogin;
import com.iamcenter.domain.security.SysLoginRole;
import com.iamcenter.domain.security.SysResource;
import com.iamcenter.domain.security.SysRole;
import com.iamcenter.repository.SysLoginRepository;
import com.iamcenter.repository.SysLoginRoleDao;
import com.iamcenter.repository.SysResourceDao;
import com.iamcenter.repository.SysRoleRepository;
import com.javapai.framework.action.PageResult;
import com.javapai.framework.action.ResultBuilder;
import com.javapai.framework.action.RstResult;
import com.javapai.framework.enums.ErrorCode;
import com.javapai.framework.utils.UtilDateTime;
import com.saasapi.contract.security.SecurityContract;
import com.saasapi.contract.security.dto.LoginRoleDTO;
import com.saasapi.contract.security.dto.MenuDTO;
import com.saasapi.contract.security.dto.ResourceCreateDTO;
import com.saasapi.contract.security.dto.ResourceDeleteDTO;
import com.saasapi.contract.security.dto.ResourceListDTO;
import com.saasapi.contract.security.dto.ResourceUpdateDTO;
import com.saasapi.contract.security.dto.RoleDTO;
import com.saasapi.contract.security.dto.RoleDeleteDTO;
import com.saasapi.contract.security.dto.RoleListDTO;
import com.saasapi.contract.security.dto.RoleUpdateDTO;
import com.saasapi.contract.security.enums.ResEnum;
import com.saasapi.contract.security.enums.SecurityECode;
import com.saasapi.contract.security.vo.LoginRoleVO;
import com.saasapi.contract.security.vo.LoginVO;
import com.saasapi.contract.security.vo.MenuVO;
import com.saasapi.contract.security.vo.PrivilegeVO;
import com.saasapi.contract.security.vo.ResourceVO;
import com.saasapi.contract.security.vo.RoleVO;

/**
 * 权限管理[SecurityService] 服务层接口.<br>
 * <p>
 * 
 * 权限相关基础信息服务。
 * 
 * @author lx
 * 
 */
@DubboService
public class SecurityService implements SecurityContract {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private SysResourceDao resourceDao;
	
	@Autowired
	private SysRoleRepository roleRepository;
	
	@Autowired
	private SysLoginRepository loginRepository;
	
	@Autowired
	private SysLoginRoleDao loginRoleRepository;
	
	@Autowired
	RBACBusiness rbacBusiness;
	
	@Autowired
	protected JdbcTemplate jdbcTemplate;
	
	@Autowired
	protected JwtUtil jwtUtil;
	
	@Override
	public RstResult<String> createMenu(MenuDTO dto) {
		//因为菜单的特殊性，不仅验证了code的唯一性，还验证了类型和名称不能重复。
		SysResource menu = resourceDao.findByCode(dto.getCode());
		if (null != menu) {
			logger.warn("------>菜单编码:{}对应的菜单名称({})已存在,请确认!", dto.getCode(), menu.getName());
			return ResultBuilder.buildResult(ErrorCode.EXIST_CODE);
		}
		List<SysResource> list = resourceDao.findByTypeAndName(ResEnum.Menu.name(), dto.getName());
		if (null == list || list.size()==0 ) {
			SysResource entity = new SysResource();
			BeanUtils.copyProperties(dto, entity);
			entity.setType(ResEnum.Menu.name());
			resourceDao.save(entity);
			logger.warn("------>菜单编码:{}对应的菜单名称({})已存在,请确认!", dto.getCode(), dto.getName());
			return ResultBuilder.normalResult();
		} else {
			logger.warn("------>菜单名称({})已存在,请确认!", dto.getName());
			return ResultBuilder.buildResult(ErrorCode.EXIST_NAME);
		}
	}

	@Override
	public RstResult<String> updateMenu(MenuVO dto) {
		if (StringUtils.isBlank(dto.getId())) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_EMPTY);
		}
		// 检查menu存在性。
		Optional<SysResource> optional = resourceDao.findById(dto.getId());
		if (!optional.isPresent()) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_EMPTY);
		}
		if (StringUtils.isNotBlank(dto.getName())) {
			optional.get().setName(dto.getName());
		}
		if (StringUtils.isNotBlank(dto.getIcon())) {
			optional.get().setIcon(dto.getIcon());
		}
		if (StringUtils.isNotBlank(dto.getUrl())) {
			optional.get().setUrl(dto.getUrl());
		}
		if (StringUtils.isNotBlank(dto.getParent())) {
			optional.get().setUrl(dto.getParent());
		}
		resourceDao.save(optional.get());
		return ResultBuilder.normalResult();
	}

	@Override
	public RstResult<String> deleteMenu(String menuId) {
		if(StringUtils.isBlank(menuId)) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_EMPTY);
		}
		//检查menu存在性。
		Optional<SysResource> optional = resourceDao.findById(menuId);
		if(!optional.isPresent()) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_EMPTY);
		}
		//检查menu是否有子节点。
		List<SysResource> childList = resourceDao.findByParent(menuId);
		if (childList.size() > 0) {
			return ResultBuilder.buildResult(ErrorCode.EXCEPTION_DELETE, "当前菜单存在子结点，无法删除！");
		}
		//删除menu节点。
		resourceDao.delete(optional.get());
		return ResultBuilder.normalResult();
	}
	
	@Override
	public RstResult<ResourceVO> getResource(String resourceId) {
		if (StringUtils.isBlank(resourceId)) {
			return ResultBuilder.buildResult(ErrorCode.INVALID_ID);
		}
		Optional<SysResource> optional = resourceDao.findById(resourceId);
		if (!optional.isPresent()) {
			return ResultBuilder.buildResult(ErrorCode.INVALID_ID);
		}
		ResourceVO vo = new ResourceVO();
		BeanUtils.copyProperties(optional.get(), vo);
		return ResultBuilder.normalResult(vo);
	}
	
	@Override
	public RstResult<String> createResource(ResourceCreateDTO dto) {
		if (StringUtils.isEmpty(dto.getAppId())) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_APPID);
		}
		if (StringUtils.isBlank(dto.getResType())) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_EMPTY, "资源类型未指定！");
		}
		if (StringUtils.isBlank(dto.getResCode()) || StringUtils.isBlank(dto.getResName())) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_EMPTY);
		}
		SysResource priviliege = resourceDao.findByCode(dto.getResCode());
		if (null != priviliege) {
			logger.warn("------>权限编码:{}对应的权限名({})已存在,请确认!", dto.getResCode(), priviliege.getName());
			return ResultBuilder.buildResult(SecurityECode.PRIVILIGE_EXISIT);
		}

		SysResource entity = new SysResource();
		entity.setAppId(dto.getAppId());
		entity.setType(dto.getResType());
		entity.setCode(dto.getResCode());
		entity.setName(dto.getResName());
		entity.setUrl(dto.getResUrl());
		entity.setRemark(dto.getResRemark());
		resourceDao.save(entity);
		return ResultBuilder.normalResult();
	}

	@Override
	public RstResult<String> deleteResource(ResourceDeleteDTO dto) {
		if (StringUtils.isBlank(dto.getResId())) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_EMPTY);
		}
		if (StringUtils.isEmpty(dto.getAppId())) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_APPID);
		}
		Optional<SysResource> optional = resourceDao.findById(dto.getResId());
		if (!optional.isPresent() || !optional.get().getAppId().equals(dto.getAppId())) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_EMPTY);
		}
		List<SysResource> childList = resourceDao.findByParent(dto.getResId());
		if (null != childList && childList.size() > 0) {
			return ResultBuilder.buildResult(ErrorCode.EXCEPTION_DELETE, "当前资源存在子节点，无法删除！");
		}
		resourceDao.delete(optional.get());
		logger.info("--->[{}]资源信息已删除！", dto.getResId());
		return ResultBuilder.normalResult();
	}

	@Override
	public RstResult<String> updateResource(ResourceUpdateDTO dto) {
		if (StringUtils.isBlank(dto.getResId())) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_EMPTY);
		}
		if (StringUtils.isEmpty(dto.getAppId())) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_APPID);
		}
		Optional<SysResource> optional = resourceDao.findById(dto.getResId());
		if (!optional.isPresent() || !optional.get().getAppId().equals(dto.getAppId())) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_EMPTY);
		}
		if (StringUtils.isNotBlank(dto.getResName())) {
			optional.get().setName(dto.getResName());
		}
		// code不能更新（怕有地方引用）
//		if (StringUtils.isNotBlank(dto.getResCode())) {
//			optional.get().setCode(dto.getResCode());
//		}
//		if (StringUtils.isNotBlank(dto.getIcon())) {
//			optional.get().setIcon(dto.getIcon());
//		}
		if (StringUtils.isNotBlank(dto.getResUrl())) {
			optional.get().setUrl(dto.getResUrl());
		}
//		if (StringUtils.isNotBlank(dto.getParent())) {
//			optional.get().setParent(dto.getParent());
//		}
//		if (dto.getSort() != null) {
//			optional.get().setSort(dto.getSort());
//		}
		if (StringUtils.isNotBlank(dto.getResRemark())) {
			optional.get().setRemark(dto.getResRemark());
		}
		resourceDao.save(optional.get());
		logger.info("--->[{}]资源信息已更新！", dto.getResId());
		return ResultBuilder.normalResult();
	}
	
	@Override
	public PageResult<ResourceVO> listResource(ResourceListDTO dto) {
		return rbacBusiness.listResource(dto);
	}
	
	@Override
	public RstResult<List<ResourceVO>> findChildResource(String parentId) {
		if (StringUtils.isBlank(parentId)) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_EMPTY);
		}

		return ResultBuilder.normalResult(resourceDao.findByParentOrderBySortAsc(parentId).stream().map(mapper -> {
			ResourceVO vo = new ResourceVO();
			BeanUtils.copyProperties(mapper, vo);
			return vo;
		}).collect(Collectors.toList()));
	}
	
	@Override
	public RstResult<String> addRole(RoleDTO dto) {
		if (StringUtils.isEmpty(dto.getAppId())) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_APPID);
		}
		if(StringUtils.isEmpty(SecurityContextHolder.getContext().getAuthentication().getName())) {
			return ResultBuilder.buildResult(ErrorCode.INVALID_TOKEN);
		}
		if (StringUtils.isBlank(dto.getRoleCode()) && StringUtils.isBlank(dto.getRoleName())) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_EMPTY);
		}

		SysRole role = roleRepository.findByAppIdAndCode(dto.getAppId(), dto.getRoleCode());
		if (null != role) {
			logger.warn("------>角色编码:{}对应的角色名({})已存在,请确认!", role.getCode(), role.getName());
			return ResultBuilder.buildResult(SecurityECode.ROLE_EXISIT);
		}
		
		SysRole entity = new SysRole();
		entity.setAppId(dto.getAppId());
		entity.setCode(dto.getRoleCode());
		entity.setName(dto.getRoleName());
		entity.setCreatorId(SecurityContextHolder.getContext().getAuthentication().getName());
		entity.setRemark(dto.getRoleRemark());
		roleRepository.save(entity);
		return ResultBuilder.normalResult();
	}
	
	@Override
	public RstResult<String> deleteRole(RoleDeleteDTO dto) {
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		if(StringUtils.isEmpty(userId)) {
			return ResultBuilder.buildResult(ErrorCode.INVALID_TOKEN);
		}
		Optional<SysRole> optional = roleRepository.findById(dto.getRoleId());
		if (!optional.isPresent() || !optional.get().getCreatorId().equals(userId)) {
			return ResultBuilder.buildResult("40000002", "当前角色个人不存在!");
		}
		List<SysLoginRole> roleList = loginRoleRepository.findByRoleId(dto.getRoleId());
		if (null != roleList && roleList.size() > 0) {
			return ResultBuilder.buildResult("40000002", "当前角色存在引用关系无法删除!");
		}
		/* 删除角色 */
		roleRepository.delete(optional.get());
		logger.info("--->[{}]角色信息已删除！", dto.getRoleId());
		return ResultBuilder.normalResult();
	}
	
	@Override
	public RstResult<String> updateRole(RoleUpdateDTO dto) {
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		if(StringUtils.isEmpty(userId)) {
			return ResultBuilder.buildResult(ErrorCode.INVALID_TOKEN);
		}
		if (StringUtils.isEmpty(dto.getAppId())) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_APPID);
		}
		Optional<SysRole> optional = roleRepository.findById(dto.getRoleId());
		if (!optional.isPresent() || !optional.get().getAppId().equals(dto.getAppId())) {
			return ResultBuilder.buildResult("40000002", "当前角色信息不存在!");
		}
		/* 修改角色 */
		if (StringUtils.isNotBlank(dto.getRoleName())) {
			optional.get().setName(dto.getRoleName());
		}
		if (StringUtils.isNotBlank(dto.getRoleRemark())) {
			optional.get().setRemark(dto.getRoleRemark());
		}
		optional.get().setUpdateId(userId);
		optional.get().setUpdateTime(UtilDateTime.currentTimestamp());
		roleRepository.save(optional.get());
		logger.info("--->[{}]角色信息已更新！", dto.getRoleId());
		return ResultBuilder.normalResult();
	}

	@Override
	public PageResult<RoleVO> listRole(RoleListDTO dto) {
		if (StringUtils.isEmpty(dto.getAppId())) {
			return ResultBuilder.buildPageResult(dto.getPageIndex(), dto.getPageSize());
		}
		return rbacBusiness.listRole(dto);
	}
	
	/**
	 * 资源/模块列表信息
	 * 
	 * @return
	 */
	public List<SysResource> listResource(){
		return null;
	};

	/**
	 * 保存模块
	 * 
	 * @param role
	 */
	public void addResource(SysResource resource){
		
	};
	
	/**
	 * 根据Username 获取 SysLogin
	 * @param loginname
	 * @return
	 */
//	public SysLogin getSysLoginByLoginName(String loginname);
	
	@Override
	public RstResult<List<LoginVO>> listLogin() {
		return ResultBuilder.normalResult(loginRepository.findAll().stream().map(mapper -> {
			LoginVO vo = new LoginVO();
			vo.setLoginName(mapper.getLoginName());
			vo.setStatus(mapper.getLoginState());
			return vo;
		}).collect(Collectors.toList()));
	}
	
	@Override
	public RstResult<LoginVO> getLoginByLoginName(String loginName) {
		return null;
	}
	
	@Override
	public List<LoginRoleVO> listLoginRole(Long loginId) {
//		return loginRoleRepository.findAll().stream().map(mapper -> {
//			LoginRoleVO vo = new LoginRoleVO();
//			vo.setLoginId(String.valueOf(mapper.getLoginId()));
//			vo.setRoleId(mapper.getRoleId());
//			return vo;
//		}).collect(Collectors.toList());
		return loginRoleRepository.listLoginRole(loginId).stream().map(mapper -> {
			LoginRoleVO vo = new LoginRoleVO();
			vo.setRoleId(String.valueOf(mapper[0]));
			vo.setRoleCode(String.valueOf(mapper[1]));
			vo.setRoleName(String.valueOf(mapper[2]));
			return vo;
		}).collect(Collectors.toList());
	}

	@Override
	public List<LoginRoleVO> listLoginRole(String loginName) {
		SysLogin login = loginRepository.findByLoginName(loginName);
		if (null != login) {
			return listLoginRole(login.getLoginId());
		} else {
			return null;
		}
	}

	@Override
	public RstResult<Boolean> addLoginRole(LoginRoleDTO dto) {
		return addLoginRole(dto.getAppId(), dto.getLoginId(), dto.getRoleId());
	}
	
	@Override
	public RstResult<Boolean> addLoginRole(String appId, Long loginId, Long roleId) {
		if (StringUtils.isEmpty(appId)) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_APPID);
		}
		Optional<SysRole> roleOptional = roleRepository.findById(roleId);
		if (!roleOptional.isPresent() || !roleOptional.get().getAppId().equals(appId)) {
			return ResultBuilder.buildResult(SecurityECode.ROLE_INVALID);
		}
		Optional<SysLogin> loginOptional = loginRepository.findById(loginId);
		if (!loginOptional.isPresent() || !loginOptional.get().getAppId().equals(appId)) {
			return ResultBuilder.buildResult(SecurityECode.USERID_INVALID);
		}
		
		loginRoleRepository.save(new SysLoginRole(loginId, roleId));
		logger.info("--->用户[{}]添加角色[{}]完成！", loginOptional.get().getLoginName(), roleOptional.get().getName());
		return ResultBuilder.normalResult();
	}
	
	@Override
	public RstResult<Boolean> addLoginRole(String loginName, Long roleId) {
		return null;
	}
	
	@Override
	public RstResult<Boolean> removeLoginRole(LoginRoleDTO dto) {
		return removeLoginRole(dto.getAppId(), dto.getLoginId(), dto.getRoleId());
	}
	
	@Override
	@Transactional
	public RstResult<Boolean> removeLoginRole(String appId, Long loginId, Long roleId) {
		if (StringUtils.isEmpty(appId)) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_APPID);
		}
		Optional<SysRole> roleOptional = roleRepository.findById(roleId);
		if (!roleOptional.isPresent() || !roleOptional.get().getAppId().equals(appId)) {
			return ResultBuilder.buildResult(SecurityECode.ROLE_INVALID);
		}
		Optional<SysLogin> loginOptional = loginRepository.findById(loginId);
		if (!loginOptional.isPresent() || !loginOptional.get().getAppId().equals(appId)) {
			return ResultBuilder.buildResult(SecurityECode.USERID_INVALID);
		}
		loginRoleRepository.deleteByLoginIdAndRoleId(loginId, roleId);
		logger.info("--->用户[{}]移除角色[{}]完成！", loginId, roleId);
		return ResultBuilder.normalResult();
	}

	@Override
	public RstResult<Boolean> addRolePrivilege(Long roleId, String privilegeId) {
		Optional<SysRole> roleOptional = roleRepository.findById(roleId);
		Optional<SysResource> resourceOptional = resourceDao.findById(privilegeId);
		if(roleOptional.isPresent() && resourceOptional.isPresent()) {
			roleOptional.get().getResources().add(resourceOptional.get());
			roleRepository.save(roleOptional.get());
			return ResultBuilder.normalResult();
		} else {
			return ResultBuilder.buildResult(ErrorCode.EXCEPTION_CREATE);
		}
	}

	@Override
	public RstResult<Boolean> removeRolePrivilege(Long roleId, String privilegeId) {
		Optional<SysRole> roleOptional = roleRepository.findById(Long.valueOf(roleId));
		Optional<SysResource> resourceOptional = resourceDao.findById(privilegeId);
		if (roleOptional.isPresent() && resourceOptional.isPresent()) {
			roleOptional.get().getResources().remove(resourceOptional.get());
			roleRepository.save(roleOptional.get());
			return ResultBuilder.normalResult();
		} else {
			return ResultBuilder.buildResult(ErrorCode.EXCEPTION_DELETE);
		}
	}

	@Override
	public RstResult<List<PrivilegeVO>> listRolePrivilege(String roleId) {
		return ResultBuilder.normalResult(selectPrvilegeByRoleId(roleId));
	}

	@Override
	public RstResult<List<PrivilegeVO>> listLoginPrivilege(Long loginId) {
		List<PrivilegeVO> list = new ArrayList<PrivilegeVO>();
		listLoginRole(loginId).forEach(action -> {
			list.addAll(selectPrvilegeByRoleId(action.getRoleId()));
		});
		return ResultBuilder.normalResult(list);
	}

	@Override
	public RstResult<List<PrivilegeVO>> listLoginPrivilege(String loginName) {
		List<PrivilegeVO> list = new ArrayList<PrivilegeVO>();
		listLoginRole(loginName).forEach(action -> {
			//list.addAll(selectPrvilegeByRoleId(action.getRoleId()));
		});

		return ResultBuilder.normalResult(list);
	}
	
	private List<PrivilegeVO> selectPrvilegeByRoleId(String roleId) {
		String sql = "select b.code,b.name from sys_role_privilege a left join sys_resource b on a.id=b.id where a.role_id=?";
		return jdbcTemplate.query(sql, new String[] { roleId }, new BeanPropertyRowMapper<PrivilegeVO>(PrivilegeVO.class));
	}

}
