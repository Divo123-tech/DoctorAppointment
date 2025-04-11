import { useNavigate } from "react-router";
import { Socket } from "socket.io-client";

const clientSocketListeners = (
  socket: Socket,
  typeOfCall: string,
  localStream,
  callStatus,
  setCallStatus,
  peerConnection,
  setRemoteStream
) => {
  socket.on("answerResponse", (entireOfferObj) => {
    console.log(entireOfferObj);
    const copyCallStatus = { ...callStatus };
    copyCallStatus.answer = entireOfferObj.answer;
    copyCallStatus.myRole = typeOfCall;
    setCallStatus(copyCallStatus);
  });

  socket.on("receivedIceCandidateFromServer", (iceC) => {
    if (iceC) {
      peerConnection.addIceCandidate(iceC).catch((err) => {
        console.log("Chrome thinks there is an error. There isn't...");
      });
      console.log(iceC);
      console.log("Added an iceCandidate to existing page presence");
      // setShowCallInfo(false);
    }
  });

  socket.on("hangup", () => {
    setCallStatus((prevCallStatus) => {
      prevCallStatus.current = "complete";
      return prevCallStatus;
    });
    if (localStream) {
      localStream.getTracks().forEach((track) => {
        if (track.kind === "video") {
          track.stop();
        }
      });
    }
    peerConnection.close();
    peerConnection.onicecandidate = null;
    peerConnection.ontrack = null;
    peerConnection = null;
    setRemoteStream((prevRemoteStream) => {
      if (!prevRemoteStream) return null; // Ensure it's not null

      // Stop all tracks in the MediaStream
      prevRemoteStream.getTracks().forEach((track) => track.stop());

      return new MediaStream(); // ✅ Replace with a new empty stream
    });
    window.location.href = "/";
  });
};

export default clientSocketListeners;
