package com.quartzodev.api.strategies;

import com.quartzodev.api.interfaces.BaseAPI;

public abstract class GenericStrategy{

    protected BaseAPI baseAPI;

    public GenericStrategy(BaseAPI baseAPI){
        this.baseAPI = baseAPI;
    }
}
