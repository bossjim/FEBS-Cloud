package cc.mrbird.febs.server.system.controller;

import cc.mrbird.febs.common.annotation.ControllerEndpoint;
import cc.mrbird.febs.common.entity.FebsResponse;
import cc.mrbird.febs.common.entity.QueryRequest;
import cc.mrbird.febs.common.entity.system.Dept;
import cc.mrbird.febs.server.system.service.IDeptService;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.wuwenze.poi.ExcelKit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequestMapping("dept")
public class DeptController {

    @Autowired
    private IDeptService deptService;

    @GetMapping
    public FebsResponse deptList(QueryRequest request, Dept dept) {
        Map<String, Object> depts = this.deptService.findDepts(request, dept);
        return new FebsResponse().data(depts);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('dept:add')")
    @ControllerEndpoint(operation = "新增部门", exceptionMessage = "新增部门失败")
    public void addDept(@Valid Dept dept) {
        this.deptService.createDept(dept);
    }

    @DeleteMapping("/{deptIds}")
    @PreAuthorize("hasAnyAuthority('dept:delete')")
    @ControllerEndpoint(operation = "删除部门", exceptionMessage = "删除部门失败")
    public void deleteDepts(@NotBlank(message = "{required}") @PathVariable String deptIds) {
        String[] ids = deptIds.split(StringPool.COMMA);
        this.deptService.deleteDepts(ids);
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('dept:update')")
    @ControllerEndpoint(operation = "修改部门", exceptionMessage = "修改部门失败")
    public void updateDept(@Valid Dept dept) {
        this.deptService.updateDept(dept);
    }

    @PostMapping("excel")
    @PreAuthorize("hasAnyAuthority('dept:export')")
    @ControllerEndpoint(operation = "导出部门数据", exceptionMessage = "导出Excel失败")
    public void export(Dept dept, QueryRequest request, HttpServletResponse response) {
        List<Dept> depts = this.deptService.findDepts(dept, request);
        ExcelKit.$Export(Dept.class, response).downXlsx(depts, false);
    }
}
