#include "../include/socketReader.h"


socketReader::socketReader(int id, ConnectionHandler &CH,StateControler *sc) : id(id), CH(CH) , sc(sc){}

void socketReader::run()
{
    
    std::string income = "";
    bool shutdown = sc->get_shutdown();
    while (!shutdown)
    {
        // returns a FRAME of string - Decoder
        bool msg = sc->get_waiting_message();
        if (msg)
        {
            try{
                auto x = CH.getFrameAscii(income, '\0');\
                
                if (x)
                {
                    CH.stompReceivedProcess(income,sc); // the receiving process    - Process
                    
                    income = "";
                }
            }
            catch(std::exception e)
            {
                std::cout << "something went wrong while reading and processing a message from server!\n logging you out." << std::endl;
                sc->set_logout(true);
                while(sc->get_waiting_message())
                    sc->less_waiting_message();
            }
        }
        
    }
   
}