package cc.mrbird.febs.auth;

import cc.mrbird.febs.common.annotation.EnableFebsAuthExceptionHandler;
import cc.mrbird.febs.common.annotation.EnableFebsLettuceRedis;
import cc.mrbird.febs.common.annotation.EnableFebsServerProtect;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@EnableDiscoveryClient
@EnableFebsLettuceRedis
@EnableFebsAuthExceptionHandler
@EnableFebsServerProtect
@SpringBootApplication
@MapperScan("cc.mrbird.febs.auth.mapper")
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class FebsAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(FebsAuthApplication.class, args);
    }
}
