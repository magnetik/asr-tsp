package csc4509;

public enum ReadMessageStatus {
	ReadUnstarted,
	ReadHeaderStarted ,
	ReadHeaderCompleted ,
	ReadDataStarted , 
	ReadDataCompleted ,
	ChannelClosed;
}
