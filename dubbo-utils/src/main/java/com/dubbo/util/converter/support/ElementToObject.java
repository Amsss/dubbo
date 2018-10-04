package com.dubbo.util.converter.support;

import com.dubbo.BeanUtils;
import com.dubbo.util.S;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ElementToObject implements GenericConverter {
	
	@SuppressWarnings("unchecked")
	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if(Element.class.isInstance(source)){
			try {
				Element el = (Element)source;
				Object dest = targetType.getType().newInstance();
				
				List<Attribute> attrs = el.attributes();
				for(Attribute attr : attrs){
					String str = attr.getValue();
					if(S.isEmpty(str)){	//	ignore when value is null
						continue;
					}
					try{
						BeanUtils.setProperty(dest, attr.getName(), str);
					}
					catch(Exception e){
						try{
							BeanUtils.setPropertyInMap(dest, attr.getName(), str);
						}catch (Exception e2) {
							throw e2;
						}
					}
				}
				return dest;
			} 
			catch(Exception e){
        		throw new IllegalStateException("falied to convert element to bean",e);
        	}
		}
		else{
			throw new IllegalStateException("source object must be a Element");
		}
	}

	@Override
	public Set<ConvertiblePair> getConvertibleTypes() {
		Set<ConvertiblePair> set = new HashSet<ConvertiblePair>();
		set.add(new ConvertiblePair(Element.class,Object.class));
		return set;
	}
	
}
