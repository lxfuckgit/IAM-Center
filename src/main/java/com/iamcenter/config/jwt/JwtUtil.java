package com.iamcenter.config.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtUtil {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Value("${jwt.secret}")
	private String secret;

	/**
	 * 过期时长（毫秒）；默认1小时后过期。
	 */
	@Value("${jwt.expiration:3600000}")
	private Long expiration;

	@Value("${jwt.issuer}")
	private String issuer;
	
	private SecretKey secretKey;

	@PostConstruct
	public void init() {
		// 1. 验证并创建 SecretKey
		if (secret == null || secret.trim().isEmpty()) {
			logger.error("JWT 密钥未配置！请在 application.yml 中配置 jwt.secret");
			throw new IllegalStateException("JWT 密钥未配置");
		}

		byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
		if (keyBytes.length < 32) {
			logger.error("JWT 密钥长度不足 256 位！当前: {} 位", keyBytes.length * 8);
			logger.error("请使用至少 32 个字符的密钥");
			throw new IllegalStateException("JWT 密钥长度必须 >= 256 位");
		}

		this.secretKey = Keys.hmacShaKeyFor(keyBytes);
		// 2. 打印配置信息
		logger.info("========================================");
		logger.info("JWT 配置:");
		logger.info("   - 密钥长度: {} 位", this.secretKey.getEncoded().length * 8);
		logger.info("   - expiration: {} 毫秒 ({} 分钟)", expiration, expiration / 60000);
		logger.info("   - issuer: {}", issuer);
		logger.info("========================================");

		// 3. 警告过期时间太短
		if (expiration < 60000) {
			logger.warn("JWT 过期时间只有 {} 秒，建议至少 15 分钟", expiration / 1000);
		}
	}

	/**
	 * 生成 JWT Token<br>
	 * 
	 * @param claims 自定义数据（如 userId, appId, roles, scopes 等）
	 * @return JWT 字符串
	 */
	public String generateToken(Map<String, Object> claims) {
		return generateToken(null, claims);
	}

	/**
	 * 生成 JWT Token<br>
	 * 
	 * @param claims 自定义数据（如 userId, appId, roles, scopes 等）
	 * @return JWT 字符串
	 */
	public String generateToken(String userId, Map<String, Object> claims) {
		Date now = new Date();
		// 过期时间建议 15分钟到120分钟
		Date expireDate = new Date(now.getTime() + expiration);
		if (ObjectUtils.isEmpty(userId)) {
			return Jwts.builder().claims(claims) // 自定义 claims
					.issuer(issuer) // 颁发者
					.issuedAt(now) // 签发时间（单位：秒）
					.expiration(expireDate) // 过期时间（单位：秒）
					.signWith(getSigningKey(), Jwts.SIG.HS256) // 使用 HS256 签名
					.compact();
		} else {
			return Jwts.builder().subject(userId).claims(claims).issuer(issuer).issuedAt(now).expiration(expireDate)
					.signWith(getSigningKey(), Jwts.SIG.HS256).compact();
		}
	}

	/**
	 * 解析 JWT Token
	 * 
	 * @param token JWT 字符串
	 * @return Claims 对象（包含所有自定义数据）
	 * @throws JwtException 如果 Token 无效或过期
	 */
	public Claims parseToken(String token) {
		return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
	}

	/**
	 * 验证 Token 是否有效（签名正确 + 未过期）
	 * 
	 * @param token JWT 字符串
	 * @return true-有效，false-无效
	 */
	public boolean validateToken(String token) {
		try {
			parseToken(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			// 可以在这里记录日志
			logger.error("--->token validate exception:{}", e.getMessage());
			return false;
		}
	}

	/**
	 * 从 Token 中提取主体（userId用户标识或userName用户账户 ）
	 */
	public String extractSubject(String token) {
		return parseToken(token).getSubject();
	}

	/**
	 * 从 Token 中提取自定义字段（如 appId）
	 */
	public String extractAppId(String token) {
		return parseToken(token).get("appId", String.class);
	}

	/**
	 * 获取签名密钥（从配置的 secret 字符串生成）
	 */
	private SecretKey getSigningKey() {
//		byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
//		return Keys.hmacShaKeyFor(keyBytes);
		// 直接取公共的secretKey（不用每次生成）
		return secretKey;
	}

	/**
	 * 检查 Token 是否过期（内部方法，用于自定义场景）
	 */
	public boolean isTokenExpired(String token) {
		Date expiration = parseToken(token).getExpiration();
		return expiration.before(new Date());
	}

}
