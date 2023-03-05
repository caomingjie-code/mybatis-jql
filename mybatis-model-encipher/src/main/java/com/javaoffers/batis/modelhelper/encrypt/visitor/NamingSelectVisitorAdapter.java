package com.javaoffers.batis.modelhelper.encrypt.visitor;

import com.javaoffers.batis.modelhelper.encrypt.ConditionName;
import com.javaoffers.batis.modelhelper.encrypt.NamingContent;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.*;
import org.apache.commons.collections4.CollectionUtils;


import java.util.Collection;
import java.util.List;

/**
 * @author mingJie
 */
public class NamingSelectVisitorAdapter extends SelectVisitorAdapter {

    private NamingContent namingContent;

    public NamingSelectVisitorAdapter(NamingContent namingContent) {

        this.namingContent = namingContent;
    }

    @Override
    public void visit(PlainSelect plainSelect) {

        //step1: 解析 from table信息.
        FromItem fromItem = plainSelect.getFromItem();
        namingContent.setHavaJoin(CollectionUtils.isNotEmpty(plainSelect.getJoins()));
        fromItem.accept(new NamingFromItemVisitorAdapter(namingContent));

        //step2: 解析 join table on (这样可以先拿到joinTable的信息)
        List<Join> joins = plainSelect.getJoins();
        if(CollectionUtils.isNotEmpty(joins)){
            joins.forEach(join -> {
                //join table
                FromItem rightItem = join.getRightItem();
                rightItem.accept(new NamingFromItemVisitorAdapter(namingContent));
                Collection<Expression> onExpressions = join.getOnExpressions();
                //on
                if(CollectionUtils.isNotEmpty(onExpressions)){
                    onExpressions.forEach(onExpression->{
                        onExpression.accept(new NamingExpressionVisitorAdapter(ConditionName.ON, namingContent));
                    });
                }
            });
        }

        //step3: 解析select colName (查询字段可能存在于主表和join表, 因此要先解析表信息: step1, step2)
        List<SelectItem> selectItems = plainSelect.getSelectItems();
        if(CollectionUtils.isNotEmpty(selectItems)){
            selectItems.forEach(selectItem -> {
                selectItem.accept(new NamingSelectItemVisitor(namingContent));
            });
        }

        //step4: 解析 where
        Expression where = plainSelect.getWhere();
        if(where != null){
            where.accept(new NamingExpressionVisitorAdapter(ConditionName.WHERE, namingContent));
        }


    }

}
