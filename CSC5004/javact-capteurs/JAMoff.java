public class JAMoff implements org.javact.lang.Message
{
	private int signatureNumber ;


	public JAMoff()
	{
		signatureNumber = 0 ;
	}

	public final void handle(org.javact.lang.QuasiBehavior _behavior)
	{
		switch (signatureNumber)
		{
			case 0 :
				if (_behavior instanceof Lumiere)
					((Lumiere) _behavior).off() ;
				else 
					throw new org.javact.lang.MessageHandleException() ;
				break ;
			default :
				throw new org.javact.lang.MessageHandleException() ;
		}
	}
}
