package cc.mrbird.febs.auth.properties;

import lombok.Data;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

/**
 * @author MrBird
 */
@Data
@SpringBootConfiguration
@PropertySource(value = {"classpath:febs-auth.properties"})
@ConfigurationProperties(prefix = "febs.auth")
public class FebsAuthProperties {

    /**
     * 免认证访问路径
     */
    private String anonUrl;
    /**
     * 验证码配置
     */
    private FebsValidateCodeProperties code = new FebsValidateCodeProperties();
    /**
     * JWT加签密钥
     */
    private String jwtAccessKey;
    /**
     * 是否使用 JWT令牌
     */
    private Boolean enableJwt;
}
