package main

import (

)

type Community struct {
	ID 			string
}

func NewComm(id string) (comm *Community) {
	comm = new(Community)
	comm.ID = id

	return
}

// Will disconnect all Clients in this Community
func (comm *Community) Shutdown() {
	// TODO: atomic boolean
}
