package com.stock.web.community.controller;


import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.stock.web.community.domain.Comments;
import com.stock.web.community.domain.CommunityDto;
import com.stock.web.community.domain.Stock;
import com.stock.web.community.service.CommunityService;
import com.stock.web.user.domain.UserDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import net.sf.json.JSONArray;

@Controller
@RequestMapping("/community/*")
@RequiredArgsConstructor
@Log4j
public class CommuController {
	
	private final CommunityService service;
	
	@GetMapping("community")
	public void community(HttpSession session)
	{
		
		
		session.setAttribute("login",session.getAttribute("login"));
	}
	
	
	@PostMapping("viewContent")
	@ResponseBody
	public String viewContent(HttpSession session,int id)
	{
		UserDto user=new UserDto();			
		if(session.getAttribute("login")==null) {
			user.setId("");
			log.info("---------------아이디 : "+Long.valueOf(id)+"---------------유저아이디널일때 : "+ user.getId());
		}else{
			session.setAttribute("login",session.getAttribute("login"));
			user=(UserDto) session.getAttribute("login");
			log.info("---------------아이디 : "+Long.valueOf(id)+"---------------유저아이디널아닐때 : "+ user.getId());
		}
		log.info("---------------아이디 : "+Long.valueOf(id)+"---------------유저아이디전체샷 : "+ user.getId());
		CommunityDto dto=service.selectContent(Long.valueOf(id), user.getId());
		JSONArray jsonArray = JSONArray.fromObject(dto);
		log.info("---------------아이디 : "+Long.valueOf(id)+"---------------유저아이디 마지막 : "+ user.getId());
		return jsonArray.toString();
	}
	
	
	@PostMapping("likePush")
	@ResponseBody
	public String likePush(HttpSession session,int id)
	{
		UserDto user=new UserDto();			
		if(session.getAttribute("login")==null) {
			return "error";
		}else{
			session.setAttribute("login",session.getAttribute("login"));
		}
		CommunityDto com =new CommunityDto();
		com.setID(Long.valueOf(id));
		
		String result2=service.likePush((UserDto)session.getAttribute("login"), com);
		log.info("ddddddddddddddddddddddddddddddd");
		log.info("ddddddddddddddddddddddddddddddd"+ result2);
		return result2;
		
		
		
		
	}
	
	@PostMapping("commnets")
	@ResponseBody
	public String commnets(HttpSession session, int fpage, int epage,int bid)
	{
		UserDto user=new UserDto();			
		if(session.getAttribute("login")==null) {
			user.setId("");
		}else{
			user=(UserDto) session.getAttribute("login");
		}
		
		List<Comments> list =service.commentsList(fpage, epage, Long.valueOf(bid));

		JSONArray jsonArray = JSONArray.fromObject(list);


		log.info(jsonArray);
		session.setAttribute("login",session.getAttribute("login"));
        return jsonArray.toString();	
	}
	
	
	@PostMapping("communityList")
	@ResponseBody
	public String communityList(HttpSession session, int fpage, int epage)
	{
		UserDto user=new UserDto();			
		if(session.getAttribute("login")==null) {
			user.setId("");
		}else{
			user=(UserDto) session.getAttribute("login");
		}
		
		List<CommunityDto> list =service.getList(fpage,epage,user.getId());

		JSONArray jsonArray = JSONArray.fromObject(list);


		log.info(jsonArray);
		session.setAttribute("login",session.getAttribute("login"));
        return jsonArray.toString();	
	}
	
	@GetMapping("search")
	public String search(HttpSession session,String search,Model model)
	{
		
		UserDto user=new UserDto();			
		if(session.getAttribute("login")==null) {
			user.setId("");
		}else{
			user=(UserDto) session.getAttribute("login");
		}
		
		System.out.println("검색값---------ssssssssssssssssssssss--------"+ search);
		model.addAttribute("jong",search);
		return "redirect:/community/chart";
	
	}
	
	
	@PostMapping("write")
	public String write(CommunityDto com,HttpSession session)
	{
		UserDto user =(UserDto)session.getAttribute("login");
		if(user.getId().isEmpty())
		{
			return "redirect:/user/login";
		}
		com.setHASHTAG(com.getHASHTAG().replaceAll("\\p{Z}",""));
		com.setUSER_ID(user.getId());
		com.setCOUNT(StringUtils.countMatches(com.getHASHTAG(), "#"));
		service.write(com);
		session.setAttribute("login",session.getAttribute("login"));
		return "redirect:/community/community";
	}
	
	
	@GetMapping("padcast")
	public void padcast(HttpSession session)
	{
		session.setAttribute("login",session.getAttribute("login"));
	}
	
	
	@GetMapping("chart")
	public void chart(HttpSession session)
	{
		session.setAttribute("login",session.getAttribute("login"));
	}
	
