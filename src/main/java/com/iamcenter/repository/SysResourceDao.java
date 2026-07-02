package com.iamcenter.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.iamcenter.domain.security.SysResource;

public interface SysResourceDao extends JpaRepository<SysResource, String> {
	/**
	 * 根据资源编码[code]参数查询资源。<br>
	 * 
	 * @param code
	 * @return
	 */
	public SysResource findByCode(String code);
	
	/**
	 * 根据分类参数查询资源。<br>
	 * 
	 * @param type
	 * @return
	 */
	public List<SysResource> findByType(String type);
	
	public List<SysResource> findByParent(String parent);
	
	public List<SysResource> findByTypeAndName(String type, String name);
	
	/**
	 * 根据资源分类[parent]参数查询资源(按sort升序)。<br>
	 * 
	 * @param parent 父类标识。<br>
	 * @return
	 */
	//@Query(value = "select * from sys_resource where parent =? order by sort asc", nativeQuery = true)
	public List<SysResource> findByParentOrderBySortAsc(String parent);
	
	/**
	 * 根据资源分类[parent]参数查询资源(按sort降序)。<br>
	 * 
	 * @param parent 父类标识。<br>
	 * @return
	 */
	//@Query(value = "select * from sys_resource where parent =? order by sort desc", nativeQuery = true)
	public List<SysResource> findByParentOrderBySortDesc(String parent);
	
	/**
	 * 根据资源分类[type]参数查询资源(默认按sort升序)。<br>
	 * 
	 * @param type 资源分类。<br>
	 * @return
	 */
	@Query(value = "select * from sys_resource where type =? order by sort", nativeQuery = true)
	public List<SysResource> selectResource(String type);
	
}
