#include "../include/StateControler.h"

StateControler::StateControler() : waiting_for_logout(false),waiting_message(false),logout(true),shutdown(false),is_user_logged_in(false),mu()
     {
    //     this->waiting_for_logout = false;
    //     this->shutdown = false;
    //     this->logout = true;
    //     this->is_user_logged_in = false;
    //     this->waiting_message = false;
    }

    bool StateControler::get_waiting_for_logout()
    {
        std::lock_guard<std::mutex> lock(mu);
        return waiting_for_logout;
    }
    void StateControler::set_waiting_for_logout(bool inp)
    {
        std::lock_guard<std::mutex> lock(mu);
        waiting_for_logout = inp;
    }

    bool StateControler::get_shutdown()
    {
        std::lock_guard<std::mutex> lock(mu);
        return shutdown;
    }

    bool StateControler::get_is_user_logged_in()
    {
        std::lock_guard<std::mutex> lock(mu);
        return is_user_logged_in;
    }

   

    void StateControler::set_shutdown(bool inp)
    {
        std::lock_guard<std::mutex> lock(mu);
        shutdown = inp;
    }
    
    void StateControler::set_is_user_logged_in(bool inp)
    {
        std::lock_guard<std::mutex> lock(mu);
        is_user_logged_in = inp;
    }

    bool StateControler::get_logout()
    {
        std::lock_guard<std::mutex> lock(mu);
        return logout;
    }

    void StateControler::set_logout(bool inp)
    {
        std::lock_guard<std::mutex> lock(mu);
        logout = inp;
    }

    void StateControler::less_waiting_message()
    {
        std::lock_guard<std::mutex> lock(mu);
        waiting_message--;
    }

     void StateControler::more_waiting_message()
    {
        std::lock_guard<std::mutex> lock(mu);
        waiting_message++;
    }

    int StateControler::get_waiting_message()
    {
        std::lock_guard<std::mutex> lock(mu);
        return waiting_message;
    }