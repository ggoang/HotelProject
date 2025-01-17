package com.boot.hotel.controller;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.boot.hotel.dto.HotelBasketDTO;
import com.boot.hotel.dto.HotelDTO;
import com.boot.hotel.dto.HotelInfoDTO;
import com.boot.hotel.dto.HotelPictureDTO;
import com.boot.hotel.oauth2.dto.SessionUser;
import com.boot.hotel.service.HotelBasketService;
import com.boot.hotel.service.HotelInfoService;
import com.boot.hotel.util.MyUtil;


@RestController
@RequestMapping("/hotel")
public class HotelInfoController {
	
	@Autowired
	private HttpSession httpSession;

    @Autowired
    private HotelInfoService hotelInfoService;
 
    @Autowired
    private HotelBasketService hotelBasketService;
    
    @Autowired
    private MyUtil myUtil;
    
    
    @RequestMapping(value = "/hotellist", method = { RequestMethod.GET, RequestMethod.POST })
    public ModelAndView list(HttpServletRequest request)throws Exception {

        ModelAndView mav = new ModelAndView();

        String pageNum = request.getParameter("pageNum");

        int currentPage = 1;

        if(pageNum!=null) {
            currentPage = Integer.parseInt(pageNum);
        }

        String searchValue = request.getParameter("searchValue");


        if (searchValue == null || searchValue.equals("")) {
            searchValue = "";
        }

        if (request.getMethod().equalsIgnoreCase("GET") && searchValue != null) {
            searchValue = URLDecoder.decode(searchValue, "UTF-8");
        }

        Map<String, Object> params1 = new HashMap<>();
        params1.put("searchValue", searchValue);

        int dataCount = hotelInfoService.getHotelCount(params1);

        int numPerPage = 10;

        int totalPage = myUtil.getPageCount(numPerPage, dataCount);

        if (currentPage>totalPage) {
            currentPage = totalPage;
        }

        int start = (currentPage-1)*numPerPage+1;
        int end = currentPage*numPerPage;

        Map<String, Object> params = new HashMap<>();
        params.put("start", start);
        params.put("end", end);
        params.put("searchValue", searchValue);
        
        
        if(!searchValue.equals("") && searchValue != null) {
            params.put("searchValue", searchValue);
        }
       
        List<HotelDTO> hotelList1 = hotelInfoService.getHotelList1(params);
        List<HotelInfoDTO> hotelList2 = hotelInfoService.getHotelList2(params);
        List<HotelPictureDTO> hotelList3 = hotelInfoService.getHotelList3(params); // 추가된 부분입니다.

        
        System.out.println("hotelList3 :" + hotelList3);
        
        String currentUrl = request.getRequestURL().toString() + "?" + request.getQueryString();
        String listUrl = currentUrl.replaceAll("&?pageNum=\\d+", "");

       

        String pageIndexList = myUtil.pageIndexList(currentPage, totalPage, listUrl, searchValue);


        
        String detailUrl = "/detail?searchValue=" + 
                searchValue + "&pageNum=" + currentPage;

        if (searchValue != null && !searchValue.equals("")) {
            detailUrl += "&searchValue=" + searchValue;
        }

        if (pageNum != null) {
            detailUrl += "&" + "pageNum=" + pageNum;
        }

        
        
        mav.addObject("hotelList1", hotelList1);
        mav.addObject("hotelList2", hotelList2);
        mav.addObject("hotelList3", hotelList3); 
        mav.addObject("pageIndexList", pageIndexList);
        mav.addObject("dataCount", dataCount);
        mav.addObject("currentPage", currentPage);
        mav.addObject("numPerPage", numPerPage);
        mav.addObject("detailUrl", detailUrl);
        mav.addObject("searchValue", searchValue);
      

        mav.setViewName("hotel/hotelList");

        return mav;
    
        
    }
  //리스트에서 추가하면 코드 짐하기 코드..어쩌고.... 저저고..
    //여기에 찜하기 어쩌고 저쩌ㅏ고 
    @PostMapping("/addBasket")
    public ModelAndView addBasket(@RequestParam int hotel_id) throws Exception {
    	
    	System.out.println("===찜하기추가===");
    	
    	SessionUser sessionUser = (SessionUser) httpSession.getAttribute("sessionUser");
    	String userid = sessionUser.getId();

        
        System.out.println("==userid=== " + userid);
        
        System.out.println("==hotel_id=== " + hotel_id);
        
        ModelAndView mav = new ModelAndView();
        HotelBasketDTO dto = new HotelBasketDTO();

        int maxNum = hotelBasketService.maxNum();
        dto.setBasket_num(maxNum + 1);
        dto.setUserid(userid);
        dto.setHotel_id(hotel_id);

        System.out.println("==userid=== " + userid);
        System.out.println("==hotel_id=== " + hotel_id);
        
        
        hotelBasketService.addHotelBasket(dto);

        mav.setView(new RedirectView("/hotel/hotelList"));
        return mav;
    }

    @PostMapping("/removeBasket")
    public ModelAndView removeBasket(@RequestParam int hotel_id) throws Exception {
    	
    	System.out.println("===찜해제===");
    	
    	SessionUser sessionUser = (SessionUser) httpSession.getAttribute("sessionUser");
    	String userid = sessionUser.getId();
    	
    	ModelAndView mav = new ModelAndView();
    
     	HotelBasketDTO dto = new HotelBasketDTO();
     	dto.setUserid(userid);
     	dto.setHotel_id(hotel_id);
     	
     	hotelBasketService.deleteHotelBasket(dto);
     	
         mav.setView(new RedirectView("/hotel/hotelList"));
         return mav;
         
         
    }
    
}





















