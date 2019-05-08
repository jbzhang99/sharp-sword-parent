package com.dili.ss.activiti.controller;

import com.dili.ss.activiti.domain.ActForm;
import com.dili.ss.activiti.service.ActFormService;
import com.dili.ss.activiti.domain.ActForm;
import com.dili.ss.activiti.service.ActFormService;
import com.dili.ss.domain.BaseOutput;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2019-03-21 16:02:46.
 */
@Api("/actForm")
@Controller
@RequestMapping("/actForm")
public class ActFormController {
    @Autowired
    ActFormService actFormService;

    @ApiOperation("跳转到ActForm页面")
    @RequestMapping(value="/index.html", method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        return "actForm/index";
    }

    @ApiOperation(value="分页查询ActForm", notes = "分页查询ActForm，返回easyui分页信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="ActForm", paramType="form", value = "ActForm的form信息", required = false, dataType = "string")
	})
    @RequestMapping(value="/listPage.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String listPage(ActForm actForm) throws Exception {
        return actFormService.listEasyuiPageByExample(actForm, true).toString();
    }

    @ApiOperation("新增ActForm")
    @ApiImplicitParams({
		@ApiImplicitParam(name="ActForm", paramType="form", value = "ActForm的form信息", required = true, dataType = "string")
	})
    @RequestMapping(value="/insert.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput insert(ActForm actForm) {
        //设置默认表单提交地址
        if(StringUtils.isBlank(actForm.getUrl())){
            actForm.setUrl("/actControl/submit.action");
        }
        //设置默认表单模板地址,必填参数formKey， processDefinitionId或taskId必填一个
        if(StringUtils.isBlank(actForm.getTemplateUri())){
            actForm.setTemplateUri("/actControl/dynamicForm.html");
        }
        actFormService.insertSelective(actForm);
        return BaseOutput.success("新增成功");
    }

    @ApiOperation("修改ActForm")
    @ApiImplicitParams({
		@ApiImplicitParam(name="ActForm", paramType="form", value = "ActForm的form信息", required = true, dataType = "string")
	})
    @RequestMapping(value="/update.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput update(ActForm actForm) {
        actFormService.updateExactSimple(actForm);
        return BaseOutput.success("修改成功");
    }

    @ApiOperation("删除ActForm")
    @ApiImplicitParams({
		@ApiImplicitParam(name="id", paramType="form", value = "ActForm的主键", required = true, dataType = "long")
	})
    @RequestMapping(value="/delete.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput delete(Long id) {
        actFormService.delete(id);
        return BaseOutput.success("删除成功");
    }
}