package com.jiuba.nb.miaosha.directive;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;


import com.jspgou.cms.action.directive.ProductAbstractDirective;
import com.jspgou.common.page.Pagination;
import com.jspgou.common.web.freemarker.DirectiveUtils;
import com.jspgou.core.entity.Website;

import freemarker.core.Environment;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

public class MiaoShaProductDirective extends ProductAbstractDirective{

	public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
			throws TemplateException, IOException {

		Website web = getWeb(env, params, this.websiteMng);
		Long ctgId = getCategoryId(params);
		Long brandId = getLong("brandId", params);
		Long tagId = getTagId(params);
		String[] names = StringUtils.split(getString("names", params), ',');
		String[] values = StringUtils.split(getString("values", params), ',');
		Integer orderBy = getInt("orderBy", params);
		Pagination pagination = this.productMng.getPageForTagChannel(brandId,
				web.getId(), ctgId, tagId, isRecommend(params), names, values,
				isSpecial(params), orderBy.intValue(), getPageNo(env),
				getCount(params));
		Map<String, TemplateModel> paramWrap = new HashMap(params);
		paramWrap.put("tag_pagination",
				ObjectWrapper.DEFAULT_WRAPPER.wrap(pagination));
		Map<String, TemplateModel> origMap = DirectiveUtils.addParamsToVariable(env, paramWrap);
		if (isInvokeTpl(params)) {
			includeTpl("shop", "ProductPage", web, params, env);
		} else {
			renderBody(env, loopVars, body);
		}
		DirectiveUtils.removeParamsFromVariable(env, paramWrap, origMap);

	}

}
