import org.javact.lang.QuasiBehavior;

public abstract class RechercheQuasiBehavior extends QuasiBehavior implements Recherche
{
	public void become(Supervisor b)
	{
		try
		{ becomeAny((QuasiBehavior) b); }
		catch (RuntimeException e)
		{ throw new org.javact.lang.BecomeException(e);}
	}

}
