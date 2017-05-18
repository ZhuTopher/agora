package main

import (
    "errors"
    "log"
    "net"
    "sync"
    "time"
)

// ServerWrapper houses the acceptLoop which takes TCP connections
//    and moves those clients into the respective Server
type ServerWrapper struct {
    // public fields
    Servers       map[string]*Server

    // private fields
    tcpl        *net.TCPListener
    connChan    chan *net.Conn

    // TODO: make global method to send CA into sw.caChan (?)
    caChan		chan *ClientAction

    done        chan bool
    running     bool
    loopWG      sync.WaitGroup
}

// newServerWrapper() defined in main.go (private to main)

func (sw *ServerWrapper) Shutdown() (err error) {
    if !sw.running { // TODO: atomic boolean/mutex
        return errors.New("ServerWrapper already stopped.")
    }
    sw.running = false
    log.Println("Shutting down Servers (ServerWrapper.Shutdown()")

    // stop accepting TCP connections
    /*defer*/ sw.tcpl.Close()

    // stop server loops from processing
    close(sw.done) // sends on channel to all receivers
    sw.loopWG.Wait()

    // close all client connections in s.Comms
    var wg sync.WaitGroup
    for _, s := range sw.Servers {
        wg.Add(1)
        go func(wg *sync.WaitGroup, s *Server) {
            defer (*wg).Done()
            s.Shutdown()
        }(&wg, s)
    }
    log.Println("Waiting on all Servers to shut down...")
    wg.Wait()
    log.Println("All Servers successfully shut down")

    return
}

// Accepts tcp connections and sends them as Clients to the
//    sw.mainLoop() to handle Server placement
func (sw *ServerWrapper) acceptLoop() {
    // sw.tcpl should've been resolved and initialized already
    defer func() {
        log.Println("ServerWrapper exiting acceptLoop()")
        sw.loopWG.Done()
    }()

    for {
        conn, err := sw.tcpl.Accept()
        if err != nil {
            log.Printf("Unable to accept TCP connection: %v\n", err)
        }

        select {
        case <-sw.done: 
            conn.Close() // ignoring errors
            break // exit for-loop
        case sw.connChan <- &conn: // send conn to client builder
        case <-time.After(time.Millisecond*10000):
            log.Println(`ServerWrapper timed out trying to send
                conn to clientBuilderLoop; closing conn`)
            conn.Close() // ignoring errors
        }
    }

    // deferred sw.loopWG.Done() called
}

// Performs initial Client building process from a net.Conn
func (sw *ServerWrapper) clientBuilderLoop() {
    defer func() {
        log.Println("ServerWrapper exiting clientBuilderLoop()")
        sw.loopWG.Done()
    }()

    for connPtr := range sw.connChan {
        c, err := NewClient(connPtr)
        if err != nil {
            log.Printf(
                "Unable to create Client object for conn: %v\n",
                err)
            continue // wait for next conn
        }

        select {
        case <-sw.done: 
            break // exit for-loop
        case sw.caChan <- &ClientAction{
                ClientID:   (*c).ID,
                Action:     JoinServer{c.ServerID, c},
            }:
        }
    }

    // deferred sw.loopWG.Done() called
}

// Controls processing of major events in response to tcp conn,
//    API requests, internal Server-Server communication, etc.
func (sw *ServerWrapper) controlLoop() {
    defer func() {
        log.Println("ServerWrapper exiting controlLoop()")
        sw.loopWG.Done()
    }()

    for {
    	select {
    	case <-sw.done:
            break // exit for-loop
    	case caPtr := <-sw.caChan:
    		sw.handleCA(caPtr)
    	}
    }

    // deferred sw.loopWG.Done() called
}

// method implementations in client_actions.go
func (sw *ServerWrapper) handleCA(caPtr *ClientAction) {
	switch caPtr.Action.(type) {
	case JoinServer:
		sw.CAJoinServer(caPtr)
	default: // should never happen
		log.Fatalf("(sw) Encountered invalid ClientAction: %v\n", (*caPtr))
	}
}
