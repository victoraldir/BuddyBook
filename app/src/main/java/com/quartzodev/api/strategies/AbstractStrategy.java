package com.quartzodev.api.strategies;

import com.quartzodev.api.interfaces.IBaseAPI;

/**
 * Created by victoraldir on 15/06/2017.
 */

public abstract class AbstractStrategy {

    IBaseAPI mIBaseAPI;

    public AbstractStrategy(IBaseAPI iBaseAPI){
        this.mIBaseAPI = iBaseAPI;
    }

}
