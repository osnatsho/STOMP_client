#pragma once
#include <mutex>

class StateControler
{
    private:
    volatile bool waiting_for_logout;
    volatile int waiting_message;
    volatile bool logout;
    volatile bool shutdown;
    volatile bool is_user_logged_in;
    std::mutex mu;

    public:
    StateControler();

    bool get_waiting_for_logout();
    
    void set_waiting_for_logout(bool inp);
    
    bool get_shutdown();

    bool get_is_user_logged_in();

    void set_shutdown(bool inp);
    
    void set_is_user_logged_in(bool inp);

    bool get_logout();

    void set_logout(bool inp);

    void less_waiting_message();

    void more_waiting_message();

    int get_waiting_message();
};