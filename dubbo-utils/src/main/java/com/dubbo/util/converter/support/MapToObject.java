package com.dubbo.util.converter.support;

import com.dubbo.BeanUtils;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class MapToObject implements GenericConverter {
	
	
	@SuppressWarnings("unchecked")
	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if(sourceType.isMap()){
			try {
				Object target = targetType.getType().newInstance();
				Map<String,Object> map = (Map<String,Object>)source;
				Set<String> keys = map.keySet();
				for(String k : keys){
					try {
						BeanUtils.setProperty(target, k, map.get(k));
					} catch (Exception e) {
						try{
							BeanUtils.setPropertyInMap(target, k, map.get(k));
						}catch (Exception e2) {
							// TODO: handle exception
						}
					}
				}
				return target;
			} 
			catch(Exception e){
        		throw new IllegalStateException("falied to convert map to bean",e);
        	}
		}
		else{
			throw new IllegalStateException("source object must be a map");
		}
	}

	@Override
	public Set<ConvertiblePair> getConvertibleTypes() {
		Set<ConvertiblePair> set = new HashSet<ConvertiblePair>();
		set.add(new ConvertiblePair(Map.class,Object.class));
		return set;
	}

}
