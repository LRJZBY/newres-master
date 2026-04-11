package com.newsrec.manage.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.newsrec.common.annotation.Log;
import com.newsrec.common.core.controller.BaseController;
import com.newsrec.common.core.domain.AjaxResult;
import com.newsrec.common.enums.BusinessType;
import com.newsrec.manage.domain.UserPreferences;
import com.newsrec.manage.service.IUserPreferencesService;
import com.newsrec.common.utils.poi.ExcelUtil;
import com.newsrec.common.core.page.TableDataInfo;

/**
 * 用户偏好Controller
 *
 * @author weiyan
 * @date 2025-03-31
 */
@RestController
@RequestMapping("/manage/preferences")
public class UserPreferencesController extends BaseController {
    @Autowired
    private IUserPreferencesService userPreferencesService;


    /**
     * 查询用户偏好列表
     */
    @GetMapping("/list")
    public TableDataInfo list(UserPreferences userPreferences) {
        startPage();
        List<UserPreferences> list = userPreferencesService.selectUserPreferencesList(userPreferences);
        return getDataTable(list);
    }

    /**
     * 导出用户偏好列表
     */
    @Log(title = "用户偏好", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, UserPreferences userPreferences) {
        List<UserPreferences> list = userPreferencesService.selectUserPreferencesList(userPreferences);
        ExcelUtil<UserPreferences> util = new ExcelUtil<UserPreferences>(UserPreferences.class);
        util.exportExcel(response, list, "用户偏好数据");
    }

    /**
     * 获取用户偏好详细信息
     */
    @GetMapping(value = "/{preferenceId}")
    public AjaxResult getInfo(@PathVariable("preferenceId") Long preferenceId) {
        return success(userPreferencesService.selectUserPreferencesByPreferenceId(preferenceId));
    }

    /**
     * 新增用户偏好
     */
    @Log(title = "用户偏好", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody UserPreferences userPreferences) {
        return toAjax(userPreferencesService.insertUserPreferences(userPreferences));
    }

    /**
     * 修改用户偏好
     */
    @Log(title = "用户偏好", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody UserPreferences userPreferences) {
        return toAjax(userPreferencesService.updateUserPreferences(userPreferences));
    }

    /**
     * 删除用户偏好
     */
    @Log(title = "用户偏好", businessType = BusinessType.DELETE)
    @DeleteMapping("/{preferenceIds}")
    public AjaxResult remove(@PathVariable Long[] preferenceIds) {
        return toAjax(userPreferencesService.deleteUserPreferencesByPreferenceIds(preferenceIds));
    }
}
