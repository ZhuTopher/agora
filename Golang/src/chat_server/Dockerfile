# FROM: https://blog.golang.org/docker

# Start from a Debian image with the latest version of Go installed
# and a workspace (GOPATH) configured at /go.
#FROM golang

#RUN mkdir -p /go/src/github.com/agora/chat_server

# Copy the local package files to the container's workspace.
#ADD . /go/src/github.com/agora/chat_server

# Build the compiled chat_server command inside the container.
# (You may fetch or manage dependencies here,
# either manually or with a tool like "godep".)
#RUN go get -v -d
#RUN go install

# Run the outyet command by default when the container starts.
#ENTRYPOINT /go/bin/chat_server

# ***************************************************************************
# Shortcut which automatically copies the package source, fetches the application dependencies, builds the program, and configures it to run on startup
FROM golang:onbuild

# Environment Variables
ENV HOST_IP="" 
ENV TCP_PORT="3333"
ENV API_PORT="5555"

# Document that the service listens on port 3333 (TCP Accept).
EXPOSE 3333

# Document that the service listens on port 5555 (HTTP API).
EXPOSE 5555
