FROM ubuntu

# install prerequisites
RUN apt-get update
RUN apt-get -y install git
RUN apt -y install sudo
RUN sudo apt-get -y install curl

# install montithings
RUN git clone https://github.com/julius-gummersbach/montithings.git # todo use official url
WORKDIR "/montithings"
RUN ./installLinux.sh
