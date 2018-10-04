package com.dubbo.util.converter.support;

import com.dubbo.BeanUtils;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class ObjectToMap implements GenericConverter {
	
	
	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if(targetType.isMap()){
			try {
				return BeanUtils.map(source, HashMap.class);
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
		set.add(new ConvertiblePair(Object.class,Map.class));
		return set;
	}

}
