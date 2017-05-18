package main

import (
	"errors"
	"fmt"
	"log"
    "sync"
)

// Server takes TCP clients from main and moves them into the
//    respective Community (chat room) according to the chat room id 
type Server struct {
    // public fields
    ID          string     // corresponds to area (i.e: Waterloo)
    Comms       map[string]*Community

    // private fields
    swCAChan    chan *ClientAction
    caChan      chan *ClientAction
    running     bool
    done        chan bool
    loopWG      sync.WaitGroup
}

const (
    ROOT_COMM_ID = "root"
)

func NewServer(id string) (s *Server, err error) {
    s = new(Server)

    s.ID = id;
    s.caChan = make(chan *ClientAction)

    s.Comms = make(map[string]*Community)
    s.Comms[ROOT_COMM_ID] = NewComm(ROOT_COMM_ID)

    s.done = make(chan bool)

    if err = s.Start(); err != nil {
        return nil, err
    }
    return
}

func (s *Server) Start() (err error) {
    if s.running {
        return errors.New(fmt.Sprintf(
            "Server %s already running.", s.ID))
    }

    s.loopWG.Add(1)
    go s.controlLoop()

    s.running = true
    return
}

func (s *Server) Shutdown() (err error) {
    if !s.running { // TODO: atmomic boolean
        return errors.New(fmt.Sprintf(
            "Server %s already stopped.", s.ID))
    }
    s.running = false

    // stop server loops from processing
    close(s.done) // sends on channel to all receivers
    s.loopWG.Wait()

    // close all client connections in s.Comms
    var wg sync.WaitGroup
    for _, comm := range s.Comms {
        wg.Add(1)
        go func(wg sync.WaitGroup, comm *Community) {
            defer wg.Done()
            comm.Shutdown()
        }(wg, comm)
    }
    wg.Wait()

    return
}

func (s *Server) controlLoop() {
    defer s.loopWG.Done()

    for {
        select {
        case <-s.done:
            log.Printf("Server %s exiting control loop.\n", s.ID)
            break;
        }
    }

    // deferred s.loopWG.Done() called
}
