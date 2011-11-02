package smtp;

public enum SmtpState {
	Unstarted,
	HelloSended ,
	HelloReceived ,
	MailFromSended , 
	MailFromReceived ,
	RcptSended ,
	RcptReceived ,
	DataSended ,
	DataReceived ,
	DataEnded ,
	DataFinished ,
	QuitSended ,
	QuitFinished ;
}