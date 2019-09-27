package cc.mrbird.febs.server.system.controller;

import cc.mrbird.febs.common.annotation.ControllerEndpoint;
import cc.mrbird.febs.common.entity.FebsResponse;
import cc.mrbird.febs.common.entity.QueryRequest;
import cc.mrbird.febs.common.entity.system.LoginLog;
import cc.mrbird.febs.common.utils.FebsUtil;
import cc.mrbird.febs.server.system.service.ILoginLogService;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.wuwenze.poi.ExcelKit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

/**
 * @author MrBird
 */
@Slf4j
@RestController
@RequestMapping("loginLog")
public class LoginLogController {

    @Autowired
    private ILoginLogService loginLogService;

    @GetMapping
    public FebsResponse loginLogList(LoginLog loginLog, QueryRequest request) {
        Map<String, Object> dataTable = FebsUtil.getDataTable(this.loginLogService.findLoginLogs(loginLog, request));
        return new FebsResponse().data(dataTable);
    }

    @GetMapping("/{username}")
    public FebsResponse getUserLastSevenLoginLogs(@NotBlank(message = "{required}") @PathVariable String username) {
        List<LoginLog> userLastSevenLoginLogs = this.loginLogService.findUserLastSevenLoginLogs(username);
        return new FebsResponse().data(userLastSevenLoginLogs);
    }

    @DeleteMapping("{ids}")
    @PreAuthorize("hasAnyAuthority('loginlog:delete')")
    @ControllerEndpoint(operation = "删除登录日志", exceptionMessage = "删除登录日志失败")
    public void deleteLogss(@NotBlank(message = "{required}") @PathVariable String ids) {
        String[] loginLogIds = ids.split(StringPool.COMMA);
        this.loginLogService.deleteLoginLogs(loginLogIds);
    }

    @PostMapping("excel")
    @PreAuthorize("hasAnyAuthority('loginlog:export')")
    @ControllerEndpoint(operation = "导出登录日志数据", exceptionMessage = "导出Excel失败")
    public void export(QueryRequest request, LoginLog loginLog, HttpServletResponse response) {
        List<LoginLog> loginLogs = this.loginLogService.findLoginLogs(loginLog, request).getRecords();
        ExcelKit.$Export(LoginLog.class, response).downXlsx(loginLogs, false);
    }
}
