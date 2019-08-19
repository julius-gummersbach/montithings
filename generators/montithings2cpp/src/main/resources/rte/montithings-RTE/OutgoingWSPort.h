#pragma once
#include "Port.h"
#include "nngpp/nngpp.h"
#include "nngpp/protocol/pub0.h"
#include <iostream>
#include "cereal/archives/json.hpp"
#include "cereal/types/vector.hpp"
#include "cereal/types/string.hpp"
#include "cereal/types/base_class.hpp"
#include "cereal/types/map.hpp"
#include "cereal/types/set.hpp"
#include "cereal/types/list.hpp"
#include <future>
#include "tl/optional.hpp"
using namespace std;

/*
 * The class IPCPort extends the standard MontiArc Port with ipc capabilities in order to request data from
 * other processes running on the same machine.
 */
template <class T>
class OutgoingWSPort {

public:
    explicit OutgoingWSPort(const char* uri) {
        this->uri = uri;
        //Open Socket in Request mode
        socket = nng::pub::open();
        //Dial specifies, that it connects to an already established socket (the server)

        try
        {
            socket.dial(uri , nng::flag::alloc);

        }
        catch (const std::exception&)
        {
            cout << "Connection to" << uri << " could not be established!\n";
            return;
        }
        printf("Connection established\n");
    };
    explicit OutgoingWSPort(T initialValue) {}

    void setPort(Port<T>* port){
        portSet = true;
        this->port = port;
        fut = std::async(std::launch::async, &OutgoingIPCPort::run, this);
    }

private:
    nng::socket socket;
    const char* uri;
    bool portSet = false;
    Port<T>* port;
    std::future<bool> fut;


    bool run() {
        while (true) {
            tl::optional<T> dataOpt = port->getCurrentValue();
            if (dataOpt) {
                try {
                    socket.dial(uri, nng::flag::alloc);

                }
                catch (const std::exception &) {
                    cout << "Connection to" << uri << " could not be established!\n";
                    continue;
                }
                T data = dataOpt.value();
                std::ostringstream stream;
                {
                    cereal::JSONOutputArchive outputArchive(stream);
                    outputArchive(data);
                }
                auto dataString = stream.str();
                nng::msg msg(strlen(dataString.c_str()) + 1);
                msg.body().insert(nng::view(dataString.c_str(), strlen(dataString.c_str()) + 1));
                auto body = msg.body().data<std::string>();
                std::cout << dataString << "\n";
                socket.send(std::move(msg));
            }
        }
        return true;
    }



};