package com.dubbo.util.converter.support;


import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.springframework.core.convert.converter.Converter;

import java.util.Date;

public class StringToDate implements Converter<String,Date> {
	
	private  String DATE_FORMAT = "yyyy-MM-dd";
    private  String DATETIME1_FORMAT = "yyyy-MM-dd HH:mm:ss";
//    private  String DATETIME2_FORMAT = "yyyy-MM-ddTHH:mm:ss";

    public static void main(String[] args) {
		String source = "2011-01-01T00:00:00";
		System.out.println(DateTimeFormat.forPattern("yyyy-MM-ddTHH:mm:ss").parseDateTime(source));
	}
    
	@Override
	public Date convert(String source) {
		if(StringUtils.isEmpty(source)){
			return null;
		}
		if(StringUtils.contains(source, "T")){
			return DateTimeFormat.forPattern(DATETIME1_FORMAT).parseDateTime(source.replace("T", " ")).toDate();
		}
		else if(StringUtils.contains(source, ":")){
			return DateTimeFormat.forPattern(DATETIME1_FORMAT).parseDateTime(source).toDate();
		}
		else  if(StringUtils.contains(source, "-")){
			return DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(source).toDate();
		}
		else if(StringUtils.equals(source.toLowerCase(), "now")){
			return new Date();
		}
		else if(StringUtils.equals(source.toLowerCase(), "today")){
			return (new DateTime()).withTimeAtStartOfDay().toDate();
		}
		else if(StringUtils.equals(source.toLowerCase(), "yesterday")){
			return (new LocalDate().minusDays(1).toDate());
		}
		else if(StringUtils.equals(source.toLowerCase(), "tomorrow")){
			return (new LocalDate().plusDays(1).toDate());
		}
		else{
			throw new IllegalArgumentException("Invalid date string value '" + source + "'");
		}
		
	}

}
