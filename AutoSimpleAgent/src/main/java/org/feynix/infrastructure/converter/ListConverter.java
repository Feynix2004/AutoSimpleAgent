package org.feynix.infrastructure.converter;

import org.apache.ibatis.type.MappedTypes;

import java.util.ArrayList;
import java.util.List;

/**
 * 模型配置转换器
 */
@MappedTypes(ArrayList.class)
public class ListConverter extends JsonToStringConverter<ArrayList> {

    public ListConverter() {
        super(ArrayList.class);
    }
}
