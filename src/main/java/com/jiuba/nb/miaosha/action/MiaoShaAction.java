package com.jiuba.nb.miaosha.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jspgou.cms.entity.Brand;
import com.jspgou.cms.entity.Category;
import com.jspgou.cms.entity.Exended;
import com.jspgou.cms.entity.Product;
import com.jspgou.cms.entity.ProductStandard;
import com.jspgou.cms.entity.ShopArticle;
import com.jspgou.cms.entity.ShopChannel;
import com.jspgou.cms.entity.StandardType;
import com.jspgou.cms.manager.BrandMng;
import com.jspgou.cms.manager.CategoryMng;
import com.jspgou.cms.manager.ExendedMng;
import com.jspgou.cms.manager.ProductMng;
import com.jspgou.cms.manager.ProductStandardMng;
import com.jspgou.cms.manager.ShopArticleMng;
import com.jspgou.cms.manager.ShopChannelMng;
import com.jspgou.cms.manager.StandardTypeMng;
import com.jspgou.cms.web.ShopFrontHelper;
import com.jspgou.cms.web.SiteUtils;
import com.jspgou.common.web.RequestUtils;
import com.jspgou.core.entity.Global;
import com.jspgou.core.entity.Website;
import com.jspgou.core.web.WebErrors;
import com.jspgou.core.web.front.FrontHelper;
import com.jspgou.core.web.front.URLHelper;


@Controller
public class MiaoShaAction {
	
	public static final String TPL_INDEX = "tpl.index";
	private static final String BRAND = "tpl.brand";
	private static final String BRAND_DETAIL = "tpl.brandDetail";
	@Autowired
	private CategoryMng categoryMng;
	@Autowired
	private ProductMng productMng;
	@Autowired
	private ShopChannelMng shopChannelMng;
	@Autowired
	private ShopArticleMng shopArticleMng;
	@Autowired
	private BrandMng brandMng;
	@Autowired
	private StandardTypeMng standardTypeMng;
	@Autowired
	private ProductStandardMng productStandardMng;
	@Autowired
	private ExendedMng exendedMng;
	
