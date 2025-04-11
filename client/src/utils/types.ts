export type SDP = { sdp: string; type: string };
export type CallStatus = {
  audioEnabled: boolean;
  haveMedia: boolean;
  videoEnabled: boolean | null;
};
export type IceCandidate = {
  candidate: string;
  sdpMid: string | null;
  sdpMLineIndex: number | null;
};

export type Offer = {
  offererUserName: string;
  offer: SDP;
  offerIceCandidates: IceCandidate[];
  answererUserName: string | null;
  answer: SDP | null;
  answererIceCandidates: IceCandidate[];
  offeringTo: string;
};
