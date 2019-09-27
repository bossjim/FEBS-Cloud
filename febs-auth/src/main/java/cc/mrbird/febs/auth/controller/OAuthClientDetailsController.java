package cc.mrbird.febs.auth.controller;

import cc.mrbird.febs.auth.entity.OAuthClientDetails;
import cc.mrbird.febs.auth.service.OAuthClientDetailsService;
import cc.mrbird.febs.common.entity.FebsResponse;
import cc.mrbird.febs.common.entity.QueryRequest;
import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.common.utils.FebsUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Map;

/**
 * @author Yuuki
 */
@Slf4j
@Validated
@RestController
@RequestMapping("client")
public class OAuthClientDetailsController {

    @Autowired
    private OAuthClientDetailsService oAuthClientDetailsService;

    @GetMapping("check/{clientId}")
    public boolean checkUserName(@NotBlank(message = "{required}") @PathVariable String clientId) {
        OAuthClientDetails client = this.oAuthClientDetailsService.findById(clientId);
        return client == null;
    }

    @GetMapping("secret/{clientId}")
    @PreAuthorize("hasAnyAuthority('client:decrypt')")
    public FebsResponse getOriginClientSecret(@NotBlank(message = "{required}") @PathVariable String clientId) {
        OAuthClientDetails client = this.oAuthClientDetailsService.findById(clientId);
        String origin = client != null ? client.getOriginSecret() : StringUtils.EMPTY;
        return new FebsResponse().data(origin);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('client:view')")
    public FebsResponse oauthCliendetailsList(QueryRequest request, OAuthClientDetails oAuthClientDetails) {
        Map<String, Object> dataTable = FebsUtil.getDataTable(this.oAuthClientDetailsService.findOAuthClientDetails(request, oAuthClientDetails));
        return new FebsResponse().data(dataTable);
    }


    @PostMapping
    @PreAuthorize("hasAnyAuthority('client:add')")
    public void addOauthCliendetails(@Valid OAuthClientDetails oAuthClientDetails) throws FebsException {
        try {
            this.oAuthClientDetailsService.createOAuthClientDetails(oAuthClientDetails);
        } catch (Exception e) {
            String message = "新增客户端失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    @DeleteMapping
    @PreAuthorize("hasAnyAuthority('client:delete')")
    public void deleteOauthCliendetails(@NotBlank(message = "{required}") String clientIds) throws FebsException {
        try {
            this.oAuthClientDetailsService.deleteOAuthClientDetails(clientIds);
        } catch (Exception e) {
            String message = "删除客户端失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('client:update')")
    public void updateOauthCliendetails(@Valid OAuthClientDetails oAuthClientDetails) throws FebsException {
        try {
            this.oAuthClientDetailsService.updateOAuthClientDetails(oAuthClientDetails);
        } catch (Exception e) {
            String message = "修改客户端失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }
}
