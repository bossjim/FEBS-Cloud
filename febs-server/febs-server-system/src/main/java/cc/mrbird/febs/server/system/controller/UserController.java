package cc.mrbird.febs.server.system.controller;

import cc.mrbird.febs.common.annotation.ControllerEndpoint;
import cc.mrbird.febs.common.entity.FebsResponse;
import cc.mrbird.febs.common.entity.QueryRequest;
import cc.mrbird.febs.common.entity.system.LoginLog;
import cc.mrbird.febs.common.entity.system.SystemUser;
import cc.mrbird.febs.common.utils.FebsUtil;
import cc.mrbird.febs.server.system.service.ILoginLogService;
import cc.mrbird.febs.server.system.service.IUserService;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.wuwenze.poi.ExcelKit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author MrBird
 */
@Slf4j
@Validated
@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private IUserService userService;
    @Autowired
    private ILoginLogService loginLogService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("success/{username}")
    public void loginSuccess(@NotBlank(message = "{required}") @PathVariable String username, HttpServletRequest request) {
        // update last login time
        this.userService.updateLoginTime(username);
        // save login log
        LoginLog loginLog = new LoginLog();
        loginLog.setUsername(username);
        loginLog.setSystemBrowserInfo(request.getHeader("user-agent"));
        this.loginLogService.saveLoginLog(loginLog);
    }

    @GetMapping("index/{username}")
    public FebsResponse index(@NotBlank(message = "{required}") @PathVariable String username) {
        Map<String, Object> data = new HashMap<>();
        // 获取系统访问记录
        Long totalVisitCount = loginLogService.findTotalVisitCount();
        data.put("totalVisitCount", totalVisitCount);
        Long todayVisitCount = loginLogService.findTodayVisitCount();
        data.put("todayVisitCount", todayVisitCount);
        Long todayIp = loginLogService.findTodayIp();
        data.put("todayIp", todayIp);
        // 获取近期系统访问记录
        List<Map<String, Object>> lastTenVisitCount = loginLogService.findLastTenDaysVisitCount(null);
        data.put("lastTenVisitCount", lastTenVisitCount);
        SystemUser param = new SystemUser();
        param.setUsername(username);
        List<Map<String, Object>> lastTenUserVisitCount = loginLogService.findLastTenDaysVisitCount(param);
        data.put("lastTenUserVisitCount", lastTenUserVisitCount);
        return new FebsResponse().data(data);
    }


    @GetMapping
    @PreAuthorize("hasAnyAuthority('user:view')")
    public FebsResponse userList(QueryRequest queryRequest, SystemUser user) {
        Map<String, Object> dataTable = FebsUtil.getDataTable(userService.findUserDetail(user, queryRequest));
        return new FebsResponse().data(dataTable);
    }

    @GetMapping("check/{username}")
    public boolean checkUserName(@NotBlank(message = "{required}") @PathVariable String username) {
        return this.userService.findByName(username) == null;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('user:add')")
    @ControllerEndpoint(operation = "新增用户", exceptionMessage = "新增用户失败")
    public void addUser(@Valid SystemUser user) {
        this.userService.createUser(user);
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('user:update')")
    @ControllerEndpoint(operation = "修改用户", exceptionMessage = "修改用户失败")
    public void updateUser(@Valid SystemUser user) {
        this.userService.updateUser(user);
    }

    @DeleteMapping("/{userIds}")
    @PreAuthorize("hasAnyAuthority('user:delete')")
    @ControllerEndpoint(operation = "删除用户", exceptionMessage = "删除用户失败")
    public void deleteUsers(@NotBlank(message = "{required}") @PathVariable String userIds) {
        String[] ids = userIds.split(StringPool.COMMA);
        this.userService.deleteUsers(ids);
    }

    @PutMapping("profile")
    @ControllerEndpoint(exceptionMessage = "修改个人信息失败")
    public void updateProfile(@Valid SystemUser user) {
        this.userService.updateProfile(user);
    }

    @PutMapping("avatar")
    @ControllerEndpoint(exceptionMessage = "修改头像失败")
    public void updateAvatar(
            @NotBlank(message = "{required}") String username,
            @NotBlank(message = "{required}") String avatar) {
        this.userService.updateAvatar(username, avatar);
    }

    @GetMapping("password/check")
    public boolean checkPassword(
            @NotBlank(message = "{required}") String username,
            @NotBlank(message = "{required}") String password) {
        SystemUser user = userService.findByName(username);
        return user != null && passwordEncoder.matches(password, user.getPassword());
    }

    @PutMapping("password")
    @ControllerEndpoint(exceptionMessage = "修改密码失败")
    public void updatePassword(
            @NotBlank(message = "{required}") String username,
            @NotBlank(message = "{required}") String password) {
        userService.updatePassword(username, password);
    }

    @PutMapping("password/reset")
    @PreAuthorize("hasAnyAuthority('user:reset')")
    @ControllerEndpoint(exceptionMessage = "重置用户密码失败")
    public void resetPassword(@NotBlank(message = "{required}") String usernames) {
        String[] usernameArr = usernames.split(StringPool.COMMA);
        this.userService.resetPassword(usernameArr);
    }

    @PostMapping("excel")
    @PreAuthorize("hasAnyAuthority('user:export')")
    @ControllerEndpoint(operation = "导出用户数据", exceptionMessage = "导出Excel失败")
    public void export(QueryRequest queryRequest, SystemUser user, HttpServletResponse response) {
        List<SystemUser> users = this.userService.findUserDetail(user, queryRequest).getRecords();
        ExcelKit.$Export(SystemUser.class, response).downXlsx(users, false);
    }
}
