package com.iamcenter.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.iamcenter.domain.security.SysRole;
import com.iamcenter.repository.SysLoginRepository;
import com.iamcenter.repository.SysLoginRoleDao;
import com.iamcenter.repository.SysRoleRepository;
import com.javapai.framework.common.service.AbstractBizService;
import com.saasapi.contract.security.vo.MenuVO;
import com.saasapi.contract.security.vo.PrivilegeVO;

@Component
public class RBACBusiness extends AbstractBizService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private SysLoginRepository sysLoginRepository;
	
	@Autowired
	private SysLoginRoleDao sysLoginRoleDao;
	
	@Autowired
	private SysRoleRepository sysRoleRepository;
	
	@Transactional
	public boolean restoreLoginRoles(String appId, Long loginId, List<Long> roleIdList) {
		// 撤销原角色
		int deleteSize = jdbcTemplate.update("delete from sys_login_role where login_id=?", loginId);
		logger.info("--->[{}]账户角色撤销：{}", loginId, deleteSize);

		// 授予新角色
		List<Object[]> batchArgs = new ArrayList<>();
		for (Long roleId : roleIdList) {
			batchArgs.add(new Object[] { loginId, roleId });
		}
		String insertSQL = "insert into sys_login_role (login_id, role_id) values (?, ?)";
		int[] updateSize = jdbcTemplate.batchUpdate(insertSQL, batchArgs);
		logger.info("--->[{}]账户角色授予：{}", loginId, updateSize);

		return true;
	}
	
	@Transactional
	public boolean restoreRoleResourceList(String appId, Long roleId, List<Long> resIdList) {
		// 撤销原权限
		int deleteSize = jdbcTemplate.update("delete from sys_role_resource where role_id=?", roleId);
		logger.info("--->[{}]角色权限撤销：{}", roleId, deleteSize);

		// 授予新权限
		List<Object[]> batchArgs = new ArrayList<>();
		for (Long resId : resIdList) {
			batchArgs.add(new Object[] { roleId, resId });
		}
		String insertSQL = "insert into sys_role_resource (role_id, resource_id) values (?, ?)";
		int[] updateSize = jdbcTemplate.batchUpdate(insertSQL, batchArgs);
		logger.info("--->[{}]角色权限授予：{}", roleId, updateSize);
		
		return true;
	}
	
	public List<PrivilegeVO> listRoleResource(Long roleId) {
		String sql = "select b.code,b.name from sys_role_resource a left join sys_resource b on a.id=b.id where a.role_id=?";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<PrivilegeVO>(PrivilegeVO.class), new Object[] { roleId });
	}
	
	@Cacheable(value = "listRoleResourceCode", key = "#roleId")
	public List<String> listRoleResourceCode(Long roleId) {
		return sysRoleRepository.listRoleResourceCode(roleId);
//		return jdbcTemplate.queryforObject(sql, String.class, new Object[] { roleId });
	}
	
	public List<PrivilegeVO> listRoleResource(String appId, Long roleId) {
		String sql = "select b.code,b.name from sys_role_resource a left join sys_resource b on a.id=b.id where a.role_id=? and b.app_id=?";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<PrivilegeVO>(PrivilegeVO.class), new Object[] { roleId, appId });
	}
	
	public List<MenuVO> listLoginMenu(Long loginId) {
		// 备注：这里直接写SQL是觉得jdbcTemplate比jpaRepository性能高。
		/* 1、查询【登录用户】关联【角色】的（有效）菜单数据 */
		StringBuffer sb = new StringBuffer();
		sb.append("select b.id as menu_id,b.res_code,b.res_name as menu_name,b.res_url as menu_path,b.res_icon as menu_icon,b.parent_id ");
		sb.append("from sys_role_resource a left join sys_resource b on a.resource_id=b.id ");
		sb.append("where a.role_id in(select role_id from sys_login_role where login_id=?) and b.res_type='Menu' and b.status_id='ENABLE' ");
		sb.append("order by parent_id,sequence");
		List<MenuVO> menuList = jdbcTemplate.query(sb.toString(), new BeanPropertyRowMapper<MenuVO>(MenuVO.class), loginId);

		/* 2、将扁平的【菜单列表】构建为【菜单树】结构 */
		return buildMenuTree(menuList, 0L);
	}
	
	@Cacheable(value = "listLoginRole", key = "#loginId")
	public List<SysRole> listLoginRole(Long loginId) {
		// 优先读缓存再读本地库
		// 提示：sysLoginRoleDao.listLoginRole(loginId)只能返回List<Object[]>我嫌麻烦就用jdbcTemplate写；
		String sql = "select b.* from sys_login_role a left join sys_role b on a.role_id=b.id where a.login_id=?";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<SysRole>(SysRole.class), loginId);
	}
	
	@Cacheable(value = "listLoginRoleCode", key = "#loginId")
	public List<String> listLoginRoleCode(Long loginId) {
		// 优先读缓存再读本地库
		return sysLoginRoleDao.listRoleCodeByLoginId(loginId);
	}
	
	/**
	 * 删除登录账户的所有信息。
	 * 
	 * @param loginId
	 * @return
	 */
	public boolean deleteAllByLoginId(Long loginId) {
		sysLoginRepository.deleteById(loginId);
		sysLoginRoleDao.deleteByLoginId(loginId);
		logger.info("--->提示：登录[{}]信息删除完毕！", loginId);
		return true;
	}
	
	/**
	 * 将扁平的菜单列表组装成树。 前提：入参列表已（SQL 已保证）。
	 *
	 * @param menuList 扁平菜单列表
	 * @param rootId   根节点的 parentId，通常为 0L
	 * @return 树形菜单列表
	 */
	private List<MenuVO> buildMenuTree(List<MenuVO> menuList, Long rootId) {
		if (menuList == null || menuList.isEmpty()) {
			return Collections.emptyList();
		}
		// 1. 建立 id -> Menu 映射。用 LinkedHashMap 保持插入顺序（即 SQL 排序后的顺序）
		Map<Long, MenuVO> menuMap = new LinkedHashMap<Long, MenuVO>(menuList.size() * 2);
		for (MenuVO menu : menuList) {
			// 防御：清空可能残留的 children，避免重复组装时叠加
			// menu.setChildList(new ArrayList<MenuVO>());
			menuMap.put(menu.getMenuId(), menu);
		}

		// 2. 一次遍历组装父子关系
		List<MenuVO> roots = new ArrayList<>();
		for (MenuVO menu : menuList) {
			Long parentId = menu.getParentId();
			if (parentId == null || parentId.equals(rootId)) {
				roots.add(menu);
				continue;
			}
			MenuVO parent = menuMap.get(parentId);
			if (parent != null) {
				parent.getChildList().add(menu);
			} else {
				// 父节点不存在（数据异常/父节点被删），按根节点处理，避免丢数据
				roots.add(menu);
			}
		}
		// 3. 如果 SQL 没有按 parent_id, sequence 排序，可在此处开启兜底排序（可选）
		// sortRecursively(roots);
		return roots;
	}

	/**
	 * 兜底排序：当 SQL 无法保证顺序时使用。 会递归对每一层按 sort 升序排序。
	 */
//	private static void sortRecursively(List<MenuVO> menus) {
//		if (menus == null || menus.isEmpty()) {
//			return;
//		}
//		menus.sort((a, b) -> {
//			int sa = a.getSort() == null ? 0 : a.getSort();
//			int sb = b.getSort() == null ? 0 : b.getSort();
//			return Integer.compare(sa, sb);
//		});
//		for (Menu menu : menus) {
//			sortRecursively(menu.getChildren());
//		}
//	}

}
