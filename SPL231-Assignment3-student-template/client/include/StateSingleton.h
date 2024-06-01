#pragma once
#include "../include/StateControler.h"

static StateControler* state_control = nullptr;

class StateSingleton
{
    public:
    static StateControler *get_state_controler()
    {
        if(state_control == nullptr)
            state_control = new StateControler();
        return state_control;
    }
    
};