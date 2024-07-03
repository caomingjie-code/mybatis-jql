package com.javaoffers.brief.modelhelper.utils;

import com.javaoffers.brief.modelhelper.anno.derive.JsonColumn;
import com.javaoffers.brief.modelhelper.anno.derive.flag.Version;
import com.javaoffers.brief.modelhelper.core.Id;
import com.javaoffers.brief.modelhelper.filter.JqlMetaInfo;

/**
 * database type
 * @author mingJie
 */
public enum DBType {

    MYSQL {
        @Override
        public boolean isSupportDuplicateModify() {
            return true;
        }
    },

    H2 {
        @Override
        public boolean isSupportDuplicateModify() {
            return true;
        }
    },

    ORACLE{
        @Override
        public boolean isSupportDuplicateModify() {
            return true;
        }
    },

    SQL_SERVER {
        @Override
        public String toString() {
            return "sqlserver";
        }

        @Override
        public boolean isSupportDuplicateModify() {
            return true;
        }
    },

    CLICK_HOUSE {
        @Override
        public String toString() {
            return "ch";
        }
    },

    CLICKHOUSE {
        @Override
        public String toString() {
            return "clickhouse";
        }
    }
    ;

    //processingTranslation
    public Object processingTranslation(JqlMetaInfo jqlMetaInfo, String key, Object value){
        if (value instanceof Id) {
            value = ((Id) value).value();
        } else if (value instanceof Enum) {
            value = EnumValueUtils.getEnumValue(((Enum) value));
        }else if(value instanceof Version){
            value = ((Version) value).longValue();
        } else if(value instanceof JsonColumn){
            value = GsonUtils.gson.toJson(value);
        }
        return value;
    }

    //Whether to support conflict updates
    public boolean isSupportDuplicateModify(){
        return false;
    }

    public String toString(){
        return name();
    }

}
