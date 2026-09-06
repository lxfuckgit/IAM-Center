package com.iamcenter.repository.contact;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import com.iamcenter.domain.contact.HY701;
import com.javapai.framework.common.service.AbstractBizService;

@Service
public class ContactRepository extends AbstractBizService {
	/**/
	private Logger logger = LoggerFactory.getLogger(ContactRepository.class);
	
	public long createHY701(HY701 entity) {
		if(entity.getLoginId() == null) {
			logger.warn("------->user login id 为空!");
			return -1;
		}
		
		/* 适合主键自增情况下返回主键 */
		KeyHolder keyHolder = new GeneratedKeyHolder();
		final String sql="insert into hy701(consignee, geo_code, zip_code, mobile, emaill, address, login_id, sfmrdz, description, yzbm, status) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		int row = jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, entity.getConsignee());
				ps.setString(2, entity.getGeoCode());
				ps.setString(3, entity.getZipCode());
				ps.setString(4, entity.getMobile());
				ps.setString(5, entity.getEmaill());
				ps.setString(10, entity.getAddress());
				ps.setString(7, entity.getLoginId());
				ps.setString(8, entity.getIsDefault());
				ps.setString(9, entity.getDescription());
				ps.setString(10, entity.getStatus());
				return ps;
			}
		}, keyHolder);
		
		logger.info(">>>操作完毕！" + row + "条记录已受影响!");
		
		if (keyHolder.getKey() != null) {
			return keyHolder.getKey().longValue();
		} else {
			return -1;
		}
	}

	public int updateHY701(HY701 entity) {
		StringBuffer sql = new StringBuffer("update hy701 set");
		List<Object> param = new ArrayList<Object>();
		if (StringUtils.isNotEmpty(entity.getConsignee())) {
			sql.append(" consignee=?,");
			param.add(entity.getConsignee());
		}
		if (StringUtils.isNotEmpty(entity.getMobile())) {
			sql.append(" shrsj=?,");
			param.add(entity.getMobile());
		}
		// 不能改创建人
//		if (StringUtils.isNotEmpty(entity.getLoginId())) {
//			sql.append(" login_id=?,");
//			param.add(entity.getLoginId());
//		}
		if (StringUtils.isNotEmpty(entity.getAddress())) {
			sql.append(" address=?,");
			param.add(entity.getAddress());
		}
		if (StringUtils.isNotEmpty(entity.getIsDefault())) {
			sql.append(" sfmrdz=?,");
			param.add(entity.getIsDefault());
		}
		if (StringUtils.isNotEmpty(entity.getZipCode())) {
			sql.append(" yzbm=?,");
			param.add(entity.getZipCode());
		}
		if (param.size() >= 1 && sql.toString().endsWith(",")) {
			sql.delete(sql.length() - 1, sql.length());
		}
		sql.append(" where id=?");
		param.add(entity.getId());

		return jdbcTemplate.update(sql.toString(), param.toArray());
	}
	
	public HY701 getHY701(String id) {
		String sql = "select * from hy701 where id=?";
		
		/* 方式1(失败：因为queryForObject和queryForList的requiredType参数不支持自定义类型,只支持基本包装类型。) */
//		return jdbcTemplate.queryForObject(sql, new String[] { id }, ShipmentAddress.class);//说明：其实此方法是返回某表的其中一个列，参数三是返回列的基本类型。
		
		/* 方式2(成功：这种方式可以更直接的转化出我们需要的对象) */
		return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<HY701>(HY701.class), new Object[] { id });
		
		/* 方式3(成功：这种方式可以手动控制返回数据与目标对象的数据绑定格式，方便个性化或优化,可以和方式2对比下性能，估计2慢些) */
//		return jdbcTemplate.queryForObject(sql, new Object[] { addressId }, new RowMapper<ShipmentAddress>() {
//			@Override
//			public ShipmentAddress mapRow(ResultSet rs, int rowNum) throws SQLException {
//				ShipmentAddress address = new ShipmentAddress();
//				address.setAddressId(rs.getString("addressId"));
//				address.setConsignee(rs.getString("consignee"));
//				address.setGeoId(rs.getString("geoId"));
//				address.setAddress(rs.getString("addressId"));
//				address.setMobilePhone(rs.getString("mobilePhone"));
//				address.setTelephone(rs.getString("telephone"));
//				address.setEmaill(rs.getString("emaill"));
//				address.setUserLoginId(rs.getString("userLoginId"));
//				address.setIsDefault(rs.getString("isDefault"));
//				address.setDescription(rs.getString("description"));
//				return address;
//			}
//		});
	}
	
	public HY701 getDefaultHY701ByLoginId(String loginId) {
		String sql = "select * from HY701 where sfmrdz=1 and login_id=?";
		return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<HY701>(HY701.class), new Object[] { loginId });
	}
	
	public List<HY701> listHY701ByLoginId(String loginId) {
		String sql = "select * from hy701 where login_id=?";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<HY701>(HY701.class), new Object[] { loginId });
	}
	
	public boolean deleteHY701(String id) {
		int row = jdbcTemplate.update("delete from hy701 where id=?", id);
		/* 我感觉没有必要作软删除，历史的订单数据里有用户的历史收货地址 */
//		int row = jdbcTemplate.update("update hy701 set status=? where id=?", StatusEnum.DISABLE.getValue(),id);//set update_time= now()
		logger.info(">>>操作完毕！" + row + "条记录已受影响!");
		if (row > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean deleteHY701(String[] ids) {
		String sql="DELETE FROM HY701 WHERE ID=?";
		int[] rows = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int index) throws SQLException {
				// TODO Auto-generated method stub
				ps.setString(1, ids[index]);
			}
			@Override
			public int getBatchSize() {
				// TODO Auto-generated method stub
				return ids.length;
			}
		});
		
		logger.info(">>>操作完毕！" + rows + "条记录已受影响!");
		if (rows.length > 0) {
			return true;
		} else {
			return false;
		}
	}
	
//	public String createHY707(final HY707 dto) {
//		if(dto.getNoteTypeId() == null) {
//			logger.info(">>>操作出错!请确认参数(note type)完整性!!");
//			return null;
//		}
//		
//		final String id = UtilUUID.getRandomInteger();
//		final String sql="insert into hy707(, noteTypeId, createPartyId, createLoginId, noteData1, noteData2, noteData3, noteData4, noteData5) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
//		int row = jdbcTemplate.update(new PreparedStatementCreator() {
//			@Override
//			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
//				PreparedStatement ps = con.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS);
//				ps.setString(1, id);
//				ps.setString(2, dto.getNoteTypeId());
//				ps.setString(3, dto.getPartyId());
//				ps.setString(4, dto.getCreateLoginId());
//				ps.setString(5, dto.getNoteData1());
//				ps.setString(6, dto.getNoteData2());
//				ps.setString(7, dto.getNoteData3());
//				ps.setString(8, dto.getNoteData4());
//				ps.setString(9, dto.getNoteData5());
//				return ps;
//			}
//		});
//		
//		logger.info(">>>操作完毕！" + row + "条记录已受影响!");
//		return id;
//	}
	
//	public Page<NoteDateVo> findHY707(int pageIndex, int pageSize) {
//		// TODO Auto-generated method stub
//		String sql ="SELECT * FROM HY707";
//		return getPage(sql, pageIndex, pageSize, NoteDateVo.class);
//		return null;
//	}
	
//	public String updateHY707(HY707 vo) {
//		return null;
//	}

}
