package com.ticketservice.common;

import java.util.List;

public class Utility {
	
	public static boolean isNotNull(Object obj){
		if(obj != null){
			return true;
		}
		return false;
	}
	
	public static <E> boolean listNotEmpty(List<E> list){
		if(list != null && !list.isEmpty()){
			return true;
		}
		return false;
	}

}