	@RequestMapping(value = { "/miaosha/tb/abc.htm" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String indexForWeblogic(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		
		String url = request.getRequestURL().toString();
	    Global global = SiteUtils.getWeb(request).getGlobal();
	    model.addAttribute("global", global);
	    URLHelper.URLInfo info = URLHelper.getURLInfo(url, request.getContextPath());
	    String queryString = request.getQueryString();
	    Website web = SiteUtils.getWeb(request);
	    ShopFrontHelper.setDynamicPageData(request, model, web, url, info.getUrlPrefix(), info.getUrlSuffix(), info.getPageNo());
	    String[] paths = info.getPaths();
	    String[] params = info.getParams();
	    int pageNo = info.getPageNo();
	    int len = paths.length;
	    if (len == 1) {
	      return channel(paths[0], params, pageNo, queryString, url, web, request, response, model);
	    }
	    else
	    {
	      if ("index".equals("index")) {
	        return channel(paths[0], params, pageNo, queryString, url, web, request, response, model);
	      }
	      try
	      {
	        Long id = Long.valueOf(Long.parseLong(paths[1]));
	        
	        return content(paths[0], id, params, pageNo, queryString, url, web, request, response, model);
	      }
	      catch (NumberFormatException localNumberFormatException) {}
	    }
	    //return FrontHelper.pageNotFound(web, model, request);
		
		
		return "/WEB-INF/t/gou/tpl/ms/tree.html";
	}
	
	@RequestMapping(value = { "/ajax/tb/abc.htm" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public String indexForWeblogic2(HttpServletRequest request, ModelMap model) {
		
		
		
		return "/WEB-INF/ms/tree.html";
	}
	
	public String channel(String path, String[] params, int pageNo,
			String queryString, String url, Website web,
			HttpServletRequest request, HttpServletResponse response,
			ModelMap model) {
		Category category = this.categoryMng.getByPath(web.getId(), path);
		if (category != null) {
			List<Exended> exendeds = this.exendedMng.getList(category.getType().getId());
			Map map = new HashMap();
			Map map1 = new HashMap();
			int num = exendeds.size();
			for (int i = 0; i < num; i++) {
				map.put(((Exended) exendeds.get(i)).getId().toString(),
						((Exended) exendeds.get(i)).getItems());
				map1.put(((Exended) exendeds.get(i)).getId().toString(),
						exendeds.get(i));
			}
			model.addAttribute("brandId", getBrandId(request));
			model.addAttribute("map", map);
			model.addAttribute("map1", map1);
			model.addAttribute("fields", getNames(request));
			model.addAttribute("zhis", getValues(request));
			model.addAttribute("category", category);
			model.addAttribute("pageSize", getpageSize(request));
			model.addAttribute("names", getName(request));
			model.addAttribute("values", getValue(request));
			model.addAttribute("isShow", getIsShow(request));
			model.addAttribute("orderBy", getOrderBy(request));
			model.addAttribute("miaosha","测试秒杀");
			String ftlpath = ""/*category.getTplChannelRel()*/;
			ftlpath = "/WEB-INF/t/gou/tpl/ms/tree.html";
			return ftlpath;
		}
		ShopChannel channel = this.shopChannelMng.getByPath(web.getId(), path);
		if (channel != null) {
			model.addAttribute("channel", channel);
			return channel.getTplChannelRel();
		}
		return FrontHelper.pageNotFound(web, model, request);
	}

	public String content(String chnlName, Long id, String[] params,
			int pageNo, String queryString, String url, Website web,
			HttpServletRequest request, HttpServletResponse response,
			ModelMap model) {
		String ftlpath ="/WEB-INF/t/gou/tpl/ms/tree.html";
		Category category = this.categoryMng.getByPath(web.getId(), chnlName);
		if (category != null) {
			Product product = this.productMng.findById(id);
			if (product != null) {
				if (product.getProductFashion() != null) {
					String[] arr = (String[]) null;
					arr = product.getProductFashion().getNature().split("_");
					model.addAttribute("arr", arr);
				}
				List<ProductStandard> psList = this.productStandardMng
						.findByProductId(id);
				List<StandardType> standardTypeList = this.standardTypeMng
						.getList(category.getId());

				model.addAttribute("standardTypeList", standardTypeList);
				model.addAttribute("psList", psList);
				model.addAttribute("category", category);
				model.addAttribute("product", product);
				
				//ftlpath = category.getTplContentRel();
				ftlpath ="/WEB-INF/t/gou/tpl/ms/tree.html";
				
				return ftlpath;
			}
			return FrontHelper.pageNotFound(web, model, request);
		}
		ShopArticle article = this.shopArticleMng.findById(id);
		if (article != null) {
			ShopChannel channel = article.getChannel();
			model.addAttribute("article", article);
			model.addAttribute("channel", channel);
			return channel.getTplContentRel();
		}
		return FrontHelper.pageNotFound(web, model, request);
	}

	public Integer getBrandId(HttpServletRequest request) {
		String brandId = RequestUtils.getQueryParam(request, "brandId");
		Integer id = null;
		if (!StringUtils.isBlank(brandId)) {
			id = Integer.valueOf(Integer.parseInt(brandId));
		}
		return id;
	}

	public Integer getpageSize(HttpServletRequest request) {
		String pageSize = RequestUtils.getQueryParam(request, "pageSize");
		Integer pagesize = null;
		if (!StringUtils.isBlank(pageSize)) {
			pagesize = Integer.valueOf(Integer.parseInt(pageSize));
		}
		if (pagesize == null) {
			pagesize = Integer.valueOf(12);
		}
		return pagesize;
	}

	public Integer getIsShow(HttpServletRequest request) {
		String isShow = RequestUtils.getQueryParam(request, "isShow");
		Integer isshow = null;
		if (!StringUtils.isBlank(isShow)) {
			isshow = Integer.valueOf(Integer.parseInt(isShow));
		}
		if (isshow == null) {
			isshow = Integer.valueOf(0);
		}
		return isshow;
	}

	public Integer getOrderBy(HttpServletRequest request) {
		String orderBy = RequestUtils.getQueryParam(request, "orderBy");
		Integer orderby = null;
		if (!StringUtils.isBlank(orderBy)) {
			orderby = Integer.valueOf(Integer.parseInt(orderBy));
		}
		if (orderby == null) {
			orderby = Integer.valueOf(0);
		}
		return orderby;
	}

	public String[] getNames(HttpServletRequest request) {
		Map<String, String> attr = RequestUtils.getRequestMap(request,
				"exended_");
		List li = new ArrayList((Collection) attr.keySet());
		String name = "";
		for (int i = 0; i < li.size(); i++) {
			if (i + 1 == li.size()) {
				name = name + (String) li.get(i);
			} else {
				name = name + (String) li.get(i) + ",";
			}
		}
		String[] names = StringUtils.split(name, ',');
		return names;
	}

	public String[] getValues(HttpServletRequest request) {
		Map<String, String> attr = RequestUtils.getRequestMap(request,
				"exended_");
		List li = new ArrayList((Collection) attr.keySet());
		String value = "";
		for (int i = 0; i < li.size(); i++) {
			if (i + 1 == li.size()) {
				if (StringUtils.isBlank((String) attr.get(li.get(i)))) {
					value = value + "0";
				} else {
					value = value + (String) attr.get(li.get(i));
				}
			} else if (StringUtils.isBlank((String) attr.get(li.get(i)))) {
				value = value + "0,";
			} else {
				value = value + (String) attr.get(li.get(i)) + ",";
			}
		}
		String[] values = StringUtils.split(value, ',');
		return values;
	}

	public String getName(HttpServletRequest request) {
		Map<String, String> attr = RequestUtils.getRequestMap(request,
				"exended_");
		List li = new ArrayList((Collection) attr.keySet());
		String name = "";
		for (int i = 0; i < li.size(); i++) {
			if (i + 1 == li.size()) {
				name = name + (String) li.get(i);
			} else {
				name = name + (String) li.get(i) + ",";
			}
		}
		return name;
	}

	public String getValue(HttpServletRequest request) {
		Map<String, String> attr = RequestUtils.getRequestMap(request,
				"exended_");
		List li = new ArrayList((Collection) attr.keySet());
		String value = "";
		for (int i = 0; i < li.size(); i++) {
			if (i + 1 == li.size()) {
				if (StringUtils.isBlank((String) attr.get(li.get(i)))) {
					value = value + "0";
				} else {
					value = value + (String) attr.get(li.get(i));
				}
			} else if (StringUtils.isBlank((String) attr.get(li.get(i)))) {
				value = value + "0,";
			} else {
				value = value + (String) attr.get(li.get(i)) + ",";
			}
		}
		return value;
	}

	private WebErrors validateBrand(Long id, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		if (id != null) {
			Brand brand = this.brandMng.findById(id);
			if (errors.ifNotExist(brand, Brand.class, id)) {
				return errors;
			}
		}
		return errors;
	}

}
