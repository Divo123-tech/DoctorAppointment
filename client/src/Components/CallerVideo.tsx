import { useEffect, useReducer, useRef, useState } from "react";
import "./VideoPage.css";
import { useNavigate } from "react-router-dom";
import socketConnection from "../webrtcUtilities/socketConnection";
import ActionButtons from "./ActionButtons/ActionButtons";
import { useCallStore } from "../store/webrtcStore";
type Props = {
  remoteStream: MediaStream | null;
};
const CallerVideo = ({ remoteStream }: Props) => {
  const remoteFeedEl = useRef<HTMLVideoElement>(null); //this is a React ref to a dom element, so we can interact with it the React way
  const localFeedEl = useRef<HTMLVideoElement>(null); //this is a React ref to a dom element, so we can interact with it the React way
  const navigate = useNavigate();
  const {
    username,
    callStatus,
    setCallStatus,
    localStream,
    peerConnection,
    userOfferTo,
  } = useCallStore();
  const [offerCreated, setOfferCreated] = useState(false);
  const [, forceUpdate] = useReducer((x) => x + 1, 0);
  //send back to home if no localStream
  useEffect(() => {
    if (!localStream) {
      navigate(`/`);
    } else {
      if (localFeedEl.current) {
        localFeedEl.current.srcObject = localStream;
      }

      console.log("local stream", localStream);
      console.log("remote stream", remoteStream);

      if (
        callStatus.videoEnabled != null &&
        remoteFeedEl.current &&
        remoteStream
      ) {
        remoteFeedEl.current.srcObject = remoteStream;
      }
    }
  }, [callStatus, localStream, navigate, remoteStream]);

  //set video tags
  // useEffect(()=>{
  //     remoteFeedEl.current.srcObject = remoteStream
  //     localFeedEl.current.srcObject = localStream
  // },[])

  //once the user has shared video, start WebRTC'ing :)
  useEffect(() => {
    const shareVideoAsync = async () => {
      const offer = await peerConnection?.createOffer();
      peerConnection?.setLocalDescription(offer);
      //we can now start collecing ice candidates!
      // we need to emit the offer to the server
      const socket = socketConnection(username);
      socket?.emit("newOffer", { offer, offerTo: userOfferTo });
      setOfferCreated(true); //so that our useEffect doesn't make an offer again
      console.log(
        "created offer, setLocalDesc, emitted offer, updated videoMessage"
      );
      console.log("peerconnect: ", peerConnection?.remoteDescription);
      console.log("callstatus.answer: ", callStatus);
    };
    if (!offerCreated && callStatus.videoEnabled) {
      //CREATE AN OFFER!!
      console.log("We have video and no offer... making offer");
      shareVideoAsync();
    }
  }, [
    callStatus,
    callStatus.videoEnabled,
    offerCreated,
    peerConnection,
    userOfferTo,
    username,
  ]);

  useEffect(() => {
    const addAnswerAsync = async () => {
      if (callStatus.answer) {
        await peerConnection?.setRemoteDescription(callStatus.answer);
        console.log("Answer added!");
        setTimeout(forceUpdate, 100);
      }
    };
    addAnswerAsync();
  }, [callStatus, peerConnection]);

  if (!offerCreated && callStatus.videoEnabled == null) {
    return (
      <div
        style={{
          display: "flex",
          flexDirection: "column",
          justifyContent: "center",
          gap: "4rem",
          alignItems: "center",
          height: "100vh",
        }}
      >
        <video
          id="local-feed"
          ref={localFeedEl}
          autoPlay
          controls
          playsInline
          style={{
            width: "720px",
            height: "400px",
            objectFit: "cover",
          }}
        ></video>
        <button
          style={{
            padding: "10px 20px",
            backgroundColor: "#007bff",
            color: "#fff",
            border: "none",
            borderRadius: "4px",
            cursor: "pointer",
          }}
          onClick={() => {
            const copyCallStatus = { ...callStatus };
            copyCallStatus.videoEnabled = true;
            setCallStatus(copyCallStatus);
            localStream?.getTracks().forEach((track) => {
              peerConnection?.addTrack(track, localStream);
            });
          }}
        >
          Join Call
        </button>
      </div>
    );
  }

  return (
    <div>
      <div className="videos">
        <video
          id="local-feed"
          ref={localFeedEl}
          autoPlay
          controls
          playsInline
          style={
            remoteStream?.active === false
              ? {
                  height: "90vh",
                  width: "100vw",
                  backgroundColor: "#222",
                }
              : {
                  position: "absolute",
                  zIndex: "2",
                  right: "5%",
                  bottom: "10%",
                  width: "10%",
                  display: "block",
                }
          }
        ></video>
        <video
          id="remote-feed"
          ref={remoteFeedEl}
          autoPlay
          controls
          style={{
            ...(remoteStream?.active === false ? { visibility: "hidden" } : {}),
            height: "90vh",
            width: "100vw",
            backgroundColor: "#222",
          }}
          playsInline
        ></video>
      </div>
      <ActionButtons />
    </div>
  );
};

export default CallerVideo;
