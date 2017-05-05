package main

 import (
	"errors"
	"fmt"
	"log"
	"net"
	"os"
 )

 func handleUDPConnection(conn *net.UDPConn) {

         // here is where you want to do stuff like read or write to client

         buffer := make([]byte, 8192)

         n, addr, err := conn.ReadFromUDP(buffer)
         msg := string(buffer[:n])

         fmt.Println("UDP client : ", addr)
         fmt.Println("Received from UDP client :  ", msg)

         if err != nil {
                log.Fatal(err)
         }

         // NOTE : Need to specify client address in WriteToUDP() function
         //        otherwise, you will get this error message
         //        write udp : write: destination address required if you use Write() function instead of WriteToUDP()

         // write message back to client
         outPacket := []byte(fmt.Sprintf("Server received: %s", msg))
         _, err = conn.WriteToUDP(outPacket, addr)
         if err != nil {
                log.Println(err)
         }

 }

 // Return the deployed service application's PUBLIC_IP
func PublicIP() (string, error) {
	host := os.Getenv("PUBLIC_IP")
	if host == "" {
		return "", errors.New("Missing/empty public ip")
	}
	return host, nil
}

 func main() {
         hostName, err := PublicIP()
         if (err != nil) {
         	log.Fatalf("No ip to host server on: %v", err)
         }

         portNum := "6000"
         service := hostName + ":" + portNum

         udpAddr, err := net.ResolveUDPAddr("udp4", service)
         if err != nil {
                log.Fatal(err)
         }

         // setup listener for incoming UDP connection
         ln, err := net.ListenUDP("udp", udpAddr)
         if err != nil {
                log.Fatal(err)
         }
         defer ln.Close()
         fmt.Println("UDP server up and listening on " + udpAddr.String())

         // NOTE: server doesn't stop on EOF at the moment
         for {
                // wait for UDP client to connect
                handleUDPConnection(ln)
         }

        /*go func(serviceAddr string){
        }(service)*/
        //select{}
 }