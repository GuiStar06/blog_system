package com.guistar.entity.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.function.Consumer;

public interface BaseData {

    static final Logger logger = LoggerFactory.getLogger(BaseData.class);

    default <V> V asViewObj(Class<V> clazz, Consumer<V> consumer){
        V v = asViewObj(clazz);
        consumer.accept(v);
        return v;
    }

    default <V> V asViewObj(Class<V> clazz){
        try{
            Field[] fields = clazz.getDeclaredFields();
            Constructor<V> constructor = clazz.getConstructor();
            V v =constructor.newInstance();
            Arrays.asList(fields).forEach(field -> convert(field,v));
            return v;
        }catch (ReflectiveOperationException e){
            logger.error("转换问题:{}",e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    default void convert(Field field,Object tar) {
        try{
            field.setAccessible(true);
            Field source = this.getClass().getDeclaredField(field.getName());
            source.setAccessible(true);
            if(field.getType().isAssignableFrom(source.getType())){
                field.set(tar,source.get(this));
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("字段转换失败" + field.getName(),e);
        }
    }
}
