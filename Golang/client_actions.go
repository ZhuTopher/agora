package main

import (
	"log"
)

// Client action (eg: server-join, community-leave, etc.) wrapper
type ClientAction struct {
	ClientID 	uint32
	Action     	interface{}
}

type JoinServer struct {
	ServerID	string
	ClientPtr	*Client
}

// requires caPtr.Action points to a JoinServer
func (sw *ServerWrapper) CAJoinServer(caPtr *ClientAction) {
	js := caPtr.Action.(JoinServer)
	sID := js.ServerID
	log.Printf("(sw) Moving Client %v (%v) to Server %v\n",
		(*js.ClientPtr).Name, (*js.ClientPtr).ID, sID)

	/*if !sw.Servers.has(sID) {
		sw.Servers[sID] = NewServer(sID)
	}

	cID := (*js.ClientPtr).ID
	err := sw.Servers[sID].removeClient(cID)
	if err != nil {
		log.Printf("WARNING")
	}

	sw.Servers[sID].caChan <- caPtr
	*/
}

// requires caPtr.Action points to a JoinServer
func (s *Server) CAJoinServer(caPtr *ClientAction) {
	js := caPtr.Action.(JoinServer)
	sID := js.ServerID
	log.Printf("(s) Moving Client %v (%v) to Server %v\n",
		js.ClientPtr.Name, js.ClientPtr.ID, sID)

	/*if sw.Servers[sID] == nil {
		sw.Servers[sID] = NewServer(sID)
	}

	sw.Servers[sID].caChan <- caPtr
	*/
}