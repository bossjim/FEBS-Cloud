package cc.mrbird.febs.auth.service;

import cc.mrbird.febs.auth.entity.OAuthClientDetails;
import cc.mrbird.febs.common.entity.QueryRequest;
import cc.mrbird.febs.common.exception.FebsException;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author Yuuki
 */
public interface OAuthClientDetailsService extends IService<OAuthClientDetails> {

    /**
     * 查询（分页）
     *
     * @param request            QueryRequest
     * @param oauthClientDetails oauthClientDetails
     * @return IPage<OAuthClientDetails>
     */
    IPage<OAuthClientDetails> findOAuthClientDetails(QueryRequest request, OAuthClientDetails oauthClientDetails);

    /**
     * 根据主键查询
     *
     * @param clientId clientId
     * @return OAuthClientDetails
     */
    OAuthClientDetails findById(String clientId);

    /**
     * 新增
     *
     * @param oauthClientDetails oauthClientDetails
     */
    void createOAuthClientDetails(OAuthClientDetails oauthClientDetails) throws FebsException;

    /**
     * 修改
     *
     * @param oauthClientDetails oauthClientDetails
     */
    void updateOAuthClientDetails(OAuthClientDetails oauthClientDetails);

    /**
     * 删除
     *
     * @param clientIds clientIds
     */
    void deleteOAuthClientDetails(String clientIds);
}
