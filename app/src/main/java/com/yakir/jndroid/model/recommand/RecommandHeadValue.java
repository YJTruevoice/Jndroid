package com.yakir.jndroid.model.recommand;

import com.yakir.jndroid.model.BaseModel;

import java.util.ArrayList;

/**
 * @author yakir
 * @function
 */
public class RecommandHeadValue extends BaseModel{

    public ArrayList<String> ads;
    public ArrayList<String> middle;
    public ArrayList<RecommandFooterValue> footer;
}
