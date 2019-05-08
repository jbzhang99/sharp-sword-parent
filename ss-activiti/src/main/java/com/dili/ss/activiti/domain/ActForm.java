package com.dili.ss.activiti.domain;

import com.dili.ss.dto.IBaseDomain;
import com.dili.ss.dto.IMybatisForceParams;
import com.dili.ss.metadata.FieldEditor;
import com.dili.ss.metadata.annotation.EditMode;
import com.dili.ss.metadata.annotation.FieldDef;
import java.util.Date;
import javax.persistence.*;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 由MyBatis Generator工具自动生成
 * 
 * This file was generated on 2019-03-21 16:02:46.
 */
@Table(name = "`act_form`")
public interface ActForm extends IBaseDomain, IMybatisForceParams {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @FieldDef(label="id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getId();

    void setId(Long id);

    @Column(name = "`form_key`")
    @FieldDef(label="表单key", maxLength = 20)
    @EditMode(editor = FieldEditor.Text, required = true)
    String getFormKey();

    void setFormKey(String formKey);

    @Column(name = "`callback`")
    @FieldDef(label="回调方法", maxLength = 20)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getCallback();

    void setCallback(String callback);

    @Column(name = "`url`")
    @FieldDef(label="URL", maxLength = 20)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getUrl();

    void setUrl(String url);

    @Column(name = "`template_uri`")
    @FieldDef(label="模板URI", maxLength = 100)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getTemplateUri();

    void setTemplateUri(String templateUri);

    @Column(name = "`created`")
    @FieldDef(label="创建时间")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    Date getCreated();

    void setCreated(Date created);
}