	@PostMapping("autocomplete") 
	@ResponseBody
	public String autocomplete(HttpSession session)
	{
		//log.info("실행=--------------------------------------------------------------------------------");
		List<Stock> stockList=service.autocomplete();
		
		JSONArray jsonArray = JSONArray.fromObject(stockList);
		//log.info(jsonArray);
        return jsonArray.toString();	
	}
	
	
	
	
	
	@PostMapping("header")
	@ResponseBody
	public String header()
	{
		
		String URL = "https://finance.naver.com/sise/";
		Document doc;
		JsonArray infoArray = new JsonArray();
		List<String> sup1 = new ArrayList<String>();
		List<String> liup1 = new ArrayList<String>();
		String[] buf1 = null;
		String[] sup2 = null;
		List<String> sdown1 = new ArrayList<String>();
		List<String> lidown1 = new ArrayList<String>();
		String[] buf2 = null;
		String[] sdown2  =null;
		try {
			doc = Jsoup.connect(URL).get();
			List<Element> up1 = doc.select("#siselist_tab_0 tbody .tltle");//원하는 태그 선택
			Elements up2 = doc.select("#siselist_tab_0 tbody .number .tah");//원하는 태그 선택
			
			List<Element> down1 = doc.select("#siselist_tab_1 tbody .tltle");//원하는 태그 선택
			Elements down2 = doc.select("#siselist_tab_1 tbody .number .tah");//원하는 태그 선택
		
		
			String check=down1.toString();
			for (int i=0;i<up1.size();i++) {
				liup1.add(up1.get(i).attr("href").toString().substring(up1.get(i).attr("href").toString().lastIndexOf("=")).substring(1));
				log.info(liup1.get(i));
			}
			for (int i=0;i<down1.size();i++) {
				lidown1.add(down1.get(i).attr("href").toString().substring(down1.get(i).attr("href").toString().lastIndexOf("=")).substring(1));
				
				log.info(lidown1.get(i));
			}
			for (int i=0;i<up1.size();i++) {
				sup1.add(up1.get(i).text().toString());
				//log.info(sup1.get(i));
			}
			for (int i=0;i<down1.size();i++) {
			
				sdown1.add(down1.get(i).text().toString());
				//log.info(sdown1.get(i));
			}

			buf1 = up2.text().split(" ");//정보 파싱
			
			buf2 = down2.text().split(" ");//정보 파싱

				//0 1 2 3 4 5
			sup2= new String[buf1.length/2];
			sdown2= new String[buf2.length/2];

			int odd=0;
			for(int i=0;i<buf1.length;i++){
				if(i % 2 == 1){
					sup2[odd]= buf1[i];
					odd++;
				}	
			}
			odd=0;
			for(int i=0;i<buf2.length;i++){
				if(i % 2 == 1){
					sdown2[odd]= buf2[i];
					odd++;
				}
	
			}
	
			
			 for(int i=0 ;i<sup1.size();i++){
				 JsonObject jsonobject = new JsonObject();
				 jsonobject.addProperty("name", sup1.get(i));
				 jsonobject.addProperty("per", sup2[i]);
				 jsonobject.addProperty("h", liup1.get(i));
				 infoArray.add(jsonobject);  		
				
			 }
			 if(!check.isEmpty()) {
			 for(int i=0 ;i<sdown1.size();i++){
				 JsonObject jsonobject = new JsonObject();
				 jsonobject.addProperty("name", sdown1.get(i));
				 jsonobject.addProperty("per", sdown2[i]);
				 jsonobject.addProperty("h", lidown1.get(i));
				 
				 infoArray.add(jsonobject);
				 
			 }
			 }
		
			 log.info(infoArray.toString());
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //HTML로 부터 데이터 가져오기
//		for (String L: sup1)
//		{
//			log.info("sup1확인 _-----------------: " +L);
//			
//		}
//		for (String L: sup2)
//		{
//			log.info("sup2확인 _-----------------: " +L);
//			
//		}
//		
//		for (String L: sdown1)
//		{
//			log.info("sdown1확인 _-----------------: " +L);
//			
//		}
//		for (String L: sdown2)
//		{
//			log.info("sdown2확인 _-----------------: " +L);
//			
//		}
		return infoArray.toString();
	
	}
	
	
	}
		
	
	
	


