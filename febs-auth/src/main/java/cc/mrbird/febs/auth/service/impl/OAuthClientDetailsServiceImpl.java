package cc.mrbird.febs.auth.service.impl;

import cc.mrbird.febs.auth.entity.OAuthClientDetails;
import cc.mrbird.febs.auth.mapper.OAuthClientDetailsMapper;
import cc.mrbird.febs.auth.service.OAuthClientDetailsService;
import cc.mrbird.febs.common.entity.QueryRequest;
import cc.mrbird.febs.common.exception.FebsException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Yuuki
 */
@Slf4j
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class OAuthClientDetailsServiceImpl extends ServiceImpl<OAuthClientDetailsMapper, OAuthClientDetails> implements OAuthClientDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RedisClientDetailsService redisClientDetailsService;

    @Override
    public IPage<OAuthClientDetails> findOAuthClientDetails(QueryRequest request, OAuthClientDetails oauthClientDetails) {
        LambdaQueryWrapper<OAuthClientDetails> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(oauthClientDetails.getClientId())) {
            queryWrapper.eq(OAuthClientDetails::getClientId, oauthClientDetails.getClientId());
        }
        Page<OAuthClientDetails> page = new Page<>(request.getPageNum(), request.getPageSize());
        IPage<OAuthClientDetails> result = this.page(page, queryWrapper);

        List<OAuthClientDetails> records = new ArrayList<>();
        result.getRecords().forEach(o -> {
            o.setOriginSecret(null);
            o.setClientSecret(null);
            records.add(o);
        });
        result.setRecords(records);
        return result;
    }

    @Override
    public OAuthClientDetails findById(String clientId) {
        return this.baseMapper.selectById(clientId);
    }

    @Override
    public void createOAuthClientDetails(OAuthClientDetails oauthClientDetails) throws FebsException {
        OAuthClientDetails byId = this.findById(oauthClientDetails.getClientId());
        if (byId != null) {
            throw new FebsException("该Client已存在");
        }
        oauthClientDetails.setOriginSecret(oauthClientDetails.getClientSecret());
        oauthClientDetails.setClientSecret(passwordEncoder.encode(oauthClientDetails.getClientSecret()));
        boolean saved = this.save(oauthClientDetails);
        if (saved) {
            log.info("缓存Client -> {}", oauthClientDetails);
            this.redisClientDetailsService.loadClientByClientId(oauthClientDetails.getClientId());
        }
    }

    @Override
    public void updateOAuthClientDetails(OAuthClientDetails oauthClientDetails) {
        String clientId = oauthClientDetails.getClientId();

        LambdaQueryWrapper<OAuthClientDetails> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OAuthClientDetails::getClientId, oauthClientDetails.getClientId());

        oauthClientDetails.setClientId(null);
        oauthClientDetails.setClientSecret(null);
        boolean updated = this.update(oauthClientDetails, queryWrapper);
        if (updated) {
            log.info("更新Client -> {}", oauthClientDetails);
            this.redisClientDetailsService.removeRedisCache(clientId);
            this.redisClientDetailsService.loadClientByClientId(clientId);
        }
    }

    @Override
    public void deleteOAuthClientDetails(String clientIds) {
        Object[] clientIdArray = StringUtils.splitByWholeSeparatorPreserveAllTokens(clientIds, ",");
        LambdaQueryWrapper<OAuthClientDetails> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(OAuthClientDetails::getClientId, clientIdArray);
        boolean removed = this.remove(queryWrapper);
        if (removed) {
            log.info("删除ClientId为({})的Client", clientIds);
            Arrays.stream(clientIdArray).forEach(c -> this.redisClientDetailsService.removeRedisCache(String.valueOf(c)));

        }
    }
}
