import { io, Socket } from "socket.io-client";

let socket: Socket | null = null;
const socketConnection = (userName: string) => {
  //check to see if the socket is already connected
  if (socket && socket.connected) {
    //if so, then just return it so whoever needs it, can use it
    return socket;
  } else {
    //its not connected... connect!
    socket = io(import.meta.env.VITE_SS_URL, {
      // socket = io.connect('https://192.168.1.44:8181',{
      auth: {
        // jwt,
        password: "x",
        userName,
      },
    });

    if (userName == "test") {
      console.log("Testing...");
      socket?.emitWithAck("test").then((resp) => {
        console.log(resp);
      });
    }

    return socket;
  }
};

export default socketConnection;
