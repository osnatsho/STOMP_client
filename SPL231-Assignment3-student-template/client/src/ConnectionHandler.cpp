#include "../include/ConnectionHandler.h"


using boost::asio::ip::tcp;

using std::cin;
using std::cout;
using std::cerr;
using std::endl;
using std::string;



ConnectionHandler::ConnectionHandler(string host, short port) : host_(host), port_(port), io_service_(),
                                                                socket_(io_service_) {connected_user = nullptr;}

ConnectionHandler::~ConnectionHandler() {
	close();
}

bool ConnectionHandler::connect() {
	std::cout << "Starting connect to "
	          << host_ << ":" << port_ << std::endl;
	try {
		tcp::endpoint endpoint(boost::asio::ip::address::from_string(host_), port_); // the server endpoint
		boost::system::error_code error;
		socket_.connect(endpoint, error);
		if (error)
			throw boost::system::system_error(error);
	}
	catch (std::exception &e) {
		std::cerr << "Could not connect to server" << std::endl;
		return false;
	}
	return true;
}

bool ConnectionHandler::getBytes(char bytes[], unsigned int bytesToRead) {
	size_t tmp = 0;
	boost::system::error_code error;
	try {
		while (!error && bytesToRead > tmp) {
			tmp += socket_.read_some(boost::asio::buffer(bytes + tmp, bytesToRead - tmp), error);
		}
		if (error)
			throw boost::system::system_error(error);
	} catch (std::exception &e) {
		std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
		return false;
	}
	return true;
}

bool ConnectionHandler::sendBytes(const char bytes[], int bytesToWrite) {
	int tmp = 0;
	boost::system::error_code error;
	try {
		while (!error && bytesToWrite > tmp) {
			tmp += socket_.write_some(boost::asio::buffer(bytes + tmp, bytesToWrite - tmp), error);
		}
		if (error)
			throw boost::system::system_error(error);
	} catch (std::exception &e) {
		std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
		return false;
	}
	return true;
}

bool ConnectionHandler::getLine(std::string &line) {
	return getFrameAscii(line, '\n');
}

bool ConnectionHandler::sendLine(std::string &line) {
	return sendFrameAscii(line, '\n');
}


bool ConnectionHandler::getFrameAscii(std::string &frame, char delimiter) {
	char ch;
	// Stop when we encounter the null character.
	// Notice that the null character is not appended to the frame string.
	try {
		do {
			if (!getBytes(&ch, 1)) {
				return false;
			}
			if (ch != '\0')
				frame.append(1, ch);
		} while (delimiter != ch);
	} catch (std::exception &e) {
		std::cerr << "recv failed2 (Error: " << e.what() << ')' << std::endl;
		return false;
	}
	return true;
}

bool ConnectionHandler::sendFrameAscii(const std::string &frame, char delimiter) {
	bool result = sendBytes(frame.c_str(), frame.length());
	if (!result) return false;
	return sendBytes(&delimiter, 1);
}

void ConnectionHandler::stompReceivedProcess(std::string income,StateControler *sc)
{
	std::lock_guard<std::mutex> lock(connectionHandlerMutex);
	std::string response = ResponseManager::parseServerMessage(income,sc,&connected_user);
	sc->less_waiting_message();
	if(response.compare("error") == 0)
	{
		socket_.close();
	}
	if(sc->get_logout() && response.compare(connected_user->get_disconnect_recipt_id()) == 0)
	{
		socket_.close();
		sc->set_logout(true);
		sc->set_waiting_for_logout(false);
	}
}


void ConnectionHandler::stompProcessClientInput(std::string income,StateControler *sc)
{
	std::lock_guard<std::mutex> lock(connectionHandlerMutex);
	
	if(income.find("login") != std::string::npos && sc->get_logout()){
		this->host_ = ResponseManager::get_host();
		this->port_ = ResponseManager::get_port();
		this->connect();
		sc->more_waiting_message();
		sc->set_logout(false);
	}
	else if(income.find("login") != std::string::npos)
	{
		//check if user is connected
		//if so print error and return
		//create new_user
		std::cout<<"The client is already logged in,log out before trying again" << std::endl;
		return;
	}
	
	if(!sc->get_logout()){
		std::vector<std::string> stomp_responses = ResponseManager::parseInput(income,sc,&connected_user);
		for(std::string stomp_response : stomp_responses){
			if(stomp_response.compare("ok") == 0)
				return;
			if(stomp_response.compare("Invalid command") != 0){
				try{
					sendFrameAscii(stomp_response,'\0');
				}
				catch(std::exception e)
				{
					std::cout << "server not present, can't send message. Try again later." << std::endl;
				}
			}
			else
				std::cout<<"bad command!";
			}
	}
	else
	{
		std::cout << "Please log in before any other commands!" << std::endl;
	}	
}

// Close down the connection properly.
void ConnectionHandler::close() {
	try {
		socket_.close();
	} catch (...) {
		std::cout << "closing failed: connection already closed" << std::endl;
	}
}
