package com.dili.ss.activiti.controller;

import com.dili.ss.activiti.domain.ActControl;
import com.dili.ss.activiti.service.ActControlService;
import com.dili.ss.activiti.domain.ActControl;
import com.dili.ss.activiti.service.ActControlService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.FormService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2019-03-19 17:14:28.
 */
@Api("/actControl")
@Controller
@RequestMapping("/actControl")
public class ActControlController {
    @Autowired
    ActControlService actControlService;

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    FormService formService;

    @ApiOperation("跳转到ActControl页面")
    @RequestMapping(value="/index.html", method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        return "actControl/index";
    }

    @ApiOperation(value="分页查询ActControl", notes = "分页查询ActControl，返回easyui分页信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="ActControl", paramType="form", value = "ActControl的form信息", required = false, dataType = "string")
	})
    @RequestMapping(value="/listPage.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String listPage(ActControl actControl) throws Exception {
        return actControlService.listEasyuiPageByExample(actControl, true).toString();
    }

    @ApiOperation("新增ActControl")
    @ApiImplicitParams({
		@ApiImplicitParam(name="ActControl", paramType="form", value = "ActControl的form信息", required = true, dataType = "string")
	})
    @RequestMapping(value="/insert.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput insert(ActControl actControl) {
        actControl.setControlId(actControl.getControlId().trim());
        if(StringUtils.isBlank(actControl.getName())){
            actControl.setName(actControl.getControlId());
        }
        actControlService.insertSelective(actControl);
        return BaseOutput.success("新增成功");
    }

    @ApiOperation("修改ActControl")
    @ApiImplicitParams({
		@ApiImplicitParam(name="ActControl", paramType="form", value = "actControl的form信息", required = true, dataType = "string")
	})
    @RequestMapping(value="/update.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput update(ActControl actControl) {
        actControl.setControlId(actControl.getControlId().trim());
        if(StringUtils.isBlank(actControl.getName())){
            actControl.setName(actControl.getControlId());
        }
        actControlService.updateExactSimple(actControl);
        return BaseOutput.success("修改成功");
    }

    @ApiOperation("删除ActControl")
    @ApiImplicitParams({
		@ApiImplicitParam(name="id", paramType="form", value = "ActControl的主键", required = true, dataType = "long")
	})
    @RequestMapping(value="/delete.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput delete(Long id) {
        actControlService.delete(id);
        return BaseOutput.success("删除成功");
    }

    /**
     * processDefinitionId和taskId至少填一个，分别代码开始节点表单和任务节点表单
     * @param formKey
     * @param processDefinitionId
     * @param taskId
     * @param modelMap
     * @return
     */
    @ApiOperation("跳转到动态表单页面")
    @RequestMapping(value="/dynamicForm.html", method = {RequestMethod.GET, RequestMethod.POST})
    public String dynamicForm(@RequestParam String formKey,@RequestParam(required = false) String processDefinitionId,@RequestParam(required = false) String taskId, ModelMap modelMap) {
        ActControl actControl = DTOUtils.newDTO(ActControl.class);
        actControl.setFormKey(formKey);
        List<ActControl> actControls = actControlService.list(actControl);
        if(actControls.isEmpty()){
            throw new RuntimeException("formKey不存在");
        }
        modelMap.put("actControls", actControls);
        modelMap.put("processDefinitionId", processDefinitionId);
        modelMap.put("taskId", taskId);
        return "actForm/dynamicForm";
    }

    @ApiOperation("提交动态表单")
    @RequestMapping(value="/submit.action", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public BaseOutput<String> submit(@RequestParam Map<String, String> variables, HttpServletRequest request) {
        if(variables.get("_processDefinitionId") != null){
            try {
                ProcessInstance processInstance = formService.submitStartFormData(variables.get("_processDefinitionId"), variables);
                return BaseOutput.success("流程启动成功").setData(processInstance.getId());
            } catch (ActivitiException e) {
                return BaseOutput.failure(e.getMessage());
            }
        }else if(variables.get("_taskId") != null){
            try {
                formService.submitTaskFormData(variables.get("_taskId"), variables);
                return BaseOutput.success("任务提交成功");
            } catch (ActivitiException e) {
                return BaseOutput.failure(e.getMessage());
            }
        }else{
            return BaseOutput.success("无任务启动");
        }
    }


}