#include "../include/keyboardInputSend.h"


keyboardInputSend::keyboardInputSend(int id, ConnectionHandler& CH,StateControler *sc):id(id), CH(CH), sc(sc){}

void keyboardInputSend::run(){
   
    std::string input = "";
    while (!sc->get_shutdown() ){                          
        if(!sc->get_waiting_for_logout() ){
            if(input==""){       
                std::cout << "Command:";
                getline(std::cin,input);            //reading from keyboard
                std::cout << std::endl;
                CH.stompProcessClientInput(input,sc);
                input = "";
                //std::this_thread::sleep_for(100ms);
            }
        }
        // else {                   //we saved input - use it instead of a new one
        //     CH.stompProcessClientInput(last_input);         // the send process
        //     last_input="";
        // }
/*
        if((input!="") & (!logout)) {
            ///------------------logged in procces send------------
            CH.stompProcessClientInput(input);         // the send process
            input = "";
        }else if((input=="shutdown") & (logout)) {       //only if user logged out, we will allow shutdown
            ///------------------shutdown send---------------------
            do_shutdown = true;
        }else if((logout) & (input.find("login")!= std::string::npos)){     //user wants to log in again
            ///----------login again - rerun threads proccess------
            last_input=input;
            logout=false;       //for making the socketReader, to run on the while
            first_login =false; //for making the socketReader NOT read the socket until we are logged in again
            break;              //close the run loop - make the thread join
        }*/
    }
}
    