#include <stdlib.h>
#include "../include/connectionHandler.h"

#include <string>
#include <iostream>
#include <boost/asio.hpp>
#include <boost/thread.hpp>


/**
* This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
*/
int main (int argc, char *argv[]) {
    if (argc < 3) {
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);

    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        return 1;
    }

    boost::thread th1(boost::bind(&ConnectionHandler::getAnswer, &connectionHandler));

    while (!connectionHandler.shouldTerminateFunction()) {

        int len;
        std::string answer;
        if (!connectionHandler.getLine(answer)) {
            break;
        }

        len=answer.length();
        answer.resize(len-1);
        std::cout<< answer << std::endl;
        if (answer == "ACK signout succeeded")
        {
            connectionHandler.shouldTerminateSetTrue();
            break;

        }
    }
    //thd1.join();
    return 0;
}
